package processor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import model.ThreadConexao;
import model.dao.DAOCercaEletronica;
import model.dao.DAOEmail;
import model.dao.DAOSms;

import org.apache.commons.lang3.StringUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import entidade.CercaEletronica;
import entidade.Email;
import entidade.PacoteRecebido;
import entidade.Posicionamento;
import entidade.Sms;
import entidade.Veiculo;
import exception.DaoException;

public class CercaProcessor extends Processo{

	private Calendar calendar;
	private int weekday;
	private SimpleDateFormat df;
	private int horario;
	
	public void processar(PacoteRecebido pacote, ThreadConexao conexao) {
		logger.debug("inicio");
		long timeInicio = System.currentTimeMillis();
		
		try{
			Posicionamento pos = (Posicionamento) pacote;
			Veiculo v = conexao.getVeiculo(pos.getImei());
			List<CercaEletronica> cercas = v.getCercas();
			
			//inicializa variaveis
			calendar = Calendar.getInstance();
			calendar.setTime(pos.getDataCapturado());
			weekday = calendar.get(Calendar.DAY_OF_WEEK);
			df = new SimpleDateFormat("HHmm");
			horario = Integer.parseInt(df.format(pos.getDataCapturado()));
			
			//valida todas as cercas
			for (Iterator iterator = cercas.iterator(); iterator.hasNext();) {
				CercaEletronica cercaEletronica = (CercaEletronica) iterator.next();
				
				//verifica o intervalo de horas para validacao
				if(validarHorario(cercaEletronica)){
				
					Coordinate[] coordinates = new Coordinate[cercaEletronica.getCoordinates().size()+1];
					for (int i = 0; i < cercaEletronica.getCoordinates().size(); i++) {
						coordinates[i] = cercaEletronica.getCoordinates().get(i);
					}
					//fecha o circulo com o primeiro ponto
					coordinates[cercaEletronica.getCoordinates().size()] = cercaEletronica.getCoordinates().get(0);
					LinearRing ring = new GeometryFactory().createLinearRing(coordinates);
					Geometry cercaLocalizacao = new GeometryFactory().createPolygon(ring, null);
					Geometry posicaoVeiculo = new GeometryFactory().createPoint(new Coordinate(v.getUltimoPosicionamento().getLat(), v.getUltimoPosicionamento().getLon()));
					
					//se o veiculo estiver dentro da cerca...
					if(cercaLocalizacao.intersects(posicaoVeiculo)){
						//enviar alerta apenas ao entrar na cerca
						if(cercaEletronica.getAlerta()==1 || cercaEletronica.getAlerta()==3){
							//se o veiculo estiver fora da cerca, enviar notificao de entrou
							if(cercaEletronica.getStatusDentroFora()==2){
								//atualiza status para: 'entrou na cerca'
								cercaEletronica.setStatusDentroFora(1);
								processarEnvioEmailSms(cercaEletronica,1,v);
							}
						}
					}else{
						//enviar alerta ao sair da cerca
						if(cercaEletronica.getAlerta()==2 || cercaEletronica.getAlerta()==3){
							//se o veiculo estiver dentro da cerca, enviar notificao de saiu
							if(cercaEletronica.getStatusDentroFora()==1){
								//atualiza status para: 'saiu da cerca'
								cercaEletronica.setStatusDentroFora(2);
								processarEnvioEmailSms(cercaEletronica,2,v);
							}
						}
					}
				
				}
				
			}
			
		}catch (DaoException e) {
			logger.debug("Problemas com banco de dados, imei:"+pacote.getImei(),e);
		}catch (Exception e) {
			logger.debug("Erro inesperado, imei:"+pacote.getImei(),e);
		}finally{
			logger.debug("fim ("+(System.currentTimeMillis()-timeInicio)+" millis)");
		}
	}
	
	private boolean validarHorario(CercaEletronica cerca) throws Exception{
		//monitorar 24h
		if(cerca.getMonitorar()==24){
			return true;
		}
		
		switch (weekday) {
		// segunda
		case Calendar.MONDAY:
			if (horario >= cerca.getSegunda_de()
					&& horario <= cerca.getSegunda_de()) {
				return true;
			}
			break;
		//terca....
		case Calendar.TUESDAY:
			if (horario >= cerca.getTerca_de()
					&& horario <= cerca.getTerca_ate()) {
				return true;
			}
			break;
		case Calendar.WEDNESDAY:
			if (horario >= cerca.getQuarta_de()
					&& horario <= cerca.getQuarta_ate()) {
				return true;
			}
			break;
		case Calendar.THURSDAY:
			if (horario >= cerca.getQuinta_de()
					&& horario <= cerca.getQuinta_ate()) {
				return true;
			}
			break;
		case Calendar.FRIDAY:
			if (horario >= cerca.getSexta_de()
					&& horario <= cerca.getSexta_ate()) {
				return true;
			}
			break;
		case Calendar.SATURDAY:
			if (horario >= cerca.getSabado_de()
					&& horario <= cerca.getSabado_ate()) {
				return true;
			}
			break;
		case Calendar.SUNDAY:
			if (horario >= cerca.getDomingo_de()
					&& horario <= cerca.getDomingo_ate()) {
				return true;
			}
			break;
		default:
			break;
		}
		
		return false;
	}

	private void processarEnvioEmailSms(final CercaEletronica cerca,final int entrouSaiu, final Veiculo v) throws DaoException{
		//busca os emails e numeros de celular para notificar
		DAOCercaEletronica daoCerca = new DAOCercaEletronica();
		daoCerca.entrouSaiu(cerca.getId(), entrouSaiu);
		
		final List<String> emails = daoCerca.getCercaEmails(cerca.getId());
		final List<String> numeros = daoCerca.getCercaNumerosSms(cerca.getId());
		
		//Thread que grava no banco de dados as notificacos
		Thread t = new Thread(){
			public void run() {
				try{
					
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					
					//mensagem padrao
					String mensagem = "";
					if(entrouSaiu==1){
						mensagem = v.getModelo()+" - "+v.getPlaca()+", entrou na cerca: " + cerca.getNome() + "(" + df.format(v.getUltimoPosicionamento().getDataCapturado()) + ")";
					}else{
						mensagem = v.getModelo()+" - "+v.getPlaca()+", saiu da cerca: " + cerca.getNome() + "(" + df.format(v.getUltimoPosicionamento().getDataCapturado()) + ")";
					}
					
					//cadastra os emails de notificacao
					DAOEmail daoEmail = new DAOEmail();
					for (Iterator iterator = emails.iterator(); iterator.hasNext();) {
						String dest = (String) iterator.next();
						Email email = new Email();
						email.setMensagem(mensagem);
						email.setDestinatario(dest);
						email.setTitulo("Extravio de cerca");
						try {
							daoEmail.inserir(email);
						} catch (DaoException e) {
							logger.debug("Erro ao inserir email.",e);
						}
					}
					
					//cadastra os sms de notificacao
					DAOSms daoSms = new DAOSms();
					for (Iterator iterator = numeros.iterator(); iterator.hasNext();) {
						String num = (String) iterator.next();
						Sms sms = new Sms();
						sms.setNumero(StringUtils.remove(StringUtils.remove(StringUtils.remove(num, '-'), ')'), '('));
						sms.setTipo(5);
						sms.setMsg(mensagem);
						try {
							daoSms.inserir(sms);
						} catch (DaoException e) {
							logger.debug("Erro ao inserir sms.",e);
						}
					}
				}catch (Exception e) {
					logger.debug("Erro processando notificacoes.",e);
				}
			}
		};
		
		t.start();
		
	}
	
}

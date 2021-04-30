package model.parser;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.ThreadConexao;
import model.dao.DAOBloqueio;
import entidade.Bloqueio;
import entidade.PacoteRecebido;
import entidade.Posicionamento;
import entidade.Veiculo;
import exception.DaoException;
import exception.TraduzirMensagemException;

public class ParserVT310 extends Parser{

	private final Charset ASCII_CHARSET = Charset.forName("ASCII");
	
	//desligou o veiculo
	private static final int IGNITION_OFF_5 = 999935;
	private static final int IGNITION_OFF_4 = 999934;
	
	//response bloqueio
	private static final int BLOCK_OK = 411401;
	private static final int BLOCK_FAIL = 411400;
	
	//deu partida
	private static final int IGNITION_ON_5 = 999905;
	private static final int IGNITION_ON_4 = 999904;
	
	//apertou botao de panico
	private static final int BOTAO_PANICO = 999901;
	
	//posicionamento
	private static final int POS = 9955;
	//posicionamento com status dos sensores
	private static final int POS_ALARMS = 9999;
	//codigo para autenticacao
	private static final int AUTH = 5000;
	
	
	public PacoteRecebido traduzirMensagem(byte[] buf,ThreadConexao th) throws TraduzirMensagemException {
		Posicionamento p = new Posicionamento();
	
		
		try{
			String s = new String(buf,ASCII_CHARSET);
			s = s .replace("##", "");
			if (s.startsWith("$$")) {
				//delimitador da mensagem
				String fs[] = s.split("&");
				
				//obtem campos comum independente do tipo do pacote
				
				p.setImei(fs[0].substring(2));
				setImei(p.getImei());
				p.setTipo(fs[1].substring(1));
				p.setSize(buf.length);
				
				int tipoPacote = Integer.parseInt(p.getTipo());
				
				//tratar apenas pacotes que possuem dados de gps
				if(tipoPacote >= POS && tipoPacote<=POS_ALARMS){
					
					logger.debug("tipo: ["+Integer.parseInt(p.getTipo())+"]");
					if(!p.getTipo().equals(POS)){
						if(tipoPacote == IGNITION_OFF_4 || tipoPacote == IGNITION_OFF_5){
							p.setTipoPosicionamento(1);
							logger.debug("desligou o veiculo");
						}else if(tipoPacote == IGNITION_ON_4 || tipoPacote == IGNITION_ON_5){
							p.setTipoPosicionamento(2);
							logger.debug("ligou o veiculo");
						}else if(tipoPacote == BOTAO_PANICO){
							p.setTipoPosicionamento(3);
							logger.debug("apertou botao panico");
						}
					}
					p.setDataRecebido(new Date());
					//delimitador para os dados do gps
					String[] gpsData = fs[2].split(",");
					
					//indicador se o posicionamento eh valido
					p.setValido(gpsData[1].equals("A"));
					
					//a data vem em 2 campos separados, junta os 2
					SimpleDateFormat df = new SimpleDateFormat("ddMMyy'B'HHmmss.SSS");
					p.setDataCapturado(df.parse(gpsData[8]+gpsData[0]));
					
					
					if(!p.isValido()){
						return p;
					}
					
					// Latitude
				    String raw_lat = gpsData[2];
				    String lat_deg = raw_lat.substring(0, 2);
				    String lat_min1 = raw_lat.substring(2, 4);
				    String lat_min2 = raw_lat.substring(5);
				    String lat_min3 = "0." + lat_min1 + lat_min2;
				    float lat_dec = Float.parseFloat(lat_min3)/.6f;
				    float lat_val = Float.parseFloat(lat_deg) + lat_dec;
	
				    // Latitude direction
				    String lat_direction = gpsData[3];
				    if(lat_direction.equals("S")){
				    	lat_val = lat_val * -1;
				    }
					p.setLat(lat_val);
				    
				    // Longitude
				    String raw_lon = gpsData[4];
				    String lon_deg = raw_lon.substring(0, 3);
				    String lon_min1 = raw_lon.substring(3, 5);
				    String lon_min2 = raw_lon.substring(6);            
				    String lon_min3 = "0." + lon_min1 + lon_min2;
				    float lon_dec = Float.parseFloat(lon_min3)/.6f;
				    float lon_val = Float.parseFloat(lon_deg) + lon_dec;
				    
				    
				    // Longitude direction
				    String lon_direction = gpsData[5];
				    if(lon_direction.equals("W")){
				    	lon_val = lon_val * -1;
				    }
				    p.setLon(lon_val);
				    
				    double knots = Double.parseDouble(gpsData[6]);
				    if(knots>0){
				    	p.setVel(knots * 1.8532);
				    }else{
				    	p.setVel(0);
				    }
				    
				    if(!"".equals(gpsData[7])){
				    	p.setAng(Double.parseDouble(gpsData[7]));
				    }
				    return p;
				}else{
					logger.debug("ignorando mensagem");
				}
			}
		}catch (Exception e) {
			logger.debug("erro no parserVT310", e);
			throw new TraduzirMensagemException("Erro ao traduzir msg", e);
		}
		return p;
	}

	public void processamentoEspefico(ThreadConexao th, PacoteRecebido pacote)	throws Exception {
		int tipoPacote = Integer.parseInt(pacote.getTipo());
		if(tipoPacote == AUTH){
			logger.debug("tratando autenticacao");
			
			ByteArrayOutputStream saida = new ByteArrayOutputStream();
			saida.write(new byte[]{0x40,0x40});
			saida.write(getImei().getBytes());
			saida.write("01".getBytes());
			saida.write("&A400001####".getBytes());
			
			pacote.setSend(saida.toByteArray());
		}else if(tipoPacote == BLOCK_OK || tipoPacote == BLOCK_FAIL){
			logger.debug("bloqueio realizado = "+(tipoPacote == BLOCK_OK));
			
			//atualizar base com o status do bloqueio
			try {
				Bloqueio b = new Bloqueio();
				b.setEnviado(new Date());
				Veiculo v = th.getVeiculoForced(getImei());
				
				//2 bloquear
				if(v.getIdStatus()==2){
					if(tipoPacote == BLOCK_OK){
						b.setIdStatus(3);
					}else{ // caso falhe
						b.setIdStatus(1);
					}
				//4 desbloquear
				}else{
					if(tipoPacote == BLOCK_OK){
						b.setIdStatus(1);
					}else { // caso falhe
						b.setIdStatus(3);
					}
				}
				
				b.setIdVeiculo(v.getId());
				b.setTentativas(0);
				
				DAOBloqueio dao = new DAOBloqueio();
			
				dao.atualizarBloqueio(b);
			} catch (DaoException e) {
				logger.debug(e);
			}

			
			
			//responde o rastreador com uma mensagem de auth ok
			ByteArrayOutputStream saida = new ByteArrayOutputStream();
			saida.write(new byte[]{0x40,0x40});
			saida.write(getImei().getBytes());
			saida.write("01".getBytes());
			saida.write("&A400001####".getBytes());
			pacote.setSend(saida.toByteArray());
		}
	}

	public void enviarBloqueio(ThreadConexao th, Bloqueio b) throws Exception {
		logger.debug("inicio enviarBloqueio");
		//2 bloquear
		if(b.getIdStatus()==2){
			ByteArrayOutputStream saida = new ByteArrayOutputStream();
			saida.write(new byte[]{0x40,0x40});
			saida.write(getImei().getBytes());
			saida.write("&A4114".getBytes());
			saida.write("0000000000####".getBytes());
			logger.debug("enviando: "+new String(saida.toByteArray()));
			th.getOut().write(saida.toByteArray());
		//4 desbloquear
		}else{
			ByteArrayOutputStream saida = new ByteArrayOutputStream();
			saida.write(new byte[]{0x40,0x40});
			saida.write(getImei().getBytes());
			saida.write("&A4114".getBytes());
			saida.write("0101010101####".getBytes());
			logger.debug("enviando: "+new String(saida.toByteArray()));
			th.getOut().write(saida.toByteArray());
		}
		try{
			b.setTentativas(b.getTentativas()+1);
			b.setEnviado(new Date());
			DAOBloqueio dao = new DAOBloqueio();
			dao.atualizarBloqueio(b);
		} catch (DaoException e) {
			logger.debug(e);
		}
		
		logger.debug("fim enviarBloqueio");
	}
	
	

}


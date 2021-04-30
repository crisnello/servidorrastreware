package processor;

import java.util.Calendar;
import java.util.Date;

import model.ThreadConexao;
import model.dao.DAOPosicionamento;
import entidade.GeoCoordinate;
import entidade.GeoUtils;
import entidade.PacoteRecebido;
import entidade.Posicionamento;
import exception.DaoException;

public class PosicionamentoProcessor extends Processo{

	public void processar(PacoteRecebido pacote, ThreadConexao conexao) {
		logger.debug("inicio");
		long timeInicio = System.currentTimeMillis();
		try{
			Posicionamento pos = (Posicionamento) pacote;
			DAOPosicionamento dao = new DAOPosicionamento();
			pos.setVeiculo(conexao.getVeiculo(pos.getImei()));
			//o imei pode nao estar associado a nenhum veiculo
			if(pos.getVeiculo()==null){
				logger.debug(pos.getImei()+" ainda nao possui veiculo");
				return;
			}
			
			//atualiza horario do posicionamento se tiver definido alguma configuracao de timezone 
			//na base para o rastreador
			if(pos.getVeiculo().getTimeDiference()!=0){
				logger.debug("ajustando timezone "+pos.getVeiculo().getTimeDiference());
				Calendar cal = Calendar.getInstance();
				cal.setTime(pos.getDataCapturado());
				cal.add(Calendar.MINUTE, pos.getVeiculo().getTimeDiference());
				pos.setDataCapturado(cal.getTime());
			}
			
			//se o ultimo posicionamento eh null entao nao tem resgistros na tabela posicionamento_atual
			if(pos.getVeiculo().getUltimoPosicionamento()==null){
				logger.debug(conexao.getParser().getImei()+" ainda nao possui posionamento, gravando o primeiro");
				
				//grava um registo na tabela de posicionamento atual, so cai aki na primeira conexao de todas do veiculo
				int id = dao.inserirPosicionamentoAtual(pos);
				pos.setId(id);
				pos.getVeiculo().setUltimoPosicionamento(pos);
				pos.getVeiculo().getInfo().setIdPosicionamentoAtual(id);
				
				dao.inserirPosicionamentoHistorico(pos);
				
				//atualiza ultimo posicionameno do objeto veiculo
				pos.getVeiculo().setUltimoPosicionamento(pos);
			}else{
				//id para update da tabela de posicionamento atual
				pos.setId(pos.getVeiculo().getInfo().getIdPosicionamentoAtual());
				
				//calcula a distancia entre dois pontos
				GeoCoordinate first = new GeoCoordinate(pos.getLat(),pos.getLon());
				GeoCoordinate second = new GeoCoordinate(pos.getVeiculo().getUltimoPosicionamento().getLat(), pos.getVeiculo().getUltimoPosicionamento().getLon());
				double distancia = GeoUtils.geoDistanceInKm(first, second);

				Calendar dateIni = Calendar.getInstance();
				dateIni.set(Calendar.HOUR, 23);
				dateIni.set(Calendar.MINUTE, 58);
				dateIni.set(Calendar.SECOND, 0);
				dateIni.set(Calendar.DATE, dateIni.get(Calendar.DATE)-1);
				
				Calendar dateFim = Calendar.getInstance();
				dateFim.set(Calendar.HOUR, 0);
				dateFim.set(Calendar.MINUTE, 2);
				dateFim.set(Calendar.SECOND, 0);
				
				//valida posicionamento, se insere ou da updade para nao gerar lixo na tabela
				if(distancia>=0.02 || (pos.getDataCapturado().before(dateFim.getTime()) && pos.getDataCapturado().after(dateIni.getTime()))){
					//grava posicionamento
					logger.debug("distancia "+distancia+" valida, gravando posicionamento para imei: "+pos.getImei());
					pos.getVeiculo().setUltimoPosicionamento(pos);
					dao.inserirPosicionamentoHistorico(pos);
				}
				//atualiza variaveis de informacao
				dao.atualizarPosicionamentoAtual(pos);
			}
		}catch (DaoException e) {
			logger.debug("Problemas com banco de dados, imei:"+conexao.getParser().getImei(),e);
		}catch (Exception e) {
			logger.debug("Erro inesperado, imei:"+conexao.getParser().getImei(),e);
		}finally{
			logger.debug("fim ("+(System.currentTimeMillis()-timeInicio)+" millis)");
		}
		
	}

}

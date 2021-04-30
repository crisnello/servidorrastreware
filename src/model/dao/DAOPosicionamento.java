package model.dao;

import java.sql.ResultSet;
import java.sql.Statement;

import entidade.Posicionamento;
import entidade.Veiculo;
import exception.DaoException;

public class DAOPosicionamento extends Dao{
	
	@SuppressWarnings("finally")
	public Posicionamento getUltimoPosicionamento(Veiculo v) throws DaoException{
		
		Posicionamento p = null;
		try{
			conectar();
			
			String query = "select * from posicionamento_atual where id_veiculo="+v.getId();
			
			ResultSet rs = getCon().createStatement().executeQuery(query);
			
			while(rs.next()){
				p = new Posicionamento();
				
				p.setId(rs.getInt("id"));
				p.setLat(rs.getDouble("lat"));
				p.setLon(rs.getDouble("lon"));
				p.setAng(rs.getDouble("angulo"));
				p.setVel(rs.getDouble("velocidade"));
				p.setDataCapturado(rs.getTimestamp("data_capturado"));
				p.setDataRecebido(rs.getTimestamp("data_recebido"));
				
				break;
			}
			
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return p;
		}
	}
	
	
	@SuppressWarnings("finally")
	public int inserirPosicionamentoHistorico(Posicionamento pos) throws DaoException{
		int id = 0;
		try{
			conectar();
			
			
			String query = "insert into posicionamento_historico(data_capturado,data_recebido,lat,lon,angulo,velocidade" +
					",id_veiculo,id_rastreador,tamanho_pacote,tipo) values (" +
					"'"+formatoDataBase.format(pos.getDataCapturado())+"','"+formatoDataBase.format(pos.getDataRecebido())+"'," +
					pos.getLat()+","+pos.getLon()+","+pos.getAng()+","+pos.getVel()+","+
					pos.getVeiculo().getId()+","+pos.getVeiculo().getIdRastreador()+","+
					pos.getSize()+","+pos.getTipoPosicionamento()+")";
			
			Statement stmt = getCon().createStatement();
			stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			//pega o id inserido
			ResultSet rs = stmt.getGeneratedKeys();
	        if (rs.next()){
	            id=rs.getInt(1);
	        }
			
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return id;
		}
	}
	
	@SuppressWarnings("finally")
	public int inserirPosicionamentoAtual(Posicionamento pos) throws DaoException{
		int id = 0;
		try{
			conectar();
			
			String query = "insert into posicionamento_atual(data_capturado,data_recebido,lat,lon,angulo,velocidade" +
					",id_veiculo,id_rastreador) values (" +
					"'"+formatoDataBase.format(pos.getDataCapturado())+"','"+formatoDataBase.format(pos.getDataRecebido())+"'," +
					pos.getLat()+","+pos.getLon()+","+pos.getAng()+","+pos.getVel()+","+
					pos.getVeiculo().getId()+","+pos.getVeiculo().getIdRastreador()+")";
			
			Statement stmt = getCon().createStatement();
			stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			//pega o id inserido
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()){
	            id=rs.getInt(1);
	        }
			
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return id;
		}
	}
	
	public void atualizarPosicionamentoAtual(Posicionamento pos) throws DaoException{
		try{
			conectar();
			
			String query = "update posicionamento_atual set data_capturado='"+formatoDataBase.format(pos.getDataCapturado())+"'," +
					"data_recebido='"+formatoDataBase.format(pos.getDataRecebido())+"'," +
					"lat="+pos.getLat()+",lon="+pos.getLon()+",angulo="+pos.getAng()+"," +
					"velocidade="+pos.getVel()+",id_veiculo="+pos.getVeiculo().getId()+"," +
					"id_rastreador="+pos.getVeiculo().getIdRastreador()+
					" where id="+pos.getId() ;
			
			logger.debug(query);
			
			getCon().createStatement().executeUpdate(query);
			
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}

}

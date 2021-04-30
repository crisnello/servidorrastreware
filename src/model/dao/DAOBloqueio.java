package model.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entidade.Bloqueio;
import exception.DaoException;

public class DAOBloqueio extends Dao{
	
	/**
	 * verifica se os veiculos possuem algum bloqueio para envio
	 * @return
	 * @throws DaoException
	 */
	public List<Bloqueio> getBloqueioEnviar() throws DaoException{
		List<Bloqueio> bs = new ArrayList<Bloqueio>();
		try{
			conectar();
			
			String query = "select * from rastreador_bloquear where id_status=2 or id_status=4";
			
			ResultSet rs = getCon().createStatement().executeQuery(query);
			
			while(rs.next()){
				Bloqueio b = new Bloqueio();
				b.setId(rs.getInt("id"));
				b.setIdStatus(rs.getInt("id_status"));
				b.setIdVeiculo(rs.getInt("id_veiculo"));
				b.setTentativas(rs.getInt("tentativas"));
				b.setEnviado(rs.getTimestamp("enviado"));
				bs.add(b);
			}
			
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return bs;
		}
	}
	
	/**
	 * 
	 * @param b
	 * @throws DaoException
	 */
	public void atualizarBloqueio(Bloqueio b) throws DaoException{
		try{
			conectar();
			
			String query = "update rastreador_bloquear set id_status="+b.getIdStatus()+", enviado='"+formatoDataBase.format(b.getEnviado())+"', tentativas="+b.getTentativas()+" where id_veiculo="+b.getIdVeiculo();
			
			Statement stm = getCon().createStatement();
			stm.executeUpdate(query);
			stm.close();
			
			query = "update veiculo set id_status="+b.getIdStatus()+" where id="+b.getIdVeiculo();
			
			stm = getCon().createStatement();
			stm.executeUpdate(query);
			stm.close();
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}

}

package model.dao;

import java.sql.ResultSet;

import entidade.Veiculo;
import exception.DaoException;

public class DAOVeiculo extends Dao{
	
	/**
	 * busca o veiculo pelo codigo imei
	 * @param codRastreador
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("finally")
	public Veiculo getVeiculoByRastreador(String codRastreador) throws DaoException{
		Veiculo v = null;
		
		try{
			conectar();
			
			String query = "select r.time_diference,v.id,v.modelo,v.placa,v.id_status,r.id as id_rastreador from veiculo v " +
					"inner join rastreador r on r.id=v.id_rastreador and r.codigo='"+codRastreador+"'";
			
			ResultSet rs = getCon().createStatement().executeQuery(query);
			
			while(rs.next()){
				v = new Veiculo();
				v.setId(rs.getInt("id"));
				v.setModelo(rs.getString("modelo"));
				v.setPlaca(rs.getString("placa"));
				v.setIdStatus(rs.getInt("id_status"));
				v.setIdRastreador(rs.getInt("id_rastreador"));
				v.setTimeDiference(rs.getInt("time_diference"));
			}
			
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return v;
		}
		
	}
	

}

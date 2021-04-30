package model.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import entidade.CercaEletronica;
import entidade.Veiculo;
import exception.DaoException;

public class DAOCercaEletronica extends Dao{
	
	/**
	 * busca as cercas cadastradas do veiculo
	 * @param v
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("finally")
	public List<CercaEletronica> getCercas(Veiculo v) throws DaoException{
		List<CercaEletronica> cercas = new ArrayList<CercaEletronica>();
		try{
			conectar();
			String query = "select distinct id,nome,segunda_de,segunda_ate,terca_de,terca_ate,quarta_de," +
			 		"quarta_ate,quinta_de,quinta_ate,sexta_de,sexta_ate,sabado_de,sabado_ate,domingo_de," +
			 		"domingo_ate,monitorar,alerta,tipo " +
			 		"from cerca c " +
			 		"inner join cerca_veiculo cv on cv.id_veiculo = "+v.getId()+" and c.id = cv.id_cerca";
			
			ResultSet rs = getCon().createStatement().executeQuery(query);
			while(rs.next()){
				//constroi a cerca 
				CercaEletronica c = new CercaEletronica();
				c.setId(rs.getInt("id"));
				c.setNome(rs.getString("nome"));
				c.setMonitorar(rs.getInt("monitorar"));
				c.setAlerta(rs.getInt("alerta"));
				
				String seg_de = rs.getString("segunda_de");
				if(seg_de!=null)
					c.setSegunda_de(Integer.parseInt(seg_de.replaceAll(":", "")));
				
				String segunda_ate = rs.getString("segunda_ate");
				if(segunda_ate!=null)
					c.setSegunda_ate(Integer.parseInt(segunda_ate.replaceAll(":", "")));
				
				String terca_de = rs.getString("terca_de");
				if(terca_de!=null)
					c.setTerca_de(Integer.parseInt(terca_de.replaceAll(":", "")));
				
				String terca_ate = rs.getString("terca_ate");
				if(terca_ate!=null)
					c.setTerca_ate(Integer.parseInt(terca_ate.replaceAll(":", "")));
				
				String quarta_de = rs.getString("quarta_de");
				if(quarta_de!=null) 
					c.setQuarta_de(Integer.parseInt(quarta_de.replaceAll(":", "")));
				
				String quarta_ate = rs.getString("quarta_ate");
				if(quarta_ate!=null)
					c.setQuarta_ate(Integer.parseInt(quarta_ate.replaceAll(":", "")));
				
				String quinta_de = rs.getString("quinta_de");
				if(quinta_de!=null)
					c.setQuinta_de(Integer.parseInt(quinta_de.replaceAll(":", "")));
				
				String quinta_ate = rs.getString("quinta_ate");
				if(quinta_ate!=null)
					c.setQuinta_ate(Integer.parseInt(quinta_ate.replaceAll(":", "")));
				
				String sexta_de = rs.getString("sexta_de");
				if(sexta_de!=null)
					c.setSexta_de(Integer.parseInt(sexta_de.replaceAll(":", "")));
				
				String sexta_ate = rs.getString("sexta_ate");
				if(sexta_ate!=null)
					c.setSexta_ate(Integer.parseInt(sexta_ate.replaceAll(":", "")));
				
				String sabado_de = rs.getString("sabado_de");
				if(sabado_de!=null)
					c.setSabado_de(Integer.parseInt(sabado_de.replaceAll(":", "")));
				
				String sabado_ate = rs.getString("sabado_ate");
				if(sabado_ate!=null)
					c.setSabado_ate(Integer.parseInt(sabado_ate.replaceAll(":", "")));
				
				String domingo_de = rs.getString("domingo_de");
				if(domingo_de!=null)
					c.setDomingo_de(Integer.parseInt(domingo_de.replaceAll(":", "")));
				
				String domingo_ate = rs.getString("domingo_ate");
				if(domingo_ate!=null)
					c.setDomingo_ate(Integer.parseInt(domingo_ate.replaceAll(":", "")));
				
				
				
				c.setStatusDentroFora(rs.getInt("tipo"));
				//carrega os pontos da cerca
				String queryGeo = "select lat,lng from cerca_pontos where id_cerca="+c.getId();
				ResultSet rsGeo = getCon().createStatement().executeQuery(queryGeo);
				while(rsGeo.next()){
					Coordinate geo = new Coordinate(Double.parseDouble(rsGeo.getString("lat")), Double.parseDouble(rsGeo.getString("lng")));
					c.getCoordinates().add(geo);
				}
				cercas.add(c);
			}
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return cercas;
		}
	}
	
	/**
	 * busca os emails cadastro para notificacao da cerca
	 * @param idCerca
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("finally")
	public List<String> getCercaEmails(int idCerca) throws DaoException{
		List<String> emails = new ArrayList<String>();
		try{
			conectar();
			String query = "select email from cerca_email where id_cerca="+idCerca;
			ResultSet rs = getCon().createStatement().executeQuery(query);
			while(rs.next()){
				String email = rs.getString("email");
				emails.add(email);
			}
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return emails;
		}
	}
	
	/**
	 * busca os numeros de celulares de notificacao cadastrados para a cerca
	 * @param idCerca
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("finally")
	public List<String> getCercaNumerosSms(int idCerca) throws DaoException{
		List<String> numeros = new ArrayList<String>();
		try{
			conectar();
			String query = "select numero from cerca_sms where id_cerca="+idCerca;
			ResultSet rs = getCon().createStatement().executeQuery(query);
			while(rs.next()){
				String numero = rs.getString("numero");
				numeros.add(numero);
			}
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return numeros;
		}
	}
	
	
	/**
	 * atualizar tabela de status quando o veiculo entra ou sai da cerca, para controlar as notificacoes
	 * @param idCerca
	 * @param entrouSaiu
	 * @throws DaoException
	 */
	public void entrouSaiu(int idCerca,int entrouSaiu) throws DaoException{
		try{
			conectar();
			String query = "update cerca_veiculo set tipo="+entrouSaiu+" where id_cerca="+idCerca;
			getCon().createStatement().executeUpdate(query);
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}

}

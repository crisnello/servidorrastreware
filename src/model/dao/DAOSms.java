package model.dao;

import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;

import entidade.Sms;
import exception.DaoException;

public class DAOSms extends Dao{
	
	@SuppressWarnings("finally")
	public Sms getSmsEnviar() throws DaoException{
		Sms s = null;
		try{
			conectar();
			String query = "select id,numero,mensagem from sms_enviar where status_envio=1 or status_envio=3 limit 1";
			ResultSet rs = getCon().createStatement().executeQuery(query);
			while(rs.next()){
				s = new Sms();
				s.setId(rs.getInt("id"));
				s.setMsg(StringEscapeUtils.unescapeHtml4(rs.getString("mensagem")));
				s.setNumero(rs.getString("numero"));
				break;
			}
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return s;
		}
	}
	
	public void atualizar(String numero,boolean enviado) throws DaoException{
		try{
			conectar();
			if(enviado){
				String query = "update sms_enviar set status_envio=2,data_enviado='"+formatoDataBase.format(new Date())+"' where numero='"+numero+"'";
				getCon().createStatement().executeUpdate(query);
			}else{
				String query = "update sms_enviar set status_envio=3,tentativas=tentativas+1,data_enviado='"+formatoDataBase.format(new Date())+"' where numero='"+numero+"' and tentativas<=3";
				int update = getCon().createStatement().executeUpdate(query);
				if(update==0){
					query = "update sms_enviar set status_envio=4,data_enviado='"+formatoDataBase.format(new Date())+"' where numero='"+numero+"'";
					getCon().createStatement().executeUpdate(query);
				}
			}
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}
	
	public void inserir(Sms sms) throws DaoException{
		try{
			conectar();
			String query = "insert into sms_enviar(numero,mensagem,tipo,data_cadastro,status_envio,tentativas) values " +
					"('"+sms.getNumero()+"','"+sms.getMsg()+"',"+sms.getTipo()+",'"+formatoDataBase.format(new Date())+"',1,0)";
			getCon().createStatement().executeUpdate(query);
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}

}

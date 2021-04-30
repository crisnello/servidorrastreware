package model.dao;

import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;

import entidade.Email;
import exception.DaoException;

public class DAOEmail extends Dao{
	
	@SuppressWarnings("finally")
	public Email getEmailEnviar() throws DaoException{
		Email email = null;
		try{
			conectar();
			String query = "select id,destinatario,mensagem,titulo from email_enviar where situacao=1 limit 1";
			ResultSet rs = getCon().createStatement().executeQuery(query);
			while(rs.next()){
				email = new Email();
				email.setId(rs.getInt("id"));
				email.setTitulo(rs.getString("titulo"));
				email.setMensagem(StringEscapeUtils.unescapeHtml4(rs.getString("mensagem")));
				email.setDestinatario(rs.getString("destinatario"));
				break;
			}
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
			return email;
		}
	}
	
	public void atualizar(int id,boolean enviado) throws DaoException{
		try{
			conectar();
			
			int situacao = 2;
			if(!enviado){
				situacao = 3;
			}
			
			String query = "update email_enviar set situacao="+situacao+",data_envio='"+formatoDataBase.format(new Date())+"' where id="+id;
			getCon().createStatement().executeUpdate(query);
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}
	
	public void inserir(Email email) throws DaoException{
		try{
			conectar();
			String query = "insert into email_enviar(destinatario,mensagem,situacao,data_cadastro,titulo) values " +
					"('"+email.getDestinatario()+"','"+email.getMensagem()+"',1,'"+formatoDataBase.format(new Date())+"','"+email.getTitulo()+"')";
			getCon().createStatement().executeUpdate(query);
		}catch (Exception e) {
			logger.debug(e);
			throw new DaoException(e.getMessage(), e.getCause());
		}finally{
			desconectar();
		}
	}

}

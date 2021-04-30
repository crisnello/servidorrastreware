package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import model.Configuracoes;

public abstract class Dao {
	
	private Connection con;
	
	protected Logger logger = Logger.getLogger(Dao.class);
	
	protected SimpleDateFormat formatoDataBase = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	protected void conectar() throws Exception{
		// Carregando o JDBC Driver padrão
		String driverName = "com.mysql.jdbc.Driver";                        
		Class.forName(driverName);

		// Configurando a nossa conexão com um banco de dados//
		String serverName = Configuracoes.getValue("server");
		String mydatabase =Configuracoes.getValue("base");
		String url = "jdbc:mysql://"+serverName+"/"+mydatabase;
		String username = Configuracoes.getValue("user");      
		String password = Configuracoes.getValue("pass");
		con = DriverManager.getConnection(url, username, password);
		con.setAutoCommit(false);
	}
	
	protected void desconectar(){
		try {

            if(!con.isClosed()){
            	con.commit();
            	con.close();
            }

        } catch (SQLException e) {
        	logger.debug(e);
        }        
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public Connection getCon() {
		return con;
	}

}

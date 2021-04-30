package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import model.parser.Parser;

import org.apache.log4j.Logger;

public class ServidorConexao extends Thread{	
	
	private Logger logger = Logger.getLogger(ServidorConexao.class);
	
	private Parser parser;
	private int porta;
	
	public ServidorConexao(Parser parser, int porta) {
		this.parser = parser;
		this.porta = porta;
	}

	public void run() {
		try {
			ServerSocket server = new ServerSocket(porta);
			logger.debug("servidor iniciado na porta: "+porta);
			while(true){
				try {
					Socket s = server.accept();
					s.setSoTimeout(60000);
					logger.debug("conexao aceita: "+s.getRemoteSocketAddress());
					ThreadConexao tc = new ThreadConexao(s,parser.getClass().newInstance());
					tc.start();
				}  catch (Exception e) {
					logger.debug("falha ao aceitar conexao servidor", e);
				} 
			}
			
		} catch (IOException e) {
			logger.debug("falha ao iniciar servidor na porta: "+porta, e);
		} 
	}
	
}

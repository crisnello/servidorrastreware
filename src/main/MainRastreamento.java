package main;
import org.apache.log4j.Logger;

import model.Configuracoes;
import model.ServidorConexao;
import model.ThreadBloqueio;
import model.ThreadEmail;
import model.parser.ParserMobile;
import model.parser.ParserSMS;
import model.parser.ParserTK103;
import model.parser.ParserVT310;


public class MainRastreamento {
	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(MainRastreamento.class);
		
		try{
			ThreadBloqueio bloq = new ThreadBloqueio();
			bloq.start();
		}catch (Exception e) {
			logger.debug("erro iniciando ThreadBloqueio",e);
		}
		
		//inicia servidor mobile
		try{
			ServidorConexao serverMobile = new ServidorConexao(new ParserMobile(),Integer.parseInt(Configuracoes.getValue("portaMobile")));
			serverMobile.start();
		}catch (Exception e) {
			logger.debug("erro iniciando mobile",e);
		}
		
		//inicia servidor tk103
		try{
			ServidorConexao serverTK103 = new ServidorConexao(new ParserTK103(),Integer.parseInt(Configuracoes.getValue("portaTK103")));
			serverTK103.start();
		}catch (Exception e) {
			logger.debug("erro iniciando tk103",e);
		}
		
		//inicia servidor vt310
		try{
			ServidorConexao serverVT310 = new ServidorConexao(new ParserVT310(),Integer.parseInt(Configuracoes.getValue("portaVT310")));
			serverVT310.start();
		}catch (Exception e) {
			logger.debug("erro iniciando vt310",e);
		}
		
		//inicia servidor sms
		try{
			ServidorConexao serverSMS = new ServidorConexao(new ParserSMS(),Integer.parseInt(Configuracoes.getValue("portaSMS")));
			serverSMS.start();
		}catch (Exception e) {
			logger.debug("erro iniciando sms",e);
		}
		
		try{
			ThreadEmail te = new ThreadEmail();
			te.start();
		}catch (Exception e) {
			logger.debug("erro iniciando email server",e);
		}
	}
}

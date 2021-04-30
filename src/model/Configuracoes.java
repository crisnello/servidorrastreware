package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuracoes {

	private static Logger logger = Logger.getLogger(Configuracoes.class);
	
	public static int getIntValue(String key){
		return Integer.parseInt(getValue(key));
	}
	
	public static String getValue(String key){
		Properties p = new Properties();
		String ret = null;
		try {
			p.load(new FileInputStream("./config.txt"));
			ret = p.getProperty(key);
			
		} catch (FileNotFoundException e) {
			logger.debug(e);
		} catch (IOException e) {
			logger.debug(e);
		}
		if(ret==null){
			ret = "";
		}
		return ret;
	}
	
}

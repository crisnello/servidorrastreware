package main;

import util.MyCriptografia;

import java.math.BigInteger;  
import org.apache.commons.codec.binary.Base64;

public class GeraSenha {

	public static void main(String[] args) {

		String strSenha = "crisnelloalerta@123";
		//String strSenha = "321@atrelaollensirc";
		
		//String strSenha = "123456";
		String senhaMD5 = MyCriptografia.crypt(strSenha);
		System.out.println("crypt:"+senhaMD5);
		
		byte[] bytes = Base64.encodeBase64(new BigInteger(senhaMD5, 16).toByteArray());  
	      String s = new String(bytes);  
	      System.out.println("MD5encodeBase64:"+s);  
	      
	      String sCrypito = MyCriptografia.crypito(strSenha);
	      
	      System.out.println("crypito:"+sCrypito);
	      System.out.println("descrypito:"+MyCriptografia.descrypito(sCrypito));

	}

}

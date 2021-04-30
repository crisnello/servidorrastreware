package util;

public class MyCriptografia extends Criptografia {
	
	public static String descrypito(String str){
		if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String para encriptar não pode ser nula ou zerada");
        }
        String retorno = new String();
        for(int r=0;r<str.length();r++){
        	char ret = str.charAt(r);
	        for (int i = 48; i <= 123; i++) {  
		    	    char ch = (char) i;  
		    	    if(ret == ch)
		    	    	retorno = retorno + --ch;
		    }
        }
        String resp = new String();
        for(int i=0;i<retorno.length();i++){
        	 resp = resp + retorno.charAt((retorno.length()-1)-i);
        }

        
		return resp;
		
	}
	
	public static String crypito(String str) {
		
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String para encriptar não pode ser nula ou zerada");
        }
        String resp = new String();
        for(int i=0;i<str.length();i++){
        	 resp = resp + str.charAt((str.length()-1)-i);
        }
        //48  0 49  1 50  2 51  3 52  4 53  5 54  6 55  7 56  8 57  9
        //65  A 66  B 67  C 68  D 69  E 70  F 71  G 72  H 73  I 74  J 75  K 76  L 77  M 78  N 79  O 80  P 81  Q 82  R 83  S 84  T 85  U 86  V 87  W 88  X 89  Y 90  Z
        //97  a 98  b 99  c 100  d 101  e 102  f 103  g 104  h 105  i 106  j 107  k 108  l 109  m 110  n 111  o 112  p 113  q 114  r 115  s 116  t 117  u 118  v 119  w 120  x 121  y 122  z 123
        String retorno = new String();
        for(int r=0;r<resp.length();r++){
        	char ret = resp.charAt(r);
	        for (int i = 48; i <= 123; i++) {  
		    	    char ch = (char) i;  
		    	    if(ret == ch)
		    	    	retorno = retorno + ++ch;
		    }
        }
        
		return retorno;
	}
	

}

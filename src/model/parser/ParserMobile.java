package model.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.ThreadConexao;

import entidade.Bloqueio;
import entidade.PacoteRecebido;
import entidade.Posicionamento;
import exception.TraduzirMensagemException;

public class ParserMobile extends Parser{
	
	//"TYPE","TIME","LAT","LON","ALT","BEARING","ACCURACY","SPEED","NAME","DESCRIPTION","SEGMENT"
	//msgTemplateMEI = "5880,robo1,1";   
	
	public PacoteRecebido traduzirMensagem(byte[] buf,ThreadConexao th)
			throws TraduzirMensagemException {
		try{
			String s = new String(buf);
			s = s.replaceAll("\"", "");
			PacoteRecebido retorno = null;
			if(s.startsWith("P")){
				String p[] = s.split(",");
				Posicionamento pos = new Posicionamento();
				pos.setImei(getImei());
				pos.setValido(true);
				pos.setDataRecebido(new Date());
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
				pos.setDataCapturado(df.parse(p[1]));
				double parse = Double.parseDouble(p[2]);
				if(parse!=0)
				pos.setLat(parse);
				parse = Double.parseDouble(p[3]);
				if(parse!=0)
				pos.setLon(parse);
				parse = Double.parseDouble(p[5]);
				if(parse!=0)
				pos.setAng(parse);
				parse = Double.parseDouble(p[6]);
				if(parse!=0)
				pos.setPrecisao(parse);
				//convert knot para kmh
				parse = Double.parseDouble(p[7])*1.852;
				if(parse!=0)
				pos.setVel(Double.parseDouble(p[7])*1.852);
				
				pos.setSize(s.length());
				retorno = pos;
				
			}else if(s.startsWith("5880")){
				String msg[] = s.split(",");
				setImei(msg[msg.length-2]);
			}
			return retorno;
		}catch (Exception e) {
			logger.debug("falha no parserCelular",e);
			throw new TraduzirMensagemException("Erro ao traduzir mensagem", e);
		}
	}

	@Override
	public void processamentoEspefico(ThreadConexao th, PacoteRecebido pacote)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enviarBloqueio(ThreadConexao th, Bloqueio b) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

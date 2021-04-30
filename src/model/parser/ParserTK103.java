package model.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.ThreadConexao;
import entidade.Autenticacao;
import entidade.Bloqueio;
import entidade.PacoteRecebido;
import entidade.Posicionamento;
import exception.TraduzirMensagemException;

public class ParserTK103 extends Parser{

	public PacoteRecebido traduzirMensagem(byte[] buf,ThreadConexao th)
			throws TraduzirMensagemException {
		try{
			
			String s = new String(buf);
			
			if (s.startsWith("imei:")) {
				String fs[] = s.split(",");
				Posicionamento p = new Posicionamento();
				
				p.setValido("F".equals(fs[4]));
				
				p.setDataRecebido(new Date());
				p.setImei(fs[0].replaceFirst("imei:", ""));
				
				setImei(p.getImei());
				
				p.setTipo(fs[1]);
				
				SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmm");
				p.setDataCapturado(df.parse(fs[2]));
				
				if(!p.isValido()){
					return p;
				}
				
				// Latitude
			    String raw_lat = fs[7];
			    String lat_deg = raw_lat.substring(0, 2);
			    String lat_min1 = raw_lat.substring(2, 4);
			    String lat_min2 = raw_lat.substring(5);
			    String lat_min3 = "0." + lat_min1 + lat_min2;
			    float lat_dec = Float.parseFloat(lat_min3)/.6f;
			    float lat_val = Float.parseFloat(lat_deg) + lat_dec;

			    // Latitude direction
			    String lat_direction = fs[8];
			    if(lat_direction.equals("S")){
			    	lat_val = lat_val * -1;
			    }
				p.setLat(lat_val);
			    
			    // Longitude
			    String raw_lon = fs[9];
			    String lon_deg = raw_lon.substring(0, 3);
			    String lon_min1 = raw_lon.substring(3, 5);
			    String lon_min2 = raw_lon.substring(6);            
			    String lon_min3 = "0." + lon_min1 + lon_min2;
			    float lon_dec = Float.parseFloat(lon_min3)/.6f;
			    float lon_val = Float.parseFloat(lon_deg) + lon_dec;
			    
			    
			    // Longitude direction
			    String lon_direction = fs[10];
			    if(lon_direction.equals("W")){
			    	lon_val = lon_val * -1;
			    }
			    p.setLon(lon_val);
			    
			    double knots = Double.parseDouble(fs[11]);
			    if(knots>0){
			    	p.setVel(knots * 1.8532);
			    }else{
			    	p.setVel(0);
			    }
			    
			    if(!"".equals(fs[12])){
			    	p.setAng(Double.parseDouble(fs[12]));
			    }
			    return p;
			}else if(s.startsWith("##") && s.endsWith("A;")){
				Autenticacao au = new Autenticacao();
				String fs[] = s.split(",");
				au.setImei(fs[0].replaceFirst("imei:", ""));
				setImei(au.getImei());
				au.setRet("LOAD");
				return au;
			}else{
				Autenticacao au = new Autenticacao();
				String fs[] = s.split(",");
				au.setImei(fs[0].replaceFirst("imei:", ""));
				setImei(au.getImei());
				au.setRet("ON");
				return au;
			}
		}catch (Exception e) {
			logger.debug("erro no parserTK103", e);
			throw new TraduzirMensagemException("Erro ao traduzir msg", e);
		}
	}

	public void processamentoEspefico(ThreadConexao th, PacoteRecebido pacote)
			throws Exception {
		/**
		logger.debug("INICIO");
		long inicio = System.currentTimeMillis();
		try {
			
			if(pacote instanceof Posicionamento){
				Veiculo v = th.getVeiculo(null);
				Posicionamento p = (Posicionamento) pacote;
				
				
				if("tracker".equals(p.getTipo()) || 
						"dt".equals(p.getTipo()) || 
						"gt".equals(p.getTipo()) ||
						"et".equals(p.getTipo())){
					//se estiver parado a mais de 5m
					if(p.getDataCapturado().getTime() - v.getUltimoPosicionamento().getDataCapturado().getTime()>= 300000){
						//atualiza o intervalo de pacotes para 10m
						logger.debug("esta parado a mais de 5m, atualiza o intervalo de pacotes para 10m");
						th.getOut().write(("**,imei:"+p.getImei()+",C,10m").getBytes());
						th.getOut().flush();
						
						//liga o alarm de movimento
						logger.debug("liga o alarm de movimento");
						th.getOut().write(("**,imei:"+p.getImei()+",G").getBytes());
						th.getOut().flush();
					}
				}
				//se for alarm
				else{
					//cancelo os alarms
					logger.debug("cancelo os alarms");
					th.getOut().write(("**,imei:"+p.getImei()+",E").getBytes());
					th.getOut().flush();
					//voltar a monitorar de 20 em 20 segundos
					logger.debug("voltar a monitorar de 20 em 20 segundos");
					th.getOut().write(("**,imei:"+p.getImei()+",C,20s").getBytes());
					th.getOut().flush();
				}
			}else{
				Autenticacao au = (Autenticacao) pacote;
				if(au.getRet()!=null){
					logger.debug("cofirmando autenticacao: "+au.getRet());
					th.getOut().write(au.getRet().getBytes());
					th.getOut().flush();
				}
			}
			
		} catch (DaoException e) {
			logger.debug(e);
		} 
		logger.debug("FIM ("+(System.currentTimeMillis()-inicio)+" millis)");
		**/
	}

	@Override
	public void enviarBloqueio(ThreadConexao th, Bloqueio b) throws Exception {
		// TODO Auto-generated method stub
		
	}


}

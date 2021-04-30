package model.parser;

import model.ThreadConexao;
import model.dao.DAOSms;
import entidade.Bloqueio;
import entidade.PacoteRecebido;
import entidade.Sms;
import exception.DaoException;
import exception.TraduzirMensagemException;

/**
 * parser das mensagens recebidas da aplica que consulta sms para envio
 * @author Crisnello
 *
 */
public class ParserSMS extends Parser{

	private DAOSms dao = new DAOSms();
	
	public PacoteRecebido traduzirMensagem(byte[] buf,ThreadConexao th)
			throws TraduzirMensagemException {
		try{
			String s = new String(buf);
			PacoteRecebido pr = new PacoteRecebido();
			pr.setTipo(s);
			return pr;
		}catch (Exception e) {
			logger.debug("falha no parserSMS",e);
			throw new TraduzirMensagemException("Erro ao traduzir mensagem", e);
		}
	}

	public void processamentoEspefico(ThreadConexao th, PacoteRecebido pacote)
			throws Exception {
		try {
			//buscar ultimo sms para envio
			if(pacote.getTipo().equals("SMSGET")){
				Sms sms = dao.getSmsEnviar();
				if(sms!=null){
					String msg = sms.getNumero()+sms.getMsg();
					logger.debug("respondendo sms: "+msg);
					th.getOut().write(msg.getBytes());
				}else{
					th.getOut().write("vazio".getBytes());
					th.getOut().flush();
				}
			
			//sms enviado
			}else if(pacote.getTipo().startsWith("SMSOK")){
				logger.debug("sms enviado: "+pacote.getTipo());
				String msg[] = pacote.getTipo().split(";");
				dao.atualizar(msg[1], true);
				th.getOut().write("vazio".getBytes());
				th.getOut().flush();
			
			//falha ao enviar sms
			}else if(pacote.getTipo().startsWith("SMSNOK")){
				logger.debug("sms não enviado: "+pacote.getTipo());
				String msg[] = pacote.getTipo().split(";");
				dao.atualizar(msg[1], false);
				th.getOut().write("vazio".getBytes());
				th.getOut().flush();
			}
		} catch (DaoException e) {
			th.getOut().write("vazio".getBytes());
			th.getOut().flush();
		}
		th.getOut().flush();
	}

	@Override
	public void enviarBloqueio(ThreadConexao th, Bloqueio b) throws Exception {
		// TODO Auto-generated method stub
		
	}


}

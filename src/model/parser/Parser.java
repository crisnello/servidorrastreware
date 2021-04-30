package model.parser;

import model.ThreadConexao;

import org.apache.log4j.Logger;

import entidade.Bloqueio;
import entidade.PacoteRecebido;
import exception.TraduzirMensagemException;

public abstract class Parser {
	
	protected static Logger logger = Logger.getLogger("A1");
	
	private String imei;
	
	/**
	 * parser da mensagem recebida, tratar aqui
	 * @param buf
	 * @param th
	 * @return
	 * @throws TraduzirMensagemException
	 */
	public abstract PacoteRecebido traduzirMensagem(byte[] buf,ThreadConexao th) throws TraduzirMensagemException;
	
	/**
	 * quando precisar responder para o rastreador, tratar aqui
	 * @param th
	 * @param pacote
	 * @throws Exception
	 */
	public abstract void processamentoEspefico(ThreadConexao th,PacoteRecebido pacote) throws Exception;
	
	/**
	 * chamado por uma thread de tempo em tempo caso haja bloqueio para envio
	 * @param th
	 * @param b
	 * @throws Exception
	 */
	public abstract void enviarBloqueio(ThreadConexao th,Bloqueio b) throws Exception;
	
	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}
	
}

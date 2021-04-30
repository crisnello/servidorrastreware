package processor;

import model.ThreadConexao;

import org.apache.log4j.Logger;

import entidade.PacoteRecebido;

public abstract class Processo{
	
	protected Logger logger = Logger.getLogger(Processo.class);

	public abstract void processar(PacoteRecebido pacote,ThreadConexao conexao);
	
}

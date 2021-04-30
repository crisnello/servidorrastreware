package model;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import model.dao.DAOBloqueio;

import org.apache.log4j.Logger;

import entidade.Bloqueio;

/**
 * fica lendo a tabela de bloqueio e entao envia para a conexao do veiculo correspondente
 * @author Crisnello
 *
 */
public class ThreadBloqueio extends Thread{	
	
	private Logger logger = Logger.getLogger(ThreadBloqueio.class);
	
	public void run() {

		while(true){
			try{
				logger.debug("inicio loop thread bloqueio");
				DAOBloqueio dao = new DAOBloqueio();
				List<Bloqueio> bs = dao.getBloqueioEnviar();
				
				for (Iterator iterator = bs.iterator(); iterator.hasNext();) {
					Bloqueio bloqueio = (Bloqueio) iterator.next();
					logger.debug("processando bloqueio "+bloqueio.getId());
					try{
						//se nunca foi enviado, ou se faz mais de 3 min o envio
						if(bloqueio.getEnviado()==null || (System.currentTimeMillis()-bloqueio.getEnviado().getTime()>3*60000)){
							ThreadConexao th = PoolConexoes.getConexao(bloqueio.getIdVeiculo());
							if(th!=null)
							synchronized (th) {
								if(th!=null){
									th.getParser().enviarBloqueio(th, bloqueio);
									bloqueio.setEnviado(new Date());
									bloqueio.setTentativas(bloqueio.getTentativas()+1);
									dao.atualizarBloqueio(bloqueio);
								}
							}
						}
					}catch (Throwable e) {
						logger.debug(e);
					}
				}
				
				logger.debug("fim loop thread bloqueio");

				Thread.sleep(Configuracoes.getIntValue("delay_bloqueio"));
			}catch (Throwable e) {
				logger.debug(e);
			}			
		}
	
	}
	
}

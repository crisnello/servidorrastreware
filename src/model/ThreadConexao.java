package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import model.dao.DAOCercaEletronica;
import model.dao.DAOPosicionamento;
import model.dao.DAOVeiculo;
import model.parser.Parser;

import org.apache.log4j.Logger;

import processor.CercaProcessor;
import processor.PosicionamentoProcessor;
import processor.Processo;
import entidade.PacoteRecebido;
import entidade.Posicionamento;
import entidade.Veiculo;
import exception.DaoException;
import exception.TraduzirMensagemException;

public class ThreadConexao extends Thread{

	private Logger logger = Logger.getLogger(ThreadConexao.class);
	
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
	private Veiculo veiculo;
	private List<byte[]> pacotesProcessar;
	
	private Thread consumidor;
	
	private Parser parser;
	
	//ultima atualizacao do cache de veiculos
	private long lastUpdateVeiculo = 0;
	//tempo em min para atualizacao de cache
	private int cacheVeiculo = Configuracoes.getIntValue("cache_veiculo") * 60000;
	
	public ThreadConexao(Socket socket,Parser parser) throws InterruptedException {
		this.socket = socket;
		this.setParser(parser);
	}
	
	/**
	 * apenas joga as mensagens para a fila de processamento
	 */
	public void run() {
		try {
			setOut(socket.getOutputStream());
			in = socket.getInputStream();
			
			this.pacotesProcessar =  new ArrayList<byte[]>();
			this.consumidor = new Consumidor();
			this.consumidor.start();
			
			try{
				out.write("CONECTOU".getBytes());
				out.flush();
			}catch(Exception e){
				logger.debug("Erro ao enviar o CONECTOU", e);
				//e.printStackTrace();
			}
			
			
			while(true){
				byte[] buf = new byte[256];
				int size = in.read(buf);
				if(size<0){
					logger.debug("size da msg menor que 0, fechando conexao");
					try {
						socket.close();
					} catch (Exception e) {
						logger.debug("falha fechando conexao",e);
					}
					consumidor.interrupt();
					consumidor = null;
					try {
						PoolConexoes.removerConexao(veiculo.getId());
					} catch (Exception e) {	}
					break;
				}
				
				byte[] buf2 = new byte[size];
				for (int i = 0; i < size; i++) {
					buf2[i] = buf[i];
				}
				
				synchronized (pacotesProcessar) {
					pacotesProcessar.add(buf2);
					pacotesProcessar.notifyAll();
				}
				
				try{
					String strResp = "RECEBEU:"+new String(buf);
					out.write(strResp.getBytes());
					out.flush();
					
				}catch(Exception e){
					logger.debug("Erro ao enviar resposta", e);
				}
				
				
				
			}
			
		} catch (IOException e) {
			logger.debug("falha de IO, fechando conexao",e);
			try {
				socket.close();
			} catch (IOException e1) {
				logger.debug("falha fechando conexao",e1);
			}
			try {
				PoolConexoes.removerConexao(veiculo.getId());
			} catch (Exception xe) {	}
		} catch (Throwable e) {
			logger.debug(e);
		}
		
	}
	
	/**
	 * processa os pacotes, toda a logica aqui
	 */
	class Consumidor extends Thread {
		public void run() {
			logger.debug("Consumidor iniciado");
			
			while(true){
					synchronized (pacotesProcessar) {
						try {

							for (int i = 0; i < pacotesProcessar.size(); i++) {
								byte[] buf = pacotesProcessar.get(i);
								logger.debug("received from rastreador("+getParser().getImei()+"): ["+new String(buf)+"]");
								PacoteRecebido pacote = getParser().traduzirMensagem(buf,getThread());
								if(pacote instanceof Posicionamento){
									Posicionamento pos = (Posicionamento) pacote;
									if(pos.isValido()){
										Processo proc = new PosicionamentoProcessor();
										proc.processar(pacote, getThread());
										Processo procCerca = new CercaProcessor();
										procCerca.processar(pacote, getThread());
									}else{
										logger.debug("posicionamento invalido");
									}
								}
								
								getParser().processamentoEspefico(getThread(), pacote);
								
								//envia algo que foi setado pelo processamento
								if(pacote!=null && pacote.getSend()!=null && pacote.getSend().length>0){
									logger.debug("sending to rastreador("+getParser().getImei()+"): ["+new String(pacote.getSend())+"]");
									out.write(pacote.getSend());
								}
								
							}
							pacotesProcessar.clear();
								
						} catch (TraduzirMensagemException e) {
							logger.debug(e);
						} catch (NullPointerException e) {
							logger.debug(e);
						} catch (IOException e) {
							logger.debug(e);
							break;
						} catch (InterruptedException e) {
							logger.debug(e);
						} catch (Throwable e) {
							logger.debug(e);
						}
						
						try {
							pacotesProcessar.wait();
						} catch (InterruptedException e) {
							logger.debug(e);
						}
						
					}
				}
		}
	}
	

	private ThreadConexao getThread(){
		return this;
	}
	
	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	/**
	 * busca o veiculo do banco apenas a primeira vez e mantem em cache
	 * @param imei
	 * @return
	 * @throws DaoException
	 */
	public synchronized Veiculo getVeiculo(String imei) throws DaoException {
		long time = System.currentTimeMillis();
		//atualiza cache do veiculo se maior que tempo configurado
		if((veiculo==null || time - lastUpdateVeiculo > cacheVeiculo) && imei!=null){
			DAOVeiculo daoVeiculo = new DAOVeiculo();
			logger.debug("buscando o veiculo pelo imei: "+imei);
			Veiculo v = daoVeiculo.getVeiculoByRastreador(imei);
			DAOPosicionamento daoPosicionamento = new DAOPosicionamento();
			logger.debug("buscando ultimo posicionamento do veiculo imei: "+imei);
			Posicionamento pos = daoPosicionamento.getUltimoPosicionamento(v);
			if(pos!=null){
				logger.debug("ultimo posicionamento encontrado para o veiculo imei: "+imei);
				v.setUltimoPosicionamento(pos);	
				v.getInfo().setIdPosicionamentoAtual(pos.getId());
			}
			logger.debug("atualiza cercas do veiculo imei: "+imei);
			DAOCercaEletronica daoCerca = new DAOCercaEletronica();
			v.setCercas(daoCerca.getCercas(v));
			logger.debug(v.getCercas().size()+" cercas encontradas do veiculo imei: "+imei);
			setVeiculo(v);
			lastUpdateVeiculo = time;
			//atualiza o pool de conexoes
			PoolConexoes.addConexao(v.getId(), getThread());
		}
		return veiculo;
	}

	/**
	 * busca o veiculo do banco se validar cache
	 * @param imei
	 * @return
	 * @throws DaoException
	 */
	public Veiculo getVeiculoForced(String imei) throws DaoException {
		if(imei!=null){
			DAOVeiculo daoVeiculo = new DAOVeiculo();
			logger.debug("buscando o veiculo sem validar cache pelo imei: "+imei);
			Veiculo v = daoVeiculo.getVeiculoByRastreador(imei);
			return v;
		}
		return null;
	}
	
	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}
	
}
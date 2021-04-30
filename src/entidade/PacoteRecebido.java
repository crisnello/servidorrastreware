package entidade;

import java.util.Date;

/**
 * classe pai para todos os pacotes recebidos
 * @author Crisnello
 *
 */
public class PacoteRecebido {
	
	/**
	 * tipo do pacote
	 */
	private String tipo;
	/**
	 * identificacao do rastreador
	 */
	private String imei;
	
	/**
	 * enviar para o rastreador
	 */
	private byte[] send;
	/**
	 * tamanho do pacote
	 */
	private int size;
	/**
	 * data de chegada do pacote no sistema
	 */
	private Date dataRecebido;
	/**
	 * data gerada pelo rastreador
	 */
	private Date dataCapturado;
	
	/**
	 * precisao do gps
	 */
	private double precisao;
	
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * tamanho do pacote
	 * @return
	 */
	public int getSize() {
		return size;
	}
	public void setDataRecebido(Date dataRecebido) {
		this.dataRecebido = dataRecebido;
	}
	/**
	 * data de chegada do pacote no sistema
	 * @return
	 */
	public Date getDataRecebido() {
		return dataRecebido;
	}
	public void setDataCapturado(Date dataCapturado) {
		this.dataCapturado = dataCapturado;
	}
	/**
	 * data gerado pelo rastreador
	 * @return
	 */
	public Date getDataCapturado() {
		return dataCapturado;
	}
	public void setPrecisao(double precisao) {
		this.precisao = precisao;
	}
	public double getPrecisao() {
		return precisao;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public byte[] getSend() {
		return send;
	}
	public void setSend(byte[] send) {
		this.send = send;
	}
	
}

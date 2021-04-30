package entidade;

import java.util.Date;

public class Bloqueio {
	
	private int id;
	private int idVeiculo;
	private int idStatus;
	private int tentativas;
	private Date enviado;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdVeiculo() {
		return idVeiculo;
	}
	public void setIdVeiculo(int idVeiculo) {
		this.idVeiculo = idVeiculo;
	}
	public int getIdStatus() {
		return idStatus;
	}
	public void setIdStatus(int idStatus) {
		this.idStatus = idStatus;
	}
	public void setEnviado(Date enviado) {
		this.enviado = enviado;
	}
	public Date getEnviado() {
		return enviado;
	}
	public void setTentativas(int tentativas) {
		this.tentativas = tentativas;
	}
	public int getTentativas() {
		return tentativas;
	}

}

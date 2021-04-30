package entidade;

import java.util.ArrayList;
import java.util.List;

public class Veiculo {
	
	private int id;
	private String placa;
	private String modelo;
	private double km;
	private int idStatus;
	private int idRastreador;
	/**
	 * recuperado da base de dados para ajustar horario caso necessario
	 */
	private int timeDiference;
	
	private Info info;
	
	private List<CercaEletronica> cercas;
	
	public Veiculo() {
		setCercas(new ArrayList<CercaEletronica>());
		setInfo(new Info());
	}
	
	
	/**
	 * ultimo posicionamento inserido, sempre atualizado quando chegar um posicionamento novo
	 */
	private Posicionamento ultimoPosicionamento;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPlaca() {
		return placa;
	}
	public void setPlaca(String placa) {
		this.placa = placa;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public double getKm() {
		return km;
	}
	public void setKm(double km) {
		this.km = km;
	}
	public void setUltimoPosicionamento(Posicionamento ultimoPosicionamento) {
		this.ultimoPosicionamento = ultimoPosicionamento;
	}
	public Posicionamento getUltimoPosicionamento() {
		return ultimoPosicionamento;
	}
	public void setIdRastreador(int idRastreador) {
		this.idRastreador = idRastreador;
	}
	public int getIdRastreador() {
		return idRastreador;
	}

	public void setCercas(List<CercaEletronica> cercas) {
		this.cercas = cercas;
	}
	public List<CercaEletronica> getCercas() {
		return cercas;
	}
	public void setInfo(Info info) {
		this.info = info;
	}
	public Info getInfo() {
		return info;
	}
	public void setTimeDiference(int timeDiference) {
		this.timeDiference = timeDiference;
	}
	public int getTimeDiference() {
		return timeDiference;
	}
	public void setIdStatus(int idStatus) {
		this.idStatus = idStatus;
	}
	public int getIdStatus() {
		return idStatus;
	}
}

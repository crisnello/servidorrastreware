package entidade;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class CercaEletronica {
	
	private int monitorar;
	private int alerta;
	private int id;
	private String nome;
	
	private int statusDentroFora;
	
	private List<Coordinate> coordinates;

	private int segunda_de = -1;
	private int segunda_ate = -1;
	private int terca_de = -1;
	private int terca_ate = -1;
	private int quarta_de = -1;
	private int quarta_ate = -1;
	private int quinta_de = -1;
	private int quinta_ate = -1;
	private int sexta_de = -1;
	private int sexta_ate = -1;
	private int sabado_de = -1;
	private int sabado_ate = -1;
	private int domingo_de = -1;
	private int domingo_ate = -1;
	
	public CercaEletronica() {
		setCoordinates(new ArrayList<Coordinate>());
	}
	
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}


	public int getMonitorar() {
		return monitorar;
	}

	public void setMonitorar(int monitorar) {
		this.monitorar = monitorar;
	}

	public int getAlerta() {
		return alerta;
	}

	public void setAlerta(int alerta) {
		this.alerta = alerta;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setStatusDentroFora(int statusDentroFora) {
		this.statusDentroFora = statusDentroFora;
	}

	public int getStatusDentroFora() {
		return statusDentroFora;
	}

	public int getSegunda_de() {
		return segunda_de;
	}

	public void setSegunda_de(int segunda_de) {
		this.segunda_de = segunda_de;
	}

	public int getSegunda_ate() {
		return segunda_ate;
	}

	public void setSegunda_ate(int segunda_ate) {
		this.segunda_ate = segunda_ate;
	}

	public int getTerca_de() {
		return terca_de;
	}

	public void setTerca_de(int terca_de) {
		this.terca_de = terca_de;
	}

	public int getTerca_ate() {
		return terca_ate;
	}

	public void setTerca_ate(int terca_ate) {
		this.terca_ate = terca_ate;
	}

	public int getQuarta_de() {
		return quarta_de;
	}

	public void setQuarta_de(int quarta_de) {
		this.quarta_de = quarta_de;
	}

	public int getQuarta_ate() {
		return quarta_ate;
	}

	public void setQuarta_ate(int quarta_ate) {
		this.quarta_ate = quarta_ate;
	}

	public int getQuinta_de() {
		return quinta_de;
	}

	public void setQuinta_de(int quinta_de) {
		this.quinta_de = quinta_de;
	}

	public int getQuinta_ate() {
		return quinta_ate;
	}

	public void setQuinta_ate(int quinta_ate) {
		this.quinta_ate = quinta_ate;
	}

	public int getSexta_de() {
		return sexta_de;
	}

	public void setSexta_de(int sexta_de) {
		this.sexta_de = sexta_de;
	}

	public int getSexta_ate() {
		return sexta_ate;
	}

	public void setSexta_ate(int sexta_ate) {
		this.sexta_ate = sexta_ate;
	}

	public int getSabado_de() {
		return sabado_de;
	}

	public void setSabado_de(int sabado_de) {
		this.sabado_de = sabado_de;
	}

	public int getSabado_ate() {
		return sabado_ate;
	}

	public void setSabado_ate(int sabado_ate) {
		this.sabado_ate = sabado_ate;
	}

	public int getDomingo_de() {
		return domingo_de;
	}

	public void setDomingo_de(int domingo_de) {
		this.domingo_de = domingo_de;
	}

	public int getDomingo_ate() {
		return domingo_ate;
	}

	public void setDomingo_ate(int domingo_ate) {
		this.domingo_ate = domingo_ate;
	}
	
}

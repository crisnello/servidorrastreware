package entidade;


public class Posicionamento extends PacoteRecebido{
	
	private Veiculo veiculo;
	private int id;
	
	private boolean valido;
	
	private double lat = 0;
	private double lon = 0;
	private double ang = 0;
	private double vel = 0;
	/**
	 * ignicao on - off, panico
	 */
	private int tipoPosicionamento = 0;
	
	public Veiculo getVeiculo() {
		return veiculo;
	}
	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getAng() {
		return ang;
	}
	public void setAng(double ang) {
		this.ang = ang;
	}
	public double getVel() {
		return vel;
	}
	public void setVel(double vel) {
		this.vel = vel;
	}
	public boolean isValido() {
		return valido;
	}
	public void setValido(boolean valido) {
		this.valido = valido;
	}
	public void setTipoPosicionamento(int tipoPosicionamento) {
		this.tipoPosicionamento = tipoPosicionamento;
	}
	public int getTipoPosicionamento() {
		return tipoPosicionamento;
	}

}

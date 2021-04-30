package entidade;

public class Sms {
	
	private String numero;
	private String msg;
	private int id;
	private int tipo;
	
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public int getTipo() {
		return tipo;
	}
	

}

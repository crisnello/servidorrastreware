package entidade;

public class Email {
	
	private int id;
	private String destinatario;
	private String mensagem;
	private String titulo;
	//1 enviar 2 enviado 3 erro
	private int situacao;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}
	public int getSituacao() {
		return situacao;
	}

}

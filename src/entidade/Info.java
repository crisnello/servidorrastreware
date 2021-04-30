package entidade;

public class Info {
	
	
	private boolean atualizarIntervaloMensagens = true;
	
	/**
	 * usado para o update da tabela posicionamento atual
	 */
	private int idPosicionamentoAtual;
	
	
	public int getIdPosicionamentoAtual() {
		return idPosicionamentoAtual;
	}

	public void setIdPosicionamentoAtual(int idPosicionamentoAtual) {
		this.idPosicionamentoAtual = idPosicionamentoAtual;
	}

	public void setAtualizarIntervaloMensagens(boolean atualizarIntervaloMensagens) {
		this.atualizarIntervaloMensagens = atualizarIntervaloMensagens;
	}

	public boolean isAtualizarIntervaloMensagens() {
		return atualizarIntervaloMensagens;
	}
	
}

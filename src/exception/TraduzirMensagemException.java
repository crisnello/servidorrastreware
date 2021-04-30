package exception;

public class TraduzirMensagemException extends Throwable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7512327435329016147L;
	private Throwable cause;
	private String msg;
	
	public Throwable getCause() {
		return cause;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public TraduzirMensagemException(String msg,Throwable cause) {
		this.msg = msg;
		this.cause = cause;
	}

}

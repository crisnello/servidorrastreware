package exception;

public class ProcessException extends Throwable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1356721543088987691L;
	private Throwable cause;
	private String msg;
	private StackTraceElement[] stack;
	
	public Throwable getCause() {
		return cause;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public StackTraceElement[] getStackTrace() {
		return stack;
	}
	
	public ProcessException(String msg,Throwable cause,StackTraceElement[] stack) {
		this.msg = msg;
		this.cause = cause;
	}

}

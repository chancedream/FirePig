package my.framework.util;

public class NestedException extends Exception {
	
	private static final long serialVersionUID = 3485623664207541859L;

	public NestedException(String message) {
		super(message);
	}
	
	public NestedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	@Override
	public String getMessage() {
		String message = super.getMessage();
		Throwable cause = getCause();
		if (cause != null) {
			StringBuffer buf = new StringBuffer();
			if (message != null) {
				buf.append(message).append("; ");
			}
			buf.append("nested exception is ").append(cause);
			return buf.toString();
		}
		else {
			return message;
		}
	}

}

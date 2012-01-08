package my.framework;

public class HttpException extends RuntimeException {
	
	private static final long serialVersionUID = -2146321694025876275L;

	public HttpException(int statusCode) {
		super(String.valueOf(statusCode));
	}

}

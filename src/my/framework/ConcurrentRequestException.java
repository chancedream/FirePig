package my.framework;

public class ConcurrentRequestException extends RuntimeException {

	private static final long serialVersionUID = 4865421645454693488L;
	
	public ConcurrentRequestException(String lastUri, String uri) {
		super(String.format("Request to %s interrupted previous request to %s", uri, lastUri));
	}

}

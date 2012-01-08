package my.framework;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;

public class StringPostBody implements PostBody {
	
	private static final Logger logger = Logger.getLogger(StringPostBody.class);
	
	protected String content;
	private String contentType;
	private String charset;
	
	public StringPostBody(String content, String contentType, String charset) {
		this.content = content;
		this.contentType = contentType;
		this.charset = charset;
	}

	@Override
	public HttpGet createGetMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpPost createPostMethod() {
	    HttpPost method = new HttpPost();
		try {
			method.setEntity(new StringEntity(content, contentType, charset));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return method;
	}

}

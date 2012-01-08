package my.framework;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import my.framework.util.CancelledException;
import my.framework.util.Future;
import my.framework.util.FutureObject;
import my.framework.util.XString;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class Response {
	
	@SuppressWarnings("serial")
	private static final Map<String, String> FILE_EXTENSION = new HashMap<String, String>() {{
		put("image/jpeg", "jpg");
		put("image/gif", "gif");
		put("text/html", "html");
		put("text/plain", "txt");
		put("text/xml", "xml");		
	}};
	
	private static final Logger logger = Logger.getLogger(Response.class);
	
	private byte[] body;
	private String bodyString;
	private Future<InputStream> bodyAsStream;
	private String logFileName;
	private HttpResponse method;
	private URI uri;
	
	public Response(HttpResponse method, URI uri) {		
		this.method = method;		
		this.uri = uri;
		this.bodyAsStream = new FutureObject<InputStream>();		
	}
	
	public HttpResponse getResponse() {
	    return method;
	}
	
	public InputStream getBodyAsStream() throws CancelledException, InterruptedException {
		InputStream result = bodyAsStream.get();
		bodyAsStream.set(new ByteArrayInputStream(body));
		return result;
	}
	
	public String getBodyString() {
	    return bodyString;
	}
	
	public String getCharset() {
	    String charset = EntityUtils.getContentCharSet(method.getEntity());
		return charset == null ? "ISO-8859-1" : charset;
	}
	
	public String getContentType() {
		String contentType = method.getEntity().getContentType().getValue();
		return XString.from(contentType).split(";", 0).value();
	}
	
	public String getLogFileName() {
		return logFileName;
	}
	
	public int getStatusCode() {
		return method.getStatusLine().getStatusCode();
	}
	
	public URI getURI() {
		return uri;
	}
	
	public void setBodyString(String bodyString) {
	    this.bodyString = bodyString;
	}

	public void setBody(byte[] body) {
		this.body = body;
		this.bodyAsStream.set(new ByteArrayInputStream(body));
//		if (logger.isDebugEnabled() && ApplicationContext.getInstance().get(ApplicationContext.TEMP_FILE_DIR) != null) {
//			try {
//				Date now = new Date();
//				StringBuffer sb = new StringBuffer();
//				sb.append(ApplicationContext.getInstance().get(ApplicationContext.TEMP_FILE_DIR)).append(File.separator);
//				sb.append(new SimpleDateFormat("yyyyMMdd").format(now)).append(File.separator);
//				
//				File logDir = new File(sb.toString());
//				if (!logDir.exists()) logDir.mkdirs();
//				
//				sb.append(now.getTime());
//				sb.append(method.getURI().getEscapedPath().replaceAll("\\/", "_").replaceAll("\\-", "_")).append('.');
//				
//				String extension = FILE_EXTENSION.get(getContentType());
//				sb.append(extension == null ? "unknown" : extension);
//				
//				logFileName = sb.toString();
//				logger.debug("Log file: " + logFileName);
//				OutputStream out = new BufferedOutputStream(new FileOutputStream(logFileName));
//		    	FileCopyUtils.copy(new ByteArrayInputStream(body), out);
//			} catch (Exception e) {
//				logger.error("Error when log response", e);
//			}
//		}
	}

}

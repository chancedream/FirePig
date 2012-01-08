package my.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

public class FormPostBody implements PostBody {
	
	private class Part {		
				
		public String key, value;
		public int type;
		
		public Part(int type, String key, String value) {
			this.type = type;
			this.key = key;
			this.value = value;
		}
		
		public String getKey() {
		    return key;
		}
		
		public Object getObject() throws UnsupportedEncodingException {
			switch (type) {
			case STRING_PART: return new StringBody(value, Charset.forName(charset));
			case FILE_PART: {
				    String contentType = "text/plain";
				    if (value == null) {
				        return null;
				    }
				    value = value.toLowerCase();
				    if (value.endsWith("jpg") || value.endsWith("jpeg")) {
				        contentType = "image/jpeg";
				    }
				    else if (value.endsWith("gif")) {
				        contentType = "image/gif";
				    }
				    else if (value.endsWith("txt")) {
				        contentType = "text/plain";
				    }
					return new FileBody(new File(value), contentType, charset);
			}
			case NAME_VALUE_PAIR: return new BasicNameValuePair(key, value);
			}
			return null;
		}
		
	}	
	
	private static final int FILE_PART = 1;
	private static final Logger logger = Logger.getLogger(FormPostBody.class);
	private static final int NAME_VALUE_PAIR = 2;
	private static final String  BOUNDARY = "---------------------------16336183371278";
	
	private static final int STRING_PART = 0;
	private String charset = "ISO-8859-1";
	private Map<String, Object> context;	
	private boolean multipart = false;
	private String referer;
	
	private List<Part> parts = new ArrayList<Part>();
	
	public void addFile(String name, String filename) throws FileNotFoundException {
		assert multipart;
		parts.add(new Part(FILE_PART, name, filename));
	}
	
	public void addParam(String name, String value) {
		if (value == null) return;
		if (multipart) {
			parts.add(new Part(STRING_PART, name, value));
		} else {
			try {
				name = new String(name.getBytes(charset), "ISO-8859-1");			
				value = new String(value.getBytes(charset), "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {}
			parts.add(new Part(NAME_VALUE_PAIR, name, value));
		}
	}
	
	public HttpGet createGetMethod() {
		assert !multipart;
		HttpGet method = new HttpGet();
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    URI uri;
        try {
            for (int i = 0; i < parts.size(); i++) {
                Part part = parts.get(i);           
                params.add((BasicNameValuePair)part.getObject());
            }       
            uri = URIUtils.createURI("http", "", -1, "", 
                    URLEncodedUtils.format(params, "UTF-8"), null);
            method.setURI(uri);
            parts.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	    return method;
	}
	
	public HttpPost createPostMethod() {
		HttpPost method = null;
        try {
            method = _createPostMethod();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		parts.clear();
		return method;		
	}
	
//	public String getContent() {
//		updateParts();
//		HttpPost method = _createPostMethod();
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		try {
//			method.getRequestEntity().writeRequest(out);
//			out.close();
//			return out.toString(charset);
//		} catch (IOException e) {
//			logger.error("", e);
//			return null;
//		}		
//	}
//	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public void setContext(Map<String, Object> context) {
		this.context = context;
	}
	
	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}
	
	public void setReferer(String referer) {
	    this.referer = referer;
	}
	
	private HttpPost _createPostMethod() throws UnsupportedEncodingException {
	    HttpPost method = new HttpPost();
		if (multipart) {
		    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY, null);
			for (int i = 0; i < parts.size(); i++) {
			    Part part = parts.get(i);
			    if (part != null) {
			        entity.addPart(part.getKey(), (ContentBody)part.getObject());
			    }
			}
			method.setEntity(entity);
		} else {
		    List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Iterator<Part> it = parts.iterator(); it.hasNext(); ) {
				Part part = it.next();
				NameValuePair param = (NameValuePair)part.getObject();
				params.add(param);
			}
			method.setEntity(new UrlEncodedFormEntity(params, charset));
		}
		if (referer != null)
		    method.setHeader(HttpHeaders.REFERER, referer);
		return method;
	}
	
	private String valueOf(Object object) {
		return object == null ? null : object.toString();
	}

}

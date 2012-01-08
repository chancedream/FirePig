package my.framework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import my.framework.html.Document;
import my.framework.html.Element;
import my.framework.util.CancelledException;
import my.framework.util.ContentTypeInputStream;
import my.framework.util.Future;
import my.framework.util.FutureIterator;
import my.framework.util.FutureList;
import my.framework.util.FutureObject;
import my.framework.util.Updator;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.util.EncodingMap;
import org.springframework.util.FileCopyUtils;
import org.xml.sax.InputSource;

public class Window {
	
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1"; 
    
	private static final Logger logger = Logger.getLogger(Window.class);
	
	private static Executor defaultExecutor = Executors.newCachedThreadPool();
	
	private static Map<String, FrequencyLimitedResource> limitedResources = new HashMap<String, FrequencyLimitedResource>();
	
	static {		
//		Properties properties = new Properties();
//		try {
//			properties.load(Window.class.getResourceAsStream("/window.properties"));
//			for (Map.Entry<Object, Object> property : properties.entrySet()) {
//				String key = (String)property.getKey();
//				String[] values = ((String)property.getValue()).split(",");
//				FrequencyLimitedResource resource;
//				if (values.length >= 2) {
//					resource = new FrequencyLimitedResource(Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim()));
//				} else {
//					resource = new FrequencyLimitedResource(Integer.parseInt(values[0].trim()));
//				}				
//				limitedResources.put(key, resource);
//			}
//		} catch (IOException e) {
//			logger.warn("Could not find window.properties in classpath", e);
//		}
	}

	protected Executor executor;
	
	private Client client;	
	private Updator<FutureList<Response>, Document> document;
	private String requestUri;
	private FutureList<Response> responses;
	private Semaphore semaphore = new Semaphore(1);
	private Updator<FutureList<Response>, org.w3c.dom.Document> xmlDocument;
		
	public Window(Client client) {		
		this.client = client;
		this.responses = new FutureList<Response>(true);		
		this.document = new Updator<FutureList<Response>, Document>(responses, new Updator.Runner<FutureList<Response>, Document>() {
			@Override
			public Document run() {				
				try {					
					Response response = source.getLast();
					if (response.getStatusCode() != HttpStatus.SC_OK) {
						throw new HttpException(response.getStatusCode());
					}
					String charset = response.getCharset();
					DOMParser parser = ParserFactory.getParser(response.getContentType(), charset);
					Document document;
					synchronized (parser) {
						parser.parse(new InputSource(response.getBodyAsStream()));
						document = new Document(Window.this, response.getURI(), parser.getDocument());
						document.setLogFileName(response.getLogFileName());
					}
					//baidu login page's http-equiv is 'content-type'
			        Element contentTypeMeta = document.first("/html/head/meta[@http-equiv='Content-Type' or @http-equiv='content-type']");
			        if (contentTypeMeta != null) {
			        	String contentType = contentTypeMeta.getAttribute("content");
			        	Matcher matcher = Pattern.compile("charset=(.*)").matcher(contentType);
			        	if (matcher.find()) charset = matcher.group(1);
			        	document.setCharset(EncodingMap.getIANA2JavaMapping(charset.toUpperCase()));
			        } else {			        
			        	document.setCharset(charset);
			        }
			        return document;
				} catch (HttpException e) {
					throw e;
				} catch (Throwable e) {
					throw new RuntimeException("Error when parsing document", e);
				}
			}	
		});
		this.xmlDocument = new Updator<FutureList<Response>, org.w3c.dom.Document>(responses, new Updator.Runner<FutureList<Response>, org.w3c.dom.Document>() {
			@Override
			public org.w3c.dom.Document run() {
				try {
					Response response = source.getLast();
					if (response.getStatusCode() != HttpStatus.SC_OK) {
						throw new HttpException(response.getStatusCode());
					}
					DOMParser parser = ParserFactory.getParser(response.getContentType(), response.getCharset());
					synchronized (parser) {
						parser.parse(new InputSource(response.getBodyAsStream()));
						return parser.getDocument();
					}
				} catch (Throwable e) {
					throw new RuntimeException("Error when parsing document", e);
				}
			}
		});
	}
	
	public boolean awaitUri(String uriPattern) throws InterruptedException {
		for (FutureIterator<Response> it = responses.iterator(); it.hasNext(); ) {
			Response response = it.next();
				if (response.getURI().toString().matches(uriPattern)) return true;
		}
		return false;
	}
	
	public void clear() {
		clearResponses(true);
		document.clear();
	}
	
	public Window forAjax() {
		Window newWindow = new Window(getClient());
		try {
			newWindow.getResponses().clear();
			for (FutureIterator<Response> it = getResponses().iterator(); it.hasNext(); ) {
				newWindow.getResponses().add(it.next());
			}
		} catch (InterruptedException e) {
			logger.error("", e);
		} finally {
			newWindow.getResponses().done();
		}
		if (executor != null) newWindow.setExecutor(executor);
		return newWindow;		
	}
	
	public void get(final String uri) {
		get(uri, null);				
	}
	
    public void get(final String uri, final Object filter) {
        get(uri, null, filter);
    }
    
    public void get(final String uri, final Object filter, final boolean uriEscaped) {
        get(uri, null, filter, uriEscaped);
    }
    
    public void get(final String uri, final PostBody postBody, final Object filter) {
        get(uri, postBody, filter, false);
    }
    
    public void get(final String uri, final PostBody postBody, final Object filter, final boolean uriEscaped) {
		if (responses.isDone()) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		} else if (!semaphore.tryAcquire()) throw new ConcurrentRequestException(requestUri, uri);
		requestUri = uri;
        clearResponses(false);        
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
            	try {
            	    System.out.println(uri);
	            	HttpGet method = postBody == null ? new HttpGet() : postBody.createGetMethod();
	                try {
						method.setURI(new URI(uri));
					} catch (Exception e) {
						logger.error("Error when set URI", e);
						return;
					}
//					if (query != null) {
//	    				String oldQuery = method.getURI().getQuery();
//	    				if (oldQuery != null) {
//	    				    query = oldQuery + "&" + query;
//	    				}
//	    				method.setQueryString(query);
//					}
	                request(method, filter, true);
            	} finally {
            		responses.done();
            		semaphore.release();
            	}
            }
        });
	}
	
	public Client getClient() {
		return client;
	}
	
	public Document getDocument() {
		return document.get();
	}
	
	public Future<ContentTypeInputStream> getFile(final String uri) {		
		try {
			responses.awaitTermination();
		} catch (InterruptedException e) {
			// TODO
		}		
		final Future<ContentTypeInputStream> result = new FutureObject<ContentTypeInputStream>();
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
				    Response response = request(new HttpGet(uri), null, false);
					result.set(new ContentTypeInputStream(response.getBodyAsStream(), response.getContentType()));
				} catch (CancelledException e) {
					// TODO
				} catch (InterruptedException e) {
					// TODO
				}				
			}			
		});		
		return result;
	}
	
	public String getFileAsString(String uri) {
		return getFileAsString(uri, null);
	}
	
	public String getFileAsString(String uri, String charset) {
		try {
			responses.awaitTermination();
		} catch (InterruptedException e) {}		
		Response response = request(new HttpGet(uri), null, false);
		if (response.getStatusCode() != HttpStatus.SC_OK) {
			throw new HttpException(response.getStatusCode());
		}
		try {
			return FileCopyUtils.copyToString(new InputStreamReader(response.getBodyAsStream(), charset == null ? response.getCharset() : charset));
		} catch (Exception e) {
			throw new RuntimeException("Unable to copy response stream to string", e);
		}
	}
	
	public Response getResponse() {
		try {
			return responses.getLast();
		} catch (InterruptedException e) {
			// TODO
		}
		return null;
	}
	
    public String getResponseAsString() {
        return getResponseAsString(null);
    }
    
    public String getResponseAsString(String charset) {
		Response response = getResponse();
		try {
		    return FileCopyUtils.copyToString(new InputStreamReader(response.getBodyAsStream(), charset != null ? charset : response.getCharset()));
		} catch (Exception e) {
			logger.error("", e);			
		}
		return null;
	}
    
    public void consumeResponse() {
        try {
            EntityUtils.consume(getResponse().getResponse().getEntity());
        } catch (IOException e) {
            logger.error("", e);
        }
    }
	
	public FutureList<Response> getResponses() {
		return responses;
	}
	
	public org.w3c.dom.Document getXmlDocument() {
		return xmlDocument.get();
	}
	
	public void post(String uri, PostBody postBody) throws URISyntaxException {
        post(uri, postBody, null);
    }
	
	public void post(URI uri, PostBody postBody) {
		post(uri, postBody, null);
	}
	
	public void post(String uri, PostBody postBody, Object filter) throws URISyntaxException {
		post(new URI(uri), postBody, filter);
	}
	
	public void post(final URI uri, final PostBody postBody, final Object filter) {
		if (responses.isDone()) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		} else if (!semaphore.tryAcquire()) throw new ConcurrentRequestException(requestUri, uri.toString());
		requestUri = uri.toString();
		clearResponses(false);
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					HttpPost method = postBody.createPostMethod();
					method.setURI(uri);
					System.out.println(uri);
					request(method, filter, true);
				} finally {
					responses.done();
					semaphore.release();
				}
			}			
		});			
	}
	
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	
	private void clearResponses(boolean isDone) {
		while (!responses.isDone()) {			
			try {
				responses.awaitTermination();
			} catch (InterruptedException e) {
				logger.error("Interrupted while waiting last response termination", e);
				return;
			}
		}
		responses.clear(isDone);
	}
	
	private void filterHttpMethod(HttpUriRequest method) {
		method.setHeader("Accept-Language", "zh-cn");
		method.setHeader("Accept-Encoding", "gzip, deflate");
	    method.setHeader(HttpHeaders.USER_AGENT, USER_AGENT);
	}
	
	private Executor getExecutor() {
		return executor == null ? defaultExecutor : executor;
	}
	
	private Response request(HttpUriRequest method, Object filter, boolean storeResponse) {
	    HttpUriRequest nextMethod = null;
		while (method != null) {
			if (filter != null && filter instanceof RequestFilter) {
				method = ((RequestFilter)filter).filterRequest(method);
			}
			filterHttpMethod(method);					
			try {
			    HttpResponse httpResponse = null;
				int statusCode = 0;
				
				FrequencyLimitedResource resource = limitedResources.get(method.getURI().getHost());
				if (resource != null) {
					boolean needRelease = true;
					try {
						resource.acquire();
						logger.info(method.getMethod() + " " + method.getURI().toString());		
						httpResponse = getClient().execute(method);
						statusCode = httpResponse.getStatusLine().getStatusCode();
						if (statusCode == HttpStatus.SC_FORBIDDEN) {
							// blocked?
							needRelease = false;
							resource.releaseAndBlock();
						}
					} catch (SocketException e) {
						if ("Connection reset".equals(e.getMessage())) {
							needRelease = false;
							resource.releaseAndBlock();
							throw e;
						}
					} finally {
						if (needRelease) resource.release();
					}
				} else {
					logger.info(method.getMethod() + " " + method.getURI().toString());		
					httpResponse = getClient().execute(method);
					statusCode = httpResponse.getStatusLine().getStatusCode();
				}
				Response response = new Response(httpResponse, method.getURI());
				if (storeResponse) responses.add(response); // FIXME: unsafe
				
				InputStream in = httpResponse.getEntity().getContent();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				Header contentEncodingHeader = httpResponse.getEntity().getContentEncoding();
				if (contentEncodingHeader != null) {
					String contentEncoding = contentEncodingHeader.getValue();
					if ("gzip".equalsIgnoreCase(contentEncoding)) {
						in = new GZIPInputStream(in);
					} else if ("deflate".equalsIgnoreCase(contentEncoding)) {
						in = new DeflaterInputStream(in);
					} else if (!"none".equals(contentEncoding)) {
						logger.error("Unknown Content-Encoding: " + contentEncoding);
					}
				}
				FileCopyUtils.copy(in, out);
				
				if (filter != null && filter instanceof ResponseFilter) {
					String charset = response.getCharset();
					String body = ((ResponseFilter)filter).filterResponse(out.toString(charset));
					response.setBody(body.getBytes(charset));
				} else {					
					response.setBody(out.toByteArray());
					
				}				
								
				switch (statusCode) {
					case HttpStatus.SC_OK: {						
						return response;						
					}
					case HttpStatus.SC_MOVED_TEMPORARILY: {
				        Header locationHeader = httpResponse.getFirstHeader("location");
				        String location = locationHeader.getValue();
				        try {
				            URI uri = new URI(location);
				            if (uri.getHost() == null || uri.getHost().isEmpty()) {
				                uri = method.getURI();
				                uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), location, "", "");
				            }
				        	nextMethod = new HttpGet(uri.toString());
				        } catch (Exception e) {
				            URI uri = method.getURI();
				            uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), location, "", "");
				        	nextMethod = new HttpGet(uri.toString());
				        }
				        break;
					}
					default: {
						logger.warn("Unsupported response type: " + httpResponse.getStatusLine());
						return response;
					}
				}
		    } catch (Exception e) {
		    	logger.error("Request error", e);
		    	return null;
		    } finally {
		        method.abort();
		    }
		    method = nextMethod;
		}
		throw new RuntimeException("Internal error");
	}

}

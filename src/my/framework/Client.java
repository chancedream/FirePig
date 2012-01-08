package my.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;

import my.framework.util.LazyMap;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.log4j.Logger;

public class Client extends Window {
	
	private static final Logger logger = Logger.getLogger(Client.class);
			
	private DefaultHttpClient httpClient;
	private Map<String, Window> windows = new LazyMap<String, Window>(
		new LazyMap.Loader<String, Window>() {
			@Override
			public Window load(String key) {
				Window window = new Window(Client.this);
				if (executor != null) window.setExecutor(executor);
				return window;
			}			
		}
	);
	
	public Client(DefaultHttpClient httpClient) {
		super(null);		
		this.httpClient = httpClient;
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		httpClient.getParams().setBooleanParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);
	}
	
	@Override
	public Client getClient() {
		return this;
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}
	
	public Window getWindow(String name) {
		return windows.get(name);
	}
	
	public void loadSession(InputStream input) {
		try {
			ObjectInputStream is = new ObjectInputStream(input);
			BasicCookieStore cookieStore = new BasicCookieStore();
			cookieStore.addCookies((Cookie[])is.readObject());
			httpClient.setCookieStore(cookieStore);			
		} catch (IOException e) {
			logger.error("Unable to load session", e);
		} catch (ClassNotFoundException e) {
			logger.error("Unable to load session", e);
		}
	}
	
	public void saveSession(OutputStream output) {
		try {
			ObjectOutputStream os = new ObjectOutputStream(output);
			os.writeObject(httpClient.getCookieStore().getCookies());
		} catch (IOException e) {
			logger.error("Unable to save session", e);
		}
	}
	
	HttpResponse execute(HttpUriRequest method) throws HttpException, IOException {
		return httpClient.execute(method);		
	}
	
}

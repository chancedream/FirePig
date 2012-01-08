package my.framework;

import org.apache.http.client.methods.HttpUriRequest;

public interface RequestFilter {
	
    HttpUriRequest filterRequest(HttpUriRequest method);

}

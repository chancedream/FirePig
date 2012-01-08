package my.framework;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public interface PostBody {

	HttpGet createGetMethod();

	HttpPost createPostMethod();

}
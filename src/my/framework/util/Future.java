package my.framework.util;

public interface Future<V> extends java.util.concurrent.Future<V> {	
	
	V get() throws InterruptedException, CancelledException;
	
	void set(V object);

}

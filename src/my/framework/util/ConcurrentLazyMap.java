package my.framework.util;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentLazyMap<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = 4522137850417305831L;

	public static interface Loader<K,V> {		
		public V load(K key);		
	}

	private Loader<K,V> loader;	
	
	public ConcurrentLazyMap(Loader<K,V> loader) {
		super();
		this.loader = loader;
	}
	
	public ConcurrentLazyMap(int size, Loader<K,V> loader) {
		super(size);
		this.loader = loader;
	}
	
	@Override
	public V get(Object key) {
		return get(key, true);		
	}
	
	@SuppressWarnings("unchecked")
	public V get(Object key, boolean autoLoad) {
		V value = super.get(key);
		if (autoLoad && value==null) {
			K k = (K)key;
			value = loader.load(k);
			putIfAbsent(k, value);
		}
		return value;
	}

}

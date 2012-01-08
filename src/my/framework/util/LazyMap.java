package my.framework.util;

import java.util.HashMap;

public class LazyMap<K,V> extends HashMap<K,V> {
	
	private static final long serialVersionUID = -5096274778588835460L;

	public static interface Loader<K,V> {		
		public V load(K key);		
	}

	private Loader<K,V> loader;	
	
	public LazyMap(Loader<K,V> loader) {
		super();
		this.loader = loader;
	}
	
	public LazyMap(int size, Loader<K,V> loader) {
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
			put(k, value);
		}
		return value;
	}

}

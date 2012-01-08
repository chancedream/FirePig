package my.framework.util;

public class LRUCache<K, V> implements Cache<K, V> {
	
	private LazyMap<K, CacheNode<K, V>> map;
	private int capacity, size;
	private int hits, visits;
	private CacheNode<K, V> head;
	
	public LRUCache(int capacity, LazyMap.Loader loader) {
		assert capacity >= 2;
		this.map = new LazyMap<K, CacheNode<K, V>>(size, new LoaderWrapper(loader));
		this.capacity = capacity;		
	}

	public synchronized V get(K key) {
		hits++; visits++;
		CacheNode<K, V> node = map.get(key);
		visitNode(node);
		return node.value;
	}
	
	private void visitNode(CacheNode<K, V> node) {
		if (node.prev == null && node.next == null) {
			// new node
			if (head == null) {
				// first node
				head = node;
				head.prev = head;
				head.next = head;
				size = 1;
			} else {				
				if (size == capacity) {
					// cache full
					CacheNode<K, V> tail = head.prev;
					replaceNode(node, tail);
					head = node;
					map.remove(tail.key);
				} else {
					// cache not full
					insertNode(node, head);
					head = node;					
				}
			}
		} else {
			// old node
			removeNode(node);
			insertNode(node, head);
		}
	}
	
	private void insertNode(CacheNode<K, V> node, CacheNode<K, V> target) {
		target.prev.next = node;
		node.next = target;
		node.prev = target.prev;		
		target.prev = node;		
		size++;
	}
	
	private void removeNode(CacheNode<K, V> node) {
		node.prev.next = node.next;
		node.next.prev = node.prev;
		size--;
	}	
	
	private void replaceNode(CacheNode<K, V> node, CacheNode<K, V> target) {
		target.prev.next = node;
		target.next.prev = node;
	}
	
	private class LoaderWrapper implements LazyMap.Loader {

		private LazyMap.Loader loader;
		
		public LoaderWrapper(LazyMap.Loader loader) {
			this.loader = loader;
		}
		
		@SuppressWarnings("unchecked")
		public Object load(Object key) {
			hits--;
			return new CacheNode<K, V>((K)key, (V)loader.load(key));			
		}
		
	}
	
	private static class CacheNode<K, V> {		
		
		public K key;
		public V value;
		public CacheNode<K, V> prev, next;
		
		public CacheNode(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
	}
		
}

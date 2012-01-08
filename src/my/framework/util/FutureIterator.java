package my.framework.util;

public class FutureIterator<T> {
	
	private FutureList<T> list;
	private int index;
	
	public FutureIterator(FutureList<T> list) {
		this.list = list;
		this.index = 0;
	}

	public boolean hasNext() throws InterruptedException {
		synchronized (list) {
			while (index >= list.size()) {
				if (list.isDone()) return false;
				list.wait();
			}
			return true;			
		}
	}

	public T next() throws InterruptedException {		
		return list.get(index++);
	}

}

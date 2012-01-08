package my.framework.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureObject<V> implements Future<V> {
	
	private V object;
	private boolean isDone = false;
	private boolean isCancelled = false;
	
	@Override
	public synchronized void set(V object) {
		this.object = object;
		this.isDone = true;
		notifyAll();
	}

	@Override
	public synchronized boolean cancel(boolean mayInterruptIfRunning) {
		if (isDone) return false;
		isCancelled = true;
		notifyAll();
		return true;
	}

	@Override
	public synchronized V get() throws InterruptedException, CancelledException {
		while (true) {
			if (isDone) return object;
			if (isCancelled) throw new CancelledException();
			wait();
		}
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		// TODO Auto-generated method stub		
		return null;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public boolean isDone() {
		return isDone;
	}

}

package my.framework.util;

import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

public class ThreadBinder extends Thread {
	
	private static Logger logger = Logger.getLogger(ThreadBinder.class);
	
	private static ThreadLocal<ThreadBinder> threadLocal = new ThreadLocal<ThreadBinder>();
	
	private boolean isDone = false;
	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		
	public static ThreadBinder get() {
		return threadLocal.get();
	}
	
	public static void set(ThreadBinder thread) {
		threadLocal.set(thread);
	}
	
	public Object bind(Object object) {
		Enhancer enhancer = new Enhancer();
		Class clazz = object.getClass();
		while (Enhancer.isEnhanced(clazz)) clazz = clazz.getSuperclass();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new MethodInterceptorImpl(object));		
		return enhancer.create();
	}
	
	public void done() {
		isDone = true;
		interrupt();
	}
	
	public void execute(final Runnable runnable) {
		final Future<Object> result = new FutureObject<Object>();
		try {
			queue.put(new Runnable() {
				@Override
				public void run() {
					try {
						runnable.run();						
					} catch (Exception e) {
						logger.error("", e);
					} finally {
						result.set(null);
					}
				}				
			});
			result.get();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public <T> T execute(final Callable<T> callable) {
		final Future<T> result = new FutureObject<T>();
		try {
			queue.put(new Runnable() {
				@Override
				public void run() {
					T value = null;
					try {
						value = callable.call();
					} catch (Exception e) {
						logger.error("", e);
					} finally {
						result.set(value);
					}
				}				
			});
			return result.get();
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	public void executeTransactional(final Runnable runnable) {
		execute(new Runnable() {
			@Override
			public void run() {
				transactional(runnable);
			}			
		});
	}
	
	public <T> T executeTransactional(final Callable<T> callable) {
		return execute(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return transactional(callable);
			}			
		});
	}
	
	@Transactional
	private void transactional(Runnable runnable) {
		runnable.run();
	}
	
	@Transactional
	private <T> T transactional(Callable<T> callable) throws Exception {
		return callable.call();
	}
	
	@Override
	public void run() {		
		while (!isDone) {
			try {
				queue.take().run();
			} catch (InterruptedException e) {}
		}
		queue = null;
	}
	
	public static class Invocation implements Runnable {

		private MethodProxy proxy;
		private Object object;
		private Object[] args;
		private Future<Object> result;
		
		public Invocation(MethodProxy proxy, Object object, Object[] args, Future<Object> result) {
			this.proxy = proxy;
			this.object = object;
			this.args = args;
			this.result = result;
		}
		
		@Override
		public void run() {
			Object value = null;
			try {
				value = proxy.invoke(object, args);
			} catch (Throwable e) {
				logger.error("", e);
			} finally {
				result.set(value);
			}
		}
		
	}
	
	public class MethodInterceptorImpl implements MethodInterceptor {
		
		private Object target;
		
		public MethodInterceptorImpl(Object target) {
			this.target = target;
		}

		@Override
		public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			// FIXME
			if ("finalize".equals(method.getName())) {
				method.setAccessible(true);
				return method.invoke(target, args);
			}
			
			Future<Object> result = new FutureObject<Object>();
			Invocation invocation = new Invocation(proxy, target, args, result);
			queue.put(invocation);
			return result.get();
		}
		
	}
	
}

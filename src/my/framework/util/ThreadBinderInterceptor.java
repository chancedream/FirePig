package my.framework.util;

import java.util.concurrent.Callable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

public class ThreadBinderInterceptor implements MethodInterceptor {
	
	private static final Logger logger = Logger.getLogger(ThreadBinderInterceptor.class);

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {		
		ThreadBinder thread = ThreadBinder.get();
		if (thread == null) {
			logger.warn("No ThreadBinder available in thread [" + Thread.currentThread().getName() + "]");
			return invocation.proceed();
		}
		return thread.execute(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				try {
					return invocation.proceed();
				} catch (Throwable e) {
					// FIXME: ...
					throw (Exception)e;
				}
			}
		});
	}

}

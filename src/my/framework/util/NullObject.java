package my.framework.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class NullObject {
	
	private static Logger logger = Logger.getLogger(NullObject.class);
	
	private static Map<Class<?>, Object> info = new HashMap<Class<?>, Object> ();
	
	static {
		register(Boolean.TYPE, false);
		register(Integer.TYPE, 0);
		register(Float.TYPE, 0f);
		register(String.class);
		register(Iterator.class, new Iterator<Object>() {
			@Override public boolean hasNext() { return false; }
			@Override public Object next() { return null; }
			@Override public void remove() {}
		});
		register(List.class, Collections.EMPTY_LIST);		
	}
	
	public static void register(Class<?> clazz) {
		info.put(clazz, clazz);
	}	
	
	public static <T> void register(Class<T> clazz, T defaultValue) {
		info.put(clazz, defaultValue);
	}
	
	private static class SafeInvocationHandler implements InvocationHandler {

		@SuppressWarnings("unchecked")
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Class<?> returnType = method.getReturnType();
			Object result = info.get(returnType);
			if (result == null) {
				if (returnType.getAnnotation(SafeObject.class) != null) {
					result = method.getReturnType();
					info.put(returnType, result);
				}
			}			
			if (result != null) {
				if (!(result instanceof Class)) return result;
				return newInstance((Class)result);
			}
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<? extends T> clazz) {		
		if (clazz.isInterface()) {
			return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new SafeInvocationHandler());			
		}
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			logger.error("Failed to create NullObject of class: " + clazz, e);
		}
		return null;
	}

}

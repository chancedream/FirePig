package my.framework.util;

import java.util.LinkedHashMap;
import java.util.Map;

import sun.org.mozilla.javascript.internal.ScriptableObject;
import sun.org.mozilla.javascript.internal.Undefined;
import sun.org.mozilla.javascript.internal.UniqueTag;
import sun.org.mozilla.javascript.internal.Wrapper;

public class JavascriptUtil {
	
	public static Object convertToJavaObject(Object jsObj) {
		 if (jsObj == null || jsObj == Undefined.instance || jsObj == ScriptableObject.NOT_FOUND) return null;
		 if (jsObj instanceof Wrapper) return ((Wrapper)jsObj).unwrap();
		 if (jsObj instanceof ScriptableObject) {             
             return scriptableToJavaObject((ScriptableObject)jsObj);
         }
         return jsObj;
	}
	
	private static Object scriptableToJavaObject(ScriptableObject jsObj) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		for (Object id : jsObj.getIds()) {
			String key = id.toString();
			Object value = jsObj.get(key, jsObj);
			if (value instanceof UniqueTag) {
				value = jsObj.get(Integer.parseInt(key), jsObj);
			}
			result.put(key, convertToJavaObject(value));
		}
		return result;
	}

}

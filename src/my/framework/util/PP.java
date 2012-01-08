package my.framework.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PP {
	
	private static final String NULL = "null";
	
	public static void pp(Object obj) {
		pp(obj, System.out);
	}
	
	public static void pp(Object obj, OutputStream out) {
		(new PP(out)).print(obj);		
	}
	
	private int indent = 0;
	private Stack<Object> objectStack = new Stack<Object>();
	private OutputStream out;
	private boolean allowNewLine = true;
		
	public PP(OutputStream out) {
		this.out = out;
	}
	
	public List<Field> getFields(Class clazz) {
		List<Field> result = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) continue;
			result.add(field);
		}		
		Collections.sort(result, new Comparator<Field>() {
			private Collator collator = Collator.getInstance();
			@Override
			public int compare(Field o1, Field o2) {
				return collator.compare(o1.getName(), o2.getName());
			}			
		});		
		return result;
	}
	
	public boolean inspect(Object obj) {
		Class clazz = obj.getClass();
		String name = clazz.getName();
		String output = name + '@' + Integer.toHexString(obj.hashCode()) + " ";
		if (name.startsWith("java.")) {
			text(output);
			return false;
		}
		for (Object o : objectStack) {
			if (o == obj) { 
				text(output + "(...)"); 
				return false;
			}
		}
		List<Field> fields = getFields(clazz);
		if (fields.size() > 0) {
			objectStack.push(obj);
			newLine();
			text(output); text("(");
			indent++;			
			for (Iterator<Field> it = fields.iterator(); it.hasNext(); ) {
				Field field = it.next();
				newLine(true);
				text(field.getName() + " = ");
				boolean flag = false;
				try {
					field.setAccessible(true);
					allowNewLine = false;
					flag = print(field.get(obj));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (it.hasNext()) {
					text(",");
					if (flag) newLine();
				}				
			}
			indent--;
			newLine(true);
			text(")");
			objectStack.pop();
			return true;
		} else {
			text(output);
			return false;
		}
	}
	
	public boolean list(List obj) {
		if (obj.size() == 0) {
			text("[]"); return false;
		}
		for (Object o : objectStack) {
			if (o == obj) { text("[...]"); return false; }
		}
		objectStack.push(obj);
		text("[");
		indent++;
		boolean multiLine = false;
		for (Iterator it = obj.iterator(); it.hasNext(); ) {
			allowNewLine = true;
			boolean flag = print(it.next());
			if (flag) multiLine = true;
			if (it.hasNext()) {
				text(", ");
				if (flag) newLine();				
			}			
		}
		indent--;
		if (multiLine) newLine(true);
		text("]");
		objectStack.pop();
		return multiLine;
	}
	
	public boolean map(Map obj) {
		if (obj.size() == 0) {
			text("{}"); return false;
		}
		for (Object o : objectStack) {
			if (o == obj) { text("{...}"); return false; }
		}
		objectStack.push(obj);
		text("{");
		indent++;
		for (Iterator it = obj.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry)it.next();
			newLine(true);
			print(entry.getKey());
			text(" => ");
			allowNewLine = false;
			print(entry.getValue());
			if (it.hasNext()) text(",");
		}
		indent--;
		newLine(true);
		text("}");
		objectStack.pop();
		return true;
	}
	
	public void newLine() {		
		newLine(false);
	}
	
	private void newLine(boolean force) {
		if (force || allowNewLine) {
			String s = "\n";
			for (int i = 0; i < indent; i++) s += "  ";
			text(s);
			return;
		}
		allowNewLine = true;
	}
	
	public boolean print(Object obj) {
		if (obj == null) { text(NULL); return false; }
		try {
			Method toStringMethod = obj.getClass().getMethod("toString", (Class[])null);
			if (obj.getClass() == String.class) {
				return text('"' + obj.toString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t") + '"');				
			}
			if (obj instanceof Object[]) {
				return list(Arrays.asList((Object[])obj));				
			}
			if (obj instanceof List) {				
				return list((List)obj);
			}
			if (obj instanceof Map) {
				return map((Map)obj);				
			}
			if (toStringMethod.getDeclaringClass() != Object.class) {
				return text(obj.toString());				
			}			
			return inspect(obj);
		} catch (Exception e) {}
		return false;
	}	
	
	public boolean text(String obj) {
		try {
			out.write(obj.getBytes());
		} catch (IOException e) {}
		return false;
	}
	
	public static void main(String[] args) {
		Map m = new HashMap();
		m.put("aaa", new Integer(1));
		m.put("bbb", "\\t\\r\\n\t\r\n");
		m.put("ccc", new Object[] {
			"ddd", "eee", new PP(System.out)
		});
		pp(m);
	}

}

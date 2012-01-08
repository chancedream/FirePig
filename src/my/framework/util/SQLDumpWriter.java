package my.framework.util;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SQLDumpWriter extends Writer {
	
	public static interface Serializer<T> {
		void serialize(T e, Map<String, Object> result);
	}
	
	private Writer out;
	
	public SQLDumpWriter(Writer out) throws IOException {
		this.out = out;
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		out.write(cbuf, off, len);
	}
	
	public <T> void write(String table, List<T> rows, Serializer<T> serializer) throws IOException {
		if (rows == null || rows.isEmpty()) return;		
		out.write("INSERT INTO `" + table + "` ");
		TreeMap<String, Object> result = new TreeMap<String, Object>();
		boolean first = true;
		for (Iterator<T> it = rows.iterator(); it.hasNext(); ) {
			serializer.serialize(it.next(), result);
			if (first) {
				first = false;
				out.write("(");
				for (Iterator<String> jt = result.navigableKeySet().iterator(); jt.hasNext(); ) {
					out.write("`" + jt.next() + "`");
					if (jt.hasNext()) out.write(",");
				}
				out.write(") VALUES ");
			}
			out.write("(");
			for (Iterator<Object> jt = result.values().iterator(); jt.hasNext(); ) {
				out.write(toSQLValue(jt.next()));
				if (jt.hasNext()) out.write(",");
			}
			out.write(")");
			if (it.hasNext()) out.write(",");
		}
	}
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String toSQLValue(Object o) {
		if (o == null) return "NULL";
		if (o instanceof String) return "'" + o.toString().replaceAll("'", "''") + "'";
		if (o instanceof Date) return "'" + df.format((Date)o) + "'";
		return o.toString();
	}

}

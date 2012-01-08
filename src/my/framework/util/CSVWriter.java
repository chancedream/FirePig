package my.framework.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class CSVWriter extends Writer {
	
	private int length;
	private Writer out;
	
	public CSVWriter(Writer out, String... header) throws IOException {
		this.out = out;
		if (header != null) {
			this.length = header.length;
			writeLine((Object[])header);
		}
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
	
	public void write(Object... data) throws IOException {		
		if (length == 0) {
			length = data.length;
		} else {
			if (data.length != length) throw new IllegalArgumentException("Unexpected data size. Expected: " + length + ", Actual: " + data.length);
		}
		writeLine(data);
	}
	
	private void writeLine(Object... row) throws IOException {
		for (int i = 0; i < row.length; i++) {
			if (i > 0) out.write(",");
			if (row[i] != null) out.write('"' + escape(row[i].toString()) + '"');
		}
		out.write("\r\n");
	}
	
	private String escape(String s) {
		return s == null ? "" : s.replaceAll("\"", "\"\"");
	}
	
	public static void main(String[] args) throws Exception {
		CSVWriter out = new CSVWriter(new OutputStreamWriter(System.out), "a", "b", "c");
		out.write("a1", "b1", "c1");
		out.write("\"", ",", "\r\n");
		out.write(1, 2, 3);
		out.flush();
	}

}

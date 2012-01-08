package my.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVReader extends Reader {
	
	private BufferedReader in;
	private Matcher mquote;
	private Pattern pattern;
	
	public CSVReader(Reader in) {
		this(in, ',');
	}
	
	public CSVReader(Reader in, char sep) {
		this.in = in instanceof BufferedReader ? (BufferedReader)in : new BufferedReader(in);
		this.pattern = Pattern.compile("\\G(?:^|" + sep + ")(?:\"([^\"]*+(?:\"\"[^\"]*+)*+)\"|([^\"" + sep + "]*+))");
		this.mquote = Pattern.compile("\"\"").matcher("");
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return in.read(cbuf, off, len);
	}
	
	public List<String> readLine() throws IOException {		
		List<String> result = new ArrayList<String>();
		String line = null;
		while (true) {
			line = (line == null) ? in.readLine() : line + "\r\n" + in.readLine();
			if (line == null) return null;
			Matcher matcher = pattern.matcher(line);
			int end = 0;
			while (matcher.find()) {
				String field;
				if (matcher.start(2) >= 0) {
					field = matcher.group(2);
				} else {
					field = mquote.reset(matcher.group(1)).replaceAll("\"");
				}
				end = matcher.end();
				result.add(field);				
			}
			line = line.substring(end);
			if (line.isEmpty()) {
				break;
			} else {
				result.remove(result.size()-1);				
			}
		}		
		return result;
	}

}

package my.framework;

import my.framework.util.LRUCache;
import my.framework.util.LazyMap;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.util.EncodingMap;
import org.cyberneko.html.HTMLConfiguration;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class ParserFactory {
    
	private static final Logger logger = Logger.getLogger(ParserFactory.class);
	
    static {
    	// HACK
    	EncodingMap.putIANA2JavaMapping("GB2312", "GBK");    	
    }
    
    private static LRUCache<String, DOMParser> cache = new LRUCache<String, DOMParser>(10, new LazyMap.Loader<String, DOMParser>() {
		@Override
		public DOMParser load(String key) {
			String[] s = key.split(";");
			String contentType = s[0];
			String charset = s[1];
			if ("text/html".equals(contentType)) {
				try {
					DOMParser parser = new DOMParser(new HTMLConfiguration());
			        parser.setFeature("http://xml.org/sax/features/namespaces", false);
			        parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", false);
			        parser.setProperty("http://cyberneko.org/html/properties/default-encoding", charset);
			        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower"); 
			        return parser;
				} catch (Exception e) {
					logger.fatal("Unable to create DOMParser", e);
				}
			} else if ("text/xml".equals(contentType)) {
				try {
					DOMParser parser = new DOMParser();
			        return parser;
				} catch (Exception e) {
					logger.fatal("Unable to create DOMParser", e);
				}
			} else {
				logger.error("Unsupported content type: " + contentType);
			}
			return null;
		}
    });
	
    public static DOMParser getParser(String contentType, String charset) throws SAXNotRecognizedException, SAXNotSupportedException {
    	synchronized (cache) {
    		return cache.get(contentType + ";" + charset);
    	}    	
    }
    
    private ParserFactory() {}
    
}

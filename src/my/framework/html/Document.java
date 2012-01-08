package my.framework.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import my.framework.Window;
import my.framework.util.NullObject;

import org.apache.http.client.utils.URIUtils;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.w3c.dom.Node;

public class Document {
	
	private static final Logger logger = Logger.getLogger(Document.class);
	
	//private static Transformer transformer;
	private static HTMLSerializer serializer;
	
	static {
//		TransformerFactory transFactory = TransformerFactory.newInstance();		
//        try {
//			transformer = transFactory.newTransformer();
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");			
//			transformer.setOutputProperty(OutputKeys.METHOD, "html");
//			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//		} catch (TransformerConfigurationException e) {
//			logger.fatal("Error when create transformer", e);
//		}        
        OutputFormat outputFormat = new OutputFormat("html","UTF-8",true);
        outputFormat.setOmitXMLDeclaration(true);
        outputFormat.setOmitDocumentType(true);
        outputFormat.setLineWidth(Integer.MAX_VALUE);
        serializer = new HTMLSerializer(outputFormat);
	}
	
	private String charset;
	private Map<Node, Element> elementPool = new HashMap<Node, Element>();
	private String logFileName;
	private Element root;	
	private URI uri;	
	
	private Window window;
	
	public Document(Window window, URI uri, org.w3c.dom.Document document) {
		this.window = window;
		this.uri = uri;
		this.root = new ElementSupport(this, document.getDocumentElement(), "document");		
	}
	
	public boolean contains(String xpath) {
	    return root.contains(xpath);
	}
	
	public Element first(String xpath) {
		return root.first(xpath);
	}
	
	public String getCharset() {
		return charset;
	}
	
	public Element getElementById(String id) {
	    return root.getElementById(id);
	}
	
	public Form getForm(String name) {
		return (Form)root.first("//form[@name='" + name + "']", Form.class);
	}

	public String getLogFileName() {
		return logFileName;
	}
	
	public Element getRoot() {
		return root;
	}
	
	public String getTitle() {
		return first("/html/head/title").getTextContent();
	}
	
	public URI getURI() {
		return uri;
	}
	
	public URI getURI(String relativePath) {
	    return URIUtils.resolve(uri, relativePath);
	}
	
	public Window getWindow() {
		return window;
	}
	
	public Iterator<Element> iterate(String xpath) {
		return root.iterate(xpath);
	}
	
	public List<Element> match(String xpath) {
		return root.match(xpath);
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	
	@Override
	public String toString() {
		return toString(root);
	}
	
	public String toString(Element element) {
        synchronized (serializer) {
    	    //因为html中不规范namespace的问题，暂时只能用HtmlSerializer来输出html
    	    ByteArrayOutputStream out = new ByteArrayOutputStream();
    	    serializer.setOutputByteStream(out);
    	    try {
    	        if (element.getNode() instanceof org.w3c.dom.Element) {
    	        		serializer.serialize((org.w3c.dom.Element)element.getNode());
    	        		return out.toString("UTF-8");
    	        }
    	        else {
    	            return element.getTextContent();
    	        }
            } catch (IOException e) {
                logger.error("Unable to transform xml node to string", e);
            }
        }
//		DOMSource source = new DOMSource();
//        source.setNode(element.getNode());
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        StreamResult result = new StreamResult();
//        result.setOutputStream(out);
//        try {
//        	transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//			transformer.transform(source, result);
//		} catch (TransformerException e) {
//			logger.error("Unable to transform xml node to string", e);
//		}		
//		try {
//			return out.toString("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			logger.error("Unsupported encoding: " + charset, e);
//		}
		return null;		
	}

	@SuppressWarnings("unchecked")
	<T extends Element> T createElement(Node node, Class<T> clazz, String path) {
		if (node == null) return NullObject.newInstance(clazz);
		if (elementPool.containsKey(node)) return (T)elementPool.get(node);		
		Element e = _createElement(node, path);
		elementPool.put(node, e);
		return (T)e;
	}

	private Element _createElement(Node node, String path) {
		String nodeName = node.getNodeName();
		if ("input".equalsIgnoreCase(nodeName)) {
		    String type = ((org.w3c.dom.Element)node).getAttribute("type");
		    if ("checkbox".equalsIgnoreCase(type)) {
		        return new CheckboxInputSupport(Document.this, node, path);
		    }
		    if ("radio".equalsIgnoreCase(type)) {
		        return new RadioInputSupport(Document.this, node, path);
		    }
		    if ("submit".equalsIgnoreCase(type)) {
		        return new SubmitInputSupport(Document.this, node, path);
		    }
			return new InputSupport(Document.this, node, path);
		}
		if ("select".equalsIgnoreCase(nodeName)) {
		    return new SelectSupport(Document.this, node, path);
		}
		if ("option".equalsIgnoreCase(nodeName)) {
		    return new OptionSupport(Document.this, node, path);
		}
		if ("textarea".equalsIgnoreCase(nodeName)) {
		    return new TextareaSupport(Document.this, node, path);
		}
		if ("form".equalsIgnoreCase(nodeName)) {
			return new FormSupport(Document.this, node, path);
		}
		if ("a".equalsIgnoreCase(nodeName)) {
		    return new LinkSupport(Document.this, node, path);
		}
		return new ElementSupport(Document.this, node, path);
	}
	
}

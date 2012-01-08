package my.framework.html;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import my.framework.ParserFactory;
import my.framework.util.Cache;
import my.framework.util.LRUCache;
import my.framework.util.LazyMap;
import my.framework.util.NullObject;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ElementSupport implements Element {
	
	private class NodeIterator implements Iterator<Node> {
		
		private boolean hasNext;
		private int index;
		private Node node;
		private NodeIterator subIterator;
		
		public NodeIterator(Node node) {
			this.node = node;
			this.index = -1;
			this.hasNext = true;			
		}

		public boolean hasNext() {
			return hasNext;			
		}

		public Node next() {			
			if (index == -1) {
				index++;
				hasNext = node.hasChildNodes();
				return node; 
			}			
			if (subIterator == null || !subIterator.hasNext())
				subIterator = new NodeIterator(node.getChildNodes().item(index++));
			Node result = subIterator.next();			
			if (index == node.getChildNodes().getLength()) hasNext = subIterator.hasNext();			
			return result;
		}

		public void remove() {
			throw new RuntimeException("Unsupported method \"remove\"");
		}
		
	}
	
	private static final XPathFactory factory = XPathFactory.newInstance();
	
	private static final Logger logger = Logger.getLogger(ElementSupport.class);
	private static final XPath xpath = factory.newXPath();
	private static final int xpathExpressionCacheSize = 1000;
	private static final Cache<String, XPathExpression> xpathExpression = 
		new LRUCache<String, XPathExpression>(
			xpathExpressionCacheSize,
			new LazyMap.Loader<String, XPathExpression>() {
				@Override
				public XPathExpression load(String key) {
					try {
						return xpath.compile(key);
					} catch (XPathExpressionException e) {
						logger.error("unable to compile xpath: " + key, e);
					}
					return null;
				}
			}
		);	
	
	protected Document document;
	
	protected Node node;
	
	protected String path;
	
	public ElementSupport() {
		this(null, null, null);
	}
	
	public ElementSupport(Document document, Node node, String path) {
		this.document = document;
		this.node = node;
		this.path = path;
	}
	
	@Override
	public boolean contains(String xpath) {
		XPathExpression expr = xpathExpression.get(xpath);
		try {
			Node firstNode = (Node)expr.evaluate(node, XPathConstants.NODE);
			if (firstNode != null) return true;
		} catch (XPathExpressionException e) {
			logger.error("Invalid XPath: " + xpath, e);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#disable()
	 */
	public void disable() {
	    setAttribute("disabled", "disabled");
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#enable()
	 */
	public void enable() {
	    removeAttribute("disabled");
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#first(java.lang.String)
	 */
	public Element first(String xpath) {
		return first(xpath, Element.class);
	}
	
	@Override
	public <T extends Element> T first(String xpath, Class<T> clazz) {
		XPathExpression expr = xpathExpression.get(xpath);
		try {
			Node firstNode = (Node)expr.evaluate(node, XPathConstants.NODE);
			String path = String.format("%s.first(\"%s\")", this.path, xpath);
			if (firstNode == null) {
				logger.warn(String.format("Element not found: %s, file: %s", path, document.getLogFileName()));
			}
			return createElement(firstNode, clazz, path);
		} catch (XPathExpressionException e) {
			logger.error("Invalid XPath: " + xpath, e);
		}
		return NullObject.newInstance(clazz);
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name) {		
		return ((org.w3c.dom.Element)node).getAttribute(name);		
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#getElementById(java.lang.String)
	 */
	public Element getElementById(String id) {
	    return first(String.format("//*[@id='%s']", id));
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#getInnerHtml()
	 */
	public String getInnerHtml() {
	    NodeList nodeList = node.getChildNodes();
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < nodeList.getLength(); i++) {
	        sb.append(new ElementSupport(document, nodeList.item(i), String.format("%s[%d]", path, i)));
	    }
	    return sb.toString();
	}
	
	public Node getNode() {
		return node;
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#getTextContent()
	 */
	public String getTextContent() {
		return node.getTextContent();//((org.w3c.dom.Element)node).getTextContent();
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#hasAttribute(java.lang.String)
	 */
	public boolean hasAttribute(String name) {
	    return ((org.w3c.dom.Element)node).hasAttribute(name);
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#isDisabled()
	 */
	public boolean isDisabled() {
	    return hasAttribute("disabled");
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#iterate(java.lang.String)
	 */
	public Iterator<Element> iterate(String xpath) {
		return match(xpath).iterator();
	}	
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#match(java.lang.String)
	 */
	public List<Element> match(String xpath) {
		XPathExpression expr = xpathExpression.get(xpath);
		try {
			NodeList nodes = (NodeList)expr.evaluate(node, XPathConstants.NODESET);
			List<Element> result = new ArrayList<Element>();
			for (int i = 0; i < nodes.getLength(); i++) {
				result.add(createElement(nodes.item(i), Element.class, String.format("%s.match(\"%s\")[%d]", path, xpath, i)));
			}
			return result;
		} catch (XPathExpressionException e) {
			logger.error("Invalid XPath: " + xpath, e);
		}
		return new ArrayList<Element>();
	}
	
	@Override
	public boolean notEmpty() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		((org.w3c.dom.Element)node).removeAttribute(name);
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String name, String value) {		
		((org.w3c.dom.Element)node).setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#setInnerHtml(java.lang.String)
	 */
	public void setInnerHtml(String innerHtml) {
	    //FIXME: not a good implementation, maybe there will be some problems, not sure
        for(Node child = node.getFirstChild(); child != null; child = node.getFirstChild()) {
            node.removeChild(child);
        }
	    try {
            DOMParser parser = ParserFactory.getParser("text/html", document.getCharset());
            parser.parse(new InputSource(new StringReader(innerHtml)));
            NodeList nodeList = parser.getDocument().getElementsByTagName("body").item(0).getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                node.appendChild(node.getOwnerDocument().importNode(nodeList.item(i), true));
            }
        } catch (Exception e) {
            logger.warn("Failed to parse inner html:'" + innerHtml + "'", e);            
            node.setTextContent(innerHtml);
        }
	}
	
	/* (non-Javadoc)
	 * @see my.framework.html.Element#setTextContent(java.lang.String)
	 */
	public void setTextContent(String textContent) {
	    ((org.w3c.dom.Element)node).setTextContent(textContent);
	}

	@Override
	public String toString() {
		return document.toString(this);
	}

	protected Iterator<Node> iterate() {
		return new NodeIterator(node);
	}

	@SuppressWarnings("unchecked")
	private <T extends Element> T createElement(Node node, Class<T> clazz, String path) {
		return document.createElement(node, clazz, path);
	}

}

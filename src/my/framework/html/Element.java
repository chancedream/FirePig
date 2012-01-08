package my.framework.html;

import java.util.Iterator;
import java.util.List;

import my.framework.util.SafeObject;

import org.w3c.dom.Node;

@SafeObject
public interface Element {
	
	boolean contains(String xpath);

	void disable();

	void enable();

	Element first(String xpath);
	
	<T extends Element> T first(String xpath, Class<T> clazz);

	String getAttribute(String name);

	Element getElementById(String id);

	String getInnerHtml();
	
	Node getNode();

	String getTextContent();

	boolean hasAttribute(String name);

	boolean isDisabled();

	Iterator<Element> iterate(String xpath);

	List<Element> match(String xpath);
	
	boolean notEmpty();

	void removeAttribute(String name);

	void setAttribute(String name, String value);

	void setInnerHtml(String innerHtml);

	void setTextContent(String textContent);

}
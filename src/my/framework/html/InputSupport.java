package my.framework.html;

import org.w3c.dom.Node;

public class InputSupport extends ElementSupport implements Input {

	public InputSupport(Document document, Node node, String path) {
		super(document, node, path);		
	}

	@Override
	public String getValue() {
		return getAttribute("value");
	}

	@Override
	public String[] getValues() {
		return new String[] { getValue() };
	}

	@Override
	public void setValue(String... value) {
		if (value != null && value.length > 1) {
		    throw new IllegalArgumentException("Input can has only one value");
		}
		if (value == null || value.length == 0) {
			removeAttribute("value");
		} else {
			setAttribute("value", value[0]);
		}
	}

}

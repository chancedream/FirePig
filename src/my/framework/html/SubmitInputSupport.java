package my.framework.html;

import org.w3c.dom.Node;

public class SubmitInputSupport extends InputSupport implements SubmitInput {
    
	public SubmitInputSupport(Document document, Node node, String path) {
        super(document, node, path);
    }

	@Override
    public void click() {
        Form form = first("ancestor::form", Form.class);
        form.submit(getAttribute("name"));
    }
}

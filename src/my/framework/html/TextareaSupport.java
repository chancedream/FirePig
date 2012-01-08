package my.framework.html;

import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Node;

public class TextareaSupport extends ElementSupport implements Textarea {

    public TextareaSupport(Document document, Node node, String path) {
        super(document, node, path);
    }

    @Override
    public String getValue() {
        return HtmlUtils.htmlUnescape(getInnerHtml());
    }

    @Override
    public String[] getValues() {
        return new String[] { getValue() };
    }

    @Override
    public void setValue(String... value) {
    	if (value != null && value.length != 1) {
            throw new IllegalArgumentException("Textarea can only set one value");
        }
        setInnerHtml((value == null || value[0] == null) ? "" : value[0]);
    }

}

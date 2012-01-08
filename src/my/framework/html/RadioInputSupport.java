package my.framework.html;

import java.util.Iterator;

import org.w3c.dom.Node;

public class RadioInputSupport extends InputSupport implements RadioInput {
    
	public RadioInputSupport(Document document, Node node, String path) {
        super(document, node, path);
    }

    public void setValue(String... value) {
        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                if (value[i] != null && value[i].equals(getAttribute("value"))) check();
            }
        }
    }
    
    public String getValue() {
        if (isChecked()) return getAttribute("value");
        return null;
    }
    
    public void check() {
        Element form = first("ancestor::form");
        Iterator<Element> iter = form.iterate(String.format("//input[@type='radio' and @name='%s']", getAttribute("name")));
        while (iter.hasNext()) {
            iter.next().removeAttribute("checked");
        }
        setAttribute("checked", "checked");
    }
    
    public boolean isChecked() {
        return hasAttribute("checked");
    }
    
}

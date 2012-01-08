package my.framework.html;

import org.w3c.dom.Node;

public class CheckboxInputSupport extends InputSupport implements CheckboxInput {
	
    public CheckboxInputSupport(Document document, Node node, String path) {
        super(document, node, path);
    }

    @Override
    public void setValue(String... value) {
        boolean checked = false;
        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                if (value[i].equals(getAttribute("value"))) {
                    check();
                    checked = true;
                    break;
                }
            }
        }
        if (!checked) uncheck();
    }
    
    @Override
    public String getValue() {
        if (isChecked()) return getAttribute("value");
        return null;
    }
    
    @Override
    public void check() {
        setAttribute("checked", "checked");
    }
    
    @Override
    public void uncheck() {
        removeAttribute("checked");
    }
    
    @Override
    public boolean isChecked() {
        return hasAttribute("checked");
    }
    
}

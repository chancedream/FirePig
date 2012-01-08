package my.framework.html;

import org.w3c.dom.Node;

public class OptionSupport extends ElementSupport implements Option{
    
	private Select select;
    
    public OptionSupport(Document document, Node node, String path) {
        super(document, node, path);
    }
    
    @Override
    public String getValue() {
        return getAttribute("value");
    }
    
    @Override
    public void setOwnerSelect(Select select) {
        this.select = select;
    }
    
    @Override
    public Select getOwnerSelect() {
        checkSelectExist();
        return select;
    }
    
    @Override
    public void select() {
        checkSelectExist();
        select.select(getValue());
    }
    
    @Override
    public void unselect() {
        checkSelectExist();
        select.unselect(getValue());
    }
    
    @Override
    public void setSelected() {
        setAttribute("selected", "selected");
    }
    
    @Override
    public void setUnselected() {
        removeAttribute("selected");
    }
    
    @Override
    public boolean isSelected() {
        return hasAttribute("selected");
    }
    
    private void checkSelectExist() {
        if (select != null)
            return;
        select = (Select)first("ancestor::select");
    }
    
}

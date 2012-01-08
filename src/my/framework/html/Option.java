package my.framework.html;

import my.framework.util.SafeObject;

@SafeObject
public interface Option extends Element {
    
	String getValue();
    
    void setOwnerSelect(Select select);
    
    Select getOwnerSelect();
    
    void select();
    
    void unselect();
    
    void setSelected();
    
    void setUnselected();
    
    boolean isSelected();
    
}

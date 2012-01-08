package my.framework.html;

import my.framework.util.SafeObject;

@SafeObject
public interface Select extends FormElement {
    
	boolean isMultiple();
    
    void select(String... value);
    
    void select(Integer... index);
    
    void unselect(String... value);
    
    void unselect(Integer... index);
    
    Option[] getOptions();

}

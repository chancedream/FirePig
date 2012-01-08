package my.framework.html;

import my.framework.util.SafeObject;

@SafeObject
public interface RadioInput extends Input {
	
    void check();
    
    boolean isChecked();
    
}

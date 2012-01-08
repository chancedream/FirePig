package my.framework.html;

import my.framework.util.SafeObject;

@SafeObject
public interface CheckboxInput extends Input {
    
	void check();

	void uncheck();
    
    boolean isChecked();
    
}

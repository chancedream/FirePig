package my.framework.html;

import my.framework.util.SafeObject;

@SafeObject
public interface FormElement extends Element {

	String getValue();
	
	String[] getValues();
	
	void setValue(String... value);

}

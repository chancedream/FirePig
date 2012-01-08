package my.framework.html;

import java.util.Map;

import my.framework.util.SafeObject;

@SafeObject
public interface Form extends Element {
	
	CheckboxInput getCheckboxInput(String name);
	
	FormElement getElement(String name);
	
	Input getInput(String name);
	
	String getParam(String name);
	
	RadioInput getRadioInput(String name);
	
	Select getSelect(String name);
	
	SubmitInput getSubmitInput(String name);
	
	Textarea getTextarea(String name);
	
	void setExtraParams(Map<String, String[]> extraParams);
	
	void setExtraFileParams(Map<String, String> extraFileParams);

	void setParam(String name, String... value); 	
	
	void submit();
	
	void submit(String submitButton);
	
	void setFilter(Object responseFilter);

}

package my.framework.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FormElements extends ElementSupport implements FormElement {
    
	private List<FormElement> elements;
    
    public FormElements(List<FormElement> elements) {
        this.elements = elements;
    }

    @Override
    public String getValue() {
        for(Iterator<FormElement> iter = elements.iterator(); iter.hasNext();) {
            String value = iter.next().getValue();
            if (value != null) return value;
        }
        return null;
    }

    @Override
    public String[] getValues() {
        List<String> values = new ArrayList<String>();
        for(Iterator<FormElement> iter = elements.iterator(); iter.hasNext();) {
            String value = iter.next().getValue();
            if (value != null) values.add(value);
        }
        return values.toArray(new String[0]);
    }

    @Override
    public void setValue(String... value) {
        for(Iterator<FormElement> iter = elements.iterator(); iter.hasNext();) {
            iter.next().setValue(value);
        }
    }
    
    @Override
    public void enable() {
        for (Element e : elements) {
            e.enable();
        }
    }
    
    @Override 
    public void disable() {
        for (Element e : elements) {
            e.disable();
        }
    }

}

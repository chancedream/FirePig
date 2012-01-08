package my.framework.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

public class SelectSupport extends ElementSupport implements Select {
    
	private Option[] options;
    
    public SelectSupport(Document document, Node node, String path) {
        super(document, node, path);
        List<Element> elements = match(".//option");
        options = new Option[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            options[i] = (Option)elements.get(i);
            options[i].setOwnerSelect(this);
        }
    }
    
    @Override
    public String getValue() {
        String[] values = getValues();
        if (!isMultiple() && values.length == 0 && options.length > 0) {
            return options[0].getValue();
        }
        return values.length == 0 ? null : values[0];
    }

    @Override
    public String[] getValues() {
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected())
                values.add(options[i].getValue());
        }
        return values.toArray(new String[0]);
    }

    @Override
    public void setValue(String... value) {
        select(value);
    }
    
    @Override
    public boolean isMultiple() {
        return hasAttribute("multiple");
    }
    
    @Override
    public void select(String... value) {
    	if (value == null || (!isMultiple() && value.length != 1)) {
            throw new IllegalArgumentException("value is null or value length is not 1 but the select is not multiple.");
        }
        Set<String> valueSet = new HashSet<String>(Arrays.asList(value));
        for (int i = 0; i < options.length; i++) {
            if (valueSet.contains(options[i].getValue())) {
                options[i].setSelected();
            }
            else if (!isMultiple()){
                options[i].setUnselected();
            }
        }
    }
    
    @Override
    public void select(Integer... index) {
    	if (index == null || (!isMultiple() && index.length != 1)) {
            throw new IllegalArgumentException("index is null or index length is not 1 but the select is not multiple.");
        }
    	Set<Integer> indexSet = new HashSet<Integer>(Arrays.asList(index));
        for (int i = 0; i < options.length; i++) {
            if (indexSet.contains(i)) {
                options[i].setSelected();
            }
            else if (!isMultiple()) {
                options[i].setUnselected();
            }
        }
    }
    
    @Override
    public void unselect(String... value) {
    	if (value == null || !isMultiple()) {
            throw new IllegalArgumentException("value is null or the select is not multiple.");
        }
        Set<String> valueSet = new HashSet<String>(Arrays.asList(value));
        for (int i = 0; i < options.length; i++) {
            if (valueSet.contains(options[i].getValue())) {
                options[i].setUnselected();
            }
        }
    }
    
    @Override
    public void unselect(Integer... index) {
    	if (index == null || !isMultiple()) {
            throw new IllegalArgumentException("index is null or the select is not multiple.");
        }
        for (int i = 0; i < index.length; i++) {
            assert index[i] < options.length && index[i] >= 0;
            options[index[i]].setUnselected();
        }
    }
    
    @Override
    public Option[] getOptions() {
        return options;
    }

}

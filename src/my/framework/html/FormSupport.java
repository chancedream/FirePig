package my.framework.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import my.framework.FormPostBody;
import my.framework.PostBody;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

public class FormSupport extends ElementSupport implements Form {
	
	private static final Logger logger = Logger.getLogger(FormSupport.class);
	private Map<String, String[]> extraParams;
	private Map<String, String> extraFileParams;
	private Object filter;
	
	public FormSupport(Document document, Node node, String path) {
		super(document, node, path);
	}
	
	@Override
	public CheckboxInput getCheckboxInput(String name) {
	    return first(String.format("//input[@name='%s' and @type='checkbox']", name), CheckboxInput.class);
	}
	
	@Override
	public FormElement getElement(String name) {
		List<Element> elements = match(String.format("//input[@name='%s'] | //select[@name='%s'] | //textarea[@name='%s']", name, name, name));
		if (elements == null || elements.isEmpty()) return null;
		if (elements.size() == 1) return (FormElement)elements.get(0);
		List<FormElement> formElements = new ArrayList<FormElement>();
		for(Iterator<Element> iter = elements.iterator(); iter.hasNext();) {
		    formElements.add((FormElement)iter.next());
		}
	    return new FormElements(formElements);
	}
	
	@Override
	public Input getInput(String name) {
		return first(String.format("//input[@name='%s']", name), Input.class);
	}
	
	@Override
	public String getParam(String name) {
		FormElement element = getElement(name);
		if (element == null) {
			logger.warn("No element named \"" + name + "\" in form");
			return null;
		}
		return element.getValue();
	}
	
	@Override
	public RadioInput getRadioInput(String name) {
	    return first(String.format("//input[@name='%s' and @type='radio']", name), RadioInput.class);
	}
	
	@Override
	public Select getSelect(String name) {
	    return first(String.format("//select[@name='%s']", name), Select.class);
	}
	
	@Override
	public SubmitInput getSubmitInput(String name) {
	    return first(String.format("//input[@name='%s' and @type='submit']", name), SubmitInput.class);
	}
	
	@Override
	public Textarea getTextarea(String name) {
	    return first(String.format("//textarea[@name='%s']", name), Textarea.class);
	}
	
	@Override
	public void setExtraParams(Map<String, String[]> extraParams) {
	    this.extraParams = extraParams;
	}

	@Override
	public void setExtraFileParams(Map<String, String> extraParams) {
	    this.extraFileParams = extraFileParams;
	}
	
	@Override
	public void setParam(String name, String... value) {
		FormElement element = getElement(name);
		if (element == null) {
			logger.warn("No element named \"" + name + "\" in form");
			return;
		}
		element.setValue(value);
	}
	
	@Override
	public void submit() {
	    submit(null);
	}
	
	@Override
	public void submit(String submitButton) {
		String action = getAttribute("action");
			if ("post".equalsIgnoreCase(getAttribute("method"))) {
				document.getWindow().post(document.getURI(action), getPostBody(submitButton), filter);	        
			} else {
				// TODO
		        //document.client.get action + '?' + post_body(submit_button)[0]
			    document.getWindow().get(document.getURI(action).toString(), getPostBody(submitButton), filter);
			}
	}
	
	private PostBody getPostBody(String submitButton) {
		FormPostBody postBody = new FormPostBody();		
		postBody.setCharset(document.getCharset());
		postBody.setMultipart("multipart/form-data".equalsIgnoreCase(getAttribute("enctype")));
		postBody.setReferer(document.getURI().toString());
		for (Iterator<Node> it = iterate(); it.hasNext(); ) {
			Node node = it.next();			
			if (node.getNodeType() != Node.ELEMENT_NODE) continue;			
			org.w3c.dom.Element element = (org.w3c.dom.Element)node;
			if (element.hasAttribute("declare") || element.hasAttribute("disabled") || element.getAttribute("name").isEmpty()) continue;
			String tagName = element.getTagName();
			if ("input".equals(tagName)) {
				String type = element.getAttribute("type");				
				if (type == null || type.isEmpty() || "hidden".equals(type) || "text".equals(type) || "password".equals(type)) {					
					postBody.addParam(element.getAttribute("name"), element.getAttribute("value"));
				} else if ("checkbox".equals(type)) {
					if (!element.hasAttribute("checked")) continue;
					String value = element.getAttribute("value");
					postBody.addParam(element.getAttribute("name"), (value == null) || value.isEmpty() ? "on" : value);
				} else if ("radio".equals(type)) {
					if (!element.hasAttribute("checked")) continue;
					postBody.addParam(element.getAttribute("name"), element.getAttribute("value"));
				} else if ("file".equals(type)) {
				    String filePath = element.getAttribute("value");
				    if (filePath == null || filePath.trim().isEmpty()) {
				        continue;
				    }
				    File file = new File(filePath);
				    if (!file.exists()) {
				        logger.warn("File not found: " + filePath);
				        continue;
				    }
					try {
						postBody.addFile(element.getAttribute("name"), filePath);
					} catch (FileNotFoundException e) {
						logger.error("File not found: " + filePath, e);
					}
				} else if ("submit".equals(type) && (submitButton == null || submitButton.equals(element.getAttribute("name")))) {
					postBody.addParam(element.getAttribute("name"), element.getAttribute("value"));
				}
			} else if ("select".equals(tagName)) {
				Select select = new SelectSupport(document, node, null); // FIXME
				String name = select.getAttribute("name");
				String[] values = select.getValues();
				for (int i = 0; i < values.length; i++) {
				    postBody.addParam(name, values[i]);
				}
			} else if ("textarea".equals(tagName)) {
				Textarea textarea = new TextareaSupport(document, node, null); // FIXME
				postBody.addParam(element.getAttribute("name"), textarea.getValue());
			}
		}
		if (extraParams != null) {
		    for (Iterator<Entry<String, String[]>> iter = extraParams.entrySet().iterator(); iter.hasNext();) {
		        Entry<String, String[]> entry = iter.next();
		        String key = entry.getKey();
		        String[] value = entry.getValue();
		        if (value == null) continue;
		        for(int i = 0; i < value.length; i++) {
		            if (value[i] != null) {
		                postBody.addParam(key, value[i]);
		            }
		        }
		    }
		}
		if (extraFileParams != null) {
            for (Iterator<Entry<String, String[]>> iter = extraParams.entrySet().iterator(); iter.hasNext();) {
                Entry<String, String[]> entry = iter.next();
                String key = entry.getKey();
                String[] value = entry.getValue();
                if (value == null) continue;
                for(int i = 0; i < value.length; i++) {
                    if (value[i] != null) {
                        try {
                            postBody.addFile(key, value[i]);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
		}
		return postBody;
	}

    public void setFilter(Object responseFilter) {
        this.filter = responseFilter;
    }

}

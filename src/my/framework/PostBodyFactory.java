package my.framework;

import java.util.Map;

public class PostBodyFactory {
	
	private Map<String, String> files;	
	private boolean multipart = false;
	private Map<String, String> params;

	public Object getObject() throws Exception {
		FormPostBody postBody = new FormPostBody();
		postBody.setCharset("UTF-8");
		postBody.setMultipart(multipart);
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				postBody.addParam(entry.getKey(), entry.getValue());
			}
		}
		if (files != null) {
			for (Map.Entry<String, String> entry : files.entrySet()) {
				postBody.addFile(entry.getKey(), entry.getValue());
			}
		}
		return postBody;
	}

	public void setFiles(Map<String, String> files) {
		this.files = files;
	}

	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

}

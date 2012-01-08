package my.framework.util;

import java.io.IOException;
import java.io.InputStream;

public class ContentTypeInputStream extends InputStream {
    private InputStream inputStream;
    private String contentType;
    
    public ContentTypeInputStream(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
    
    public String getContentType() {
        return contentType;
    }

}

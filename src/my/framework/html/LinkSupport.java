package my.framework.html;

import org.w3c.dom.Node;

public class LinkSupport extends ElementSupport implements Link {

    public LinkSupport(Document document, Node node, String path) {
        super(document, node, path);
    }
    
    @Override
    public void click() {
        document.getWindow().get(document.getURI(getHref()).toString());
    }

    @Override
    public String getHref() {
        return getAttribute("href");
    }

}

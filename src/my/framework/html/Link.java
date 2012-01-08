package my.framework.html;

import my.framework.util.SafeObject;

@SafeObject
public interface Link extends Element {
    public void click();
    public String getHref();
}

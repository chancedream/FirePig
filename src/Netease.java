import my.framework.Client;
import my.framework.Window;
import my.framework.html.Form;

import org.apache.http.impl.client.DefaultHttpClient;


public class Netease {
    private Client httpclient;
    private Window window;
    
    public static void main(String[] args) {
        Netease netease = new Netease();
        netease.login("", "");
    }

    public Netease() {
        httpclient = new Client(new DefaultHttpClient());
        window = httpclient.getWindow("etao");

    }
    
    /**
     * @param user
     * @param password
     */
    public void login(String user, String password) {
        window.get("http://quan.163.com/activity/index.do");
        Form form = window.getDocument().getForm("loginForm");
        form.getInput("username").setValue(user + "@163.com");
        form.getInput("username2").setValue(user);
        form.getInput("password").setValue(password);
        form.getInput("url").setValue("http://quan.163.com/activity/index.do?username=" + user);
        form.submit();
        window.consumeResponse();
    }
    
    public void getBusiness(String url) {
        
    }
}

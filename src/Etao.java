import java.io.IOException;
import java.util.Date;
import java.util.Random;

import my.framework.Client;
import my.framework.Window;
import my.framework.html.Form;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class Etao {
    private Random random;
    private Client httpclient;
    private Window window;
    
    public static void main(String args[]) throws Exception {
        Etao etao = new Etao();
        etao.login("", "");
        etao.lotteryPage();
        for (int i = 0; i < 10000; i++) {
            Thread.sleep(1000);
            etao.lottery(40 + 11 * i);
        }
    }

    public Etao() {
        httpclient = new Client(new DefaultHttpClient());
        window = httpclient.getWindow("etao");
        random = new Random();
    }
    
    public void login(String name, String password) throws IOException {
        window.get("https://login.taobao.com/member/login.jhtml?style=miniall&full_redirect=true&css_style=etao&default_long_login=1&from=etao&tpl_redirect_url=http%3A%2F%2Flogin.etao.com%2Floginmid.html%3Fredirect_url%3Dhttp%253A%252F%252Fwww.etao.com%252F");
        Form form = (Form)window.getDocument().getElementById("J_StaticForm");
        form.getInput("TPL_username").setValue(name);
        form.getInput("TPL_password").setValue(password);
        form.submit();
        EntityUtils.consume(window.getResponse().getResponse().getEntity());
        //System.out.println(window.getResponseAsString());
    }
    
    public void lotteryPage() throws IOException {
        window.get("http://www.etao.com/go/act/act-xmax.php");
        EntityUtils.consume(window.getResponse().getResponse().getEntity());
        //System.out.println(window.getResponseAsString());
    }
    
    public void lottery(int jsonpId) {
        window.get(String.format("http://activity.etao.com/lottery.php?sig=962360747b1adc929b499fc49c82001a&type=red&pid=10&t=%d&callback=jsonp%d", 
                new Date().getTime() + random.nextInt(1000), jsonpId));
        System.out.println(window.getResponseAsString());
    }
}

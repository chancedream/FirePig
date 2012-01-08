import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import my.framework.Client;
import my.framework.Window;
import my.framework.html.Document;
import my.framework.html.Form;
import my.framework.html.Link;

import org.apache.http.impl.client.DefaultHttpClient;

public class NewD2 {
    private static String defaultCharset = "GBK";
    private static int count, start = 725;
    private Client httpclient;
    private Window window;
    

    public static void main(String[] args) throws Exception {
        while (count < 642 * 11) {
            try {
            run();
            }
            catch(Exception e) {}
        }
    }
    
    public static void run() throws Exception{
        NewD2 d2 = new NewD2();
        Random random = new Random();
        //d2.login("love4ever", "passw0rd");
        d2.login("chancedream", "sssv13vt");
        Thread.sleep(2000);
        d2.get("http://bbs.91d2.cn/read-htm-tid-1089855.html");
        Thread.sleep(2000);
        for (int i = 15; i < 26; i++) {
            for (int[] array : d2.genNums()) {
                count++;
                if (count <= start) continue;
                start = count;
                System.out.println(count);
                String str = "";
                for (int j = 0; j < 5; j++) {
                    str += array[j] + ", ";
                }
                str += i;
                System.out.println(str);
                d2.postComment(str + "\r\n猪你圣诞快乐！");
                Thread.sleep(6000);
                d2.redirectAfterPost();
                Thread.sleep(5000);
                if (random.nextInt(50) == 0) {
                    Thread.sleep(5000 + random.nextInt(5000));
                }
                if (random.nextInt(400) == 0) {
                    Thread.sleep(30000 + random.nextInt(30000));
                }
            }
        }
    }

    public NewD2() {
        httpclient = new Client(new DefaultHttpClient());
        window = httpclient.getWindow("d2");
    }

    public void login(String name, String password) throws Exception {
        String loginUrl = "http://bbs.91d2.cn/login.php";
        window.get(loginUrl);
        Document document = window.getDocument();
        Thread.sleep(2000);
        Form form = document.getForm("login");
        form.getInput("pwuser").setValue(name);
        form.getInput("pwpwd").setValue(password);
        form.submit();
        // System.out.println(window.getResponseAsString(defaultCharset));
    }

    public void postComment(String comment) throws Exception {
        Document document = window.getDocument();
        Form form = document.getForm("FORM");
        form.getTextarea("atc_content").setValue(comment);
        Map<String, String[]> extraParams = new HashMap<String, String[]>();
        extraParams.put("atc_downrvrc1", new String[] { "0" });
        extraParams.put("atc_desc1", new String[] { "" });
        form.setExtraParams(extraParams);
        Map<String, String> extraFileParams = new HashMap<String, String>();
        extraFileParams.put("attachment_1", "");
        form.setExtraFileParams(extraFileParams);
        form.submit();
        System.out.println(window.getResponseAsString(defaultCharset));
    }

    public void redirectAfterPost() {
        Document document = window.getDocument();
        ((Link) document.first("//a[3]")).click();
    }

    public List<int[]> genNums() {
        List<int[]> list = new ArrayList<int[]>();
        int[] array = new int[5];
        getNum(array, 0, list);
        return list;
    }

    private void getNum(int[] array, int loop, List<int[]> list) {
        int last = loop == 0 ? 14 : array[loop - 1];
        if (loop == 5) {
            int[] result = new int[5];
            System.arraycopy(array, 0, result, 0, 5);
            list.add(result);
        } else {
            for (int i = last + 1; i < 26; i++) {
                array[loop] = i;
                getNum(array, loop + 1, list);
            }
        }
    }

    private void get(String url) {
        window.get(url);
    }

}

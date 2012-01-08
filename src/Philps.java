import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Philps {
    public static String MESSAGE = "message";
    private static Map<String, String> answer = new HashMap<String, String>();
    private static String getNumberUrl = "http://119.161.148.59:2000/Front.aspx/getNumber?times=";
    private static String getBeginUrl = "http://119.161.148.59:2000/Front.aspx/getBegin?times=";
    private static String submitUrl = "http://119.161.148.59:2000/Front.aspx/getTotal?Keys=%s&Values=%s&UserName=%s&Pone=%s&times=%d";
    private static String key = "";
    private static String value = "";
    private static HttpClient httpclient;
    private static Random random = new Random();

    public static void main(String[] args) throws Exception {
        answer.put("1", "C");
        answer.put("2", "A");
        answer.put("3", "B");
        answer.put("4", "C");
        answer.put("5", "B");
        answer.put("6", "C");
        answer.put("7", "A");
        answer.put("8", "A");
        answer.put("9", "C");
        httpclient = new DefaultHttpClient();

        for (int j = 0; j< 100; j++) {
            getNumber();
            Thread.sleep(20);
            long current = System.currentTimeMillis();
            getBegin();
            for (int i = 0; i < 4; i++) {
                Thread.sleep(1200 + random.nextInt(500));
                getNumber();
            }
            Thread.sleep(1000 + random.nextInt(500));
            while(System.currentTimeMillis() - current < 8500) {
                System.out.println(System.currentTimeMillis() - current);
                Thread.sleep(random.nextInt(10));
            }
            submit("name", "phone");
            Thread.sleep(10000 + random.nextInt(10000));
        }
    }

    public static void run() throws Exception {
        

    }
    
    private static void getNumber() throws Exception {
        int time = getTime();
        HttpGet httpget = new HttpGet(getNumberUrl + time);
        filterHttpMethod(httpget);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        System.out.println(content);
        JSONObject jObject = JSONObject.fromObject(content);
        String message = jObject.getString(MESSAGE);
        System.out.println(String.format("time=%d, number=%s, message=%s", time, jObject.getString("number"), message));
        key += message + "%2C";
        value += answer.get(message) + "%2C";
    }
    
    private static void getBegin() throws Exception {
        int time = getTime();
        System.out.println("begin=" + time);
        HttpGet httpget = new HttpGet(getBeginUrl + time);
        filterHttpMethod(httpget);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        System.out.println(content);
    }
    
    private static void submit(String name, String phone) throws Exception {
        int time = getTime();
        System.out.println("submit=" + time);
        HttpGet httpget = new HttpGet(String.format(submitUrl, key, value, URLEncoder.encode(name, "UTF-8"), phone, time));
        filterHttpMethod(httpget);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        System.out.println(content);
    }
    

    
    private static int getTime() {
        Calendar cal = Calendar.getInstance();
        Date v = new Date();
        return cal.get(Calendar.DATE) + cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE) + cal.get(Calendar.SECOND) + cal.get(Calendar.MILLISECOND);
    }
    
    private static void filterHttpMethod(HttpUriRequest method) {
        method.setHeader(
                HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1"
            );
        method.setHeader(HttpHeaders.REFERER, "http://119.161.148.59:2000/Front.aspx/kaochang");
    }
    
}

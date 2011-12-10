import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class OCJ {
    public static void main(String[] args) throws Exception {
        
    }
    
    private static void run(String login, String password) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://www.ocj.com.cn/login/Login.jsp");
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        Pattern pattern = Pattern.compile("uid=[0-9a-z]+&chkDate=.*\\d+");
        Matcher matcher = pattern.matcher(content);
        matcher.find();
        String loginUrl = "https://www.ocj.com.cn/login/LoginRst.jsp?" + URLEncoder.encode(matcher.group());
        System.out.println(loginUrl);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("returnUrl", ""));
        formparams.add(new BasicNameValuePair("loginid", login));
        formparams.add(new BasicNameValuePair("password", password));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        HttpPost httppost = new HttpPost(loginUrl);
        httppost.setEntity(formEntity);
        response = httpclient.execute(httppost);
        content = EntityUtils.toString(response.getEntity());
        httpget = new HttpGet("http://www.ocj.com.cn/event/2011/06/singit/singitRst.jsp");
        response = httpclient.execute(httpget);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}

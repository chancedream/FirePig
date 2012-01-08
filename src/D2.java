import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class D2 {
    private HttpClient httpclient;
    
    public static void main(String[] args) throws Exception {
        D2 d2 = new D2();
        d2.login("love4ever", "passw0rd");
        Thread.sleep(2000);
        HttpResponse lastResponse = d2.get("http://bbs.91d2.cn/read-htm-tid-1090820.html");
        Thread.sleep(2000);
        d2.postComment("", lastResponse);
    }
    
    public D2() {
        httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(
                HttpHeaders.USER_AGENT,
                "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1"
            );
    }
    
    public void login(String name, String password) throws Exception {
        String loginUrl = "http://bbs.91d2.cn/login.php";
        HttpGet httpget = new HttpGet(loginUrl);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entry = response.getEntity();
        String content = EntityUtils.toString(response.getEntity());
        System.out.println(content);
        Thread.sleep(2000);
        String postUrl = "http://bbs.91d2.cn/login.php?";
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("forward", ""));
        formparams.add(new BasicNameValuePair("jumpurl", "http://bbs.91d2.cn/index.php"));
        formparams.add(new BasicNameValuePair("step", "2"));
        formparams.add(new BasicNameValuePair("lgt", "0"));
        formparams.add(new BasicNameValuePair("pwuser", name));
        formparams.add(new BasicNameValuePair("pwpwd", password));
        formparams.add(new BasicNameValuePair("question", "0"));
        formparams.add(new BasicNameValuePair("customquest", ""));
        formparams.add(new BasicNameValuePair("answer", ""));
        formparams.add(new BasicNameValuePair("hideid", "0"));
        formparams.add(new BasicNameValuePair("cktime", "31536000"));
        formparams.add(new BasicNameValuePair("submit", "登 录"));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                formparams, "UTF-8");
        HttpPost httppost = new HttpPost(postUrl);
        httppost.setEntity(formEntity);
        response = httpclient.execute(httppost);
        entry = response.getEntity();
        content = EntityUtils.toString(response.getEntity());
        System.out.println(content);
    }
    
    public HttpResponse get(String url) throws Exception {
        HttpGet httpget = new HttpGet(url);
        return httpclient.execute(httpget);
    }
    
    public HttpResponse postComment(String comment, HttpResponse lastResponse) throws Exception {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        HttpEntity entry = lastResponse.getEntity();
        String content = EntityUtils.toString(lastResponse.getEntity());
        String title = find(content, "\\<input.*value=\"(.*)\"\\sname=\"atc_title\"", 1);
        System.out.println(title);
        formparams.add(new BasicNameValuePair("atc_title", title));
        formparams.add(new BasicNameValuePair("atc_usesign", "1"));
        formparams.add(new BasicNameValuePair("atc_convert", "1"));
        formparams.add(new BasicNameValuePair("atc_autourl", "1"));
        formparams.add(new BasicNameValuePair("atc_content", comment));
        formparams.add(new BasicNameValuePair("step", "2"));
        formparams.add(new BasicNameValuePair("action", "reply"));
        String fid = find(content, "\\<input.*name=\"fid\"\\svalue=\"(\\d+)\"", 1);
        System.out.println(fid);
        formparams.add(new BasicNameValuePair("fid", fid));
        String tid = find(content, "\\<input.*name=\"tid\"\\svalue=\"(\\d+)\"", 1);
        System.out.println(tid);
        formparams.add(new BasicNameValuePair("tid", tid));
        String verify = find(content, "\\<input.*value=\"([0-9a-z]+)\"\\sname=\"verify\"", 1);
        System.out.println(verify);
        formparams.add(new BasicNameValuePair("verify", verify));
        formparams.add(new BasicNameValuePair("Submit", " 提 交 "));
//        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
//                formparams, "UTF-8");
//        HttpPost httppost = new HttpPost("http://bbs.91d2.cn/post.php?");
//        httppost.setEntity(formEntity);
//        return httpclient.execute(httppost);
        return null;
    }
    
    private String find(String str, String regex, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return "";
    }
}

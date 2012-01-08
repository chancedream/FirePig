import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HaoQi extends Thread {
    private String username;
    private String password;
    private String size;
    private int count;

    public static void main(String[] args) throws Exception {
        new HaoQi("YMH不要脸", "831202", "M22", 1).start();
        new HaoQi("YMH不要脸_2", "831202", "L20", 2).start();
        new HaoQi("YMN不要脸_3", "831202", "M22", 1).start();
        new HaoQi("YMH不要脸_4", "831202", "L20", 0).start();
        new HaoQi("YMH不要脸_5", "831202", "M22", 1).start();

    }

    public HaoQi(String username, String password, String size, int count) {
        this.username = username;
        this.password = password;
        this.size = size;
        this.count = count;
    }

    @Override
    public void run() {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            String loginUrl = "http://www.huggieshappyclub.com/DryComfort/login.aspx?nocache=5949.665806822479";
            // System.out.println(loginUrl);
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("username", username));
            formparams.add(new BasicNameValuePair("password", password));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                    formparams, "UTF-8");
            HttpPost httppost = new HttpPost(loginUrl);
            httppost.setEntity(formEntity);
            JSONObject jObject = null;
            boolean loginSuccess = false;
            while (!loginSuccess) {
                HttpResponse response = httpclient.execute(httppost);
                String content = EntityUtils.toString(response.getEntity());
                System.out.println(content);
                JSONArray jArray = JSONArray.fromObject(content);
                jObject = jArray.getJSONObject(0);
                loginSuccess = jObject.getInt("success") == 1;
            }
            String postUrl = "http://www.huggieshappyclub.com/DryComfort/save_redeem.aspx?nocache=2476.351151454728";
            formparams = new ArrayList<NameValuePair>();
            // System.out.println(jObject.getString("address"));
            // System.out.println(jObject.getString("realname"));
            // System.out.println(jObject.getString("UserID"));
            // System.out.println(jObject.getString("mobile"));
            // System.out.println(jObject.getString("zipcode"));
            String name = jObject.getString("realname");
            formparams.add(new BasicNameValuePair("address", jObject
                    .getString("address")));
            formparams.add(new BasicNameValuePair("realname", jObject
                    .getString("realname")));
            formparams.add(new BasicNameValuePair("UserID", jObject
                    .getString("UserID")));
            formparams.add(new BasicNameValuePair("ProductID", "2"));
            formparams.add(new BasicNameValuePair("mobile", jObject
                    .getString("mobile")));
            formparams.add(new BasicNameValuePair("Diapersize", size));
            formparams.add(new BasicNameValuePair("amount", "1"));
            formparams.add(new BasicNameValuePair("zipcode", jObject
                    .getString("zipcode")));
            formEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost = new HttpPost(postUrl);
            httppost.setEntity(formEntity);
            boolean postSuccess = false;
            while (!postSuccess) {
                HttpResponse response = httpclient.execute(httppost);
                String content = EntityUtils.toString(response.getEntity());
                System.out.println(name + " 100 point:" + content);
                postSuccess = !content.contains("error");
                // postSuccess = true;
            }
            postUrl = "http://www.huggieshappyclub.com/DryComfort/save_redeem.aspx?nocache=327.2146611283533";
            formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("address", jObject
                    .getString("address")));
            formparams.add(new BasicNameValuePair("realname", jObject
                    .getString("realname")));
            formparams.add(new BasicNameValuePair("UserID", jObject
                    .getString("UserID")));
            formparams.add(new BasicNameValuePair("ProductID", "1"));
            formparams.add(new BasicNameValuePair("mobile", jObject
                    .getString("mobile")));
            formparams.add(new BasicNameValuePair("Diapersize", "M"));
            formparams.add(new BasicNameValuePair("amount", String
                    .valueOf(count)));
            formparams.add(new BasicNameValuePair("zipcode", jObject
                    .getString("zipcode")));
            formEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost = new HttpPost(postUrl);
            httppost.setEntity(formEntity);
            postSuccess = false;
            while (count > 0 && !postSuccess) {
                HttpResponse response = httpclient.execute(httppost);
                String content = EntityUtils.toString(response.getEntity());
                System.out.println(name + " 20 point:" + content);
                postSuccess = !content.contains("error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class Chance {
    public static void main(String[] args) {
        while (true) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://chancedream.net/chance/test.php");
                HttpResponse response = httpclient.execute(httpget);
                System.out.println("a");
            }
            catch (Exception e) {
                
            }
        }

    }
}

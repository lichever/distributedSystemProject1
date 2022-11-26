package quizMakeup;


import client1.Counter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

// a thread to send posts to the server
public class Poster implements Runnable {

  // one instance, reuse

  private LinkedBlockingQueue<Integer> q;
  private CountDownLatch endCountDown;
  private Counter successfulCounter;
  private Counter unsuccessfulCounter;
  private int totalPost;

  public static final String BASE_PATH = "http://localhost:8080/prime/";
  //  public static String BASE_PATH = "http://alb-ass2-620885190.us-west-2.elb.amazonaws.com/ass1Server_war";
//  public static String BASE_PATH = "http://54.201.250.4:8080/ass1Server_war/prime/";
  public static final int SUCCESS_CODE = 200;

  public Poster(LinkedBlockingQueue<Integer> q,
      CountDownLatch endCountDown, Counter successfulCounter, Counter failCounter, int numOfPost) {
    this.q = q;
    this.endCountDown = endCountDown;
    this.successfulCounter = successfulCounter;
    this.unsuccessfulCounter = failCounter;
    this.totalPost = numOfPost;
  }

  @Override
  public void run() {

    for (int i = 0; i < totalPost; i++) {
      Integer num = q.poll();
      HttpClient httpClient = HttpClients.createDefault();


      try {
        HttpGet httpGet = new HttpGet(BASE_PATH+num);
        HttpResponse response = httpClient.execute(httpGet);

        if (response.getStatusLine().getStatusCode() == SUCCESS_CODE) {
          successfulCounter.increase();
        } else {
          unsuccessfulCounter.increase();
        }

        endCountDown.countDown();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


}

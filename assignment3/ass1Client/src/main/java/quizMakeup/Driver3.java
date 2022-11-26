package quizMakeup;

import client1.Counter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpResponse;

public class Driver3 {

  private static final int TOTAL_REQUESTS = 100000;
  public static final String BASE_PATH = "http://54.201.250.4:8080/ass1Server_war/prime/";
  private static Random rand = new Random();
  private static final OkHttpClient httpClient = new OkHttpClient();

  public static void main(String[] args) throws InterruptedException {
    CountDownLatch completed = new CountDownLatch(TOTAL_REQUESTS);
    Counter primeCounter = new Counter();

    // Execute the method.
    Long start = System.currentTimeMillis();
    for (int i = 0; i < 200; i++) {

      // lambda runnable creation - interface only has a single method so lambda works fine
      Runnable thread = () -> {
        for (int j = 0; j < 500; j++) {
          int num = rand.nextInt(5000) * 2 + 1;
          Request request = new Request.Builder()
              .url(BASE_PATH + num)
              .build();

          try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200) {
              primeCounter.increase();
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
          completed.countDown();
        }
      };
      new Thread(thread).start();
    }

    completed.await();
    Long end = System.currentTimeMillis();
    long duration = end - start;
    System.out.println(
        "percentage of prime numbers: " + (double) primeCounter.getValue() / TOTAL_REQUESTS * 100
            + "%");
    System.out.println("total wall time used in millisecond: " + duration);
    System.out.println("mean response time in millisecond: " + (double) duration / TOTAL_REQUESTS);

  }

}

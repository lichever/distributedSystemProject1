package quizMakeup;

import client1.Counter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

public class Driver2 {

  private static final int TOTAL_REQUESTS = 100000;
//  public static final String BASE_PATH = "http://localhost:8080/prime/";
  public static final String BASE_PATH = "http://52.43.122.104:8080/ass1Server_war/prime/";
  private static Random rand = new Random();

  public static void main(String[] args) throws InterruptedException {
    CountDownLatch completed = new CountDownLatch(TOTAL_REQUESTS);
    Counter primeCounter = new Counter();

    // Execute the method.
    Long start = System.currentTimeMillis();
    for (int i = 0; i < 200; i++) {

      // lambda runnable creation - interface only has a single method so lambda works fine
      Runnable thread = () -> {
        HttpClient client = HttpClients.createDefault();

        for (int j = 0; j < 500; j++) {
          int num = rand.nextInt(5000) * 2 + 1;
          HttpGet get = new HttpGet(BASE_PATH + num);
          try {
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
              primeCounter.increase();
            }
          } catch (IOException e) {
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

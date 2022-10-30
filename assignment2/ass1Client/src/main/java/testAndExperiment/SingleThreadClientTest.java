package testAndExperiment;

import client1.Counter;
import client1.Generator;
import client1.MyLiftRide;
import client1.Poster;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleThreadClientTest {

  private static final int TEST_REQUEST = 1000;

  public static void main(String[] args) throws InterruptedException {

    LinkedBlockingQueue<MyLiftRide> q = new LinkedBlockingQueue<>();
    Generator generator = new Generator(q, TEST_REQUEST);
    Counter successfulCounter = new Counter();
    Counter unsuccessfulCounter = new Counter();
    CountDownLatch endCountDown = new CountDownLatch(TEST_REQUEST);

    long start = System.currentTimeMillis();
    Thread producer = new Thread(generator);
    producer.start();

    Poster testPoster = new Poster(q, endCountDown, successfulCounter, unsuccessfulCounter,
        TEST_REQUEST);
    Thread Consumer = new Thread(testPoster);
    Consumer.start();
    Consumer.join();

    long end = System.currentTimeMillis();
    long duration = end - start;
    int numSuccess = successfulCounter.getValue();
    int numUnsuccess = unsuccessfulCounter.getValue();

    System.out.println("number of successful requests sent: " + numSuccess);
    System.out.println("number of unsuccessful requests: " + numUnsuccess);
    System.out.println("total runtime used in millisecond: " + duration);
    System.out.println(
        "avg response time of requests in millisecond: " + duration / (numSuccess + numUnsuccess));
    System.out.println("total throughput in requests per second: "
        + (double) (successfulCounter.getValue() + unsuccessfulCounter.getValue()) / duration
        * 1000);


  }


}

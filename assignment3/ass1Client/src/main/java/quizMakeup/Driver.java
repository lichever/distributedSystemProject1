package quizMakeup;


import client1.Counter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Driver {
  private static final int TOTAL_REQUESTS = 100000;
  private static final int POOL_SIZE = 50;
  private static final int REQUEST_PER_THREAD = 2000;

  public static void main(String[] args) throws InterruptedException {

    LinkedBlockingQueue<Integer> q = new LinkedBlockingQueue<>();
    Generator generator = new Generator(q, TOTAL_REQUESTS);
    ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);
    Counter primeCounter = new Counter();
    Counter nonPrimeCounter = new Counter();
    CountDownLatch endCountDown = new CountDownLatch(TOTAL_REQUESTS);

    Thread producer = new Thread(generator);
    producer.start();

    long start = System.currentTimeMillis();

    for (int i = 0; i < POOL_SIZE; i++) {
      Poster poster = new Poster(q, endCountDown, primeCounter, nonPrimeCounter,
          REQUEST_PER_THREAD);
      threadPool.execute(poster);
    }
    endCountDown.await();
    long end = System.currentTimeMillis();
    threadPool.shutdown();
    threadPool.awaitTermination(10, TimeUnit.SECONDS);
    long duration = end - start;
    System.out.println("percentage of prime numbers: " + (double)primeCounter.getValue()/TOTAL_REQUESTS);
    System.out.println("total wall time used in millisecond: " + duration);
    System.out.println("mean response time : " + (double)duration/TOTAL_REQUESTS);
  }

}

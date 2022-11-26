package client1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Driver {

  public static final int FIRST_STAGE_THREADS = 31;
  public static final int FIRST_STAGE_NUM_OF_POST = 1000;
  public static final int SECOND_STAGE_THREADS = 336;
  public static final int SECOND_STAGE_NUM_OF_POST = 500;
  public static final int POOL_SIZE = 367;

//  public static final int SECOND_STAGE_THREADS = 168;
//  public static final int SECOND_STAGE_NUM_OF_POST = 1000;
//  public static final int POOL_SIZE = 199;


  public static final int TOTAL_REQUESTS = 200000;

  public static void main(String[] args) throws InterruptedException {

    LinkedBlockingQueue<MyLiftRide> q = new LinkedBlockingQueue<>();
    Generator generator = new Generator(q, TOTAL_REQUESTS);
    Counter successfulCounter = new Counter();
    Counter unsuccessfulCounter = new Counter();
    ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);
    CountDownLatch endCountDown = new CountDownLatch(TOTAL_REQUESTS);

    long start = System.currentTimeMillis();
    Thread producer = new Thread(generator);
    producer.start();

    // first phase:  32 threads with 1000 requests
    Poster firstWarmUpPoster = new Poster(q, endCountDown, successfulCounter, unsuccessfulCounter,
        FIRST_STAGE_NUM_OF_POST);
    Thread firstWarmUpConsumer = new Thread(firstWarmUpPoster);
    firstWarmUpConsumer.setPriority(Thread.MAX_PRIORITY);
    firstWarmUpConsumer.start();

    for (int i = 0; i < FIRST_STAGE_THREADS; i++) {
      Poster poster = new Poster(q, endCountDown, successfulCounter, unsuccessfulCounter,
          FIRST_STAGE_NUM_OF_POST);
      threadPool.execute(poster);
    }
    firstWarmUpConsumer.join();

    //second phase: 336 threads with 500 requests
    for (int i = 0; i < SECOND_STAGE_THREADS; i++) {
      Poster poster = new Poster(q, endCountDown, successfulCounter, unsuccessfulCounter,
          SECOND_STAGE_NUM_OF_POST);
      threadPool.execute(poster);
    }
    endCountDown.await();
    long end = System.currentTimeMillis();

    threadPool.shutdown();
    threadPool.awaitTermination(10, TimeUnit.SECONDS);
    long duration = end - start;
    System.out.println("number of successful requests sent: " + successfulCounter.getValue());
    System.out.println("number of unsuccessful requests: " + unsuccessfulCounter.getValue());
    System.out.println("total runtime used in millisecond: " + duration);
    System.out.println("total throughput in requests per second: "
        + (double) (successfulCounter.getValue() + unsuccessfulCounter.getValue()) / duration
        * 1000);
  }
}

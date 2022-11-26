package client2;

import static client1.Driver.FIRST_STAGE_NUM_OF_POST;
import static client1.Driver.FIRST_STAGE_THREADS;
import static client1.Driver.POOL_SIZE;
import static client1.Driver.SECOND_STAGE_NUM_OF_POST;
import static client1.Driver.SECOND_STAGE_THREADS;
import static client1.Driver.TOTAL_REQUESTS;

import client1.Counter;
import client1.Generator;
import client1.MyLiftRide;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Driver2 {

  public static void main(String[] args) throws InterruptedException {

    LinkedBlockingQueue<MyLiftRide> q = new LinkedBlockingQueue<>();
    Generator generator = new Generator(q, TOTAL_REQUESTS);
    Counter successfulCounter = new Counter();
    Counter unsuccessfulCounter = new Counter();
    ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);
    CountDownLatch endCountDown = new CountDownLatch(TOTAL_REQUESTS);
    Stats stats = new Stats(successfulCounter, unsuccessfulCounter);

    stats.start();
    Thread producer = new Thread(generator);
    producer.start();

    // first phase:  32 threads with 1000 requests
    Poster2 firstWarmUpPoster = new Poster2(q, endCountDown, successfulCounter, unsuccessfulCounter,
        FIRST_STAGE_NUM_OF_POST, stats);

    Thread firstWarmUpConsumer = new Thread(firstWarmUpPoster);
    firstWarmUpConsumer.setPriority(Thread.MAX_PRIORITY);
    firstWarmUpConsumer.start();

    for (int i = 0; i < FIRST_STAGE_THREADS; i++) {
      Poster2 poster = new Poster2(q, endCountDown, successfulCounter, unsuccessfulCounter,
          FIRST_STAGE_NUM_OF_POST, stats);
      threadPool.execute(poster);
    }
    firstWarmUpConsumer.join();

    //second phase: 336 threads with 500 requests
    for (int i = 0; i < SECOND_STAGE_THREADS; i++) {
      Poster2 poster = new Poster2(q, endCountDown, successfulCounter, unsuccessfulCounter,
          SECOND_STAGE_NUM_OF_POST, stats);
      threadPool.execute(poster);
    }

    endCountDown.await();
    stats.end();
    threadPool.shutdown();
    threadPool.awaitTermination(10, TimeUnit.SECONDS);
    stats.printStats();
  }
}

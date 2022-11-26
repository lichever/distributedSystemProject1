package quizMakeup;


import client1.MyLiftRide;
import io.swagger.client.model.LiftRide;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Generator implements Runnable {

  private LinkedBlockingQueue<Integer> q;
  private int totalRequest;
  private final Random rand = new Random();
  public Generator(LinkedBlockingQueue<Integer> q, int totalRequest) {
    this.q = q;
    this.totalRequest = totalRequest;
  }

  @Override
  public void run() {
    for (int i = 0; i < totalRequest; i++) {
      this.q.offer(rand.nextInt(5000)*2 +1);
    }

  }
}

package testAndExperiment;

import java.util.concurrent.atomic.AtomicInteger;

public class TestCounter {

  public static class CounterSync {

    int count = 0;

    public synchronized void increment() {
      count++;
    }

    public synchronized void decrement() {
      count--;
    }
  }

  public static class CounterAtomic {

    AtomicInteger count = new AtomicInteger();

    public void increment() {
      count.incrementAndGet();
    }

    public void decrement() {
      count.decrementAndGet();
    }
  }

//  public static void main(String[] args) throws InterruptedException {
//    CounterSync counterSync = new CounterSync();
//    CounterAtomic counterAtomic = new CounterAtomic();
//
//    Runnable runnable = () -> {
//      for (int i = 0; i < 10000000; i++) {
//        int j = i & 1;
//        if (j == 0) {
//          counterSync.increment();
//        } else {
//          counterSync.decrement();
//        }
//      }
//    };
//
//    Runnable runnable2 = () -> {
//      for (int i = 0; i < 10000000; i++) {
//        int j = i & 1;
//        if (j == 0) {
//          counterAtomic.increment();
//        } else {
//          counterAtomic.decrement();
//        }
//      }
//    };
//
//    System.out.println("Sync count = " + helper(runnable));
//    System.out.println("Atomic count = " + helper(runnable2));
//  }

  static long helper(Runnable runnable) throws InterruptedException {
    Thread[] threads = new Thread[10];
    long start1 = System.currentTimeMillis();
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(runnable);
    }

    for (Thread t : threads) {
      t.start();
    }

    for (Thread t : threads) {
      t.join();
    }
    long end1 = System.currentTimeMillis();

    return end1 - start1;
  }


}

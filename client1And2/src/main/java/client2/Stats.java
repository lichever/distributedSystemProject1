package client2;

import client1.Counter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Stats {

  private Long startTime;
  private Long endTime;

  private Counter successfulCounter;
  private Counter unsuccessfulCounter;

  private ConcurrentLinkedQueue<List<Long>> durations;
  private ConcurrentLinkedQueue<String> logs;

  public Stats(Counter successfulCounter, Counter unsuccessfulCounter) {
    this.successfulCounter = successfulCounter;
    this.unsuccessfulCounter = unsuccessfulCounter;
    this.durations = new ConcurrentLinkedQueue<>();
    this.logs = new ConcurrentLinkedQueue<>();

  }

  public void start() {
    this.startTime = System.currentTimeMillis();
  }

  public void end() {
    this.endTime = System.currentTimeMillis();
  }

  public void addDurationOfPoster(List<Long> durationList) {
    this.durations.add(durationList);
  }

  public void addLog(String log) {
    this.logs.add(log);
  }

  public void printStats() {
    List<Long> arr = new ArrayList<>();
    for (List<Long> durationList : durations) {
      arr.addAll(durationList);
    }

    int n = arr.size();
    Collections.sort(arr);

    //mean response time (millisecs)
    //median response time (millisecs)
    //p99 (99th percentile) response time (millisecs)
    //min and max response time (millisecs)
    //number of successful requests sent
    //number of unsuccessful requests sent
    //total runtime used in millisecond
    //total throughput in requests per second

    double median = arr.get(n / 2);
    long p99 = arr.get((n - 1) * 99 / 100);
    long min = arr.get(0);
    long max = arr.get(n - 1);
    double mean = 0F;
    for (long x : arr) {
      mean += x;
    }
    mean /= n;
    int numSuccess = successfulCounter.getValue();
    int numUnsuccess = unsuccessfulCounter.getValue();
    long totalRuntime = endTime - startTime;
    double throughput = ((double) numSuccess + numUnsuccess) / totalRuntime * 1000;

    System.out.println("mean response time in millisecond: " + mean);
    System.out.println("median response time in millisecond: " + median);
    System.out.println("p99 response time in millisecond: " + p99);
    System.out.println("min response time in millisecond: " + min);
    System.out.println("max response time in millisecond: " + max);
    System.out.println("number of successful requests sent: " + numSuccess);
    System.out.println("number of unsuccessful requests: " + numUnsuccess);
    System.out.println("total runtime used in millisecond: " + totalRuntime);
    System.out.println("total throughput in requests per second: " + throughput);

    //Write out a record containing {start time, request type (ie POST), latency, response code} to csv file
    File file = new File("logs.csv");
    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
      for (String log : logs) {
        writer.write(log);
        writer.newLine();
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

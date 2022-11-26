package client2;

import static client1.Poster.BASE_PATH;
import static client1.Poster.SUCCESS_CODE;
import static client1.Poster.TOTAL_RETRY;

import client1.Counter;
import client1.MyLiftRide;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class Poster2 implements Runnable{
  private Stats stats;
  private LinkedBlockingQueue<MyLiftRide> q;
  private CountDownLatch endCountDown;
  private Counter successfulCounter;
  private Counter unsuccessfulCounter;
  private int totalPost;


  public Poster2(LinkedBlockingQueue<MyLiftRide> q,
      CountDownLatch endCountDown, Counter successfulCounter, Counter failCounter, int numOfPost, Stats stats) {
    this.q = q;
    this.endCountDown = endCountDown;
    this.successfulCounter = successfulCounter;
    this.unsuccessfulCounter = failCounter;
    this.totalPost = numOfPost;
    this.stats = stats;
  }


  @Override
  public void run() {

    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(BASE_PATH);
    SkiersApi skiersApi = new SkiersApi(apiClient);
    List<Long> durations = new ArrayList<>();
    StringBuilder logForPoster = new StringBuilder();

    for (int i = 0; i < totalPost; i++) {
      MyLiftRide myLiftRide = q.poll();
      long start = System.currentTimeMillis();
      ApiResponse<Void> res = null;
      try {
          res = skiersApi.writeNewLiftRideWithHttpInfo(myLiftRide.getBody(),
            myLiftRide.getResortID(), myLiftRide.getSeasonID()
            , myLiftRide.getDayID(), myLiftRide.getSkierID());

        int retry = 0;

        while (res.getStatusCode() != SUCCESS_CODE && retry < TOTAL_RETRY) {
          res = skiersApi.writeNewLiftRideWithHttpInfo(myLiftRide.getBody(),
              myLiftRide.getResortID(), myLiftRide.getSeasonID()
              , myLiftRide.getDayID(), myLiftRide.getSkierID());
          retry++;
        }
        if (res.getStatusCode() == SUCCESS_CODE) {
          endCountDown.countDown();
          successfulCounter.increase();
        } else {
          unsuccessfulCounter.increase();
        }

      } catch (ApiException e) {
        throw new RuntimeException(e);
      }

      long end = System.currentTimeMillis();
      long duration = end - start;
      durations.add(duration);

      logForPoster.append(start).append(",").append("POST").append(",").append(duration).append(",")
          .append(res.getStatusCode()).append("\n");

    }
    logForPoster.deleteCharAt(logForPoster.length()-1);//due to  writer.newLine() in stats
    stats.addLog(logForPoster.toString());
    stats.addDurationOfPoster(durations);
  }
}

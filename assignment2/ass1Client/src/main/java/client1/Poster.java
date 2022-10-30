package client1;


import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

// a thread to send posts to the server
public class Poster implements Runnable {

  private LinkedBlockingQueue<MyLiftRide> q;
  private CountDownLatch endCountDown;
  private Counter successfulCounter;
  private Counter unsuccessfulCounter;
  private int totalPost;

//    public static final String BASE_PATH = "http://localhost:8080";
  public static String BASE_PATH = "http://alb-ass2-620885190.us-west-2.elb.amazonaws.com/ass1Server_war";
  public static final int TOTAL_RETRY = 5;
  public static final int SUCCESS_CODE = 201;

  public Poster(LinkedBlockingQueue<MyLiftRide> q,
      CountDownLatch endCountDown, Counter successfulCounter, Counter failCounter, int numOfPost) {
    this.q = q;
    this.endCountDown = endCountDown;
    this.successfulCounter = successfulCounter;
    this.unsuccessfulCounter = failCounter;
    this.totalPost = numOfPost;
  }

  @Override
  public void run() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(BASE_PATH);
    SkiersApi skiersApi = new SkiersApi(apiClient);

    for (int i = 0; i < totalPost; i++) {
      MyLiftRide myLiftRide = q.poll();
//      System.out.println(i);
      try {
        ApiResponse<Void> res = skiersApi.writeNewLiftRideWithHttpInfo(myLiftRide.getBody(),
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
    }
  }

}

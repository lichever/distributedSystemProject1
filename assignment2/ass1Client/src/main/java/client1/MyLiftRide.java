package client1;

import io.swagger.client.model.LiftRide;
import java.util.Objects;

public class MyLiftRide {
  private LiftRide body;
  private Integer resortID;
  private String seasonID;
  private String dayID;
  private Integer skierID;

  public MyLiftRide(LiftRide body, Integer resortID, String seasonID, String dayID,
      Integer skierID) {
    this.body = body;
    this.resortID = resortID;
    this.seasonID = seasonID;
    this.dayID = dayID;
    this.skierID = skierID;
  }


  public LiftRide getBody() {
    return body;
  }

  public void setBody(LiftRide body) {
    this.body = body;
  }

  public Integer getResortID() {
    return resortID;
  }

  public void setResortID(Integer resortID) {
    this.resortID = resortID;
  }

  public String getSeasonID() {
    return seasonID;
  }

  public void setSeasonID(String seasonID) {
    this.seasonID = seasonID;
  }

  public String getDayID() {
    return dayID;
  }

  public void setDayID(String dayID) {
    this.dayID = dayID;
  }

  public Integer getSkierID() {
    return skierID;
  }

  public void setSkierID(Integer skierID) {
    this.skierID = skierID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MyLiftRide that = (MyLiftRide) o;
    return Objects.equals(body, that.body) && Objects.equals(resortID,
        that.resortID) && Objects.equals(seasonID, that.seasonID)
        && Objects.equals(dayID, that.dayID) && Objects.equals(skierID,
        that.skierID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(body, resortID, seasonID, dayID, skierID);
  }


  @Override
  public String toString() {
    return "MyLiftRide{" +
        "body=" + body +
        ", resortID=" + resortID +
        ", seasonID='" + seasonID + '\'' +
        ", dayID='" + dayID + '\'' +
        ", skierID=" + skierID +
        '}';
  }
}

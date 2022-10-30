import java.util.Objects;

public class Message {

  private int time;
  private int liftID;
  private int resortID;
  private int seasonID;
  private int dayID;
  private int skierID;

  public Message(int time, int liftID, int resortID, int seasonID, int dayID, int skierID) {
    this.time = time;
    this.liftID = liftID;
    this.resortID = resortID;
    this.seasonID = seasonID;
    this.dayID = dayID;
    this.skierID = skierID;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public int getLiftID() {
    return liftID;
  }

  public void setLiftID(int liftID) {
    this.liftID = liftID;
  }

  public int getResortID() {
    return resortID;
  }

  public void setResortID(int resortID) {
    this.resortID = resortID;
  }

  public int getSeasonID() {
    return seasonID;
  }

  public void setSeasonID(int seasonID) {
    this.seasonID = seasonID;
  }

  public int getDayID() {
    return dayID;
  }

  public void setDayID(int dayID) {
    this.dayID = dayID;
  }


  public int getSkierID() {
    return skierID;
  }

  public void setSkierID(int skierID) {
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
    Message message = (Message) o;
    return time == message.time && liftID == message.liftID && resortID == message.resortID
        && seasonID == message.seasonID && dayID == message.dayID && skierID == message.skierID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, liftID, resortID, seasonID, dayID, skierID);
  }

  @Override
  public String toString() {
    return "Message{" +
        "time=" + time +
        ", liftID=" + liftID +
        ", resortID=" + resortID +
        ", seasonID=" + seasonID +
        ", dayID=" + dayID +
        ", skierID=" + skierID +
        '}';
  }
}

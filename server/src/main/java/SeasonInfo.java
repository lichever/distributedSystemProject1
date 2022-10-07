public class SeasonInfo {

  /*
    "time": 217,
  "liftID": 21


   */

  private int time;
  private int liftID;

  public SeasonInfo(int time, int liftID) {
    this.time = time;
    this.liftID = liftID;
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

  @Override
  public String toString() {
    return "SeasonInfo{" +
        "time=" + time +
        ", liftID=" + liftID +
        '}';
  }
}

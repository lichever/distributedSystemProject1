package dao;

import database.DBCPDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import pojo.Message;

public class LiftRideDao {

  private static final String DELIMITER = " ";

  // batch insert messages per thread
  public static Runnable getBatchInsertRunnable(ConcurrentLinkedQueue<String> q) {
    return () -> {
      String sql = "INSERT INTO liftRides (skierId, resortId, seasonId, dayId, time, liftId) " +
          "VALUES (?,?,?,?,?,?)";
      Connection conn = null;
      PreparedStatement stat = null;
      try {
        conn = DBCPDataSource.getConnection();
        stat = conn.prepareStatement(sql);

        for (int i = 1; i <= 5000; i++) {
          String message = q.poll();// not using blocking take to speed up
          if (message == null) { //may not 5000 enough
            continue;
          }
          String[] tokens = message.split(DELIMITER);
          int time = Integer.parseInt(tokens[0]);
          int liftID = Integer.parseInt(tokens[1]);
          int resortID = Integer.parseInt(tokens[2]);
          int seasonID = Integer.parseInt(tokens[3]);
          int dayID = Integer.parseInt(tokens[4]);
          int skierID = Integer.parseInt(tokens[5]);

          stat.setInt(1, skierID);
          stat.setInt(2, resortID);
          stat.setInt(3, seasonID);
          stat.setInt(4, dayID);
          stat.setInt(5, time);
          stat.setInt(6, liftID);
          stat.addBatch();
        }
        System.out.println(Thread.currentThread().getName() + ": " + 5000);
        stat.executeBatch();
        stat.clearBatch();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          DBCPDataSource.closeConnection();
          if (stat != null) {
            stat.close();
          }
        } catch (SQLException se) {
          se.printStackTrace();
        }
      }
    };
  }

  //test: to batch all 200k in a single thread
  public static void executeOne(ConcurrentLinkedQueue<String> q) {
    String sql = "INSERT INTO liftRides (skierId, resortId, seasonId, dayId, time, liftId) " +
        "VALUES (?,?,?,?,?,?)";
    Connection conn = null;
    PreparedStatement stat = null;
    try {
      conn = DBCPDataSource.getConnection();
      stat = conn.prepareStatement(sql);

      for (int i = 1; i <= 200000; i++) {
        String message = q.poll();
        String[] tokens = message.split(DELIMITER);
        int time = Integer.parseInt(tokens[0]);
        int liftID = Integer.parseInt(tokens[1]);
        int resortID = Integer.parseInt(tokens[2]);
        int seasonID = Integer.parseInt(tokens[3]);
        int dayID = Integer.parseInt(tokens[4]);
        int skierID = Integer.parseInt(tokens[5]);

        stat.setInt(1, skierID);
        stat.setInt(2, resortID);
        stat.setInt(3, seasonID);
        stat.setInt(4, dayID);
        stat.setInt(5, time);
        stat.setInt(6, liftID);
        stat.addBatch();
        if (i % 1000 == 0) {
          System.out.println(i + ": " + 1000);
          stat.executeBatch();
          stat.clearBatch();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        DBCPDataSource.closeConnection();
        if (stat != null) {
          stat.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }

  //  insert one message for each channel
  public static void createLiftRide(int skierID, int resortID, int seasonID, int dayID, int time,
      int liftID) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement =
        "INSERT INTO liftRides (skierId, resortId, seasonId, dayId, time, liftId) " +
            "VALUES (?,?,?,?,?,?)";
    try {
      conn = DBCPDataSource.getConnection();
//      conn = DBCPDataSource.getDataSource().getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, skierID);
      preparedStatement.setInt(2, resortID);
      preparedStatement.setInt(3, seasonID);
      preparedStatement.setInt(4, dayID);
      preparedStatement.setInt(5, time);
      preparedStatement.setInt(6, liftID);

      // execute insert SQL statement
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
//        if (conn != null) {
//          conn.close();
//        }
        DBCPDataSource.closeConnection();
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }

  //  insert one message for each channel
  public static void createLiftRide(Message newLiftRide) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement =
        "INSERT INTO LiftRides (skierId, resortId, seasonId, dayId, time, liftId) " +
            "VALUES (?,?,?,?,?,?)";
    try {
      conn = DBCPDataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newLiftRide.getSkierID());
      preparedStatement.setInt(2, newLiftRide.getResortID());
      preparedStatement.setInt(3, newLiftRide.getSeasonID());
      preparedStatement.setInt(4, newLiftRide.getDayID());
      preparedStatement.setInt(5, newLiftRide.getTime());
      preparedStatement.setInt(6, newLiftRide.getLiftID());

      // execute insert SQL statement
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
//        if (conn != null) {
//          conn.close();
//        }
        DBCPDataSource.closeConnection();
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }


}

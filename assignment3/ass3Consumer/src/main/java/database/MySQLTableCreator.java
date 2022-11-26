package database;

import config.ConfigUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLTableCreator {

  private static String getMySQLAddress() throws IOException {
    return String.format(
        "jdbc:mysql://%s:%s/%s?user=%s&password=%s&autoReconnect=true&serverTimezone=UTC&createDatabaseIfNotExist=true",
        ConfigUtil.getMySQLHostName(), ConfigUtil.getMySQLPort(), ConfigUtil.getMySQLDBName(),
        ConfigUtil.getMySQLUserName(), ConfigUtil.getMySQLPassword());
  }

  // Run this as a Java application to reset the database.
  public static void main(String[] args) {
    try {
      // Step 1 Connect to MySQL.
      System.out.println("Connecting to " + getMySQLAddress());
      //反射机制 用string创建instance，可以写在另外一个文件里面，如果有以后有新的driver，后面方便修改
      Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
      //上面的 newinstance() 在 DriverManager里面
      Connection conn = DriverManager.getConnection(getMySQLAddress());
      if (conn == null) {
        System.out.println("Driver manager get connection failed");
        return;
      }
      // Step 2 Drop tables in case they exist. if there are multiple dependent tables, reverse order.
      Statement statement = conn.createStatement();
      String sql = "DROP TABLE IF EXISTS liftRides";
      statement.executeUpdate(sql);
      // Step 3 Create new tables.
      sql = "CREATE TABLE liftRides ("
          + "id INT NOT NULL AUTO_INCREMENT,"
          + "skierId INT,"
          + "resortId INT,"
          + "seasonId INT,"
          + "dayId INT,"
          + "time INT,"
          + "liftId INT,"
          + "PRIMARY KEY (id)"
          + ")";
      statement.executeUpdate(sql);
      conn.close();
      System.out.println("Import done successfully");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}

package database;

import config.ConfigUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

public class DBCPDataSource {

  private static final ThreadLocal<Connection> connHolder;

  private static final BasicDataSource dataSource;

  static {
    connHolder = new ThreadLocal<Connection>();
    dataSource = new BasicDataSource();
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    String url = null;
    try {
      url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", ConfigUtil.getMySQLHostName(),
          ConfigUtil.getMySQLPort(),
          ConfigUtil.getMySQLDBName());
      dataSource.setUrl(url);
      dataSource.setUsername(ConfigUtil.getMySQLUserName());
      dataSource.setPassword(ConfigUtil.getMySQLPassword());
      dataSource.setInitialSize(20);
      dataSource.setMaxTotal(60);
      dataSource.setMaxWaitMillis(20 * 1000);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static BasicDataSource getDataSource() {
    return dataSource;
  }


  public static Connection getConnection() {
    Connection conn = connHolder.get();
    if (conn == null) {
      try {
        conn = dataSource.getConnection();
        System.out.println("get connection success");
      } catch (SQLException e) {
        System.out.println("get connection failure:" + e);
      } finally {
        connHolder.set(conn);
      }
    }
    return conn;
  }

  public static void closeConnection() {
    Connection conn = connHolder.get();
    if (conn != null) {
      try {
        conn.close();
        System.out.println("close connection success");
      } catch (SQLException e) {
        System.out.println("close connection failure:" + e);
        throw new RuntimeException(e);
      } finally {
        connHolder.remove();
      }
    }
  }


}

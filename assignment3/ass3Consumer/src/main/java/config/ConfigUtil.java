package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

  private static final String PROP_PATH  = "src/main/java/config/config.properties";

  public static Properties getPropertyObject() throws IOException {
    Properties prop = new Properties();
    InputStream inputStream = new FileInputStream(PROP_PATH);
    prop.load(inputStream);
    return prop;
  }

  public static String getMySQLUserName() throws IOException {
    return getPropertyObject().getProperty("mysqlUser");
  }

  public static String getMySQLPassword() throws IOException {
    return getPropertyObject().getProperty("mysqlPassword");
  }

  public static String getMySQLPort() throws IOException {
    return getPropertyObject().getProperty("mysqlPort");
  }

  public static String getMySQLDBName() throws IOException {
    return getPropertyObject().getProperty("dbName");
  }

  public static String getMySQLHostName() throws IOException {
    return getPropertyObject().getProperty("mysqlHostName");
  }


  public static String getRMQ_IP() throws IOException {
    return getPropertyObject().getProperty("RMQ_IP");
  }
  public static String getRMQUserName() throws IOException {
    return getPropertyObject().getProperty("RMQUser");
  }
  public static String getRMQPassword() throws IOException {
    return getPropertyObject().getProperty("RMQPassword");
  }


//    public static void main(String[] args) throws IOException {
//      System.out.println(getMySQLPort());
//      System.out.println(getMySQLHostName());
//    }


}

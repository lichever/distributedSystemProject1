package testOtherPoolOptions;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

//this factory is for Apache pool library
public class ChannelFactory2 extends BasePooledObjectFactory<Channel> {
  private static Connection connection;
  private static final String RMQ_IP = "35.92.163.188";

  static {
    ConnectionFactory factory = new ConnectionFactory();
    //factory.setHost("localhost");
    factory.setHost(RMQ_IP);
    factory.setUsername("admin");
    factory.setPassword("password");
    try {
      connection = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      System.out.println("error while trying to new a connection");
      e.printStackTrace();
    }
  }

  @Override
  public Channel create() throws IOException {
    return connection.createChannel();
  }

  /**
   * Use the default PooledObject implementation.
   */
  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<>(channel);
  }
}

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

//this factory is for my own pool
public class ChannelFactory extends BasePooledObjectFactory<Channel> {

  private final Connection connection;
  // used to count created channels for debugging
//  private static final AtomicInteger count = new AtomicInteger(0);


  public ChannelFactory(Connection connection) {
    this.connection = connection;
  }

  @Override
   public Channel create() throws Exception {
//    count.getAndIncrement();
//    System.out.println("Channel created: " + count.get());
    return connection.createChannel();
  }

  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<>(channel);
  }
}

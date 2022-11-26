import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojo.Message;
import rmq.ChannelFactory;
import rmq.RMQChannelPool;

public class ReceiverWithPool {


  private static final String RMQ_IP = "54.202.238.35";
  private static final int NUM_CHANNEL = 400;
  private static Connection connection;
  private final static String QUEUE_NAME = "skiersPost";
  private static final String EXCHANGE_NAME = "liftride";
  private static final String DELIMITER = " ";

  private static ConcurrentHashMap<Integer, List<Message>> mp = new ConcurrentHashMap();//skierId->["time: 40 liftId: 50" , "..."]


  public static void main(String[] args) {

    //my own pool
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RMQ_IP);//public RMB instance ip
    factory.setUsername("admin");
    factory.setPassword("password");
    try {
      connection = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      System.out.println("error while trying to new a connection");
      e.printStackTrace();
    }
    ChannelFactory channelFactory = new ChannelFactory(connection);
    RMQChannelPool channelPool = new RMQChannelPool(NUM_CHANNEL, channelFactory);




    for (int i = 0; i < NUM_CHANNEL; i++) {
      System.err.println("for loop : " +  i );
      try {
        final Channel channel = channelPool.borrowObject();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");        // max one message per receiver
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

//          System.out.println("using channel number: "  + channel.getChannelNumber());

//            try {
          storeToMap(message);
//            } finally {
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//async function
//            }
//          try {
//            Thread.sleep(5000);
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
        };
        // process messages
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {//sync?
        });


        //useless here because async above
//        try {
//          System.err.println("return channel number: "  + channel.getChannelNumber());
//          channelPool.returnObject(channel);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }


      } catch (IOException ex) {
        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
      }finally {
//        try {
//          if (null != channel) {
//            channelPool.returnObject(channel);
//          }
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
      }
    }
  }


  //store the messages for each skierID
  private static void storeToMap(String message) {
    String[] tokens = message.split(DELIMITER);
    int time = Integer.parseInt(tokens[0]);
    int liftID = Integer.parseInt(tokens[1]);
    int resortID = Integer.parseInt(tokens[2]);
    int seasonID = Integer.parseInt(tokens[3]);
    int dayID = Integer.parseInt(tokens[4]);
    int skierID = Integer.parseInt(tokens[5]);
    Message msg = new Message(time, liftID, resortID, seasonID, dayID, skierID);
    mp.computeIfAbsent(skierID, key -> new ArrayList<>()).add(msg);
  }

}

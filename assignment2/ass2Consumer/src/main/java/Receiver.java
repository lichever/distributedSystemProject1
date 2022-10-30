import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver {

  private static final String RMQ_IP = "34.220.245.83";
//  private static final String RMQ_IP = "54.190.71.200";
  private static final int THREAD_POOL_SIZE = 25;
  private static final int PER_THREAD_CHANNEL_SIZE = 10;
  private final static String QUEUE_NAME = "skiersPost";
  private static final String EXCHANGE_NAME = "liftride";
  private static final String DELIMITER = " ";
  private static ConcurrentHashMap<Integer, List<Message>> mp = new ConcurrentHashMap();//skierId->["time: 40 liftId: 50" , "..."]

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
//    factory.setHost("localhost");
    factory.setHost(RMQ_IP);
    factory.setUsername("admin");
    factory.setPassword("password");

    final Connection connection = factory.newConnection();

    Runnable runnable = () -> {
//      Channel channel = null;
      for (int i = 0; i < PER_THREAD_CHANNEL_SIZE; i++) {
        try {
          final Channel channel = connection.createChannel();
          channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
          channel.queueDeclare(QUEUE_NAME, false, false, false, null);
          channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");        // max one message per receiver
          channel.basicQos(1);

          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//            try {
            storeToMap(message);
//            } finally {
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//            }
          };
          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
          });
        } catch (IOException ex) {
          Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };

    // start threads and block to receive messages
    ExecutorService consumerPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      consumerPool.execute(runnable);
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

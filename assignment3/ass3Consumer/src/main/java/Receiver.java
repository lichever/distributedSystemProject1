import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import config.ConfigUtil;
import dao.LiftRideDao;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojo.Message;

public class Receiver {

  private static final int THREAD_POOL_SIZE = 20;
  private static final int PER_THREAD_CHANNEL_SIZE = 15;
  private final static String QUEUE_NAME = "skiersPost";
  private static final String EXCHANGE_NAME = "liftride";
  private static final String DELIMITER = " ";
  private static ConcurrentHashMap<Integer, List<Message>> mp = new ConcurrentHashMap();//skierId->["time: 40 liftId: 50" , "..."]
  private static ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<>();//skierId->["time: 40 liftId: 50" , "..."]


  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
//    factory.setHost("localhost");
    factory.setHost(ConfigUtil.getRMQ_IP());
    factory.setUsername(ConfigUtil.getRMQUserName());
    factory.setPassword(ConfigUtil.getRMQPassword());

    final Connection connection = factory.newConnection();

    //ass3: batch insert after getting a number of messages
//    Runnable runnableToBatchStore = () -> {
//
//      for (int i = 0; i < PER_THREAD_CHANNEL_SIZE; i++) {
//        try {
//          final Channel channel = connection.createChannel();
//          channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//          channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//          channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");        // max one message per receiver
//          channel.basicQos(1);
//
//          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//            q.offer(message);
//            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
////            }
//          };
//          // process messages
//          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
//          });
//        } catch (IOException ex) {
//          Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      }
////      LiftRideDao.executeBatch(q);
//    };
//
//
//    Runnable runnableToBatchInsert = () -> {
//        while (true){
//                if(!LiftRideDao.executeBatch(q)){
//                  try {
//                    Thread.sleep(1000);
//                  } catch (InterruptedException e) {
//                    e.printStackTrace();
//                  }
//                }
//        }
//    };

    //ass3: per channel insert one message
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
//            storeToMap(message);
            insertToDB(message);
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

//    new Thread( ()->{
//      while (true){
//        if(q.size() == 200000){
//          System.out.println("yes");
////          LiftRideDao.executeOne(q);
//          LiftRideDao.executeMany(q);
//        }
//      }
//    }).start();

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

  private static void insertToDB(String message) {
    String[] tokens = message.split(DELIMITER);
    int time = Integer.parseInt(tokens[0]);
    int liftID = Integer.parseInt(tokens[1]);
    int resortID = Integer.parseInt(tokens[2]);
    int seasonID = Integer.parseInt(tokens[3]);
    int dayID = Integer.parseInt(tokens[4]);
    int skierID = Integer.parseInt(tokens[5]);
    LiftRideDao.createLiftRide(skierID, resortID, seasonID, dayID, time, liftID);
  }


}

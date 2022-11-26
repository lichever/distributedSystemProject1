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

    //ass3: batch insert after getting some messages
    Runnable runnableToBatchStore = () -> {

      for (int i = 0; i < PER_THREAD_CHANNEL_SIZE; i++) {
        try {
          final Channel channel = connection.createChannel();
          channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
          channel.queueDeclare(QUEUE_NAME, false, false, false, null);
          channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");        // max one message per receiver
          channel.basicQos(1);

          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            q.offer(message);
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

    //ass3: per channel insert one message
//    Runnable runnable = () -> {
////      Channel channel = null;
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
////            try {
////            storeToMap(message);
//            insertToDB(message);
////            } finally {
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
//    };

    // start threads and block to receive messages
    long start = System.currentTimeMillis();
    ExecutorService consumerPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      consumerPool.execute(runnableToBatchStore);
    }

    //dynamic abstract the messages and insert to db on the fly (half lazy)
    ExecutorService consumerPool2 = Executors.newFixedThreadPool(50);
    Thread.sleep(1000);
    int round = 0;
    int maxQueueSize = 0;

    while (true) {
      System.out.println("q size: " + q.size());
      for (int i = 0; i < 100; i++) {
        maxQueueSize = Math.max(maxQueueSize, q.size());
        System.out.println("inner max q size: " + maxQueueSize);
        System.out.println("thread: " + i);
        Thread.sleep(1000);
        if (i % 20 == 0) {
          Thread.sleep(1000);
        }
        consumerPool2.execute(LiftRideDao.getBatchInsertRunnable(q));
      }

      //let q has change to get new messages and start a new round
      try {
        Thread.sleep(2000);
        ++round;
        System.out.println("=====================");
        System.out.println("round - " + round);
        System.out.println("=====================");

        if (q.isEmpty()) {
          break;
        }

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    long end = System.currentTimeMillis();
    System.out.println("total time: " + (end - start) / 1000);//127s
    System.out.println("max cache queue size: " + maxQueueSize);//69943
  }


  //==============================================================================
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

  // per channel insert one message
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

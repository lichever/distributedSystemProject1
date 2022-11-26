//package test;
//
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.DeliverCallback;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeoutException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.sound.midi.Receiver;
//
//// just for testing RMQ instead of real running code
//public class Consume {
//
//  private static final String RMQ_IP = "54.202.238.35";
//  //  private static final String RMQ_IP = "54.190.71.200";
//  private static final int THREAD_POOL_SIZE = 25;
//  private static final int PER_THREAD_CHANNEL_SIZE = 10;
//  private static final String EXCHANGE_NAME = "liftride";
//  private static final String DELIMITER = " ";
//  private ConcurrentHashMap<Integer, List<pojo.Message>> mp = new ConcurrentHashMap();//skierId->["time: 40 liftId: 50" , "..."]
//
//
//  public ConcurrentHashMap<Integer, List<pojo.Message>> start(String QUEUE_NAME, int id)
//      throws IOException, TimeoutException, InterruptedException {
//    ConnectionFactory factory = new ConnectionFactory();
////    factory.setHost("localhost");
//    factory.setHost(RMQ_IP);
//    factory.setUsername("admin");
//    factory.setPassword("password");
//
//    final Connection connection = factory.newConnection();
//
//    Runnable runnable = () -> {
////      Channel channel = null;
//      for (int i = 0; i < PER_THREAD_CHANNEL_SIZE; i++) {
//        try {
//          final Channel channel = connection.createChannel();
//          channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//          channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//          channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");        // max one message per receiver
//          channel.basicQos(20);
//
//          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
////            try {
//            storeToMap(message, id);
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
//
//    // start threads and block to receive messages
//    ExecutorService consumerPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
//      consumerPool.execute(runnable);
//    }
//    return mp;
//  }
//
//  //store the messages for each skierID
//  private synchronized void storeToMap(String message, int id) {
//    String[] tokens = message.split(DELIMITER);
//    int time = Integer.parseInt(tokens[0]);
//    int liftID = Integer.parseInt(tokens[1]);
//    int resortID = Integer.parseInt(tokens[2]);
//    int seasonID = Integer.parseInt(tokens[3]);
//    int dayID = Integer.parseInt(tokens[4]);
//    int skierID = Integer.parseInt(tokens[5]);
//    pojo.Message msg = new pojo.Message(time, liftID, resortID, seasonID, dayID, skierID);
//    mp.computeIfAbsent(skierID, key -> new ArrayList<>()).add(msg);
//
//  }
//
//
//
//}

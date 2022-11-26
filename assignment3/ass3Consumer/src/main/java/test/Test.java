//package test;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeoutException;
//
//public class Test {
//
//
//  public static void main(String[] args)
//      throws InterruptedException, IOException, TimeoutException {
//    String qName1 = "skiersPost";
//    String qName2 = "skiersPost";
//    Consume c1 = new Consume();
//    Consume c2 = new Consume();
//
//    ConcurrentHashMap<Integer, List<pojo.Message>> mp1 = c1.start(qName1, 1);
//    ConcurrentHashMap<Integer, List<pojo.Message>> mp2 = c2.start(qName2, 2);
//
////    Thread.sleep(120000);
////
////    //count
////    int res1 = 0;
////    int res2 = 0;
////    for (int key : mp1.keySet()){
////      res1 += mp1.get(key).size();
////    }
////    for (int key : mp2.keySet()){
////      res2 += mp2.get(key).size();
////    }
////    System.out.println(res1);
////    System.out.println(res2);
//  }
//
//}

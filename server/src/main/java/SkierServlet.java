import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {

  private Gson gson = new Gson();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

    long start = System.currentTimeMillis();

    ResponseBody responseBody;
    String urlPath = req.getPathInfo();
    PrintWriter out = res.getWriter();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      responseBody = new ResponseBody("missing paramterers");
      String responseBodyString = this.gson.toJson(responseBody);
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      out.print(responseBodyString);
      out.flush();
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (isUrlValid(urlParts) == 2) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      responseBody = new ResponseBody("missing paramterers");
      String responseBodyString = this.gson.toJson(responseBody);
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      out.print(responseBodyString);
      out.flush();


    } else if (isUrlValid(urlParts) == 1) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      responseBody = new ResponseBody("invalid url");
      String responseBodyString = this.gson.toJson(responseBody);
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      out.print(responseBodyString);
      out.flush();

    } else {

      try {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = req.getReader().readLine()) != null) {
          sb.append(s);
        }
        SeasonInfo requestBody = (SeasonInfo) gson.fromJson(sb.toString(), SeasonInfo.class);

        if (requestBody.getLiftID() < 1 || requestBody.getLiftID() > 40
            || requestBody.getTime() < 1 || requestBody.getTime() > 360) {
          res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          responseBody = new ResponseBody("invalid request body data");
          String responseBodyString = this.gson.toJson(responseBody);
          res.setContentType("application/json");
          res.setCharacterEncoding("UTF-8");
          out.print(responseBodyString);
          out.flush();
        } else {
          res.setStatus(HttpServletResponse.SC_CREATED);
          responseBody = new ResponseBody("Successful posted a lift ride for the skier");
          String responseBodyString = this.gson.toJson(responseBody);
          res.setContentType("application/json");
          res.setCharacterEncoding("UTF-8");
          out.print(responseBodyString);
          out.flush();
        }

      } catch (Exception ex) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        responseBody = new ResponseBody("missing paramterers");
        String responseBodyString = this.gson.toJson(responseBody);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        out.print(responseBodyString);
        out.flush();
      }
    }

    long end = System.currentTimeMillis();
    System.out.println(end - start);


  }

  private int isUrlValid(String[] urlPath) {
    /*
    return 0: 201
    return 1: 400	Invalid inputs
    return 2: 404 Data not found
     urlPath  = "/1/seasons/2019/day/1/skier/123"
    POST/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
     urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
     */

    if (urlPath.length != 8) {
      return 2;
    }

    System.out.println(Arrays.toString(urlPath));

    try {
      int resortId = Integer.parseInt(urlPath[1]);
      String season = urlPath[2];
      int seasonID = Integer.parseInt(urlPath[3]);
      String day = urlPath[4];
      int dayID = Integer.parseInt(urlPath[5]);
      String skier = urlPath[6];
      int skierId = Integer.parseInt(urlPath[7]);

      if (resortId < 1 || resortId > 10 || seasonID != 2022 || dayID != 1 || skierId < 1
          || skierId > 100000 || !season.equals("seasons") || !day.equals("days")
          || !skier.equals("skiers")) {
        return 1;
      } else {
        return 0;
      }
    } catch (Exception e) {
      return 1;

    }
  }
}

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PrimeServlet", value = "/PrimeServlet")
public class PrimeServlet extends HttpServlet {

  Boolean[] memo = new Boolean[10005];
  Boolean[] table;


  @Override
  public void init() throws ServletException {
    table = new Boolean[10005];
    //Sieve of Eratosthenes
    Arrays.fill(table, true);
    table[0] = false;
    table[1] = false;
    int n = table.length;
    for (int i = 2; i * i < n; i++) {
      if (table[i]) {
        for (int j = i * i; j < n; j += i) {
          table[j] = false;
        }
      }
    }
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String urlPath = req.getPathInfo();
    String[] urlParts = urlPath.split("/");

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    int num = Integer.valueOf(urlParts[1]);
//    boolean ans = isPrime(num);
    boolean ans = table[num];

    if (ans) {
      res.setStatus(HttpServletResponse.SC_OK);
      return;
    } else {
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
  }


  boolean isPrime(int n) {
    if (memo[n] != null) {
      return memo[n];
    }

    if (n == 1) {
      return false;
    }
    int i = 2;
    while (i * i <= n) {
      if (n % i == 0) {
        return memo[n] = false;
      }
      i++;
    }
    return memo[n] = true;
  }


}

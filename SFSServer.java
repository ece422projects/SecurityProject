import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.InetAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.lang.*;

public class SFSServer {

  public static void main(String[] args) throws Exception {

    InetAddress IP = InetAddress.getByName("localhost");
    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
    // server.createContext("/info", new InfoHandler());
    // server.createContext("/login", new GetHandler());
    server.createContext("/login", new DataHandler());
    server.setExecutor(null);
    server.start();
  }

  static class DataHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {

      // Returns the body of GET/POST request
      System.out.println(t.getRequestURI());

      // Returns if it is GET or POST request
      System.out.println(t.getRequestMethod());

      String request;
      InputStream in = t.getRequestBody();
      System.out.println("NUMBER OF BYTES AVALIABLE: " + String.valueOf(in.available()));
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte buf[] = new byte[4096];
        for (int n = in.read(buf); n > 0; n = in.read(buf)) {
            out.write(buf, 0, n);
        }
        request = new String(out.toByteArray(), "US-ASCII");
        System.out.println("REQUEST: " + request);
    } finally {
        in.close();
    }

    }
  }

  static class InfoHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
      String response = "Use /get to download a PDF";
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }

  static class GetHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {

      // add the required response header for a PDF file
      Headers h = t.getResponseHeaders();
      h.add("Content-Type", "application/pdf");

      // a PDF (you provide your own!)
      File file = new File ("c:/temp/doc.pdf");
      byte [] bytearray  = new byte [(int)file.length()];
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      bis.read(bytearray, 0, bytearray.length);

      // ok, we are ready to send the response.
      t.sendResponseHeaders(200, file.length());
      OutputStream os = t.getResponseBody();
      os.write(bytearray,0,bytearray.length);
      os.close();
    }
  }
}
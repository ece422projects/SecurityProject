import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.URI;

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
    server.createContext("/", new DataHandler());
    server.setExecutor(null);
    server.start();
  }

  static class DataHandler implements HttpHandler {
    //**Route client to login page first, then home page, generate a cookie?
    public void handle(HttpExchange t) throws IOException {
      String root = "/";
      // Returns the body of GET/POST request
      URI uri = t.getRequestURI();
      String path = uri.getPath().substring(1);
      System.out.println(path);
      File file = new File(path).getCanonicalFile();
      if (!file.isFile()) {
         // Object does not exist or is not a file: reject with 404 error.
         String response = "404 (Not Found)\n";
         t.sendResponseHeaders(404, response.length());
         OutputStream os = t.getResponseBody();
         os.write(response.getBytes());
         os.close();
       }
       else{
         String mime = "text/html";
         if(path.substring(path.length()-3).equals(".js")){
           mime = "application/javascript";
         }
         if(path.substring(path.length()-4).equals(".css")){
            mime = "text/css";
          }
         System.out.println("MIME Type: " + mime);
         Headers h = t.getResponseHeaders();
         h.set("Content-Type", mime);
         t.sendResponseHeaders(200, 0);
         OutputStream os = t.getResponseBody();
         FileInputStream fs = new FileInputStream(file);
         final byte[] buffer = new byte[0x10000];
         int count = 0;
         while ((count = fs.read(buffer)) >= 0) {
           os.write(buffer,0,count);
         }
         fs.close();
         os.close();
       }
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

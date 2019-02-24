// import java.net.*;
// import java.util.*;
// import java.io.*;

// public class SFSServer {

//     private String url;
//     private int portNumber;

//     public SFSServer(String URL, int portN) throws Exception {
//         this.url = URL;
//         this.portNumber = portN;

//         URL url = new URL(this.url);
//         HttpURLConnection con = (HttpURLConnection) url.openConnection();
//         con.setRequestMethod("GET");
//         con.setDoOutput(true);
//     }

//     public static void main(String[] args) throws Exception {
//         try {
//             SFSServer server = new SFSServer("http://localhost:8080/", 8000);
//         }
//         catch(Exception e) {
//             System.out.println("Nope");
//         }
//     }

// }

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SFSServer {

  public static void main(String[] args) throws Exception {

    InetAddress IP = InetAddress.getByName("127.0.0.1");
    HttpServer server = HttpServer.create(new InetSocketAddress(IP, 8000), 0);
    // server.createContext("/info", new InfoHandler());
    // server.createContext("/login", new GetHandler());
    server.createContext("/login", new DataHandler());
    server.setExecutor(null);
    server.start();
  }

  static class DataHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {

      try {
             
        InputStream is = t.getRequestBody();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
         
        String line = null;
         
        while ((line = br.readLine()) != null) {
            if (line.equalsIgnoreCase("quit")) {
                break;
            }
            System.out.println("Line entered : " + line);
        }
         
    }
    catch (IOException ioe) {
        System.out.println("Exception while reading input " + ioe);
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
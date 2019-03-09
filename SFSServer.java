import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.URI;
import java.util.UUID;

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
import java.util.HashMap;
import java.lang.*;
import java.util.List;
import java.util.ArrayList;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
//import org.json.JSONObject;
//import org.json.JSONArray;
import javax.json.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import java.io.Writer;
import java.io.PrintWriter;


public class SFSServer {
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  public static String printRequestInfo(HttpExchange t) throws IOException{
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
    }
    finally {
      in.close();
    }
    return request;
  }

  public static void main(String[] args) throws Exception {

    InetAddress IP = InetAddress.getByName("localhost");
    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
    // server.createContext("/info", new InfoHandler());
    // server.createContext("/login", new GetHandler());
    server.createContext("/", new StaticRequestHandler());
    server.createContext("/getInodes.t", new InodeRequestHandler());
    server.createContext("/viewFile", new TextEditorHandler());
    server.createContext("/editFile", new TextEditorHandler());
    server.createContext("/saveFile", new TextEditorHandler());
    server.createContext("/loginhandler", new LoginHandler());
    server.createContext("/signuphandler", new LoginHandler());
    server.createContext("/newFile", new CreateInodeHandler());
    server.createContext("/newFolder", new CreateInodeHandler());
    server.setExecutor(null);
    server.start();
  }

  static class StaticRequestHandler implements HttpHandler {
    /**
      Use this handler for serving things that do not change such as css
      or javascript.
    **/
    public void handle(HttpExchange t) throws IOException {
      // Returns the body of GET/POST request
      URI uri = t.getRequestURI();
      printRequestInfo(t);
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
    }
  }

  static class TextEditorHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
      String requestBody = printRequestInfo(t);
      URI uri = t.getRequestURI();
      String path = uri.getPath();
      String query = uri.getQuery();
      System.out.println("Path: " + path);
      System.out.println("Query: " + query);
      Map<String, String> params = queryToMap(query);

      File file = new File("textEditor.html").getCanonicalFile();
      Document doc = Jsoup.parse(file, "UTF-8");
      OutputStream os = t.getResponseBody();
      String html = "";
      Writer writer = new PrintWriter(os);

      Headers h = t.getResponseHeaders();

      h.set("Content-Type", "text/html");
      t.sendResponseHeaders(200, 0);

      if(path.equals("/viewFile")){
        System.out.println("Path was /viewFile");
        doc.getElementById("textEditor").attr("readonly","true");
        String someText = "My awesome blog!";
        doc.getElementById("textEditor").text(someText);
        doc.getElementById("filename").text(params.get("file"));
        // doc.getElementById("saveLI").remove();
        doc.getElementById("saveFile").remove();
        html = doc.html();

      }

      if(path.equals("/editFile")){
        System.out.println("Path was /editFile");
        doc.getElementById("filename").text(params.get("file"));
        html = doc.html();
      }

      writer.write(html);
      writer.close();

      if(path.equals("/saveFile")){
        //save file
        System.out.println("Body: "+requestBody);
      }

      os.close();
    }
  }

  static class LoginHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
      System.out.println("We made it to the login handler");
      String requestBody = printRequestInfo(t);
      URI uri = t.getRequestURI();
      String path = uri.getPath();
      String query = uri.getQuery();
      System.out.println("Path: " + path);
      System.out.println("Query: " + query);

      Map<String, String> params = queryToMap(requestBody);
      System.out.println("Uname: "+params.get("uname"));
      System.out.println("PSW: "+params.get("psw"));
      String responseBody = "/home.html";
      Headers h = t.getResponseHeaders();

      h.set("Content-Type", String.format("text/plain; charset=%s", CHARSET));
      final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
      t.sendResponseHeaders(200, rawResponseBody.length);
      t.getResponseBody().write(rawResponseBody);
      t.close();

    }
  }

  static class CreateInodeHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException{
      String requestBody = printRequestInfo(t);
      URI uri = t.getRequestURI();
      String path = uri.getPath();
      String query = uri.getQuery();
      System.out.println("Path: " + path);
      System.out.println("Query: " + query);
      Map<String, String> params = queryToMap(requestBody);
      
    }
  }

  static class InodeRequestHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
      URI uri = t.getRequestURI();
      String path = uri.getPath();
      String query = uri.getQuery();
      System.out.println("Path: " + path);
      System.out.println("Query: " + query);
      Map<String, String> params = queryToMap(query);
      //TO DO: make response dynamic, dependent on current path and permissions
      //for now, return hard coded thing
      String responseBody = "";
      if(params.get("path").equals("/Home")){ //userHome remains generic on the client, server should resolve actual home using cookie
        // responseBody = "[{\"name\":\"file.txt\",\"type\":\"file\",\"permissions\":\"\",\"group\":\"\"}, {\"name\":\"blog\",\"type\":\"folder\",\"permissions\":\"\",\"group\":\"\"}]";
        JsonArray arr = Json.createArrayBuilder()
          .add(Json.createObjectBuilder()
          .add("name","file.txt")
          .add("type","file"))
          .add(Json.createObjectBuilder()
          .add("name","blog")
          .add("type","folder")).build();
        responseBody = arr.toString();
        System.out.println("Response Body: "+responseBody);
      }
      if(params.get("path").equals("/Home/blog")){
        // responseBody = "[{\"name\":\"blog.txt\",\"type\":\"file\",\"permissions\":\"\",\"group\":\"\"}]";
        JsonArray arr = Json.createArrayBuilder()
          .add(Json.createObjectBuilder()
          .add("name","blog1.txt")
          .add("type","file")).build();
        responseBody = arr.toString();
        System.out.println("Response Body: "+responseBody);
      }
      if(path.equals("Users/")){
       // responseBody = "[{\"name\":\"OtherUser\",\"type\":\"folder\",\"permissions\":\"\",\"group\":\"\"}]";
      }

      Headers h = t.getResponseHeaders();
      h.set("Content-Type", String.format("application/json; charset=%s", CHARSET));
      final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
      t.sendResponseHeaders(200, rawResponseBody.length);
      t.getResponseBody().write(rawResponseBody);
      t.close();
    }
  }


  private static Map<String, String> queryToMap(String query) {
    Map<String, String> result = new HashMap<>();
    for (String param : query.split("&")) {
        String[] entry = param.split("=");
        if (entry.length > 1) {
            result.put(entry[0], entry[1]);
        }else{
            result.put(entry[0], "");
        }
    }
    return result;
  }

  private static void redirectToLogin(HttpExchange t) throws IOException{
    File file = new File("login_signup.html").getCanonicalFile();

    Headers h = t.getResponseHeaders();
    h.set("Content-Type", "text/html");
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
}

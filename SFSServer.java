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
  private static Controller controller = new Controller();

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

    HttpServer server = HttpServer.create(new InetSocketAddress("10.2.14.222", 4000), 0);
    // HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
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
      String requestPath = uri.getPath();
      String query = uri.getQuery();
      System.out.println("Path: " + requestPath);
      System.out.println("Query: " + query);
      Map<String, String> params = queryToMap(query);
      System.out.println("PArams: "+params.toString());

      String path = params.get("file");
      Headers requestHeaders = t.getRequestHeaders();
      List<String> cookies = requestHeaders.get("Cookie");
      System.out.println(cookies.get(0));
      Map<String, String> cookieMap = queryToMap(cookies.get(0));
      String uname = cookieMap.get("uname");
      System.out.println("User: "+uname);

      if(path.contains("/Home")){
        path = path.replaceFirst("/Home","/users/"+uname);
      }
      if(path.contains("/Users")){
        path = path.replaceFirst("/Users","/users");
      }
      System.out.println("Parsed path: "+path);

      File file = new File("textEditor.html").getCanonicalFile();
      Document doc = Jsoup.parse(file, "UTF-8");
      OutputStream os = t.getResponseBody();
      String html = "";
      Writer writer = new PrintWriter(os);



      Headers h = t.getResponseHeaders();

      h.set("Content-Type", "text/html");
      t.sendResponseHeaders(200, 0);

      if(requestPath.equals("/viewFile")){
        //To do
        System.out.println("Path was /viewFile");
        doc.getElementById("textEditor").attr("readonly","true");
        String fileBody = controller.openFile(uname, path);
        doc.getElementById("textEditor").text(fileBody);
        doc.getElementById("filename").text(params.get("file"));
        // doc.getElementById("saveLI").remove();
        doc.getElementById("saveFile").remove();
        html = doc.html();

      }

      if(requestPath.equals("/editFile")){
        System.out.println("Path was /editFile");
        if(controller.canEdit(uname,path)){
          String fileBody = controller.openFile(uname,path);
          doc.getElementById("textEditor").text(fileBody);
          doc.getElementById("filename").text(path);
          html = doc.html();
        }
        else{
          return;
        }
      }

      writer.write(html);
      writer.close();

      if(requestPath.equals("/saveFile")){
        //save file
        // System.out.println("Body: "+requestBody);
        Map<String, String> postMap = queryToMap(requestBody);
        String fileBody = postMap.get("body");
        System.out.println("Body: "+fileBody);

        try{
          controller.editFile(uname,path,fileBody);
          System.out.println("After controller");
        }
        catch(Exception e){
          e.printStackTrace();
        }
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
      String responseBody = "Denied";
      Headers h = t.getResponseHeaders();

      ArrayList<String> corruptedFiles = null;
      List<String> cookies = null;

      if (path.equals("/signuphandler")) {
        controller.signUp(params.get("uname"), params.get("psw"));
      } else {
        corruptedFiles = controller.login(params.get("uname"), params.get("psw"));
      }

      if(corruptedFiles!=null){
        cookies = new ArrayList<String>();
        cookies.add("uname="+params.get("uname")+";");
        h.put("Set-Cookie", cookies);
        responseBody = "/home.html";
      }

      h.set("Content-Type", String.format("text/plain; charset=%s", CHARSET));
      final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
      t.sendResponseHeaders(200, rawResponseBody.length);
      t.getResponseBody().write(rawResponseBody);
      t.close();

    }
  }

  static class CreateInodeHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException{
      System.out.println("We get into the creat handler");

      String requestBody = printRequestInfo(t);
      URI uri = t.getRequestURI();
      String path = uri.getPath().trim();
      String query = uri.getQuery();
      System.out.println("Path: " + path);
      System.out.println("Query: " + query);

      Map<String, String> params = queryToMap(query);
      System.out.println(params.toString());

      String inode = "";
      try{
        inode = params.get("inode");
        inode = inode.replaceFirst("/Home","/users/user1");
      }
      catch(Exception e){
        e.printStackTrace();
      }

      System.out.println(inode);
      if(path.equals("/newFile")){
        System.out.println("adding file");
        controller.addFile("user1", inode, "");
      }
      if(path.equals("/newFolder")){
        System.out.println("Adding folder");
        controller.addDirectory("user1", inode);
      }
      String responseBody = "Good";
      Headers h = t.getResponseHeaders();
      h.set("Content-Type", String.format("text/plain; charset=%s", CHARSET));
      final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
      t.sendResponseHeaders(200, rawResponseBody.length);
      t.getResponseBody().write(rawResponseBody);
      t.close();
    }
  }

  static class InodeRequestHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
      URI uri = t.getRequestURI();
      String requestPath = uri.getPath();
      String query = uri.getQuery();
      System.out.println("Path: " + requestPath);
      System.out.println("Query: " + query);
      Map<String, String> params = queryToMap(query);
      //TO DO: make response dynamic, dependent on current path and permissions
      //for now, return hard coded thing
      Headers requestHeaders = t.getRequestHeaders();
      List<String> cookies = requestHeaders.get("Cookie");
      System.out.println(cookies.get(0));
      Map<String, String> cookieMap = queryToMap(cookies.get(0));
      String uname = cookieMap.get("uname");
      System.out.println("User: "+uname);
      String responseBody = "";
      String path = params.get("path");
      if(path.contains("/Home")){
        path = path.replaceFirst("/Home","/users/"+uname);
      }
      if(path.contains("/Users")){
        path = path.replaceFirst("/Users","/users");
      }
      System.out.println("Processed Path: "+path);

      ArrayList<ArrayList<String>> directoryContents = controller.openDirectory(uname, path);
      ArrayList<String> files = directoryContents.get(0);
      ArrayList<String> directories = directoryContents.get(1);
      System.out.println("Dir contents: "+directoryContents.toString());
      JsonArrayBuilder builder = Json.createArrayBuilder();

      for (String file : files) {
        builder.add(Json.createObjectBuilder()
        .add("name",file)
        .add("type", "file").build());
      }

      for (String folder : directories) {
        builder.add(Json.createObjectBuilder()
        .add("name",folder)
        .add("type", "folder").build());
      }
      JsonArray arr = builder.build();
      responseBody = arr.toString();
      System.out.println("Response body: "+responseBody);

      Headers h = t.getResponseHeaders();
      h.set("Content-Type", String.format("application/json; charset=%s", CHARSET));
      final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
      t.sendResponseHeaders(200, rawResponseBody.length);
      t.getResponseBody().write(rawResponseBody);
      t.close();
    }
  }


  private static Map<String, String> queryToMap(String query) {
    System.out.println("Query in queryToMap: "+query);
    Map<String, String> result = new HashMap<>();
    for (String param : query.split("&")) {
        System.out.println("Param: "+param);
        String[] entry = param.split("=");
        System.out.println("entry 0: "+entry[0]);
        System.out.println("entry 1: "+entry[1]);
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

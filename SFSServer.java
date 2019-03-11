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
    server.createContext("/newGroup", new GroupHandler());
    server.createContext("/getGroups", new GroupHandler());
    server.createContext("/getCorruptedFiles", new CorruptedFileHandler());
    server.createContext("/logOut", new LogOutHandler());
    server.createContext("/canViewFile", new TextEditorHandler());
    server.createContext("/canEditFile", new TextEditorHandler());
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
      // redirectToLogin(t);
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

  static class LogOutHandler implements HttpHandler{
    public void handle(HttpExchange t) throws IOException{
      URI uri = t.getRequestURI();
      String requestPath = uri.getPath();
      Headers responseHeaders = t.getResponseHeaders();
      List<String> cookies = new ArrayList<>();
      cookies.add("uname=");
      responseHeaders.put("Set-Cookie", cookies);
      File file = new File("login_signup.html").getCanonicalFile();

      responseHeaders.set("Content-Type", "text/html");
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

  static class CorruptedFileHandler implements HttpHandler{
    public void handle(HttpExchange t) throws IOException{
      System.out.println("We made it to the corrupted file handler");
      URI uri = t.getRequestURI();
      String requestPath = uri.getPath();
      System.out.println("Path: " + requestPath);
      Headers requestHeaders = t.getRequestHeaders();
      List<String> cookies = requestHeaders.get("Cookie");
      System.out.println(cookies.get(0));
      Map<String, String> cookieMap = queryToMap(cookies.get(0));
      String uname = cookieMap.get("uname");
      System.out.println("User: "+uname);

      String responseBody = "";
      Headers h = t.getResponseHeaders();
      ArrayList<String> fileList = controller.getCorruptedFiles(uname);

      JsonArrayBuilder builder = Json.createArrayBuilder();
      for(String file : fileList){
        builder.add(file);
      }
      JsonArray arr = builder.build();
      responseBody = arr.toString();
      System.out.println("Response body: "+responseBody);

      h.set("Content-Type", String.format("application/json; charset=%s", CHARSET));
      final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
      t.sendResponseHeaders(200, rawResponseBody.length);
      t.getResponseBody().write(rawResponseBody);
      t.close();
    }
  }

  static class TextEditorHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
      System.out.println("We get to editor handler");
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
      String originalPath = path;


      if(path.contains("/Home")){
        path = path.replaceFirst("/Home","/users/"+uname);
      }
      if(path.contains("/Users")){
        path = path.replaceFirst("/Users","/users");
      }
      System.out.println("Parsed path: "+path);

      File file = new File("textEditor.html").getCanonicalFile();
      Document doc = Jsoup.parse(file, "UTF-8");
      if(doc == null){
        System.out.println("Doc is null");
      }
      OutputStream os = t.getResponseBody();
      // String html = "";
      // Writer writer = new PrintWriter(os);

      Headers h = t.getResponseHeaders();

      if(requestPath.equals("/canViewFile")){
        String fileBody = controller.openFile(uname, path);
        String responseBody = "Denied";
        if(fileBody!=null){
          responseBody = "/viewFile?file="+originalPath+"#"+originalPath;
        }
        h.set("Content-Type", String.format("text/plain; charset=%s", CHARSET));
        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
        t.sendResponseHeaders(200, rawResponseBody.length);
        t.getResponseBody().write(rawResponseBody);
      }

      if(requestPath.equals("/canEditFile")){
        System.out.println("We get to can edit");
        String responseBody = "Denied";
        if(controller.canEdit(uname,path)){
          System.out.println("we can edit");
          responseBody = "/editFile?file="+originalPath+"#"+originalPath;
        }
        h.set("Content-Type", String.format("text/plain; charset=%s", CHARSET));
        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
        t.sendResponseHeaders(200, rawResponseBody.length);
        t.getResponseBody().write(rawResponseBody);
      }


      if(requestPath.equals("/viewFile")){
        String fileBody = controller.openFile(uname, path);
        h.set("Content-Type", "text/html");
        t.sendResponseHeaders(200, 0);
        System.out.println("Path was /viewFile");
        doc.getElementById("textEditor").attr("readonly","true");
        doc.getElementById("textEditor").text(fileBody);
        doc.getElementById("filename").text(params.get("file"));
        doc.getElementById("saveFile").remove();
        String html = "";
        Writer writer = new PrintWriter(os);
        html = doc.html();
        writer.write(html);
        writer.close();
      }

      if(requestPath.equals("/editFile")){
        System.out.println("We get to edit file");
        String fileBody = controller.openFile(uname, path);
        System.out.println("Path was /editFile");
        doc.getElementById("textEditor").text(fileBody);
        doc.getElementById("filename").text(path);
        String html = "";
        Writer writer = new PrintWriter(os);
        html = doc.html();
        writer.write(html);
        writer.close();
      }

      // writer.write(html);
      // writer.close();
      System.out.println("We serve the html");

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
      t.close();
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

      List<String> cookies = new ArrayList<>();
      boolean correctLogin = false;

      if (path.equals("/signuphandler")) {
        controller.signUp(params.get("uname"), params.get("psw"));
        cookies.add("uname="+params.get("uname")+";");
        h.put("Set-Cookie", cookies);
        responseBody = "/home.html";
      } else {
      correctLogin = controller.login(params.get("uname"), params.get("psw"));
      }

      if(correctLogin){
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

  static class GroupHandler implements HttpHandler{
    public void handle(HttpExchange t) throws IOException{
      System.out.println("We get into the group handler");
      String requestBody = printRequestInfo(t);
      System.out.println("Request body: "+requestBody);
      URI uri = t.getRequestURI();
      String requestPath = uri.getPath().trim();
      System.out.println("Request Path: "+requestPath);

      Headers requestHeaders = t.getRequestHeaders();
      List<String> cookies = requestHeaders.get("Cookie");
      System.out.println(cookies.get(0));
      Map<String, String> cookieMap = queryToMap(cookies.get(0));
      String uname = cookieMap.get("uname");

      if(requestPath.equals("/newGroup")){
        Map<String,String> inputMap = queryToMap(requestBody);
        String grpName = inputMap.get("grpName");
        String users = inputMap.get("users");

        ArrayList<String> userList = new ArrayList<String>();
        userList.add(uname);
        String[] arrUsers = users.split(",");
        for(String str : arrUsers){
          userList.add(str.trim());
        }
        controller.addToGroup(uname, grpName, userList);
      }

      System.out.println("Right before get groups");
      if(requestPath.equals("/getGroups")){
        System.out.println("We are getting groups");
        String responseBody = "";
        Headers h = t.getResponseHeaders();
        ArrayList<String> groupList = controller.getOwnerGroups(uname);
        System.out.println("Groups: "+groupList.toString());
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for(String group : groupList){
          builder.add(group);
        }
        JsonArray arr = builder.build();
        responseBody = arr.toString();
        System.out.println("Response body: "+responseBody);

        h.set("Content-Type", String.format("application/json; charset=%s", CHARSET));
        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
        t.sendResponseHeaders(200, rawResponseBody.length);
        t.getResponseBody().write(rawResponseBody);
      }
      t.close();
    }
  }

  static class CreateInodeHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException{
      System.out.println("We get into the create handler");

      String requestBody = printRequestInfo(t);
      URI uri = t.getRequestURI();
      String path = uri.getPath().trim();
      String query = uri.getQuery();
      System.out.println("Path: " + path);
      System.out.println("Query: " + query);

      Headers requestHeaders = t.getRequestHeaders();
      List<String> cookies = requestHeaders.get("Cookie");
      System.out.println(cookies.get(0));
      Map<String, String> cookieMap = queryToMap(cookies.get(0));
      String uname = cookieMap.get("uname");

      Map<String, String> params = queryToMap(query);
      System.out.println(params.toString());

      String inode = "";
      try{
        inode = params.get("inode");
        inode = inode.replaceFirst("/Home","/users/"+uname);
      }
      catch(Exception e){
        e.printStackTrace();
      }

      System.out.println(inode);
      if(path.equals("/newFile")){
        System.out.println("adding file");
        controller.addFile(uname, inode, "");
      }
      if(path.equals("/newFolder")){
        System.out.println("Adding folder");
        controller.addDirectory(uname, inode);
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

      Headers h = t.getResponseHeaders();
      h.set("Content-Type", String.format("application/json; charset=%s", CHARSET));
      final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
      System.out.println("We get past the raw resposne body");
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
        if(entry.length == 0){
          continue;
        }
        if (entry.length > 1) {
            result.put(entry[0], entry[1]);
        }else{
            result.put(entry[0], "");
        }
    }
    return result;
  }

  private static void redirectToLogin(HttpExchange t) throws IOException{
    System.out.println("We get in to redirectToLogin");
    boolean redirect = false;
    URI uri = t.getRequestURI();
    String requestPath = uri.getPath();
    if(requestPath.contains("login")){
      return;
    }
    Headers requestHeaders = t.getRequestHeaders();
    Map<String, String> cookieMap = null;
    String uname = "";
    List<String> cookies = requestHeaders.get("Cookie");

    if(cookies == null){
      System.out.println("We get to null check");
      redirect = true;
    }
    else{
      cookieMap = queryToMap(cookies.get(0));
      uname = cookieMap.get("uname");
      System.out.println("Uname "+uname);
      if(uname.equals("") && !requestPath.contains("login")){
        redirect = true;
      }
    }


    if(redirect){
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
}

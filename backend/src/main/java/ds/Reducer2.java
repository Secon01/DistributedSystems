package ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

public class Reducer2 
{   
    private static ServerSocket serverSocket;       // server socker 
    private static int port = 8001;                 // set port of reducer
    private static String hostname = "localhost";   // host name       
    // Reducer constructor
    Reducer2() throws IOException
    {
        serverSocket = new ServerSocket(port);                          // create server socket
        System.out.println("Reducer is listening on port " + port);
        while(true) {
            Socket connection = serverSocket.accept();                  // accept the incoming connections 
            new Thread(() -> {                                          // create new thread 
                try {
                    runServer(connection);                              // run server side of reducer
                } catch (IOException e) {
                    e.printStackTrace();                                // exception in thread 
                } catch (InterruptedException e) {
                    e.printStackTrace();                                // exception in thread 
                }
            }).start();
        }
    }
    // Deserializes json to request object 
    private Request deserializeRequest(String json)
    {
        return new Gson().fromJson(json, Request.class);
    }
    // Runs server side of reducer
    private void runServer(Socket connection) throws IOException, InterruptedException
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));      // get input stream in buffered reader
        OutputStream output = connection.getOutputStream();                                                 // get output stream from reducer's socket
        String requestLine = input.readLine();                                                              // set request line 
        if (requestLine == null || requestLine.isEmpty()) {
            return;                                                                                         // kill thread running
        }
        handleRequest(input, output);
        consumeRemainingRequest(input);
        connection.close();                                                                                 // close connection      
    }
    // Handles incoming requests for reducer
    private void handleRequest(BufferedReader in, OutputStream out) throws IOException
    {
        String jsonRequest = extractBody(in);                                        // extract json from http request body
        Request request = deserializeRequest(jsonRequest);
        Room room = (Room) request;
        System.out.println(room.getArea());
    }
    // Extracts body from http search room request
    private static String extractBody(BufferedReader in) throws IOException 
    {
    StringBuilder requestBody = new StringBuilder();    // build the body of request into string
    String line;                                        // represents each line of http header
    // Extract content length
    int contentLength = 0;
        while (!(line = in.readLine()).isEmpty()) {
            //System.out.println(line);                   // print http response
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.substring("content-length:".length()).trim());
            }
        }    
        // Read the body
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            requestBody.append(new String(buffer));
        }
        return requestBody.toString();
    }
    // Clear buffer in case anything is left
    private static void consumeRemainingRequest(BufferedReader in) throws IOException {
        while (in.ready()) {                                                            
            in.readLine();
        }
    }    
    public static void main(String[] args) throws IOException {
        new Reducer2();
    }
}
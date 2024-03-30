import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Worker {
    public static void main(String[] args) {
        try {
            // Connect to the Master server
            URL masterUrl = new URL("http://localhost:8080");
            HttpURLConnection masterConnection = (HttpURLConnection) masterUrl.openConnection();
            masterConnection.setRequestMethod("POST");
            masterConnection.setDoOutput(true);
            masterConnection.setRequestProperty("Content-Type", "application/json");

            // Construct the message to send
            String message = "{\"path\":\"/example/path\",\"id\":\"123456\"}";

            // Send the message to Master
            OutputStream masterOs = masterConnection.getOutputStream();
            masterOs.write(message.getBytes());
            masterOs.flush();
            masterOs.close();

            // Read the response from Master (optional)
            BufferedReader masterIn = new BufferedReader(new InputStreamReader(masterConnection.getInputStream()));
            StringBuilder masterResponse = new StringBuilder();
            String masterInputLine;
            while ((masterInputLine = masterIn.readLine()) != null) {
                masterResponse.append(masterInputLine);
            }
            masterIn.close();
            System.out.println("Response from Master: " + masterResponse.toString());

            // Once done with Master, you can proceed with your task

            // Now, let's connect to the Reducer server and send the results
            URL reducerUrl = new URL("http://localhost:9090");
            HttpURLConnection reducerConnection = (HttpURLConnection) reducerUrl.openConnection();
            reducerConnection.setRequestMethod("POST");
            reducerConnection.setDoOutput(true);
            reducerConnection.setRequestProperty("Content-Type", "application/json");

            // Construct the result message to send to Reducer
            String resultMessage = "{\"result\":\"your_result_here\"}";

            // Send the result message to Reducer
            OutputStream reducerOs = reducerConnection.getOutputStream();
            reducerOs.write(resultMessage.getBytes());
            reducerOs.flush();
            reducerOs.close();

            // Read the response from Reducer (optional)
            BufferedReader reducerIn = new BufferedReader(new InputStreamReader(reducerConnection.getInputStream()));
            StringBuilder reducerResponse = new StringBuilder();
            String reducerInputLine;
            while ((reducerInputLine = reducerIn.readLine()) != null) {
                reducerResponse.append(reducerInputLine);
            }
            reducerIn.close();
            System.out.println("Response from Reducer: " + reducerResponse.toString());

            // Disconnect from servers
            masterConnection.disconnect();
            reducerConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

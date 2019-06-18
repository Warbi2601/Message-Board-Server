// Solution to Week 13 Homework Exercise 4

// compile: javac -cp json-simple-1.1.1.jar;. MessageBoardClientV4.java
// run:     java  -cp json-simple-1.1.1.jar;. MessageBoardClientV4 <host name> <port number>

import java.io.*;
import java.net.*;
import java.util.*;        // required for Scanner
import org.json.simple.*;  // required for JSON encoding and decoding

public class MessageBoardClientV4 {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java MessageBoardClientV4 <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                // Parse user and build request
                Request req;
                Scanner sc = new Scanner(userInput);
                try {
                    switch (sc.next()) {
                    //case "login":
                        //req = new OpenRequest(sc.skip(" ").nextLine());
                        //break;
                    /*case "post":
                        req = new PublishRequest(sc.skip(" ").nextLine());
                        break;*/
                    /*case "read":
                        req = new GetRequest();
                        break;*/
                    /*case "subscribe":
                        req = new SubscribeRequest(sc.skip(" ").nextLine());
                        break;*/
                    case "quit":
                        req = new QuitRequest();
                        break;
                    default:
                        System.out.println("ILLEGAL COMMAND");
                        continue;
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("ILLEGAL COMMAND");
                    continue;
                }

                // Send request to server
                out.println(req);

                // Read server response; terminate if null (i.e. server quit)
                String serverResponse;
                if ((serverResponse = in.readLine()) == null)
                    break;

                // Parse JSON response, then try to deserialize JSON
                Object json = JSONValue.parse(serverResponse);
                Response resp;

                // Try to deserialize a success response
                if (SuccessResponse.fromJSON(json) != null)
                    continue;

                // Try to deserialize a list of messages
                if ((resp = MessageListResponse.fromJSON(json)) != null) {
                    for (Message m : ((MessageListResponse)resp).getMessages())
                        System.out.println(m);
                    continue;
                }

                // Try to deserialize an error response
                if ((resp = ErrorResponse.fromJSON(json)) != null) {
                    System.out.println(((ErrorResponse)resp).getError());
                    continue;
                }

                // Not any known response
                System.out.println("PANIC: " + serverResponse +
                                   " parsed as " + json);
                break;
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }
}

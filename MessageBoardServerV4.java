// Solution to Week 13 Homework Exercise 4

// compile: javac -cp json-simple-1.1.1.jar;. MessageBoardServerV4.java
// run:     java  -cp json-simple-1.1.1.jar;. MessageBoardServerV4 <port number>

import java.net.*;
import java.io.*;
import java.util.*;        // required for List and Scanner
import org.json.simple.*;  // required for JSON encoding and decoding

public class MessageBoardServerV4 {

    static class Clock {
        private long t;

        public Clock() { t = 0; }

        // tick the clock and return the current time
        public synchronized long tick() { return ++t; }
    }


    static class ClientHandler extends Thread {
        // shared message board
        private static List<Message> board = new ArrayList<Message>();
        private static List<User> users = new ArrayList<User>();

        // shared logical clock
        private static Clock clock = new Clock();

        // number of messages that were read by this client already
        private int read;

        // login name; null if not set
        private String login;
        private User currentUser;

        private Socket client;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) throws IOException {
            client = socket;
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(
                   new InputStreamReader(client.getInputStream()));
            read = 0;
            login = null;
        }

        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // tick the clock and record the current time stamp
                    long ts = clock.tick();

                    // logging request (+ login if possible) to server console 
                    if (login != null)
                        System.out.println(login + ": " + inputLine);
                    else
                        System.out.println(inputLine);

                    // parse request, then try to deserialize JSON
                    Object json = JSONValue.parse(inputLine);
                    JSONObject obj = (JSONObject)json;
                    Request req;

                    // login request? Must not be logged in already
                    if (login == null && OpenRequest.fromJSON(json) != null) {
                        // set login name
                        login = (String)obj.get("identity");
                        currentUser = new User(login, new ArrayList<User>());
                        users.add(currentUser);
                        // response acknowledging the login request
                        out.println(new SuccessResponse());
                        continue;
                    }

                    // publish request? Must be logged in
                    if (login != null && PublishRequest.fromJSON(json) != null) {
                        //String message = ((PublishRequest)req).getMessage();
                        JSONObject messageObj = (JSONObject)obj.get("message");
                        String message = (String)messageObj.get("body");
                        // synchronized access to the shared message board
                        synchronized (ClientHandler.class) {
                            // add message with login and timestamp
                            board.add(new Message(message, login, ts));
                        }
                        // response acknowledging the post request
                        out.println(new SuccessResponse());
                        continue;
                    }

                    // read request? Must be logged in
                    if (login != null && GetRequest.fromJSON(json) != null) {
                        List<Message> msgs;
                        // synchronized access to the shared message board
                        synchronized (ClientHandler.class) {
                            msgs = board.subList(read, board.size());
                        }
                        // adjust read counter
                        read = board.size();
                        // response: list of unread messages
                        out.println(new MessageListResponse(msgs));
                        continue;
                    }

                    // subscribe request? Must be logged in
                    if (login != null && SubscribeRequest.fromJSON(json) != null) {
                        String name = (String)obj.get("message");
                        
                        // synchronized access to the shared message board
                        synchronized (ClientHandler.class) {
                            // add message with login and timestamp
                            boolean wasFound = false;
                            for (User user : users) {
                                if(user.getName() == name) {
                                    wasFound = true;
                                    currentUser.addToSubList(user);
                                    continue;
                                }
                            }

                            if(!wasFound) {
                                out.println(new ErrorResponse("User not found"));
                            }

                            List<User> test = currentUser.getSubList();

                            for (User user : test) {
                                out.println(user.getName());    
                            }
                            
                        }

                    // unsubscribe request? Must be logged in
                    if (login != null && UnsubscribeRequest.fromJSON(json) != null) {
                        String name = (String)obj.get("message");
                        
                        // synchronized access to the shared message board
                        synchronized (ClientHandler.class) {
                            // add message with login and timestamp
                            boolean wasFound = false;

                            List<User> subs = currentUser.getSubList();

                            for (User user : subs) {
                                if(user.getName() == name) {
                                    wasFound = true;
                                    currentUser.deleteFromSubList(user);
                                    continue;
                                }
                            }

                            if(!wasFound) {
                                out.println(new ErrorResponse("User not found in your subscriber list"));
                            }
                        }
                        // response acknowledging the subscribe request
                        out.println(new SuccessResponse());
                        continue;
                    }

                    // quit request? Must be logged in; no response
                    if (login != null && QuitRequest.fromJSON(json) != null) {
                        in.close();
                        out.close();
                        return;
                    }

                    // error response acknowledging illegal request
                    out.println(new ErrorResponse("ILLEGAL REQUEST"));
                }
            } catch (IOException e) {
                System.out.println("Exception while connected");
                System.out.println(e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java MessageBoardServerV4 <port number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        
        try (
            ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println("Exception listening for connection on port " +
                               portNumber);
            System.out.println(e.getMessage());
        }
    }
}

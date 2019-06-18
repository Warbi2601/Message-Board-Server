FILES:

* Controller.java 	      needed for javafx project
* ErrorResponse.java          representation of error responses
* GetRequest.java 	      representation of request to read messages
* json-simple-1.1.1.jar       JSON library
* Main.java 		      Main Class where all code is ran from
* LoginRequest.java           representation of request to login
* MessageBoardClientV4.java   client
* Message.java                representation of messages
* MessageListResponse.java    representation of responses to read request
* PublisRequest.java            representation of request to post a message
* QuitRequest.java            representation of request to quit (client-s
* UI.java 		      where all UI code is
* User.java		      Houses user data
* Request.java                abstract super class of all responses
* README.txt                  this file
* Response.java               abstract super class of all responses
* SuccessResponse.java        representation of responses acknowledging success

COMPILE:
javac -cp json-simple-1.1.1.jar;. *.java

START CLIENT on localhost:
java -cp json-simple-1.1.1.jar;. MessageBoardClientV4 localhost 12345

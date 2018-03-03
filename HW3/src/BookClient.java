import java.net.Socket;
import java.util.Scanner;
import java.io.*;
import java.util.*;
public class BookClient {
  Socket server;
  Scanner din ;
  PrintStream pout ;
  public static void main (String[] args) throws IOException {
    String hostAddress;
    int tcpPort;
    int udpPort;
    int clientId;
    boolean isTCP;
    boolean running = true;

    if (args.length != 2) {
      System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
      System.out.println("\t(1) <command-file>: file with commands to the server");
      System.out.println("\t(2) client id: an integer between 1..9");
      System.exit(-1);
    }

    String commandFile = args[0];
    clientId = Integer.parseInt(args[1]);
    hostAddress = "localhost";
    tcpPort = 7000;// hardcoded -- must match the server's tcp port
    udpPort = 8000;// hardcoded -- must match the server's udp port
    isTCP = false; // default mode is UDP

    try {
        Scanner sc = new Scanner(new FileReader(commandFile));
        BookClient client = new BookClient();

        while(sc.hasNextLine() && running) {
          String cmd = sc.nextLine();
          String[] tokens = cmd.split(" ");

          if (tokens[0].equals("setmode")) {
            // TODO: set the mode of communication for sending commands to the server
            if(tokens[1].equals("T"))
              isTCP = true;
            else
              isTCP = false;
          }
          else if (tokens[0].equals("borrow")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
            String studentName = tokens[1];
            String bookName = "";
            for(int i = 2; i < tokens.length - 1; i++)
              bookName += tokens[i] + " ";
            bookName += tokens[tokens.length - 1];
            client.borrow(isTCP, studentName, bookName, hostAddress, tcpPort, udpPort);
          } else if (tokens[0].equals("return")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
            String recordID = tokens[1];
            client.returnBook(isTCP, recordID, hostAddress, tcpPort, udpPort);
          } else if (tokens[0].equals("inventory")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
            client.inventory(isTCP, hostAddress, tcpPort, udpPort);
          } else if (tokens[0].equals("list")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
            String studentName = tokens[1];
            client.listStudent(isTCP, studentName, hostAddress, tcpPort, udpPort);
          } else if (tokens[0].equals("exit")) {
            // TODO: send appropriate command to the server
            client.exit(isTCP, hostAddress, tcpPort, udpPort);
            running = false;
          } else {
            System.out.println("ERROR: No such command");
          }
        }
    } catch (FileNotFoundException e) {
	e.printStackTrace();
    }
  }

  public void getTCPPort(String hostAddress, int port) throws IOException {
    server = new Socket(hostAddress, port);
    din = new Scanner(server.getInputStream());
    pout = new PrintStream(server.getOutputStream());
  }

  public void borrow(boolean isTCP, String studentName, String bookName, String hostAddress, int tcpPort, int udpPort) throws IOException {
    if(isTCP) {
      getTCPPort(hostAddress, tcpPort);
      pout.println("borrow_" + studentName + "_" + bookName);
      pout.flush();
      String message = din.nextLine();
      System.out.println(message);
    }
    else {
      // UDP code

    }
  }

  public void returnBook(boolean isTCP, String recordID, String hostAddress, int tcpPort, int udpPort) throws IOException {
    if(isTCP) {
      getTCPPort(hostAddress, tcpPort);
      pout.println("return_" + recordID);
      pout.flush();
      String message = din.nextLine();
      System.out.println(message);
    }
    else {
      // UDP code
    }
  }

  public void inventory(boolean isTCP, String hostAddress, int tcpPort, int udpPort) throws IOException {
    if(isTCP) {
      getTCPPort(hostAddress, tcpPort);
      pout.println("inventory");
      pout.flush();
      String message = "";
      while(din.hasNextLine()) {
        message += din.nextLine() + "\n";
      }
      System.out.println(message.substring(0, message.length() - 2));
    }
    else {
      // UDP code
    }
  }

  public void listStudent(boolean isTCP, String studentName, String hostAddress, int tcpPort, int udpPort) throws IOException {
    if(isTCP) {
      getTCPPort(hostAddress, tcpPort);
      pout.println("list_" + studentName);
      pout.flush();
      String message = din.nextLine();
      System.out.println(message);
    }
    else {
      // UDP code
    }
  }

  public void exit(boolean isTCP, String hostAddress, int tcpPort, int udpPort) throws IOException {
    if(isTCP) {
      getTCPPort(hostAddress, tcpPort);
      pout.println("exit");
      pout.flush();
    }
  }


}

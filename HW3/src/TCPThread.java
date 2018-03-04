import java.io.*;
import java.util.*;
import java.net.*;

public class TCPThread extends Thread {
    Socket theClient;
    Library library;

    public TCPThread(Socket s, Library l) {
        theClient = s;
        library = l;
    }

    public void run() {
        Scanner sc = null;
        try {
            sc = new Scanner(theClient.getInputStream());
            PrintWriter pout = new PrintWriter(theClient.getOutputStream());
            //String command = sc.nextLine();
            while(sc.hasNextLine()) {
                String command = sc.nextLine();
                System.out.println("received: " + command);
                Scanner st = new Scanner(command);
                String[] cmdArray = command.split("_");
                String tag = cmdArray[0];
                if (tag.equals("borrow")) {
                    String studentName = cmdArray[1];
                    String bookName = cmdArray[2];
                    int recordID = library.borrow(studentName, bookName);
                    if (recordID > 0)
                        pout.println("Your request has been approved, " + recordID + " " + studentName + " " + bookName);
                    else
                        pout.println("Request Failed - Book not available");
                    pout.flush();
                } else if (tag.equals("return")) {
                    int recordID = Integer.parseInt(cmdArray[1]);
                    String bookName = library.returnBook(recordID);
                    if (!bookName.equals(""))
                        pout.println(recordID + " is returned");
                    else
                        pout.println(recordID + " not found, no such borrow record");
                    pout.flush();
                } else if (tag.equals("list")) {
                    String studentName = cmdArray[1];
                    String books = library.booksForStudent(studentName);
                    if (!books.equals(""))
                        pout.println(books);
                    else
                        pout.println("No record found for " + studentName);
                    pout.flush();
                } else if (tag.equals("inventory")) {
                    String inv = library.getInventory();
                    pout.println(inv);
                    pout.flush();
                } else if (tag.equals("exit")) {
                    library.updateInventory();
                }
                //command = sc.nextLine();
            }
        } catch(IOException e) {
            System.err.println(e);
        } finally {
            sc.close();
        }
    }
}

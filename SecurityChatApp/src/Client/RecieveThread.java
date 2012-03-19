/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

/**
 *
 * @author mazz
 */
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.util.StringTokenizer;
//import java.lang.*;

public class RecieveThread implements Runnable
{
    Thread t;
    Socket socket;

    BufferedReader inFromServer;
    PrintWriter pw;
    javax.swing.JTextArea jTextArea;
    MainWindow parentRef;
    StringTokenizer st;
    
    RecieveThread(Socket s, MainWindow parent) throws Exception
    {
        socket = s;
        t= new Thread(this);
        inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //pw = new PrintWriter(socket.getOutputStream(), true);
        parentRef = parent;
        t.start();
    }
//    RecieveThread(Socket s, javax.swing.JTextArea jTextArea1) throws Exception
//    {
//        socket = s;
//        t= new Thread(this);
//        inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        jTextArea = jTextArea1;
//        t.start();
//    }
    @Override
    public void run()
    {
        String modifiedSentence;
        try
        {
            modifiedSentence = inFromServer.readLine();
            while(modifiedSentence!=null)
            {
//                System.out.println(modifiedSentence);
                if(!modifiedSentence.equals(""))
                {
                    st = new StringTokenizer(modifiedSentence);
                    String test = st.nextToken();
                    System.out.println(test);
                    if(test.equals("/FriendsList"))
                    {
                        //parentRef.updateList(modifiedSentence);
//                        System.out.println(modifiedSentence);
                    }
                    else if(test.equals("/ReturnSocket"))
                    {
                        //System.out.println("herezor");
                        //pw.println(parentRef.welcomeSocket.getLocalPort() + " ");
//                        System.out.println(modifiedSentence);
                    }
                    else if(test.equals("/AddChatContact"))
                    {
                        //chatW.addChatContact(modifiedSentence);
//                        System.out.println(modifiedSentence);
                    }
                    else if(test.equals("/StartChat"))
                    {
                        //chatW.addChatContact(modifiedSentence);
//                        System.out.println(modifiedSentence);
                    }
                    else
                    {
                        try
                        {
                            parentRef.consoleMessage(modifiedSentence + "");
                        }
                        catch(Exception e)
                        {
                            System.out.println("Recieved thread failed to write" + e.getMessage());
                        }
                    }
                }
                modifiedSentence = inFromServer.readLine();
            }
            System.out.println("Null caught in recieved thread");
        }
        catch(Exception e)
        {
            System.out.println("Recieved thread failed " + e.getMessage());
        }
    
    }
}

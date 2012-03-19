/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

/**
 *
 * @author mazz
 */
public class ServerThread implements Runnable
{
    Thread t;
    String uname = "";
    Socket connectionSocket = null;
    BufferedReader inFromClient = null;
    PrintWriter outToClient = null;
    StringTokenizer st;
    
    MainWindowServer mainWindow = null;
    
    ServerThread(Socket s, MainWindowServer mWindow)
    {
        mainWindow = mWindow;
        connectionSocket = s;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run()
    {
        try
        {
            inFromClient =
               new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    //                DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
    //                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
            String clientSentence;
            String capitalizedSentence;
    //            while ((clientSentence = inFromClient.readLine()) != null)
            while(true)
            {
                try
                {
                    clientSentence = inFromClient.readLine();
                    st = new StringTokenizer(clientSentence);
                    String command = st.nextToken();
                    //mainWindow.consoleMessage(command);
                    if(command.equals("/UserName"))
                    {
                        uname = st.nextToken();
                        String ip = st.nextToken();
                        int port = Integer.parseInt(st.nextToken());
                        String masterKey = st.nextToken();
                        mainWindow.consoleMessage("Welcome "+uname +" at " +ip+" with open port "+ port+" shhhh... "+ masterKey+" :)");
                        mainWindow.usersLoggedOn.add(mainWindow.new User(uname,ip,port, masterKey));
                    }
                    else if (command.equals("/Logout"))
                    {
                        break;
                    }
                    else
                    {
                        if(uname.equals(""))
                        {
                            mainWindow.consoleMessage("Received: " + clientSentence);
                        }
                        else
                        {
                            mainWindow.consoleMessage(uname+": " + clientSentence);
                        }
                        //System.out.println("Received: " + clientSentence);
                        capitalizedSentence = clientSentence.toUpperCase() + '\n';
                        outToClient.println(capitalizedSentence);
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Server thread error" + e.getMessage());
                    break;
                }
            }            
        }
        catch(Exception e)
        {
            
        }
    }
}

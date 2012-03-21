/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

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
                        String KMstr = st.nextToken();
                        String KUstr = st.nextToken();
                        byte[] KMarr = DatatypeConverter.parseBase64Binary(KMstr);
                        SecretKeySpec masterKey = new SecretKeySpec(KMarr, "AES");
                        
                        byte[] KUarr = DatatypeConverter.parseBase64Binary(KUstr);
//                      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(KUarr);
                        PublicKey KU = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(KUarr));


                        mainWindow.consoleMessage("Welcome "+uname +" at " +ip+" with open port "+ port+" master key "+ KMstr+" and public key "+KUstr+" :)");
                        
                        mainWindow.usersLoggedOn.add(mainWindow.new User(uname,ip,port, masterKey, KU));
                        
                        
                        outToClient.println("/Message " + "Server " + "Thanks for logging in "+uname +"!");
                        
                        String temp = "";
                        for(MainWindowServer.User friend : mainWindow.usersLoggedOn)
                        {
                            if(!friend.userName.equals(uname))
                            {
                                byte[] Kshared = friend.publicKey.getEncoded();
                                String KMstr2 = DatatypeConverter.printBase64Binary(Kshared);
                                
                                temp = temp + " " + friend.userName + " " + friend.ip + " " + friend.portNo +" "+ KMstr2;
                            }
                        }
                                
                        outToClient.println("/FriendsList" + temp);
                        
                        //mainWindow.consoleMessage("/FriendsList" + temp);
                        
                        mainWindow.consoleMessage("Sent list update to " + uname);
                    }
                    else if(command.equals("/Phase1"))
                    {
                        mainWindow.consoleMessage("Phase1 request from " + uname);
                        //mainWindow.consoleMessage("clientSentence "+clientSentence);
                        String encrypted = clientSentence.substring(8);
                        //mainWindow.consoleMessage("encrypted "+ encrypted);
                        byte[] cipherText = DatatypeConverter.parseBase64Binary(encrypted);
                        
                        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                        Key aKey = null;
                        
                        for(MainWindowServer.User friend : mainWindow.usersLoggedOn)
                        {
                            if(friend.userName.equals(uname))
                            {
                               aKey = friend.sharedKey;
                            }
                        }
                        
                        byte[] cipherTemp = aKey.getEncoded();
                        String s = DatatypeConverter.printBase64Binary(cipherTemp);
                        
                        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                        IvParameterSpec ivspec = new IvParameterSpec(iv);
                        cipher.init(Cipher.DECRYPT_MODE, aKey, ivspec);
                        
                        
                        byte[] decryptedText = cipher.doFinal(cipherText);
                        
                        mainWindow.consoleMessage("Phase2 encrypted " + s);
                        
                        String text = DatatypeConverter.printBase64Binary(decryptedText);
                        
                        StringTokenizer strT = new StringTokenizer(text);
                        
                        mainWindow.consoleMessage("Phase2 decrypted " + text);
                        
                        String friendName = strT.nextToken();
                        mainWindow.consoleMessage("Phase2 friendName " + friendName);
                        
                        String nonce = text.substring(friendName.length()+1);
                        
                        MainWindowServer.User b = null;
                        
                        for(MainWindowServer.User friend : mainWindow.usersLoggedOn)
                        {
                            if(!friend.userName.equals(friendName))
                            {
                               b = friend;
                            }
                        }
                        
                        Key sharedKey = mainWindow.generateSharedKey();
                        
                        byte[] KsharedArr = sharedKey.getEncoded();
                        String sharedKeyString = DatatypeConverter.printBase64Binary(KsharedArr);
                        
                        String bEncrypt = uname + " " + sharedKeyString;
                        String bEncrypted = mainWindow.encrypt(bEncrypt, b.sharedKey);
                        mainWindow.consoleMessage("Phase2 nonce " + nonce);
                        String phase2 = nonce + " " + sharedKeyString +" "+ friendName + " " + bEncrypted;
                        
                        String phase2Encrypted = mainWindow.encrypt(phase2, aKey);
                        
                        outToClient.println("/Phase2 " + phase2Encrypted);
                        
                        mainWindow.consoleMessage("Phase2 sent to " + uname);
                        
                    }
                    else if(command.equals("/ListUpdate"))
                    {
                        String temp = "";
                        String userName = st.nextToken();
                        
                        for(MainWindowServer.User friend : mainWindow.usersLoggedOn)
                        {
                            if(!friend.userName.equals(uname))
                            {
                                byte[] Kshared = friend.publicKey.getEncoded();
                                String KMstr2 = DatatypeConverter.printBase64Binary(Kshared);
                                
                                temp = temp + " " + friend.userName + " " + friend.ip + " " + friend.portNo +" "+ KMstr2;
                            }
                        }
                                
                        outToClient.println("/FriendsList" + temp);
                                
                        //outToClient.println("/FriendsList" + temp);
                        mainWindow.consoleMessage("Sent list update to " + userName);
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
                        capitalizedSentence = clientSentence.toUpperCase();
                        outToClient.println("/Message " + "Server " + capitalizedSentence);
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Server thread error" + e.getMessage());
                    e.printStackTrace();
                    
                    for(MainWindowServer.User friend : mainWindow.usersLoggedOn)
                    {
                        if(!friend.userName.equals(uname))
                        {
                            mainWindow.usersLoggedOn.remove(friend);
                        }
                    }
                    break;
                }
            }            
        }
        catch(Exception e)
        {
            
        }
    }
}

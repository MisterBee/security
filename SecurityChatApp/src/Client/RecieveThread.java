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
import java.security.Key;
import java.security.PrivateKey;
import javax.swing.*;
import java.util.StringTokenizer;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
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
                        parentRef.updateList(modifiedSentence);
//                        System.out.println(modifiedSentence);
                    }
                    else if(test.equals("/Phase2"))
                    {
                        parentRef.consoleMessage("Recieved phase2 from server");
                        
                        String phase2Encrypted = modifiedSentence.substring(8);
                        byte[] cipherText = DatatypeConverter.parseBase64Binary(phase2Encrypted);
                        
                        Cipher cipher = Cipher.getInstance("AES");
                        
//                        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
//                        IvParameterSpec ivspec = new IvParameterSpec(iv);
                        cipher.init(Cipher.DECRYPT_MODE, parentRef.masterKey);//, ivspec);
                        
                        byte[] decryptedText = cipher.doFinal(cipherText);
                        
                        String text = new String(decryptedText);
                        parentRef.consoleMessage("Successfully decrypted text: "+ text);
                        
                        StringTokenizer strT = new StringTokenizer(text);
                        
                        String nonce = strT.nextToken();
                        
                        parentRef.consoleMessage("Nonces "+ nonce+" and "+parentRef.nonce);
                        if(nonce.equals(parentRef.nonce))
                        {
                            String sharedKeyString = strT.nextToken();
                            String friendName = strT.nextToken();
                        
                            String bCipherText = text.substring(nonce.length()+ sharedKeyString.length()+friendName.length() + 3);
                            
                            byte[] KAB = sharedKeyString.getBytes();
                            SecretKeySpec sharedKey = new SecretKeySpec(KAB, "AES");
                            MainWindow.Friend b = null;
                            for (MainWindow.Friend temp : parentRef.friendsLoggedOn) 
                            {
                                if (temp.userName.equals(friendName)) 
                                {
                                    temp.sharedKey = sharedKey;
                                    b = temp;
                                    break;
                                }
                            }
                            
                            parentRef.consoleMessage("Start of Phase3 creation");
                            
                            String nonceB = parentRef.generateNonce();
                            String toEncrypt = nonceB + " " + bCipherText;
                            
                            PrivateKey KRA = parentRef.keyPair.getPrivate();
                            System.out.println("it works");
                            String phase3 = "/Phase3 "+ parentRef.userName + " "+ parentRef.publicEncrypt(toEncrypt, KRA);
                            System.out.println("but not here" + phase3);
                            b.printWriter.println(phase3);
                            parentRef.consoleMessage("Phase3 sent to " + b.userName);
                            
                        }
                        else
                        {
                            parentRef.consoleMessage("Nonce incorrect, suspicious activity detected.");
                        
                        }
                        
                    }
                    else if(test.equals("/Phase3"))
                    {
                        // get the name of the sender
                        String friend = st.nextToken();
                        // debug statement
                        parentRef.consoleMessage("Recieved phase3 from " + friend);
                        // extract the "signed" string from the message
                        String phase3Encrypted = modifiedSentence.substring(8 + friend.length()+ 1);
                        
                        MainWindow.Friend b = null;
                        for (MainWindow.Friend temp : parentRef.friendsLoggedOn) 
                        {
                            if (temp.userName.equals(friend)) 
                            {
                                b = temp;
                                break;
                            }
                        }
                        
                        
                        // test "signature", by decrypting
                        String myCipherTextWithNonceB = parentRef.publicDecrypt(phase3Encrypted, b.publicKey);
                        // extract and store nonceB
                        StringTokenizer st2 = new StringTokenizer(myCipherTextWithNonceB);
                        String thisNonce = st2.nextToken();
                        //remove nonceB from the string.
                        String myCipherText = modifiedSentence.substring(thisNonce.length());
                        
                        String myDecryptedText = parentRef.decrypt(myCipherText);
                        StringTokenizer st3 = new StringTokenizer(myDecryptedText);
                        String friendName = st3.nextToken();
                        if(friendName.equals(friend))
                        {
                           String KABstr = modifiedSentence.substring(thisNonce.length() +1);
                        }
                        else  parentRef.consoleMessage("Look out...info Ninjas are about!");
                        
                        
                        
                    }
                    else if(test.equals("/Message"))
                    {
                        String friend = st.nextToken();
                        String leftOverMessage = modifiedSentence.substring(8 + friend.length() + 1);
                        parentRef.consoleMessage(friend +": " +leftOverMessage);
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
                    e.printStackTrace();
        }
    
    }
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author rmazzolini
 */
public class MainWindow extends javax.swing.JFrame implements Runnable
{
    Thread t;
    
    Socket connectionSocket = null;
    ServerSocket welcomeSocket = null;
    Socket clientSocket = null;
    PrintWriter outToServer;
    
    String userName = "Alice";
    String serverIp = "localhost";
    String nonce = "";
    int port = 6889;
    Key masterKey;
    KeyPair keyPair;
    
    public class Friend
    {
        String userName;
        String ip;
        int portNo;
        Key sharedKey;
        PublicKey publicKey;
        public boolean hasSecureConnection;
        public Socket contactSocket;
        public RecieveThread recieve;
        public PrintWriter printWriter;
        
        Friend(String userName, String userIp, int userPortNo, Key sharedKey, PublicKey publicKey, MainWindow parent)
        {
            this.userName = userName;
            this.ip = userIp;
            this.portNo = userPortNo;
            this.sharedKey = sharedKey;
            this.publicKey = publicKey;
            this.hasSecureConnection = false;
            
            try
            {
                contactSocket = new Socket(this.ip, this.portNo);
                printWriter = new PrintWriter(contactSocket.getOutputStream(), true);
                
                recieve = new RecieveThread(contactSocket, parent);
            }
            catch(Exception e)
            {
                consoleMessage("Contact failed " + e.getMessage());
            }
        }
    }
    
    ArrayList<Friend> friendsLoggedOn = new ArrayList();
    
    /**
     * Creates new form MainWindow
     */
    public MainWindow() 
    {
        initComponents();
        
    }
    //this is an infinite loop that tries to get connections - probably need to make this another thread
    @Override
    public void run()
    {
        try
        {
            boolean inloop = false;
            
            while (true)
            {
                if(!inloop)
                {
                    //consoleMessage("Welcome to the server!\n");
                    inloop = true;
                }
                
                try
                {
                   clientSocket = welcomeSocket.accept();
                   //jTextArea1.append("User Logging in\n");
                   RecieveThread st =  new RecieveThread(clientSocket, this);
                }
                catch(Exception e)
                {
                    consoleMessage("User Failed Logging in");
                }
            }
         }
         catch(Exception e)
         {
             consoleMessage("Server setup failed " + e.getMessage());
         }
         
    }
    
    public void login()
    {
        try
        {
            //create the welcome socket
            welcomeSocket = new ServerSocket(0);
        }
        catch(Exception e)
        {
            consoleMessage("Server setup failed " + e.getMessage());
        }
        try
        {
            
            
            
            clientSocket = new Socket(serverIp, port);
            RecieveThread st =  new RecieveThread(clientSocket, this);
            
            try
            {

                outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
                //System.out.println("Alice");
                //System.out.println(clientSocket.getLocalAddress().getHostName());

                masterKey = generateMasterKey();
                //System.out.println("master uses " +masterKey.getAlgorithm());
                byte[] KMarr = masterKey.getEncoded();
                String KMstr = DatatypeConverter.printBase64Binary(KMarr);
                
                keyPair = generateKeyPair();
                PublicKey KU = keyPair.getPublic();
                //System.out.println("public uses " +KU.getAlgorithm());
                byte[] KUarr = KU.getEncoded();
                String KUstr = DatatypeConverter.printBase64Binary(KUarr);
                consoleMessage("Loggin in to server with masterkey " +KMstr+" and public key "+KUstr);
                //System.out.println(ssThread.welcomeSocket.getLocalPort() + " /");
                outToServer.println("/UserName " + userName + " " +clientSocket.getLocalAddress().getHostAddress() +" "+ welcomeSocket.getLocalPort() + " " + KMstr +" "+KUstr);
                //outToServer.println("/UserName " + "Alice" + " " +clientSocket.getLocalAddress().getHostAddress() +" "+ ssThread.welcomeSocket.getLocalPort());
    //            sleeper.t.start();
    //            outToServer.println("/UserName " + userName + " " +clientSocket.getLocalAddress().getHostAddress() +" "+ ssThread.welcomeSocket.getLocalPort());
    //            outToServer.println(loginString);
                
                t = new Thread(this);
                t.start();
            }
            catch(Exception e)
            {
                consoleMessage("Output failed " + e.getMessage());
            }
            
        }
        catch(Exception e)
        {
            consoleMessage("Failed to connect to server");
        }
        
    }
    public Key generateMasterKey() throws Exception
    {
        System.out.println("\nStart generating AES key");
        KeyGenerator keyG = KeyGenerator.getInstance("AES");
        keyG.init(128);
        Key key = keyG.generateKey();
        System.out.println("Finish Generating key");
        return key;
    }
    public KeyPair generateKeyPair() throws Exception
    {
        System.out.println("\nStart generating RSA key");
        KeyPairGenerator keyG = KeyPairGenerator.getInstance("RSA");
        keyG.initialize(1024);
        KeyPair keys = keyG.generateKeyPair();
        System.out.println("Finish generating RSA key");
        return keys;
    }
    public void consoleMessage(String input)
    {
        jTextArea1.append( input + "\n");
    }
    public void serverMessage(String text)
    {
        if(outToServer!=null)
        {
            outToServer.println(text);
        }
        else
        {
            consoleMessage("Not connected to server");
        }
    }
    public void friendMessage(String text, Friend friend)
    {
              
        if(friend.printWriter!=null)
        {
            friend.printWriter.println("/Message " + userName + " " + text);
        }
        else
        {
            consoleMessage("Not connected to friend");
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("Bonk here");
        jTextArea2.setToolTipText("Type here to chat to your paramour");
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setEnabled(false);
        jTextArea2.setOpaque(false);
        jTextArea2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                DropText(evt);
            }
        });
        jTextArea2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Entered(evt);
            }
        });
        jScrollPane2.setViewportView(jTextArea2);
        jTextArea2.getAccessibleContext().setAccessibleName("Input");

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jList1);

        jLabel1.setText("Contact List:");

        jLabel2.setText("Chat Window:");

        jCheckBox1.setText("Show Encrypted");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Update Contacts");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Get Secure Connection");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setText("Log into Server");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jButton1)
                            .addComponent(jCheckBox1)
                            .addComponent(jButton2)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(202, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DropText(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_DropText
        // TODO add your handling code here:
        jTextArea2.setText("");
    }//GEN-LAST:event_DropText
    
    private void Entered(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Entered
        // TODO add your handling code here:
        int keyCode = evt.getKeyCode();
        if(keyCode==KeyEvent.VK_ENTER)
        {
            String input = jTextArea2.getText().trim();
            jTextArea1.append(userName+": " + input + "\n");
            try
            {
                int i = jList1.getSelectedIndex();
                ListModel md = jList1.getModel();
                
                String s = md.getElementAt(i).toString();
                //consoleMessage(s);
                boolean sent = false;
                if(s!=null)
                    for (Friend temp : friendsLoggedOn) 
                    {
                        if (s.equals("Server")) 
                        {
                            serverMessage(input);
                            sent = true;
                            break;
                        }
                        else if (temp.userName.equals(s)) 
                        {
                            friendMessage(input, temp);
                            //new ChatWindow(temp.contactName, temp.ipAdress, temp.port, userName, this);
                            sent = true;
                            break;
                        }
                    }
                if(!sent)
                {
                    serverMessage(input);
                }
                
            }
            catch(Exception e)
            {
                jTextArea1.append("Couldn't send your message, try selecting someone to talk to.\n");
            }
            jTextArea2.setText("");
        }
    }//GEN-LAST:event_Entered

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        Login s = new Login(this);
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged

//        int minIndex = evt.getFirstIndex();
//        int maxIndex = evt.getLastIndex();
        
        if (evt.getValueIsAdjusting()) {
            int i = jList1.getSelectedIndex();
            ListModel md = jList1.getModel();
            String s = md.getElementAt(i).toString();
            
            if(s!=null)
            {
                jTextArea2.setEnabled(false);
                jTextArea2.setEditable(false);
                jTextArea2.setOpaque(false);
                jButton2.setEnabled(false);
                
                if (s.equals("Server")) 
                {
                    jTextArea2.setEnabled(true);
                    jTextArea2.setEditable(true);
                    jTextArea2.setOpaque(true);
                    jButton2.setEnabled(false);
                    
                }
                else
                {
                    for (Friend temp : friendsLoggedOn) 
                    {
                        if (temp.userName.equals(s)) 
                        {
                            if(temp.hasSecureConnection)
                            {
                                jTextArea2.setEnabled(true);
                                jTextArea2.setEditable(true);
                                jTextArea2.setOpaque(true);
                                jButton2.setEnabled(false);
                            }
                            else
                            {
                                jTextArea2.setEnabled(false);
                                jTextArea2.setEditable(false);
                                jTextArea2.setOpaque(false);
                                jButton2.setEnabled(true);
                            }
                            break;
                        }
                    }
                }
            }
        }
//        for (int i = minIndex; i <= maxIndex; i++)
//        {
//
//        }
//        System.out.println(minIndex+" "+maxIndex);
    }//GEN-LAST:event_jList1ValueChanged

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed
     public void updateList(String friendList)
    {
        StringTokenizer st = new StringTokenizer(friendList);
        //System.out.println(friendList);
        st.nextToken();
        String name = "";
        String ipAddress = "";
        int portNumber = 0;
        //String sharedKey = "";
        String publicKey = "";
        
        DefaultListModel listModel;
        listModel = new DefaultListModel();
        listModel.addElement("Server");
        
        Friend test;
        while(st.hasMoreTokens())
        {
            name = st.nextToken();
            ipAddress = st.nextToken();
            portNumber = Integer.parseInt(st.nextToken());
            //sharedKey = st.nextToken();
            publicKey = st.nextToken();
            
            byte[] KUarr = DatatypeConverter.parseBase64Binary(publicKey);
//                      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(KUarr);
            try
            {
                PublicKey KU = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(KUarr));
                consoleMessage(name+" "+ipAddress+" "+portNumber+" "+KU);
            
                test = new Friend(name, ipAddress, portNumber,null, KU , this);

                friendsLoggedOn.add(test);
                listModel.addElement(name);
            }
            catch(Exception e)
            {
                consoleMessage("Couldnt get key" +e.getMessage());
            }
            
            
            
            
        }
        jList1.setModel(listModel);// = new JList(listModel);
    }
    public void sendListRequest() throws Exception
    {
//        System.out.println("-"+userName+ "-");
//        if(!userName.equals("")&&userName!=null)
            outToServer.println("/ListUpdate " + userName);
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try
        {
            sendListRequest();
        }
        catch(Exception e)
        {
            System.out.println("Sending list error");
        }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    public String generateNonce() throws Exception
    {
        // creates random number generator
        SecureRandom rand = new SecureRandom();
        //sets the length of the nonce
        byte[] nonceArr = new byte[1];
        //gets a random number
        rand.nextBytes(nonceArr);
        //converts to a string in HexaDecimal format
        String Nonce = DatatypeConverter.printHexBinary(nonceArr);
        return Nonce;
    }
    
    public String encrypt(String toEncrypt) throws Exception
    {
        byte[] plainText = toEncrypt.getBytes(); 
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, masterKey);
        byte[] cipherText = cipher.doFinal(plainText);
        String s = DatatypeConverter.printBase64Binary(cipherText);
        
        return s;
    }
    
        public String encrypt(String toEncrypt, Key master) throws Exception
    {
        byte[] plainText = toEncrypt.getBytes(); 
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, masterKey);
        byte[] cipherText = cipher.doFinal(plainText);
        String s = DatatypeConverter.printBase64Binary(cipherText);
        
        return s;
    }
    public String decrypt(String toDecrypt)throws Exception
    {
        byte[] plainText = DatatypeConverter.parseBase64Binary(toDecrypt);
        Cipher cipher = Cipher.getInstance("AES");//CBC/PKCS5Padding");
//        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
//        IvParameterSpec ivspec = new IvParameterSpec(iv);
        System.out.println();
        cipher.init(Cipher.DECRYPT_MODE, masterKey);//, ivspec);
        byte[] cipherText = cipher.doFinal(plainText);
        String s = new String(cipherText);
        return s;
    }
    
    public String publicEncrypt(String toEncrypt, PrivateKey KRA) throws Exception
    {
        byte[] plainText = toEncrypt.getBytes(); 
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, KRA);
        byte[] cipherText = cipher.doFinal(plainText);
        String s = DatatypeConverter.printBase64Binary(cipherText);
        
        return s;
    }
    
    public String publicDecrypt(String toDecrypt, PublicKey KRA) throws Exception
    {
        byte[] plainText = DatatypeConverter.parseBase64Binary(toDecrypt);
        Cipher cipher = Cipher.getInstance("RSA");//CBC/PKCS5Padding");
//        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
//        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, KRA);//, ivspec);
        byte[] cipherText = cipher.doFinal(plainText);
        String s = new String(cipherText);
        return s;
    }
            
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        try
        {
            int i = jList1.getSelectedIndex();
            ListModel md = jList1.getModel();

            String s = md.getElementAt(i).toString();
            //consoleMessage(s);
            boolean sent = false;
            if(s!=null)
                for (Friend friend : friendsLoggedOn) 
                {
                    if (friend.userName.equals(s)) 
                    {
                              
                        if(friend.printWriter!=null)
                        {
                            nonce = generateNonce();
                            String temp = friend.userName + " " + nonce;
                            String encrypted = encrypt(temp);
                            String decrypted = decrypt(encrypted);
                            consoleMessage("decrypted now is " + decrypted);
                            
                            consoleMessage("Not encrypted yet " + temp);
                            byte[] cipherTemp = masterKey.getEncoded();
                            
                            String sE = DatatypeConverter.printBase64Binary(cipherTemp);
                            consoleMessage("Encrypted with key " + sE);
                            
                            outToServer.println("/Phase1 " + encrypted);
                        }
                        else
                        {
                            consoleMessage("Not connected to friend");
                        }
                        //new ChatWindow(temp.contactName, temp.ipAdress, temp.port, userName, this);
                        sent = true;
                        break;
                    }
                }
            if(!sent)
            {
                consoleMessage("Authentification failed in Phase1");
            }

        }
        catch(Exception e)
        {
            jTextArea1.append("Couldn't send your message, try selecting someone to talk to. " + e + "/n");
        }
        
        
    }//GEN-LAST:event_jButton2ActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}

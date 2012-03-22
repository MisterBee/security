import java.security.*;
import javax.crypto.*;
import java.io.*;
import javax.xml.bind.DatatypeConverter;
import java.util.StringTokenizer;



public class encryption{

	public static void main(String [] args) throws Exception
	{
		if(args.length != 1)
		{
			System.err.println("Usage : enter text");
			System.exit(1);
		}
		
		//digestExample(args[0]);
		//macExample(args[0]);
		//privateKey(args[0]);
		//publicKey(args[0]);
		//DigitalSignature(args[0]);
		//fileWithPrivateKey(args[0]);
		doubleEncryptDecrypt(args[0]);
		
	}
	
	public static void doubleEncryptDecrypt(String args) throws Exception
	{
				
		System.out.println("\nStart generating two AES keys");
		KeyGenerator keyG = KeyGenerator.getInstance("AES");
		keyG.init(128);
		Key KRa = keyG.generateKey();
		Key Kab = keyG.generateKey();
		System.out.println("Finish Generating keys");
		
		//gets a AES Cipher and prints the provider
		Cipher cipher = Cipher.getInstance("AES");
		System.out.println("\n" + cipher.getProvider().getInfo());
		String aDigest = digestExample(args);
		System.out.println();
		byte[] plainText = args.getBytes("UTF8");
		//sign plaintext with and A's private key
		System.out.println("\nStart signing");
		cipher.init(Cipher.ENCRYPT_MODE, KRa);
		//dataTypeConverter DC = new dataTypeConverter();
		byte[] cipherText = cipher.doFinal(plainText);
		System.out.println("Finish signing");
		String msgSigned = DatatypeConverter.printBase64Binary(cipherText);
		msgSigned += " M: Go to Alice";
		System.out.println(msgSigned);
		
		
		
		byte[] newCipherText = msgSigned.getBytes("UTF8");
		//Encrpyt cipherText with shared key
		System.out.println("\nStart Encrypting with shared key");
		cipher.init(Cipher.ENCRYPT_MODE, Kab);
		byte[] doubleCipherText = cipher.doFinal(newCipherText);
		System.out.println("Finish Encrpyting with shared key");
		String msgEncrypted = DatatypeConverter.printBase64Binary(doubleCipherText);
		System.out.println(msgEncrypted);
		
		
		System.out.println("\nStart decrypting with shared key");
		cipher.init(Cipher.DECRYPT_MODE, Kab);
		byte[] doubleDecryptText = cipher.doFinal(doubleCipherText);
		System.out.println("Finish decrpyting with shared key");
		String msgDecrypted = new String(doubleDecryptText);
		System.out.println(msgDecrypted);		
		StringTokenizer st = new StringTokenizer(msgDecrypted);
		msgDecrypted = st.nextToken(" ");
		System.out.println(msgDecrypted);
		
		
		System.out.println("\nStart testing signature");
		byte[] cipherText2 = DatatypeConverter.parseBase64Binary(msgDecrypted);
		cipher.init(Cipher.DECRYPT_MODE, KRa);
		byte[] decryptText = cipher.doFinal(cipherText2);
		System.out.println("Finish testing starting");
		String msg = new String(decryptText);
		System.out.println(msg);
	}
	
	public static void fileWithPrivateKey(String args) throws Exception
	{	
		// selected code from www.avajava.com/tutorials/lessons/how-do-i-encrypt-and-decrypt-files-using-des.html
		// generates a DES private key.
		System.out.println("\nStart generating DES key");
		KeyGenerator keyG = KeyGenerator.getInstance("DES");
		keyG.init(56);
		Key key = keyG.generateKey();
		System.out.println("Finish Generating key");
		
		FileInputStream fis = new FileInputStream(args);
		FileOutputStream fos = new FileOutputStream("encrypted");
		
		FileInputStream fis1 = new FileInputStream("encrypted");
		FileOutputStream fos1 = new FileOutputStream("decrypted");
		
		//gets a DES Cipher and prints the provider
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		System.out.println("\n" + cipher.getProvider().getInfo());
		
		//Encrypt using plaintext and key
		System.out.println("\nStart encryption");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		byte[] cipherFile = new byte[64];
		int numBytes;
		while (((numBytes = fis.read(cipherFile))) != -1)
		{	
			fos.write(cipherFile, 0, numBytes);
		}
		
		System.out.println("Finish encryption");
		//System.out.println(new String (cipherText, "UTF8"));
		
		
		//decrypt using ciphertext and key
		System.out.println("\nStart decryption");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptFile = new byte[64];
		System.out.println("Finish decryption");
		//System.out.println(new String (dePlainText, "UTF8"));
		
	}	
	public static void DigitalSignature(String args) throws Exception
	{
		byte[] plainText = args.getBytes("UTF8");
		
		System.out.println("\nStart Generate RSA key");
		KeyPairGenerator keyG = KeyPairGenerator.getInstance("RSA");
		keyG.initialize(1024);
		
		KeyPair key = keyG.generateKeyPair();
		System.out.println("Finished generating key");
		
//		get a signature object using MD5 and RSA combo
//		and sign the plaintext with the private key,
//		listing the provider along the way

		Signature sig = Signature.getInstance("MD5withRSA");
		sig.initSign(key.getPrivate());
		sig.update(plainText);
		byte[] signature = sig.sign();
		System.out.println(sig.getProvider().getInfo());
		System.out.println("\nSignature");
		System.out.println(new String(signature, "UTF8"));
		
		// verify the signature with the public key
		System.out.println("\nstart signature verification");
		sig.initVerify(key.getPublic());
		sig.update(plainText);
		try
		{
			if(sig.verify(signature))
			{
				System.out.println("Signature verified");
			}
			else
			{
				System.out.println("Signature verification failed");
			}
		}
		catch(SignatureException se)
		{
			System.err.println("Signature Failed");
		}
	}
	public static void publicKey (String args) throws Exception
	{
		//stores string as byte array
		byte[] plainText = args.getBytes("UTF8");
		
		//Generate an RSA key
		System.out.println("\nStart generating RSA key");
		KeyPairGenerator keyG = KeyPairGenerator.getInstance("RSA");
		keyG.initialize(1024);
		KeyPair key = keyG.generateKeyPair();
		System.out.println("Finish generating RSA key");
		
		//get an RSA cipher object
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		System.out.println("\n" + cipher.getProvider().getInfo());
		
		//encrypt using the plaintext and public key
		System.out.println("\nStart encrypt using public key");
		cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());
		byte[] cipherText = cipher.doFinal(plainText);
		System.out.println("Finish Encrypting using public key");
		
		System.out.println(new String( cipherText, "UTF8"));
		//decrypt using private key.
		System.out.println("\nStart decrypt using private key");
		cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
		byte[] dePlainText = cipher.doFinal(cipherText);
		System.out.println("Finish decrypting using private key");
		
		System.out.println(new String( dePlainText, "UTF8"));
	}
	
	public static void privateKey(String args) throws Exception
	{
		//stores string as byte array
		byte[] plainText = args.getBytes("UTF8");

		// generates a DES private key.
		System.out.println("\nStart generating DES key");
		KeyGenerator keyG = KeyGenerator.getInstance("DES");
		keyG.init(56);
		Key key = keyG.generateKey();
		System.out.println("Finish Generating key");
		
		//gets a DES Cipher and prints the provider
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		System.out.println("\n" + cipher.getProvider().getInfo());
		
		//Encrypt using plaintext and key
		System.out.println("\nStart encryption");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(plainText);
		System.out.println("Finish encryption");
		System.out.println(new String (cipherText, "UTF8"));
		
		
		//decrypt using ciphertext and key
		System.out.println("\nStart decryption");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] dePlainText = cipher.doFinal(cipherText);
		System.out.println("Finish decryption");
		System.out.println(new String (dePlainText, "UTF8"));
		
	}
	
	public static String digestExample(String args) throws Exception
	{

		
		byte[] plainText = args.getBytes("UTF8");
		MessageDigest msgDgst = MessageDigest.getInstance("MD5");
		System.out.println("\n" + msgDgst.getProvider().getInfo());
		msgDgst.update(plainText);
		System.out.println("\nDigest");
		String aDigest = new String( msgDgst.digest());
		return aDigest;
		
	}
	public static void macExample(String args) throws Exception
	{
		byte[] plainText = args.getBytes("UTF8");
		System.out.println("\nStart generating key");
		KeyGenerator keyG = KeyGenerator.getInstance("HmacMD5");
		SecretKey MD5Key = keyG.generateKey();
		System.out.println("Finish generating key");
		
		Mac mac = Mac.getInstance("HmacMD5");
		mac.init(MD5Key);
		mac.update(plainText);
		
		System.out.println("\n" + mac.getProvider().getInfo());
		System.out.println("\nMact");
		System.out.println(new String( mac.doFinal(), "UTF8") );
	}
	
}


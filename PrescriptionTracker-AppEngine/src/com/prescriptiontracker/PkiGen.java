package com.prescriptiontracker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;
import javax.xml.bind.DatatypeConverter;

public class PkiGen {
	private static String privateKeyFinal=null;
	private static String publicKeyFinal=null;
	
	
	
	//public PkiGen() throws NoSuchAlgorithmException, InvalidKeySpecException{
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
		
		RandomAccessFile rafPub;
		RandomAccessFile rafPriv;
		byte[] bufPriv;
		byte[] bufPub;
		//byte [] myKey;
		
		
		try {
			rafPriv = new RandomAccessFile("war/WEB-INF/dsaprivate.key", "r");
			rafPub = new RandomAccessFile("war/WEB-INF/dsapublic.key", "r");
			try {
				bufPriv = new byte[(int)rafPriv.length()];
				rafPriv.readFully(bufPriv);
				PKCS8EncodedKeySpec kspecPriv = new PKCS8EncodedKeySpec(bufPriv);
				KeyFactory kfPriv = KeyFactory.getInstance("RSA");
				PrivateKey privKey = kfPriv.generatePrivate(kspecPriv);
				rafPriv.close();
				
				bufPub = new byte[(int)rafPub.length()];
				rafPub.readFully(bufPub);
				X509EncodedKeySpec kspecPub = new X509EncodedKeySpec(bufPub);
				KeyFactory kfPub = KeyFactory.getInstance("RSA");
				PublicKey pubKey = kfPub.generatePublic(kspecPub);
				rafPub.close();
				
				String plain="test data";
				
				
			    
			    byte[] cipherText = encryptWithPubKey(plain.getBytes("UTF-8"),pubKey);
			    String plainDecrypt=new String(decryptWithPrivKey(cipherText,privKey),"UTF-8");
			   System.out.println("cipherText: "+b2h(cipherText));
			    System.out.println("plainText:");
			    System.out.println(new String(decryptWithPrivKey(cipherText,privKey),"UTF-8"));
			    System.out.println(plainDecrypt);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
			
					
								
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		
		/*try {
			BufferedReader reader = new BufferedReader(new FileReader("war/WEB-INF/resource"));
			
			String sCurrentLine;
			 
			try {
				while ((sCurrentLine = reader.readLine()) != null) {
					
					myKey=sCurrentLine.getBytes();		
					System.out.println(sCurrentLine);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	
		
	/*	KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	Integer keySize=1024;
	kpg.initialize(keySize);
	KeyPair kp = kpg.genKeyPair();
	Key publicKey = kp.getPublic();
	Key privateKey = kp.getPrivate();
	
	System.out.println(publicKey);
	System.out.println(privateKey);
	//System.out.println(privateKey);
	KeyFactory fact = KeyFactory.getInstance("RSA");
	RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
	  RSAPublicKeySpec.class);
	RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
	  RSAPrivateKeySpec.class);
	
	
	
	//testing*/
	
	
	
	}
	
	
	
	public static byte[] encryptWithPubKey(byte[] input, Key key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    public static byte[] decryptWithPrivKey(byte[] input, Key key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }
    
    private static String b2h(byte[] bytes){
        return DatatypeConverter.printHexBinary(bytes);
    }
	
    private static byte[] h2b(String hex){
        return DatatypeConverter.parseHexBinary(hex);
    }
    
    public static byte[] pubKeyToBytes(PublicKey key){
        return key.getEncoded(); // X509 for a public key
    }
    public static byte[] privKeyToBytes(PrivateKey key){
        return key.getEncoded(); // PKCS8 for a private key
    }
    
    public static PublicKey bytesToPubKey(byte[] bytes) throws InvalidKeySpecException, NoSuchAlgorithmException{
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }
    public static PrivateKey bytesToPrivKey(byte[] bytes) throws InvalidKeySpecException, NoSuchAlgorithmException{
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }
    
}
	


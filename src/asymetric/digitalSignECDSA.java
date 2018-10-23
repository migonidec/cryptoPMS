package asymetric;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.util.Arrays;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DigitalSignECDSA {

	public static KeyPair generateKeyPair() throws Exception{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC"); //BC for BouncyCastle Provider
		keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec("prime256v1"), new SecureRandom());
		return keyPairGenerator.generateKeyPair();
	}
	
	public static void writeKeys(KeyPair keyPair) throws Exception {
		byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
		FileOutputStream publicFileOutput = new FileOutputStream("public.key");
		publicFileOutput.write(publicKeyBytes);
		publicFileOutput.close();
		
		byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
		FileOutputStream privateFileOutput = new FileOutputStream("private.key");
		privateFileOutput.write(privateKeyBytes);
		privateFileOutput.close();
	}

	
	public static byte[] generateSignatureText(byte[] plainData, PrivateKey privateKey) throws Exception {
		Signature sigatureECDSA = Signature.getInstance("SHA256withECDSA", "BC"); //BC for BouncyCastle Provider
		sigatureECDSA.initSign(privateKey);
		sigatureECDSA.update(plainData);
		return sigatureECDSA.sign();
	}
	
	public static boolean validateSignatureText(byte[] plainData, PublicKey publicKey, byte[] signature) throws Exception{
		Signature sigatureECDSA = Signature.getInstance("SHA256withECDSA", "BC");
		sigatureECDSA.initVerify(publicKey);
		sigatureECDSA.update(plainData);
		return sigatureECDSA.verify(signature);
	}
	
	public static void writeSignature(byte[] signature) throws Exception{
		FileOutputStream privateFileOutput = new FileOutputStream("signature.sig");
		privateFileOutput.write(signature);
		privateFileOutput.close();
	}
	
	public static byte[] generateSignatureFile(String fileName, PrivateKey privateKey) throws Exception {
		Signature sigatureECDSA = Signature.getInstance("SHA256withECDSA", "BC"); //BC for BouncyCastle Provider
		sigatureECDSA.initSign(privateKey);

		//Data recuperation
		FileInputStream fileInput = new FileInputStream(fileName);
		byte[] dataBytes = new byte[1024];
		int numberByteRead = 0; 
		while (numberByteRead >= 0) { //numberByteRead=-1, end of file
			sigatureECDSA.update(dataBytes, 0, numberByteRead);
			numberByteRead = fileInput.read(dataBytes);	//read file and put it in dataBytes
		}
		fileInput.close();
		
		return sigatureECDSA.sign(); 
	}
	
	public static boolean validateSignatureFile(String fileName, PublicKey publicKey, byte[] signature) throws Exception{
		Signature sigatureECDSA = Signature.getInstance("SHA256withECDSA", "BC");
		sigatureECDSA.initVerify(publicKey);
		
		//Data recuperation
		FileInputStream fileInput = new FileInputStream(fileName);
		byte[] dataBytes = new byte[1024];
		int numberByteRead = 0; 
		while (numberByteRead >= 0) { //numberByteRead=-1, end of file
			sigatureECDSA.update(dataBytes, 0, numberByteRead);
			numberByteRead = fileInput.read(dataBytes);	//read file and put it in dataBytes
		}
		fileInput.close();
		
		return sigatureECDSA.verify(signature);
	}
	
	
	

	public static void main (String[] args) throws Exception{
		Security.addProvider(new BouncyCastleProvider());
		
		String plaintext = "Simple plain text";

		KeyPair keys = generateKeyPair();
		writeKeys(keys);
		byte[] signature = generateSignatureFile("test.txt", keys.getPrivate());
		writeSignature(signature);
		
		boolean isValidated = validateSignatureFile("test.txt", keys.getPublic(), signature);
		System.out.println("Result: " + isValidated);
		
		
		

	}
}

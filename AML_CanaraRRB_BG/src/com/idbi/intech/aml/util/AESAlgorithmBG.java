package com.idbi.intech.aml.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.idbi.intech.aml.bg_process.AML_PasswordBaseEncryption;

public class AESAlgorithmBG {

	private int pwdIterations = 65536;
	private int keySize = 256;
	private String keyAlgorithm = "AES";
	private String encryptAlgorithm = "AES/CBC/PKCS5Padding";
	private String secretKeyFactoryAlgorithm = "PBKDF2WithHmacSHA1";

	public String encyrpt(String plainText, String password, String salt,
			String iv) throws Exception {
		// generate key
		byte[] saltBytes = salt.getBytes("UTF-8");

		SecretKeyFactory skf = SecretKeyFactory
				.getInstance(this.secretKeyFactoryAlgorithm);
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes,
				this.pwdIterations, this.keySize);
		SecretKey secretKey = skf.generateSecret(spec);
		SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(),
				keyAlgorithm);

		// AES initialization
		Cipher cipher = Cipher.getInstance(encryptAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, getIV(iv));

		byte[] encryptedText = cipher.doFinal(plainText.getBytes("UTF-8"));
		return new Base64().encodeAsString(encryptedText);
	}

	public AlgorithmParameterSpec getIV(String ivInputString) {
		byte[] iv = ivInputString.getBytes();
		IvParameterSpec ivParameterSpec;
		ivParameterSpec = new IvParameterSpec(iv);
		return ivParameterSpec;
	}

	public String decrypt(String encryptText, String password, String salt,
			String iv) throws Exception {
		byte[] saltBytes = salt.getBytes("UTF-8");
		byte[] encryptTextBytes = new Base64().decode(encryptText);

		SecretKeyFactory skf = SecretKeyFactory
				.getInstance(this.secretKeyFactoryAlgorithm);
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes,
				this.pwdIterations, this.keySize);
		SecretKey secretKey = skf.generateSecret(spec);
		SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(),
				keyAlgorithm);

		// decrypt the message
		Cipher cipher = Cipher.getInstance(encryptAlgorithm);

		cipher.init(Cipher.DECRYPT_MODE, key, getIV(iv));

		byte[] decyrptTextBytes = null;
		try {
			decyrptTextBytes = cipher.doFinal(encryptTextBytes);
		} catch (IllegalBlockSizeException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		String text = new String(decyrptTextBytes);
		return text;
	}

	public static String readFile() {
		String fileData = "", data = "";
		ResourceBundle bundle = ResourceBundle.getBundle("AMLProp");

		String salt = bundle.getString("EncryptionSalt");
		String password = bundle.getString("EncryptionPassword");
		String iv = bundle.getString("EncryptionIV");
		String path = bundle.getString("PasswordFilePath");

		try {
			FileReader reader = new FileReader(path + "EncryptedFile.txt");
			BufferedReader bufferedReader = new BufferedReader(reader);

			while ((fileData = bufferedReader.readLine()) != null) {
				data += fileData;
			}
			reader.close();

			String val[] = data.split("~");
			
			AML_PasswordBaseEncryption encrypt =new AML_PasswordBaseEncryption();

			//data = new AESAlgorithmBG().decrypt(val[0], password, salt, iv)
			//		+ "~"
			//		+ new AESAlgorithmBG().decrypt(val[1], password, salt, iv);
			
			data = encrypt.decrypt(val[0])
							+ "~"
							+ encrypt.decrypt(val[1]);

		//	System.out.println("data ::" + data);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

}

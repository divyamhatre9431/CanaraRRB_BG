package com.idbi.intech.iaml.factory;

import java.io.IOException;
import java.security.InvalidKeyException;

import sun.misc.*;

public class AML_PasswordBaseEncryption {

	public AML_PasswordBaseEncryption() throws InvalidKeyException {
		this("iAML");
	}

	public AML_PasswordBaseEncryption(String s) throws InvalidKeyException {
		key = null;
		objEncrypt = new AML_PasswordEncrypt();
		encryptedPass = null;
		buildKey(s);
	}

	private void buildKey(String s) throws InvalidKeyException {
		String s1 = null;
		if (s.length() < 32)
			s1 = pad(s, 32, "p");
		key = AML_PasswordEncrypt.makeKey(s1.getBytes());
	}

	public boolean isPassword(String s) {
		if (s == null)
			return false;
		else
			return s.equals(decrypt(encryptedPass));
	}

	private static final String pad(String s, int i, String s1) {
		int j = s.length();
		int k = i - j;
		StringBuffer stringbuffer = new StringBuffer(i);
		stringbuffer.append(s);
		for (int l = 0; l < k; l++)
			stringbuffer.append(s1);

		return stringbuffer.toString();
	}

	private static final String pad16(String s) {
		int i = s.length();
		int j = (int) (Math.floor((double) i / 16D) * 16D) + 16;
		return pad(s, j, "p");
	}

	private static final String removeCRLF(String s) {
		char ac[] = s.toCharArray();
		StringBuffer stringbuffer = new StringBuffer(ac.length);
		for (int i = 0; i < ac.length; i++)
			if (ac[i] != '\n' && ac[i] != '\r')
				stringbuffer.append(ac[i]);

		return stringbuffer.toString();
	}

	public String encrypt(String s) {
		s = s + "iAML";
		s = pad16(s);
		byte abyte0[] = s.getBytes();
		byte abyte1[] = new byte[abyte0.length];
		int i = abyte0.length / 16;
		for (int j = 0; j < i; j++) {
			byte abyte2[] = AML_PasswordEncrypt.blockEncrypt(abyte0, j * 16,
					key);
			System.arraycopy(abyte2, 0, abyte1, j * 16, 16);
		}

		return removeCRLF(b64encoder.encode(abyte1));
	}

	public String decrypt(String s) {
		byte abyte0[] = (byte[]) null;
		try {
			abyte0 = b64decoder.decodeBuffer(s);
		} catch (IOException ioexception) {
			throw new RuntimeException(
					"base64Decoder unable to decode ciphertext: "
							+ ioexception.toString());
		}
		byte abyte1[] = new byte[abyte0.length];
		int i = abyte0.length / 16;
		for (int j = 0; j < i; j++) {
			byte abyte2[] = AML_PasswordEncrypt.blockDecrypt(abyte0, j * 16,
					key);
			System.arraycopy(abyte2, 0, abyte1, j * 16, 16);
		}

		String s1 = new String(abyte1);
		int k = s1.lastIndexOf("iAML");
		if (k == -1)
			return s1;
		else
			return s1.substring(0, k);
	}

	public static void main(String args[]) throws Exception {
		// AML_PasswordBaseEncryption IC_Outgoing_PasswordBaseEncryption = new
		// AML_PasswordBaseEncryption("testpassword");
		// String s = "Testing";
		// String s1 = IC_Outgoing_PasswordBaseEncryption.encrypt(s);
		// System.out.println("Encrypting '" + s + "'");
		// System.out.println("Results in \n" + s1 + "\n");
		// AML_PasswordBaseEncryption passwordbaseencryption1 = new
		// AML_PasswordBaseEncryption("testpassword");
		// System.out.println("Decrypted text '" +
		// passwordbaseencryption1.decrypt(s1) + "'");
	}

	private static final BASE64Encoder b64encoder = new BASE64Encoder();
	private static final BASE64Decoder b64decoder = new BASE64Decoder();
	private Object key;
	private AML_PasswordEncrypt objEncrypt;
	private static final String endcode = "AMLINTECH";
	private String encryptedPass;

}

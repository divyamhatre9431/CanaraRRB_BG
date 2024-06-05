package com.idbi.intech.aml.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ResourceBundle;

import com.idbi.intech.aml.bg_process.AML_PasswordBaseEncryption;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class UsernamePasswordGenTool {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		Connection connection = null;
		String result = "NA";
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("AMLProp");
			
			String salt = bundle.getString("EncryptionSalt");
			String password = bundle.getString("EncryptionPassword");
			String iv = bundle.getString("EncryptionIV");
			String path = bundle.getString("PasswordFilePath");
			
		//	System.out.println(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));

			System.out.println("Enter Username");
			String userName = reader.readLine();

			System.out.println("Enter Password");
			String pass = reader.readLine();
			
			result = ConnectionFactory.testConnection(userName, pass);
			
			if(result.equalsIgnoreCase("success")){
				System.out.println("connection established");
				AML_PasswordBaseEncryption encryptObj=new AML_PasswordBaseEncryption();
				//userName = new AESAlgorithmBG().encyrpt(userName, password, salt, iv);
				userName = encryptObj.encrypt(userName);

				//pass = new AESAlgorithmBG().encyrpt(pass, password, salt, iv);
				pass = encryptObj.encrypt(pass);
				
				File file = new File(path+"EncryptedFile.txt");
				
				file.createNewFile();
				
				FileWriter writer = new FileWriter(file);
				writer.write(userName + "~" + pass);
				writer.close();
				reader.close();
				System.out.println("File created in "+path);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}

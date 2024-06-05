package com.idbi.intech.iaml.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.AML_PasswordBaseEncryption;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class PasswordGene {
	public static void main(String arg[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		final ResourceBundle bundle_info = ResourceBundle
				.getBundle("com.idbi.intech.iaml.misc.db");
		AML_PasswordBaseEncryption obj = null;
		try {
			obj = new AML_PasswordBaseEncryption();
			final String ip = bundle_info.getString("ip");
			final String port = bundle_info.getString("port");
			final String sid = bundle_info.getString("sid");
			final String path = bundle_info.getString("path");
			System.out.println("Please enter the username");
			String strUser = br.readLine();
			System.out.println("Please enter the password");
			String strPass = br.readLine();
			String encryptedString = obj.encrypt("jdbc:oracle:thin:@~" + ip
					+ ":~" + port + ":~" + sid + "~" + strUser + "~" + strPass);
			// String encryptedString = obj.encrypt("abc");
			System.out.println(encryptedString);
			FileWriter fw = new FileWriter(path);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(encryptedString);
			bw.close();
			
			System.out.println(obj.decrypt(encryptedString));

			System.out
					.println("New Credentails set.Do you want to test connection?(Y/N)");

			String flg = br.readLine();
			if (flg.equalsIgnoreCase("Y")) {
				try {
					ConnectionFactory.makeConnectionAMLLive();
				} catch (SQLException ex) {
					System.out.println("Test connecntion unsuccessfull");
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

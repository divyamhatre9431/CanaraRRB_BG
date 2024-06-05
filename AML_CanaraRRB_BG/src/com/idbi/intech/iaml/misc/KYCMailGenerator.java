package com.idbi.intech.iaml.misc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class KYCMailGenerator {

	private static Connection connection = null;
	ResourceBundle bundle = ResourceBundle
			.getBundle("com.idbi.intech.iaml.misc.email");
	String from = bundle.getString("EMAILID");
	String password = bundle.getString("PASSWORD");
	String host = bundle.getString("SMTPSERVER");

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void sendMailToBranch() {
		Statement stmt = null;
		Statement stmtDiff = null;
		ResultSet rs = null;
		ResultSet rsDiff = null;
		String custId = "";
		String solId = "";
		String custName = "";
		String finalResult = "";
		String htmlText = "";
		String email = "";
		ArrayList<String> arrTo = null;
		Statement stmtUp = null;
		ResultSet rsUp = null;

		try {
			stmt = connection.createStatement();
			stmtDiff = connection.createStatement();
			stmtUp = connection.createStatement();

			rs = stmt
					.executeQuery("select cust_id,primary_sol_id,cust_name from aml_cust_kyc_threshold where flag = 'N'");
			while (rs.next()) {
				custId = rs.getString(1);
				solId = rs.getString(2);
				custName = rs.getString(3);

				rsDiff = stmtDiff.executeQuery("select get_diffin_threshold('"
						+ custId + "') from dual");
				while (rsDiff.next()) {
					finalResult = rsDiff.getString(1);
				}

				String result[] = finalResult.split("~");
				if (result[3].equals("Y")) {
					htmlText += "<html><body style=\"font-family:arial;font-size:10pt\"><h4><b>Reg : Customer Id :"
							+ custId
							+ "&nbsp;&nbsp;("
							+ custName
							+ ")</b></h4>";
					htmlText += "<i><br>Dear sir,<br><br>";
					htmlText += "We have observed that in the above account at your branch, the credits in the last 12 months far exceed the threshold limit fixed by you for the account.The details are as follows<br><br><br>";
					htmlText += "1) Credits in the last 12 month&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rs."
							+ convertAmt(result[1]) + "<br>";
					htmlText += "2) Threshold limit fixed for the customer&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Rs."
						+ convertAmt(result[0]) + "<br>";
					htmlText += "3) Difference between Threshold and Actaul credits&nbsp;&nbsp;&nbsp;&nbsp;Rs."
							+ convertAmt(result[2]) + "<br>";
					htmlText += "<br><br>";
					htmlText += "As per the Bank's KYC guidelines, branches have to fix and review periodically the threshold limit based on - <br><br>a) customer profile and activity/occupation<br>b) expected annual credits in the account<br>c) any breach in threshold limit<br><br>";
					htmlText += "You are advised to review the threshold limit and effect necessary changes in the Finacle System.</i><br><br><b>ASSTT. GENERAL MANAGER(I&C)</b>";
					htmlText += "<br><br><br><br><p style=\"font-family:arial;font-size:8pt\"><font color=\"red\">*</font>This is an <b>i-AML Solution</b> auto generated e-mail.</p><body></html>";

					arrTo = new ArrayList<String>();

					 email = "bm"+solId+"@obc.co.in";
					//email = "sharath.nair@outlook.com";

					arrTo.add(email);
					try {
						if (SendMail.sendEmailKYC(arrTo,
								new ArrayList<String>(),
								new ArrayList<String>(), "Urgent & Important - Total credits in the account exceeding threshold limit (Customer Id : "+custId+" - "+custName+")",
								htmlText, host, from, password)) {
							
						}
					} catch (AuthenticationFailedException e) {
						e.printStackTrace();
					} catch (SendFailedException e) {
						e.printStackTrace();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}
				stmtUp.executeUpdate("update aml_cust_kyc_threshold set flag='Y' where cust_id = '"
						+ custId + "'");

				connection.commit();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmtDiff != null) {
					stmtDiff.close();
					stmtDiff = null;
				}
				if (rsDiff != null) {
					rsDiff.close();
					rsDiff = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public String convertAmt(String amount) {
		String cntAmt = "";
		String fAmt = "";
		int i = 0;
		boolean flg = true;
		String decimal = "";
		boolean negFlg = false;

		if (amount.contains(".")) {
			decimal = amount.substring(amount.indexOf("."));
			amount = amount.substring(0, amount.indexOf("."));
		}

		if (amount.startsWith("-")) {
			amount = amount.substring(1);
			negFlg = true;
		}

		char digit[] = amount.toCharArray();

		for (int j = (digit.length - 1); j >= 0; j--) {
			if (i == 3) {
				cntAmt += ",";
				flg = false;
				i = 0;
			}
			if (i == 2 && flg == false) {
				cntAmt += "," + (String.valueOf(digit[j]));
				i = 0;
			} else {
				cntAmt += (String.valueOf(digit[j]));
			}
			i++;
		}

		char fdigit[] = cntAmt.toCharArray();

		for (int j = (fdigit.length - 1); j >= 0; j--) {
			fAmt += String.valueOf(fdigit[j]);
		}

		if (decimal != null) {
			fAmt += decimal;
		}

		if (negFlg) {
			fAmt = "-" + fAmt;
		}

		return fAmt;
	}

	public static void main(String args[]) {
		makeConnection();
		KYCMailGenerator kycObj =  new KYCMailGenerator();
		kycObj.sendMailToBranch();
		
	}

}

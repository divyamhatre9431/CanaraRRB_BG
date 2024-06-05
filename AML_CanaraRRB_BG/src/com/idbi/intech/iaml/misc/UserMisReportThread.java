package com.idbi.intech.iaml.misc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class UserMisReportThread implements Runnable {

	private static UserMisReportThread process_run = new UserMisReportThread();

	private static Connection connection = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public boolean getThreadStartTime() {
		boolean flg = false;
		int actualTime = 0;
		int threadStartTime = 0;
		String date = "";
		Statement stmtGetTime = null;
		ResultSet rsGetTime = null;

		try {

			stmtGetTime = connection.createStatement();
			rsGetTime = stmtGetTime
					.executeQuery("select sysdate mail_date,to_char(sysdate,'hh24') actual_time,(select trim(value) value from p_aml_general where name='THREAD_START_TIME') thread_start_time from dual");
			while (rsGetTime.next()) {
				date = rsGetTime.getString("mail_date");
				actualTime = Integer.parseInt(rsGetTime
						.getString("actual_time"));
				threadStartTime = Integer.parseInt(rsGetTime
						.getString("thread_start_time"));
			}
			if (actualTime == threadStartTime) {
				//System.out.println();
				System.out.println("User MIS Mail Send Date : " + date);
				flg = true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtGetTime != null) {
					stmtGetTime.close();
					stmtGetTime = null;
				}

				if (rsGetTime != null) {
					rsGetTime.close();
					rsGetTime = null;
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	public void userMisReport() {
		String userMisHtml = "";
		ResourceBundle bundle = ResourceBundle
				.getBundle("com.idbi.intech.iaml.misc.email");
		String from = bundle.getString("EMAILID");
		String password = bundle.getString("PASSWORD");
		String host = bundle.getString("SMTPSERVER");
		Statement stmt = null;
		Statement dateStmt = null;
		ResultSet rs = null;
		ResultSet rsDate = null;
		String query = "", misDate = "";
		ArrayList<String> misData = new ArrayList<String>();
		ArrayList<String> mailToList = new ArrayList<String>();
		ArrayList<String> mailToCCList = new ArrayList<String>();
		try {
			stmt = connection.createStatement();
			query = "select * from aml_user_mis_mgr where to_date(gen_date,'dd-mm-yy') = TRUNC(SYSDATE - 1)";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				misData.add(rs.getString(1) + "~" + rs.getString(2) + "~" + rs.getString(3) + "~" + rs.getString(4) + "~" + rs.getString(5) + "~" + rs.getString(6) + "~"
						+ rs.getString(7) + "~" + rs.getString(8) + "~"
						+ rs.getString(9));
			}

			dateStmt = connection.createStatement();
			rsDate = dateStmt
					.executeQuery("SELECT to_char(to_date(sysdate-1), 'DD-MM-YYYY') misDate from dual");
			while (rsDate.next()) {
				misDate = rsDate.getString(1);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
 finally {
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rsDate != null) {
					rsDate.close();
					rsDate = null;
				}
				if (dateStmt != null) {
					dateStmt.close();
					dateStmt = null;
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		userMisHtml = "<html><style>table{color:#2B2B2B;border:1px solid #708090;border-collapse: collapse;font-size: 8pt;font-family: Verdana;}th{border:1px solid #708090;background-color:#E0FFFF;color:#2B2B2B;text-align:center;}td{border: 1px solid #708090;color:#2B2B2B;text-align:left;}</style><body><table width='80%' align='center'><th colspan='9'>i-AML USER MIS REPORT</th><tr bgcolor='silver'><td><b>USER_ID</b></td><td><b>TOTAL_GEN_COUNT</b></td><td><b>DISPOSED_CNT</b></td><td><b>DISPOSED_PER</b></td><td><b>ESCLATED_CNT</b></td>"
				+ "<td><b>ESCLATED_PER</b></td> <td><b>PENDING_CNT</b></td> <td><b>PENDING_PER</b></td> <td><b>GEN_DATE</td></b></tr> ";
		for (String misInfo : misData) {
			userMisHtml += "<tr>";
			String misInfoArr[] = misInfo.split("~");
			for (int i = 0; i < misInfoArr.length; i++) {

				userMisHtml += "<td>" + misInfoArr[i] + "</td>";
			}
			userMisHtml += "</tr>";

		}

		userMisHtml += "</table>"
				+ "<br><br><br><p style=\"font-family:arial;font-size:8pt\"><font color=\"red\">*</font>This is an <b>i-AML Solution</b> auto generated e-mail.</p></body></html>";
		
		/*Mail to users*/
		mailToList.add("amlcell@idbi.co.in");

		/*Mail to CC users*/
		mailToCCList.add("sharath.nair@idbiintech.com");
		mailToCCList.add("rohit.singh@idbiintech.com");
		mailToCCList.add("akshay.mhatre@idbiintech.com");
		mailToCCList.add("avinash.ambekar@idbiintech.com");
		mailToCCList.add("chintan.darji@idbiintech.com");
		mailToCCList.add("suvarna.mhatre@idbiintech.com");
		mailToCCList.add("patil.prathamesh@idbiintech.com");
		mailToCCList.add("sagar.kamble@idbiintech.com");
		
		try {
			if (SendMail.sendEmailKYC(mailToList,mailToCCList,
					new ArrayList<String>(), "i-AML User Mis Report for " + misDate,
					userMisHtml, host, from, password)) {
				System.out.println("User Mis Mail Sent Successfully...");
			}
		} catch (AuthenticationFailedException e) {
			e.printStackTrace();
		} catch (SendFailedException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out
						.println("----------USER MIS Mail Send Thread Started------------");
				if (getThreadStartTime()) {
					makeConnection();
					userMisReport();
				}
				Thread.sleep(1000 * 60 * 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public static void windowsService(String args[]) throws Exception {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}

		if ("start".equals(cmd)) {
			process_run.start();
		} else {
			process_run.stop();
		}
	}

	public void start() {
		try {
			makeConnection();
			Thread tObj = new Thread(new UserMisReportThread());
			tObj.start();
		} catch (MissingResourceException e) {
		}
	}

	public void stop() {

	}

	public static void main(String args[]) {
		makeConnection();
		Thread tObj = new Thread(new UserMisReportThread());
		tObj.start();

	}

}

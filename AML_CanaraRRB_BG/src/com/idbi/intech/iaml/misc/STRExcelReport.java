package com.idbi.intech.iaml.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import com.idbi.intech.aml.misc.AMLXLCreator;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class STRExcelReport {
	private static Connection con_aml = null;

	public static void makeConnection() {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void generateReportStr(String reportDate) {
		Statement stmt = null;
		ResultSet rs = null;
		String data = "";
		String reportName = "";
		ArrayList<String> arrTo = new ArrayList<String>();
		ArrayList<ArrayList<String>> datavalue = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<String> arrHead = new ArrayList<String>();
		try {
			arrHead.add("Sr. No.");
			arrHead.add("Cust Id");
			arrHead.add("Account No.");
			arrHead.add("Account Name");
			arrHead.add("Account Open Date");
			arrHead.add("Account Type");
			arrHead.add("Occupation");
			arrHead.add("Pan No.");
			arrHead.add("Threshold");
			arrHead.add("Alert Type");
			arrHead.add("Alert Value");
			arrHead.add("Branch");
			arrHead.add("Region");
			arrHead.add("Ground Of Suspicion");
			arrHead.add("User Id");

			stmt = con_aml.createStatement();

			reportName = "Report" + reportDate;

			rs = stmt
					.executeQuery("select rownum,a.cust_id,(select cust_ac_no from aml_ac_master where cust_acid = a.cust_acid) as account,"
							+ "(select acct_name from aml_ac_master where cust_acid = a.cust_acid) as acct_name,"
							+ "(select to_char(acct_opn_date,'dd/mm/yyyy') from aml_ac_master where cust_acid = a.cust_acid) as acct_opn,"
							+ "(select cust_ac_type from aml_ac_master where cust_acid = a.cust_acid) as acct_type,"
							+ "(select ref_desc from aml_rct where ref_rec_type='21' and ref_code=b.cust_occp_code) occupation,cust_pan_no,"
							+ "(select threshold from aml_kyc where cust_id = a.cust_id) as threshold,"
							+ "(select rule_desc from aml_rule_master where rule_id = a.rule_id) alert_type,tran_amt,"
							+ "branch_id,(select region_name from aml_sol where sol_id = branch_id) as region,get_Comment(a.ticket_id) as rule_comment,get_comment_level(a.ticket_id) as userid "
							+ " from aml_rule_ticket a,aml_cust_master b where a.cust_id = b.cust_id and user_id = (select user_id from aml_user_master where role_id = 'XXX') and ticket_status = 'O' and to_date(last_change_date,'dd-mon-yy')='"
							+ reportDate + "' order by cust_id");
			while (rs.next()) {
				data = rs.getString(1) + "~" + rs.getString(2) + "~"
						+ rs.getString(3) + "~" + rs.getString(4) + "~"
						+ rs.getString(5) + "~" + rs.getString(6) + "~"
						+ rs.getString(7) + "~" + rs.getString(8) + "~"
						+ convertAmt(rs.getString(9)) + "~" + rs.getString(10)
						+ "~" + convertAmt(rs.getString(11)) + "~"
						+ rs.getString(12) + "~" + rs.getString(13) + "~"
						+ rs.getString(14) + "~" + rs.getString(15);

				row = new ArrayList<String>();
				for (String info : data.split("~")) {
					row.add(info);
				}
				datavalue.add(row);
			}

			AMLXLCreator amlXl = new AMLXLCreator();
			amlXl.createExcel("D://AML//STRAnnexure//" + reportName + ".xls",
					"Alert Details", arrHead, datavalue);

			stmt.executeUpdate("insert into aml_str_excel values('"
					+ reportName + "','N')");

			con_aml.commit();

			ResourceBundle bundle = ResourceBundle
					.getBundle("com.idbi.intech.iaml.misc.email");
			String from = bundle.getString("EMAILID");
			String password = bundle.getString("PASSWORD");
			String host = bundle.getString("SMTPSERVER");

			// arrTo.add("insp@obc.co.in");

			// try {
			// if (SendMail
			// .sendEmailNew(
			// arrTo,
			// new ArrayList<String>(),
			// new ArrayList<String>(),
			// "Annexure for STR",
			// "<br><br><br><font size='3'><p>This is system generated mail.Please do not reply on this.</p></font>",
			// host, from, password, reportName,
			// "D://AML//STRAnnexure//")) {
			//
			// stmt.executeUpdate("update aml_str_excel set sendflag='Y' where filename = '"
			// + reportName + "'");
			//
			// con_aml.commit();
			// }
			// } catch (AuthenticationFailedException e) {
			// e.printStackTrace();
			// } catch (SendFailedException e) {
			// e.printStackTrace();
			// } catch (MessagingException e) {
			// e.printStackTrace();
			// }
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

	public static String getTime() {
		Statement stmt = null;
		ResultSet rs = null;
		String time = "";
		try {
			stmt = con_aml.createStatement();
			rs = stmt.executeQuery("select to_char(sysdate,'hh24') from dual");
			while (rs.next()) {
				time = rs.getString(1);
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
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return time;
	}

	public static void main(String args[]) {
		makeConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String reportDate = "";
		try {
			System.out.println("Enter the report date (dd-mon-yy)");
			reportDate = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new STRExcelReport().generateReportStr(reportDate);
	}
}

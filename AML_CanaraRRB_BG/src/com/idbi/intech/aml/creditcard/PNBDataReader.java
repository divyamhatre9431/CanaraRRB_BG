package com.idbi.intech.aml.creditcard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class PNBDataReader {

	PreparedStatement pstmt, pstmtTranh;
	Statement stmt, stmtUpdate;
	ResultSet rs;
	CallableStatement clstmt;
	private static Connection connection;
	   
	
	public String formatDate(String value, String inputDtFr, String outputDtFr) {
		SimpleDateFormat formatter = new SimpleDateFormat(inputDtFr);
		Date date = null;
		try {
			date = formatter.parse(value);
			// System.out.println("DAte:: "+date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat format = new SimpleDateFormat(outputDtFr);
		String pDate = format.format(date);
		// System.out.println("pDAte:: "+pDate);
		return pDate;
	}
	
	public Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public String fileDate() {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		String pDate = format.format(yesterday());
		return pDate;
	}
	
	
	public int readNumberOfLines(String filePath) {
		System.out.println("filepath:"+filePath);
		int lineCnt = 0;
		try {
			LineNumberReader lineReader = new LineNumberReader(new FileReader(new File(filePath)));
			while (lineReader.readLine() != null) {
				lineCnt++;
			}
			
			lineReader.close();
			System.out.println("Number of rows read : " + lineCnt);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		return lineCnt;
	}
	
	public void amlCCAccounts(Connection connection) throws IOException {
		System.out.println("Start of amlCCAccounnts file");
		ResourceBundle bundle = ResourceBundle.getBundle("com.idbi.intech.aml.creditcard.DataPullingFiles");
		//String source = bundle.getString("source") + bundle.getString("ccAccounts")+ ".txt";
		String source = bundle.getString("source");
		File file=new File(source);
		String acc="";
		String[] filesrc=file.list();
		for(String s : filesrc){
			if(s.contains("ACCOUNT") || s.contains("account") || s.contains("Account")){
				acc+=s;
			}
		}
		source=bundle.getString("source") + acc ;
		String destination = bundle.getString("destination") + acc ;
		BufferedReader br = null;

		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("update aml_core_process_cc set exe_flg = 'P' where PROCD_NAME='AML_CC_ACCOUNTS' ");
			connection.commit();
			int lineCnt = readNumberOfLines(source);

			FileReader fr = new FileReader(source);
			br = new BufferedReader(fr);

			String line = "", columns = "", firstLine = "";
			int cnt = 0;
			//firstLine = br.readLine();

			//columns = firstLine == null ? "NA" : firstLine.replace("#", ",");
			//System.out.println(columns);
			//if (!columns.equalsIgnoreCase("NA")) {
				stmt = connection.createStatement();
			//	stmt.executeQuery("truncate table aml_ac_master_temp");

				String query = "insert into aml_cc_accounts_temp values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(query);
				while ((line = br.readLine()) != null) {

					String arrline[] = line.split("\\#", -1);
					pstmt.setString(1, arrline[0]);
					pstmt.setString(2, arrline[1]);
					pstmt.setString(3, arrline[2]);
					pstmt.setString(4, arrline[3]);
					pstmt.setString(5, arrline[4]);
					pstmt.setString(6, arrline[5]);
					pstmt.setString(7, arrline[6]);
					pstmt.setString(8, arrline[7]);
					pstmt.setString(9, arrline[8]);
					pstmt.setString(10,
							arrline[9].equals("") ? null : formatDate(arrline[9],  "dd-MMM-yyyy", "dd-MMM-yyyy"));
					pstmt.setString(11, arrline[10]);
					pstmt.setString(12,	arrline[11]);
					pstmt.setString(13, arrline[12]);
					pstmt.executeUpdate();
					cnt++;
				}
				br.close();
				if (cnt == lineCnt) {
					connection.commit();

					
					//clstmt = connection.prepareCall("call gen_ac_master()");
					//clstmt.execute();
					
					Files.move(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
					System.out.println("File moved.");

					/*stmt.executeUpdate(
							"update aml_core_process_cc set exe_flg = 'Y' where procd_name='AML_CC_ACCOUNTS'");
					connection.commit();*/

					// stmt.executeQuery("insert into aml_ac_bal select
					// cust_acid,cust_ac_balance,to_date(sysdate-1,'dd-mm-yy') eod_date from
					// aml_ac_master");
					// connection.commit();
					// System.out.println("AML AC BAL Created");

					System.out.println("Number of rows inserted in AML_CC_ACCOUNTS_temp : " + cnt);
				} else {
					connection.rollback();
				}
		  //}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
				stmt.executeUpdate("update aml_core_process_cc set exe_flg='E' error_desc='" + e.getMessage()
						+ "' where procd_name='AML_CC_ACCOUNTS'");
				connection.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (br != null) {
					br.close();
					br = null;
				}
				if (clstmt != null) {
					clstmt.close();
					clstmt = null;
				}
				
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}


	
	
	
	
	
	public void amlCCCustomer(Connection connection) throws IOException {
		ResourceBundle bundle = ResourceBundle.getBundle("com.idbi.intech.aml.creditcard.DataPullingFiles");
		//String source = bundle.getString("source") + bundle.getString("ccCustomer")+ ".txt";
		String source = bundle.getString("source");
		File file=new File(source);
		String accc="";
		String[] filesrc=file.list();
		for(String c : filesrc){
			if(c.contains("customer") || c.contains("CUSTOMER") || c.contains("Customer")){
				accc+=c;
			}
		}
		
		source=bundle.getString("source") + accc ;
		String destination = bundle.getString("destination") + accc;
		BufferedReader br = null;

		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("update aml_core_process_cc set exe_flg = 'P' where PROCD_NAME='AML_CC_CUSTOMER' ");
			connection.commit();
			int lineCnt = readNumberOfLines(source);

			FileReader fr = new FileReader(source);
			br = new BufferedReader(fr);

			String line = "", columns = "", firstLine = "";
			int cnt = 0;
			//firstLine = br.readLine();
			//System.out.println("firstLine:"+firstLine);

			//columns = firstLine == null ? "NA" : firstLine.replace("#|", ",");
			
			//System.out.println("coulumn :"+columns);
			//if (!columns.equalsIgnoreCase("NA")) {
				stmt = connection.createStatement();
			//	stmt.executeQuery("truncate table aml_ac_master_temp");

				String query = "insert into aml_cc_customer_temp values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(query);
				/*while ((line = br.readLine()) != null) {*/
				while ((line = br.readLine()) != null) {

					String arrline[] = line.split("\\#\\|", -2);
					//System.out.println("arrdtaa:"+Arrays.toString(arrline));
					pstmt.setString(1, arrline[0]);
					pstmt.setString(2, arrline[1]);
					pstmt.setString(3, arrline[2]);
					pstmt.setString(4, arrline[3]);
					pstmt.setString(5, arrline[4]);
					pstmt.setString(6, arrline[5]);
					pstmt.setString(7, arrline[6]);
					pstmt.setString(8, arrline[9]);
					pstmt.setString(9, arrline[8]);
					pstmt.setString(10,arrline[7]);
					pstmt.setString(11,arrline[10]);
					pstmt.setString(12,arrline[11]);
					pstmt.setString(13, arrline[12]);
					pstmt.setString(14, arrline[13]);
					pstmt.setString(15, arrline[14]);
					pstmt.setString(16, arrline[15]);
					pstmt.setString(17,	arrline[16]);
					pstmt.setString(18, arrline[17]);
					pstmt.setString(19, arrline[18]);
					pstmt.setString(20, arrline[19]);
					pstmt.setString(21, arrline[20]);
					pstmt.setString(22, arrline[21]);
					pstmt.setString(23,	arrline[22]);
					pstmt.setString(24, arrline[23]);
					pstmt.setString(25, arrline[24]);
					pstmt.setString(26, arrline[25]);
					pstmt.setString(27, arrline[26]);
					pstmt.setString(28, arrline[27]);
					pstmt.setString(29, arrline[28]);
					pstmt.setString(30, arrline[29]);
					pstmt.setString(31, arrline[31]);
					pstmt.setString(32, arrline[30]);
					pstmt.setString(33, arrline[32]);
					pstmt.setString(34, arrline[33]);
					pstmt.setString(35, arrline[35]);
					pstmt.setString(36, arrline[34]);
					pstmt.setString(37, arrline[36]);
					pstmt.setString(38, arrline[37]);
					pstmt.setString(39, arrline[39]);
					pstmt.setString(40, arrline[38]);
					pstmt.setString(41, arrline[40]);
					pstmt.setString(42, arrline[41]);
					pstmt.executeUpdate();
					cnt++;
				}
				br.close();
				if (cnt == lineCnt) {
					connection.commit();

					
					
					//clstmt = connection.prepareCall("call gen_ac_master()");
					//clstmt.execute();

					Files.move(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
					System.out.println("File moved.");
					
					/*stmt.executeUpdate(
							"update aml_core_process_cc set exe_flg = 'Y' where procd_name='AML_CC_CUSTOMER'");
					connection.commit();*/

					// stmt.executeQuery("insert into aml_ac_bal select
					// cust_acid,cust_ac_balance,to_date(sysdate-1,'dd-mm-yy') eod_date from
					// aml_ac_master");
					// connection.commit();
					// System.out.println("AML AC BAL Created");

					System.out.println("Number of rows inserted in aml_cc_customer_temp : " + cnt);
				} else {
					connection.rollback();
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
				stmt.executeUpdate("update aml_core_process_cc set exe_flg='E' error_desc='" + e.getMessage()
						+ "' where procd_name='AML_CC_CUSTOMER'");
				connection.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (br != null) {
					br.close();
					br = null;
				}
				if (clstmt != null) {
					clstmt.close();
					clstmt = null;
				}
				
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	public void amlCCTransaction(Connection connection) throws IOException {
		ResourceBundle bundle = ResourceBundle.getBundle("com.idbi.intech.aml.creditcard.DataPullingFiles");
		//String source = bundle.getString("source") + bundle.getString("ccTransaction")+ ".txt";
		//String destination = bundle.getString("destination") + bundle.getString("ccTransaction")+ ".txt";
		String source = bundle.getString("source");
		File file=new File(source);
		String acct="";
		String[] filesrc=file.list();
		for(String t : filesrc){
			if(t.contains("transaction") || t.contains("TRANSACTION") || t.contains("Transaction")){
				acct+=t;
			}
		}
		source=bundle.getString("source") + acct ;
		String destination = bundle.getString("destination") + acct;
		BufferedReader br = null;

		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("update aml_core_process_cc set exe_flg = 'P' where PROCD_NAME='AML_CC_TRANSACTION' ");
			connection.commit();
			int lineCnt = readNumberOfLines(source);

			FileReader fr = new FileReader(source);
			br = new BufferedReader(fr);

			String line = "", columns = "", firstLine = "";
			int cnt = 0;
			//firstLine = br.readLine();

			//columns = firstLine == null ? "NA" : firstLine.replace("|", ",");
			//System.out.println("coulumn :"+columns);
			//if (!columns.equalsIgnoreCase("NA")) {
				stmt = connection.createStatement();
				//stmt.executeQuery("truncate table aml_ac_master_temp");

				String query = "insert into aml_cc_transaction_temp values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(query);
				while ((line = br.readLine()) != null) {

					String arrline[] = line.split("\\|", -1);
					pstmt.setString(1, arrline[0]);
					pstmt.setString(2, arrline[1]);
					pstmt.setString(3, arrline[2]);
					pstmt.setString(4, arrline[3]);
					pstmt.setString(5, arrline[4]);
					pstmt.setString(6, arrline[5].equals("") ? null : formatDate(arrline[5], "dd-MMM-yyyy", "dd-MMM-yyyy"));
					pstmt.setString(7, arrline[6].equals("") ? null : formatDate(arrline[6], "dd-MMM-yyyy", "dd-MMM-yyyy"));
					pstmt.setString(8, arrline[7].equals("") ? null : formatDate(arrline[7], "dd-MMM-yyyy", "dd-MMM-yyyy"));
					pstmt.setString(9, arrline[8]);
					pstmt.setString(10,arrline[9]);
					pstmt.setString(11,arrline[10]);
					pstmt.setString(12,arrline[11]);
					pstmt.setString(13, arrline[12]);
					pstmt.setString(14, arrline[13]);
					pstmt.setString(15, arrline[14]);
					pstmt.setString(16, arrline[15]);
					pstmt.setString(17,	arrline[16]);
					pstmt.setString(18, arrline[17]);
					pstmt.setString(19, arrline[18]);
					pstmt.setString(20, arrline[19]);
					pstmt.setString(21, arrline[20]);
					pstmt.setString(22, arrline[21]);
					pstmt.setString(23,	arrline[22]);
					pstmt.setString(24, arrline[23]);
					pstmt.setString(25, arrline[24]);
					pstmt.setString(26, arrline[25]);
					pstmt.setString(27, arrline[26]);
					pstmt.setString(28, arrline[27]);
					pstmt.setString(29, arrline[28]);
					pstmt.setString(30, arrline[29]);
					pstmt.setString(31, arrline[30]);
					pstmt.setString(32, arrline[31]);
					pstmt.setString(33, arrline[32]);
					pstmt.setString(34, arrline[33]);
					pstmt.setString(35, arrline[34]);
					pstmt.setString(36, arrline[35]);
					pstmt.setString(37, arrline[36]);
					pstmt.setString(38, arrline[37]);
					pstmt.setString(39, arrline[38]);
					pstmt.setString(40, arrline[39]);
					pstmt.setString(41, arrline[40]);
					pstmt.setString(42, arrline[41]);
					pstmt.setString(43, arrline[42]);
					pstmt.setString(44, arrline[43]);
					pstmt.setString(45, arrline[44].equals("") ? "0" : arrline[44]);
					pstmt.setString(46, "");
					pstmt.setString(47, "");
					pstmt.executeUpdate();
					cnt++;
				}
				br.close();
				if (cnt == lineCnt) {
					connection.commit();

					
					
					//clstmt = connection.prepareCall("call gen_ac_master()");
					//clstmt.execute();
					Files.move(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
					System.out.println("File moved.");

					/*stmt.executeUpdate(
							"update aml_core_process_cc set exe_flg = 'Y' where procd_name='AML_CC_TRANSACTION'");
					connection.commit();*/

					// stmt.executeQuery("insert into aml_ac_bal select
					// cust_acid,cust_ac_balance,to_date(sysdate-1,'dd-mm-yy') eod_date from
					// aml_ac_master");
					// connection.commit();
					// System.out.println("AML AC BAL Created");

					System.out.println("Number of rows inserted in aml_cc_transaction_temp : " + cnt);
				} else {
					connection.rollback();
				}
			//}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
				stmt.executeUpdate("update aml_core_process_cc set exe_flg='E' error_desc='" + e.getMessage()
						+ "' where procd_name='AML_CC_TRANSACTION'");
				connection.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (br != null) {
					br.close();
					br = null;
				}
				if (clstmt != null) {
					clstmt.close();
					clstmt = null;
				}
			
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}

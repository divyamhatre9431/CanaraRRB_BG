package com.idbi.intech.aml.creditcard;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.datapull.DataMain;
import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.rulethread.RuleThread;

public class PNBDataMain implements Runnable{

	private static Connection connection = null;
	Statement stmt = null;
	Statement stmtCheck = null, stmtError = null, stmtErrUpdate = null, stmtRuleUp = null, stmtFileCount = null,
			stmtErrPull = null, stmtmail = null;
	ResultSet rs = null,rs1=null, rsError = null, rsRule = null, rsCheck = null, rsFileCount = null, rsErrPull = null,
			rsmail = null;
	static DataMain process_run = new DataMain();
	int errCnt = 0;
	String checkDate = "", cbs_date = "", errDate = "", toMail = "", ccMail = "";
	
	
	
	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
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
			Thread t = new Thread(new DataMain());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public int checkCreateThread() {
		int cnt = 0;
		try {
			stmtCheck = connection.createStatement();
			rs = stmtCheck.executeQuery("select count(1) from aml_core_process_cc where active_flg='Y' and exe_flg='N'");
			while (rs.next()) {
				cnt = rs.getInt(1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return cnt;
	}

	public String checkFlgCreateThread() {
		int cnt = 0;
		String flg = "N";
		try {
			stmtCheck = connection.createStatement();
			//System.out.println("Check Y status");
			rs = stmtCheck
					.executeQuery("select count(distinct(exe_flg)) from aml_core_process_cc where active_flg='Y'");
			while (rs.next()) {
				cnt = rs.getInt(1);
			}

			if (cnt == 1) {
				rs = stmtCheck
						.executeQuery("select distinct(exe_flg) from aml_core_process_cc where active_flg='Y'");
				while (rs.next()) {
					flg = rs.getString(1);
				//	System.out.println("Get Y status");
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	//	System.out.println(flg);
		return flg;
	}

	public void createRuleThread() {
		try {
			String cbsDt = "";
			int days =0;
			stmt = connection.createStatement();

			rs = stmt
					.executeQuery("select to_char(cbs_date,'dd-mon-yyyy') from aml_bizz_tab_CC");
			while (rs.next()) {
				cbsDt = rs.getString(1);  
			}
			System.out.println("cbsDt:"+cbsDt);
			System.out.println("---------- Is reached for taking rule id ---------- ");
			rs = stmt.executeQuery("select rule_id from aml_rule_master where next_exe_date='"+ cbsDt + "' and del_flg='N' and category='CREDIT CARD' order by rule_id");
			

			while (rs.next()) {
				String rId = rs.getString(1);
			//	System.out.println(rId);
			
				String procedure = rId + "('" + cbsDt + "')";
				
				//new Thread(new RuleThread(procedure)).start();
				//executeRule(procedure);
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
	}

	
	public boolean startDataPuuling() {
		
		boolean flg=false;
	
		try
		{
			
			ResourceBundle bundle = ResourceBundle.getBundle("com.idbi.intech.aml.creditcard.DataPullingFiles");
			String source = bundle.getString("source");
			File curFolder = new File(source);
			
			PNBDataReader dataReaderObj = new PNBDataReader();
			int procedureCnt = checkCreateThread();
			System.out.println("procedureCnt :"+ procedureCnt);
			if (procedureCnt == 3) {
				 			 
				 flg=true;
				//System.out.println("Main loop");
				//FileUnzipper.unGunzipFile();
				

				try {
					//System.out.println("before connection"); 
					stmt =connection.createStatement(); 
					stmt.executeUpdate("update aml_bizz_tab_CC set OPE_FLG_RULE_CC='N'");

					stmtCheck = connection.createStatement();
					rsCheck = stmtCheck.executeQuery(
							"select procd_name from aml_core_process_cc where exe_flg='N' and active_flg = 'Y'");
					while (rsCheck.next()) { 
						
						String pname = rsCheck.getString(1);
						if (pname.equals("AML_CC_ACCOUNTS")) {
							dataReaderObj.amlCCAccounts(connection);
							//System.out.println("True Data Pulling end First FIle");
							String procedure = "GEN_"+pname + "()";
							new Thread(new DataMgrCC(procedure)).start();
							//System.out.println("True01 Data Pulling end First FIle");
						}
						if (pname.equals("AML_CC_CUSTOMER")) {
							dataReaderObj.amlCCCustomer(connection);
							//System.out.println("True Data Pulling end Second FIle");
							String procedure = "GEN_"+pname + "()";
							new Thread(new DataMgrCC(procedure)).start();
							//System.out.println("True02 Data Pulling end First FIle");
						}
						if (pname.equals("AML_CC_TRANSACTION")) {
							dataReaderObj.amlCCTransaction(connection);
							//System.out.println("True Data Pulling end Third FIle");
							String procedure = "GEN_"+pname + "()";
							new Thread(new DataMgrCC(procedure)).start();
							//System.out.println("True03 Data Pulling end First FIle");
						}

					}
					connection.commit(); 
					// dataReaderObj.executeProcedure(connection);

				} catch (SQLException | IOException ex) {
					ex.printStackTrace();
				}
				
			}else {
				flg=false;
			}
		}
		catch(Exception e)
		{
			flg=false;
			e.printStackTrace();
		}
		
		return flg;
	}
	
	public static void main(String[] args) {
		makeConnection();
		Thread t = new Thread(new PNBDataMain());
		t.start();
	}
	
	public void compileObjects() {

		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("update aml_bizz_tab_CC set OPE_FLG_RULE_CC='Y'");
			connection.commit();
			// stmt.execute("call compile_objects()");
			// System.out.println("Objects Compiled Successfully");
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

	@Override
	public void run() {
		
		while (true) {
			System.out.println("Data Pulling service started");
			
			boolean flg = startDataPuuling();
			
			System.out.println("DATA puuling return result :"+flg);
			
			 /*if (checkFlgCreateThread().equals("Y") && flg == true) {
				 
				 compileObjects();
					System.out.println("rule started");
					
					createRuleThread();
				 }*/
			 
			 try {
					Thread.sleep(1000 * 60 * 60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			
		}
		
	}
}

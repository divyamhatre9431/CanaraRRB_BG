package com.idbi.intech.iaml.EDPMSUpload;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.Scanner;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class EDPMSRuleNew implements Runnable {
	static EDPMSRule processRun = new EDPMSRule();
	private static Connection con = null;
	CallableStatement procStmt = null;
	Statement processstmt=null,statusStmt=null;

	public static void makeConnection() throws SQLException {
		con = ConnectionFactory.makeConnectionAMLLive();
	}

	public void ruleruningEDPMS() throws IOException, SQLException {

		Scanner sc = null;
		Statement stmt = null, procstmt = null;

		ResultSet rsDtls = null, procDtls = null;

		try {
			try {
				makeConnection();
				stmt = con.createStatement();
				String fetchQuery =  "select CBS_DATE from AML_BIZZ_TAB_EDPMS where RULE_RUN_FLG='N'";
				System.out.println(fetchQuery);
				rsDtls = stmt.executeQuery(fetchQuery);

				while (rsDtls.next()) {
					try {
						Date start_date = rsDtls.getDate(1);

						String STARTDATE = new SimpleDateFormat("dd-MM-yy").format(start_date);

						// System.out.println("STARTDATE :"+STARTDATE);

						System.out.println( " START DATE :" + STARTDATE );

						procstmt = con.createStatement();
						String procQuery = "select rule_id from aml_rule_master where category in ('EXPORT','IMPORT')"
								+ " and next_exe_date='"+ STARTDATE + "' AND del_flg='N'";
						procDtls = procstmt.executeQuery(procQuery);

						while (procDtls.next()) {
							String ruleId = procDtls.getString(1);
							
							String procedure = ruleId;
							executeProc(procedure, STARTDATE);
						}
						
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (sc != null) {
				sc.close();
			}
			if (con != null) {
				con.close();
			}
		}
	}
	
	public void executeProc(String procedure, String STARTDATE) {
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("Started execution for : " + procedure);

			makeConnection();
			processstmt=con.createStatement();
			processstmt.execute("update AML_BIZZ_TAB_EDPMS set RULE_RUN_FLG='P' where RULE_RUN_FLG='N'");
			con.commit();
			
			procStmt = con.prepareCall("{call " + procedure + "}");
			procStmt.execute();

			statusStmt=con.createStatement();
			statusStmt.execute("update AML_BIZZ_TAB_EDPMS set RULE_RUN_FLG='Y' where RULE_RUN_FLG='P'");
			con.commit();

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println(
					"Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			System.out.flush();

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (procStmt != null) {
					procStmt.close();
					procStmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public String checkRunThread() {
		String flg = "";
		Statement stmtCheck = null;

		ResultSet rsDtls = null;
		try {
			makeConnection();
			stmtCheck = con.createStatement();
		   	String fetchQuery =  "select RULE_RUN_FLG from AML_BIZZ_TAB_EDPMS";
		   	System.out.println(fetchQuery);
		   	rsDtls = stmtCheck.executeQuery(fetchQuery);
		   	while (rsDtls.next()) {
				flg = rsDtls.getString(1);

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (rsDtls != null) {
					rsDtls.close();
					rsDtls = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}
	

	public static void stopService(String serviceName) throws IOException, InterruptedException {
		String executeCmd = "cmd /c net stop \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Successfully Stopped");
		}
	}

	public static void windowsService(String args[]) throws Exception {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}

		if ("start".equals(cmd)) {
			processRun.start();
		} else {
			processRun.stop();
		}
	}

	public void start() {
		try {
			try {
				makeConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread t = new Thread(new EDPMSRule());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	@Override
	public void run() {
		while (true) {
			try {if (checkRunThread().equals("N")){
				ruleruningEDPMS();
			}
			} catch (IOException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}

	}

	public static void main(String[] args) throws IOException, SQLException {
		makeConnection();
		Thread t = new Thread(new EDPMSRule());
		t.start();
	}
}
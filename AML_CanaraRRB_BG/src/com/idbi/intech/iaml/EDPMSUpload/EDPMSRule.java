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

public class EDPMSRule implements Runnable {
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
				String fetchQuery = "select rule_req_id,start_date,end_date from aml_edpm_rule_exe_req where status='N'";
				System.out.println(fetchQuery);
				rsDtls = stmt.executeQuery(fetchQuery);

				while (rsDtls.next()) {
					try {
						String ruleReqId = rsDtls.getString(1);
						Date start_date = rsDtls.getDate(2);
						Date end_date = rsDtls.getDate(3);

						String STARTDATE = new SimpleDateFormat("dd-MM-yy").format(start_date);

						// System.out.println("STARTDATE :"+STARTDATE);

						System.out.println("rule_req_id :" + ruleReqId + " START DATE :" + STARTDATE + " end_date :"
								+ start_date);

						procstmt = con.createStatement();
						String procQuery = "select rule_id from aml_rule_master where category in ('EXPORT','IMPORT') AND del_flg='N'";
						procDtls = procstmt.executeQuery(procQuery);

						while (procDtls.next()) {
							String ruleId = procDtls.getString(1);
							
							String procedure = ruleId;
							executeProc(ruleReqId,procedure, STARTDATE);
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

	public void executeProc(String ruleReqId,String procedure, String STARTDATE) {
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("Started execution for : " + procedure);

			makeConnection();
			processstmt=con.createStatement();
			processstmt.execute("update aml_edpm_rule_exe_req set status='P' where rule_req_id='"+ruleReqId+"'");
			con.commit();
			
			procStmt = con.prepareCall("{call " + procedure + "('" + ruleReqId + "')" + "}");
			procStmt.execute();

			statusStmt=con.createStatement();
			statusStmt.execute("update aml_edpm_rule_exe_req set status='Y' where rule_req_id='"+ruleReqId+"'");
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
			try {
				ruleruningEDPMS();
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
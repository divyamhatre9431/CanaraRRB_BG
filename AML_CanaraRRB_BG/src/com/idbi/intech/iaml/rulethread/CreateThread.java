package com.idbi.intech.iaml.rulethread;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CreateThread implements Runnable {

	private static Connection connectionOra = null;
	CallableStatement callStmt = null;
	Statement stmt = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	String displayDt = "";
	private static CreateThread process_run = new CreateThread();

	public static void makeConnection() {
		try {
			connectionOra = ConnectionFactory.makeConnectionAMLLive();
		} catch (Exception sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public String checkCreateThread() {

		int cnt = 0;
		String flg = "N";
		try {
			makeConnection();
			stmtCheck = connectionOra.createStatement();

			rs = stmtCheck.executeQuery("select count(distinct(exe_flg)) from AML_CORE_PROCESS  where active_flg='Y'");

			while (rs.next()) {
				cnt = rs.getInt(1);
			}

			if (cnt == 1) {
				rs = stmtCheck.executeQuery("select distinct(exe_flg) from AML_CORE_PROCESS  where active_flg='Y'");
				while (rs.next()) {
					flg = rs.getString(1);

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

		return flg;
	}

	public void createRuleThread() {
		try {
			String cbsDt = "";
			int days = 0;
			stmt = connectionOra.createStatement();

			rs = stmt.executeQuery("select to_char(cbs_date,'dd-mm-yy') from AML_BIZZ_TAB");
			while (rs.next()) {
				cbsDt = rs.getString(1);
			}

			System.out.println("BIZZ CBS_DATE :: " + cbsDt);

			rs = stmt.executeQuery(
					"select RULE_ID from AML_RULE_MASTER where del_flg='N' and NEXT_EXE_DATE= '" + cbsDt + "'");

			while (rs.next()) {
				String procedure = rs.getString(1);
				System.out.println("Executing Rule : " + procedure);
				// new Thread(new RuleThread(procedure, cbsDt)).start();
				executeRule(procedure, cbsDt);
			}

			stmt.executeUpdate("update AML_BIZZ_TAB set OPE_FLG_RULE='Y'");
			connectionOra.commit();
			// stmt.execute("call compile_objects()");
			System.out.println("Bizz Execution Updated Successfully :: Y");

			System.out.println("Rule Execution Completed for date :: " + cbsDt);
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

	public void executeRule(String procedure, String cbsDt) {
		try {
			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure + " - " + cbsDt);
			

			callStmt = connectionOra.prepareCall("{call " + procedure + "(?)}");
			callStmt.setString(1, cbsDt);
			callStmt.execute();

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println(
					"Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (callStmt != null) {
					callStmt.close();
					callStmt = null;
				}
				if (connectionOra != null) {
					connectionOra.close();
					connectionOra = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public int checkBizzTab() {

		int cnt = 0;
		try {
			stmt = connectionOra.createStatement();
			System.out.println("start checkBizzTab");
			rs = stmt.executeQuery(
					"select (select count(1) from AML_TRAN_CASH_DAILY where tran_date =a.cbs_date) cashCnt,\n"
							+ "(select count(1) from AML_TRAN_NONCASH_DAILY where tran_date =a.cbs_date) noncashCnt \n"
							+ "from AML_BIZZ_TAB a where a.OPE_FLG_RULE='N'");

			while (rs.next()) {
				if (rs.getInt(1) > 0 || rs.getInt(2) > 0) {
					cnt = 1;
				}
			}
			System.out.println("end checkBizzTab");
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
		return cnt;
	}

	public static void startService(String serviceName) throws IOException, InterruptedException {
		String executeCmd = "cmd /c net start \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Successfully Started");
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
			Thread t = new Thread(new CreateThread());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public static void main(String args[]) {
		try {

			makeConnection();
			Thread t = new Thread(new CreateThread());
			t.start();

		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void compileObjects() {

		try {
			stmt = connectionOra.createStatement();
			stmt.executeUpdate("update AML_BIZZ_TAB set OPE_FLG_RULE='P'");
			connectionOra.commit();

			System.out.println("Bizz Execution Updated Successfully :: P");
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

			if (checkBizzTab() > 0) {
				System.out.println("Updating Bizz Execution");
				compileObjects();

				createRuleThread();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

package com.idbi.intech.iaml.rulethread;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CreateThreadHist implements Runnable {

	private static Connection connection = null;
	Statement stmt = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	String displayDt = "";
	private static CreateThreadHist process_run = new CreateThreadHist();

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public String checkCreateThread() {
		int cnt = 0;
		String flg = "N";
		try {
			stmtCheck = connection.createStatement();

			rs = stmtCheck.executeQuery("select exe_flg from aml_core_process_hist where active_flg='Y'");
			while (rs.next()) {
				flg = rs.getString(1);
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
			stmt = connection.createStatement();

			rs = stmt.executeQuery("select to_char(cbs_date,'dd-mon-yyyy') from aml_bizz_tab_hist");
			while (rs.next()) {
				cbsDt = rs.getString(1);
			}

			rs = stmt.executeQuery("select rule_id from aml_rule_master where next_exe_date<='" + cbsDt
					+ "' and del_flg='N' and hist_flg='Y'");
			// rs = stmt.executeQuery("select rule_id from aml_rule_master where
			// next_exe_date='"+cbsDt+"' and rule_id not in (select distinct
			// rule_id from aml_rule_ticket) and frequency='M' and
			// del_flg='N'");

			while (rs.next()) {
				String rId = rs.getString(1);
				String procedure = rId + "('" + cbsDt + "')";
				// System.out.println("Executing Rule : " + rId);
				new Thread(new RuleThread(rId,cbsDt)).start();
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

	public int checkBizzTab() {
		int cnt = 0;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(
					"select count(1) from aml_transaction_master_h a,aml_bizz_tab_hist b where a.tran_date = b.cbs_date and ope_flg_rule='N' and rownum <= 1");
			while (rs.next()) {
				cnt = rs.getInt(1);
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
		cnt = 1;
		// System.out.println("cnt 11 : "+cnt);
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
			stmt = connection.createStatement();
			stmt.executeUpdate("update aml_bizz_tab_hist set ope_flg_rule ='Y'");
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

			// System.out.println("Check Thread Created::"+checkCreateThread());
			// System.out.println("Compiling"+checkBizzTab());
			if (checkCreateThread().equals("Y") && checkBizzTab() > 0) {
				// System.out.println("Compiling Objects..");
				compileObjects();
				// System.out.println("Successfully Compiled Objects..");
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

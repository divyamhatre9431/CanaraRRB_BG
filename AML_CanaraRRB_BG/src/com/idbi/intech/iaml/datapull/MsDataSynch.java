package com.idbi.intech.iaml.datapull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class MsDataSynch implements Runnable {
	private static Connection connectionOra = null;
	private static Connection connectionMs = null;
	static MsDataSynch processRun = new MsDataSynch();
	static Logger logger = Logger.getLogger("MyLogger");

	public static void makeOracleConnection() {
		try {
			connectionOra = ConnectionFactory.makeConnectionAMLLive();
			if (connectionOra != null) {
				System.out.println( "Oracle DB Connected.");
			}

		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.out.println(sqlExp.getMessage());

		}
	}

	public static void makeMSSQLConnection() {
		try {
			connectionMs = ConnectionFactory.makeMSSQLConnection();
			if (connectionMs != null) {
				System.out.println("MSSQL DB Connected.");
			}
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.out.println("Exception e:" + sqlExp);

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

	public static void windowsService(String[] args) {
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
			makeOracleConnection();
			Thread t = new Thread(new MsDataSynch());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public boolean checkMsBizzTab() {
		int cnt = 0;
		boolean flg = false;
		ResultSet rsOra = null;
		Statement stmt = null;
		try {
			stmt = connectionMs.createStatement();

			rsOra = stmt.executeQuery("SELECT count(1) FROM AML_BIZZ_TAB WHERE OPE_FLG_RULE='N'");
			while (rsOra.next()) {
				cnt = rsOra.getInt(1);
				if (cnt == 1) {
					System.out.println("Flg : True");
					flg = true;
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rsOra != null) {
					rsOra.close();
					rsOra = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	private boolean checkOraMsBizzDt() {
		Statement stmtCheck = null;
		Statement stmt = null;
		ResultSet rsOra = null;
		ResultSet rsMs = null;
		boolean flg = false;
		String oraBizzDate = null;
		int count = 0;
		try {
			stmt = connectionOra.createStatement();
			stmtCheck = connectionMs.createStatement();

			rsOra = stmt.executeQuery("SELECT TO_CHAR(CBS_DATE,'dd-MM-yyyy') FROM AML_BIZZ_TAB");
			while (rsOra.next()) {
				oraBizzDate = rsOra.getString(1);
				System.out.println("Ora Bizz Date :" + oraBizzDate);
			}

			if (oraBizzDate != null) {
				rsMs = stmtCheck.executeQuery(
						"select COUNT(1) from aml_bizz_tab where format(CONVERT(DATETIME,CBS_DATE),'dd-MM-yyyy')='"
								+ oraBizzDate + "'");
				while (rsMs.next()) {
					count = rsMs.getInt(1);
					System.out.println("MS Bizz Date :" + count);
					if (count == 1) {
						flg = true;
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (rsOra != null) {
					rsOra.close();
					rsOra = null;
				}
				if (rsMs != null) {
					rsMs.close();
					rsMs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	private void dataPullOraToMs() {
		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = connectionMs.createStatement();
			rs = stmt.executeQuery("select procd_name from aml_core_process where active_flg='Y' and exe_flg = 'N'");
			while (rs.next()) {
				String rName = rs.getString(1);
				String procedure = rName;
				System.out.println("Procedure Name :" + procedure);
				// new Thread(new DataMigrateMs(procedure)).start();
				executeProc(procedure);
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

	public void executeProc(String procedure) {
		Statement stmt = null;

		try {
			stmt = connectionMs.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("{call dbo." + procedure + "}");

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println("Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			System.out.flush();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void run() {

		while (true) {
			if (checkMsBizzTab() && checkOraMsBizzDt()) {
				dataPullOraToMs();
				UpdateBizzTab();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	private void UpdateBizzTab() {
	
		Statement stmt = null;
		try {
			stmt = connectionMs.createStatement();
			stmt.executeUpdate("update AML_BIZZ_TAB set OPE_FLG_RULE='Y'");
			connectionMs.commit();
			
			System.out.println("Bizz Execution Updated Successfully :: Y");
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		makeOracleConnection();
		makeMSSQLConnection();
		Thread t = new Thread(new MsDataSynch());
		t.start();
	}
}

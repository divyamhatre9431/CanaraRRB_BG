package com.idbi.intech.iaml.datapull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class OraDataPulling implements Runnable{
	private static Connection connectionOra = null;
	Statement stmt = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	static DataPullingOra process_run = new DataPullingOra();
	
	public static void makeOracleConnection() {
		try {
			
			connectionOra = ConnectionFactory.makeConnectionAMLLive();
			//System.out.println("ORACLE CONNECTION");
			if(connectionOra!=null) {
				System.out.println("Oracle DB Connected.");
			}

		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.out.println(sqlExp.getMessage());
		}
	}
	

	public void createDataThread(){
		try {
			String date = "";
			stmt = connectionOra.createStatement();
			
			rs = stmt.executeQuery("select procd_name from aml_core_process where active_flg='Y' and exe_flg = 'N'");
			while (rs.next()) {
				String rName = rs.getString(1);
				String procedure = rName;
				System.out.println("Procedure Name :"+procedure);
				new Thread(new DataMigrateOra(procedure)).start();
				
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
			process_run.start();
		} else {
			process_run.stop();
		}
	}

	public void start() {
		try {
			makeOracleConnection();
			Thread t = new Thread(new OraDataPulling());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}
	
	public boolean checkOracleBizzTab() {
		int cnt = 0;
		boolean flg = false;
		ResultSet rsOra = null;
		try {
			stmt= connectionOra.createStatement();

			rsOra = stmt.executeQuery("SELECT count(1) FROM AML_BIZZ_TAB WHERE OPE_FLG_RULE='N'");
			while (rsOra.next()) {
				cnt = rsOra.getInt(1);
				if(cnt==1) {
					System.out.println("Flg : True");
					flg=true;
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
	
	public void run() {
		while (true) {
			if ( checkOracleBizzTab()) {
				createDataThread();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		makeOracleConnection();
		Thread t = new Thread(new OraDataPulling());
		t.start();
	}
}

package com.idbi.intech.iaml.datapull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.misc.MarkLeaveUser;

public class DataMain implements Runnable {
	private static Connection connection = null;
	Statement stmt = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	static DataMain process_run = new DataMain();
	
	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void createDataThread() {
		try {
			String date="";
			stmt = connection.createStatement();
			rs=stmt.executeQuery("select to_date(sysdate,'dd-mm-yy') from dual");
			while(rs.next()){
				date=rs.getString(1);
			}
			System.out.println("Data Pulling Started for Date "+date);
			rs = stmt
					.executeQuery("select procd_name from aml_core_process where active_flg='Y' and exe_flg = 'N'");
			while (rs.next()) {
				String rName = rs.getString(1);
				String procedure = rName + "()";
				new Thread(new DataMigrate(procedure)).start();
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

	public static void stopService(String serviceName) throws IOException,
			InterruptedException {
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
			makeConnection();
			Thread t = new Thread(new DataMain());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public String checkCreateThread() {
		int cnt = 0;
		String flg = "Y";
		try {
			stmtCheck = connection.createStatement();

			rs = stmtCheck
					.executeQuery("select count(distinct(exe_flg)) from aml_core_process where active_flg='Y'");
			while (rs.next()) {
				cnt = rs.getInt(1);
			}

			if (cnt == 1) {
				rs = stmtCheck
						.executeQuery("select distinct(exe_flg) from aml_core_process where active_flg='Y'");
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

	@Override
	public void run() {
		while (true) {
			if (checkCreateThread().equals("N")) {
				// try {
				// stopService("Tomcat7");
				// } catch (IOException e1) {
				// e1.printStackTrace();
				// } catch (InterruptedException e1) {
				// e1.printStackTrace();
				// }
				/*MarkLeaveUser mkObj = new MarkLeaveUser();
				mkObj.reallocateLeaveUser();
				mkObj.changeUserNo();
				mkObj.makeDistribution();*/
				
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
		makeConnection();
		Thread t = new Thread(new DataMain());
		t.start();
	}

}

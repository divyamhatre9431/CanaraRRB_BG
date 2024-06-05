package com.idbi.intech.iaml.datapull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class DataPullingOra implements Runnable {
	private static Connection connectionOra = null;
	private static Connection connectionMs = null;
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
//			else {
//				System.out.println("connection is null");
//			}
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.out.println(sqlExp.getMessage());
		}
	}
	
	public static void makeMSSQLConnection() {
		try {
			connectionMs = ConnectionFactory.makeMSSQLConnection();
			if(connectionMs!=null) {
			System.out.println("MSSQL DB Connected.");
			}
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void createDataThread(){
		try {
			String date = "";
			stmt = connectionOra.createStatement();
			//rs = stmt.executeQuery("select to_date(sysdate,'dd-mm-yy') from dual");
			rs=stmt.executeQuery("SELECT CBS_DATE FROM AML_BIZZ_TAB WHERE OPE_FLG_RULE='N' AND OPE_FLG_SDN='N'");
			while (rs.next()) {
				date = rs.getString(1);
				System.out.println("BIZZ_TAB DATE->"+date);
			}
			System.out.println("Oracle Data Pulling Started for Date " + date);
			rs = stmt.executeQuery("select procd_name from aml_core_process where active_flg='Y' and exe_flg = 'N'");
			while (rs.next()) {
				String rName = rs.getString(1);
				String procedure = rName +"_TEST()";
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
			Thread t = new Thread(new DataPullingOra());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public String checkOracleCreateThread() {
		int cnt = 0;
		String flg = "Y";
		try {
			stmtCheck = connectionOra.createStatement();

			rs = stmtCheck.executeQuery("select count(distinct(exe_flg)) from aml_core_process where active_flg='Y'");
			while (rs.next()) {
				cnt = rs.getInt(1);
				//System.out.println(cnt +"<-oracle");
			}

			if (cnt == 1) {
				
				rs = stmtCheck.executeQuery("select distinct(exe_flg) from aml_core_process where active_flg='Y'");
				while (rs.next()) {
					flg = rs.getString(1);
					//System.out.println(flg+"<-oracle");
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

	public String checkMssqlCreateThread() {
		int cnt = 0;
		String flg = "Y";
		try {
			stmtCheck = connectionMs.createStatement();

			rs = stmtCheck.executeQuery("select count(distinct(exe_flg)) from aml_core_process where active_flg='Y'");
			while (rs.next()) {
				cnt = rs.getInt(1);
				//System.out.println(cnt+" ->ms");
			}

			if (cnt == 1) {
				rs = stmtCheck.executeQuery("select distinct(exe_flg) from aml_core_process where active_flg='Y'");
				while (rs.next()) {
					flg = rs.getString(1);
					//System.out.println(flg+" ->ms");
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
	
	private void DataPullOraToMs() {
		try {
			String date = "";
			stmt = connectionMs.createStatement();
//			rs = stmt.executeQuery("select format(getdate(),'dd-MM-yy')");
			rs=stmt.executeQuery(" SELECT CBS_DATE FROM AML_BIZZ_TAB WHERE OPE_FLG_RULE='N' AND OPE_FLG_SDN='N'");
			while (rs.next()) {
				date = rs.getString(1);
				System.out.println("BIZZ_TAB DATE in ms->"+date);
				
			}
			System.out.println("MS Data Pulling Started for Date " + date);
			rs = stmt.executeQuery("select procd_name from aml_core_process where active_flg='Y' and exe_flg = 'N'");
			while (rs.next()) {
				String rName = rs.getString(1);
				String procedure = rName +"_TEST()";
				System.out.println("Procedure Name :"+procedure);
				new Thread(new DataMigrateMs(procedure)).start();
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

	private boolean CheckBizzDate() {
		Statement stmtCheck = null;
		Statement stmt= null;
		ResultSet rsOra = null;
		ResultSet rsMs = null;
		boolean flg = false;
		String oraBizzDate=null;
		int count=0;
		try {
			stmt= connectionOra.createStatement();
			stmtCheck = connectionMs.createStatement();

			rsOra = stmt.executeQuery("SELECT TO_CHAR(CBS_DATE,'dd-MM-yyyy') FROM AML_BIZZ_TAB");
			while (rsOra.next()) {
				oraBizzDate = rsOra.getString(1);
				//System.out.println("Ora Bizz Date :"+oraBizzDate);
			}

			if (oraBizzDate!=null) {
				rsMs = stmtCheck.executeQuery("select COUNT(1) from aml_bizz_tab where format(CONVERT(DATETIME,CBS_DATE),'dd-MM-yyyy')='"+oraBizzDate+"'");
				while (rsMs.next()) {
					count = rsMs.getInt(1);
			//System.out.println("MS Bizz Count :"+count);
					
					if(count==1) {
						flg=true;
					}
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
			if ( checkOracleCreateThread().equals("N") && checkMssqlCreateThread().equals("N")) {
				createDataThread();
				System.out.println(checkOracleCreateThread()+" - "+ checkMssqlCreateThread()+" - "+CheckBizzDate());
				if(checkOracleCreateThread().equals("Y") && checkMssqlCreateThread().equals("N") && CheckBizzDate()==true) {
				System.out.println(checkOracleCreateThread()+" - "+ checkMssqlCreateThread()+" - "+CheckBizzDate());
				DataPullOraToMs();
				}
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
		makeMSSQLConnection();
		Thread t = new Thread(new DataPullingOra());
		t.start();
	}

}

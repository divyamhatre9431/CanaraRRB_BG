package com.idbi.intech.newCTR;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class STRGenerationService implements Runnable {

	private static Connection connection = null;
	Statement stmt = null;
	Statement stmtCheck = null;
	ResultSet rs = null;
	static STRGenerationService process_run = new STRGenerationService();

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
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
			makeConnection();
			Thread t = new Thread(new STRGenerationService());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}
	
	public List<String> unprocessedStr() {
		
		List<String> strList = new ArrayList<String>();
		
		try {
			stmt = connection.createStatement();
			rs=stmt.executeQuery("select req_no||'~'||LAST_USER_ID from aml_str_request where status='V'");
			
			while(rs.next()){
				String strNo = rs.getString(1);
				strList.add(strNo);
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
		
		return strList;
	}
	
	
	public void updateStrStatus(String strNo) {
		try {
			
			stmt = connection.createStatement();
			stmt.executeUpdate("update aml_str_request set status='A' where req_no='"+strNo+"'");
			connection.commit();
			
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
			List<String> strList = unprocessedStr();
			
			if(strList.size() > 0)
			{
				GenStrReport strReport = new GenStrReport();
				for(String strDetails : strList)
				{
					String strDetailsArr[] = strDetails.split("~");
					boolean flg = false;
					
					try
					{
						System.out.println("STR creation process started for request no : "+strDetailsArr[0]);
						flg = strReport.createXmlFile(strDetailsArr[0], strDetailsArr[1]);
						System.out.println("STR creation process completed for request no : "+strDetailsArr[0]);
					}
					catch(Exception e)
					{
						flg = false;
						strReport.executeResetStrProc(strDetailsArr[0]);
						System.out.println("STR creation process failed for request no : "+strDetailsArr[0]);
						e.printStackTrace();
					}
					
					if(flg)
					{
						updateStrStatus(strDetailsArr[0]);
					}
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static void main(String[] args) {
		makeConnection();
		Thread t = new Thread(new STRGenerationService());
		t.start();
	}

}

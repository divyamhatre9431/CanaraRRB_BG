package com.idbi.intech.iaml.screening;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class OpenWLNameScreenThread implements Runnable {
	
	//private static final Logger logger = Logger.getLogger(NameScreenThread.class);
	//private static Connection connection = null;
	
	/*
	 * public static void makeConnection() throws SQLException { connection =
	 * ConnectionFactory.makeConnectionAMLLive(); }
	 */

	@Override
	public void run() {
		while (true) {
			try {
				Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				InputStream is;
				is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				int sleepTime = Integer.valueOf(amlProp.getProperty("sleepTime"));
				int dbDownSleepTime = Integer.valueOf(amlProp.getProperty("dbDownSleepTime"));
				
				boolean dbStatus = checkDbStatus();
				
				if(dbStatus)
				{
					scanCustomer();
				}
				else
				{
					Thread.sleep(dbDownSleepTime);
				}
				
				Thread.sleep(sleepTime);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void scanCustomer() {
		
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is;
			is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			
			String inputFilePath = amlProp.getProperty("ntbplRequestPath");
			String responsePath = amlProp.getProperty("ntbplResponsePath");
			String responseLogPath = amlProp.getProperty("ntbplResponseLogPath");
			String processedPath = amlProp.getProperty("ntbplProcessed");
			String logsFlg = amlProp.getProperty("LOGS_FLG");
			
			int maxThreads = Integer.valueOf(amlProp.getProperty("maxThreads"));
			int cnt = 0;
			
			File reqFiles = new File(inputFilePath);
			String fname[] = reqFiles.list();
			
			for (String reqFile : fname)
			{
				cnt++;
				new Thread(new ScanningThreadOpenWL(reqFile, inputFilePath, processedPath, responsePath, responseLogPath, logsFlg)).start();
				
				if(cnt == maxThreads)
				{
					break;
				}
			}
		} catch (IOException e) {
			//logger.info("Exception occured");
			e.printStackTrace();
		}

	}
	
	
	public boolean checkDbStatus() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean flg = false;
		
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			String checkSQL = "select * from dual";
			stmt = connection.prepareStatement(checkSQL);
			rs = stmt.executeQuery();
			
			while(rs.next())
			{
				flg = true;
				rs.getString(1);
			}
			
			stmt.close();
			connection.close();
			
		} catch (Exception ex) {
			flg = false;
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
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}
	
	
	public static void main(String args[]) {
		try {
			//makeConnection();
			Thread threadObj = new Thread(new OpenWLNameScreenThread());
			threadObj.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

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

public class OpenWLNameScreenThreadNMSCR implements Runnable {
	
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
			
			String inputFilePath = amlProp.getProperty("vdokycDcaRequestPath");
			String responsePath = amlProp.getProperty("vdokycDcaResponsePath");
			String responseLogPath = amlProp.getProperty("vdokycDcaResponseLogPath");
			String processedPath = amlProp.getProperty("vdokycDcaProcessed");
			
			int maxThreads = Integer.valueOf(amlProp.getProperty("maxThreads"));
			int cnt = 0;
			
			File reqFiles = new File(inputFilePath);
			String fname[] = reqFiles.list();
			
			for (String reqFile : fname)
			{
				cnt++;
				new Thread(new ScanningThreadNMSCR(reqFile, inputFilePath, processedPath, responsePath, responseLogPath)).start();
				
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
			Thread threadObj = new Thread(new OpenWLNameScreenThreadNMSCR());
			threadObj.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

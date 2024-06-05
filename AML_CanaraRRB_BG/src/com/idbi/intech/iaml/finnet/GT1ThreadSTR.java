package com.idbi.intech.iaml.finnet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GT1ThreadSTR implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;
	private final static String patternfordb2 = "yyyy-MM-dd";
	
	String strNo = "";
	
	public GT1ThreadSTR(String strNo) {
		this.strNo = strNo;
	}

	private static void makeConnectionToDataWarehouse() {

		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			connectionDataWarehouse = ConnectionFactory.makeConnectionEDWLive();
			System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::");
			System.out.println("EDW connection established successfully...");
		} catch (SQLException | ClassNotFoundException e) {

			e.printStackTrace();
			System.err.println("Exception e:" + e);
		}
	}
	

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::");
			System.out.println("AML connection established successfully...");
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	
	public static List<String> getStrTransactionData(String strNo)
	{
		List<String> custList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		
		try
		{
			sql = "select * from aml_transaction_master_f where TRAN_CHANNEL in ('GTXN','NACH') and str_no = '"+strNo+"' ";
			
			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				System.out.println("Extracted Customer Details : "+rs.getString(1));
				custList.add(rs.getString(1));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return custList;
	}

	
	
	public void executeProc(String procedure,String requestId) {
		
		Statement stmt = null;
		
		try {
			stmt = connection.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("call " + procedure+"('"+requestId+"')");

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println("Done execution for : " + procedure
					+ " Total Time elapsed : " + (elapsedTime / 1000)
					+ " Seconds");
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
	
	
	@Override
	public void run() {
		
		try
		{
			makeConnection();
			makeConnectionToDataWarehouse();
			
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			//System.out.println("Current Directory::"+dir);
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			String procedure = amlProp.getProperty("GT1_PL_STR");
			
			String status = ConstantFunctions.getThreadStatus("GT1");
			
			if(status.equals("N"))
			{
				ConstantFunctions.updateThreadStatus("GT1", "P");
				List<String> strTxnList = getStrTransactionData(strNo);
				
				if(strTxnList.size() > 0)
				{
					executeProc(procedure,strNo);
				}
				else
				{
					
				}
				
				ConstantFunctions.updateThreadStatus("GT1", "Y");
				String threadStatus = ConstantFunctions.checkStatus("GT1");
				
				if(threadStatus.equals("Y"))
				{
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}
			}
		}
		catch(Exception e)
		{
			try {
				ConstantFunctions.updateThreadStatus("GT1", "X");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

}

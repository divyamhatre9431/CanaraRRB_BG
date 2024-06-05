package com.idbi.intech.iaml.finnet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AcctPersnRelnDataExtractionService {

	private static Connection connectionDataWarehouse;
	private static Connection connection;

	private static void makeConnectionToDataWarehouse() {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			connectionDataWarehouse = DriverManager.getConnection("jdbc:db2://10.192.206.100:50000/BLUDB", "PK5187679",
					"PKpnbobc@679");
			System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::");
			System.out.println("EDW connection established successfully...");
			connectionDataWarehouse.setAutoCommit(false);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
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


	public static List<String> getReportData(String reportType, String fromDate, String toDate, String strNo) {
		List<String> custList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			if (reportType.equals("NTR")) {
				sql = "select distinct cust_id||'-'||acid||'-'||REPORT_REF_NUM||'-'||ACCOUNT_NO from ntr_details_finnet where tran_date between '"+fromDate+"' and '"+toDate+"' ";
			} else if (reportType.equals("CTR")) {
				sql = "select distinct tran_cust_id||'-'||acid||'-'||REPORT_REF_NUM||'-'||ACCOUNT_NO from ctr_details_finnet where tran_date between '"+fromDate+"' and '"+toDate+"' ";
			} else {
				sql = "select distinct cust_id||'-'||'NA' from aml_str_req_acct where req_no='" + strNo + "' ";
			}

			pstmt = connection.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				System.out.println("Extracted Customer Details : " + rs.getString(1));
				custList.add(rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	
	public static void updateProcessStatus(String requestId,String reportType,String status,String fileType) throws SQLException
	{
		PreparedStatement pstmtUpdateIntoAml = null;
		
		try
		{
			pstmtUpdateIntoAml = connection.prepareStatement(
					"update REG_REQUEST_PROCESS set STATUS=? where REQUEST_ID=? and REG_REPORT_TYPE=? and FILE_TYPE=? ");
			pstmtUpdateIntoAml.setString(1, status);
			pstmtUpdateIntoAml.setString(2, requestId);
			pstmtUpdateIntoAml.setString(3, reportType);
			pstmtUpdateIntoAml.setString(4, fileType);
			
			pstmtUpdateIntoAml.execute();
			connection.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (pstmtUpdateIntoAml != null) {
				pstmtUpdateIntoAml.close();
				pstmtUpdateIntoAml = null;
			}
		}
	}

	
	public static void main(String[] args) throws IOException, SQLException {
		
		Properties amlProp = new Properties();
		String dir = System.getProperty("user.dir");
		//System.out.println("Current Directory::"+dir);
		InputStream is = new FileInputStream(dir + "/aml-config.properties");
		amlProp.load(is);
		is.close();
		String threadSize = amlProp.getProperty("ACCT_PERSN_THREAD_SIZE");

		makeConnection();
		makeConnectionToDataWarehouse();

		RequestDataBean reqObj = ConstantFunctions.getRequestData("APR");
		String reportType = reqObj.getReportType();
		String fromDate = reqObj.getFromDate();
		String toDate = reqObj.getEndDate();
		String strNo = reqObj.getRequestId();
		String requestId = reqObj.getRequestId();
		String batchNo = reqObj.getBatchNo();
		String fileType = reqObj.getFileType();

		List<String> reportList = getReportData(reportType, fromDate, toDate, strNo);
		System.out.println("reportList : " + reportList.size());

		updateProcessStatus(requestId,reportType,"P",fileType);
		
		int i = 0;
		int size = Integer.valueOf(threadSize);
		
	    for (int start = 0; start < reportList.size(); start += size) {
	    	
	    	i++;
	        int end = Math.min(start + size, reportList.size());
	        List<String> sublist = reportList.subList(start, end);
	        String tid = "T"+String.valueOf(i);
	        
	        new Thread(new AcctRelnPersnThread(sublist, requestId, batchNo, fromDate, toDate,reportType, tid)).start();
	    }
	}

}

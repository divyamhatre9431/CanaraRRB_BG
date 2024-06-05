package com.idbi.intech.iaml.finnet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class TS2DataExtractionService {

	private static Connection connection;
	private final static String patternfordb2 = "yyyy-MM-dd";

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
	

	public static List<String> getReportData(String reportType, String fromDate, String toDate, String strNo)
	{
		List<String> custList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		
		try
		{
			if(reportType.equals("NTR"))
			{
				sql = "select to_char(tran_date,'"+patternfordb2+"')||'~'||SUBSTR(tran_particular,9,12)||'~'||decode(CRE_DEB_FLG,'C','I','D','O',CRE_DEB_FLG)||'~'||REPORT_REF_NUM||'~'||TRAN_CUST_ID from ntr_details_finnet where TRAN_CHANNEL = 'IMPS' and tran_date between '"+fromDate+"' and '"+toDate+"' ";
			}
			else if(reportType.equals("CTR"))
			{
				sql = "select to_char(tran_date,'"+patternfordb2+"')||'~'||SUBSTR(tran_particular,9,12)||'~'||decode(CRE_DEB_FLG,'C','I','D','O',CRE_DEB_FLG)||'~'||REPORT_REF_NUM||'~'||TRAN_CUST_ID from ctr_details_finnet where TRAN_CHANNEL = 'IMPS' and tran_date between '"+fromDate+"' and '"+toDate+"' ";
			}
			else
			{
				sql = "select tickets from aml_str_req_tickets where req_no='"+strNo+"' ";
			}
			
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
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, SQLException {
		
		Properties amlProp = new Properties();
		String dir = System.getProperty("user.dir");
		//System.out.println("Current Directory::"+dir);
		InputStream is = new FileInputStream(dir + "/aml-config.properties");
		amlProp.load(is);
		is.close();
		String threadSize = amlProp.getProperty("TS2_THREAD_SIZE");

		makeConnection();
		
		RequestDataBean reqObj = ConstantFunctions.getRequestData("TS2");
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
	        
	        new Thread(new TS2Thread(sublist, requestId, batchNo, fromDate, toDate, reportType, tid)).start();
	    }
	}
	

}

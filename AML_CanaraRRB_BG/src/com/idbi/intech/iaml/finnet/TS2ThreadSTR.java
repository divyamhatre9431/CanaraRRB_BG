package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class TS2ThreadSTR implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;
	private final static String patternfordb2 = "yyyy-MM-dd";
	
	String strNo = "";
	
	public TS2ThreadSTR(String strNo) {
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
			sql = "select to_char(tran_date,'"+patternfordb2+"')||'~'||REGEXP_SUBSTR(tran_particular,'[^/]+',1,2)||'~'||decode(CRE_DEB_FLG,'C','I','D','O',CRE_DEB_FLG)||'~'||REPORT_REF_NUM||'~'||STR_NO||'~'||BATCH_NUM||'~'||REPORT_TYPE||'~'||TRAN_CUST_ID from aml_transaction_master_f where TRAN_CHANNEL = 'IMPS' and str_no = '"+strNo+"' ";
			
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
	
	
	private static void insertDataFromWarehouseToAml(List<String> reportDataList) {
		
		Statement stFetchTxnWarehouse = null;
		//CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into STR_REG_TS2(TRAN_CUST_ID,TRANSACTION_DATE,TRANSACTION_TIME,SENDER_NAME,BENEF_NAME,SENDER_MOBILE_NO,"
					+ "BENEF_MOBILE_NO,SENDER_MMID,SENDER_ACCOUNT_NO,BENEF_MMID,BENEF_ACCOUNT_NO,BENEF_IFSC_CODE,TRANSACTION_TYPE,"
					+ "TRANSACTION_AMOUNT,NARRATION,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE,"
					+ "TRANSACTION_ID) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			for(String custId : reportDataList)
			{

				String[] txnDataArr = custId.split("~");
				String tranDate = txnDataArr[0].toString();
				String tranRefNo = txnDataArr[1].toString();
				String tranType = txnDataArr[2].toString();
				String repRefNum = txnDataArr[3].toString();
				String requestId = txnDataArr[4].toString();
				String batchNo = txnDataArr[5].toString();
				String reportType = txnDataArr[6].toString();
				String tranCustId = txnDataArr[7].toString();
				
				String acctDtlsQuery = "";
				
				if(tranType.equals("O"))
				{
					acctDtlsQuery = "select REMITTORID TRAN_CUST_ID, TXNDATE TRANSACTION_DATE, TXNDATE TRANSACTION_TIME, REMITTORNAME SENDER_NAME, BENENAME BENEF_NAME, REMITTOR_MOBILE SENDER_MOBILE_NO, BENE_MOBILE BENEF_MOBILE_NO, REMITTORMMID SENDER_MMID,REMITTORACCOUNTNUMBER SENDER_ACCOUNT_NO, BENEMMID BENEF_MMID,BENEACCOUNTNUMBER BENEF_ACCOUNT_NO,BENEIFSC BENEF_IFSC_CODE, TRANTYPE TRANSACTION_TYPE, AMOUNT TRANSACTION_AMOUNT, PURPOSE_CODE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS "
							+ "from PNB_ODS.IMPS_TBL_TRANLOG_OUTWARD where txndate between '"+tranDate+" 00:00:00.000' and '"+tranDate+" 23:59:59.999' and RRN='"+tranRefNo+"' ";
				}
				else
				{
					acctDtlsQuery = "select REMITTORID TRAN_CUST_ID, TXNDATE TRANSACTION_DATE, TXNDATE TRANSACTION_TIME, REMITTORNAME SENDER_NAME, BENENAME BENEF_NAME, REMITTOR_MOBILE SENDER_MOBILE_NO, BENE_MOBILE BENEF_MOBILE_NO, REMITTORMMID SENDER_MMID,REMITTORACCOUNTNUMBER SENDER_ACCOUNT_NO, BENEMMID BENEF_MMID,BENEACCOUNTNUMBER BENEF_ACCOUNT_NO,BENEIFSC BENEF_IFSC_CODE, TRANTYPE TRANSACTION_TYPE, AMOUNT TRANSACTION_AMOUNT, PURPOSE_CODE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS "
							+ "from PNB_ODS.IMPS_TBL_TRANLOG where txndate between '"+tranDate+" 00:00:00.000' and '"+tranDate+" 23:59:59.999' and RRN='"+tranRefNo+"' ";
				}
				
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

				while (rsTxnDataWarehouse.next()) {
					
					commitCounter++;
					
					//pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("TRAN_CUST_ID"));
					pstmtIsertIntoAml.setString(1, tranCustId);
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("TRANSACTION_DATE"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("TRANSACTION_TIME"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("SENDER_NAME"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("BENEF_NAME"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("SENDER_MOBILE_NO"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("BENEF_MOBILE_NO"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("SENDER_MMID"));
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("SENDER_ACCOUNT_NO"));
					pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("BENEF_MMID"));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("BENEF_ACCOUNT_NO"));
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("BENEF_IFSC_CODE"));
					pstmtIsertIntoAml.setString(13, tranType);
					pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString("TRANSACTION_AMOUNT"));
					pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString("NARRATION"));
					pstmtIsertIntoAml.setString(16, requestId);
					pstmtIsertIntoAml.setString(17, batchNo);
					pstmtIsertIntoAml.setString(18, repRefNum);
					pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString("RECORD_STATUS"));
					pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
					pstmtIsertIntoAml.setString(21, reportType);
					pstmtIsertIntoAml.setString(22, tranRefNo);
					
					pstmtIsertIntoAml.execute();

					System.out.println(commitCounter);
					if (commitCounter == 500) {
						connection.commit();
						commitCounter = 0;
					}
				}
				
			}
			
			connection.commit();
			System.out.println(" :::::::::::::::Process Completed Successfully::::::::::::::::");
			

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
				if (rsTxnDataWarehouse != null) {
					rsTxnDataWarehouse.close();
					rsTxnDataWarehouse = null;
				}
				if (pstmtIsertIntoAml != null) {
					pstmtIsertIntoAml.close();
					pstmtIsertIntoAml = null;
				}
				if (stFetchTxnWarehouse != null) {
					stFetchTxnWarehouse.close();
					stFetchTxnWarehouse = null;
				}
				/*
				 * if (connectionDataWarehouse != null) { connectionDataWarehouse.close();
				 * connectionDataWarehouse = null; }
				 */
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void run() {
		
		try
		{
			makeConnection();
			makeConnectionToDataWarehouse();
			
			String status = ConstantFunctions.getThreadStatus("TS2");
			
			if(status.equals("N"))
			{
				ConstantFunctions.updateThreadStatus("TS2", "P");
				List<String> strTxnList = getStrTransactionData(strNo);
				
				if(strTxnList.size() > 0)
				{
					insertDataFromWarehouseToAml(strTxnList);
				}
				else
				{
					
				}
				
				ConstantFunctions.updateThreadStatus("TS2", "Y");
				String threadStatus = ConstantFunctions.checkStatus("TS2");
				
				if(threadStatus.equals("Y"))
				{
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}
			}
		}
		catch(Exception e)
		{
			try {
				ConstantFunctions.updateThreadStatus("TS2", "X");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

}

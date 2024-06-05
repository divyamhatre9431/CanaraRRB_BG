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

public class TS1ThreadSTR implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;
	private final static String patternfordb2 = "yyyy-MM-dd";
	
	String strNo = "";
	
	public TS1ThreadSTR(String strNo) {
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
	
	
	public static List<String> getStrTransactionData(String strNo) {
		List<String> custList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			sql = "select to_char(tran_date,'" + patternfordb2
					+ "')||'~'||decode(TRAN_CHANNEL,'NEFT',SUBSTR(TRAN_RMKS,1,16),'RTGS',SUBSTR(TRAN_PARTICULAR,7,22),'NA')||'~'||decode(CRE_DEB_FLG,'C','I','D','O',CRE_DEB_FLG)||'~'||decode(TRAN_CHANNEL,'RTGS','NRTGS',TRAN_CHANNEL)||'~'||REPORT_REF_NUM||'~'||STR_NO||'~'||BATCH_NUM||'~'||REPORT_TYPE from AML_TRANSACTION_MASTER_F where TRAN_CHANNEL in ('NEFT','RTGS') and str_no = '"+strNo+"' ";

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

	
	private static void insertDataFromWarehouseToAml(List<String> reportDataList) {
		
		Statement stFetchTxnWarehouse = null;
		//CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;
		

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into STR_REG_TS1(TRAN_CUST_ID,TRANSACTION_DATE,TRANSACTION_TIME,TRANSACTION_ID,SENDER_IFSC_CODE,SENDER_ACCOUNT_NO,"
					+ "BENEF_IFSC_CODE,BENEF_ACCOUNT_NO,TRANSACTION_TYPE,TRANSACTION_AMOUNT,NARRATION,"
					+ "REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
			
			for(String custId : reportDataList)
			{
				String[] txnDataArr = custId.split("~");
				String tranDate = txnDataArr[0].toString();
				String tranRefNo = txnDataArr[1].toString();
				String tranType = txnDataArr[2].toString();
				String tranChannel = txnDataArr[3].toString();
				String repRefNum = txnDataArr[4].toString();
				String requestId = txnDataArr[5].toString();
				String batchNo = txnDataArr[6].toString();
				String reportType = txnDataArr[7].toString();
				
				String acctDtlsQuery = "select CIF_ID TRAN_CUST_ID,LCHG_TIME TRANSACTION_DATE, LCHG_TIME TRANSACTION_TIME,TRANSACTION_REF TRANSACTION_ID,SENDER_BIC SENDER_IFSC_CODE,DR_ACCT SENDER_ACCOUNT_NO,RECEIVER_BIC BENEF_IFSC_CODE,CR_ACCT BENEF_ACCOUNT_NO,SERVICE_TYPE TRANSACTION_TYPE, SETTLEMENT_AMT TRANSACTION_AMOUNT,PURPOSE_REMARKS NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from PNB_ODS.RTGS_NEFT_PY_ED "
						+ "where CUST_PROCESSING_DATE='"+tranDate+"' and PAYSYS_ID='"+tranChannel+"' and SERVICE_TYPE='"+tranType+"' and TRANSACTION_REF='"+tranRefNo+"' ";
				
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

				while (rsTxnDataWarehouse.next()) {
					
					commitCounter++;
					
					pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("TRAN_CUST_ID"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("TRANSACTION_DATE"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("TRANSACTION_TIME"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("TRANSACTION_ID"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("SENDER_IFSC_CODE"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("SENDER_ACCOUNT_NO"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("BENEF_IFSC_CODE"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("BENEF_ACCOUNT_NO"));
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("TRANSACTION_TYPE"));
					pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("TRANSACTION_AMOUNT"));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("NARRATION"));
					pstmtIsertIntoAml.setString(12, requestId);
					pstmtIsertIntoAml.setString(13, batchNo);
					pstmtIsertIntoAml.setString(14, repRefNum);
					pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString("RECORD_STATUS"));
					pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
					pstmtIsertIntoAml.setString(17, reportType);

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
			
			String status = ConstantFunctions.getThreadStatus("TS1");
			
			if(status.equals("N"))
			{
				ConstantFunctions.updateThreadStatus("TS1", "P");
				List<String> strTxnList = getStrTransactionData(strNo);
				
				if(strTxnList.size() > 0)
				{
					insertDataFromWarehouseToAml(strTxnList);
				}
				else
				{
					
				}
				
				ConstantFunctions.updateThreadStatus("TS1", "Y");
				String threadStatus = ConstantFunctions.checkStatus("TS1");
				
				if(threadStatus.equals("Y"))
				{
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}
			}
		}
		catch(Exception e)
		{
			try {
				ConstantFunctions.updateThreadStatus("TS1", "X");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

}

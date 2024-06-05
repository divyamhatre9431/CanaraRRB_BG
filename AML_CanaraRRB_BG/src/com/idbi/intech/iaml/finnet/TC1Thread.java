package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class TC1Thread implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;

	List<String> reportDataList = null;
	String requestId = "";
	String batchNo = "";
	String fromDate = "";
	String toDate = "";
	String reportType = "";
	String tid = "";
	
	public TC1Thread(List<String> reportDataList, String requestId, String batchNo,
			String fromDate, String toDate, String reportType, String tid) {
		this.reportDataList = reportDataList;
		this.requestId = requestId;
		this.batchNo = batchNo;
		this.batchNo = batchNo;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.reportType = reportType;
		this.tid = tid;
	}
	
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

	private static void insertDataFromWarehouseToAml(List<String> reportDataList, String requestId, String batchNo,
			String fromDate, String toDate, String reportType) {

		Statement stFetchTxnWarehouse = null;
		// CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_TC1(TRAN_CUST_ID,ATM_CAM_ID,TRANSACTION_ID,SWITCH_TXN_ID,TRANSACTION_AMOUNT,DEPOSIT_WITHDRAWAL,"
							+ "TRANSACTION_DATE,TRANSACTION_TIME,ACCOUNT_NUMBER,ACCOUNT_TYPE,CARD_TYPE,CARD_NUMBER,TRANSACTION_STATUS,BRANCH_ID_AC,"
							+ "CARD_ISSUE_CNTRY,CARD_ISSUE_BANK,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			for (String custId : reportDataList) {
				String[] txnDataArr = custId.split("~");
				String tranId = txnDataArr[0].toString();
				String tranDate = txnDataArr[1].toString();
				// String tranTime = txnDataArr[1].toString();
				String tranCustId = txnDataArr[2].toString();
				String acctNo = txnDataArr[3].toString();
				String creDebFlg = txnDataArr[4].toString();
				String tranAmt = txnDataArr[5].toString();
				String solId = txnDataArr[6].toString();
				String txnSrNo = txnDataArr[7].toString();
				String txnMode = txnDataArr[8].toString();
				String repRefNum = txnDataArr[9].toString();

				if (txnSrNo.length() < 4) {
					txnSrNo = StringUtils.leftPad(txnDataArr[7].toString(), 4, "0");
				}

				String tc1Query = "";

				if (txnMode.equalsIgnoreCase("CDS/CRT")) {
					tc1Query = "select ATM_ID,OSEQ_NUM,PAN_CARD_NO,TRAN_STATUS,'N' RECORD_STATUS, 'N' VALIDATION_STATUS,TRAN_TIME from PNB_ODS.ATMSWTCH_ATMTRAN where TRAN_STATUS='00' and TRANSACTION_DATE = '"
							+ tranDate + "' and to_account='" + acctNo + "' and TXN_SNO='" + txnSrNo
							+ "' and TXN_AMOUNT=" + tranAmt + " ";
				} else {
					tc1Query = "select ATM_ID,OSEQ_NUM,PAN_CARD_NO,TRAN_STATUS,'N' RECORD_STATUS, 'N' VALIDATION_STATUS,TRAN_TIME from PNB_ODS.ATMSWTCH_ATMTRAN where TRAN_STATUS='00' and TRANSACTION_DATE = '"
							+ tranDate + "' and from_account='" + acctNo + "' and TXN_SNO='" + txnSrNo
							+ "' and TXN_AMOUNT=" + tranAmt + " ";
				}

				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(tc1Query);

				while (rsTxnDataWarehouse.next()) {

					commitCounter++;

					pstmtIsertIntoAml.setString(1, tranCustId);
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("ATM_ID"));
					pstmtIsertIntoAml.setString(3, tranId);
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("OSEQ_NUM"));
					pstmtIsertIntoAml.setString(5, tranAmt);
					pstmtIsertIntoAml.setString(6, creDebFlg);
					pstmtIsertIntoAml.setString(7, tranDate);
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("TRAN_TIME"));
					pstmtIsertIntoAml.setString(9, acctNo);
					pstmtIsertIntoAml.setString(10, "");
					pstmtIsertIntoAml.setString(11, "");
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("PAN_CARD_NO"));
					pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("TRAN_STATUS"));
					pstmtIsertIntoAml.setString(14, solId);
					pstmtIsertIntoAml.setString(15, "95");
					pstmtIsertIntoAml.setString(16, "PNB");
					pstmtIsertIntoAml.setString(17, requestId);
					pstmtIsertIntoAml.setString(18, batchNo);
					pstmtIsertIntoAml.setString(19, repRefNum);
					pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString("RECORD_STATUS"));
					pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
					pstmtIsertIntoAml.setString(22, reportType);

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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addthread(String requestId,String reportType,String tid,String status) throws SQLException
	{
		PreparedStatement pstmtIsertIntoAml = null;
		
		try
		{
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_REQUEST_THREADS(REQUEST_ID,REG_REPORT_TYPE,TID,STATUS,FILE_TYPE) "
							+ "values (?,?,?,?,?)");
			pstmtIsertIntoAml.setString(1, requestId);
			pstmtIsertIntoAml.setString(2, reportType);
			pstmtIsertIntoAml.setString(3, tid);
			pstmtIsertIntoAml.setString(4, status);
			pstmtIsertIntoAml.setString(5, "TC1");
			
			pstmtIsertIntoAml.execute();
			connection.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (pstmtIsertIntoAml != null) {
				pstmtIsertIntoAml.close();
				pstmtIsertIntoAml = null;
			}
		}
		
	}
	
	
	public static void updateThreadStatus(String requestId,String reportType,String tid,String status) throws SQLException
	{
		PreparedStatement pstmtUpdateIntoAml = null;
		
		try
		{
			pstmtUpdateIntoAml = connection.prepareStatement(
					"update REG_REQUEST_THREADS set STATUS=? where REQUEST_ID=? and REG_REPORT_TYPE=? and TID=? and FILE_TYPE=? ");
			pstmtUpdateIntoAml.setString(1, status);
			pstmtUpdateIntoAml.setString(2, requestId);
			pstmtUpdateIntoAml.setString(3, reportType);
			pstmtUpdateIntoAml.setString(4, tid);
			pstmtUpdateIntoAml.setString(5, "TC1");
			
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

	@Override
	public void run() {
		
		try
		{
			makeConnection();
			makeConnectionToDataWarehouse();
			
			addthread(requestId, reportType, tid, "P");
			insertDataFromWarehouseToAml(reportDataList,requestId,batchNo,fromDate,toDate,reportType);
			updateThreadStatus(requestId, reportType, tid, "Y");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}

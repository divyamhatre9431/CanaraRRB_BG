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

public class TC1ThreadSTR implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;
	private final static String patternfordb2 = "yyyy-MM-dd";
	
	String strNo = "";
	
	public TC1ThreadSTR(String strNo) {
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
			sql = "select tran_id||'~'||to_char(tran_date,'"+patternfordb2+"')||'~'||TRAN_CUST_ID||'~'||ACCOUNT_NO||'~'||CRE_DEB_FLG||'~'||TRAN_AMT||'~'||BRANCH_ID||'~'||decode(SUBSTR(tran_particular, 1, 7),'CDS/CRT',lpad(regexp_substr(tran_particular,'[^ /]+',1,4),4,'0'),'ATM WDR',regexp_substr(tran_particular,'[^ ]+',1,3),'NA')||'~'||SUBSTR(tran_particular, 1, 7)||'~'||REPORT_REF_NUM||'~'||STR_NO||'~'||BATCH_NUM||'~'||REPORT_TYPE "
					+ "from aml_transaction_master_f where TRAN_CHANNEL = 'ATM' and str_no = '"+strNo+"' ";
			
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
		// CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into STR_REG_TC1(TRAN_CUST_ID,ATM_CAM_ID,TRANSACTION_ID,SWITCH_TXN_ID,TRANSACTION_AMOUNT,DEPOSIT_WITHDRAWAL,"
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
				String requestId = txnDataArr[10].toString();
				String batchNo = txnDataArr[11].toString();
				String reportType = txnDataArr[12].toString();

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
	
	
	@Override
	public void run() {
		
		try
		{
			makeConnection();
			makeConnectionToDataWarehouse();
			
			String status = ConstantFunctions.getThreadStatus("TC1");
			
			if(status.equals("N"))
			{
				ConstantFunctions.updateThreadStatus("TC1", "P");
				List<String> strTxnList = getStrTransactionData(strNo);
				
				if(strTxnList.size() > 0)
				{
					insertDataFromWarehouseToAml(strTxnList);
				}
				else
				{
					
				}
				
				ConstantFunctions.updateThreadStatus("TC1", "Y");
				String threadStatus = ConstantFunctions.checkStatus("TC1");
				
				if(threadStatus.equals("Y"))
				{
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}
			}
		}
		catch(Exception e)
		{
			try {
				ConstantFunctions.updateThreadStatus("TC1", "X");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

}

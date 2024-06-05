package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AcctDtlsThread implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;

	List<String> reportDataList = null;
	String requestId = "";
	String batchNo = "";
	String reportType = "";
	String fromDate = "";
	String toDate = "";
	String tid = "";
	
	public AcctDtlsThread(List<String> reportDataList, String requestId, String batchNo,
			String fromDate,String toDate,String reportType, String tid) {
		this.reportDataList = reportDataList;
		this.requestId = requestId;
		this.batchNo = batchNo;
		this.reportType = reportType;
		this.fromDate = fromDate;
		this.toDate = toDate;
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

	
	private static void insertDataFromWarehouseToAml(List<String> reportDataList,String requestId,String batchNo, String fromDate, String toDate, String reportType) {
		
		Statement stFetchTxnWarehouse = null;
		//CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {
			
			SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
			SimpleDateFormat db2Fmt = new SimpleDateFormat("yyyyMMdd");
			
			Date dtfromDate = fmt.parse(fromDate);
			Date dateBefore = DateUtils.addDays(dtfromDate, -365);
			Date dateAfter = DateUtils.addDays(dtfromDate, -1);
			String dateBeforeStr = db2Fmt.format(dateBefore);
			String dateAfterStr = db2Fmt.format(dateAfter);

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_ACCOUNT_DTLS(cust_id,branch_code,account_number,account_type,date_of_ac_opn,account_status,"
					+ "date_of_ac_cls,no_of_debit,total_debit_amt,no_of_credit,total_credit_amt,total_cash_credit,total_cash_debit,no_of_cash_txn,"
					+ "REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE,REASON_FOR_FREEZE) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			for(String custId : reportDataList)
			{
				String[] custIdArr = custId.split("-");
				String customerId = custIdArr[0].toString();
				String customerAcid = custIdArr[1].toString();
				String repRefNum = custIdArr[2].toString();
				
				//String acctDtlsQuery = "select cust_id,SOL_ID branch_code,FORACID account_number,SCHM_TYPE account_type, ACCT_OPN_DATE date_of_ac_opn,ACCT_CLS_FLG account_status,ACCT_CLS_DATE date_of_ac_cls, 0.00 no_of_debit,0.00 total_debit_amt,0.00 no_of_credit,0.00 total_credit_amt,0.00 total_cash_credit, 0.00 total_cash_debit,0 no_of_cash_txn,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from pnb_ods.cbs_gam where cust_id='"+customerId+"' AND FORACID='"+customerAcid+"' ";
				
				String acctDtlsQuery = "select *,(select frez_reason_code from mis_tmp.dim_arngmnt_crn_row where account_nbr=account_number) REASON_FOR_FREEZE "
						+ "FROM (select a.IP_CODE cust_id,a.branch_code,a.ACCOUNT_NBR account_number,a.PD_STC_TYPE account_type, "
						+ "a.ACCT_OPN_DT date_of_ac_opn,a.ACCT_STATUS account_status,a.ACCT_CLS_DT date_of_ac_cls,"
						+ "sum(b.DEBIT_NBR) no_of_debit,sum(b.DEBIT_AMT) total_debit_amt,sum(b.CREDIT_NBR) no_of_credit,"
						+ "sum(b.CREDIT_AMT) total_credit_amt,sum(b.CASH_DEBIT_NBR) no_of_cash_debit,sum(b.CASH_DEBIT_AMT) total_cash_debit,"
						+ "sum(b.CASH_CREDIT_NBR) no_of_cash_credit,sum(b.CASH_CREDIT_AMT) total_cash_credit "
						+ "from mis.dim_arngmnt a,mis.fact_arngmnt_txn_m_summ b where a.ARNGMNT_ID=b.ARNGMNT_ID "
						+ "and cal_id between '"+dateBeforeStr+"' and '"+dateAfterStr+"' and ACCOUNT_NBR='"+customerAcid+"' and IP_CODE='"+customerId+"' "
						+ "group by a.IP_CODE,a.branch_code,a.ACCOUNT_NBR,a.PD_STC_TYPE,a.ACCT_OPN_DT,a.ACCT_STATUS,a.ACCT_CLS_DT)";
				
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

				while (rsTxnDataWarehouse.next()) {
					
					long noOfCashTxn = Long.valueOf(rsTxnDataWarehouse.getString("no_of_cash_debit")) + Long.valueOf(rsTxnDataWarehouse.getString("no_of_cash_credit"));
					
					commitCounter++;
					
					pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("cust_id"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("branch_code"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("account_number"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("account_type"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("date_of_ac_opn"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("account_status"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("date_of_ac_cls"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("no_of_debit"));
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("total_debit_amt"));
					pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("no_of_credit"));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("total_credit_amt"));
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("total_cash_credit"));
					pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("total_cash_debit"));
					//pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString("no_of_cash_txn"));
					pstmtIsertIntoAml.setString(14, String.valueOf(noOfCashTxn));
					pstmtIsertIntoAml.setString(15, requestId);
					pstmtIsertIntoAml.setString(16, batchNo);
					pstmtIsertIntoAml.setString(17, repRefNum);
					pstmtIsertIntoAml.setString(18, "N");
					pstmtIsertIntoAml.setString(19, "N");
					pstmtIsertIntoAml.setString(20, reportType);
					pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString("REASON_FOR_FREEZE"));

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
			pstmtIsertIntoAml.setString(5, "AD");
			
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
			pstmtUpdateIntoAml.setString(5, "AD");
			
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
			insertDataFromWarehouseToAml(reportDataList, requestId, batchNo, fromDate, toDate, reportType);
			updateThreadStatus(requestId, reportType, tid, "Y");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}


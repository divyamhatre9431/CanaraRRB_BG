package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AcctRelnPersnThread implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;

	List<String> reportDataList = null;
	String requestId = "";
	String batchNo = "";
	String reportType = "";
	String fromDate = "";
	String toDate = "";
	String tid = "";
	
	public AcctRelnPersnThread(List<String> reportDataList, String requestId, String batchNo,
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

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_ACCOUNT_PERSON_RELTN(account_number,related_person_type,name_of_noncustomer,unique_ref_no,ind_nid_status,"
					+ "REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			for(String custId : reportDataList)
			{
				String[] custIdArr = custId.split("-");
				String customerId = custIdArr[0].toString();
				String customerAcid = custIdArr[1].toString();
				String repRefNum = custIdArr[2].toString();
				String acctNo = custIdArr[3].toString();
				
				String acctPersonRelationQuery = "select acid account_number,ACCT_POA_AS_REC_TYPE related_person_type,acct_poa_as_name name_of_noncustomer,CUST_ID unique_ref_no,ACCT_POA_AS_SRL_NUM ind_nid_status,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from pnb_ods.cbs_aas where cust_id='"+customerId+"' and acid='"+customerAcid+"' ";

				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctPersonRelationQuery);

				while (rsTxnDataWarehouse.next()) {
					
					commitCounter++;
					
					pstmtIsertIntoAml.setString(1, acctNo);
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("related_person_type"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("name_of_noncustomer"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("unique_ref_no"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("ind_nid_status"));
					pstmtIsertIntoAml.setString(6, requestId);
					pstmtIsertIntoAml.setString(7, batchNo);
					pstmtIsertIntoAml.setString(8, repRefNum);
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("RECORD_STATUS"));
					pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
					pstmtIsertIntoAml.setString(11, reportType);

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
			pstmtIsertIntoAml.setString(5, "APR");
			
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
			pstmtUpdateIntoAml.setString(5, "APR");
			
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

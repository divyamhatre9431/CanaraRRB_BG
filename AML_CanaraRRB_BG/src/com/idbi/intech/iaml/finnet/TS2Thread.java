package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class TS2Thread implements Runnable {

	private static Connection connectionDataWarehouse;
	private static Connection connection;

	List<String> reportDataList = null;
	String requestId = "";
	String batchNo = "";
	String fromDate = "";
	String toDate = "";
	String reportType = "";
	String tid = "";

	public TS2Thread(List<String> reportDataList, String requestId, String batchNo, String fromDate, String toDate,
			String reportType, String tid) {
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
					"insert into REG_TS2(TRAN_CUST_ID,TRANSACTION_DATE,TRANSACTION_TIME,SENDER_NAME,BENEF_NAME,SENDER_MOBILE_NO,"
							+ "BENEF_MOBILE_NO,SENDER_MMID,SENDER_ACCOUNT_NO,BENEF_MMID,BENEF_ACCOUNT_NO,BENEF_IFSC_CODE,TRANSACTION_TYPE,"
							+ "TRANSACTION_AMOUNT,NARRATION,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE,TRANSACTION_ID) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			for (String custId : reportDataList) {
//				if (reportType.equals("STR")) {
//					processStrDetails(custId, requestId, batchNo, reportType);
//				} else {
					String[] txnDataArr = custId.split("~");
					String tranDate = txnDataArr[0].toString();
					String tranRefNo = txnDataArr[1].toString();
					String tranType = txnDataArr[2].toString();
					String repRefNum = txnDataArr[3].toString();
					String tranCustId = txnDataArr[4].toString();

					String acctDtlsQuery = "";

					if (tranType.equals("O")) {
						acctDtlsQuery = "select REMITTORID TRAN_CUST_ID, TXNDATE TRANSACTION_DATE, TXNDATE TRANSACTION_TIME, REMITTORNAME SENDER_NAME, BENENAME BENEF_NAME, REMITTOR_MOBILE SENDER_MOBILE_NO, BENE_MOBILE BENEF_MOBILE_NO, REMITTORMMID SENDER_MMID,REMITTORACCOUNTNUMBER SENDER_ACCOUNT_NO, BENEMMID BENEF_MMID,BENEACCOUNTNUMBER BENEF_ACCOUNT_NO,BENEIFSC BENEF_IFSC_CODE, TRANTYPE TRANSACTION_TYPE, AMOUNT TRANSACTION_AMOUNT, PURPOSE_CODE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS "
								+ "from PNB_ODS.IMPS_TBL_TRANLOG_OUTWARD where txndate between '" + tranDate
								+ " 00:00:00.000' and '" + toDate + " 23:59:59.999' and RRN='" + tranRefNo + "' ";
					} else {
						acctDtlsQuery = "select REMITTORID TRAN_CUST_ID, TXNDATE TRANSACTION_DATE, TXNDATE TRANSACTION_TIME, REMITTORNAME SENDER_NAME, BENENAME BENEF_NAME, REMITTOR_MOBILE SENDER_MOBILE_NO, BENE_MOBILE BENEF_MOBILE_NO, REMITTORMMID SENDER_MMID,REMITTORACCOUNTNUMBER SENDER_ACCOUNT_NO, BENEMMID BENEF_MMID,BENEACCOUNTNUMBER BENEF_ACCOUNT_NO,BENEIFSC BENEF_IFSC_CODE, TRANTYPE TRANSACTION_TYPE, AMOUNT TRANSACTION_AMOUNT, PURPOSE_CODE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS "
								+ "from PNB_ODS.IMPS_TBL_TRANLOG where txndate between '" + tranDate
								+ " 00:00:00.000' and '" + toDate + " 23:59:59.999' and RRN='" + tranRefNo + "' ";
					}

					rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

					while (rsTxnDataWarehouse.next()) {

						commitCounter++;

						// pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("TRAN_CUST_ID"));
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
						pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("TRANSACTION_TYPE"));
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

			//}

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

	/*
	 * public static void processStrDetails(String ticketDetails,String
	 * requestId,String batchNo,String reportType) { Statement stmtAml = null;
	 * //CallableStatement stmt = null; ResultSet rsAml = null;
	 * 
	 * try { String ticketDtlsQuery =
	 * "SELECT to_char(tran_date,'yyyy-MM-dd')||'~'||SUBSTR(tran_particular,10,12)||'~'||decode(CRE_DEB_FLG,'C','I','D','O',CRE_DEB_FLG)||'~'||'IMPS' "
	 * + "from aml_rule_ticket_agg where ticket_id ='"
	 * +ticketDetails+"' and SUBSTR(tran_particular,1,4) = 'IMPS' ";
	 * //System.out.println("ticketDtlsQuery : "+ticketDtlsQuery);
	 * 
	 * stmtAml = connection.createStatement(); rsAml =
	 * stmtAml.executeQuery(ticketDtlsQuery);
	 * 
	 * while(rsAml.next()) { String[] txnDataArr = rsAml.getString(1).split("~");
	 * String tranDate = txnDataArr[0].toString(); String tranRefNo =
	 * txnDataArr[1].toString(); String tranType = txnDataArr[2].toString();
	 * //String tranChannel = txnDataArr[3].toString();
	 * 
	 * writeStrDetails(tranDate,tranType,tranRefNo,requestId,batchNo,reportType); }
	 * 
	 * } catch(Exception e) { e.printStackTrace(); } finally { try {
	 * 
	 * if (connection != null) { connection.close(); connection = null; }
	 * 
	 * if (stmtAml != null) { stmtAml.close(); stmtAml = null; } if (rsAml != null)
	 * { rsAml.close(); rsAml = null; } } catch (SQLException e) {
	 * e.printStackTrace(); } } }
	 * 
	 * 
	 * public static void writeStrDetails(String tranDate,String tranType,String
	 * tranRefNo,String requestId,String batchNo,String reportType) {
	 * 
	 * Statement stFetchTxnWarehouse = null; //CallableStatement stmt = null;
	 * ResultSet rsTxnDataWarehouse = null; PreparedStatement pstmtIsertIntoAml =
	 * null; int commitCounter = 0;
	 * 
	 * try { // Insert script for AML DB pstmtIsertIntoAml =
	 * connection.prepareStatement(
	 * "insert into STR_REG_TS2(TRAN_CUST_ID,TRANSACTION_DATE,TRANSACTION_TIME,SENDER_NAME,BENEF_NAME,SENDER_MOBILE_NO,"
	 * +
	 * "BENEF_MOBILE_NO,SENDER_MMID,SENDER_ACCOUNT_NO,BENEF_MMID,BENEF_ACCOUNT_NO,BENEF_IFSC_CODE,TRANSACTION_TYPE,"
	 * +
	 * "TRANSACTION_AMOUNT,NARRATION,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
	 * + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	 * 
	 * String acctDtlsQuery = "";
	 * 
	 * if(tranType.equals("O")) { acctDtlsQuery =
	 * "select REMITTORID TRAN_CUST_ID, TXNDATE TRANSACTION_DATE, TXNDATE TRANSACTION_TIME, REMITTORNAME SENDER_NAME, BENENAME BENEF_NAME, REMITTOR_MOBILE SENDER_MOBILE_NO, BENE_MOBILE BENEF_MOBILE_NO, REMITTORMMID SENDER_MMID,REMITTORACCOUNTNUMBER SENDER_ACCOUNT_NO, BENEMMID BENEF_MMID,BENEACCOUNTNUMBER BENEF_ACCOUNT_NO,BENEIFSC BENEF_IFSC_CODE, TRANTYPE TRANSACTION_TYPE, AMOUNT TRANSACTION_AMOUNT, PURPOSE_CODE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS "
	 * + "from PNB_ODS.IMPS_TBL_TRANLOG_OUTWARD where txndate between '"
	 * +tranDate+" 00:00:00.000' and '"+tranDate+" 23:59:59.999' and RRN='"
	 * +tranRefNo+"' "; } else { acctDtlsQuery =
	 * "select REMITTORID TRAN_CUST_ID, TXNDATE TRANSACTION_DATE, TXNDATE TRANSACTION_TIME, REMITTORNAME SENDER_NAME, BENENAME BENEF_NAME, REMITTOR_MOBILE SENDER_MOBILE_NO, BENE_MOBILE BENEF_MOBILE_NO, REMITTORMMID SENDER_MMID,REMITTORACCOUNTNUMBER SENDER_ACCOUNT_NO, BENEMMID BENEF_MMID,BENEACCOUNTNUMBER BENEF_ACCOUNT_NO,BENEIFSC BENEF_IFSC_CODE, TRANTYPE TRANSACTION_TYPE, AMOUNT TRANSACTION_AMOUNT, PURPOSE_CODE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS "
	 * + "from PNB_ODS.IMPS_TBL_TRANLOG where txndate between '"
	 * +tranDate+" 00:00:00.000' and '"+tranDate+" 23:59:59.999' and RRN='"
	 * +tranRefNo+"' "; }
	 * 
	 * stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
	 * rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);
	 * 
	 * while (rsTxnDataWarehouse.next()) {
	 * 
	 * commitCounter++;
	 * 
	 * pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("TRAN_CUST_ID"));
	 * pstmtIsertIntoAml.setString(2,
	 * rsTxnDataWarehouse.getString("TRANSACTION_DATE"));
	 * pstmtIsertIntoAml.setString(3,
	 * rsTxnDataWarehouse.getString("TRANSACTION_TIME"));
	 * pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("SENDER_NAME"));
	 * pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("BENEF_NAME"));
	 * pstmtIsertIntoAml.setString(6,
	 * rsTxnDataWarehouse.getString("SENDER_MOBILE_NO"));
	 * pstmtIsertIntoAml.setString(7,
	 * rsTxnDataWarehouse.getString("BENEF_MOBILE_NO"));
	 * pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("SENDER_MMID"));
	 * pstmtIsertIntoAml.setString(9,
	 * rsTxnDataWarehouse.getString("SENDER_ACCOUNT_NO"));
	 * pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("BENEF_MMID"));
	 * pstmtIsertIntoAml.setString(11,
	 * rsTxnDataWarehouse.getString("BENEF_ACCOUNT_NO"));
	 * pstmtIsertIntoAml.setString(12,
	 * rsTxnDataWarehouse.getString("BENEF_IFSC_CODE"));
	 * pstmtIsertIntoAml.setString(13,
	 * rsTxnDataWarehouse.getString("TRANSACTION_TYPE"));
	 * pstmtIsertIntoAml.setString(14,
	 * rsTxnDataWarehouse.getString("TRANSACTION_AMOUNT"));
	 * pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString("NARRATION"));
	 * pstmtIsertIntoAml.setString(16, requestId); pstmtIsertIntoAml.setString(17,
	 * batchNo); pstmtIsertIntoAml.setString(18, String.valueOf(commitCounter));
	 * pstmtIsertIntoAml.setString(19,
	 * rsTxnDataWarehouse.getString("RECORD_STATUS"));
	 * pstmtIsertIntoAml.setString(20,
	 * rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
	 * pstmtIsertIntoAml.setString(21, reportType);
	 * 
	 * pstmtIsertIntoAml.execute();
	 * 
	 * System.out.println(commitCounter); if (commitCounter == 500) {
	 * connection.commit(); commitCounter = 0; } } } catch(Exception e) {
	 * e.printStackTrace(); } finally { try {
	 * 
	 * if (connection != null) { connection.close(); connection = null; }
	 * 
	 * if (rsTxnDataWarehouse != null) { rsTxnDataWarehouse.close();
	 * rsTxnDataWarehouse = null; } if (pstmtIsertIntoAml != null) {
	 * pstmtIsertIntoAml.close(); pstmtIsertIntoAml = null; } if
	 * (stFetchTxnWarehouse != null) { stFetchTxnWarehouse.close();
	 * stFetchTxnWarehouse = null; }
	 * 
	 * if (connectionDataWarehouse != null) { connectionDataWarehouse.close();
	 * connectionDataWarehouse = null; }
	 * 
	 * } catch (SQLException e) { e.printStackTrace(); } } }
	 */

	public static void addthread(String requestId, String reportType, String tid, String status) throws SQLException {
		PreparedStatement pstmtIsertIntoAml = null;

		try {
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_REQUEST_THREADS(REQUEST_ID,REG_REPORT_TYPE,TID,STATUS,FILE_TYPE) "
							+ "values (?,?,?,?,?)");
			pstmtIsertIntoAml.setString(1, requestId);
			pstmtIsertIntoAml.setString(2, reportType);
			pstmtIsertIntoAml.setString(3, tid);
			pstmtIsertIntoAml.setString(4, status);
			pstmtIsertIntoAml.setString(5, "TS2");

			pstmtIsertIntoAml.execute();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmtIsertIntoAml != null) {
				pstmtIsertIntoAml.close();
				pstmtIsertIntoAml = null;
			}
		}

	}

	public static void updateThreadStatus(String requestId, String reportType, String tid, String status)
			throws SQLException {
		PreparedStatement pstmtUpdateIntoAml = null;

		try {
			pstmtUpdateIntoAml = connection.prepareStatement(
					"update REG_REQUEST_THREADS set STATUS=? where REQUEST_ID=? and REG_REPORT_TYPE=? and TID=? and FILE_TYPE=? ");
			pstmtUpdateIntoAml.setString(1, status);
			pstmtUpdateIntoAml.setString(2, requestId);
			pstmtUpdateIntoAml.setString(3, reportType);
			pstmtUpdateIntoAml.setString(4, tid);
			pstmtUpdateIntoAml.setString(5, "TS2");

			pstmtUpdateIntoAml.execute();
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmtUpdateIntoAml != null) {
				pstmtUpdateIntoAml.close();
				pstmtUpdateIntoAml = null;
			}
		}
	}

	@Override
	public void run() {

		try {
			makeConnection();
			makeConnectionToDataWarehouse();

			addthread(requestId, reportType, tid, "P");
			insertDataFromWarehouseToAml(reportDataList, requestId, batchNo, fromDate, toDate, reportType);
			updateThreadStatus(requestId, reportType, tid, "Y");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

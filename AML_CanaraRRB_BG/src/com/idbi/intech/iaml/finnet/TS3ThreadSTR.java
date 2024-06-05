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

import org.apache.commons.lang3.StringUtils;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class TS3ThreadSTR implements Runnable {

	private static Connection connectionDataWarehouse;
	private static Connection connection;
	private final static String patternfordb2 = "yyyy-MM-dd";

	String strNo = "";

	public TS3ThreadSTR(String strNo) {
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
			sql = "select to_char(tran_date,'yyyy-mm-dd')||'~'||SUBSTR(tran_particular,5,12)||'~'||decode(CRE_DEB_FLG,'C','CREDIT','D','DEBIT',CRE_DEB_FLG)||'~'||REPORT_REF_NUM||'~'||STR_NO||'~'||BATCH_NUM||'~'||REPORT_TYPE||'~'||TRAN_CUST_ID from aml_transaction_master_f where TRAN_CHANNEL = 'UPI' and SUBSTR(tran_particular,1,4)='UPI/' and str_no = '"
					+ strNo + "' " + "union all "
					+ "select to_char(tran_date,'yyyy-mm-dd')||'~'||SUBSTR(tran_particular,3,12)||'~'||decode(CRE_DEB_FLG,'C','CREDIT','D','DEBIT',CRE_DEB_FLG)||'~'||REPORT_REF_NUM||'~'||STR_NO||'~'||BATCH_NUM||'~'||REPORT_TYPE||'~'||TRAN_CUST_ID from aml_transaction_master_f where TRAN_CHANNEL = 'UPI' and SUBSTR(tran_particular,1,4)='U/' and str_no = '"
					+ strNo + "' ";

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
		// CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into STR_REG_TS3(TRANSACTION_DATE,TRANSACTION_TIME,SENDER_NAME,BENEF_NAME,SENDER_VPA,"
							+ "SENDER_ACCOUNT_NO,SENDER_IFSC_CODE,SENDER_MOBILE_NO,BENEF_VPA,BENEF_ACCOUNT_NO,BENEF_IFSC_CODE,BENEF_MOBILE_NO,"
							+ "TRANSACTION_TYPE,TRANSACTION_AMOUNT,NARRATION,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE,"
							+ "TRANSACTION_ID,TRAN_CUST_ID) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			for (String custId : reportDataList) {

				String[] txnDataArr = custId.split("~");
				String tranDate = txnDataArr[0].toString();
				String tranRefNo = txnDataArr[1].toString();
				String tranType = txnDataArr[2].toString();
				String repRefNum = txnDataArr[3].toString();
				String requestId = txnDataArr[4].toString();
				String batchNo = txnDataArr[5].toString();
				String reportType = txnDataArr[6].toString();
				String tranCustId = txnDataArr[7].toString();

				/*
				 * String acctDtlsQuery =
				 * "select TRNDATE TRANSACTION_DATE,TRNDATE TRANSACTION_TIME, PRFNAME SENDER_NAME, PYFNAME BENEF_NAME,PRFVADDR SENDER_VPA, FROMACCNO SENDER_ACCOUNT_NO, PRIFSCCODE SENDER_IFSC_CODE, PRDMOBILE SENDER_MOBILE_NO, PYFVADDR BENEF_VPA, TOACCNO BENEF_ACCOUNT_NO,PYIFSCCODE BENEF_IFSC_CODE, PYDMOBILE BENEF_MOBILE_NO, TXNTYPE TRANSACTION_TYPE, TXNAMOUNT TRANSACTION_AMOUNT, TXNNOTE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from PNB_ODS.UPINEW_VTSTSETTLEREQ "
				 * + "where TRNDATE between '" + tranDate + " 00:00:00.000' and '" + tranDate +
				 * " 23:59:59.999' and CUSTREFNO='" + tranRefNo + "' and TXNTYPE='" + tranType +
				 * "' ";
				 */

				String acctDtlsQuery = "select TRNDATE TRANSACTION_DATE,TRNDATE TRANSACTION_TIME, PRFNAME SENDER_NAME, PYFNAME BENEF_NAME,PRFVADDR SENDER_VPA, FROMACCNO SENDER_ACCOUNT_NO, PRIFSCCODE SENDER_IFSC_CODE, PRDMOBILE SENDER_MOBILE_NO, PYFVADDR BENEF_VPA, TOACCNO BENEF_ACCOUNT_NO,PYIFSCCODE BENEF_IFSC_CODE, PYDMOBILE BENEF_MOBILE_NO, TXNTYPE TRANSACTION_TYPE, TXNAMOUNT TRANSACTION_AMOUNT, TXNNOTE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from PNB_ODS.UPINEW_VTSTSETTLEREQ_24072023  "
						+ "where TRNDATE between '" + tranDate + " 00:00:00.000' and '" + tranDate
						+ " 23:59:59.999' and CUSTREFNO='" + tranRefNo + "' and TXNTYPE='" + tranType + "' ";
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

				while (rsTxnDataWarehouse.next()) {

					commitCounter++;

					pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("TRANSACTION_DATE"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("TRANSACTION_TIME"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("SENDER_NAME"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("BENEF_NAME"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("SENDER_VPA"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("SENDER_ACCOUNT_NO"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("SENDER_IFSC_CODE"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("SENDER_MOBILE_NO"));
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("BENEF_VPA"));
					pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("BENEF_ACCOUNT_NO"));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("BENEF_IFSC_CODE"));
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("BENEF_MOBILE_NO"));
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
					pstmtIsertIntoAml.setString(23, tranCustId);

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

	public static String checkTS3Status(String strNo) {

		Statement stmtCheck = null;
		ResultSet rs = null;
		int cnt = 0;
		String flg = "N";

		try {
			stmtCheck = connection.createStatement();

			rs = stmtCheck.executeQuery(
					"select count(distinct(status)) from STR_REQUEST_TS3_THREADS where  request_id='" + strNo + "'");
			while (rs.next()) {
				cnt = rs.getInt(1);
			}
			rs.close();

			if (cnt == 1) {
				rs = stmtCheck.executeQuery(
						"select distinct(status) from STR_REQUEST_TS3_THREADS where request_id='" + strNo + "'");
				while (rs.next()) {
					flg = rs.getString(1);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	public static void addthread(String requestId, String reportType, String tid, String status) throws SQLException {
		PreparedStatement pstmtIsertIntoAml = null;

		try {
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into STR_REQUEST_TS3_THREADS(REQUEST_ID,REG_REPORT_TYPE,TID,STATUS,FILE_TYPE) "
							+ "values (?,?,?,?,?)");
			pstmtIsertIntoAml.setString(1, requestId);
			pstmtIsertIntoAml.setString(2, reportType);
			pstmtIsertIntoAml.setString(3, tid);
			pstmtIsertIntoAml.setString(4, status);
			pstmtIsertIntoAml.setString(5, "TS3");

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

	public static int checkTotalSize(List<String> reportList, int size) {
		int i = 0;

		for (int start = 0; start < reportList.size(); start += size) {

			i++;
			int end = Math.min(start + size, reportList.size());
			List<String> sublist = reportList.subList(start, end);

		}

		return i;

	}

	@Override
	public void run() {

		try {
			makeConnection();
			makeConnectionToDataWarehouse();

			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			// System.out.println("Current Directory::"+dir);
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			String threadSize = amlProp.getProperty("TS3_STR_THREAD_SIZE");

			String status = ConstantFunctions.getThreadStatus("TS3");
			String reportType = "STR";
			String flg = "";

			if (status.equals("N")) {
				ConstantFunctions.updateThreadStatus("TS3", "P");
				List<String> reportList = getStrTransactionData(strNo);

				if (reportList.size() > 0) {
					int i = 0;
					int j = 0;
					int size = Integer.valueOf(threadSize);
					int count = checkTotalSize(reportList, size);
					Thread t[] = new Thread[count];

					for (int start = 0; start < reportList.size(); start += size) {

						i++;
						int end = Math.min(start + size, reportList.size());
						List<String> sublist = reportList.subList(start, end);
						String tid = "T" + String.valueOf(i);
						addthread(strNo, reportType, tid, "P");
						// new Thread(new GenerateTS3STR(sublist, strNo, reportType, tid)).start();
						t[j] = new Thread(new GenerateTS3STR(sublist, strNo, reportType, tid));
						t[j].start();
						j++;
					}

					for (int k = 0; k < count; k++) {
						try {
							t[k].join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}

					boolean isRunning = true;
					while (isRunning) {
						flg = checkTS3Status(strNo);

						if (flg.equals("Y")) {
							isRunning = false;
						}
					}
				}

				ConstantFunctions.updateThreadStatus("TS3", "Y");
				String threadStatus = ConstantFunctions.checkStatus("TS3");

				if (threadStatus.equals("Y")) {
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}

			}
		} catch (Exception e) {
			try {
				ConstantFunctions.updateThreadStatus("TS3", "X");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

}

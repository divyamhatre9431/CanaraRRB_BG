package com.idbi.intech.iaml.finnet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GenerateStrAccountDtls implements Runnable {
	private static Connection connectionDataWarehouse;
	private static Connection connection;
	static final String FILETYPE = "AD";
	String strNo =  "";

	public GenerateStrAccountDtls(String strNo) {
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

	public int checkKc1Strcount(String requestId) {
		int cnt = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(1) from Str_REG_ACCOUNT_DTLS a where request_id= ?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, requestId);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				cnt = rs.getInt(1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		// System.out.println("cnt 11 : "+cnt);
		return cnt;
	}

	public static List<String> getStrAccountData(String Str_No) {
		List<String> reportList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {

			
				sql = "select  distinct tran_cust_id||'-'||REPORT_REF_NUM||'-'||ACCOUNT_NO||'-'||str_no||'-'||batch_num from aml_transaction_master_f where str_no=? ";
			

			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, Str_No);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				System.out.println("Extracted Customer Details : " + rs.getString(1));
				reportList.add(rs.getString(1));
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

		return reportList;
	}

	public void genStrAcctData(List<String> StrReportDataList) {
		CallableStatement stmt = null;
		ResultSet rsFetchCust = null;
		ResultSet rsTxnDataWarehouse = null;
		int cnt = 0;

		int commitCounter = 0;

		String vRequestId = null;
		String vCustId = null;
		String vReportRefNum = null;
		String vAcctNo = null;
		String reportType = "STR";

		SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat db2Fmt = new SimpleDateFormat("yyyyMMdd");

		Date dateAfter = new Date();
		Date dateBefore = DateUtils.addDays(dateAfter, -365);

		String dateBeforeStr = db2Fmt.format(dateBefore);
		String dateAfterStr = db2Fmt.format(dateAfter);

		String InsertAcct = "insert into Str_REG_ACCOUNT_DTLS(cust_id,branch_code,account_number,account_type,date_of_ac_opn,account_status,"
				+ "date_of_ac_cls,no_of_debit,total_debit_amt,no_of_credit,total_credit_amt,total_cash_credit,total_cash_debit,no_of_cash_txn,"
				+ "REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmtIsertIntoAml = connection.prepareStatement(InsertAcct);) {
			for (String custId : StrReportDataList) {
				String[] custIdArr = custId.split("-");
				vRequestId = custIdArr[3].toString();
				vAcctNo = custIdArr[2].toString();
				vCustId = custIdArr[0].toString();
				vReportRefNum = custIdArr[1].toString();
				String batchNo = custIdArr[4].toString();

				String acctDtlsQuery = "select a.IP_CODE cust_id,a.branch_code,a.ACCOUNT_NBR account_number,a.PD_STC_TYPE account_type, "
						+ "a.ACCT_OPN_DT date_of_ac_opn,a.ACCT_STATUS account_status,a.ACCT_CLS_DT date_of_ac_cls,"
						+ "sum(b.DEBIT_NBR) no_of_debit,sum(b.DEBIT_AMT) total_debit_amt,sum(b.CREDIT_NBR) no_of_credit,"
						+ "sum(b.CREDIT_AMT) total_credit_amt,sum(b.CASH_DEBIT_NBR) no_of_cash_debit,sum(b.CASH_DEBIT_AMT) total_cash_debit,"
						+ "sum(b.CASH_CREDIT_NBR) no_of_cash_credit,sum(b.CASH_CREDIT_AMT) total_cash_credit "
						+ "from mis.dim_arngmnt a,mis.fact_arngmnt_txn_m_summ b where a.ARNGMNT_ID=b.ARNGMNT_ID "
						+ "and cal_id  between  ? and ? and ACCOUNT_NBR=? and IP_CODE=? "
						+ "group by a.IP_CODE,a.branch_code,a.ACCOUNT_NBR,a.PD_STC_TYPE,a.ACCT_OPN_DT,a.ACCT_STATUS,a.ACCT_CLS_DT";
				PreparedStatement stmtFetchCust = connectionDataWarehouse.prepareStatement(acctDtlsQuery);
				stmtFetchCust.setString(1, dateBeforeStr);
				stmtFetchCust.setString(2, dateAfterStr);
				stmtFetchCust.setString(3, vAcctNo);
				stmtFetchCust.setString(4, vCustId);
				rsTxnDataWarehouse = stmtFetchCust.executeQuery();
				while (rsTxnDataWarehouse.next()) {

					long noOfCashTxn = Long.valueOf(rsTxnDataWarehouse.getString("no_of_cash_debit"))
							+ Long.valueOf(rsTxnDataWarehouse.getString("no_of_cash_credit"));

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
					// pstmtIsertIntoAml.setString(14,
					// rsTxnDataWarehouse.getString("no_of_cash_txn"));
					pstmtIsertIntoAml.setString(14, String.valueOf(noOfCashTxn));
					pstmtIsertIntoAml.setString(15, vRequestId);
					pstmtIsertIntoAml.setString(16, batchNo);
					pstmtIsertIntoAml.setString(17, vReportRefNum);
					pstmtIsertIntoAml.setString(18, "N");
					pstmtIsertIntoAml.setString(19, "N");
					pstmtIsertIntoAml.setString(20, reportType);

					System.out.println(commitCounter);
					if (commitCounter == 500) {
						connection.commit();
						commitCounter = 0;
					}
				}
			}
			connection.commit();
			System.out.println(" :::::::::::::::Process Completed Successfully::::::::::::::::");
			cnt = checkKc1Strcount(vRequestId);
			if (cnt > 0) {
				long startTime = System.currentTimeMillis();
				String procedure = "update_reg_ad_str_data";
				System.out.println(
						"Started execution for Cleaning StrAd: " + procedure + "For the Request id" + vRequestId);
				stmt = connection.prepareCall("call update_reg_ad_str_data('" + vRequestId + "')");
				stmt.execute();
				long stopTime = System.currentTimeMillis();

				long elapsedTime = stopTime - startTime;

				System.out.println("Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000)
						+ " Seconds");
				System.out.flush();
			} else {

			}

		}

		catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rsFetchCust != null) {
					rsFetchCust.close();
					rsFetchCust = null;
				}
				if (rsTxnDataWarehouse != null) {
					rsTxnDataWarehouse.close();
					rsTxnDataWarehouse = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

	}

	public void run() {
		try {
			makeConnection();
			makeConnectionToDataWarehouse();

			String status = ConstantFunctions.getThreadStatus(FILETYPE);

			if (status.equals("N")) {
				ConstantFunctions.updateThreadStatus(FILETYPE, "P");
				List<String> strTxnList = getStrAccountData(strNo);

				if (strTxnList.size() > 0) {
					genStrAcctData(strTxnList);
				} else {

				}

				String threadStatus = ConstantFunctions.checkStatus(FILETYPE);

				if (threadStatus.equals("Y")) {
					ConstantFunctions.updateThreadStatus(FILETYPE, "Y");
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

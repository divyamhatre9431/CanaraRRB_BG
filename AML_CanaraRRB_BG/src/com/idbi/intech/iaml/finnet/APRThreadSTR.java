package com.idbi.intech.iaml.finnet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class APRThreadSTR implements Runnable{
	
	

	private static Connection connectionDataWarehouse;
	private static Connection connection;
	static final String FILETYPE = "APR";
	String strNo = "";

	public APRThreadSTR(String strNo) {
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
			String sql = "select count(1) from REG_STR_ACCOUNT_PERSON_RELTN a where request_id= ?";
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

			
				sql = "select  distinct tran_cust_id||'-'||REPORT_REF_NUM||'-'||tran_acid||'-'||ACCOUNT_NO||'-'||str_no||'-'||batch_num from aml_transaction_master_f where str_no=? ";
			

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

	public void genStrAprData(List<String> StrReportDataList) {
		CallableStatement stmt = null;
		ResultSet rsFetchCust = null;
		ResultSet rsTxnDataWarehouse = null;
		int cnt = 0;

		int commitCounter = 0;

		String vRequestId = null;
		String vCustId = null;
		String vReportRefNum = null;
		String vAcctNo = null;
		String vAcid = null;
		String reportType = "STR";

		



	String InsertApr=	"insert into STR_REG_ACCOUNT_PERSON_RELTN(account_number,related_person_type,name_of_noncustomer,unique_ref_no,ind_nid_status,"
				+ "REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmtIsertIntoAml = connection.prepareStatement(InsertApr);) {
			for (String custId : StrReportDataList) {
				String[] custIdArr = custId.split("-");
				vRequestId = custIdArr[4].toString();
				vAcctNo = custIdArr[3].toString();
				vAcid = custIdArr[2].toString();
				vCustId = custIdArr[0].toString();
				vReportRefNum = custIdArr[1].toString();
				String batchNo = custIdArr[5].toString();

				String acctPersonRelationQuery = "select acid account_number,ACCT_POA_AS_REC_TYPE related_person_type,acct_poa_as_name name_of_noncustomer,CUST_ID unique_ref_no,ACCT_POA_AS_SRL_NUM ind_nid_status,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from pnb_ods.cbs_aas where cust_id=? and acid=? ";
				PreparedStatement stmtFetchCust = connectionDataWarehouse.prepareStatement(acctPersonRelationQuery);
				stmtFetchCust.setString(1, vCustId);
				stmtFetchCust.setString(2, vAcid);
				
				rsTxnDataWarehouse = stmtFetchCust.executeQuery();
				while (rsTxnDataWarehouse.next()) {


					commitCounter++;

					pstmtIsertIntoAml.setString(1, vAcctNo);
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("related_person_type"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("name_of_noncustomer"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("unique_ref_no"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("ind_nid_status"));
					pstmtIsertIntoAml.setString(6, vRequestId);
					pstmtIsertIntoAml.setString(7, batchNo);
					pstmtIsertIntoAml.setString(8, vReportRefNum);
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
			/*
			 * cnt = checkKc1Strcount(vRequestId); if (cnt > 0) { long startTime =
			 * System.currentTimeMillis(); String procedure = "update_reg_apr_str_data";
			 * System.out.println( "Started execution for Cleaning StrApr: " + procedure +
			 * "For the Request id" + vRequestId); stmt =
			 * connection.prepareCall("call update_reg_apr_str_data('" + vRequestId + "')");
			 * stmt.execute(); long stopTime = System.currentTimeMillis();
			 * 
			 * long elapsedTime = stopTime - startTime;
			 * 
			 * System.out.println("Done execution for : " + procedure +
			 * " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			 * System.out.flush(); } else {
			 * 
			 * }
			 */

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
					genStrAprData(strTxnList);
				} else {

				}

				ConstantFunctions.updateThreadStatus(FILETYPE, "Y");
				String threadStatus = ConstantFunctions.checkStatus(FILETYPE);

				if (threadStatus.equals("Y")) {
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}
			}
		} catch (Exception e) {
			try {
				ConstantFunctions.updateThreadStatus(FILETYPE, "X");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}


}

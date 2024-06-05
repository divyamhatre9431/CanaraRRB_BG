package com.idbi.intech.iaml.finnet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GenerateStrGs1 implements Runnable {

	private static Connection connectionDataWarehouse;
	private static Connection connection;
	static final String FILETYPE = "GS1";
	String strNo =  "";

	public GenerateStrGs1(String strNo) {
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

	public static List<String> getStrGS1Data(String Str_No) {
		List<String> reportList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {

			sql = "select  KYC_SOURCE_OF_FUND||'-'||KYC_DEST_OF_FUND||'-'||SOURCE_OF_ALERT||'-'||TYPE_OF_SUSP||'-'||SUSP_DUE_TO||'-'||RED_FLG_INDICATOR||'-'||a.str_no||'-'||(select distinct REPORT_REF_NUM from aml_transaction_master_f where str_no=a.str_no )REPORT_REF_NUM||'~'||GOS from str_details_finnet a where a.str_no=? ";

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

	public void genStrGs1Data(List<String> StrReportDataList) {
		CallableStatement stmt = null;
		ResultSet rsFetchCust = null;
		ResultSet rsTxnDataWarehouse = null;
		int cnt = 0;

		int commitCounter = 0;

		String vRequestId = null;
		String vCustId = null;
		String vReportRefNum = null;
		String vKycSourcefund = null;
		String vKycDestfund = null;
		String vSourceOfAlert = null;
		String vTypeOfSusp = null;
		String vSuspDueTo = null;
		String vRedFlgIindicator = null;
		String vGOS = null;
		String reportType = "STR";

		String InsertGs1 = "INSERT INTO REG_Str_GS1 (REQUEST_ID,REPORT_REF_NUM,KYC_SOURCE_OF_FUND,KYC_DEST_OF_FUND,SOURCE_OF_ALERT,TYPE_OF_SUSP,SUSP_DUE_TO,RED_FLG_INDICATOR,NARRATION,REPORT_TYPE,RECORD_MOD_BY,RECORD_MOD_DT) VALUES (?,?,?,?,?,?,?,?,?,?,?,sysdate)";

		try (PreparedStatement pstmtInsertion = connection.prepareStatement(InsertGs1);) {
			for (String custId : StrReportDataList) {
				String[] GosId = custId.split("~");
				String[] Gs1IdArr = GosId[0].split("-");
			
				vKycSourcefund = Gs1IdArr[0].toString();
				vKycDestfund = Gs1IdArr[1].toString();
				vSourceOfAlert = Gs1IdArr[2].toString();
				vTypeOfSusp = Gs1IdArr[3].toString();
				vSuspDueTo = Gs1IdArr[4].toString();
				vRedFlgIindicator = Gs1IdArr[5].toString();
				vRequestId = Gs1IdArr[6].toString();
				vGOS = GosId[1].toString();
				vReportRefNum = Gs1IdArr[7].toString();
				String batchNo = "042023";

				pstmtInsertion.setString(1, vRequestId); 
				pstmtInsertion.setString(2, vReportRefNum);
				pstmtInsertion.setString(3, vKycSourcefund); 
				pstmtInsertion.setString(4, vKycDestfund); 
				pstmtInsertion.setString(5, vSourceOfAlert);
				pstmtInsertion.setString(6, vTypeOfSusp); 
				pstmtInsertion.setString(7, vSuspDueTo); 
				pstmtInsertion.setString(8, vRedFlgIindicator); 
				pstmtInsertion.setString(9, vGOS);
				pstmtInsertion.setString(10, reportType); 
				pstmtInsertion.setString(11, "SYSTEM");

					
					System.out.println(commitCounter);
					if (commitCounter == 500) {
						connection.commit();
						commitCounter = 0;
					}
				
			}
			connection.commit();
		

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
				List<String> strTxnList = getStrGS1Data(strNo);

				if (strTxnList.size() > 0) {
					genStrGs1Data(strTxnList);
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

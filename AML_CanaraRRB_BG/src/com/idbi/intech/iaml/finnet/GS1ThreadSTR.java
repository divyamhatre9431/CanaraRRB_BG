package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GS1ThreadSTR implements Runnable {

	private static Connection connection;
	static final String FILETYPE = "GS1";
	String strNo = "";

	public GS1ThreadSTR(String strNo) {
		this.strNo = strNo;
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


	public static List<String> getStrGS1Data(String Str_No) {
		List<String> reportList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {

			sql = "select  KYC_SOURCE_OF_FUND||'~'||KYC_DEST_OF_FUND||'~'||SOURCE_OF_ALERT||'~'||TYPE_OF_SUSP||'~'||SUSP_DUE_TO||'~'||RED_FLG_INDICATOR||'~'||a.str_no||'~'||(select distinct REPORT_REF_NUM||'~'||Batch_NUM from aml_transaction_master_f where str_no=a.str_no)||'~'||GOS from str_details_finnet a where a.str_no=? ";

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
		ResultSet rsFetchCust = null;
		ResultSet rsTxnDataWarehouse = null;

		int commitCounter = 0;

		String vRequestId = null;
		String vReportRefNum = null;
		String vKycSourcefund = null;
		String vKycDestfund = null;
		String vSourceOfAlert = null;
		String vTypeOfSusp = null;
		String vSuspDueTo = null;
		String vRedFlgIindicator = null;
		String vGOS = null;
		String reportType = "STR";
		String batchNo = "";

		String InsertGs1 = "INSERT INTO STR_REG_GS1 (REQUEST_ID,REPORT_REF_NUM,KYC_SOURCE_OF_FUND,KYC_DEST_OF_FUND,SOURCE_OF_ALERT,TYPE_OF_SUSP,SUSP_DUE_TO,RED_FLG_INDICATOR,NARRATION,REPORT_TYPE,RECORD_STATUS,VALIDATION_STATUS,BATCH_NUM) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmtInsertion = connection.prepareStatement(InsertGs1);) {
			for (String custId : StrReportDataList) {
				
				commitCounter++;
				
				String[] custIdArr = custId.split("~");
				
				vKycSourcefund = custIdArr[0].toString();
				vKycDestfund = custIdArr[1].toString();
				vSourceOfAlert = custIdArr[2].toString();
				vTypeOfSusp = custIdArr[3].toString();
				vSuspDueTo = custIdArr[4].toString();
				vRedFlgIindicator = custIdArr[5].toString();
				vRequestId = custIdArr[6].toString();
				vReportRefNum = custIdArr[7].toString();
				batchNo = custIdArr[8].toString();
				vGOS = custIdArr[9].toString();
				
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
				pstmtInsertion.setString(11, "N");
				pstmtInsertion.setString(12, "N");
				pstmtInsertion.setString(13, batchNo);
				
				pstmtInsertion.execute();

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
			
			String status = ConstantFunctions.getThreadStatus(FILETYPE);

			if (status.equals("N")) {
				ConstantFunctions.updateThreadStatus(FILETYPE, "P");
				List<String> strTxnList = getStrGS1Data(strNo);

				if (strTxnList.size() > 0) {
					genStrGs1Data(strTxnList);
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

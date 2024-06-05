package com.idbi.intech.iaml.finnet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class KC2ThreadSTR implements Runnable {

	private static Connection connectionDataWarehouse;
	private static Connection connection;

	String strNo = "";

	public KC2ThreadSTR(String strNo) {
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
			String sql = "select count(1) from str_reg_kc2 a where request_id= ?";
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

	public static List<String> getStrKc2Data(String Str_No, String reportType) {
		List<String> reportList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {

			if (reportType.equals("STR")) {
				sql = "select  distinct tran_cust_id||'-'||REPORT_REF_NUM||'-'||str_no||'-'||batch_num from aml_transaction_master_f where str_no=? and cust_type='N'";
			}

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

	public void genStrKc2(List<String> StrReportDataList) {
		CallableStatement stmt = null;
		ResultSet rsFetchCust = null;
		ResultSet rsTxnDataWarehouse = null;
		int cnt = 0;

		int commitCounter = 0;

		String vRequestId = null;
		String vCustId = null;
		String vReportRefNum = null;
		String reportType = "STR";

		// reportCustList = getCustReportList(reportType, StrReportDataList);

		// Reg_Kc1_insert

		String InsertKc2 = "insert into str_reg_kc2(UCIC_ID,CUST_ID,ENTITY_NAME,MOBILE_NO,TELEPHONE_NO,EMAIL_ID,ADDRESS_LINE,"
				+ "ADDR_CITY,ADDR_STATE,COUNTRY,ADDR_PIN_CODE,ADDR_LOCALITY,ADDR_DISTRICT,REG_ADDR,"
				+ "REG_CITY,REG_STATE,REG_COUNTRY,REG_PIN_CODE,REG_LOCALITY,REG_DISTRICT,CUST_TYPE,"
				+ "UBO,UBO_DECLARATION,PEKRN,DIRECTOR_NAME,OTHER_CUST_TYPE,COMPANY_ID_TYPE,"
				+ "NO_IDENTIFIER_AVAILABLE,CUST_RISK_LEVEL,ONBOARDING_DT,DATE_OF_INCORP,LINE_OF_BUSSINESS,UNIQUE_COMPANY_ID,"
				+ "GSTIN_NO,PAN_NO,PAN_DECLARATION,IEC_CODE,IEC_DECLARATION,LAST_KYC_DT,FCRA_STATUS,FCRA_REG_NO,FCRA_REG_STATE,"
				+ "RECORD_STATUS,VALIDATION_STATUS,TAN_NO,COMPANY_WEBSITE,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,REPORT_TYPE) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmtIsertIntoAml = connection.prepareStatement(InsertKc2);) {
			for (String custId : StrReportDataList) {
				String[] custIdArr = custId.split("-");
				vRequestId = custIdArr[2].toString();
				vCustId = custIdArr[0].toString();
				vReportRefNum = custIdArr[1].toString();
				String batchNo = custIdArr[3].toString();

				String kc2Query = "select a.CUST_ID UCIC_ID,a.CUST_ID, a.CUST_NAME ENTITY_NAME, a.ALERT_MOB MOBILE_NO, a.MOBILE TELEPHONE_NO, decode (a.ALERT_MAIL_ID, null,a.EMAIL) EMAIL_ID ,a.ADDRESS_LINE1||','||a.ADDRESS_LINE2 ADDRESS_LINE,a.CITY_CODE ADDR_CITY,a.STATE_CODE ADDR_STATE,a.COUNTRY_CODE COUNTRY,a.ZIP ADDR_PIN_CODE,'NA' ADDR_LOCALITY,'NA' ADDR_DISTRICT, c.REGTRD_ADDRESS REG_ADDR,c.REGTRD_CITY_CODE REG_CITY,c.REGTRD_STATE_CODE REG_STATE,c.REGTRD_COUNTRY_CODE REG_COUNTRY,c.REGTRD_ZIP REG_PIN_CODE,'NA' REG_LOCALITY,'NA' REG_DISTRICT, decode (a.LEGALENTITY_TYPE , null,a.CUST_TYPE_CODE) as CUST_TYPE,null UBO,null UBO_DECLARATION,null PEKRN, null DIRECTOR_NAME,null OTHER_CUST_TYPE,null COMPANY_ID_TYPE,null NO_IDENTIFIER_AVAILABLE, a.RISK CUST_RISK_LEVEL, a.CUST_OPN_DT ONBOARDING_DT, a.DATE_OF_INCORPORATION DATE_OF_INCORP, a.PROF_ACTIVITY LINE_OF_BUSSINESS, b.CIN UNIQUE_COMPANY_ID, b.GST_NO GSTIN_NO, decode (b.T_PAN ,null,b.CRM_PAN ) PAN_NO,null PAN_DECLARATION, b.IEC IEC_CODE,null IEC_DECLARATION,KYC_REVIEW LAST_KYC_DT, 'N' FCRA_STATUS, NULL FCRA_REG_NO, NULL FCRA_REG_STATE,'N' RECORD_STATUS, 'N' VALIDATION_STATUS,null TAN_NO,null COMPANY_WEBSITE from PNB_ODS.DERV_CRM_CUSTOMER a ,PNB_ODS.DERV_CRM_ENTITY_DOCUMENT b ,PNB_ODS.DERV_CRM_ADDRESS C where a.cust_id =b.cust_id AND a.cust_id =c.cust_id and A.CUST_ID= ?";
				PreparedStatement stmtFetchCust = connectionDataWarehouse.prepareStatement(kc2Query);
				stmtFetchCust.setString(1, vCustId);
				rsTxnDataWarehouse = stmtFetchCust.executeQuery();
				while (rsTxnDataWarehouse.next()) {
					commitCounter++;
					pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("UCIC_ID"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("CUST_ID"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("ENTITY_NAME"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("MOBILE_NO"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("TELEPHONE_NO"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("EMAIL_ID"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("ADDRESS_LINE"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("ADDR_CITY"));
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("ADDR_STATE"));
					pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("COUNTRY"));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("ADDR_PIN_CODE"));
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("ADDR_LOCALITY"));
					pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("ADDR_DISTRICT"));
					pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString("REG_ADDR"));
					pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString("REG_CITY"));
					pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString("REG_STATE"));
					pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString("REG_COUNTRY"));
					pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString("REG_PIN_CODE"));
					pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString("REG_LOCALITY"));
					pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString("REG_DISTRICT"));
					pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString("CUST_TYPE"));
					pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString("UBO"));
					pstmtIsertIntoAml.setString(23, rsTxnDataWarehouse.getString("UBO_DECLARATION"));
					pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString("PEKRN"));
					pstmtIsertIntoAml.setString(25, rsTxnDataWarehouse.getString("DIRECTOR_NAME"));
					pstmtIsertIntoAml.setString(26, rsTxnDataWarehouse.getString("OTHER_CUST_TYPE"));
					pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString("COMPANY_ID_TYPE"));
					pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString("NO_IDENTIFIER_AVAILABLE"));
					pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString("CUST_RISK_LEVEL"));
					pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString("ONBOARDING_DT"));
					pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString("DATE_OF_INCORP"));
					pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString("LINE_OF_BUSSINESS"));
					pstmtIsertIntoAml.setString(33, rsTxnDataWarehouse.getString("UNIQUE_COMPANY_ID"));
					pstmtIsertIntoAml.setString(34, rsTxnDataWarehouse.getString("GSTIN_NO"));
					pstmtIsertIntoAml.setString(35, rsTxnDataWarehouse.getString("PAN_NO"));
					pstmtIsertIntoAml.setString(36, rsTxnDataWarehouse.getString("PAN_DECLARATION"));
					pstmtIsertIntoAml.setString(37, rsTxnDataWarehouse.getString("IEC_CODE"));
					pstmtIsertIntoAml.setString(38, rsTxnDataWarehouse.getString("IEC_DECLARATION"));
					pstmtIsertIntoAml.setString(39, rsTxnDataWarehouse.getString("LAST_KYC_DT"));
					pstmtIsertIntoAml.setString(40, rsTxnDataWarehouse.getString("FCRA_STATUS"));
					pstmtIsertIntoAml.setString(41, rsTxnDataWarehouse.getString("FCRA_REG_NO"));
					pstmtIsertIntoAml.setString(42, rsTxnDataWarehouse.getString("FCRA_REG_STATE"));
					pstmtIsertIntoAml.setString(43, rsTxnDataWarehouse.getString("RECORD_STATUS"));
					pstmtIsertIntoAml.setString(44, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
					pstmtIsertIntoAml.setString(45, rsTxnDataWarehouse.getString("TAN_NO"));
					pstmtIsertIntoAml.setString(46, rsTxnDataWarehouse.getString("COMPANY_WEBSITE"));
					pstmtIsertIntoAml.setString(47, vRequestId);
					pstmtIsertIntoAml.setString(48, batchNo);
					pstmtIsertIntoAml.setString(49, vReportRefNum);
					pstmtIsertIntoAml.setString(50, reportType);

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
			 * System.currentTimeMillis(); String procedure = "UPDATE_REG_KC2_STR_DATA";
			 * System.out.println( "Started execution for Cleaning StrKc1: " + procedure +
			 * "For the Request id" + vRequestId); stmt =
			 * connection.prepareCall("call UPDATE_REG_KC2_STR_DATA('" + vRequestId + "')");
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

			String status = ConstantFunctions.getThreadStatus("KC2");

			if (status.equals("N")) {
				ConstantFunctions.updateThreadStatus("KC2", "P");
				List<String> strTxnList = getStrKc2Data(strNo, "STR");

				if (strTxnList.size() > 0) {
					genStrKc2(strTxnList);
				} else {

				}

				ConstantFunctions.updateThreadStatus("KC2", "Y");
				String threadStatus = ConstantFunctions.checkStatus("KC2");

				if (threadStatus.equals("Y")) {
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}
			}
		} catch (Exception e) {
			try {
				ConstantFunctions.updateThreadStatus("KC2", "X");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

}

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


public class KC1ThreadSTR implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;

	String strNo = "";
	public KC1ThreadSTR(String strNo) {
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
			String sql = "select count(1) from str_reg_kc1 a where request_id= ?";
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

	public static List<String> getStrKc1Data(String Str_No,String reportType) {
		List<String> reportList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			System.out.println("Str_no"+Str_No);
				if (reportType.equals("STR")) {
					sql = "select  distinct tran_cust_id||'-'||REPORT_REF_NUM||'-'||str_no||'-'||batch_num from aml_transaction_master_f where str_no=? and cust_type='I'";
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

	public void genStrKc1(List<String> StrReportDataList) {
		CallableStatement stmt = null;
		ResultSet rsFetchCust = null;
		ResultSet rsTxnDataWarehouse = null;
		int cnt = 0;

		int commitCounter = 0;

		String vRequestId = null;
		String vCustId = null;
		String vReportRefNum = null;
		String reportType = "STR";

		

		//reportCustList = getCustReportList(reportType, StrReportDataList);

		// Reg_Kc1_insert

		String InsertKc1 = "insert into str_reg_kc1(UCIC_ID,CUST_ID,PAN_NO,PAN_DECLARATION,CKYC_NO,CKYC_DECLARATION,PASSPORT_NO,"
				+ "VOTER_ID,DRIVERS_LICENSE_NUM,NREGA_CARD,FIRST_NAME,MIDDLE_NAME,LAST_NAME,LAST_NAME_DECLARATION,"
				+ "FATHER_NAME,MOTHER_NAME,SPOUSE_PARTNER_NAME,GENDER,DATE_OF_BIRTH,NATIONALITY,MOBILE_NO,"
				+ "ALT_MOBILE_NO,TEL_NO,EMAIL_ID,PRIMARY_ADDR,PRIMARY_ADDR_LOCALITY,PRIMARY_ADDR_STATE,"
				+ "PRIMARY_ADDR_CITY,PRIMARY_ADDR_COUNTRY,PRIMARY_ADDR_PIN_CODE,CUST_TYPE,ANNUAL_INCOME,OCCUPATION,"
				+ "CUST_ONBOARDING_DT,LAST_KYC_DT,CUST_RISK,RECORD_STATUS,VALIDATION_STATUS,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,REPORT_TYPE) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmtIsertIntoAml = connection.prepareStatement(InsertKc1);) {
			for (String custId : StrReportDataList) {
				String[] custIdArr = custId.split("-");
				vRequestId = custIdArr[2].toString();
				vCustId = custIdArr[0].toString();
				vReportRefNum = custIdArr[1].toString();
				String batchNo = custIdArr[3].toString();

				String kc1Query = "SELECT A.CUST_ID UCIC_ID,A.CUST_ID CUST_ID,B.T_PAN PAN_NO,'NA' PAN_DECLARATION, A.CKYC_ID CKYC_NO ,(CASE WHEN CKYC_ID  IS NULL THEN 'NA'  END ) CKYC_DECLARATION, B.PSPRT PASSPORT_NO,B.VOTER_ID ,B.DL DRIVERS_LICENSE_NUM,B.ANREGA NREGA_CARD,A.CUST_FIRST_NAME FIRST_NAME ,A.CUST_MIDDLE_NAME MIDDLE_NAME ,A.CUST_LAST_NAME LAST_NAME ,'NA' LAST_NAME_DECLARATION ,A.FATHER_NAME FATHER_NAME ,A.MOTHER_NAME MOTHER_NAME ,A.SPOUSE_NAME SPOUSE_PARTNER_NAME ,(CASE WHEN A.GENDER='M' THEN 'MALE' WHEN A.GENDER='F' THEN 'FEMALE' ELSE 'OTHERS' END ) GENDER,TO_CHAR(A.DATE_OF_BIRTH,'DD/MM/YYYY') DATE_OF_BIRTH ,A.CNTRY_CODE NATIONALITY,A.ALERT_MOB MOBILE_NO ,A.MOBILE ALT_MOBILE_NO ,NULL TEL_NO ,A.ALERT_MAIL_ID EMAIL_ID ,COALESCE(A.ADDRESS_LINE1,' ')||COALESCE(A.ADDRESS_LINE2,' ') PRIMARY_ADDR ,'XXXXX' PRIMARY_ADDR_LOCALITY ,A.STATE_CODE PRIMARY_ADDR_STATE,A.CITY_CODE PRIMARY_ADDR_CITY,A.COUNTRY_CODE PRIMARY_ADDR_COUNTRY,A.ZIP PRIMARY_ADDR_PIN_CODE,A.CONSTITUTION_CODE CUST_TYPE,(select income from pnb_ods.derv_cust_income where cust_id='"+vCustId+"') ANNUAL_INCOME ,( CASE WHEN A.OCCUPATION='STUDENT' THEN 8 	WHEN A.OCCUPATION='HOUSEWIFE' THEN 7 	WHEN A.OCCUPATION='PVT EMPLOYEE' THEN 1 	WHEN A.OCCUPATION IN ('GOVT EMPLOYEE','PERSON BELOW OFFICER RANK','POLICE PERSONNEL- PNB RAKSHAK','BRIGADIER  OR EQUIVALENT RANK','BRIGADIERS AND ABOVE') THEN 4 WHEN A.OCCUPATION IN ('PSU EMPLOYEE') THEN 3 	WHEN A.OCCUPATION IN ('RETIRED-PSU EMPLOYEE','RETIRED - GOVT EMPLOYEE','RETIRED-OTHERS') THEN 6 	WHEN A.OCCUPATION IN ('CHARTERED ACCOUNTANT','DOCTOR','FINANCE','JEWELLERS','LAWYERS - ADVOCATES','CONTRACTOR') THEN 5 	ELSE 9 	END ) OCCUPATION  ,TO_CHAR(A.CUST_OPN_DT,'DD/MM/YYYY')  CUST_ONBOARDING_DT ,TO_CHAR(A.KYC_REVIEW,'DD/MM/YYYY')  LAST_KYC_DT , (CASE WHEN A.RISK='H' THEN 'HIGH' WHEN A.RISK='L' THEN 'LOW' WHEN A.RISK='M' THEN 'MEDIUM' END ) CUST_RISK,'N' RECORD_STATUS,'N' VALIDATION_STATUS FROM PNB_ODS.DERV_CRM_CUSTOMER A INNER JOIN PNB_ODS.DERV_CRM_ENTITY_DOCUMENT B ON B.CUST_ID=A.CUST_ID WHERE A.CUST_ID= ?";
				PreparedStatement stmtFetchCust = connectionDataWarehouse.prepareStatement(kc1Query);
				stmtFetchCust.setString(1, vCustId);
				rsTxnDataWarehouse = stmtFetchCust.executeQuery();
				while (rsTxnDataWarehouse.next()) {
					
					commitCounter++;
					
					pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("UCIC_ID"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("CUST_ID"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("PAN_NO"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("PAN_DECLARATION"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("CKYC_NO"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("CKYC_DECLARATION"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("PASSPORT_NO"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("VOTER_ID"));
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("DRIVERS_LICENSE_NUM"));
					pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("NREGA_CARD"));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("FIRST_NAME"));
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("MIDDLE_NAME"));
					pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("LAST_NAME"));
					pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString("LAST_NAME_DECLARATION"));
					pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString("FATHER_NAME"));
					pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString("MOTHER_NAME"));
					pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString("SPOUSE_PARTNER_NAME"));
					pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString("GENDER"));
					pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString("DATE_OF_BIRTH"));
					pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString("NATIONALITY"));
					pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString("MOBILE_NO"));
					pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString("ALT_MOBILE_NO"));
					pstmtIsertIntoAml.setString(23, rsTxnDataWarehouse.getString("TEL_NO"));
					pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString("EMAIL_ID"));
					pstmtIsertIntoAml.setString(25, rsTxnDataWarehouse.getString("PRIMARY_ADDR"));
					pstmtIsertIntoAml.setString(26, rsTxnDataWarehouse.getString("PRIMARY_ADDR_LOCALITY"));
					pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString("PRIMARY_ADDR_STATE"));
					pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString("PRIMARY_ADDR_CITY"));
					pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString("PRIMARY_ADDR_COUNTRY"));
					pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString("PRIMARY_ADDR_PIN_CODE"));
					pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString("CUST_TYPE"));
					pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString("ANNUAL_INCOME"));
					pstmtIsertIntoAml.setString(33, rsTxnDataWarehouse.getString("OCCUPATION"));
					pstmtIsertIntoAml.setString(34, rsTxnDataWarehouse.getString("CUST_ONBOARDING_DT"));
					pstmtIsertIntoAml.setString(35, rsTxnDataWarehouse.getString("LAST_KYC_DT"));
					pstmtIsertIntoAml.setString(36, rsTxnDataWarehouse.getString("CUST_RISK"));
					pstmtIsertIntoAml.setString(37, rsTxnDataWarehouse.getString("RECORD_STATUS"));
					pstmtIsertIntoAml.setString(38, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
					pstmtIsertIntoAml.setString(39, vRequestId);
					pstmtIsertIntoAml.setString(40, batchNo);
					pstmtIsertIntoAml.setString(41, vReportRefNum);
					pstmtIsertIntoAml.setString(42, reportType);

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
			 * System.currentTimeMillis(); String procedure = "UPDATE_REG_KC1_STR_DATA";
			 * System.out.println( "Started execution for Cleaning StrKc1: " + procedure +
			 * "For the Request id" + vRequestId); stmt =
			 * connection.prepareCall("call UPDATE_REG_KC1_STR_DATA('" + vRequestId + "')");
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
		try
		{
			makeConnection();
			makeConnectionToDataWarehouse();
			
			String status = ConstantFunctions.getThreadStatus("KC1");
			
			if(status.equals("N"))
			{
				ConstantFunctions.updateThreadStatus("KC1", "P");
				List<String> strTxnList = getStrKc1Data(strNo,"STR");
				
				if(strTxnList.size() > 0)
				{
					genStrKc1(strTxnList);
				}
				else
				{
					
				}
				
				ConstantFunctions.updateThreadStatus("KC1", "Y");
				String threadStatus = ConstantFunctions.checkStatus("KC1");
				
				if(threadStatus.equals("Y"))
				{
					ConstantFunctions.updateMainStatus(strNo, "Y");
				}
			}
		}
		catch(Exception e)
		{
			try {
				ConstantFunctions.updateThreadStatus("KC1", "X");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	

}

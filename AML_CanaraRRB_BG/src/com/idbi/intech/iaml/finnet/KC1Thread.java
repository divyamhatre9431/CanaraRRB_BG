package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class KC1Thread implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;

	List<String> reportDataList = null;
	String requestId = "";
	String batchNo = "";
	String reportType = "";
	String tid = "";
	
	public KC1Thread(List<String> reportDataList, String requestId, String batchNo,
			String reportType, String tid) {
		this.reportDataList = reportDataList;
		this.requestId = requestId;
		this.batchNo = batchNo;
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

	
	private static void insertDataFromWarehouseToAml(List<String> reportDataList,String requestId,String batchNo,String reportType) {
		
		Statement stFetchTxnWarehouse = null;
		//CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into reg_kc1(UCIC_ID,CUST_ID,PAN_NO,PAN_DECLARATION,CKYC_NO,CKYC_DECLARATION,PASSPORT_NO,"
					+ "VOTER_ID,DRIVERS_LICENSE_NUM,NREGA_CARD,FIRST_NAME,MIDDLE_NAME,LAST_NAME,LAST_NAME_DECLARATION,"
					+ "FATHER_NAME,MOTHER_NAME,SPOUSE_PARTNER_NAME,GENDER,DATE_OF_BIRTH,NATIONALITY,MOBILE_NO,"
					+ "ALT_MOBILE_NO,TEL_NO,EMAIL_ID,PRIMARY_ADDR,PRIMARY_ADDR_LOCALITY,PRIMARY_ADDR_STATE,"
					+ "PRIMARY_ADDR_CITY,PRIMARY_ADDR_COUNTRY,PRIMARY_ADDR_PIN_CODE,CUST_TYPE,ANNUAL_INCOME,OCCUPATION,"
					+ "CUST_ONBOARDING_DT,LAST_KYC_DT,CUST_RISK,RECORD_STATUS,VALIDATION_STATUS,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,REPORT_TYPE) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			for(String custId : reportDataList)
			{
				String[] custIdArr = custId.split("-");
				String customerId = custIdArr[0].toString();
				String repRefNum = custIdArr[1].toString();
				
				String kc1Query = "SELECT A.CUST_ID UCIC_ID,A.CUST_ID CUST_ID,B.T_PAN PAN_NO,'NA' PAN_DECLARATION, A.CKYC_ID CKYC_NO ,(CASE WHEN CKYC_ID  IS NULL THEN 'NA'  END ) CKYC_DECLARATION, B.PSPRT PASSPORT_NO,B.VOTER_ID ,B.DL DRIVERS_LICENSE_NUM,B.ANREGA NREGA_CARD,A.CUST_FIRST_NAME FIRST_NAME ,A.CUST_MIDDLE_NAME MIDDLE_NAME ,A.CUST_LAST_NAME LAST_NAME ,'NA' LAST_NAME_DECLARATION ,A.FATHER_NAME FATHER_NAME ,A.MOTHER_NAME MOTHER_NAME ,A.SPOUSE_NAME SPOUSE_PARTNER_NAME ,(CASE WHEN A.GENDER='M' THEN 'MALE' WHEN A.GENDER='F' THEN 'FEMALE' ELSE 'OTHERS' END ) GENDER,TO_CHAR(A.DATE_OF_BIRTH,'DD/MM/YYYY') DATE_OF_BIRTH ,A.CNTRY_CODE NATIONALITY,A.ALERT_MOB MOBILE_NO ,A.MOBILE ALT_MOBILE_NO ,NULL TEL_NO ,A.ALERT_MAIL_ID EMAIL_ID ,COALESCE(A.ADDRESS_LINE1,' ')||COALESCE(A.ADDRESS_LINE2,' ') PRIMARY_ADDR ,'XXXXX' PRIMARY_ADDR_LOCALITY ,A.STATE_CODE PRIMARY_ADDR_STATE,A.CITY_CODE PRIMARY_ADDR_CITY,A.COUNTRY_CODE PRIMARY_ADDR_COUNTRY,A.ZIP PRIMARY_ADDR_PIN_CODE,A.CONSTITUTION_CODE CUST_TYPE,(select income from pnb_ods.derv_cust_income where cust_id='"+customerId+"') ANNUAL_INCOME ,( CASE WHEN A.OCCUPATION='STUDENT' THEN 8 	WHEN A.OCCUPATION='HOUSEWIFE' THEN 7 	WHEN A.OCCUPATION='PVT EMPLOYEE' THEN 1 	WHEN A.OCCUPATION IN ('GOVT EMPLOYEE','PERSON BELOW OFFICER RANK','POLICE PERSONNEL- PNB RAKSHAK','BRIGADIER  OR EQUIVALENT RANK','BRIGADIERS AND ABOVE') THEN 4 WHEN A.OCCUPATION IN ('PSU EMPLOYEE') THEN 3 	WHEN A.OCCUPATION IN ('RETIRED-PSU EMPLOYEE','RETIRED - GOVT EMPLOYEE','RETIRED-OTHERS') THEN 6 	WHEN A.OCCUPATION IN ('CHARTERED ACCOUNTANT','DOCTOR','FINANCE','JEWELLERS','LAWYERS - ADVOCATES','CONTRACTOR') THEN 5 	ELSE 9 	END ) OCCUPATION  ,TO_CHAR(A.CUST_OPN_DT,'DD/MM/YYYY')  CUST_ONBOARDING_DT ,TO_CHAR(A.KYC_REVIEW,'DD/MM/YYYY')  LAST_KYC_DT , (CASE WHEN A.RISK='H' THEN 'HIGH' WHEN A.RISK='L' THEN 'LOW' WHEN A.RISK='M' THEN 'MEDIUM' END ) CUST_RISK,'N' RECORD_STATUS,'N' VALIDATION_STATUS FROM PNB_ODS.DERV_CRM_CUSTOMER A INNER JOIN PNB_ODS.DERV_CRM_ENTITY_DOCUMENT B ON B.CUST_ID=A.CUST_ID WHERE A.CUST_ID='"+customerId+"' ";
				
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(kc1Query);

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
					pstmtIsertIntoAml.setString(39, requestId);
					pstmtIsertIntoAml.setString(40, batchNo);
					pstmtIsertIntoAml.setString(41, repRefNum);
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
			pstmtIsertIntoAml.setString(5, "KC1");
			
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
			pstmtUpdateIntoAml.setString(5, "KC1");
			
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
			insertDataFromWarehouseToAml(reportDataList,requestId,batchNo,reportType);
			updateThreadStatus(requestId, reportType, tid, "Y");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}

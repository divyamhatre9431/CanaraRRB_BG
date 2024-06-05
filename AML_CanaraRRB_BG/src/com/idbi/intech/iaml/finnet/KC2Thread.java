package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class KC2Thread implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;

	List<String> reportDataList = null;
	String requestId = "";
	String batchNo = "";
	String reportType = "";
	String tid = "";
	
	public KC2Thread(List<String> reportDataList, String requestId, String batchNo,
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
					"insert into reg_kc2(UCIC_ID,CUST_ID,ENTITY_NAME,MOBILE_NO,TELEPHONE_NO,EMAIL_ID,ADDRESS_LINE,"
					+ "ADDR_CITY,ADDR_STATE,COUNTRY,ADDR_PIN_CODE,ADDR_LOCALITY,ADDR_DISTRICT,REG_ADDR,"
					+ "REG_CITY,REG_STATE,REG_COUNTRY,REG_PIN_CODE,REG_LOCALITY,REG_DISTRICT,CUST_TYPE,"
					+ "UBO,UBO_DECLARATION,PEKRN,DIRECTOR_NAME,OTHER_CUST_TYPE,COMPANY_ID_TYPE,"
					+ "NO_IDENTIFIER_AVAILABLE,CUST_RISK_LEVEL,ONBOARDING_DT,DATE_OF_INCORP,LINE_OF_BUSSINESS,UNIQUE_COMPANY_ID,"
					+ "GSTIN_NO,PAN_NO,PAN_DECLARATION,IEC_CODE,IEC_DECLARATION,LAST_KYC_DT,FCRA_STATUS,FCRA_REG_NO,FCRA_REG_STATE,"
					+ "RECORD_STATUS,VALIDATION_STATUS,TAN_NO,COMPANY_WEBSITE,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,REPORT_TYPE) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			for(String custId : reportDataList)
			{
				String[] custIdArr = custId.split("-");
				String customerId = custIdArr[0].toString();
				String repRefNum = custIdArr[1].toString();
				
				String kc2Query = "select a.CUST_ID UCIC_ID,a.CUST_ID, a.CUST_NAME ENTITY_NAME, a.ALERT_MOB MOBILE_NO, a.MOBILE TELEPHONE_NO, decode (a.ALERT_MAIL_ID, null,a.EMAIL) EMAIL_ID ,a.ADDRESS_LINE1||','||a.ADDRESS_LINE2 ADDRESS_LINE,a.CITY_CODE ADDR_CITY,a.STATE_CODE ADDR_STATE,a.COUNTRY_CODE COUNTRY,a.ZIP ADDR_PIN_CODE,'NA' ADDR_LOCALITY,'NA' ADDR_DISTRICT, c.REGTRD_ADDRESS REG_ADDR,c.REGTRD_CITY_CODE REG_CITY,c.REGTRD_STATE_CODE REG_STATE,c.REGTRD_COUNTRY_CODE REG_COUNTRY,c.REGTRD_ZIP REG_PIN_CODE,'NA' REG_LOCALITY,'NA' REG_DISTRICT, decode (a.LEGALENTITY_TYPE , null,a.CUST_TYPE_CODE) as CUST_TYPE,null UBO,null UBO_DECLARATION,null PEKRN, null DIRECTOR_NAME,null OTHER_CUST_TYPE,null COMPANY_ID_TYPE,null NO_IDENTIFIER_AVAILABLE, a.RISK CUST_RISK_LEVEL, a.CUST_OPN_DT ONBOARDING_DT, a.DATE_OF_INCORPORATION DATE_OF_INCORP, a.PROF_ACTIVITY LINE_OF_BUSSINESS, b.CIN UNIQUE_COMPANY_ID, b.GST_NO GSTIN_NO, decode (b.T_PAN ,null,b.CRM_PAN ) PAN_NO,null PAN_DECLARATION, b.IEC IEC_CODE,null IEC_DECLARATION,KYC_REVIEW LAST_KYC_DT, 'N' FCRA_STATUS, NULL FCRA_REG_NO, NULL FCRA_REG_STATE,'N' RECORD_STATUS, 'N' VALIDATION_STATUS,null TAN_NO,null COMPANY_WEBSITE from PNB_ODS.DERV_CRM_CUSTOMER a ,PNB_ODS.DERV_CRM_ENTITY_DOCUMENT b ,PNB_ODS.DERV_CRM_ADDRESS C where a.cust_id =b.cust_id AND a.cust_id =c.cust_id and A.CUST_ID='"+customerId+"' ";
				
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(kc2Query);

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
					pstmtIsertIntoAml.setString(47, requestId);
					pstmtIsertIntoAml.setString(48, batchNo);
					pstmtIsertIntoAml.setString(49, repRefNum);
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
			pstmtIsertIntoAml.setString(5, "KC2");
			
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
			pstmtUpdateIntoAml.setString(5, "KC2");
			
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

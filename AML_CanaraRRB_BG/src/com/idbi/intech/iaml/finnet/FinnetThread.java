package com.idbi.intech.iaml.finnet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class FinnetThread implements Runnable {
	
	private static Connection connectionDataWarehouse;
	private static Connection connection;

	String strNo = "";
	String custId = "";
	String custAcid = "";
	String accountNo = "";
	String custType = "";
	String tranChannel = "";
	String reptRefNo = "";
	String fromDate = "";
	String toDate = "";
	
	
	public FinnetThread(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {
		this.strNo = strNo;
		this.custId = custId;
		this.custAcid = custAcid;
		this.accountNo = accountNo;
		this.custType = custType;
		this.tranChannel = tranChannel;
		this.reptRefNo = reptRefNo;
		this.fromDate = fromDate;
		this.toDate = toDate;
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

	
	private static void insertKC1DataToAml(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {
		
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

			String kc1Query = "SELECT A.CUST_ID UCIC_ID,A.CUST_ID CUST_ID,B.T_PAN PAN_NO,'NA' PAN_DECLARATION, A.CKYC_ID CKYC_NO ,(CASE WHEN CKYC_ID  IS NULL THEN 'NA'  END ) CKYC_DECLARATION, B.PSPRT PASSPORT_NO,B.VOTER_ID ,B.DL DRIVERS_LICENSE_NUM,B.ANREGA NREGA_CARD,A.CUST_FIRST_NAME FIRST_NAME ,A.CUST_MIDDLE_NAME MIDDLE_NAME ,A.CUST_LAST_NAME LAST_NAME ,'NA' LAST_NAME_DECLARATION ,A.FATHER_NAME FATHER_NAME ,A.MOTHER_NAME MOTHER_NAME ,A.SPOUSE_NAME SPOUSE_PARTNER_NAME ,(CASE WHEN A.GENDER='M' THEN 'MALE' WHEN A.GENDER='F' THEN 'FEMALE' ELSE 'OTHERS' END ) GENDER,TO_CHAR(A.DATE_OF_BIRTH,'DD/MM/YYYY') DATE_OF_BIRTH ,A.CNTRY_CODE NATIONALITY,A.ALERT_MOB MOBILE_NO ,A.MOBILE ALT_MOBILE_NO ,NULL TEL_NO ,A.ALERT_MAIL_ID EMAIL_ID ,COALESCE(A.ADDRESS_LINE1,' ')||COALESCE(A.ADDRESS_LINE2,' ') PRIMARY_ADDR ,'XXXXX' PRIMARY_ADDR_LOCALITY ,A.STATE_CODE PRIMARY_ADDR_STATE,A.CITY_CODE PRIMARY_ADDR_CITY,A.COUNTRY_CODE PRIMARY_ADDR_COUNTRY,A.ZIP PRIMARY_ADDR_PIN_CODE,A.CONSTITUTION_CODE CUST_TYPE,A.ANNUL_INCOME ANNUAL_INCOME ,( CASE WHEN A.OCCUPATION='STUDENT' THEN 8 	WHEN A.OCCUPATION='HOUSEWIFE' THEN 7 	WHEN A.OCCUPATION='PVT EMPLOYEE' THEN 1 	WHEN A.OCCUPATION IN ('GOVT EMPLOYEE','PERSON BELOW OFFICER RANK','POLICE PERSONNEL- PNB RAKSHAK','BRIGADIER  OR EQUIVALENT RANK','BRIGADIERS AND ABOVE') THEN 4 WHEN A.OCCUPATION IN ('PSU EMPLOYEE') THEN 3 	WHEN A.OCCUPATION IN ('RETIRED-PSU EMPLOYEE','RETIRED - GOVT EMPLOYEE','RETIRED-OTHERS') THEN 6 	WHEN A.OCCUPATION IN ('CHARTERED ACCOUNTANT','DOCTOR','FINANCE','JEWELLERS','LAWYERS - ADVOCATES','CONTRACTOR') THEN 5 	ELSE 9 	END ) OCCUPATION  ,TO_CHAR(A.CUST_OPN_DT,'DD/MM/YYYY')  CUST_ONBOARDING_DT ,TO_CHAR(A.KYC_REVIEW,'DD/MM/YYYY')  LAST_KYC_DT , (CASE WHEN A.RISK='H' THEN 'HIGH' WHEN A.RISK='L' THEN 'LOW' WHEN A.RISK='M' THEN 'MEDIUM' END ) CUST_RISK,'N' RECORD_STATUS,'N' VALIDATION_STATUS FROM PNB_ODS.DERV_CRM_CUSTOMER A INNER JOIN PNB_ODS.DERV_CRM_ENTITY_DOCUMENT B ON B.CUST_ID=A.CUST_ID WHERE A.CUST_ID='"+custId+"' ";
			
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
				pstmtIsertIntoAml.setString(39, strNo);
				pstmtIsertIntoAml.setString(40, strNo);
				pstmtIsertIntoAml.setString(41, reptRefNo);
				pstmtIsertIntoAml.setString(42, "STR");

				pstmtIsertIntoAml.execute();

				System.out.println(commitCounter);
				if (commitCounter == 500) {
					connection.commit();
					commitCounter = 0;
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
	
	
	private static void insertKC2DataToAml(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {
		
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

			String kc2Query = "select a.CUST_ID UCIC_ID,a.CUST_ID, a.CUST_NAME ENTITY_NAME, a.ALERT_MOB MOBILE_NO, a.MOBILE TELEPHONE_NO, decode (a.ALERT_MAIL_ID, null,a.EMAIL) EMAIL_ID ,a.ADDRESS_LINE1||','||a.ADDRESS_LINE2 ADDRESS_LINE,a.CITY_CODE ADDR_CITY,a.STATE_CODE ADDR_STATE,a.COUNTRY_CODE COUNTRY,a.ZIP ADDR_PIN_CODE,'NA' ADDR_LOCALITY,'NA' ADDR_DISTRICT, c.REGTRD_ADDRESS REG_ADDR,c.REGTRD_CITY_CODE REG_CITY,c.REGTRD_STATE_CODE REG_STATE,c.REGTRD_COUNTRY_CODE REG_COUNTRY,c.REGTRD_ZIP REG_PIN_CODE,'NA' REG_LOCALITY,'NA' REG_DISTRICT, decode (a.LEGALENTITY_TYPE , null,a.CUST_TYPE_CODE) as CUST_TYPE,null UBO,null UBO_DECLARATION,null PEKRN, null DIRECTOR_NAME,null OTHER_CUST_TYPE,null COMPANY_ID_TYPE,null NO_IDENTIFIER_AVAILABLE, a.RISK CUST_RISK_LEVEL, a.CUST_OPN_DT ONBOARDING_DT, a.DATE_OF_INCORPORATION DATE_OF_INCORP, a.PROF_ACTIVITY LINE_OF_BUSSINESS, b.CIN UNIQUE_COMPANY_ID, b.GST_NO GSTIN_NO, decode (b.T_PAN ,null,b.CRM_PAN ) PAN_NO,null PAN_DECLARATION, b.IEC IEC_CODE,null IEC_DECLARATION,RISK_CATG_DT_RET LAST_KYC_DT, 'N' FCRA_STATUS, NULL FCRA_REG_NO, NULL FCRA_REG_STATE,'N' RECORD_STATUS, 'N' VALIDATION_STATUS,null TAN_NO,null COMPANY_WEBSITE from PNB_ODS.DERV_CRM_CUSTOMER a ,PNB_ODS.DERV_CRM_ENTITY_DOCUMENT b ,PNB_ODS.DERV_CRM_ADDRESS C where a.cust_id =b.cust_id AND a.cust_id =c.cust_id and A.CUST_ID='"+custId+"' ";
			
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
				pstmtIsertIntoAml.setString(47, strNo);
				pstmtIsertIntoAml.setString(48, strNo);
				pstmtIsertIntoAml.setString(49, reptRefNo);
				pstmtIsertIntoAml.setString(50, "STR");

				pstmtIsertIntoAml.execute();

				System.out.println(commitCounter);
				if (commitCounter == 500) {
					connection.commit();
					commitCounter = 0;
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
	
	
	private static void insertTC1DataToAml(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {

		Statement stFetchTxnWarehouse = null;
		// CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into reg_tc1(TRAN_CUST_ID,ATM_CAM_ID,TRANSACTION_ID,SWITCH_TXN_ID,TRANSACTION_AMOUNT,DEPOSIT_WITHDRAWAL,"
							+ "TRANSACTION_DATE,TRANSACTION_TIME,ACCOUNT_NUMBER,ACCOUNT_TYPE,CARD_TYPE,CARD_NUMBER,TRANSACTION_STATUS,BRANCH_ID_AC,"
							+ "CARD_ISSUE_CNTRY,CARD_ISSUE_BANK,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			String tc1Query = "select ATM_ID,OSEQ_NUM,PAN_CARD_NO,TRAN_STATUS,'N' RECORD_STATUS, 'N' VALIDATION_STATUS,TRAN_TIME,TRANSACTION_DATE,TXN_AMOUNT,TXN_SNO from PNB_ODS.ATMSWTCH_ATMTRAN where TRANSACTION_DATE between '"
					+ fromDate + "' and '"+ toDate + "' and to_account='" + accountNo + "' or from_account='" + accountNo+ " ";

			rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(tc1Query);

			while (rsTxnDataWarehouse.next()) {

				commitCounter++;

				pstmtIsertIntoAml.setString(1, custId);
				pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("ATM_ID"));
				pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("TXN_SNO"));
				pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("OSEQ_NUM"));
				pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("TXN_AMOUNT"));
				pstmtIsertIntoAml.setString(6, "C");
				pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("TRANSACTION_DATE"));
				pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("TRAN_TIME"));
				pstmtIsertIntoAml.setString(9, accountNo);
				pstmtIsertIntoAml.setString(10, "");
				pstmtIsertIntoAml.setString(11, "");
				pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("PAN_CARD_NO"));
				pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("TRAN_STATUS"));
				pstmtIsertIntoAml.setString(14, "");
				pstmtIsertIntoAml.setString(15, "");
				pstmtIsertIntoAml.setString(16, "");
				pstmtIsertIntoAml.setString(17, strNo);
				pstmtIsertIntoAml.setString(18, strNo);
				pstmtIsertIntoAml.setString(19, reptRefNo);
				pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString("RECORD_STATUS"));
				pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
				pstmtIsertIntoAml.setString(22, "STR");

				pstmtIsertIntoAml.execute();

				System.out.println(commitCounter);
				if (commitCounter == 500) {
					connection.commit();
					commitCounter = 0;
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static void insertTS1DataToAml(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {
		
		Statement stFetchTxnWarehouse = null;
		//CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;
		

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_TS1(TRAN_CUST_ID,TRANSACTION_DATE,TRANSACTION_TIME,TRANSACTION_ID,SENDER_IFSC_CODE,SENDER_ACCOUNT_NO,"
					+ "BENEF_IFSC_CODE,BENEF_ACCOUNT_NO,TRANSACTION_TYPE,TRANSACTION_AMOUNT,NARRATION,"
					+ "REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
			
			String acctDtlsQuery = "select CIF_ID TRAN_CUST_ID,LCHG_TIME TRANSACTION_DATE, LCHG_TIME TRANSACTION_TIME,TRANSACTION_REF TRANSACTION_ID,SENDER_BIC SENDER_IFSC_CODE,DR_ACCT SENDER_ACCOUNT_NO,RECEIVER_BIC BENEF_IFSC_CODE,CR_ACCT BENEF_ACCOUNT_NO,SERVICE_TYPE TRANSACTION_TYPE, SETTLEMENT_AMT TRANSACTION_AMOUNT,PURPOSE_REMARKS NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from PNB_ODS.RTGS_NEFT_PY_ED "
					+ "where CUST_PROCESSING_DATE between '" + fromDate + "' and '" + toDate + "' and CIF_ID='" + custId + "' and DR_ACCT='" + accountNo + "' or CR_ACCT='" + accountNo + "' ";

			rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

			while (rsTxnDataWarehouse.next()) {

				commitCounter++;

				pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("TRAN_CUST_ID"));
				pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("TRANSACTION_DATE"));
				pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("TRANSACTION_TIME"));
				pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("TRANSACTION_ID"));
				pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("SENDER_IFSC_CODE"));
				pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("SENDER_ACCOUNT_NO"));
				pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("BENEF_IFSC_CODE"));
				pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("BENEF_ACCOUNT_NO"));
				pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("TRANSACTION_TYPE"));
				pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("TRANSACTION_AMOUNT"));
				pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("NARRATION"));
				pstmtIsertIntoAml.setString(12, strNo);
				pstmtIsertIntoAml.setString(13, strNo);
				pstmtIsertIntoAml.setString(14, reptRefNo);
				pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString("RECORD_STATUS"));
				pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
				pstmtIsertIntoAml.setString(17, "STR");

				pstmtIsertIntoAml.execute();

				System.out.println(commitCounter);
				if (commitCounter == 500) {
					connection.commit();
					commitCounter = 0;
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
	
	
	private static void insertTS2DataToAml(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {

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
							+ "TRANSACTION_AMOUNT,NARRATION,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			String acctDtlsQuery = "select REMITTORID TRAN_CUST_ID, TXNDATE TRANSACTION_DATE, TXNDATE TRANSACTION_TIME, REMITTORNAME SENDER_NAME, BENENAME BENEF_NAME, REMITTOR_MOBILE SENDER_MOBILE_NO, BENE_MOBILE BENEF_MOBILE_NO, REMITTORMMID SENDER_MMID,REMITTORACCOUNTNUMBER SENDER_ACCOUNT_NO, BENEMMID BENEF_MMID,BENEACCOUNTNUMBER BENEF_ACCOUNT_NO,BENEIFSC BENEF_IFSC_CODE, TRANTYPE TRANSACTION_TYPE, AMOUNT TRANSACTION_AMOUNT, PURPOSE_CODE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS "
					+ "from PNB_ODS.IMPS_TBL_TRANLOG_OUTWARD where txndate between '" + fromDate
					+ " 00:00:00.000' and '" + toDate + " 23:59:59.999' and REMITTORID='" + custId + "' and REMITTORACCOUNTNUMBER='" + accountNo + "' or BENEACCOUNTNUMBER='" + accountNo + "' union all "
					+ "select REMITTORID TRAN_CUST_ID, TXNDATE TRANSACTION_DATE, TXNDATE TRANSACTION_TIME, REMITTORNAME SENDER_NAME, BENENAME BENEF_NAME, REMITTOR_MOBILE SENDER_MOBILE_NO, BENE_MOBILE BENEF_MOBILE_NO, REMITTORMMID SENDER_MMID,REMITTORACCOUNTNUMBER SENDER_ACCOUNT_NO, BENEMMID BENEF_MMID,BENEACCOUNTNUMBER BENEF_ACCOUNT_NO,BENEIFSC BENEF_IFSC_CODE, TRANTYPE TRANSACTION_TYPE, AMOUNT TRANSACTION_AMOUNT, PURPOSE_CODE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS "
					+ "from PNB_ODS.IMPS_TBL_TRANLOG where txndate between '" + fromDate
					+ " 00:00:00.000' and '" + toDate + " 23:59:59.999' and REMITTORID='" + custId + "' and REMITTORACCOUNTNUMBER='" + accountNo + "' or BENEACCOUNTNUMBER='" + accountNo + "' ";

			rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

			while (rsTxnDataWarehouse.next()) {

				commitCounter++;

				pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("TRAN_CUST_ID"));
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
				pstmtIsertIntoAml.setString(16, strNo);
				pstmtIsertIntoAml.setString(17, strNo);
				pstmtIsertIntoAml.setString(18, reptRefNo);
				pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString("RECORD_STATUS"));
				pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
				pstmtIsertIntoAml.setString(21, "STR");

				pstmtIsertIntoAml.execute();

				System.out.println(commitCounter);
				if (commitCounter == 500) {
					connection.commit();
					commitCounter = 0;
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
				if (connection != null) {
					connection.close();
					connection = null;
				}
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

	
	private static void insertTS3DataToAml(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {

		Statement stFetchTxnWarehouse = null;
		// CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_TS3(TRANSACTION_DATE,TRANSACTION_TIME,SENDER_NAME,BENEF_NAME,SENDER_VPA,"
							+ "SENDER_ACCOUNT_NO,SENDER_IFSC_CODE,SENDER_MOBILE_NO,BENEF_VPA,BENEF_ACCOUNT_NO,BENEF_IFSC_CODE,BENEF_MOBILE_NO,"
							+ "TRANSACTION_TYPE,TRANSACTION_AMOUNT,NARRATION,REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();

			String acctDtlsQuery = "select TRNDATE TRANSACTION_DATE,TRNDATE TRANSACTION_TIME, PRFNAME SENDER_NAME, PYFNAME BENEF_NAME,PRFVADDR SENDER_VPA, FROMACCNO SENDER_ACCOUNT_NO, PRIFSCCODE SENDER_IFSC_CODE, PRDMOBILE SENDER_MOBILE_NO, PYFVADDR BENEF_VPA, TOACCNO BENEF_ACCOUNT_NO,PYIFSCCODE BENEF_IFSC_CODE, PYDMOBILE BENEF_MOBILE_NO, TXNTYPE TRANSACTION_TYPE, TXNAMOUNT TRANSACTION_AMOUNT, TXNNOTE NARRATION,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from PNB_ODS.UPINEW_VTSTSETTLEREQ "
					+ "where TRNDATE between '" + fromDate + " 00:00:00.000' and '" + toDate
					+ " 23:59:59.999' and FROMACCNO='" + accountNo + "' or TOACCNO='" + accountNo + "' ";

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
				pstmtIsertIntoAml.setString(16, strNo);
				pstmtIsertIntoAml.setString(17, strNo);
				pstmtIsertIntoAml.setString(18, reptRefNo);
				pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString("RECORD_STATUS"));
				pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
				pstmtIsertIntoAml.setString(21, "STR");

				pstmtIsertIntoAml.execute();

				System.out.println(commitCounter);
				if (commitCounter == 500) {
					connection.commit();
					commitCounter = 0;
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
	
	
	private static void insertADDataToAml(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {
		
		Statement stFetchTxnWarehouse = null;
		//CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_ACCOUNT_DTLS(cust_id,branch_code,account_number,account_type,date_of_ac_opn,account_status,"
					+ "date_of_ac_cls,no_of_debit,total_debit_amt,no_of_credit,total_credit_amt,total_cash_credit,total_cash_debit,no_of_cash_txn,"
					+ "REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
			
			String acctDtlsQuery = "select cust_id,SOL_ID branch_code,FORACID account_number,SCHM_TYPE account_type, ACCT_OPN_DATE date_of_ac_opn,ACCT_CLS_FLG account_status,ACCT_CLS_DATE date_of_ac_cls, 0.00 no_of_debit,0.00 total_debit_amt,0.00 no_of_credit,0.00 total_credit_amt,0.00 total_cash_credit, 0.00 total_cash_debit,0 no_of_cash_txn,'N' RECORD_STATUS, 'N' VALIDATION_STATUS, 'NA' REASON_FOR_FREEZE from pnb_ods.cbs_gam where cust_id='"+custId+"' AND FORACID='"+accountNo+"' ";
			
			rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctDtlsQuery);

			while (rsTxnDataWarehouse.next()) {
				
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
				pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString("no_of_cash_txn"));
				pstmtIsertIntoAml.setString(15, strNo);
				pstmtIsertIntoAml.setString(16, strNo);
				pstmtIsertIntoAml.setString(17, reptRefNo);
				pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString("RECORD_STATUS"));
				pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
				pstmtIsertIntoAml.setString(20, "STR");

				pstmtIsertIntoAml.execute();

				System.out.println(commitCounter);
				if (commitCounter == 500) {
					connection.commit();
					commitCounter = 0;
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

	
	private static void insertAPRDataToAml(String strNo, String custId, String custAcid,
			String accountNo, String custType, String tranChannel,String reptRefNo, String fromDate,String toDate) {
		
		Statement stFetchTxnWarehouse = null;
		//CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;

		try {

			// Insert script for AML DB
			pstmtIsertIntoAml = connection.prepareStatement(
					"insert into REG_ACCOUNT_PERSON_RELTN(account_number,related_person_type,name_of_noncustomer,unique_ref_no,ind_nid_status,"
					+ "REQUEST_ID,BATCH_NUM,REPORT_REF_NUM,RECORD_STATUS,VALIDATION_STATUS,REPORT_TYPE) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?)");

			// Query for fetching txn data from EDW
			stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
			
			String acctPersonRelationQuery = "select acid account_number,ACCT_POA_AS_REC_TYPE related_person_type,acct_poa_as_name name_of_noncustomer,ACCT_POA_AS_SRL_NUM unique_ref_no,null ind_nid_status,'N' RECORD_STATUS, 'N' VALIDATION_STATUS from pnb_ods.cbs_aas where cust_id='"+custId+"' and acid='"+custAcid+"' ";

			rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery(acctPersonRelationQuery);

			while (rsTxnDataWarehouse.next()) {
				
				commitCounter++;
				
				pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("account_number"));
				pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("related_person_type"));
				pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("name_of_noncustomer"));
				pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("unique_ref_no"));
				pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("ind_nid_status"));
				pstmtIsertIntoAml.setString(6, strNo);
				pstmtIsertIntoAml.setString(7, strNo);
				pstmtIsertIntoAml.setString(8, reptRefNo);
				pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("RECORD_STATUS"));
				pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("VALIDATION_STATUS"));
				pstmtIsertIntoAml.setString(11, "STR");

				pstmtIsertIntoAml.execute();

				System.out.println(commitCounter);
				if (commitCounter == 500) {
					connection.commit();
					commitCounter = 0;
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


	
	@Override
	public void run() {
		
		try
		{
			makeConnection();
			makeConnectionToDataWarehouse();
			
			if(custType.equals("I"))
			{
				insertKC1DataToAml(strNo, custId, custAcid, accountNo, custType, tranChannel, reptRefNo, fromDate, toDate);
			}
			else
			{
				insertKC2DataToAml(strNo, custId, custAcid, accountNo, custType, tranChannel, reptRefNo, fromDate, toDate);
			}
			
			insertADDataToAml(strNo, custId, custAcid, accountNo, custType, tranChannel, reptRefNo, fromDate, toDate);
			insertAPRDataToAml(strNo, custId, custAcid, accountNo, custType, tranChannel, reptRefNo, fromDate, toDate);
			
			if(tranChannel.equalsIgnoreCase("TC1"))
			{
				insertTC1DataToAml(strNo, custId, custAcid, accountNo, custType, tranChannel, reptRefNo, fromDate, toDate);
			}
			else if(tranChannel.equalsIgnoreCase("TS1"))
			{
				insertTS1DataToAml(strNo, custId, custAcid, accountNo, custType, tranChannel, reptRefNo, fromDate, toDate);
			}
			else if(tranChannel.equalsIgnoreCase("TS2"))
			{
				insertTS2DataToAml(strNo, custId, custAcid, accountNo, custType, tranChannel, reptRefNo, fromDate, toDate);
			}
			else if(tranChannel.equalsIgnoreCase("TS3"))
			{
				insertTS3DataToAml(strNo, custId, custAcid, accountNo, custType, tranChannel, reptRefNo, fromDate, toDate);
			}
			else
			{
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}

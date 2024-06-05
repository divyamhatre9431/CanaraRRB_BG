package com.idbi.intech.iaml.datapull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CreditCardDataExtractionService {

	private static Connection connectionDataWarehouse;
	private static Connection connectionAMLDb;
	//private final static String patternfordb2 = "yyyy-MM-dd HH:mm:ss.S";
	private final static String patternfordb2 = "yyyy-MM-dd";
	private final static String patternWithoutTS = "dd-MMM-yyyy";
	private final static String patternWithTS = "dd-MMM-yyyy HH:mm:ss";

	private static void makeConnectionToDataWarehouse() {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			connectionDataWarehouse = DriverManager.getConnection("jdbc:db2://172.16.15.151:50000/BLUDB", "PK1405501",
					"PKpnbobc@501");
			System.out.println(" :::::::::::::::CONNECTED TO::::::::::::::::");
			System.out.println("EDW OK....................");
			connectionDataWarehouse.setAutoCommit(false);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void makeConnectionAMLLive() throws SQLException {
		String connCred = null, strConnection = null, ip = null, port = null, dbname = null, username = null,
				password = null;
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			// System.out.println("Current Directory::"+dir);
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			connCred = amlProp.getProperty("DBDETAILS");
			// AML_PasswordBaseEncryption obj = new
			// AML_PasswordBaseEncryption();
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// String pass_info[] = obj.decrypt(connCred).split("~");
			String pass_info[] = connCred.split("~");
			strConnection = pass_info[0];
			ip = pass_info[1];
			port = pass_info[2];
			dbname = pass_info[3];
			username = pass_info[4];
			password = pass_info[5];
			connectionAMLDb = DriverManager.getConnection(strConnection + ip + port + dbname, username, password);
			System.out.println(" :::::::::::::::CONNECTED TO:::::::::::::::::");
			System.out.println("IP::" + ip + " : " + port);
			connectionAMLDb.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String dateConverterForDb2(String in_date, boolean withTimeStamp) {
		String outDate = "";
		SimpleDateFormat outFormat = null;
		try {
			
				SimpleDateFormat formatter = new SimpleDateFormat(patternfordb2);
				Date dtTemp;

				dtTemp = formatter.parse(in_date);

				if (withTimeStamp) {
					outFormat = new SimpleDateFormat(patternWithTS);
				} else {
					outFormat = new SimpleDateFormat(patternWithoutTS);
				}
				outDate = outFormat.format(dtTemp);
			
			
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return outDate;
	}

	private void insertDataFromWarehouseToAml() {
		Statement stFetchTxnWarehouse = null;
		//CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;
		Statement stInsertTxnMain = null;
		ResultSet rsTxnMain = null;
		Connection conn11=null;
        Statement stmt1 =null;
        Connection conn2=null;
        Statement stmt2 =null;
		//PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;
		
		try {
			String txn_date = getDataExtractionDate();

			System.out.println("Start_date:" + txn_date);

			conn11 = ConnectionFactory.makeConnectionAMLLive();
			stmt1 = conn11.createStatement();
			stmt1.executeUpdate("update AML_BIZZ_TAB_CC set data_pull_flg='P'");
			conn11.commit();
			conn11.close();
				
				conn2= ConnectionFactory.makeConnectionAMLLive();
				stmt2 = conn11.createStatement();
				stmt2.executeUpdate("truncate table aml_credit_transaction_temp");
				conn2.commit();
				conn2.close();
				

			
			
			
			if(!txn_date.equals(""))
			{
				// Insert script for AML DB
				pstmtIsertIntoAml = connectionAMLDb.prepareStatement(
						"insert into aml_credit_transaction_temp(CREDIT_CARD_NO,SOL_ID,CBS_CUST_ID,CBS_AC_NO,TRAN_IDENT,TXN_CODE,"
						+ " TXN_CODE_DESC, TXN_TYPE ,txn_date, txn_process_date, CURR_CODE,TXN_AMT,TRAN_PARTICULAR,LIMIT_VAL,"
						+ " MCC_CODE,TERMINAL_ID) "
						+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				// Query for fetching txn data from EDW
				stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
				
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery("  SELECT "
+ " BILLED_TXN_CARD_NO AS CREDIT_CARD_NO, "
+ " BR.BRN_SOL_ID AS SOL_ID, "
+ " CRD.CRD_CUSTOMER_ID AS CBS_CUST_ID, "
+ " CRD.CRD_ACCOUNT_NUMBER AS CBS_ACCOUNT_NUMBER, "
+ " TXN.BILLED_TXN_SRNO AS TRAN_IDENTIFIER, "
+ " TXN.BILLED_TXN_TYPE AS TXN_CODE, "
+ " TCODE.TRXN_CODE_NARRATION AS TXN_CODE_DESC, "
+ " (CASE "
+ "        WHEN TXN.BILLED_TXN_DRCR = 1 THEN 'D' "
+ "        WHEN TXN.BILLED_TXN_DRCR = 2 THEN 'C' "
+ "        ELSE NULL "
+ " END) AS TXN_TYPE, "
+ " BILLED_TXN_CHGDATE AS TXN_DATE, "
+ " BILLED_TXN_DTOR AS TXN_PROCESS_DATE, "
+ " DECIMAL(TXN.BILLED_TXN_SRCECUR,25,0) AS CURRENCY_CODE, "
+ " (CASE "
+ "        WHEN TXN.BILLED_TXN_DRCR = 1 THEN TXN.BILLED_TXN_AMNTDR "
+ "        WHEN TXN.BILLED_TXN_DRCR = 2 THEN TXN.BILLED_TXN_AMNTCR "
+ "        ELSE MAX(TXN.BILLED_TXN_AMNTDR, TXN.BILLED_TXN_AMNTCR) "
+ " END) AS TXN_AMOUNT, "
+ " TXN.BILLED_TXN_DESC1||TXN.BILLED_TXN_DESC2||TXN.BILLED_TXN_DESC3 AS TRAN_PARTICULAR, "
+ " CRD.CRD_CRE_LIM AS LIMIT, "
+ " TXN.BILLED_TXN_MCCCODE as mcc_code, "
+ " TB.DWH_BASE1_TERMSNO AS TERMINAL_ID "
+ " FROM "
+ " PNB_ODS.CCARD_TRANSACTION_BILLED AS TXN "
+ " LEFT JOIN "
+ " ( "
+ "        SELECT * "
+ "        FROM "
+ "        PNB_ODS.CCARD_CARD_MASTER AS CRD "
+ "        WHERE "
+ "        CRD.CRD_ADD_UPD_FLG = (CASE WHEN CRD.CRD_ID IN ('0000001236632000', '0000001385266000', '0000001246343000', '0000001384746000', '0000001244732000') THEN 'U' ELSE CRD.CRD_ADD_UPD_FLG END) "
+ " )CRD ON CRD.CRD_ID = TXN.BILLED_TXN_ACCOUNT_ID "
+ " LEFT JOIN "
+ " PNB_ODS.CCARD_BRANCH_MASTER AS BR ON BR.BRN_CDE = CRD.CRD_RES_BRN_CDE "
+ " LEFT JOIN "
+ " PNB_ODS.CCARD_TRANSACTION_BASE_1 AS TB ON TB.DWH_BASE1_SRNO = TXN.BILLED_TXN_AUTHSRNO "
+ " LEFT JOIN "
+ " ( "
+ "        SELECT TRXN_CODE_ID, TRXN_CODE_NARRATION "
+ "        FROM "
+ "        PNB_ODS.CCARD_ISGGEN_TRANSACTION_CODES "
+ "        GROUP BY TRXN_CODE_ID, TRXN_CODE_NARRATION "
+ " )AS TCODE ON TCODE.TRXN_CODE_ID = TXN.BILLED_TXN_TYPE "
+ " WHERE "
+ " TXN.BILLED_TXN_CHGDATE = '"+txn_date+"' ");
				
				while (rsTxnDataWarehouse.next()) {
					commitCounter++;
					//System.out.println("!");
					//System.out.println(rsTxnDataWarehouse.getString("TXN_SNO"));
					
					pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("CREDIT_CARD_NO"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("SOL_ID"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("CBS_CUST_ID"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("CBS_ACCOUNT_NUMBER"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("TRAN_IDENTIFIER"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("TXN_CODE"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("TXN_CODE_DESC"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("TXN_TYPE"));
					pstmtIsertIntoAml.setString(9, dateConverterForDb2(rsTxnDataWarehouse.getString("txn_date"), false));
					//pstmtIsertIntoAml.setString(10, dateConverterForDb2(rsTxnDataWarehouse.getString("TRANSACTION_DATE"), false));
					pstmtIsertIntoAml.setString(10, dateConverterForDb2(rsTxnDataWarehouse.getString("txn_process_date"), false));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("CURRENCY_CODE"));
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("TXN_AMOUNT"));
					pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("TRAN_PARTICULAR"));
					pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString("LIMIT"));
					pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString("mcc_code"));
					pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString("TERMINAL_ID"));
					/*pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString("FROM_ACCOUNT"));
					pstmtIsertIntoAml.setString(18, rsTxnDataWarehouse.getString("TO_ACCOUNT"));
					pstmtIsertIntoAml.setString(19, rsTxnDataWarehouse.getString("TXN_AMOUNT"));
					pstmtIsertIntoAml.setString(20, rsTxnDataWarehouse.getString("RESPONSE_CODE"));
					pstmtIsertIntoAml.setString(21, rsTxnDataWarehouse.getString("CARD_STATUS_FLAG"));
					pstmtIsertIntoAml.setString(22, rsTxnDataWarehouse.getString("ATM_LOCATION"));
					pstmtIsertIntoAml.setString(23, rsTxnDataWarehouse.getString("TERM_CITY"));
					pstmtIsertIntoAml.setString(24, rsTxnDataWarehouse.getString("STATE"));
					pstmtIsertIntoAml.setString(25, rsTxnDataWarehouse.getString("COUNTRY"));
					pstmtIsertIntoAml.setString(26, rsTxnDataWarehouse.getString("OSEQ_NUM"));
					pstmtIsertIntoAml.setString(27, rsTxnDataWarehouse.getString("TRAN_STATUS"));
					pstmtIsertIntoAml.setString(28, rsTxnDataWarehouse.getString("CARD_PARTNERSHIP_ID"));
					pstmtIsertIntoAml.setString(29, rsTxnDataWarehouse.getString("CURRENCY_CODE"));
					pstmtIsertIntoAml.setString(30, rsTxnDataWarehouse.getString("ACT_TXN_CURR_AMT"));
					pstmtIsertIntoAml.setString(31, rsTxnDataWarehouse.getString("ACQ_CURR_CODE"));
					pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString("TRANSACTION_CODE"));*/
					
					/*pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("CREDIT_CARD_NO"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("SOL_ID"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("CBS_CUST_ID"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("CBS_ACCOUNT_NUMBER"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("TRAN_IDENTIFIER"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("TXN_CODE"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("TXN_CODE_DESC"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("TXN_TYPE"));
					//pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("TXN_DATE"));
					//pstmtIsertIntoAml.setString(10, dateConverterForDb2(rsTxnDataWarehouse.getString("TRANSACTION_DATE"), false));
					//pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("TXN_PROCESS_DATE"));
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("CURRENCY_CODE"));
					pstmtIsertIntoAml.setString(10, rsTxnDataWarehouse.getString("TXN_AMOUNT"));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("TRAN_PARTICULAR"));
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("LIMIT"));
					pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("mcc_code"));
					pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString("TERMINAL_ID"));*/
					
					pstmtIsertIntoAml.execute();

					if (commitCounter == 500000) {
						connectionAMLDb.commit();
						commitCounter = 0;
					}
				}
				connectionAMLDb.commit();
				String query = "";
				stInsertTxnMain = connectionDataWarehouse.createStatement();
				
				query="INSERT INTO AML_CREDIT_TRANSACTION SELECT * FROM AML_CREDIT_TRANSACTION_TEMP";
				
				rsTxnMain=stInsertTxnMain.executeQuery(query);
				
				connectionAMLDb.commit();
				
				compileObjects();
				
				
				/*stmt = connectionDataWarehouse.prepareCall("call ATM_TRANSACTION_UPLOAD()");
				stmt.execute();*/
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/*if (connectionAMLDb != null) {
					connectionAMLDb.close();
					connectionAMLDb = null;
				}*/
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
				if (rsTxnMain != null) {
					rsTxnMain.close();
					rsTxnMain = null;
				}
				if (stInsertTxnMain != null) {
					stInsertTxnMain.close();
					stInsertTxnMain = null;
				}
				/*if (connectionDataWarehouse != null) {
					connectionDataWarehouse.close();
					connectionDataWarehouse = null;
				}*/
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getDataExtractionDate()
	{
		Statement stmt = null;
		ResultSet rs = null;
		String fdate="";
		
		try
		{
			stmt = connectionAMLDb.createStatement();
			rs=stmt.executeQuery("select to_char(cbs_date,'yyyy-mm-dd') from aml_bizz_tab_cc where data_pull_flg='N'  ");
			while(rs.next()){
				fdate=rs.getString(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		return fdate;
	}
	
	public static String checkCreateThread() {
		String flg = "Y";
		Connection conn12=null;
        Statement stmt1 =null;
        ResultSet rs1 = null;
		try {
			conn12 = ConnectionFactory.makeConnectionAMLLive();
			stmt1 = conn12.createStatement();

			//rs1 = stmt1 
				//	.executeQuery("select ope_flg_rule from aml_bizz_tab_credit");---
			rs1 = stmt1 
					.executeQuery("select data_pull_flg from aml_bizz_tab_cc");
			while (rs1.next()) {
				flg = rs1.getString(1);
			}
			conn12.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt1 != null) {
					stmt1.close();
					stmt1 = null;
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				if (conn12 != null) {
					conn12.close();
					conn12 = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}
	
	public void compileObjects() {
         Connection conn11=null;
         Statement stmt1 =null;
		try {
			conn11= ConnectionFactory.makeConnectionAMLLive();
			stmt1 = conn11.createStatement();
			stmt1.executeUpdate("update aml_bizz_tab_credit set ope_flg_rule='Y'");
			conn11.commit();
			conn11.close();
			// stmt.execute("call compile_objects()");
			// System.out.println("Objects Compiled Successfully");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt1 != null) {
					stmt1.close();
					stmt1 = null;
				}
				
				if (conn11 != null) {
					conn11.close();
					conn11 = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		while(true){
			if(checkCreateThread().equals("N")){
				makeConnectionToDataWarehouse();
				try {
					makeConnectionAMLLive();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				long startTime = System.currentTimeMillis();
				System.out.println("Started execution : "+ (startTime / 1000) + " Seconds");
				
				//new CreditCardDataExtractionService().insertDataFromWarehouseToAml("2022-03-30");
				new CreditCardDataExtractionService().insertDataFromWarehouseToAml();
				
				long stopTime = System.currentTimeMillis();
		
				long elapsedTime = stopTime - startTime;
		
				System.out.println(
						"Done execution : Total Time elapsed - " + (elapsedTime / 1000) + " Seconds");
			}
			}	
	}

}

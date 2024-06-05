package com.idbi.intech.iaml.datapull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ATMDataExtractionService {

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
		CallableStatement stmt = null;
		ResultSet rsTxnDataWarehouse = null;
		PreparedStatement pstmtIsertIntoAml = null;

		int commitCounter = 0;
		
		try {
			
			String in_tran_date = getDataExtractionDate();
			
			if(!in_tran_date.equals(""))
			{
				// Insert script for AML DB
				pstmtIsertIntoAml = connectionAMLDb.prepareStatement(
						"insert into atm_transaction_master_d(HEADER,RECORD_TYPE,AUTH_TYPE,TERM_FIID,ATM_ID,PRO1,CARD_FIID,"
						+ "PAN_CARD_NO,MEMBER_NUM,TRANSACTION_DATE,TRAN_TIME,TXN_SNO,TERM_TYPE,ACQUIRING_INSTITUTION_ID,"
						+ "RECEIVING_INSTITUTION_ID,AC_TYPE,FROM_ACCOUNT,TO_ACCOUNT,TXN_AMOUNT,RESPONSE_CODE,CARD_STATUS_FLAG,"
						+ "ATM_LOCATION,TERM_CITY,STATE,COUNTRY,OSEQ_NUM,TRAN_STATUS,CARD_PARTNERSHIP_ID,"
						+ "CURRENCY_CODE,ACT_TXN_CURR_AMT,ACQ_CURR_CODE,TRANSACTION_CODE) "
						+ "values (?,?,?,?,?,?,?,?,?,to_date(?,'dd-mm-yy hh24:mi:ss'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				// Query for fetching txn data from EDW
				stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
				
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery("select * from pnb_ods.atmswtch_atmtran where transaction_date = '"+in_tran_date+"'");
				
				while (rsTxnDataWarehouse.next()) {
					commitCounter++;
					
					System.out.println(rsTxnDataWarehouse.getString("TXN_SNO"));
					
					pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("HEADER"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("RECORD_TYPE"));
					pstmtIsertIntoAml.setString(3, rsTxnDataWarehouse.getString("AUTH_TYPE"));
					pstmtIsertIntoAml.setString(4, rsTxnDataWarehouse.getString("TERM_FIID"));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("ATM_ID"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("PRO1"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("CARD_FIID"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("PAN_CARD_NO"));
					pstmtIsertIntoAml.setString(9, rsTxnDataWarehouse.getString("MEMBER_NUM"));
					pstmtIsertIntoAml.setString(10, dateConverterForDb2(rsTxnDataWarehouse.getString("TRANSACTION_DATE"), false));
					pstmtIsertIntoAml.setString(11, rsTxnDataWarehouse.getString("TRAN_TIME"));
					pstmtIsertIntoAml.setString(12, rsTxnDataWarehouse.getString("TXN_SNO"));
					pstmtIsertIntoAml.setString(13, rsTxnDataWarehouse.getString("TERM_TYPE"));
					pstmtIsertIntoAml.setString(14, rsTxnDataWarehouse.getString("ACQUIRING_INSTITUTION_ID"));
					pstmtIsertIntoAml.setString(15, rsTxnDataWarehouse.getString("RECEIVING_INSTITUTION_ID"));
					pstmtIsertIntoAml.setString(16, rsTxnDataWarehouse.getString("AC_TYPE"));
					pstmtIsertIntoAml.setString(17, rsTxnDataWarehouse.getString("FROM_ACCOUNT"));
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
					pstmtIsertIntoAml.setString(32, rsTxnDataWarehouse.getString("TRANSACTION_CODE"));
					
					pstmtIsertIntoAml.execute();

					if (commitCounter == 50000) {
						connectionAMLDb.commit();
						commitCounter = 0;
					}
				}
				connectionAMLDb.commit();
				
				stmt = connectionDataWarehouse.prepareCall("call ATM_TRANSACTION_UPLOAD()");
				stmt.execute();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connectionAMLDb != null) {
					connectionAMLDb.close();
					connectionAMLDb = null;
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
				if (connectionDataWarehouse != null) {
					connectionDataWarehouse.close();
					connectionDataWarehouse = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getDataExtractionDate()
	{
		Statement stmt = null;
		ResultSet rs = null;
		String date="";
		
		try
		{
			stmt = connectionAMLDb.createStatement();
			rs=stmt.executeQuery("select to_char(cbs_date,'yyyy-mm-dd') from aml_bizz_tab where OPE_FLG_ATM='N' ");
			while(rs.next()){
				date=rs.getString(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return date;
	}

	public static void main(String[] args) {
		makeConnectionToDataWarehouse();
		try {
			makeConnectionAMLLive();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long startTime = System.currentTimeMillis();
		System.out.println("Started execution : "+ (startTime / 1000) + " Seconds");
		
		//new ATMDataExtractionService().insertDataFromWarehouseToAml("2022-03-30");
		new ATMDataExtractionService().insertDataFromWarehouseToAml();
		
		long stopTime = System.currentTimeMillis();

		long elapsedTime = stopTime - startTime;

		System.out.println(
				"Done execution : Total Time elapsed - " + (elapsedTime / 1000) + " Seconds");
		
	}

}

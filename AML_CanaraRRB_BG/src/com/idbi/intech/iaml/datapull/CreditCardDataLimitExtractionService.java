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

public class CreditCardDataLimitExtractionService {

	private static Connection connectionDataWarehouse;
	private static Connection connectionAMLDb;
	//private final static String patternfordb2 = "yyyy-MM-dd HH:mm:ss.S";
	private final static String patternfordb2 = "yyyy-MM-dd";
	private final static String patternWithoutTS = "dd-MMM-yyyy";
	private final static String patternWithTS = "dd-MMM-yyyy HH:mm:ss";

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

			/*
			 * conn11 = ConnectionFactory.makeConnectionAMLLive(); stmt1 =
			 * conn11.createStatement();
			 * stmt1.executeUpdate("update AML_BIZZ_TAB_CC set data_pull_flg='P'");
			 * conn11.commit(); conn11.close();*/
			
			 conn2= ConnectionFactory.makeConnectionAMLLive(); 
			 stmt2 =conn2.createStatement();
			stmt2.executeUpdate("truncate table AML_CREDIT_DATA_LIMIT_TEMP");
			conn2.commit();
			conn2.close();
			 
				

			
			
			
			if(!txn_date.equals(""))
			{
				// Insert script for AML DB
				pstmtIsertIntoAml = connectionAMLDb.prepareStatement(
						"insert into AML_CREDIT_DATA_LIMIT_TEMP(CUST_ID,CARD_STATUS,EFF_FROM_DATE,EFF_TO_DATE,AML_UPLOAD_DATE,SANCT_LIM,"
						+ " AVAILABLE_LIMIT,"
						+ " RUNNING_BALANCE) "
						+ " values (?,?,?,?,?,?,?,?)");

				// Query for fetching txn data from EDW
				stFetchTxnWarehouse = connectionDataWarehouse.createStatement();
				
				rsTxnDataWarehouse = stFetchTxnWarehouse.executeQuery("select distinct da.ip_code AS CBS_CUST_ID ,ft.CARD_STATUS as CBS_CARD_STATUS ,ft.EFF_FM_TMS as EFF_FROM_DATE, "
						+ " ft.EFF_TO_TMS as EFF_TO_DATE , to_char(ft.PPN_TMS,'DD-MON-RR') as AML_UPLOAD_DATE , ft.SANCTIONED_LIMIT,ft.AVAILABLE_LIMIT,ft.RUNNING_BALANCE "
						+ " from MIS_TMP.DIM_ARNGMNT_CRN_ROW DA\r\n"
						+ "inner join mis.fact_ccard_os ft  on  DA.ip_id=FT.ip_id\r\n"
						+ "and ft.PPN_TMS between '"+txn_date+" 00:00:00.000' and '"+txn_date+" 23:59:59.999'"
						+ "and ft.CARD_STATUS='ACTIVE' ");
				
				while (rsTxnDataWarehouse.next()) {
					commitCounter++;
					//System.out.println("!");
					//System.out.println(rsTxnDataWarehouse.getString("TXN_SNO"));
					
					pstmtIsertIntoAml.setString(1, rsTxnDataWarehouse.getString("CBS_CUST_ID"));
					pstmtIsertIntoAml.setString(2, rsTxnDataWarehouse.getString("CBS_CARD_STATUS"));
					pstmtIsertIntoAml.setString(3, dateConverterForDb2(rsTxnDataWarehouse.getString("EFF_FROM_DATE"), false));
					pstmtIsertIntoAml.setString(4, dateConverterForDb2(rsTxnDataWarehouse.getString("EFF_TO_DATE"), false));
					pstmtIsertIntoAml.setString(5, rsTxnDataWarehouse.getString("AML_UPLOAD_DATE"));
					pstmtIsertIntoAml.setString(6, rsTxnDataWarehouse.getString("SANCTIONED_LIMIT"));
					pstmtIsertIntoAml.setString(7, rsTxnDataWarehouse.getString("AVAILABLE_LIMIT"));
					pstmtIsertIntoAml.setString(8, rsTxnDataWarehouse.getString("RUNNING_BALANCE"));
				//	pstmtIsertIntoAml.setString(9, dateConverterForDb2(rsTxnDataWarehouse.getString("txn_date"), false));
					//pstmtIsertIntoAml.setString(10, dateConverterForDb2(rsTxnDataWarehouse.getString("TRANSACTION_DATE"), false));
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
					System.out.println(commitCounter);
					if (commitCounter == 500000) {
						connectionAMLDb.commit();
						commitCounter = 0;
					}
				}
				connectionAMLDb.commit();
				String query = "";
				query="INSERT INTO AML_CREDIT_DATA_LIMIT SELECT * FROM AML_CREDIT_DATA_LIMIT_TEMP";
				conn11 = ConnectionFactory.makeConnectionAMLLive(); 
				stmt1 = conn11.createStatement();
			stmt1.executeQuery(query);
				conn11.commit(); 
			 conn11.close();
				//stInsertTxnMain = connectionDataWarehouse.createStatement();
				
				
				
				//rsTxnMain=stInsertTxnMain.executeQuery(query);
				
				connectionAMLDb.commit();
				
				//compileObjects();
				
				
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
				if (stmt1 != null) {
					stmt1.close();
					stmt1 = null;
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
			rs=stmt.executeQuery("select to_char(cbs_date,'yyyy-mm-dd') from aml_bizz_tab_cc where OPE_FLG_RULE_CC='N'  ");
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
	
				try {
					makeConnectionAMLLive();
					makeConnectionToDataWarehouse();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				long startTime = System.currentTimeMillis();
				System.out.println("Started execution : "+ (startTime / 1000) + " Seconds");
				
				//new CreditCardDataExtractionService().insertDataFromWarehouseToAml("2022-03-30");
				new CreditCardDataLimitExtractionService().insertDataFromWarehouseToAml();
				
				long stopTime = System.currentTimeMillis();
		
				long elapsedTime = stopTime - startTime;
		
				System.out.println(
						"Done execution : Total Time elapsed - " + (elapsedTime / 1000) + " Seconds");
			
		
	}

}

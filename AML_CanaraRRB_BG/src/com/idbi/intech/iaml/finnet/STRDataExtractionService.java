package com.idbi.intech.iaml.finnet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class STRDataExtractionService {

	private static Connection connection;

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

	public static String executeProc(String procedure, String requestId) {

		Statement stmt = null;
		String flg = "N";

		try {
			stmt = connection.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("call " + procedure + "('" + requestId + "')");

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println(
					"Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			System.out.flush();
			flg = "Y";
		} catch (SQLException ex) {
			flg = "N";
			ex.printStackTrace();
		} catch (Exception ex) {
			flg = "N";
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return flg;
	}
	
	public static boolean checkTransactionStatus(String strNo) {
		Connection conn = null;
		Statement stmt = null ;
		ResultSet rs = null;
		
		boolean success = false;

		 
		try {
			conn = ConnectionFactory.makeConnectionAMLLive();
			stmt = conn.createStatement();	
				String conQuery = "select CASE WHEN count(1) > 0 THEN 1 "
						+ " WHEN count(1) = 0 THEN 0 END as result from AML_TRANSACTION_MASTER_F where "
						+ "str_no ='"+strNo+"'";
				rs = stmt.executeQuery(conQuery);
				int result = 0;
				while (rs.next()) {
					result = rs.getInt("result");
				}
				
				if (result == 1) {
					success = true;
				} else {
					success = false;
				

				}
	
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
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
		return success;
	}
	

	public static void main(String[] args) throws IOException, SQLException {

		Properties amlProp = new Properties();
		String dir = System.getProperty("user.dir");
		// System.out.println("Current Directory::"+dir);
		InputStream is = new FileInputStream(dir + "/aml-config.properties");
		amlProp.load(is);
		is.close();
		String procedure = amlProp.getProperty("STR_TXN_PL");
		String strFlg = amlProp.getProperty("CHECK_STR_FLG");
		String status="N";

		try {
			makeConnection();

			List<StrReqDataBean> strList = ConstantFunctions.getStrRequestData();

			for (StrReqDataBean reqObj : strList) {
				String strNo = reqObj.getStrNo();

				if(strFlg.equals("P")) {
				 status = executeProc(procedure, strNo);
				}
				else {
					 status = StrEdwDataExtraction.genStrTransaction(strNo);
				}
				//String status = "Y";

				if (status.equals("Y") && Boolean.TRUE.equals(checkTransactionStatus(strNo))) {

					new Thread(new KC1ThreadSTR(strNo)).start();
					new Thread(new KC2ThreadSTR(strNo)).start();
					new Thread(new ADThreadSTR(strNo)).start();
					new Thread(new APRThreadSTR(strNo)).start();
					new Thread(new GS1ThreadSTR(strNo)).start();
					
					if(reqObj.getTxnChannel() != null && reqObj.getTxnChannel() != "")
					{
						new Thread(new TC1ThreadSTR(strNo)).start();
						new Thread(new TC2ThreadSTR(strNo)).start();
						new Thread(new TS1ThreadSTR(strNo)).start();
						new Thread(new TS2ThreadSTR(strNo)).start();
						new Thread(new TS3ThreadSTR(strNo)).start();
						new Thread(new GT1ThreadSTR(strNo)).start();
					}
					
					while(true)
					{
						String flg = ConstantFunctions.checkStrStatus(strNo);
						
						if(flg.equals("Y"))
						{   
						
							if(procedure.equalsIgnoreCase("GEN_STR_TRANSACTION_FINNET")) {
								
								System.out.println("procedure"+procedure);
								//ConstantFunctions.updateStrReq(strNo);
							}
							
							break;
						}
					}
					
				}
				else {
					
					
					ConstantFunctions.updateMainStatus(strNo, "D");
					
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

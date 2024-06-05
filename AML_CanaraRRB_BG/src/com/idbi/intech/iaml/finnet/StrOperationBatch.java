package com.idbi.intech.iaml.finnet;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.newCTR.GenCtrReport;



public class StrOperationBatch {
	
	private static Connection connection;
	private static Connection connectionDataWarehouse;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	private static void makeConnectionToDataWarehouse() {

		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			connectionDataWarehouse = ConnectionFactory.makeConnectionEDWLive();
		} catch (SQLException | ClassNotFoundException e) {

			e.printStackTrace();
			System.err.println("Exception e:" + e);
		}
	}
	
	public static String getCredentials() {
		Console cons = null;
		String userId = null;
		char[] pwd = null;
		String userDetails = "";
		try {
			cons = System.console();
			if (cons != null) {
				userId = cons.readLine("User Id: ");
				pwd = cons.readPassword("Password: ");
				userDetails = userId + "~" + String.valueOf(pwd);
				cons.flush();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return userDetails;
	}
	
	public static boolean executeProc(String procedure) {

		Statement stmt = null;
		boolean flg = false;

		try {
			stmt = connection.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("call " + procedure + "()");

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println(
					"Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			System.out.flush();
			flg = true;
		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		} catch (Exception ex) {
			flg = false;
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
	
	public static boolean executeStrValidateProc(String procedure) {

		Statement stmt = null;
		boolean flg = false;

		try {
			stmt = connection.createStatement();

			long startTime = System.currentTimeMillis();

			System.out.println("Started execution for : " + procedure);

			stmt.execute("call " + procedure + "()");

			long stopTime = System.currentTimeMillis();

			long elapsedTime = stopTime - startTime;

			System.out.println(
					"Done execution for : " + procedure + " Total Time elapsed : " + (elapsedTime / 1000) + " Seconds");
			System.out.flush();
			flg = true;
		} catch (SQLException ex) {
			flg = false;
			ex.printStackTrace();
		} catch (Exception ex) {
			flg = false;
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

	


	
	public static void main(String args[]) throws IOException, SQLException {
		String option = "";
		Properties amlProp = new Properties();
		String dir = System.getProperty("user.dir");
		STRDataExtractionService strDataObj = new STRDataExtractionService();
		// System.out.println("Current Directory::"+dir);
		InputStream is = new FileInputStream(dir + "/aml-config.properties");
		amlProp.load(is);
		is.close();
		String StrDataSync = amlProp.getProperty("FINNET_AML_SYNC");
		String StrDataValidation = amlProp.getProperty("STR_VALIDATAION_BATCH");
		String edwQuery = amlProp.getProperty("EDW_CHK_QUERY");
		String status="N";
		Boolean flg = true;
		String gendata = "";
		System.out.print("********Str Report Generator Tool Powered by i-AML**********\n");
		System.out.print("-------------------------------------------------------------------\n");
		try {
		
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Select any one choice from below option...\n");
			System.out.println("1.SYNC DATA(AML TO FINNET SYSTEM)");
			System.out.println("2.DATA GENERATION");
			System.out.println("3.DATA VALIDATION");
			System.out.println("4.EXIT");
			System.out.print("Enter the option: ");
			option = br.readLine();
			makeConnection();
		
			if (option.equalsIgnoreCase("1")) {
				flg = executeProc(StrDataSync);
				if (Boolean.TRUE.equals(flg)) {
					System.out.print("Data SuccessFully Moved From AML TO FINNET SYSTEM");
				} else {
					System.out.print("Data Not Moved From AML TO FINNET SYSTEM");
				}
				
			}
			else if (option.equalsIgnoreCase("2")) {
				System.out.print("To generate the report enter(Y/N): ");
                gendata = br.readLine();
				if (gendata.equalsIgnoreCase("Y")) {
	
					flg=isConnected(edwQuery) ;
					if (Boolean.TRUE.equals(flg)) {
						System.out.println("EDW connection established successfully...");
			             STRDataExtractionService.main(args);
					}
					else {
						System.out.println("EDW connection not established successfully...");
					}
					
				}
				
			}
			else if (option.equalsIgnoreCase("3")) {
				flg = executeStrValidateProc(StrDataValidation);
				if (Boolean.TRUE.equals(flg)) {
					System.out.print("Data SuccessFully Validated");
				} else {
					System.out.print("Data Valdation Failed");
				}
			}
			
			
		}
		 catch (Exception ex) {
				ex.printStackTrace();
			}
	}
	
	public static boolean isConnected( String edwQuery) {

		boolean flg = true;
		makeConnectionToDataWarehouse();
		String QUERY_IS_CONNECTED = "select * from  "+edwQuery+" ";
            try (Statement statement = connectionDataWarehouse.createStatement();
                 ResultSet resultSet = statement.executeQuery(QUERY_IS_CONNECTED)) {
                if (resultSet == null) {
                	flg = false;
                }
            } catch (Exception e) {
               flg = false;
            }
        

        return flg;
    }

}

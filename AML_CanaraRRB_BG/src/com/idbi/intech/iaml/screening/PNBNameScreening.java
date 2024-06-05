package com.idbi.intech.iaml.screening;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;

import com.idbi.intech.iaml.factory.ConnectionFactory;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class PNBNameScreening implements Runnable {

	private static Properties bundle = new Properties();
	//private static Connection connection = null;
	DecimalFormat df = new DecimalFormat("###.##");
	static PNBNameScreening process_run = new PNBNameScreening();
	private static String threshold = null;
	private static ArrayList<String> entityDetailsIndiaList = null;
	double result = 0.0;

	public void customerScreenProcess() {
		Connection connection=null;
		Statement stmt = null;
		Statement stmtId = null;
		ResultSet rsCust = null, rsId = null;
		ResultSet rsUpdate = null;
		String id = "";
		String custRisk = "";
		String custName = "";
		String correlationId = "";
		String alertFlg = "N";
		String zone = "";
		String sol = "";
		String country = "";
		PreparedStatement pstmt_scan_tab = null, stmtUpdate = null;
		try {
			connection= ConnectionFactory.makeConnectionAMLLive();
			entityDetailsIndiaList = getEntityDetails();

			threshold = getFuzzyVal();
			System.out.println("Processing Customer Data For Screening...");

			stmt = connection.createStatement();
			String customerSQL = "select cust_id,cust_name,RISK_RATING cust_risk_code,GET_SOL_ZONE(primary_sol_id) zone,primary_sol_id sol_id,(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=a.country_code) country "
					+ " from aml_cust_master a where scan_flg='N' and rownum<=10";

			rsCust = stmt.executeQuery(customerSQL);

			while (rsCust.next()) {
				
				alertFlg = "N";
				
				try {
					correlationId = "FULLPNB";
					id = rsCust.getString("cust_id");
					custRisk = rsCust.getString("cust_risk_code");
					custName = rsCust.getString("cust_name");
					zone = rsCust.getString("zone");
					sol = rsCust.getString("sol_id");
					country = rsCust.getString("country");
					
					HashMap<String, String> idMatchSet = scanName(custName,country);
					if (idMatchSet.size() > 0) {
						//System.out.println("result:"+result);
						if (generateAlert(id, custRisk, "F", threshold, "A", idMatchSet, correlationId, zone, sol)) {
							alertFlg = "Y";
						}
					}
					stmtUpdate = connection.prepareStatement(
							"update aml_cust_master a set a.scan_flg='C'  where cust_Id ='" + id + "'");
					stmtUpdate.executeUpdate();
					pstmt_scan_tab = connection
							.prepareStatement("insert into cust_scan_tab values (?,sysdate,'00',?,?,?)");

					pstmt_scan_tab.setString(1, id);
					pstmt_scan_tab.setString(2, alertFlg);
					pstmt_scan_tab.setString(3, "F");
					pstmt_scan_tab.setString(4, correlationId);
					pstmt_scan_tab.execute();

					
				} catch (SQLException ex) {
					ex.printStackTrace();
				} finally {
					try {
						if (pstmt_scan_tab != null) {
							pstmt_scan_tab.close();
							pstmt_scan_tab = null;
						}

						if (stmtUpdate != null) {
							stmtUpdate.close();
							stmtUpdate = null;
						}
						if (rsUpdate != null) {
							rsUpdate.close();
							rsUpdate = null;
						}
						if (stmtId != null) {
							stmtId.close();
							stmtId = null;
						}
						if (rsId != null) {
							rsId.close();
							rsId = null;
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
			connection.commit();
			System.out.println("Individual Customer Data Screening Completed Sucessfully...");
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt_scan_tab != null) {
					pstmt_scan_tab.close();
					pstmt_scan_tab = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (stmtUpdate != null) {
					stmtUpdate.close();
					stmtUpdate = null;
				}
				if (rsCust != null) {
					rsCust.close();
					rsCust = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (rsUpdate != null) {
					rsUpdate.close();
					rsUpdate = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

	}

	public Boolean generateAlert(String custId, String custRisk, String scanMode, String fuzzyVal, String matchFlg,
			HashMap<String, String> matchSet, String correlationId, String zone, String solId) {
		Boolean flg = false;
		Statement stmt = null, stmtSeq = null;
		ResultSet rs = null, rsSeq = null;
		String alert_id = "", record_id = "";
		PreparedStatement pstmt_sdn_alert = null;
		PreparedStatement pstmtSubAlert = null;
		Connection connection = null;
		try {
			// Insert into Alert master table
			connection= ConnectionFactory.makeConnectionAMLLive();
			pstmt_sdn_alert = connection.prepareStatement(
					"insert into ns_alert_master(alert_id,cust_id,alert_status,generated_time,user_id,last_userid,"
							+ "last_changetime,scan_type,fuzzy_val,cust_risk,match_flg,correlation_id,zone,branch_id,curr_role_id,"
							+ "last_role_id,lock_status,susp_flg,lock_time,unlock_time,lock_id) values"
							+ "(?,?,?,sysdate,?,?,sysdate,?,?,?,?,?,?,?,'LEVEL1'" + ",'NA','N','X',null,null,'NA')");
			pstmtSubAlert = connection.prepareStatement("insert into ns_alert_agg values (?,?,?,?,?,'','',?)");
			stmtSeq = connection.createStatement();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select 'NSA'||to_char(sysdate,'ddMMyyhhmmss') ||ALERTID_SEQ.nextval from dual");
			while (rs.next()) {
				alert_id = rs.getString(1);
			}
			// Insert into master table
			pstmt_sdn_alert.setString(1, alert_id);
			pstmt_sdn_alert.setString(2, custId);
			pstmt_sdn_alert.setString(3, "O");
			pstmt_sdn_alert.setString(4, "AMLSYSTEM");
			pstmt_sdn_alert.setString(5, "AMLSYSTEM");
			pstmt_sdn_alert.setString(6, scanMode);
			pstmt_sdn_alert.setString(7, fuzzyVal);
			pstmt_sdn_alert.setString(8, custRisk);
			pstmt_sdn_alert.setString(9, matchFlg);
			pstmt_sdn_alert.setString(10, correlationId);
			pstmt_sdn_alert.setString(11, zone);
			pstmt_sdn_alert.setString(12, solId);
			pstmt_sdn_alert.executeUpdate();
			// Insert into aggregate details
			for (Map.Entry<String, String> entry : matchSet.entrySet()) {
				//String key =entry.getValue();
				
				
				// " - "
				// + entry.getValue());
				rsSeq = stmtSeq
						.executeQuery("select 'NSR'||to_char(sysdate,'ddMMyyhhmmss') ||RECORDID_SEQ.nextval from dual");
				while (rsSeq.next()) {
					record_id = rsSeq.getString(1);
				}
				String per[]=entry.getValue().split("~");
				System.out.println("hey:"+per.toString());
				pstmtSubAlert.setString(1, record_id);
				pstmtSubAlert.setString(2, alert_id);
				pstmtSubAlert.setString(3, entry.getKey());
				pstmtSubAlert.setString(4, "O");
				pstmtSubAlert.setString(5, entry.getValue());
				pstmtSubAlert.setString(6, per[2]);
				pstmtSubAlert.executeUpdate();
			}
			connection.commit();
			flg = true;
			connection.close();
		} // catch (JsonProcessingException e) {
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}

				if (stmtSeq != null) {
					stmtSeq.close();
					stmtSeq = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rsSeq != null) {
					rsSeq.close();
					rsSeq = null;
				}
				if (pstmt_sdn_alert != null) {
					pstmt_sdn_alert.close();
					pstmt_sdn_alert = null;
				}
				if (pstmtSubAlert != null) {
					pstmtSubAlert.close();
					pstmtSubAlert = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	public String getFuzzyVal() {
		String fuzzyVal = "";
		Statement stmt = null;
		ResultSet rs = null;
		Connection connection=null;
		try {
			connection= ConnectionFactory.makeConnectionAMLLive();
			String query = "select VALUE from p_aml_general where name='SDN_FUZZY_MATCH_PERCENTAGE'";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				fuzzyVal = rs.getString("VALUE");
			}
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return fuzzyVal;
	}

	public HashMap<String, String> scanName(String custName,String country) {
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		//double result = 0.0;
		Connection connection=null;
		try {
			connection= ConnectionFactory.makeConnectionAMLLive();
			stmt = connection.createStatement();
			for (String entitiesDetailsList : entityDetailsIndiaList) {
				result = 0.0;
				String entities[] = entitiesDetailsList.split("~");
				result = FuzzySearch.tokenSortRatio(entities[0].toUpperCase().replace(",", "").toUpperCase().trim(),
						custName.toUpperCase());
				String entityCountry = entities[3].toString().toUpperCase();
				if (result > Integer.parseInt(threshold) && entityCountry.equalsIgnoreCase(country)) {
					matchSet.put(entities[1], custName+"~"+entityCountry+"~"+result);
				}
			}
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return matchSet;
	}

	public static void startService(String serviceName) throws IOException, InterruptedException {
		String executeCmd = "cmd /c net start \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Successfully Started");
		}
	}

	public static void stopService(String serviceName) throws IOException, InterruptedException {
		String executeCmd = "cmd /c net stop \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Successfully Stopped");
		}
	}

	public static void windowsService(String args[]) throws Exception {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}

		if ("start".equals(cmd)) {
			process_run.start();
		} else {
			process_run.stop();
		}
	}

	public void start() {
		try {
			// makeConnection();
			Thread t = new Thread(new PNBNameScreening());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	/*public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}*/

	public ArrayList<String> getEntityDetails() {
		Statement stmt = null;
		ResultSet rs = null;
		Connection connection=null;
		ArrayList<String> entityList = new ArrayList<String>();
		System.out.println("Fetching Entities Details for Country India...");
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			stmt = connection.createStatement();
			String checkNameSQL = "select name,ent_id,entrycategory,country from "
					+ "(select FIRST_NAME||' '||SECOND_NAME||' '||THIRD_NAME name,data_id ent_id,UN_LIST_TYPE entrycategory,get_wl_country(a.data_id,'INDIVIDUAL') country from aml_wl_ind a union all "
					+ "select FIRST_NAME name,data_id ent_id,UN_LIST_TYPE entrycategory,get_wl_country(b.data_id,'ENTITY') country from aml_wl_entity b union all "
					+ "select LAST_NAME name,data_id ent_id,SDN_TYPE entrycategory,get_wl_country(c.data_id,'SDN') country from aml_wl_sdn c) where name is not null ";
			System.out.println("checkNameSQL : "+checkNameSQL);
			
			rs = stmt.executeQuery(checkNameSQL);
			while (rs.next()) {
				entityList
						.add(rs.getString("name") + "~" + rs.getString("ent_id") + "~" + rs.getString("entrycategory")+ "~" + rs.getString("country"));
			}
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return entityList;

	}
	
	public boolean checkDbStatus() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean flg = false;
		
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			String checkSQL = "select * from dual";
			stmt = connection.prepareStatement(checkSQL);
			rs = stmt.executeQuery();
			
			while(rs.next())
			{
				flg = true;
				rs.getString(1);
			}
			
			stmt.close();
			connection.close();
			
		} catch (Exception ex) {
			flg = false;
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::" + dir);
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				bundle.load(is);
				is.close();
				//makeConnection();
                 boolean dbStatus = checkDbStatus();
				
				if(dbStatus)
				{
					customerScreenProcess();
				}
				else
				{
					Thread.sleep(1000 * 60 * 5);
				}
				
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// makeConnection();
		Thread t = new Thread(new PNBNameScreening());
		t.start();
	}
}

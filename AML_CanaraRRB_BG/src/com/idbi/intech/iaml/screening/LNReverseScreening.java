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

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class LNReverseScreening implements Runnable {
	private static Properties bundle = new Properties();
	private static Connection connection = null;
	DecimalFormat df = new DecimalFormat("###.##");
	static LNReverseScreening process_run = new LNReverseScreening();
	private static String threshold = null;

	public static void startService(String serviceName) throws IOException,
			InterruptedException {
		String executeCmd = "cmd /c net start \"" + serviceName + "\"";

		Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);

		int processComplete = runtimeProcess.waitFor();

		if (processComplete == 1) {
			System.out.println("Service Failed");
		} else if (processComplete == 0) {
			System.out.println("Service Successfully Started");
		}
	}

	public static void stopService(String serviceName) throws IOException,
			InterruptedException {
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
			Thread t = new Thread(new LNReverseScreening());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public HashMap<String, String> reverseIdScan(ArrayList<String> paramIdList) {
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null, stmtPid = null;
		ResultSet rs = null, rsPid = null;
		try {
			stmt = connection.createStatement();
			for (String matchId : paramIdList) {
				String checkSQL = "select cust_id from aml_cust_master where CUST_STATUS='A' and ((cust_pan_no ='"
						+ matchId
						+ "' and length(cust_pan_no)>7)  or (cust_passport_no='"
						+ matchId
						+ "' and length(cust_passport_no) > 6 )or (aadhaar_no='"
						+ matchId + "' and length(aadhaar_no)>10))";
				// + " and CUST_COMMU_CNTRY_CODE = '" + cntryCode + "'";

				rs = stmt.executeQuery(checkSQL);
				while (rs.next()) {
					matchSet.put(rs.getString("cust_id"), matchId);
				}
			}
		} catch (Exception ex) {
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
				if (stmtPid != null) {
					stmtPid.close();
					stmtPid = null;
				}
				if (rsPid != null) {
					rsPid.close();
					rsPid = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return matchSet;
	}

	public Boolean generateReverseAlert(String entId, String scanMode,
			String fuzzyVal, String matchFlg, HashMap<String, String> matchSet,
			String correlationId) {
		Boolean flg = false;
		Statement stmt = null, stmtSeq = null;
		ResultSet rs = null, rsSeq = null;
		String alert_id = "", record_id = "";
		PreparedStatement pstmt_sdn_alert = null;
		PreparedStatement pstmtSubAlert = null;
		PreparedStatement pstmtRuleAlert = null;

		try {
			pstmt_sdn_alert = connection
					.prepareStatement("insert into ns_alert_master values"
							+ "(?,?,?,sysdate,?,?,sysdate,?,?,?,?,?)");
			pstmtSubAlert = connection
					.prepareStatement("insert into ns_alert_agg values (?,?,?,?,?,'','')");
			stmtSeq = connection.createStatement();
			stmt = connection.createStatement();

			// Insert into Alert master table
			for (Map.Entry<String, String> entry : matchSet.entrySet()) {
				rs = stmt
						.executeQuery("select 'NSA'||to_char(sysdate,'ddMMyyhhmmss') ||ALERTID_SEQ.nextval from dual");
				while (rs.next()) {
					alert_id = rs.getString(1);
				}
				// Insert into master table
				pstmt_sdn_alert.setString(1, alert_id);
				pstmt_sdn_alert.setString(2, entry.getKey());
				pstmt_sdn_alert.setString(3, "O");
				pstmt_sdn_alert.setString(4, "AMLSYSTEM");
				pstmt_sdn_alert.setString(5, "AMLSYSTEM");
				pstmt_sdn_alert.setString(6, scanMode);
				pstmt_sdn_alert.setString(7, fuzzyVal);
				pstmt_sdn_alert.setString(8, getCustRisk(entry.getKey()));
				pstmt_sdn_alert.setString(9, matchFlg);
				pstmt_sdn_alert.setString(10, correlationId);
				pstmt_sdn_alert.executeUpdate();
				// Insert into Rule ticket table
				pstmtRuleAlert = connection
						.prepareStatement("Insert into AML_RULE_TICKET (ticket_id, cust_id, branch_id, category, ticket_status, generated_time,"
								+ " last_change_date, last_user_id, user_id, rule_start_date,rule_end_date, vertical, rule_id, start_amt, end_amt, no_of_txn, "
								+ "no_of_days, no_of_acct, rule_desc, tran_amt, ticket_type)"
								+ "(select alert_id,cust_id,(select primary_sol_id from aml_cust_master where cust_id=a.cust_id),'NS','W', generated_time,LAST_CHANGETIME,last_userid,"
								+ "user_id,generated_time,generated_time,'NO DATA','NS001',0,0,0,1,0,'Customer name screening',0,'N' from ns_alert_master a where alert_id ='"
								+ alert_id + "')");
				pstmtRuleAlert.executeUpdate();
				// Insert into aggregate details
				rsSeq = stmtSeq
						.executeQuery("select 'NSR'||to_char(sysdate,'ddMMyyhhmmss') ||RECORDID_SEQ.nextval from dual");
				while (rsSeq.next()) {
					record_id = rsSeq.getString(1);
				}
				pstmtSubAlert.setString(1, record_id);
				pstmtSubAlert.setString(2, alert_id);
				pstmtSubAlert.setString(3, entId);
				pstmtSubAlert.setString(4, "O");
				pstmtSubAlert.setString(5, entry.getValue());
				pstmtSubAlert.executeUpdate();
			}
			connection.commit();
			flg = true;
		} // catch (JsonProcessingException e) {
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
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
				if (pstmtRuleAlert != null) {
					pstmtRuleAlert.close();
					pstmtRuleAlert = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	public String getCustRisk(String custId) {
		String custRisk = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "select cust_risk_rating from aml_cust_master where cust_id='"
					+ custId + "'";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				custRisk = rs.getString("cust_risk_rating");
			}
		} catch (Exception ex) {
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
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return custRisk;
	}

	public String getFuzzyVal() {
		String fuzzyVal = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "select VALUE from p_aml_general where name='SDN_FUZZY_MATCH_PERCENTAGE'";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				fuzzyVal = rs.getString("VALUE");
			}
		} catch (Exception ex) {
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
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return fuzzyVal;
	}

	public String checkCreateThread() {
		String flg = "N";
		Statement stmtCheck = null;
		ResultSet rs = null;
		try {
			stmtCheck = connection.createStatement();
			rs = stmtCheck.executeQuery("select ope_flg_sdn from aml_bizz_tab");
			while (rs.next()) {
				flg = rs.getString(1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	public void customerScreenProcess() {
		Statement stmt = null;
		ResultSet rsLnData = null;
		String id = "";
		String passportId = "", nationalId = "", otherId = "", indFlg = "", cntryCode = "";
		String correlationId = "";
		PreparedStatement pstmt_scan_tab = null, pstmtUpdate = null;
		try {

			threshold = getFuzzyVal();

			System.out
					.println("Processing Reverse Customer Data For Screening...");

			stmt = connection.createStatement();
			// String lnData
			String lnDataSQL = "select ent_id,  nvl(passportid,'-') passportid,nvl(nationalid,'-') nationalid,nvl(otherid,'-') otherid  from aml_ln_entities "
					+ " where scan_flg='N' and ( passportid||nationalid||otherid) is not null and rownum < 5000";

			rsLnData = stmt.executeQuery(lnDataSQL);

			while (rsLnData.next()) {
				try {
					ArrayList<String> paramIdList = new ArrayList<String>();
					id = rsLnData.getString("ent_id");
					// passport = rsCust.getString("passport");
					passportId = rsLnData.getString("passportid");
					nationalId = rsLnData.getString("nationalid");
					otherId = rsLnData.getString("otherid");
					// indFlg = rsLnData.getString("ind_flg");
					// cntryCode = rsLnData.getString("country");

					if (!passportId.equals("-")) {
						paramIdList.add(passportId);
					}
					if (!nationalId.equals("-")) {
						paramIdList.add(nationalId);
					}
					if (!otherId.equals("-")) {
						paramIdList.add(otherId);
					}
					correlationId = "RVRSBOBK";
					String alertFlg = "N";
					if (paramIdList.size() > 0) {
						HashMap<String, String> idMatchSet = reverseIdScan(paramIdList);
						if (idMatchSet.size() > 0) {
							if (generateReverseAlert(id, "R", threshold, "A",
									idMatchSet, correlationId)) {
								alertFlg = "Y";
							}
						}
					}

					pstmtUpdate = connection
							.prepareStatement("update aml_ln_entities a set a.scan_flg='C'  where ent_Id ='"
									+ id + "'");
					pstmtUpdate.executeUpdate();
					pstmt_scan_tab = connection
							.prepareStatement("insert into cust_scan_tab values (?,sysdate,'00',?,?,?)");
					pstmt_scan_tab.setString(1, id);
					pstmt_scan_tab.setString(2, alertFlg);
					pstmt_scan_tab.setString(3, "R");
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

						if (pstmtUpdate != null) {
							pstmtUpdate.close();
							pstmtUpdate = null;
						}

					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
			connection.commit();
			System.out
					.println("Incremental Customer Data Screening Completed Sucessfully...");
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
				if (pstmtUpdate != null) {
					pstmtUpdate.close();
					pstmtUpdate = null;
				}

				if (rsLnData != null) {
					rsLnData.close();
					rsLnData = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	public void run() {
		while (true) {
			try {
				String dir = System.getProperty("user.dir");
				// System.out.println("Current Directory::" + dir);
				InputStream is = new FileInputStream(dir
						+ "/aml-config.properties");
				bundle.load(is);
				is.close();
				makeConnection();
				customerScreenProcess();
				Thread.sleep(1000 * 300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// makeConnection();
		Thread t = new Thread(new LNReverseScreening());
		t.start();
	}
}

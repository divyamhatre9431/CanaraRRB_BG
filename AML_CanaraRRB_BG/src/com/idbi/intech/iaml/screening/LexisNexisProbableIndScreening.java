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

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class LexisNexisProbableIndScreening implements Runnable {
	private static Properties bundle = new Properties();
	private static Connection connection = null;
	DecimalFormat df = new DecimalFormat("###.##");
	static LexisNexisProbableIndScreening process_run = new LexisNexisProbableIndScreening();
	private static String threshold = null;
	private static ArrayList<String> entityDetailsIndiaList = null;

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
			//makeConnection();
			Thread t = new Thread(new LexisNexisProbableIndScreening());
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

	public Boolean generateAlert(String custId, String custRisk,
			String scanMode, String fuzzyVal, String matchFlg,
			HashMap<String, String> matchSet, String correlationId) {
		Boolean flg = false;
		Statement stmt = null, stmtSeq = null;
		ResultSet rs = null, rsSeq = null;
		String alert_id = "", record_id = "";
		PreparedStatement pstmt_sdn_alert = null;
		PreparedStatement pstmtSubAlert = null;
		try {
			// Insert into Alert master table
			pstmt_sdn_alert = connection
					.prepareStatement("insert into ns_alert_master values"
							+ "(?,?,?,sysdate,?,?,sysdate,?,?,?,?,?)");
			pstmtSubAlert = connection
					.prepareStatement("insert into ns_alert_agg values (?,?,?,?,?,'','')");
			stmtSeq = connection.createStatement();
			stmt = connection.createStatement();
			rs = stmt
					.executeQuery("select 'NSA'||to_char(sysdate,'ddMMyyhhmmss') ||ALERTID_SEQ.nextval from dual");
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
			pstmt_sdn_alert.executeUpdate();
			// Insert into aggregate details
			for (Map.Entry<String, String> entry : matchSet.entrySet()) {
				// System.out.println("here entryset : " + entry.getKey() +
				// " - "
				// + entry.getValue());
				rsSeq = stmtSeq
						.executeQuery("select 'NSR'||to_char(sysdate,'ddMMyyhhmmss') ||RECORDID_SEQ.nextval from dual");
				while (rsSeq.next()) {
					record_id = rsSeq.getString(1);
				}
				pstmtSubAlert.setString(1, record_id);
				pstmtSubAlert.setString(2, alert_id);
				pstmtSubAlert.setString(3, entry.getKey());
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

	public ArrayList<String> getEntityDetailsIndiaCntry() {
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> entityList = new ArrayList<String>();
		System.out.println("Fetching Entities Details for Country India...");
		try {
			stmt = connection.createStatement();
			String query = "select ent_id,name,nvl(aka,'NA')aka,nvl(dob,'NA') dob,country,nvl(gender,'X') gender from aml_ln_entities where country ='INDIA'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				entityList
						.add(rs.getString("name") + "~" + rs.getString("aka")
								+ "~" + rs.getString("dob") + "~"
								+ rs.getString("country") + "~"
								+ rs.getString("gender") + "~"
								+ rs.getString("ent_id"));
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
		return entityList;

	}

	public HashMap<String, String> scanOtherParam(ArrayList<String> paramList) {
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		String year = "", month = "", day = "", gender = "";
		String cCountry = paramList.get(1) == null ? "NA" : paramList.get(1);
		String pCountry = paramList.get(6) == null ? "NA" : paramList.get(6);
		double result = 0.0;
		double result1 = 0.0;
		try {
			if (!(paramList.get(0).equals("-") && paramList.get(0) != null)) {
				if (!cCountry.equalsIgnoreCase("INDIA")) {
					stmt = connection.createStatement();
					String query = "select ent_id,name,nvl(aka,'NA')aka,nvl(dob,'NA'),country,nvl(gender,'X') gender from aml_ln_entities where (country ='"
							+ cCountry + "' or country ='" + pCountry + "') ";
					// System.out.println(query);
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						result = FuzzySearch.tokenSortRatio(
								rs.getString("name").toUpperCase()
										.replace(",", "").toUpperCase().trim(),
								paramList.get(2).toUpperCase());
						if (!rs.getString("aka").equals("NA"))
							result1 = FuzzySearch.tokenSortRatio(
									rs.getString("aka").replace(",", "")
											.toUpperCase().trim(), paramList
											.get(2).toUpperCase());

						gender = rs.getString("gender");
						if (result > Integer.parseInt(threshold)
								|| result1 > Integer.parseInt(threshold)) {
							// System.out.println(result + "thr-" + threshold);
							// System.out.println(gender + "~" +
							// paramList.get(7));
							if (gender.equals(paramList.get(7))) {
								matchSet.put(
										rs.getString("ent_id"),
										paramList.get(2) + "~"
												+ paramList.get(0) + "~"
												+ paramList.get(1));
							} else {
								if (!rs.getString("dob").equalsIgnoreCase("NA")) {
									if (rs.getString("dob").contains(",")) {
										year = rs.getString("dob").split(",")[1];
										month = rs.getString("dob").split(",")[0]
												.split(" ")[0];
										day = rs.getString("dob").split(",")[0]
												.split(" ")[1];
										if (year.equals(paramList.get(3))
												&& month.equals(paramList
														.get(4))
												&& day.equals(paramList.get(5))) {
											matchSet.put(
													rs.getString("ent_id"),
													paramList.get(2) + "~"
															+ paramList.get(0)
															+ "~"
															+ paramList.get(1));
										}
									} else {
										year = rs.getString("dob");
										if (year.equals(paramList.get(3))) {
											matchSet.put(
													rs.getString("ent_id"),
													paramList.get(2) + "~"
															+ paramList.get(0)
															+ "~"
															+ paramList.get(1));
										}
									}
								}
							}
						}
						result = 0.0;
						result1 = 0.0;
					}
				} else {
					for (String entitiesDetailsList : entityDetailsIndiaList) {
						result = 0.0;
						result1 = 0.0;
						String entities[] = entitiesDetailsList.split("~");
						result = FuzzySearch.tokenSortRatio(entities[0]
								.toUpperCase().replace(",", "").toUpperCase()
								.trim(), paramList.get(2).toUpperCase());
						if (!entities[1].equals("NA"))
							result1 = FuzzySearch.tokenSortRatio(
									entities[1].replace(",", "")
											.toUpperCase().trim(), paramList
											.get(2).toUpperCase());

						gender = entities[4];
						if (result > Integer.parseInt(threshold)
								|| result1 > Integer.parseInt(threshold)) {
							// System.out.println(result + "thr-" + threshold);
							// System.out.println(gender + "~" +
							// paramList.get(7));
							if (gender.equals(paramList.get(7))) {
								matchSet.put(entities[5], paramList.get(2)
										+ "~" + paramList.get(0) + "~"
										+ paramList.get(1));
							} else {
								if (!entities[2].equalsIgnoreCase("NA")) {
									if (entities[2].contains(",")) {
										year = entities[2].split(",")[1];
										month = entities[2].split(",")[0]
												.split(" ")[0];
										day = entities[2].split(",")[0]
												.split(" ")[1];
										if (year.equals(paramList.get(3))
												&& month.equals(paramList
														.get(4))
												&& day.equals(paramList.get(5))) {
											matchSet.put(
													entities[5],
													paramList.get(2) + "~"
															+ paramList.get(0)
															+ "~"
															+ paramList.get(1));
										}
									} else {
										year = entities[2];
										if (year.equals(paramList.get(3))) {
											matchSet.put(
													entities[5],
													paramList.get(2) + "~"
															+ paramList.get(0)
															+ "~"
															+ paramList.get(1));
										}
									}
								}
							}
						}
						result = 0.0;
						result1 = 0.0;
					}
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
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return matchSet;
	}

	public String generateCorelationId(int randomLength) {
		PreparedStatement preStmt = null;
		ResultSet rs = null;

		String corelationId = "";
		String time = "";
		String seq = "";

		String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer captchaStrBuffer = new StringBuffer();
		java.util.Random rnd = new java.util.Random();

		// build a random captchaLength chars salt
		while (captchaStrBuffer.length() < randomLength) {
			int index = (int) (rnd.nextFloat() * saltChars.length());
			captchaStrBuffer.append(saltChars.substring(index, index + 1));
		}
		try {
			preStmt = connection
					.prepareStatement("select to_char(sysdate,'ddmmyy'),lpad(corltn_seq.nextval,4,0) from dual");
			rs = preStmt.executeQuery();
			while (rs.next()) {
				time = rs.getString(1);
				seq = rs.getString(2);
			}
			corelationId = (time + captchaStrBuffer.toString() + seq);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (preStmt != null) {
					preStmt.close();
					preStmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception ex) {

			}
		}

		return corelationId;
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

	public void getIncrementalCustomerData() {
		Statement stmt = null;
		ResultSet rs = null;
		String cbsDt = "";
		try {
			stmt = connection.createStatement();

			rs = stmt
					.executeQuery("select to_char(cbs_date,'dd-mon-yyyy') from aml_bizz_tab");
			while (rs.next()) {
				cbsDt = rs.getString(1);
			}

			stmt.executeUpdate("insert into aml_cust_sdn (select cust_id,to_date('"
					+ cbsDt
					+ "','dd-mm-yy'),'Y' scan_flg from aml_cust_master where cust_id in (select substr(table_key,0,instr(table_key,'/')-1) from tbaadm.adt@live_fin where table_name='CCMG' and audit_date between to_date('"
					+ cbsDt
					+ " 00:00:00','dd-mm-yy hh24:mi:ss') and to_date('"
					+ cbsDt
					+ " 23:59:59','dd-mm-yy hh24:mi:ss') and func_code in ('A','M')) and cust_del_flg='N')");

			connection.commit();
			System.out
					.println("Date:: "
							+ cbsDt
							+ "\nIncremental Customer successfully added in AML_CUST_SDN table...");
		} catch (SQLException ex) {
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
	}

	public void customerScreenProcess() {
		Statement stmt = null;
		Statement stmtUpdate = null;
		ResultSet rsCust = null;
		ResultSet rsUpdate = null;
		String name = "";
		String id = "";
		String passport = "", pan = "", gender = "";
		String dob = "", custRisk = "";
		String cCountry = "", pCountry = "", correlationId = "";
		String alertFlg = "N";
		PreparedStatement pstmt_scan_tab = null;
		try {
			// System.out.println("Extracting the Incremental Customer Data...");
			// getIncrementalCustomerData();

			threshold = getFuzzyVal();

			entityDetailsIndiaList = getEntityDetailsIndiaCntry();

			System.out
					.println("Processing Individual Customer Data For Screening...");

			stmt = connection.createStatement();
			String customerSQL = "select cust_id,cust_name,nvl(cust_pan_no,'-') as pan,"
					+ "nvl(cust_passport_no,'-') as passport,nvl(to_char(cust_dob,'DD-MM-YYYY'),'') as dob,nvl(CUST_SEX,'-') gender ,"
					+ " (select ref_desc from aml_rct where ref_rec_type='03' and ref_code=a.cust_perm_cntry_code) as cpCountry,(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=a.cust_commu_cntry_code) as cCountry,(select cust_risk_rating from aml_kyc where cust_id=a.cust_id) cust_risk_code "
					+ " from aml_cust_master_180420 a where ind_flg='I' and scan_flg='N'  and rownum<=2000";

			// System.out.println(customerSQL);

			rsCust = stmt.executeQuery(customerSQL);

			while (rsCust.next()) {
				try {
					name = rsCust.getString("cust_name");
					id = rsCust.getString("cust_id");
					passport = rsCust.getString("passport");
					pan = rsCust.getString("pan");
					custRisk = rsCust.getString("cust_risk_code");
					cCountry = rsCust.getString("cCountry");
					pCountry = rsCust.getString("cpCountry");
					gender = rsCust.getString("gender");
					correlationId = "FULLIDBIP";
					HashMap<String, String> getMonth = new HashMap<String, String>();
					getMonth.put("01", "JANUARY");
					getMonth.put("02", "FEBRUARY");
					getMonth.put("03", "MARCH");
					getMonth.put("04", "APRIL");
					getMonth.put("05", "MAY");
					getMonth.put("06", "JUNE");
					getMonth.put("07", "JULY");
					getMonth.put("08", "AUGUST");
					getMonth.put("09", "SEPTEMBER");
					getMonth.put("10", "OCTOBER");
					getMonth.put("11", "NOVEMBER");
					getMonth.put("12", "DECEMBER");
					String requestDob[] = rsCust.getString("dob").split("-");
					dob = (getMonth.get(requestDob[1].trim()) + " "
							+ requestDob[2] + "," + requestDob[0]);
					ArrayList<String> paramIdList = new ArrayList<String>();

					paramIdList.add(dob);
					paramIdList.add(pCountry);
					paramIdList.add(name);
					paramIdList.add(requestDob[0]);
					paramIdList.add(getMonth.get(requestDob[1]));
					paramIdList.add(requestDob[2]);
					paramIdList.add(cCountry);
					paramIdList.add(gender);
					HashMap<String, String> dobMatchSet = scanOtherParam(paramIdList);
					if (dobMatchSet.size() > 0) {
						if (generateAlert(id, custRisk, "F", getFuzzyVal(),
								"P", dobMatchSet, correlationId)) {
							alertFlg = "Y";
						}
					}

					stmtUpdate = connection.createStatement();
					rsUpdate = stmtUpdate
							.executeQuery("update aml_cust_master_180420 a set a.scan_flg='C'  where cust_Id ='"
									+ id + "'");

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
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
			connection.commit();
			System.out
					.println("Individual Customer Data Screening Completed Sucessfully...");
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
				if (rsUpdate != null) {
					rsUpdate.close();
					rsUpdate = null;
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
				System.out.println("Current Directory::" + dir);
				InputStream is = new FileInputStream(dir
						+ "/aml-config.properties");
				bundle.load(is);
				is.close();
				makeConnection();
				// if (checkCreateThread().equals("C")){
				customerScreenProcess();
				// }
				Thread.sleep(1000 * 300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		//makeConnection();
		Thread t = new Thread(new LexisNexisProbableIndScreening());
		t.start();
	}

}

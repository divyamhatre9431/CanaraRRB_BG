package com.idbi.intech.iaml.screening;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.idbi.intech.iaml.factory.ConnectionFactory;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class IncrementalScreenThread implements Runnable {
	private static Connection connection = null;
	static IncrementalScreenThread process_run = new IncrementalScreenThread();
	//private static ArrayList<String> entityDetailsList = null;
	private static String threshold = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				customerScreenProcess();
				Thread.sleep(1000 * 60 * 60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void customerScreenProcess() {
		Statement stmt = null;
		Statement stmtUpdate = null;
		ResultSet rsCust = null;
		ResultSet rsUpdate = null;
		String name = "",id = "",passport = "", pan = "", otherTd = "", branchId = "",zoneId = "", custRisk = "",cCountry = "", pCountry = "", correlationId = "",gender="",custDob="";
		String alertFlg = "N";
		PreparedStatement pstmt_scan_tab = null;
		
		try {
			// System.out.println("Extracting the Incremental Customer Data...");
			// getIncrementalCustomerData();
			
			makeConnection();
			//entityDetailsList = getEntityDetailsAllCntry();

			System.out.println("Processing Incremental Customer Data For Screening...");

			stmt = connection.createStatement();
			String customerSQL = "select cust_id,cust_name,nvl(cust_pan_no,'NA') as pan,nvl(nat_id_Card_num,'NA') as otherId,"
					+ "(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=(select country from aml_cust_address where orgkey=a.cust_id and ADDRESSCATEGORY='Mailing')) cCountry,"
					+ "(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=a.country_code) pCountry,"
					+ "cust_sex,to_char(cust_dob,'dd-MONTH-yyyy') cust_dob,nvl(cust_passport_no,'NA') as passport,(select zone_id from aml_sol_mapping where sol_id=a.primary_sol_id) zoneId,primary_sol_id branchId from aml_cust_master a "
					+ "where scan_flg='N' and aml_upload_date = (select cbs_date from aml_bizz_tab where ope_flg_sdn='C')";
			System.out.println("customerSQL : "+customerSQL);

			rsCust = stmt.executeQuery(customerSQL);

			while (rsCust.next()) {
				try {
					alertFlg = "N";
					name = rsCust.getString("cust_name");
					id = rsCust.getString("cust_id");
					passport = rsCust.getString("passport");
					pan = rsCust.getString("pan");
					otherTd = rsCust.getString("otherId");
					zoneId = rsCust.getString("zoneId");
					branchId = rsCust.getString("branchId");
					cCountry = rsCust.getString("cCountry");
					pCountry = rsCust.getString("pCountry");
					gender = rsCust.getString("cust_sex");
					custDob = rsCust.getString("cust_dob");
					
					ArrayList<String> paramIdList = new ArrayList<String>();
					paramIdList.add(passport);
					paramIdList.add(pan);
					paramIdList.add(otherTd);
					
					ArrayList<String> paramIdList2 = new ArrayList<String>();
					paramIdList2.add(name);
					paramIdList2.add(custDob);
					paramIdList2.add(gender);
					paramIdList2.add(cCountry);
					paramIdList2.add(pCountry);
					
					
					// Scanning For Absolute Match
					System.out.println("Scanning Absolute match for cust id : " + id);
					HashMap<String, String> idMatchSet = scanId(paramIdList);
					correlationId = "INCRSCANPNB";
					if (idMatchSet.size() > 0) {
						if (generateAlert(id, custRisk, "I", "A", idMatchSet, correlationId, zoneId, branchId)) {
							alertFlg = "Y";
						}
					} else {
						System.out.println("Scanning Probable match for cust id : " + id);
						idMatchSet = scanOtherParam(paramIdList2);
						
						if (idMatchSet.size() > 0) {
							if (generateAlert(id, custRisk, "I", "P", idMatchSet, correlationId, zoneId, branchId)) {
								alertFlg = "Y";
							}
						}
					}
					stmtUpdate = connection.createStatement();

					rsUpdate = stmtUpdate
							.executeQuery("update aml_cust_master a set a.scan_flg='C' where cust_Id ='" + id + "'");
					
					pstmt_scan_tab = connection
							.prepareStatement("insert into cust_scan_tab values (?,sysdate,'00',?,?,?)");

					pstmt_scan_tab.setString(1, id);
					pstmt_scan_tab.setString(2, alertFlg);
					pstmt_scan_tab.setString(3, "I");
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
			System.out.println("Incremental Customer Data Screening Completed Sucessfully...");
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

	// Check Id match
	public HashMap<String, String> scanId(ArrayList<String> paramIdList) {
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			for (String matchId : paramIdList) {
				if (!matchId.equals("-")) {
					String checkSQL = "select ent_id,passportid,nationalid,otherid from aml_ln_entities ";
					checkSQL += "where passportid ='" + matchId + "' or NATIONALID ='" + matchId + "' or OTHERID ='"
							+ matchId + "' ";
					rs = stmt.executeQuery(checkSQL);
					while (rs.next()) {
						String absoluteMatchId = "";
						if (rs.getString("passportid") != null) {
							absoluteMatchId = rs.getString("passportid");
						} else if (rs.getString("nationalid") != null) {
							absoluteMatchId = rs.getString("nationalid");
						} else if (rs.getString("otherid") != null) {
							absoluteMatchId = rs.getString("otherid");
						}
						matchSet.put(rs.getString("ent_id"), absoluteMatchId);
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

	public Boolean generateAlert(String custId, String custRisk, String scanMode, String matchFlg,
			HashMap<String, String> matchSet, String correlationId, String zoneId, String branchId) {
		Boolean flg = false;
		Statement stmt = null, stmtSeq = null;
		ResultSet rs = null, rsSeq = null;
		String alert_id = "", record_id = "";
		PreparedStatement pstmt_sdn_alert = null;
		PreparedStatement pstmtSubAlert = null;
		PreparedStatement pstmtRuleAlert = null;
		try {
			// Insert into Alert master table
			pstmt_sdn_alert = connection.prepareStatement(
					"insert into ns_alert_master(ALERT_ID,CUST_ID,ALERT_STATUS,GENERATED_TIME,USER_ID,LAST_USERID,LAST_CHANGETIME,SCAN_TYPE,FUZZY_VAL,CUST_RISK,"
					+ "MATCH_FLG,CORRELATION_ID,ZONE,BRANCH_ID,CURR_ROLE_ID,LAST_ROLE_ID,LOCK_STATUS,SUSP_FLG,LOCK_TIME,UNLOCK_TIME,LOCK_ID) "
					+ "values" + "(?,?,?,sysdate,?,?,sysdate,?,?,?,?,?,?,?,'LEVEL1','NA','N','X',null,null,'NA')");
			
			pstmtSubAlert = connection.prepareStatement("insert into ns_alert_agg values (?,?,?,?,?,'','')");
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
			pstmt_sdn_alert.setString(7, "100");
			pstmt_sdn_alert.setString(8, custRisk);
			pstmt_sdn_alert.setString(9, matchFlg);
			pstmt_sdn_alert.setString(10, correlationId);
			pstmt_sdn_alert.setString(11, zoneId);
			pstmt_sdn_alert.setString(12, branchId);
			pstmt_sdn_alert.executeUpdate();

			// Insert into aggregate details
			for (Map.Entry<String, String> entry : matchSet.entrySet()) {
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

	
	public HashMap<String, String> scanOtherParam(ArrayList<String> paramList) {
		HashMap<String, String> matchSet = new HashMap<String, String>();
		String threshold = "";
		Statement stmt = null;
		ResultSet rs = null;
		String year = "", month = "", day = "", gender = "";
		String custName = paramList.get(0) == null ? "NA" : paramList.get(0);
		String custDob = paramList.get(1) == null ? "NA" : paramList.get(1);
		String custSex = paramList.get(2) == null ? "NA" : paramList.get(2);
		String cCountry = paramList.get(3) == null ? "NA" : paramList.get(3);
		String pCountry = paramList.get(4) == null ? "NA" : paramList.get(4);
		String custBirthYear = "";
		String custBirthMonth = "";
		String custBirthDate = "";
		
		try {
			
			if(!custDob.equals("NA"))
			{
				String[] custDobArr = custDob.split("-");
				custBirthDate = custDobArr[0];
				custBirthMonth = custDobArr[1].trim();
				custBirthYear = custDobArr[2];
			}
			
			if (!(custName.equals("-") && custName != null)) {
				threshold = getFuzzyVal();
				stmt = connection.createStatement();
				String query = "select ent_id,name,nvl(aka,'NA')aka,dob,country,nvl(gender,'X') gender from aml_ln_entities where dob is not null and (country ='"
						+ cCountry + "' or country ='" + pCountry + "') ";
				//System.out.println("threshold : "+threshold);
				//System.out.println(query);
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					double result = 0.0;
					double result1 = 0.0;
					
					result = FuzzySearch.tokenSortRatio(rs.getString("name").toUpperCase()
							.replace(",", "").toUpperCase().trim(), custName
							.toUpperCase());
					
					if (!rs.getString("aka").equals("NA"))
						result1 = FuzzySearch.tokenSortRatio(rs
								.getString("aka").replace(",", "")
								.toUpperCase().trim(), custName.toUpperCase());
					
					gender = rs.getString("gender");	
					String country = rs.getString("country");
					
					if (result > Integer.parseInt(threshold)
							|| result1 > Integer.parseInt(threshold)) {
						
						if (gender.equals(custSex)) {
							System.out.println("1");
							
							System.out.println("alerts generated after gender : "+rs.getString("ent_id")+"~"+
									custName + "~"
											+ country + "~"
											+ custSex);
							matchSet.put(
									rs.getString("ent_id"),
									custName + "~"
											+ country + "~"
											+ custSex);
						}  else {
							if (rs.getString("dob").contains(",")) {
								System.out.println("2");
								
								String dob = rs.getString("dob");
								System.out.println("dob : "+dob);
								
								Pattern pattern = Pattern.compile(" ");
								Matcher matcher = pattern.matcher(dob);
								int count = 0;
								while (matcher.find()) {
								    count++;
								}
								
								if(count == 2)
								{
									year = dob.split(",")[1].trim();
									month = dob.split(",")[0]
											.split(" ")[0].trim();
									day = dob.split(",")[0]
											.split(" ")[1].trim();
									System.out.println("year : "+year+" - month : "+month+" - day : "+day);
								}
								else
								{
									year = dob.split(",")[1].trim();
									month = dob.split(",")[0].trim();
									System.out.println("year : "+year+" - month : "+month);
								}
								
								
								System.out.println("custBirthYear : "+custBirthYear+" - custBirthMonth : "+custBirthMonth+" - custBirthDate : "+custBirthDate);
								
								if (year.equals(custBirthYear)
										&& month.equals(custBirthMonth)
										&& day.equals(custBirthDate)) {
									System.out.println("3");
									
									System.out.println("alerts generated after dob contains 'comma ,' : "+rs.getString("ent_id")+"~"+
											custName + "~"
													+ country + "~"
													+ custDob);
									
									matchSet.put(
											rs.getString("ent_id"),
											custName + "~"
													+ country + "~"
													+ custDob);
								}
								else if (year.equals(custBirthYear)) {
									
									System.out.println("4");
									
									System.out.println("alerts generated after year : "+rs.getString("ent_id")+"~"+
											custName + "~"
													+ country + "~"
													+ year);
									
									matchSet.put(
											rs.getString("ent_id"),
											custName + "~"
													+ country + "~"
													+ year);
								}
							} 
						}
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
	


	public void getIncrementalCustomerData() {
		Statement stmt = null;
		ResultSet rs = null;
		String cbsDt = "";
		try {

			stmt = connection.createStatement();
			rs = stmt.executeQuery("select to_char(cbs_date,'dd-mon-yyyy') from aml_bizz_tab");
			while (rs.next()) {
				cbsDt = rs.getString(1);
			}
			System.out.println("Incremental Screening for Data:: " + cbsDt);
			stmt.execute("truncate table aml_cust_sdn");
			stmt.executeUpdate("insert into aml_cust_sdn (select cust_id,to_date('" + cbsDt
					+ "','dd-mm-yy'),'Y' scan_flg from aml_cust_master where CUST_STATUS='A' and scan_flg='N')");

			connection.commit();

			stmt.executeUpdate(
					"update aml_cust_master set scan_flg='Y' where cust_id in (select table_key from aml_cust_sdn)");

			stmt.executeUpdate("update aml_bizz_tab set ope_flg_sdn='Y'");

			connection.commit();

			System.out.println("Incremental Customer successfully added in AML_CUST_SDN table...");

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
	
	public ArrayList<String> getEntityDetailsAllCntry() {
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> entityList = new ArrayList<String>();
		System.out.println("Fetching Entities Details for foreign Country...");
		try {
			stmt = connection.createStatement();
			String query = "select a.person_id ent_id,a.name,nvl(b.alias,'NA') aka,nvl(c.dob,'NA') dob,d.nation country,'X' gender from AML_WL_NAME a,AML_WL_ALIAS b,AML_WL_DOB c,AML_WL_NATIONALITY d "
					+ "where a.person_id=b.person_id and a.person_id=c.person_id and a.person_id=d.person_id ";
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
			makeConnection();
			Thread t = new Thread(new IncrementalScreenThread());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}
	public static void main(String[] args) {
		 try {
			makeConnection();
			Thread t = new Thread(new IncrementalScreenThread());
			t.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}



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

import com.idbi.intech.iaml.factory.ConnectionFactory;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class LexisNexisRevScreening implements Runnable {
	private static Connection connection = null;
	static LexisNexisRevScreening process_run = new LexisNexisRevScreening();
	private static ArrayList<String> entityDetailsIndiaList = null;
	private static String threshold = null;
	private static String threshold2 = null;
	double fuzzyVal = 0;
	double fuzzyVal1 = 0;

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
				entityList.add(rs.getString("name") + "~" + rs.getString("aka") + "~" + rs.getString("dob") + "~"
						+ rs.getString("country") + "~" + rs.getString("gender") + "~" + rs.getString("ent_id"));
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
	//For  Customere Address Details
	public String getCustAddress(String wlAddress,String CustId) {
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> entityList = new ArrayList<String>();
		double result1=0.0;
		String address=null;
		
		try {
			stmt = connection.createStatement();
			String query = "select nvl( ADDRESS_LINE1,'')||' '||nvl(ADDRESS_LINE2,'')||','||"
					+ "NVL((select ref_desc from aml_rct where ref_rec_type='01' and ref_code=CITY),'')||','||"
					+ "NVL((select ref_desc from aml_rct where ref_rec_type='02' and ref_code=STATE),'')||','||"
					+ "NVL((select ref_desc from aml_rct where ref_rec_type='03' and ref_code=COUNTRY),'')||','||"
					+ "nvl(zip,'') address  from aml_cust_address where orgkey='"+CustId+"'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				
				address=rs.getString("address").toUpperCase().trim();
				result1 = FuzzySearch.tokenSortRatio(address
						, wlAddress);
				System.out.println("result1 will come here"+result1);
				if(result1>Integer.parseInt(threshold)) {
				
					return result1+"~"+address;
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
		return result1+"~"+address;

	}

	public void customerScreenProcess() {
		Statement stmt = null;
		Statement stmtUpdate = null;
		ResultSet rsCust = null;
		ResultSet rsUpdate = null;
		String name = "";
		String id = "";
		String passport = "", pan = "", otherTd = "", branchId = "";
		String zoneId = "", custRisk = "";
		String cCountry = "", pCountry = "", correlationId = "";
		String dob="";
		String alertFlg = "N";
		PreparedStatement pstmt_scan_tab = null;
		try {
			// System.out.println("Extracting the Incremental Customer
			// Data...");
			// getIncrementalCustomerData();
			threshold = getFuzzyVal();
			threshold2 = getFuzzyValForAddress();
			System.out.println("Processing Reverse Customer Data For Screening...");
			entityDetailsIndiaList = getEntityDetailsIndiaCntry();
			stmt = connection.createStatement();
			String customerSQL = "select cust_id,cust_name,nvl(cust_pan_no,'NA') as pan,nvl(nat_id_Card_num,'NA') as otherId,"
					+ "(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=(select country from aml_cust_address where orgkey=a.cust_id and ADDRESSCATEGORY='Mailing' and rownum=1)) cCountry,"
					+ "(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=(select country from aml_cust_address where orgkey=a.cust_id and ADDRESSCATEGORY='Home' and rownum=1)) pCountry,"
					+ "nvl(cust_passport_no,'NA') as passport ,(select zone_id from aml_sol_mapping where sol_id=a.primary_sol_id) zoneId,primary_sol_id branchId, to_char(cust_dob, 'yyyy-mm-dd') dob from aml_cust_master a where a.cust_id not in (select scan_cust_id from name_screen_activity where scan_type='REV') ";
			// + "where cust_id='151673126' ";
			System.out.println("customerSQL : " + customerSQL);

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
					dob=rsCust.getString("dob");
					ArrayList<String> paramIdList = new ArrayList<String>();
					if (!passport.equalsIgnoreCase("NA")) {
						paramIdList.add(passport);
					}
					if (!otherTd.equalsIgnoreCase("NA")) {
						paramIdList.add(otherTd);
					}
					if (!pan.equalsIgnoreCase("NA")) {
						paramIdList.add(pan);
					}

					// Scanning For Absolute Match
					System.out.println("Scanning Absolute match for cust id : " + id);
					HashMap<String, String> idMatchSet = scanId(paramIdList);
					boolean check =NameScreenActivity(id,"REV");
					correlationId = "REVSCANPNB";
					if (idMatchSet.size() > 0) {
						if (generateAlert(id, custRisk, "I", "A", idMatchSet, correlationId, zoneId, branchId, "100")) {
							alertFlg = "Y";
						}

					} else {

						// idMatchSet = scanOtherParam(name, cCountry, // pCountry); idMatchSet =
						idMatchSet = scanByParamFuzzy(name, cCountry, pCountry,dob,id);
						if (idMatchSet.size() > 0) {
							if (generateAlert(id, custRisk, "I", "P", idMatchSet, correlationId, zoneId, branchId,
									String.valueOf(fuzzyVal))) {
								alertFlg = "Y";
							}
						}

					}
					stmtUpdate = connection.createStatement();
					rsUpdate = stmtUpdate
							.executeQuery("update aml_cust_master_ln a set a.scan_flg='C' where cust_Id ='" + id + "'");

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
			System.out.println("Reverse Customer Data Screening Completed Sucessfully...");
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
					String checkSQL = "select Doc_No,Data_id from (select doc_no,data_id from aml_wl_ind_doc ";
					checkSQL += "union all select ID_NUMBER doc_no,data_id from aml_wl_sdn_id "
							+ " union all select pan doc_no,DATA_ID from fcra_internal_watchlist_data where pan is not null "
							+ " union all select passport doc_no ,DATA_ID from fcra_internal_watchlist_data where passport is not null "
							+ " union all select aadhaar doc_no,DATA_ID from fcra_internal_watchlist_data where aadhaar is not null )where data_id in(select data_id from aml_rev_screen) ";
					checkSQL += "and Doc_No ='" + matchId + "'";
					rs = stmt.executeQuery(checkSQL);
					while (rs.next()) {
						String absoluteMatchId = "";
						if (rs.getString("Doc_No") != null) {
							absoluteMatchId = rs.getString("Doc_No");
						}
						matchSet.put(rs.getString("Data_id"), absoluteMatchId);
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

	public HashMap<String, String> scanByParamFuzzy(String name, String cCountry, String pCountry,String dob,String id) {
		// System.out.println("Scan other parameters");
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null,stmt2=null,stmt3=null;
		Statement stmt4=null;
		ResultSet rs = null, rs2 = null,rs3=null,rs4=null;
		double result = 0.0;
		double result1 = 0.0;
		double result3=0.0;
		String query2 = null,query3=null,query4=null;
		String address="",mainAddress="";
		
		try {

			stmt = connection.createStatement();
			stmt2=connection.createStatement();
			stmt3=connection.createStatement();
			stmt4=connection.createStatement();
			String query = "select data_id,name from(select data_id , first_name||' '||second_name||' '||third_name name from aml_wl_entity union all select data_id ,last_name name from aml_wl_sdn\r\n"
					+ "union all select data_id ,first_name||' '||second_name||' '||third_name name from aml_wl_ind union all select data_id , alias_name name from aml_wl_entity_alias  union all select data_id , alias_name name from aml_wl_ind_alias union all select data_id , first_name||' '||last_name name from aml_wl_sdn_aka"
					+ " union all select data_id , name from fcra_internal_watchlist_data "
					+ ") where data_id in (select data_id from aml_rev_screen) and name is not null";
		 
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				result = FuzzySearch.tokenSortRatio(
						rs.getString("name").toUpperCase().replace(",", "").toUpperCase().trim(), name.toUpperCase());
				
				if (result > Integer.parseInt(threshold)) {
					
				query4="select data_id,address from(select data_id, nvl(street,' ')||','||nvl(city,' ')||','||nvl(state_province,' ')\r\n"
						+ " ||','||nvl(country,' ')||','||nvl(zip_code,' ')\r\n"
						+ " address from aml_wl_entity_addr union all select data_id, nvl(city,' ')||','||nvl(country,' ') address from aml_wl_sdn_addr\r\n"
						+ "	union all select data_id, nvl(street,' ')||','||nvl(city,' ')||','||nvl(state_province,' ')\r\n"
						+ " ||','||nvl(country,' ')||','||nvl(zip_code,' ') address from aml_wl_ind_addr"
						+ "  union all select data_id, nvl(address,' ') address from fcra_internal_watchlist_data) where data_id='"+rs.getString("data_id")+"' ";
				
				rs4=stmt4.executeQuery(query4);
				while(rs4.next()) {
				address=rs4.getString("address").toUpperCase().trim();
				String mainResult[]=getCustAddress(address, id).split("~");
				 result3=Double.parseDouble(mainResult[0]);
				 mainAddress=mainResult[1];
			
				
					if(result3>Integer.parseInt(threshold2)) {
						fuzzyVal = Math.max(result3, result1);
						matchSet.put(rs.getString("data_id"), name + "~" +mainAddress);
						return matchSet;
					
					}
				
				}
				
					query2 = "select data_id,country from(select data_id ,country from aml_wl_entity_addr union all select data_id,country from aml_wl_ind_addr\r\n"
							+ "union all select data_id ,country from aml_wl_sdn_addr"
							+ " union all select data_id ,country from fcra_internal_watchlist_data ) where data_id ='"+rs.getString("data_id")+"' and country='"
							+ cCountry + "' or country ='" + pCountry + "'";
					//System.out.println("query2 will come here "+query2);
					rs2 = stmt2.executeQuery(query2);

					while (rs2.next()) {
					
						/*
						 * query3="select data_id ,dob from \r\n" +
						 * "(select data_id, to_char(to_date(birth_year,'dd mon yyyy'), 'YYYY') dob from aml_wl_sdn_birth \r\n"
						 * + "where birth_year is not null\r\n" +
						 * "and validate_conversion(birth_year AS DATE, 'dd mon yyyy') <> 0\r\n" +
						 * "union all\r\n" +
						 * "select data_id, to_char(to_date  (dob,'yyyy-mm-dd'), 'yyyy') dob from aml_wl_ind_dob \r\n"
						 * + "where dob is not null\r\n" +
						 * "and validate_conversion(dob AS DATE, 'yyyy-mm-dd') <> 0) where data_id ='"
						 * +rs.getString("data_id")+"' and dob="+Integer.parseInt(dob)+"";
						 * 
						 *
						 */	
						query3="    select data_id, dob from(select data_id, to_char(to_date(birth_year,'dd mon yyyy')) dob from aml_wl_sdn_birth \r\n"
								+ " where birth_year is not null\r\n"
								+ "  and validate_conversion(birth_year AS DATE, 'dd mon yyyy') <> 0\r\n"
								+ "    union all\r\n"
								+ "	select data_id, to_char(to_date  (dob,'yyyy-mm-dd')) dob from aml_wl_ind_dob \r\n"
								+ "   where dob is not null\r\n"
								+ "and validate_conversion(dob AS DATE, 'yyyy-mm-dd') <> 0"
								+ "union all select data_id, to_char(to_date  (dob,'dd-mm-yy')) dob from FCRA_INTERNAL_WATCHLIST_DATA \r\n"
								+ "where dob is not null and validate_conversion(dob AS DATE, 'dd-mm-yy') <> 0)"
								+ "WHERE dob = TO_DATE('"+dob+"', 'YYYY-MM-DD') and data_id='"+rs2.getString("data_id")+"'" ;
						//System.out.println("query:" +query3);
						
						rs3=stmt3.executeQuery(query3);
						while(rs3.next()) {
							fuzzyVal = Math.max(result, result1);
							matchSet.put(rs.getString("data_id"), name + "~" + cCountry+"~"+dob );
							}
						
					}
					
				
				
				
				 
						
				
					//matchSet.put(rs.getString("data_id"), name);
				}
				result = 0.0;
				result1 = 0.0;
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

	public HashMap<String, String> scanOtherParamFuzzy(String name, String cCountry, String pCountry) {
		System.out.println("Scan other parameters");
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		double result = 0.0;
		double result1 = 0.0;
		try {

			if (!cCountry.equalsIgnoreCase("INDIA")) {
				stmt = connection.createStatement();
				String query = "select ent_id,name,nvl(aka,'NA')aka,nvl(dob,'NA'),country,nvl(gender,'X') gender from aml_ln_entities where (country ='"
						+ cCountry + "' or country ='" + pCountry + "') ";
				// System.out.println(query);
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					result = FuzzySearch.tokenSortRatio(
							rs.getString("name").toUpperCase().replace(",", "").toUpperCase().trim(),
							name.toUpperCase());
					if (!rs.getString("aka").equals("NA"))
						result1 = FuzzySearch.tokenSortRatio(rs.getString("aka").replace(",", "").toUpperCase().trim(),
								name.toUpperCase());

					if (result > Integer.parseInt(threshold) || result1 > Integer.parseInt(threshold)) {
						fuzzyVal = Math.max(result, result1);
						matchSet.put(rs.getString("ent_id"), name + "~" + pCountry + "~" + cCountry);
					}
					result = 0.0;
					result1 = 0.0;
				}
			} else {
				for (String entitiesDetailsList : entityDetailsIndiaList) {
					result = 0.0;
					result1 = 0.0;
					String entities[] = entitiesDetailsList.split("~");

					result = FuzzySearch.tokenSortRatio(entities[0].toUpperCase().replace(",", "").toUpperCase().trim(),
							name.toUpperCase());
					if (!entities[1].equals("NA"))
						result1 = FuzzySearch.tokenSortRatio(entities[1].replace(",", "").toUpperCase().trim(),
								name.toUpperCase());
					if (result > Integer.parseInt(threshold) || result1 > Integer.parseInt(threshold)) {
						fuzzyVal = Math.max(result, result1);
						matchSet.put(entities[5], name + "~" + pCountry + "~" + cCountry);
					}
					result = 0.0;
					result1 = 0.0;
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
			HashMap<String, String> matchSet, String correlationId, String zoneId, String branchId, String fuzzyVal) {
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
					"insert into ns_alert_master(ALERT_ID,CUST_ID,ALERT_STATUS,GENERATED_TIME,USER_ID,LAST_USERID,LAST_CHANGETIME,"
							+ "SCAN_TYPE,FUZZY_VAL,CUST_RISK,MATCH_FLG,CORRELATION_ID,ZONE,BRANCH_ID,CURR_ROLE_ID,LAST_ROLE_ID,LOCK_STATUS,SUSP_FLG,"
							+ "LOCK_TIME,UNLOCK_TIME,LOCK_ID) values"
							+ "(?,?,?,sysdate,?,?,sysdate,?,?,?,?,?,?,?,'LEVEL1','NA','N','X',null,null,'NA')");
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
			pstmt_sdn_alert.setString(7, fuzzyVal);
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
	
	public Boolean NameScreenActivity(String custId, String scanMode) {
		Boolean flg = false;
		Statement stmt = null, stmtSeq = null;
		ResultSet rs = null, rsSeq = null;

		PreparedStatement pstmt_sdn_alert = null;

		try {
			// Insert into Alert master table
			pstmt_sdn_alert = connection.prepareStatement("insert into name_screen_activity values(?,sysdate,?)");

			// Insert into ns screen activity table

			pstmt_sdn_alert.setString(1, custId);
			pstmt_sdn_alert.setString(2, scanMode);

			int i = pstmt_sdn_alert.executeUpdate();

			connection.commit();
			if (i > 0) {
				flg = true;
			}
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

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return flg;
	}
	public String getFuzzyValForAddress() {
		String fuzzyVal = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "select VALUE from p_aml_general where name='SDN_FUZZY_MATCH_PERCENTAGE_ADDRESS'";
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
	// Fuzzy Val For Address

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

	// Scan Other parameter based on Name and country match
	public HashMap<String, String> scanOtherParam(String name, String cCountry, String pCountry) {
		System.out.println("Scan other parameters");
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = connection.createStatement();
			String query = "select ent_id,name,nvl(aka,'NA')aka,nvl(dob,'NA'),country,nvl(gender,'X') gender from"
					+ " aml_ln_entities where (country ='" + cCountry + "' or country ='" + pCountry + "') and name='"
					+ name.trim().toUpperCase() + "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				matchSet.put(rs.getString("ent_id"), name + "~" + cCountry + "~" + pCountry);
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
					+ "','dd-mm-yy'),'Y' scan_flg from aml_cust_master_ln where CUST_STATUS='A' and scan_flg='N')");

			connection.commit();

			stmt.executeUpdate(
					"update aml_cust_master_ln set scan_flg='Y' where cust_id in (select table_key from aml_cust_sdn)");

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
			Thread t = new Thread(new LexisNexisRevScreening());
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
			System.out.println("start");
			Thread t = new Thread(new LexisNexisRevScreening());
			t.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
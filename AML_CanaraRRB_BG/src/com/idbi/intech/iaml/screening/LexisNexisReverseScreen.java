package com.idbi.intech.iaml.screening;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Random;

import com.idbi.intech.iaml.factory.ConnectionFactory;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class LexisNexisReverseScreen implements Runnable {
	private static Connection connection = null;
	static LexisNexisReverseScreen process_run = new LexisNexisReverseScreen();
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
				ScationListScreening();
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

	// For Customere Address Details
	public String getCustAddress(String wlAddress, String CustId) {
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> entityList = new ArrayList<String>();
		double result1 = 0.0;
		String address = null;

		try {
			stmt = connection.createStatement();
			String query = "select nvl( ADDRESS_LINE1,'')||' '||nvl(ADDRESS_LINE2,'')||','||"
					+ "NVL((select ref_desc from aml_rct where ref_rec_type='01' and ref_code=CITY),'')||','||"
					+ "NVL((select ref_desc from aml_rct where ref_rec_type='02' and ref_code=STATE),'')||','||"
					+ "NVL((select ref_desc from aml_rct where ref_rec_type='03' and ref_code=COUNTRY),'')||','||"
					+ "nvl(zip,'') address  from aml_cust_address where orgkey='" + CustId + "'";
			rs = stmt.executeQuery(query);
			while (rs.next()) {

				address = rs.getString("address").toUpperCase().trim();
				result1 = FuzzySearch.tokenSortRatio(address, wlAddress);

				if (result1 > Integer.parseInt(threshold)) {

					return result1 + "~" + address;
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
		return result1 + "~" + address;

	}

	public void ScationListScreening() {
		Statement stmt = null;
		Statement stmtUpdate = null, stmt2 = null, stmt3 = null, stmt4 = null, stmt5 = null;
		ResultSet rsCust = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null;
		ResultSet rsUpdate = null;
		String query2 = "", query3 = "", query4 = "";
		String name = "";
		String id = "", data_id = "";
		String address = "";
		String zoneId = "", custRisk = "";
		String cCountry = "", pCountry = "", correlationId = "";
		String dob = "";
		String alertFlg = "N";
		int counter = 0;
		long count = 0;
		PreparedStatement pstmt_scan_tab = null;
		try {
			// System.out.println("Extracting the Incremental Customer
			// Data...");
			// getIncrementalCustomerData();
			threshold = getFuzzyVal();
			threshold2 = getFuzzyValForAddress();
			System.out.println("Processing Reverse Customer Data For Screening...");

			stmt = connection.createStatement();
			String customerSQL = "select data_id from aml_rev_screen where scan_flg='N'";
			// + "where cust_id='151673126' ";
			System.out.println("customerSQL : " + customerSQL);

			rsCust = stmt.executeQuery(customerSQL);

			while (rsCust.next()) {
				try {
					data_id = rsCust.getString(1) == null || rsCust.getString(1) == "" ? "-" : rsCust.getString(1);
					HashMap<String, String> idMatchSet = scanId(data_id);
					alertFlg = "N";
					ArrayList<String> custIdList = null;
					HashMap<String, String> idMatchedSet = new HashMap<String, String>();
					correlationId = "REVSCANPNB";
					if (idMatchSet.size() > 0) {
						for (Map.Entry<String, String> entry : idMatchSet.entrySet()) {
							custIdList = getCustomerDetails(entry.getKey());
							for (String custIdList1 : custIdList) {
								String custIdList2[] = custIdList1.split("~");
								String Idset[] = entry.getValue().split("~");
								idMatchedSet.put(Idset[0], Idset[1]);
								if (idMatchedSet.size() > 0) {
									if (generateAlert(custIdList2[0], custRisk, "I", "A", idMatchedSet, correlationId,
											custIdList2[1], custIdList2[2], "100")) {

										alertFlg = "Y";
									}

								}

							}

						}

					} else {
						stmt2 = connection.createStatement();
						stmt3 = connection.createStatement();
						stmt4 = connection.createStatement();
						stmt5 = connection.createStatement();
						String query = "select distinct * from(select data_id , first_name||' '||second_name||' '||third_name name from aml_wl_entity union all select data_id ,last_name name from aml_wl_sdn\r\n"
								+ "union all select data_id ,first_name||' '||second_name||' '||third_name name from aml_wl_ind union all select data_id , alias_name name from aml_wl_entity_alias  union all select data_id , alias_name name from aml_wl_ind_alias union all select data_id , first_name||' '||last_name name from aml_wl_sdn_aka"
								+ " union all select data_id , name from fcra_internal_watchlist_data "
								+ ") where data_id='"+data_id.trim()+"' and name is not null";
						rs3 = stmt2.executeQuery(query);
						while (rs3.next()) {
							System.out.println("Till Here Correct 3");
							
							name = rs3.getString(2) == null || rs3.getString(2) == "" ? "-"
									: rs3.getString(2);
							if(name.contains("'")) {
								name=name.replaceAll("'", "");
							}
							
							query4 = "select distinct * from(select data_id, nvl(street,' ')||','||nvl(city,' ')||','||nvl(state_province,' ')\r\n"
									+ " ||','||nvl(country,' ')||','||nvl(zip_code,' ')\r\n"
									+ " address from aml_wl_entity_addr union all select data_id, nvl(city,' ')||','||nvl(country,' ') address from aml_wl_sdn_addr\r\n"
									+ "	union all select data_id, nvl(street,' ')||','||nvl(city,' ')||','||nvl(state_province,' ')\r\n"
									+ " ||','||nvl(country,' ')||','||nvl(zip_code,' ') address from aml_wl_ind_addr"
									+ "  union all select data_id, nvl(address,' ') address from fcra_internal_watchlist_data) where data_id='"
									+ rs3.getString(1)+"'";
							rs4 = stmt3.executeQuery(query4);

							while (rs4.next()) {
						System.out.println("Till Here Correct 4");
						/*
						 * address = rs4.getString("address") == null || rs4.getString("address") == ""
						 * ? "-" : rs4.getString("address");
						 */
								address = rs4.getString(2) == null || rs4.getString(2) == "" ? "-"
										: rs4.getString(2);
								query2 = "select data_id,country from(select data_id ,country from aml_wl_entity_addr union all select data_id,country from aml_wl_ind_addr\r\n"
										+ "union all select data_id ,country from aml_wl_sdn_addr"
										+ " union all select data_id ,country from fcra_internal_watchlist_data ) where data_id ='"
										+rs4.getString(1)+"' and rownum=1 ";
								rs5 = stmt4.executeQuery(query2);

								while (rs5.next()) {
									System.out.println("Till Here Correct 5");
									cCountry = rs5.getString("country") == null || rs5.getString("country") == "" ? "-"
											: rs5.getString("country");
									query3 = " select data_id, dob from(select data_id, to_char(to_date(birth_year,'dd mon yyyy')) dob from aml_wl_sdn_birth \r\n"
											+ " where birth_year is not null\r\n"
											+ "  and validate_conversion(birth_year AS DATE, 'dd mon yyyy') <> 0\r\n"
											+ "    union all\r\n"
											+ "	select data_id, to_char(to_date  (dob,'yyyy-mm-dd')) dob from aml_wl_ind_dob \r\n"
											+ "   where dob is not null\r\n"
											+ "and validate_conversion(dob AS DATE, 'yyyy-mm-dd') <> 0"
											+ "union all select data_id, to_char(to_date  (dob,'dd-mm-yy')) dob from FCRA_INTERNAL_WATCHLIST_DATA \r\n"
											+ "where dob is not null and validate_conversion(dob AS DATE, 'dd-mm-yy') <> 0) "
											+ "WHERE  data_id='"+rs5.getString("data_id")+"' and rownum=1 ";
									rs6 = stmt5.executeQuery(query3);

									while (rs6.next()) {
										System.out.println("Till Here Correct 6");
										id = rs6.getString("data_id") == null || rs6.getString("data_id") == "" ? "-"
												: rs6.getString("data_id");
										dob = rs6.getString("dob") == null || rs6.getString("dob") == "" ? "-"
												: rs6.getString("dob");
										System.out.println(
												"id" + id + "country" + cCountry + "address" + address + "dob" + dob);
										idMatchSet = scanByParamFuzzy(name, cCountry, address, dob, id);
										if (idMatchSet.size() > 0) {
											for (Map.Entry<String, String> entry : idMatchSet.entrySet()) {
												custIdList = getCustomerDetails(entry.getKey());
												for (String custIdList1 : custIdList) {

													String custIdList2[] = custIdList1.split("~");
													String Idset[] = entry.getValue().split("\\|");

													idMatchedSet.put(Idset[0], Idset[1]);
													if (idMatchedSet.size() > 0) {
														if (generateAlert(custIdList2[0], custRisk, "I", "P",
																idMatchSet, correlationId, custIdList2[1],
																custIdList2[2], String.valueOf(fuzzyVal))) {

															alertFlg = "Y";
															counter++;
														}

													}
												}
											}

										}
									}

								}

								if (counter == 1) {
									break;
								}

							}

						}

					}

					/*
					 * stmtUpdate = connection.createStatement(); rsUpdate = stmtUpdate
					 * .executeQuery("update aml_cust_master_ln a set a.scan_flg='C' where cust_Id ='"
					 * + id + "'");
					 * 
					 * pstmt_scan_tab = connection
					 * .prepareStatement("insert into cust_scan_tab values (?,sysdate,'00',?,?,?)");
					 * 
					 * pstmt_scan_tab.setString(1, id); pstmt_scan_tab.setString(2, alertFlg);
					 * pstmt_scan_tab.setString(3, "I"); pstmt_scan_tab.setString(4, correlationId);
					 * pstmt_scan_tab.execute();
					 */

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
						if (stmt2 != null) {
							stmt2.close();
							stmt2 = null;
						}
						if (rs3 != null) {
							rs3.close();
							rs3 = null;
						}
						if (stmt3 != null) {
							stmt3.close();
							stmt3 = null;
						}
						if (rs4 != null) {
							rs4.close();
							rs4 = null;
						}
						if (stmt4 != null) {
							stmt4.close();
							stmt4 = null;
						}
						if (rs5 != null) {
							rs5.close();
							rs5 = null;
						}
						if (stmt5 != null) {
							stmt5.close();
							stmt5 = null;
						}
						if (rs6 != null) {
							rs6.close();
							rs6 = null;
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
				insertLogs(rsCust.getString(1), count, alertFlg);
				updateRequestStatus(rsCust.getString(1), "Y");

				count++;
			}
			connection.commit();
			System.out.println("Reverse Customer Data Screening Completed Sucessfully...");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			
				
				if (rsCust != null) {
					rsCust.close();
					rsCust = null;
				}
				
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

	}

	// Check Id match

	// check absolute match for cust_id

	public HashMap<String, String> scanId(String id) {
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null, stmt1 = null;
		String doc_no = "", data_id = "";
		ResultSet rs3 = null, rs = null;
		String query2 = "";

		try {
			stmt = connection.createStatement();
			query2 = "select Doc_No,Data_id from (select doc_no,data_id from aml_wl_ind_doc \r\n"
					+ " union all select ID_NUMBER doc_no,data_id from aml_wl_sdn_id \r\n"
					+ "	union all select pan doc_no,DATA_ID from fcra_internal_watchlist_data where pan is not null \r\n"
					+ "	union all select passport doc_no ,DATA_ID from fcra_internal_watchlist_data where passport is not null \r\n"
					+ "	union all select aadhaar doc_no,DATA_ID from fcra_internal_watchlist_data where aadhaar is not null )where data_id ='"
					+ id + "'";

			rs3 = stmt.executeQuery(query2);
			while (rs3.next()) {
System.out.println("Till Here Correct 2");
				doc_no = rs3.getString("doc_no");
				data_id = rs3.getString("data_id");
				stmt1 = connection.createStatement();

				if (!data_id.equals("-")) {
					String checkSQL = "select cust_id,nvl(cust_pan_no,'NA') as pan,nvl(nat_id_Card_num,'NA') as otherId,\r\n"
							+ "	nvl(cust_passport_no,'NA') as passport from aml_cust_master where cust_pan_no='"
							+doc_no+"'" + "or nat_id_Card_num='"+doc_no+"' or cust_passport_no='"+doc_no+"'";

					rs = stmt1.executeQuery(checkSQL);
					while (rs.next()) {
						System.out.println("Till Here Correct 3");
						if (rs.getString("cust_id") != null) {

						}
						matchSet.put(rs.getString("cust_id"), data_id + "~" + doc_no);
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
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (stmt1 != null) {
					stmt1.close();
					stmt1 = null;
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

	public ArrayList<String> getCustomerDetails(String key) {

		Statement stmt = null, stmt2 = null, stmt3 = null;

		ResultSet rsCust = null, rs2 = null, rs3 = null, rs4 = null;
		ArrayList<String> custDetails = new ArrayList<>();
		String query2 = null;
		String name = "";
		String id = "";
		String passport = "", pan = "", otherTd = "", branchId = "";
		String zoneId = "", custRisk = "";
		String cCountry = "", pCountry = "", correlationId = "";
		String dob = "";
		String alertFlg = "N";
		try {
			stmt = connection.createStatement();

			query2 = "select cust_id,cust_name,nvl(cust_pan_no,'NA') as pan,nvl(nat_id_Card_num,'NA') as otherId,\r\n"
					+ "(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=(select country from aml_cust_address where orgkey=a.cust_id and ADDRESSCATEGORY='Mailing' and rownum=1)) cCountry,\r\n"
					+ "(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=(select country from aml_cust_address where orgkey=a.cust_id and ADDRESSCATEGORY='Home' and rownum=1)) pCountry,\r\n"
					+ "nvl(cust_passport_no,'NA') as passport ,(select zone_id from aml_sol_mapping where sol_id=a.primary_sol_id) zoneId,primary_sol_id branchId, to_char(cust_dob, 'yyyy-mm-dd') dob from aml_cust_master a where a.cust_id='"
					+ key + "' ";
			rsCust = stmt.executeQuery(query2);
			while (rsCust.next()) {

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
				dob = rsCust.getString("dob");

				/*
				 * if (!passport.equalsIgnoreCase("NA")) { custDetails.add(passport); } if
				 * (!otherTd.equalsIgnoreCase("NA")) { custDetails.add(otherTd); } if
				 * (!pan.equalsIgnoreCase("NA")) { custDetails.add(pan); }
				 */
				if (!id.equalsIgnoreCase("NA")) {

					custDetails.add(id + "~" + zoneId + "~" + branchId);
				}

			}
			for (String custDetails1 : custDetails) {
				System.out.println("custDetails" + custDetails1);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rsCust != null) {
					rsCust.close();
					rsCust = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return custDetails;
	}

	public void insertLogs(String reqNo, long count, String alertFlg) {
		Statement stmt = null;
		PreparedStatement pstmt = null;

		ResultSet rsCust = null;
		String reverseId="";

		try {

			stmt = connection.createStatement();
			String query = "select distinct * from(select data_id , first_name||' '||second_name||' '||third_name name from aml_wl_entity union all select data_id ,last_name name from aml_wl_sdn\r\n"
					+ "union all select data_id ,first_name||' '||second_name||' '||third_name name from aml_wl_ind union all select data_id , alias_name name from aml_wl_entity_alias  union all select data_id , alias_name name from aml_wl_ind_alias union all select data_id , first_name||' '||last_name name from aml_wl_sdn_aka"
					+ " union all select data_id , name from fcra_internal_watchlist_data " + ") where data_id='"
					+reqNo.trim()+ "'";
			rsCust = stmt.executeQuery(query);

			while (rsCust.next()) {
				/*
				 * String filePath = ""; Properties reportProp = new Properties(); String dir =
				 * System.getProperty("user.dir"); InputStream is = new FileInputStream(dir +
				 * "/aml-config.properties"); // InputStream is = new FileInputStream(dir +
				 * "/Nmscr-aml-config.properties"); reportProp.load(is); is.close();
				 * 
				 * filePath = reportProp.getProperty("ReverseScreeningLogs");
				 * 
				 * String fileName = filePath + "ReverseScreeningLogs.txt"; PrintWriter out =
				 * null; File file = new File(fileName); if (!file.exists()) {
				 * file.createNewFile(); }
				 */
		
				

			

					/*
					 * Calendar cal = Calendar.getInstance();
					 * 
					 * Date date = cal.getTime(); SimpleDateFormat format1 = new
					 * SimpleDateFormat("dd-MMM-YYYY"); String date1 = format1.format(date); //
					 * Files.write(Paths.get("D:\\signup\\Rohit.txt"), s.getBytes(), //
					 * StandardOpenOption.APPEND); out = new PrintWriter(new BufferedWriter(new
					 * FileWriter(fileName, true))); out.println(count + "|" + reqNo + "|" +
					 * rsCust.getString("name") + "|" + date1 + "|" + "REV" + "|" + alertFlg);
					 * out.close();
					 */
				String name = rsCust.getString(2) == null || rsCust.getString(2) == "" ? "-"
						: rsCust.getString(2);
					LocalDate d1 = LocalDate.now();
					String currentTimestamp = d1.format(DateTimeFormatter.ofPattern("ddMMYYYY"));
					int m = (int) Math.pow(10, 6 - 1);
					long randNumFirst = m + new Random().nextInt(9 * m);
					reverseId= "REVID" + "/" + currentTimestamp + "/" + String.valueOf(randNumFirst);
					String indQuery = "insert into aml_reverse_screen_data(REVERSE_ID,PERSON_ID,PERSON_NAME,SCAN_DATE,SCREEN_TYPE,MATCH_FLG) "
							+ " values(?,?,?,sysdate,'REV',?)";
					pstmt = connection.prepareStatement(indQuery);
					pstmt.setString(1,reverseId );
					pstmt.setString(2, reqNo);
					pstmt.setString(3,name);
					pstmt.setString(4,alertFlg );
					pstmt.executeUpdate();
					pstmt.close();
					
				

			}
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rsCust != null) {
					rsCust.close();
					rsCust = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

	}

	public HashMap<String, String> scanByParamFuzzy(String name, String cCountry, String address, String dob,
			String id) {
		// System.out.println("Scan other parameters");
		HashMap<String, String> matchSet = new HashMap<String, String>();
		Statement stmt = null, stmt2 = null, stmt3 = null;

		ResultSet rs = null, rs2 = null, rs3 = null;
		double result = 0.0;
		double result1 = 0.0;
		double result3 = 0.0;
		String query2 = null, query3 = null, query4 = null;
		String mainAddress = "";
		String custId = "", custname = "";
		
		try {

			stmt = connection.createStatement();
			stmt2 = connection.createStatement();
			stmt3 = connection.createStatement();
			String query = "select nvl(cust_id,'-') cust_id,nvl(cust_name,'-') cust_name,utl_match.edit_distance_similarity(cust_name,'"+name.replace(",", "").toUpperCase().trim()+"') perc\r\n"
					+ "from aml_cust_master\r\n"
					+ "where utl_match.edit_distance_similarity(cust_name,'"+name.replace(",", "").toUpperCase() + "') >"+Integer.parseInt(threshold)+"";

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				System.out.println("Cust_id"+rs.getString(1)+"Percentage"+rs.getDouble(3));
				result =rs.getDouble(3);

				if (result > Integer.parseInt(threshold)) {

					custname = rs.getString("cust_name");
					custId = rs.getString("cust_id");
					String mainResult[] = getCustAddress(address.toUpperCase().trim(), custId).split("~");
					result3 = Double.parseDouble(mainResult[0]);
					mainAddress = mainResult[1];

					if (result3 > Integer.parseInt(threshold2)) {
						fuzzyVal = Math.max(result3, result1);
						matchSet.put(rs.getString("cust_id"), id + "|" + custname + "~" + mainAddress);
						return matchSet;

					}

					query2 = "select cust_id ,cCountry,pCountry from (select cust_id ,(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=(select country from aml_cust_address where orgkey=a.cust_id and ADDRESSCATEGORY='Mailing' and rownum=1)) cCountry,"
							+ "(select ref_desc from aml_rct where ref_rec_type='03' and ref_code=(select country from aml_cust_address where orgkey=a.cust_id and ADDRESSCATEGORY='Home' and rownum=1)) pCountry "
							+ "from aml_cust_master a where a.cust_id='" + custId + "') where cCountry='" + cCountry
							+ "' or " + "pCountry='" + cCountry + "' ";

					rs2 = stmt2.executeQuery(query2);

					while (rs2.next()) {
						System.out.println("Till Here Correct 9");
						query3 = "select cust_id,to_char(cust_dob, 'DD-MON-YY') dob from aml_cust_master a where cust_dob='"
								+dob+"' and cust_id='"+custId+"'";
						// System.out.println("query:" +query3);

						rs3 = stmt3.executeQuery(query3);
						while (rs3.next()) {
							fuzzyVal = Math.max(result, result1);
							matchSet.put(rs.getString("cust_id"), id + "|" + custname + "~" + cCountry + "~" + dob);
						}

					}

					// matchSet.put(rs.getString("data_id"), name);
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
				if (stmt2 != null) {
					stmt2.close();
					stmt2 = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (stmt3 != null) {
					stmt3.close();
					stmt3 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
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

	public void updateRequestStatus(String reqNo, String statusFlg) {
		Statement stmt = null;

		try {
			makeConnection();
			String sql = "update aml_rev_screen set scan_flg='" + statusFlg + "' where data_id='" + reqNo + "'";
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
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
			Thread t = new Thread(new LexisNexisReverseScreen());
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
			Thread t = new Thread(new LexisNexisReverseScreen());
			t.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
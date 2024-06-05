package com.idbi.intech.iaml.swift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.tool.AML_WLScan_Logic;
import com.idbi.intech.iaml.tool.FldDataSplit;

public class MsgScanDAO {
	Connection con_aml = null;
	ArrayList<String> al_category = null;
	FldDataSplit obj = new FldDataSplit();
	DecimalFormat df = new DecimalFormat("###.##");
	AML_WLScan_Logic obj1 = new AML_WLScan_Logic();
	ArrayList<String> arrNoise = new ArrayList<String>();
	boolean flgNoise = false;

	public void LoadDatabaseAml(String connectionUrl, String username,
			String password) {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLBPM(connectionUrl,
					username, password);
			// System.out.println(connectionUrl);
			con_aml.setAutoCommit(false);
			// System.out.println("Connection started");
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.exit(0);
		}
	}

	public void getNoiseWord() {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con_aml.createStatement();
			rs = stmt
					.executeQuery("select word from aml_noise_words where status = 'A'");
			while (rs.next()) {
				arrNoise.add(rs.getString(1));
			}
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

	@SuppressWarnings("unused")
	protected void getMessageScanned(String msgInfo) {
		Statement stmt_flds = null;
		Statement stmtMsg = null;
		PreparedStatement pstmt_data = null;
		Statement pstmt_nl = null;
		PreparedStatement pstmt_threshold = null;
		PreparedStatement pstmt_sdn_alert = null;
		PreparedStatement pstmt_susp = null;
		PreparedStatement pstmt_phenotic = null;
		PreparedStatement pstmt_process_stat = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		String id = "";
		String msgType = "";
		String swiftType = "";
		String fld_no = "";
		String dataTemp = "";
		String nl_query = "";
		String nl_id = "";
		String nl_name = "";
		String data = "";
		String display_result = "";
		String phono = "";
		String name = "";
		String scanTemp = "";
		Set<String> al_susp = null;
		ArrayList<String> al_MsgInfo = null;
		double threshold = 0.0;
		ArrayList<String> al_Data = null;
		StringTokenizer st = null;
		Set<String> nl_Set = null;
		ArrayList<String> al_Query = null;
		try {
			int alert = 0;
			al_Query = new ArrayList<String>();
			al_Query.add("SDN_CHK_FLG = 'Y'");
			// al_Query.add("FT_CHK = 'Y'");
			al_Query.add("SDN_CNTRY_CHK = 'Y'");
			al_Query.add("PORT_CHK = 'Y'");
			al_Query.add("VESSEL_CHK = 'Y'");
			// al_Query.add("RG_CHK = 'Y'");

			// nl_query =
			// "select trim(b.first_name||' '||c.last_name),a.person_id from aml_wc_record a,aml_wc_fname b,aml_wc_lname c where a.person_id=b.person_id and a.person_id=c.person_id and category = ? and a.person_id in (select person_id from aml_wc_country where COUNTRY in ('GHANA')";
			pstmt_nl = con_aml.createStatement();
			stmtMsg = con_aml.createStatement();
			pstmt_threshold = con_aml
					.prepareStatement("select VALUE from p_aml_general where name='SWIFT_SDN_MATCH'");
			pstmt_data = con_aml
					.prepareStatement("select fld_val from aml_swift_in_msg_txn where msg_id = ? and fld_no =?");
			pstmt_sdn_alert = con_aml
					.prepareStatement("insert into AML_SWIFT_MSG_ALERT_SDN(MSG_ID,MSG_TYPE,FLD_NO,"
							+ "PERSON_ID,MSG_WORD,WL_WORD,RESULT,GENERATED_TIME,LAST_CHANGE_USER,LAST_CHANGE_TIME,ALERT_STATUS,PHEONTIC_MATCH) values"
							+ "(?,?,?,?,?,?,?,sysdate,?,sysdate,?,?)");
			pstmt_susp = con_aml
					.prepareStatement("update aml_swift_msg_info set status = 'S',MSG_ALERT_FLG = 'Y' where msg_id = ?");
			pstmt_phenotic = con_aml
					.prepareStatement("select decode(count(*),'1','Y','N') from (select soundex(?) as wl from dual) a,(select soundex(?) as tkn from dual) b where a.wl=b.tkn");
			pstmt_process_stat = con_aml
					.prepareStatement("update aml_swift_msg_info set process_status = '4' where msg_id=?");
			stmt_flds = con_aml.createStatement();

			rs = pstmt_threshold.executeQuery();
			while (rs.next()) {
				threshold = Double.parseDouble(rs.getString(1));
			}

			alert = 0;

			String info[] = msgInfo.split("~");

			id = info[0];
			msgType = info[1];

			// System.out.println("Msg Id :: " + id);

			// System.out.println("Msg Type :: " + msgType);

			rs = stmtMsg
					.executeQuery("select decode(count(MSG_TYPE),0,'N','Y') from aml_swift_msg_type_scan where msg_type='"
							+ msgType + "'");
			while (rs.next()) {
				scanTemp = rs.getString(1);
			}

			if (scanTemp.equals("Y")) {
				for (String chkString : al_Query) {
					// System.out.println("1 check string : " + chkString);
					al_susp = new HashSet<String>();
					rs1 = stmt_flds
							.executeQuery("select fld_no from aml_swift_flds where msg_no = '"
									+ msgType
									+ "' and "
									+ chkString
									+ " and in_out_flg = (select swift_type from aml_swift_msg_info where msg_id = '"+id+"')");
					while (rs1.next()) {
						fld_no = rs1.getString(1);
						if (fld_no != null) {
							pstmt_data.setString(1, id);
							pstmt_data.setString(2, fld_no);
							rs2 = pstmt_data.executeQuery();
							while (rs2.next()) {
								dataTemp = rs2.getString(1);
								// System.out.println(dataTemp);
								if (dataTemp != null) {
									ArrayList<Set<String>> al_Set = null;
									if (chkString
											.equalsIgnoreCase("SDN_CHK_FLG = 'Y'"))
										nl_query = "SELECT TRIM (NAME), a.person_id FROM aml_wl_name a, aml_wl_orgsrc b,aml_wl_enttype c WHERE a.person_id = b.person_id and b.person_id=c.person_id AND source in ('INTERPOL','DOMESTIC TERRORISM','OFAC PART 561','OFAC 561 ENHANCEMENT','MOST WANTED TERRORISTS','OFAC', 'UNITED NATIONS','HM TREASURY','EU ENHANCEMENT','EUROPEAN UNION','SEEKING INFORMATION - WAR ON TERRORISM','IRAN SANCTIONS ACT','TEN MOST WANTED FUGITIVES','EXPORT CONTROL ORGANISATION UK - IRAN','HIJACKER SUSPECTS') and etype in ('Company','Individual','Minister/Government Official')";
									if (chkString
											.equalsIgnoreCase("SDN_CNTRY_CHK = 'Y'"))
										nl_query = "select trim(name),person_id from aml_wc_name where person_id in (select person_id from aml_wc_list where desc_3 in ('9','18')) and name <> 'INDIA'";
									if (chkString
											.equalsIgnoreCase("PORT_CHK = 'Y'"))
										nl_query = "SELECT TRIM (NAME), a.person_id FROM aml_wl_name a, aml_wl_orgsrc b,aml_wl_enttype c,aml_wl_list d WHERE a.person_id = b.person_id and b.person_id=c.person_id and c.person_id = d.person_id AND source in ('INTERPOL','DOMESTIC TERRORISM','OFAC PART 561','OFAC 561 ENHANCEMENT','MOST WANTED TERRORISTS','OFAC', 'UNITED NATIONS','HM TREASURY','EU ENHANCEMENT','EUROPEAN UNION','SEEKING INFORMATION - WAR ON TERRORISM','IRAN SANCTIONS ACT','TEN MOST WANTED FUGITIVES','EXPORT CONTROL ORGANISATION UK - IRAN','HIJACKER SUSPECTS','FATF DEFICIENT JURISDICTIONS','OFAC COUNTRY REGIMES') and etype in ('Government/Country','Principal City') and list in 'Restricted or Blocked Locations'";
									if (chkString
											.equalsIgnoreCase("VESSEL_CHK = 'Y'"))
										nl_query = "SELECT TRIM (NAME), a.person_id FROM aml_wl_name a, aml_wl_orgsrc b,aml_wl_enttype c,aml_wl_list d WHERE a.person_id = b.person_id and b.person_id=c.person_id and c.person_id = d.person_id AND source in ('INTERPOL','DOMESTIC TERRORISM','OFAC PART 561','OFAC 561 ENHANCEMENT','MOST WANTED TERRORISTS','OFAC', 'UNITED NATIONS','HM TREASURY','EU ENHANCEMENT','EUROPEAN UNION','SEEKING INFORMATION - WAR ON TERRORISM','IRAN SANCTIONS ACT','TEN MOST WANTED FUGITIVES','EXPORT CONTROL ORGANISATION UK - IRAN','HIJACKER SUSPECTS','FATF DEFICIENT JURISDICTIONS','OFAC COUNTRY REGIMES') and etype in ('Vessel')";

									// System.out.println("NL : " + nl_query);

									rs3 = pstmt_nl.executeQuery(nl_query);
									while (rs3.next()) {
										nl_name = rs3.getString(1);
										nl_id = rs3.getString(2);

										nl_Set = new HashSet<String>();

										st = new StringTokenizer(nl_name, " ");
										String negToken = "";
										while (st.hasMoreTokens()) {
											negToken = st.nextToken();
											if (negToken != null) {
												nl_Set.add(negToken);
											}
										}

										int cnt = nl_Set.size() - 1;

										al_Set = obj.splitFld(dataTemp, cnt);

										double lenNl = 0;

										for (String nltmp : nl_Set) {
											lenNl += nltmp.length();
										}

										for (Set<String> tSet : al_Set) {
											// to check whether the String has
											// qualified
											// length

											double lenCust = 0;

											double percentMatch = 0;

											for (String lenSt : tSet) {
												lenCust += lenSt.length();
											}

											// Calculating percent of match
											percentMatch = (lenCust / lenNl) * 100;

											// System.out.println(tSet);
											// Area for changing the logic the

											// System.out.println("percent :: "+percentMatch);

											if (percentMatch >= 85
													&& percentMatch <= 115) {

												boolean flgNoise = false;
												boolean preFlgNoise = false;
												boolean finalResult = false;

												// System.out
												// .println(arrNoise);

												for (String noise : arrNoise) {
													flgNoise = false;
													String[] noiseColl = (noise
															.trim()).split(" ");

													for (String noiseSplit : noiseColl) {
														boolean finalFlg = false;
														boolean prevFinal = true;
														boolean prevFlg = false;

														for (String word : tSet) {
															finalFlg = prevFlg
																	|| (noiseSplit
																			.equalsIgnoreCase(word
																					.trim()));
															prevFlg = finalFlg;
														}
														flgNoise = finalFlg
																&& prevFinal;
														prevFinal = finalFlg;
													}

													finalResult = flgNoise
															|| preFlgNoise;

													preFlgNoise = finalResult;
												}

												// System.out
												// .println("FinalResult :: "
												// + finalResult);

												double result = 0;
											//	System.out
												//		.println("NL List :: "+nl_Set);
												//System.out
												//.println("tList :: "+tSet);

												if (!finalResult) {
													result = obj1.scanName(
															nl_Set, tSet,
															0);
												}

												if (result >= threshold) {
													pstmt_phenotic.setString(1,
															nl_name);
													pstmt_phenotic.setString(2,
															name.trim());
													rs4 = pstmt_phenotic
															.executeQuery();
													while (rs4.next()) {
														phono = rs4
																.getString(1);
													}

													display_result = df
															.format(result);
													name = "";
													for (String var : tSet) {
														name += var + " ";
													}
													al_susp.add(nl_id
															+ "~"
															+ name.trim()
																	.toUpperCase()
															+ "~"
															+ display_result
															+ "~"
															+ nl_name.trim()
															+ "~" + fld_no);
												}
											}
										}
									}

								}
								// end here
							}
						}
					}
					// System.out.println("Alert Count ::: " + al_susp.size());
					if (al_susp.size() > 0) {
						for (String result : al_susp) {
							String temp[] = result.split("~");
							pstmt_sdn_alert.setString(1, id);
							pstmt_sdn_alert.setString(2, msgType);
							pstmt_sdn_alert.setString(3, temp[4]);
							pstmt_sdn_alert.setString(4, temp[0]);
							pstmt_sdn_alert.setString(5, temp[1]);
							pstmt_sdn_alert.setString(6, temp[3]);
							pstmt_sdn_alert.setString(7, temp[2]);
							pstmt_sdn_alert.setString(8, "SYSTEM");
							pstmt_sdn_alert.setString(9, "O");
							pstmt_sdn_alert.setString(10, phono);

							pstmt_sdn_alert.executeUpdate();
						}
						if (alert == 0) {
							alert = 1;
							pstmt_susp.setString(1, id);
							pstmt_susp.executeUpdate();
						}
					}
				}
			}

			pstmt_process_stat.setString(1, id);
			pstmt_process_stat.executeUpdate();
			con_aml.commit();

			System.out.println("Msg Id :: " + id + "Msg Type :: " + msgType);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt_flds != null) {
					stmt_flds.close();
					stmt_flds = null;
				}
				if (pstmt_data != null) {
					pstmt_data.close();
					pstmt_data = null;
				}
				if (pstmt_nl != null) {
					pstmt_nl.close();
					pstmt_nl = null;
				}
				if (pstmt_threshold != null) {
					pstmt_threshold.close();
					pstmt_threshold = null;
				}
				if (pstmt_sdn_alert != null) {
					pstmt_sdn_alert.close();
					pstmt_sdn_alert = null;
				}
				if (pstmt_susp != null) {
					pstmt_susp.close();
					pstmt_susp = null;
				}
				if (pstmt_phenotic != null) {
					pstmt_phenotic.close();
					pstmt_phenotic = null;
				}
				if (pstmt_process_stat != null) {
					pstmt_process_stat.close();
					pstmt_process_stat = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}
				if (rs4 != null) {
					rs4.close();
					rs4 = null;
				}
				if (stmtMsg != null) {
					stmtMsg.close();
					stmtMsg = null;
				}
				if (con_aml != null) {
					con_aml.close();
					con_aml = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}

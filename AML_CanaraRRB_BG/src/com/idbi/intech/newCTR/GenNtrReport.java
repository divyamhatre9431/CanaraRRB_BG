package com.idbi.intech.newCTR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

public class GenNtrReport {

	private static int srl = 1;

	private static Connection conn;

	ArrayList<String> arrState = null;

	public GenNtrReport(Connection conn) {
		this.conn = conn;
	}

	public String checkReport(String date) {
		Statement stmt = null;
		ResultSet rs = null;
		String flag = "";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"select decode(count(1),0,'N','Y') from ntr_ctrlfile where month_of_record=to_char(to_date('" + date
							+ "','dd-mm-yy'),'MM') and year_of_record=to_char(to_date('" + date
							+ "','dd-mm-yy'),'YYYY')");
			while (rs.next()) {
				flag = rs.getString(1);
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return flag;
	}

	public String genNtrSeq(String date) {

		Statement stmt = null;
		ResultSet rs = null;
		String ntrseq = "";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select to_char(to_date('" + date + "','dd-mm-yy'),'MMYY') from dual");
			while (rs.next()) {
				ntrseq = rs.getString(1);
			}
			System.out.println("NTR SeqNo is :" + ntrseq);
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ntrseq;
	}

	public void genNtrDetails(String fromdate, String enddate, String ntrseq, String userId) {

		CallableStatement callstmt = null;
		try {
			// NTR Details
			System.out.println("Generating NTR Detail...");
			callstmt = conn.prepareCall("call gen_ntr_details('" + fromdate + "','" + enddate + "')");
			callstmt.execute();
			System.out.println("NTR Detail Generated...");

			// NTR Transaction
			System.out.println("Generating NTR Transaction File...");
			callstmt = conn.prepareCall("call gen_ntr_tran('" + ntrseq + "')");
			callstmt.execute();
			System.out.println("NTR Transaction Generated...");

			// NTR Branch
			System.out.println("Generating NTR Branch File...");
			callstmt = conn.prepareCall("call gen_ntr_branch('" + fromdate + "','" + enddate + "','" + ntrseq + "')");
			callstmt.execute();
			System.out.println("NTR Branch Generated...");

			// NTR Accounts
			System.out.println("Generating NTR Account File...");
			callstmt = conn.prepareCall("call gen_ntr_ac('" + fromdate + "','" + enddate + "','" + ntrseq + "')");
			callstmt.execute();
			System.out.println("NTR Accounts Generated...");

			// NTR Person Details
			System.out.println("Generating NTR PersonDetails File...");
			callstmt = conn
					.prepareCall("call gen_ntr_persondtls('" + fromdate + "','" + enddate + "','" + ntrseq + "')");
			callstmt.execute();
			System.out.println("NTR PersonDetails Generated...");

			// NTR Control Details
			System.out.println("Generating NTR Control File...");
			callstmt = conn.prepareCall(
					"call gen_ntr_ctrl('" + fromdate + "','" + enddate + "','" + ntrseq + "','" + userId + "')");
			callstmt.execute();
			System.out.println("NTR ControlFile Generated...");

			// NTR Missing Branch Details
			System.out.println("Updating Missing Branch for NTR...");
			callstmt = conn
					.prepareCall("call gen_ntr_branch_missing('" + fromdate + "','" + enddate + "','" + ntrseq + "')");
			callstmt.execute();
			System.out.println("NTR Missing Branch Updated...");

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (callstmt != null) {
					callstmt.close();
					callstmt = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public int updateAccountFlg(String ntrseq) {
		Statement stmt = null;
		int cnt = 0;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("update ntr_account set process_flg='N' where ntr_seq_no='" + ntrseq + "'");
			System.out.println("Updated NTR Account Process Flag...");
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {

				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return cnt;
	}

	public void delNtrDetails(String fromdate, String enddate, String ntrseq, String userId) {

		CallableStatement callstmt = null;
		try {
			System.out.println("Deleteing NTR Details...");
			callstmt = conn.prepareCall(
					"call del_ntr_details('" + fromdate + "','" + enddate + "','" + ntrseq + "','" + userId + "')");
			callstmt.execute();
			System.out.println("NTR Detail Deleted...");

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (callstmt != null) {
					callstmt.close();
					callstmt = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public String checkSeqNo(String ntrseq) {

		Statement stmt = null;
		ResultSet rs = null;
		String flag = "";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"select decode(count(1),0,'N','Y') from ntr_ctrlfile where ntr_seq_no='" + ntrseq + "'");
			while (rs.next()) {
				flag = rs.getString(1);
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return flag;
	}

	public int getAccountCount(String ntrseq) {
		Statement stmt = null;
		ResultSet rs = null;
		int cnt = 0;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(1) from ntr_account where ntr_seq_no='" + ntrseq + "'");
			while (rs.next()) {
				cnt = rs.getInt(1);
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return cnt;
	}

	public void getRegState() {
		Statement stmt = null;
		ResultSet rs = null;
		arrState = new ArrayList<String>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select state_code from aml_regulatory_state_master");
			while (rs.next()) {
				arrState.add(rs.getString(1));
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public void createXmlFile(String ntrseq, int filecount) throws FileNotFoundException, IOException {

		Statement stmt = null;
		Statement acStmt = null;
		Statement brStmt = null;
		Statement trStmt = null;
		Statement indStmt = null;
		Statement lglStmt = null;
		Statement totStmt = null;
		Statement acProcess = null;
		ResultSet rs = null;
		ResultSet rsAc = null;
		ResultSet rsBr = null;
		ResultSet rsTr = null;
		ResultSet rsInd = null;
		ResultSet rsLgl = null;
		ResultSet rsTot = null;
		String bankName = "";
		String bankCetegory = "";
		String fiureid = "";
		String poName = "";
		String poDesg = "";
		String poAddress = "";
		String poCity = "";
		String poState = "";
		String poCountry = "";
		String poPin = "";
		String poTel = "";
		String poMobile = "";
		String poFax = "";
		String poEmail = "";
		String poReg = "";
		String batchDate = "";
		String month = "";
		String year = "";
		String acNo = "";
		String brRefNo = "";
		Properties amlProp = new Properties();
		String dir = System.getProperty("user.dir");
		// System.out.println("Current Directory::"+dir);
		InputStream is = new FileInputStream(dir + "/aml-config.properties");
		amlProp.load(is);
		is.close();
		String fileDir = amlProp.getProperty("NTRDIR");

		StringBuffer sb = null;
		String finalXml = "";
		String outfileDir = "";
		String fileName = "";
		// int filecount = 1;
		String batchNum = ntrseq;

		int i = srl;
		File file = null;
		FileOutputStream writeXml = null;
		getRegState();

		try {
			stmt = conn.createStatement();
			totStmt = conn.createStatement();
			acStmt = conn.createStatement();
			brStmt = conn.createStatement();
			trStmt = conn.createStatement();
			indStmt = conn.createStatement();
			lglStmt = conn.createStatement();
			acProcess = conn.createStatement();
			rsTot = totStmt.executeQuery(
					"select to_char(sysdate,'YYYY-MM-DD') batchDate,month_of_record,year_of_record from ntr_ctrlfile where ntr_seq_no='"
							+ ntrseq + "'");
			while (rsTot.next()) {
				batchDate = rsTot.getString(1);
				month = rsTot.getString(2);
				year = rsTot.getString(3);
			}
			rs = stmt.executeQuery(
					"select comp_name_of_bank,bank_cetegory,fiureid,po_name,po_designation,po_address,po_city,po_state,po_country,po_pin,po_tel,po_mobile,po_fax,po_email,po_rereg from po_details");
			while (rs.next()) {
				bankName = rs.getString("comp_name_of_bank");
				bankCetegory = rs.getString("bank_cetegory");
				fiureid = rs.getString("fiureid");
				poName = rs.getString("po_name");
				poDesg = rs.getString("po_designation");
				poAddress = rs.getString("po_address");
				poCity = rs.getString("po_city");
				poState = rs.getString("po_state");
				poCountry = rs.getString("po_country");
				poPin = rs.getString("po_pin");
				poTel = rs.getString("po_tel");
				poMobile = rs.getString("po_mobile");
				poFax = rs.getString("po_fax");
				poEmail = rs.getString("po_email");
				poReg = rs.getString("po_rereg");
			}
			sb = new StringBuffer();
			fileName = "NTR_ARF" + batchNum + "_" + filecount++ + ".xml";
			outfileDir = fileDir + fileName;
			file = new File(outfileDir);
			writeXml = new FileOutputStream(file);

			boolean poFlg = true;

			rsAc = acStmt.executeQuery(
					"select branch_ref_no,account_no,account_name,type_of_ac,type_of_acholder,acct_opn_date,risk_rating,"
							+ " nvl(cumm_cr_tot,0)as cumm_cr_tot,nvl(cumm_dr_tot,0) as cumm_dr_tot,nvl(cumm_cs_cr_tot,0) as cumm_cs_cr_tot,nvl(cumm_cs_dr_tot,0) as cumm_cs_dr_tot,acct_status,person_name "
							+ " from ntr_account" + " where ntr_seq_no='" + ntrseq
							+ "' and process_flg = 'N' and rownum<2001");
			while (rsAc.next()) {
				if (poFlg) {
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					sb.append("<Batch>\n");
					sb.append("<ReportType>NTR</ReportType>\n");
					sb.append("<ReportFormatType>ARF</ReportFormatType>\n");
					sb.append("<BatchHeader>\n");
					sb.append("<DataStructureVersion>2</DataStructureVersion>\n");
					sb.append("<GenerationUtilityVersion></GenerationUtilityVersion>\n");
					sb.append("<DataSource>xml</DataSource>\n");
					sb.append("</BatchHeader>\n");
					sb.append("<ReportingEntity>\n");
					sb.append("<ReportingEntityName>" + bankName + "</ReportingEntityName>\n");
					sb.append("<ReportingEntityCategory>" + bankCetegory + "</ReportingEntityCategory>\n");
					sb.append("<RERegistrationNumber>" + poReg + "</RERegistrationNumber>\n");
					sb.append("<FIUREID>" + fiureid + "</FIUREID>\n");
					sb.append("</ReportingEntity>\n");
					sb.append("<PrincipalOfficer>\n");
					sb.append("<POName>" + poName + "</POName>\n");
					sb.append("<PODesignation>" + poDesg + "</PODesignation>\n");
					sb.append("<POAddress>\n");
					sb.append("<Address>" + poAddress + "</Address>\n");
					sb.append("<City>" + poCity + "</City>\n");
					sb.append("<StateCode>" + poState + "</StateCode>\n");
					sb.append("<PinCode>" + poPin + "</PinCode>\n");
					sb.append("<CountryCode>" + poCountry + "</CountryCode>\n");
					sb.append("</POAddress>\n");
					sb.append("<POPhone>\n");
					sb.append("<Telephone>" + poTel + "</Telephone>\n");
					sb.append("<Mobile>" + poMobile + "</Mobile>\n");
					sb.append("<Fax>" + poFax + "</Fax>\n");
					sb.append("</POPhone>\n");
					sb.append("<POEmail>" + poEmail + "</POEmail>\n");
					sb.append("</PrincipalOfficer>\n");
					sb.append("<BatchDetails>\n");
					sb.append("<BatchNumber>" + batchNum + "</BatchNumber>\n");
					sb.append("<BatchDate>" + batchDate + "</BatchDate>\n");
					sb.append("<MonthOfReport>" + month + "</MonthOfReport>\n");
					sb.append("<YearOfReport>" + year + "</YearOfReport>\n");
					sb.append("<OperationalMode>P</OperationalMode>\n");
					sb.append("<BatchType>N</BatchType>\n");
					sb.append("<OriginalBatchID>0</OriginalBatchID>\n");
					sb.append("<ReasonOfRevision>N</ReasonOfRevision>\n");
					sb.append("<PKICertificateNum></PKICertificateNum>\n");
					sb.append("</BatchDetails>\n");
				}

				poFlg = false;

				brRefNo = rsAc.getString("branch_ref_no");
				acNo = rsAc.getString("account_no");
				sb.append("<Report>\n");
				sb.append("<ReportSerialNum>" + i + "</ReportSerialNum>\n");
				sb.append("<OriginalReportSerialNum>0</OriginalReportSerialNum>\n");
				sb.append("<MainPersonName>" + (rsAc.getString("person_name").length() > 75
						? rsAc.getString("person_name").substring(0, 75) : rsAc.getString("person_name"))
						+ "</MainPersonName>\n");
				sb.append("<Account>\n");
				sb.append("<AccountDetails>\n");
				sb.append("<AccountNumber>" + rsAc.getString("account_no") + "</AccountNumber>\n");
				sb.append("<AccountType>" + rsAc.getString("type_of_ac") + "</AccountType>\n");
				sb.append("<HolderName>"
						+ (rsAc.getString("account_name").length() > 75
								? rsAc.getString("account_name").substring(0, 74) : rsAc.getString("account_name").trim())
						+ "</HolderName>\n");
				sb.append("<AccountHolderType>" + rsAc.getString("type_of_acholder") + "</AccountHolderType>\n");
				sb.append("<AccountStatus>" + rsAc.getString("acct_status") + "</AccountStatus>\n");
				sb.append("<DateOfOpening>" + rsAc.getString("acct_opn_date") + "</DateOfOpening>\n");
				sb.append("<RiskRating>" + rsAc.getString("risk_rating") + "</RiskRating>\n");
				sb.append(
						"<CumulativeCreditTurnover>" + rsAc.getString("cumm_cr_tot") + "</CumulativeCreditTurnover>\n");
				sb.append("<CumulativeDebitTurnover>" + rsAc.getString("cumm_dr_tot") + "</CumulativeDebitTurnover>\n");
				sb.append("<CumulativeCashDepositTurnover>" + rsAc.getString("cumm_cs_cr_tot")
						+ "</CumulativeCashDepositTurnover>\n");
				sb.append("<CumulativeCashWithdrawalTurnover>" + rsAc.getString("cumm_cs_dr_tot")
						+ "</CumulativeCashWithdrawalTurnover>\n");
				sb.append("<NoTransactionsTobeReported>N</NoTransactionsTobeReported>\n");
				sb.append("</AccountDetails>\n");

				rsBr = brStmt.executeQuery(
						"select branch_ref_no,branch_name,branch_address,branch_city,branch_state,branch_pincode,branch_telephone,branch_fax,branch_email from ntr_branch where ntr_seq_no='"
								+ ntrseq + "' and branch_ref_no='" + brRefNo + "' and rownum <=1");

				while (rsBr.next()) {
					sb.append("<Branch>\n");
					sb.append("<BranchRefNumType>R</BranchRefNumType>\n");
					sb.append("<BranchRefNum>" + rsBr.getString("branch_ref_no") + "</BranchRefNum>\n");
					sb.append("<BranchDetails>\n");
					sb.append("<BranchName>" + rsBr.getString("branch_name") + "</BranchName>\n");
					sb.append("<BranchAddress>\n");
					sb.append("<Address>" + rsBr.getString("branch_address") + "</Address>\n");
					sb.append("<City>" + rsBr.getString("branch_city") + "</City>\n");
					sb.append("<StateCode>" + (arrState.contains(rsBr.getString("branch_state"))
							? rsBr.getString("branch_state") : "XX") + "</StateCode>\n");
					sb.append("<PinCode>" + rsBr.getString("branch_pincode") + "</PinCode>\n");
					sb.append("<CountryCode>IN</CountryCode>\n");
					sb.append("</BranchAddress>\n");
					sb.append("<BranchPhone>\n");
					sb.append("<Telephone>");
					sb.append(rsBr.getString("branch_telephone") == null ? "" : rsBr.getString("branch_telephone"));
					sb.append("</Telephone>\n");
					sb.append("<Mobile></Mobile>\n");
					sb.append("<Fax>");
					sb.append(rsBr.getString("branch_fax") == null ? "" : rsBr.getString("branch_fax"));
					sb.append("</Fax>\n");
					sb.append("</BranchPhone>\n");
					sb.append("<BranchEmail>");
					sb.append(rsBr.getString("branch_email") == null ? "" : rsBr.getString("branch_email"));
					sb.append("</BranchEmail>\n");
					sb.append("</BranchDetails>\n");
					sb.append("</Branch>\n");
				}

				rsInd = indStmt.executeQuery(
						"select distinct full_name,cust_id,relation_flg,occupation,date_of_birth,cust_sex,type_of_id,id_no,issuing_authority,place_of_issue,commu_addr,commu_city,commu_state,commu_cntry,commu_pin,commu_tel,commu_mb_no,pan,father_spouse_name from ntr_individual where ntr_seq_no='"
								+ ntrseq + "' and account_no='" + acNo + "'");
				while (rsInd.next()) {
					sb.append("<PersonDetails>\n");
					//sb.append("<PersonName>" + rsInd.getString("full_name").trim().replace("&", "") + "</PersonName>\n");
					sb.append("<PersonName>" + (rsInd.getString("full_name") == null ? "XXXXXXXXXXXXXXX" : rsInd.getString("full_name").trim().replace("&", "")) + "</PersonName>\n");
					sb.append("<CustomerId>");
					sb.append(rsInd.getString("cust_id") == null ? "" : rsInd.getString("cust_id"));
					sb.append("</CustomerId>\n");
					sb.append("<RelationFlag>" + rsInd.getString("relation_flg") + "</RelationFlag>\n");
					sb.append("<CommunicationAddress>\n");
					sb.append("<Address>");
					sb.append(rsInd.getString("commu_addr") == null ? "XX"
							: rsInd.getString("commu_addr").trim().equals("") ? "XX"
									: rsInd.getString("commu_addr").replace("&", " "));
					sb.append("</Address>\n");
					sb.append("<City>");
					sb.append(rsInd.getString("commu_city") == null ? "XX" : rsInd.getString("commu_city"));
					sb.append("</City>\n");
					sb.append("<StateCode>");
					sb.append(rsInd.getString("commu_state") == null ? "XX"
							: arrState.contains(rsInd.getString("commu_state")) ? rsInd.getString("commu_state")
									: "XX");
					sb.append("</StateCode>\n");
					sb.append("<PinCode>");
					sb.append(rsInd.getString("commu_pin") == null ? "" : rsInd.getString("commu_pin"));
					sb.append("</PinCode>\n");
					sb.append("<CountryCode>");
					//sb.append(rsInd.getString("commu_cntry") == null || rsInd.getString("commu_cntry").equals("*") ? "XX" : rsInd.getString("commu_cntry"));
					sb.append(rsInd.getString("commu_cntry") == null || rsInd.getString("commu_cntry").equals("*") || rsInd.getString("commu_cntry").equals(".") ? "XX" : rsInd.getString("commu_cntry"));
					sb.append("</CountryCode>\n");
					sb.append("</CommunicationAddress>\n");
					sb.append("<Phone>\n");
					sb.append("<Telephone>");
					sb.append(rsInd.getString("commu_tel") == null || rsInd.getString("commu_tel").contains("X") || rsInd.getString("commu_tel").contains("+") ? "00000000000" : rsInd.getString("commu_tel"));
					sb.append("</Telephone>\n");
					sb.append("<Mobile>");
					sb.append(rsInd.getString("commu_mb_no") == null ? "" : rsInd.getString("commu_mb_no"));
					sb.append("</Mobile>\n");
					sb.append("<Fax></Fax>\n");
					sb.append("</Phone>\n");
					sb.append("<Email></Email>\n");
					/*
					 * sb.append("<SecondAddress>\n");
					 * sb.append("<Address></Address>\n");
					 * sb.append("<City></City>\n");
					 * sb.append("<StateCode></StateCode>\n");
					 * sb.append("<PinCode></PinCode>\n");
					 * sb.append("<CountryCode></CountryCode>\n");
					 * sb.append("</SecondAddress>\n");
					 */
					
					/*if (rsInd.getString("pan") == null || rsInd.getString("pan").equals("PANNOTREQD")
							|| rsInd.getString("pan").equals("PANINVALID") || rsInd.getString("pan").equals("FORM60")
							|| rsInd.getString("pan").equals("PANNOTAVBL")) {
						sb.append("<PAN></PAN>\n");
					} else {
						sb.append("<PAN>");
						sb.append(rsInd.getString("pan") == null || rsInd.getString("pan").length() < 10 ? "" : rsInd.getString("pan"));
						sb.append("</PAN>\n");
					}*/
					
					if (rsInd.getString("pan") != null && !rsInd.getString("pan").equals("null")
							&& !rsInd.getString("pan").equals("NA") && rsInd.getString("pan").length() == 10
							&& !rsInd.getString("pan").contains("XXX") && rsInd.getString("pan").matches(".*[a-zA-Z]+.*")) {
						sb.append("<PAN>");
						sb.append(rsInd.getString("pan").replaceAll("\\s", ""));
						sb.append("</PAN>\n");
					} else {
						sb.append("<PAN></PAN>\n");
					}
					
					sb.append("<UIN></UIN>\n");
					sb.append("<Individual>\n");
					sb.append("<Gender>");
					sb.append(rsInd.getString("cust_sex") == null || rsInd.getString("cust_sex").equals("T") ? "X" : rsInd.getString("cust_sex"));
					sb.append("</Gender>\n");
					if (rsInd.getString("date_of_birth") != null) {
						sb.append("<DateOfBirth>" + rsInd.getString("date_of_birth") + "</DateOfBirth>\n");
					}
					if (rsInd.getString("id_no") == null || rsInd.getString("id_no").equals("PANNOTREQD")
							|| rsInd.getString("id_no").equals("PANINVALID")
							|| rsInd.getString("id_no").equals("FORM60")
							|| rsInd.getString("id_no").equals("PANNOTAVBL")) {
						sb.append("<IdentificationType>Z</IdentificationType>\n");
						sb.append("<IdentificationNumber></IdentificationNumber>\n");
					} else {
						sb.append("<IdentificationType>");
						sb.append(rsInd.getString("type_of_id") == null ? "Z" : rsInd.getString("type_of_id"));
						sb.append("</IdentificationType>\n");
						sb.append("<IdentificationNumber>");
						sb.append(rsInd.getString("id_no") == null ? "" : rsInd.getString("id_no"));
						sb.append("</IdentificationNumber>\n");
					}
					sb.append("<IssuingAuthority></IssuingAuthority>\n");
					sb.append("<PlaceOfIssue></PlaceOfIssue>\n");
					sb.append("<Nationality>IN</Nationality>\n");
					sb.append("<PlaceOfWork></PlaceOfWork>\n");
					sb.append("<FatherOrSpouse>"+(rsInd.getString("father_spouse_name") == null ? "" : rsInd.getString("father_spouse_name"))+"</FatherOrSpouse>\n");
					sb.append("<Occupation>");
					sb.append(
							rsInd.getString("occupation") == null ? "OTHER SERVICES"
									: rsInd.getString("occupation").length() > 45
											? rsInd.getString("occupation").substring(0, 45)
											: rsInd.getString("occupation"));
					sb.append("</Occupation>\n");
					sb.append("</Individual>\n");
					sb.append("</PersonDetails>\n");
				}

				rsLgl = lglStmt.executeQuery(
						"select distinct full_name,cust_id,relation_flg,commu_addr,commu_city,commu_state,commu_cntry,commu_pin,commu_tel,pan,type_of_const,date_of_incorp,nature_of_buss,substr(reg_no,1,18) reg_no,substr(reg_place,1,18) reg_place from ntr_legalperson where ntr_seq_no='"
								+ ntrseq + "' and account_no='" + acNo + "'");
				while (rsLgl.next()) {
					sb.append("<PersonDetails>\n");
					//sb.append("<PersonName>" + rsLgl.getString("full_name").trim().replace("&", "") + "</PersonName>\n");
					sb.append("<PersonName>" + (rsLgl.getString("full_name") == null ? "XXXXXXXXXXXXXXX" : rsLgl.getString("full_name").trim().replace("&", "")) + "</PersonName>\n");
					sb.append("<CustomerId>");
					sb.append(rsLgl.getString("cust_id") == null ? "" : rsLgl.getString("cust_id"));
					sb.append("</CustomerId>\n");
					sb.append("<RelationFlag>" + rsLgl.getString("relation_flg") + "</RelationFlag>\n");
					sb.append("<CommunicationAddress>\n");
					sb.append("<Address>");
					sb.append(rsLgl.getString("commu_addr") == null ? "XX"
							: rsLgl.getString("commu_addr").trim().equals("") ? "XX"
									: rsLgl.getString("commu_addr").replace("&", " "));
					sb.append("</Address>\n");
					sb.append("<City>");
					sb.append(rsLgl.getString("commu_city") == null ? "" : rsLgl.getString("commu_city"));
					sb.append("</City>\n");
					sb.append("<StateCode>");
					sb.append(rsLgl.getString("commu_state") == null ? "XX"
							: arrState.contains(rsLgl.getString("commu_state")) ? rsLgl.getString("commu_state")
									: "XX");
					sb.append("</StateCode>\n");
					sb.append("<PinCode>");
					sb.append(rsLgl.getString("commu_pin") == null ? "" : rsLgl.getString("commu_pin"));
					sb.append("</PinCode>\n");
					sb.append("<CountryCode>");
					sb.append(rsLgl.getString("commu_cntry") == null || rsLgl.getString("commu_cntry").equals("*") || rsLgl.getString("commu_cntry").equals(".") ? "XX" : rsLgl.getString("commu_cntry"));
					sb.append("</CountryCode>\n");
					sb.append("</CommunicationAddress>\n");
					sb.append("<Phone>\n");
					sb.append("<Telephone>");
					sb.append(rsLgl.getString("commu_tel") == null || rsLgl.getString("commu_tel").contains("X") || rsLgl.getString("commu_tel").contains("+") ? "00000000000" : rsLgl.getString("commu_tel"));
					sb.append("</Telephone>\n");
					sb.append("<Mobile></Mobile>\n");
					sb.append("<Fax></Fax>\n");
					sb.append("</Phone>\n");
					sb.append("<Email></Email>\n");
					/*
					 * sb.append("<SecondAddress>\n");
					 * sb.append("<Address></Address>\n");
					 * sb.append("<City></City>\n");
					 * sb.append("<StateCode></StateCode>\n");
					 * sb.append("<PinCode></PinCode>\n");
					 * sb.append("<CountryCode></CountryCode>\n");
					 * sb.append("</SecondAddress>\n");
					 */
					
					/*if (rsLgl.getString("pan") == null || rsLgl.getString("pan").equals("PANNOTREQD")
							|| rsLgl.getString("pan").equals("PANINVALID") || rsLgl.getString("pan").equals("FORM60")
							|| rsLgl.getString("pan").equals("PANNOTAVBL")) {
						sb.append("<PAN></PAN>\n");
					} else {
						sb.append("<PAN>");
						sb.append(rsLgl.getString("pan") == null || rsLgl.getString("pan").length() < 10 ? "" : rsLgl.getString("pan"));
						sb.append("</PAN>\n");
					}*/
					
					if (rsLgl.getString("pan") != null && !rsLgl.getString("pan").equals("null")
							&& !rsLgl.getString("pan").equals("NA") && rsLgl.getString("pan").length() == 10
							&& !rsLgl.getString("pan").contains("XXX") && rsLgl.getString("pan").matches(".*[a-zA-Z]+.*")) {
						sb.append("<PAN>");
						sb.append(rsLgl.getString("pan").replaceAll("\\s", ""));
						sb.append("</PAN>\n");
					} else {
						sb.append("<PAN></PAN>\n");
					}
					
					sb.append("<UIN></UIN>\n");
					sb.append("<LegalPerson>\n");
					sb.append("<ConstitutionType>");
					String typeOfConstitution = rsLgl.getString("type_of_const") == null ? "Z"
							: rsLgl.getString("type_of_const");
					sb.append(typeOfConstitution);
					sb.append("</ConstitutionType>\n");
					if (rsLgl.getString("reg_no") != null) {
						sb.append("<RegistrationNumber>" + rsLgl.getString("reg_no")
								+ "</RegistrationNumber>\n");
					}
					else
					{
						sb.append("<RegistrationNumber></RegistrationNumber>\n");
					}
					if (rsLgl.getString("date_of_incorp") != null) {
						sb.append("<DateOfIncorporation>" + rsLgl.getString("date_of_incorp")
								+ "</DateOfIncorporation>\n");
					}
					if (rsLgl.getString("reg_place") != null) {
						sb.append("<PlaceOfRegistration>" + rsLgl.getString("reg_place")
								+ "</PlaceOfRegistration>\n");
					}
					else
					{
						sb.append("<PlaceOfRegistration></PlaceOfRegistration>\n");
					}
					sb.append("<CountryCode>");
					sb.append(rsLgl.getString("commu_cntry") == null ? "XX" : rsLgl.getString("commu_cntry"));
					sb.append("</CountryCode>\n");
					sb.append("<NatureOfBusiness>");
					sb.append(rsLgl.getString("nature_of_buss") == null ? ""
							: rsLgl.getString("nature_of_buss"));
					sb.append("</NatureOfBusiness>\n");
					sb.append("</LegalPerson>\n");
					sb.append("</PersonDetails>\n");
				}
				rsTr = trStmt.executeQuery(
						"select tran_id,tran_date,mode_of_tran,cre_deb_flg,tran_amount,tran_crncy,tran_remarks from ntr_transaction where ntr_seq_no='"
								+ ntrseq + "' and account_no='" + acNo + "'");
				while (rsTr.next()) {
					sb.append("<Transaction>\n");
					sb.append("<DateOfTransaction>" + rsTr.getString("tran_date") + "</DateOfTransaction>\n");
					sb.append("<TransactionID>" + rsTr.getString("tran_id") + "</TransactionID>\n");
					sb.append("<TransactionMode>" + rsTr.getString("mode_of_tran") + "</TransactionMode>\n");
					sb.append("<DebitCredit>" + rsTr.getString("cre_deb_flg") + "</DebitCredit>\n");
					sb.append("<Amount>" + rsTr.getString("tran_amount") + "</Amount>\n");
					sb.append("<Currency>" + rsTr.getString("tran_crncy") + "</Currency>\n");
					/*
					 * sb.append("<ProductTransaction>\n");
					 * sb.append("<ProductType></ProductType>\n");
					 * sb.append("<Identifier></Identifier>\n");
					 * sb.append("<TransactionType></TransactionType>\n");
					 * sb.append("<Units></Units>\n");
					 * sb.append("<Rate></Rate>\n");
					 * sb.append("</ProductTransaction>\n");
					 * sb.append("<DispositionOfFunds></DispositionOfFunds>\n");
					 * sb.append("<RelatedAccountNum></RelatedAccountNum>\n");
					 * sb .append(
					 * "<RelatedInstitutionName></RelatedInstitutionName>\n" );
					 * sb.append(
					 * "<RelatedInstitutionRefNum></RelatedInstitutionRefNum>\n"
					 * );
					 */
					sb.append("<Remarks>");
					sb.append(rsTr.getString("tran_remarks").length() > 42 ? rsTr.getString("tran_remarks").substring(0, 40).replaceAll("&amp;", "") : rsTr.getString("tran_remarks").replaceAll("[-+.^:,/<>]", " "));
					sb.append("</Remarks>\n");
					sb.append("</Transaction>\n");
				}
				sb.append("</Account>\n");
				sb.append("</Report>\n");
				i++;

				acProcess.executeUpdate("update ntr_account set process_flg = 'Y',file_name='" + fileName
						+ "' where account_no = '" + acNo + "'");
			}
			conn.commit();

			sb.append("</Batch>");
			finalXml = sb.toString().replaceAll("&amp;", "and").replaceAll("&", "and");
			String xml = new String(finalXml.getBytes("iso-8859-1"), "UTF-8");
			writeXml.write(xml.getBytes());
			writeXml.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
		srl = i;
	}
}

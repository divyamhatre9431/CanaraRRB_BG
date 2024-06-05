
package com.idbi.intech.newCTR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.ConnectionFactory;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class GenStrReport {

	ArrayList<String> arrState = null;
	private static Connection conn = null;

	public static void makeConnection() {
		try {
			conn = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void genStrDetails(String strseq) {
		CallableStatement callstmt = null;
		try {
			// STR Details
			
			makeConnection();

			conn.prepareCall("call gen_str_details('" + strseq + "')");
			callstmt.execute();
			callstmt.close();

			// STR Transaction
			callstmt = conn.prepareCall("call gen_str_tran('" + strseq + "')");
			callstmt.execute();
			callstmt.close();

			// STR Branch
			callstmt = conn.prepareCall("call gen_str_branch('" + strseq + "')");
			callstmt.execute();
			callstmt.close();

			// STR Accounts
			callstmt = conn.prepareCall("call gen_str_ac('" + strseq + "')");
			callstmt.execute();
			callstmt.close();

			// STR Person Details
			callstmt = conn.prepareCall("call gen_str_persondtls('" + strseq + "')");
			callstmt.execute();
			callstmt.close();

			// STR Control Details
			callstmt = conn.prepareCall("call gen_str_ctrl('" + strseq + "')");
			callstmt.execute();

		} catch (

		SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (callstmt != null) {
					callstmt.close();
					callstmt = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public void getRegState() {
		Statement stmt = null;
		ResultSet rs = null;
		arrState = new ArrayList<String>();
		try {
			makeConnection();
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
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public boolean createXmlFile(String strseq, String userId) {

		boolean flg = true;
		Statement stmt = null;
		Statement acStmt = null;
		Statement brStmt = null;
		Statement trStmt = null;
		Statement indStmt = null;
		Statement lglStmt = null;
		Statement totStmt = null;
		Statement acProcess = null;
		Statement suspStmt = null;
		ResultSet rs = null;
		ResultSet rsAc = null;
		ResultSet rsBr = null;
		ResultSet rsTr = null;
		ResultSet rsInd = null;
		ResultSet rsLgl = null;
		ResultSet rsTot = null;
		ResultSet rsSusp = null;
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
		String poFax = "";
		String poMobile = "";
		String poEmail = "";
		String poRegNo = "";
		String batchDate = "";
		String month = "";
		String year = "";
		String acNo = "";
		String brRefNo = "";
		String gos = "";
		String doi = "";
		String sourceAlert = "";
		String complxTran = "";
		String ecoRational = "";
		String leaInformed = "";
		String priorityRating = "";
//ResourceBundle bundle= ResourceBundle.getBundle("AMLProp"); 
//String fileDir =bundle.getString("STRXML"); StringBuffer sb = null; 
		String finalXml = "";
		String outfileDir = "";
		String alertInd1 = "";
		String alertInd2 = "";
		String alertInd3 = "";
		String reportCoverage = "";
		String addDocs = "";
		String leaDetails = ""; // int filecount = 1;
		String batchNum = strseq.substring(3, strseq.length());
		int i = 1;
		File file = null;
		FileOutputStream writeXml = null;

		try {

			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
//System.out.println("Current Directory::"+dir); 
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			String fileDir = amlProp.getProperty("STRXML");

			Connection conn = ConnectionFactory.makeConnectionAMLLive();
			genStrDetails(strseq);
			getRegState();

			stmt = conn.createStatement();
			totStmt = conn.createStatement();
			acStmt = conn.createStatement();
			brStmt = conn.createStatement();
			trStmt = conn.createStatement();
			indStmt = conn.createStatement();
			lglStmt = conn.createStatement();
			acProcess = conn.createStatement();
			suspStmt = conn.createStatement();
			rsTot = totStmt.executeQuery(
					"select to_char(sysdate,'YYYY-MM-DD') batchDate,month_of_record,year_of_record from str_ctrlfile where str_seq_no='"
							+ strseq + "'");
			while (rsTot.next()) {
				batchDate = rsTot.getString(1);
				month = rsTot.getString(2);
				year = rsTot.getString(3);
			}
			rs = stmt.executeQuery(
					"select comp_name_of_bank,bank_cetegory,fiureid,po_name,po_designation,po_address,po_city,po_state,po_country,po_pin,po_tel,po_mobile,po_fax,po_email,PO_REREG from po_details");
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
				poRegNo = rs.getString("PO_REREG");
			}

			rsSusp = suspStmt.executeQuery(
					"select gos,nvl(doi,'X') as doi,source,complex_tran,no_eco_rationale,lea_informed,priority_rating,lea_details,REPORT_COVERAGE,ADDITIONAL_DOCS,alert_indicator_1,alert_indicator_2,alert_indicator_3 from str_details where str_seq_no='"
							+ strseq + "'");
			while (rsSusp.next()) {
				gos = rsSusp.getString("gos");
				doi = rsSusp.getString("doi");
				sourceAlert = rsSusp.getString("source");
				complxTran = rsSusp.getString("complex_tran");
				ecoRational = rsSusp.getString("no_eco_rationale");
				leaInformed = rsSusp.getString("lea_informed");
				leaDetails = rsSusp.getString("lea_details");
				priorityRating = rsSusp.getString("priority_rating");
				reportCoverage = rsSusp.getString("REPORT_COVERAGE");
				addDocs = rsSusp.getString("ADDITIONAL_DOCS");
				alertInd1 = rsSusp.getString("alert_indicator_1");
				alertInd2 = rsSusp.getString("alert_indicator_2");
				alertInd3 = rsSusp.getString("alert_indicator_3");
			}

			StringBuffer sb = new StringBuffer();
			outfileDir = fileDir + "STR_ARF_" + batchNum + ".xml";
			file = new File(outfileDir);
			writeXml = new FileOutputStream(file);

			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			sb.append("<Batch>\n");
			sb.append("<ReportType>STR</ReportType>\n");
			sb.append("<ReportFormatType>ARF</ReportFormatType>\n");
			sb.append("<BatchHeader>\n");
			sb.append("<DataStructureVersion>2</DataStructureVersion>\n");
			sb.append("<GenerationUtilityVersion></GenerationUtilityVersion>\n");
			sb.append("<DataSource>xml</DataSource>\n");
			sb.append("</BatchHeader>\n");
			sb.append("<ReportingEntity>\n");
			sb.append("<ReportingEntityName>" + bankName + "</ReportingEntityName>\n");
			sb.append("<ReportingEntityCategory>" + bankCetegory + "</ReportingEntityCategory>\n");
			sb.append("<RERegistrationNumber>" + poRegNo + "</RERegistrationNumber>");
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
			sb.append("<MonthOfReport>NA</MonthOfReport>\n");
			sb.append("<YearOfReport>NA</YearOfReport>\n");
			sb.append("<OperationalMode>P</OperationalMode>\n");
			sb.append("<BatchType>N</BatchType>\n");
			sb.append("<OriginalBatchID>0</OriginalBatchID>\n");
			sb.append("<ReasonOfRevision>N</ReasonOfRevision>\n");
			sb.append("<PKICertificateNum></PKICertificateNum>\n");
			sb.append("</BatchDetails>\n");

			sb.append("<Report>\n");
			sb.append("<ReportSerialNum>" + i + "</ReportSerialNum>\n");
			sb.append("<OriginalReportSerialNum>0</OriginalReportSerialNum>\n");

			sb.append("<SuspicionDetails>\n");
			sb.append("<SourceOfAlert>" + sourceAlert + "</SourceOfAlert>\n");
			sb.append("<AlertIndicator>" + alertInd1 + "</AlertIndicator>\n");
			sb.append("<AlertIndicator>" + alertInd2 + "</AlertIndicator>\n");
			sb.append("<AlertIndicator>" + alertInd3 + "</AlertIndicator>\n");
			sb.append("<SuspicionDueToProceedsOfCrime>N</SuspicionDueToProceedsOfCrime>\n");
			sb.append("<SuspicionDueToComplexTrans>" + complxTran + "</SuspicionDueToComplexTrans>\n");
			sb.append("<SuspicionDueToNoEcoRationale>" + ecoRational + "</SuspicionDueToNoEcoRationale>");
			sb.append("<SuspicionOfFinancingOfTerrorism>N</SuspicionOfFinancingOfTerrorism>\n");
			sb.append("<AttemptedTransaction>N</AttemptedTransaction>\n");
			sb.append("<GroundsOfSuspicion>\n" + gos + "</GroundsOfSuspicion>\n");
			if (!doi.equalsIgnoreCase("X")) {
				sb.append("<DetailsOfInvestigations>\n" + doi + "</DetailsOfInvestigations>\n");
			}
			sb.append("<LEAInformed>" + leaInformed + "</LEAInformed>\n");
			sb.append("<LEADetails>" + leaDetails + "</LEADetails>\n");
			sb.append("<PriorityRating>" + priorityRating + "</PriorityRating>\n");
			sb.append("<ReportCoverage>" + reportCoverage + "</ReportCoverage>\n");
			sb.append("<AdditionalDocuments>" + addDocs + "</AdditionalDocuments>\n");
			sb.append("</SuspicionDetails>\n");

			rsAc = acStmt.executeQuery(
					"select branch_ref_no,account_no,account_name,type_of_ac,type_of_acholder,acct_opn_date,risk_rating,"
							+ " nvl(cumm_cr_tot,0)as cumm_cr_tot,nvl(cumm_dr_tot,0) as cumm_dr_tot,nvl(cumm_cs_cr_tot,0) as cumm_cs_cr_tot,nvl(cumm_cs_dr_tot,0) as cumm_cs_dr_tot,acct_status,nvl(person_name,'XXXXX') person_name, "
							+ "(select decode(count(1),0,'Y','N') from str_transaction where str_seq_no=a.str_seq_no and account_no=a.account_no) txnrpt"
							+ " from str_account a" + " where str_seq_no='" + strseq + "' and process_flg = 'N'");
			while (rsAc.next()) {
				brRefNo = rsAc.getString("branch_ref_no");
				acNo = rsAc.getString("account_no");

				sb.append("<Account>\n");
				sb.append("<AccountDetails>\n");
				sb.append("<AccountNumber>" + rsAc.getString("account_no") + "</AccountNumber>\n");
				sb.append("<AccountType>" + rsAc.getString("type_of_ac") + "</AccountType>\n");
				sb.append("<HolderName>" + (rsAc.getString("account_name").length() > 75
						? rsAc.getString("account_name").substring(0, 74)
						: rsAc.getString("account_name")) + "</HolderName>\n");
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
				sb.append(
						"<NoTransactionsTobeReported>" + rsAc.getString("txnrpt") + "</NoTransactionsTobeReported>\n");
				sb.append("</AccountDetails>\n");

				rsBr = brStmt.executeQuery(
						"select branch_ref_no,branch_name,branch_address,branch_city,branch_state,branch_pincode,branch_telephone,branch_fax,branch_email from str_branch where str_seq_no='"
								+ strseq + "' and branch_ref_no='" + brRefNo + "' and rownum<=1");

				while (rsBr.next()) {
					sb.append("<Branch>\n");
					sb.append("<BranchRefNumType>R</BranchRefNumType>\n");
					sb.append("<BranchRefNum>" + rsBr.getString("branch_ref_no") + "</BranchRefNum>\n");
					sb.append("<BranchDetails>\n");
					sb.append("<BranchName>" + rsBr.getString("branch_name") + "</BranchName>\n");
					sb.append("<BranchAddress>\n");
					sb.append("<Address>" + rsBr.getString("branch_address") + "</Address>\n");
					sb.append("<City>" + rsBr.getString("branch_city") + "</City>\n");
					sb.append("<StateCode>"
							+ (arrState.contains(rsBr.getString("branch_state")) ? rsBr.getString("branch_state")
									: "XX")
							+ "</StateCode>\n");
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
						"select full_name,cust_id,get_UIN(cust_id) uin,relation_flg,occupation,date_of_birth,cust_sex,nationality,type_of_id,id_no,issuing_authority,place_of_issue,get_cust_address(a.cust_id,'Mailing') commu_addr,commu_city,commu_state,commu_cntry,commu_pin,commu_tel,pan,father_spouse_name from str_individual a where str_seq_no='"
								+ strseq + "' and account_no='" + acNo + "' and cust_id is not null");
				while (rsInd.next()) {
					sb.append("<PersonDetails>\n");
					sb.append("<PersonName>" + rsInd.getString("full_name").replace("&", " ") + "</PersonName>\n");
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
					sb.append(rsInd.getString("commu_city") == null ? "" : rsInd.getString("commu_city"));
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
					sb.append(rsInd.getString("commu_cntry") == null ? "XX" : rsInd.getString("commu_cntry"));
					sb.append("</CountryCode>\n");
					sb.append("</CommunicationAddress>\n");
					sb.append("<Phone>\n");
					sb.append("<Telephone>");
					sb.append(rsInd.getString("commu_tel") == null ? "" : rsInd.getString("commu_tel"));
					sb.append("</Telephone>\n");
					sb.append("<Mobile></Mobile>\n");
					sb.append("<Fax></Fax>\n");
					sb.append("</Phone>\n");
					sb.append("<Email></Email>\n");

					sb.append("<SecondAddress>\n");
					sb.append("<Address></Address>\n");
					sb.append("<City></City>\n");
					sb.append("<StateCode></StateCode>\n");
					sb.append("<PinCode></PinCode>\n");
					sb.append("<CountryCode></CountryCode>\n");
					sb.append("</SecondAddress>\n");

					if (rsInd.getString("pan") == null || rsInd.getString("pan").equals("PANNOTREQD")
							|| rsInd.getString("pan").equals("PANINVALID") || rsInd.getString("pan").equals("FORM60")
							|| rsInd.getString("pan").equals("PANNOTAVBL")) {
						sb.append("<PAN></PAN>\n");
					} else {
						sb.append("<PAN>");
						sb.append(rsInd.getString("pan") == null ? "" : rsInd.getString("pan"));
						sb.append("</PAN>\n");
					}
					sb.append("<UIN>");
					sb.append(rsInd.getString("uin") == null ? "" : rsInd.getString("uin"));
					sb.append("</UIN>\n"); // sb.append("<UIN></UIN>\n");
					sb.append("<Individual>\n");
					sb.append("<Gender>");
					sb.append(rsInd.getString("cust_sex") == null ? "X" : rsInd.getString("cust_sex"));
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
					sb.append("<Nationality>"
							+ (rsInd.getString("nationality") == null ? "IN" : rsInd.getString("nationality"))
							+ "</Nationality>\n");
					sb.append("<PlaceOfWork></PlaceOfWork>\n");
					sb.append("<FatherOrSpouse>" + (rsInd.getString("father_spouse_name") == null ? ""
							: rsInd.getString("father_spouse_name")) + "</FatherOrSpouse>\n");
					sb.append("<Occupation>");
					sb.append(rsInd.getString("occupation") == null ? "OTHER SERVICES"
							: rsInd.getString("occupation").length() > 45
									? rsInd.getString("occupation").substring(0, 45)
									: rsInd.getString("occupation"));
					sb.append("</Occupation>\n");
					sb.append("</Individual>\n");
					sb.append("</PersonDetails>\n");
				}

				rsLgl = lglStmt.executeQuery(
						"select full_name,cust_id,get_UIN(cust_id) uin,relation_flg,get_cust_address(a.cust_id,'Mailing') commu_addr,commu_city,commu_state,commu_cntry,commu_pin,commu_tel,pan,type_of_const,date_of_incorp,nature_of_buss,substr(reg_no,1,18) reg_no,substr(reg_place,1,18) reg_place from str_legalperson a where str_seq_no='"
								+ strseq + "' and account_no='" + acNo + "' and cust_id is not null");
				while (rsLgl.next()) {
					sb.append("<PersonDetails>\n");
					sb.append("<PersonName>" + rsLgl.getString("full_name").replace("&", " ") + "</PersonName>\n");
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
					sb.append(rsLgl.getString("commu_cntry") == null ? "XX" : rsLgl.getString("commu_cntry"));
					sb.append("</CountryCode>\n");
					sb.append("</CommunicationAddress>\n");
					sb.append("<Phone>\n");
					sb.append("<Telephone>");
					sb.append(rsLgl.getString("commu_tel") == null ? "" : rsLgl.getString("commu_tel"));
					sb.append("</Telephone>\n");
					sb.append("<Mobile></Mobile>\n");
					sb.append("<Fax></Fax>\n");
					sb.append("</Phone>\n");
					sb.append("<Email></Email>\n");

					sb.append("<SecondAddress>\n");
					sb.append("<Address></Address>\n");
					sb.append("<City></City>\n");
					sb.append("<StateCode></StateCode>\n");
					sb.append("<PinCode></PinCode>\n");
					sb.append("<CountryCode></CountryCode>\n");
					sb.append("</SecondAddress>\n");

					if (rsLgl.getString("pan") == null || rsLgl.getString("pan").equals("PANNOTREQD")
							|| rsLgl.getString("pan").equals("PANINVALID") || rsLgl.getString("pan").equals("FORM60")
							|| rsLgl.getString("pan").equals("PANNOTAVBL")) {
						sb.append("<PAN></PAN>\n");
					} else {
						sb.append("<PAN>");
						sb.append(rsLgl.getString("pan") == null ? "" : rsLgl.getString("pan"));
						sb.append("</PAN>\n");
					}
					sb.append("<UIN>");
					sb.append(rsLgl.getString("uin") == null ? "" : rsLgl.getString("uin"));
					sb.append("</UIN>\n"); // sb.append("<UIN></UIN>\n");
					sb.append("<LegalPerson>\n");
					sb.append("<ConstitutionType>");
					String typeOfConstitution = rsLgl.getString("type_of_const") == null ? "Z"
							: rsLgl.getString("type_of_const");
					sb.append(typeOfConstitution);
					sb.append("</ConstitutionType>\n");
					if (rsLgl.getString("reg_no") != null) {
						sb.append("<RegistrationNumber>" + rsLgl.getString("reg_no") + "</RegistrationNumber>\n");
					} else {
						sb.append("<RegistrationNumber></RegistrationNumber>\n");
					}
					if (rsLgl.getString("date_of_incorp") != null) {
						sb.append("<DateOfIncorporation>" + rsLgl.getString("date_of_incorp")
								+ "</DateOfIncorporation>\n");
					}
					if (rsLgl.getString("reg_place") != null) {
						sb.append("<PlaceOfRegistration>" + rsLgl.getString("reg_place") + "</PlaceOfRegistration>\n");
					} else {
						sb.append("<PlaceOfRegistration></PlaceOfRegistration>\n");
					}
					sb.append("<CountryCode>");
					sb.append(rsLgl.getString("commu_cntry") == null ? "XX" : rsLgl.getString("commu_cntry"));
					sb.append("</CountryCode>\n");
					sb.append("<NatureOfBusiness>");
					sb.append(rsLgl.getString("nature_of_buss") == null ? "" : rsLgl.getString("nature_of_buss"));
					sb.append("</NatureOfBusiness>\n");
					sb.append("</LegalPerson>\n");
					sb.append("</PersonDetails>\n");
				}
				rsTr = trStmt.executeQuery(
						"select tran_id,tran_date,mode_of_tran,cre_deb_flg,tran_amount,tran_crncy,tran_remarks from str_transaction where str_seq_no='"
								+ strseq + "' and account_no='" + acNo + "'");
				while (rsTr.next()) {
					sb.append("<Transaction>\n");
					sb.append("<DateOfTransaction>" + rsTr.getString("tran_date") + "</DateOfTransaction>\n");
					sb.append("<TransactionID>" + rsTr.getString("tran_id") + "</TransactionID>\n");
					sb.append("<TransactionMode>" + rsTr.getString("mode_of_tran") + "</TransactionMode>\n");
					sb.append("<DebitCredit>" + rsTr.getString("cre_deb_flg") + "</DebitCredit>\n");
					sb.append("<Amount>" + rsTr.getString("tran_amount") + "</Amount>\n");
					sb.append("<Currency>" + rsTr.getString("tran_crncy") + "</Currency>\n");

					sb.append("<ProductTransaction>\n");
					sb.append("<ProductType></ProductType>\n");
					sb.append("<Identifier></Identifier>\n");
					sb.append("<TransactionType></TransactionType>\n");
					sb.append("<Units></Units>\n");
					sb.append("<Rate></Rate>\n");
					sb.append("</ProductTransaction>\n");
					sb.append("<DispositionOfFunds></DispositionOfFunds>\n");
					sb.append("<RelatedAccountNum></RelatedAccountNum>\n");
					sb.append("<RelatedInstitutionName></RelatedInstitutionName>\n");
					sb.append("<RelatedInstitutionRefNum></RelatedInstitutionRefNum>\n");

					sb.append("<Remarks>");
					sb.append(rsTr.getString("tran_remarks").length() > 50
							? rsTr.getString("tran_remarks").substring(0, 48)
							: rsTr.getString("tran_remarks").replaceAll("&amp;", ""));
					sb.append("</Remarks>\n");
					sb.append("</Transaction>\n");
				}
				sb.append("</Account>\n");

				acProcess.executeUpdate("update str_account set process_flg = 'Y' where account_no = '" + acNo
						+ "' and str_seq_no='" + strseq + "'");
			}

			sb.append("</Report>\n");
			sb.append("</Batch>");
			finalXml = sb.toString().replace("&", " &amp; ");
			String xml = new String(finalXml.getBytes("iso-8859-1"), "UTF-8");
			writeXml.write(xml.getBytes());
			writeXml.close();

			stmt.executeUpdate("insert into str_filedetails values('" + strseq + "','Normal','STR_ARF_" + batchNum
					+ ".xml','" + userId + "',sysdate)");
			conn.commit();
			flg = true;

//String fileName = "STR_ARF_" + batchNum + ".xml";
//moveToAppDirectory(xml,fileName);

		} catch (Exception ex) {
			flg = false;
			ex.printStackTrace();
		} finally {
			try {
				if (acStmt != null) {
					acStmt.close();
					acStmt = null;
				}
				if (brStmt != null) {
					brStmt.close();
					brStmt = null;
				}
				if (lglStmt != null) {
					lglStmt.close();
					lglStmt = null;
				}
				if (indStmt != null) {
					indStmt.close();
					indStmt = null;
				}
				if (trStmt != null) {
					trStmt.close();
					trStmt = null;
				}
				if (totStmt != null) {
					totStmt.close();
					totStmt = null;
				}
				if (acProcess != null) {
					acProcess.close();
					acProcess = null;
				}
				if (suspStmt != null) {
					suspStmt.close();
					suspStmt = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rsAc != null) {
					rsAc.close();
					rsAc = null;
				}
				if (rsBr != null) {
					rsBr.close();
					rsBr = null;
				}
				if (rsTr != null) {
					rsTr.close();
					rsTr = null;
				}
				if (rsInd != null) {
					rsInd.close();
					rsInd = null;
				}
				if (rsLgl != null) {
					rsLgl.close();
					rsLgl = null;
				}
				if (rsTot != null) {
					rsTot.close();
					rsTot = null;
				}
				if (rsSusp != null) {
					rsSusp.close();
					rsSusp = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return flg;
	}

	public void executeResetStrProc(String strNo) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute("call reset_str('" + strNo + "')");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
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
	}

	public void moveToAppDirectory(String strXml, String fileName) throws IOException {
		ResourceBundle bundle = ResourceBundle.getBundle("AMLProp");
		String smbUser = bundle.getString("smbUser");
		String smbPass = bundle.getString("smbPass");
		String destination1 = bundle.getString("destination1"); // String destination2 =
		bundle.getString("destination2");
		SmbFileOutputStream out1 = null;
//SmbFileOutputStream out2 = null;

		try {
			//NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication();
			SmbFile smbFileDest1 = new SmbFile(destination1 + "" + fileName);
//SmbFile smbFileDest2 = new SmbFile(destination2+""+fileName, auth);

			out1 = new SmbFileOutputStream(smbFileDest1);
//out2 = newSmbFileOutputStream(smbFileDest2);

			out1.write(strXml.getBytes()); // out2.write(strXml.getBytes());

			out1.close(); // out2.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

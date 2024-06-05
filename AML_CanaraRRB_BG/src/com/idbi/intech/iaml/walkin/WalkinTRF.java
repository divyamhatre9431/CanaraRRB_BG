package com.idbi.intech.iaml.walkin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.idbi.intech.aml.CBWTng.CBWTReportGenerator;
import com.idbi.intech.iaml.CCR.CCRBranchDatabean;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class WalkinTRF {
	private static Connection connection = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public static String getCurrentMonth() throws Exception {
		String sql = "select to_char(sysdate-30,'MM') as result from dual";// ,
																			// to_char(sysdate,'YYYY')as
																			// year
		ResultSet rs = null;
		PreparedStatement statement = null;
		String dat = null;
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			while (rs.next()) {
				dat = rs.getString("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
		return dat;
	}

	public static String getCurrentYear() throws Exception {
		String sql = "select to_char(sysdate-30,'YYYY') as result from dual";// ,
																				// to_char(sysdate,'YYYY')as
																				// year
		ResultSet rs = null;
		PreparedStatement statement = null;
		String dat = null;
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			while (rs.next()) {
				dat = rs.getString("result");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
		return dat;
	}

	public static boolean isIntNumber(String num) {
		try {
			Integer.valueOf(num);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public ArrayList<String> checkCountry() {
		ArrayList<String> arrctry = new ArrayList<String>();

		arrctry.add("AF");
		arrctry.add("AX");
		arrctry.add("AL");
		arrctry.add("DZ");
		arrctry.add("AS");
		arrctry.add("AD");
		arrctry.add("AO");
		arrctry.add("AI");
		arrctry.add("AQ");
		arrctry.add("AG");
		arrctry.add("AR");
		arrctry.add("AM");
		arrctry.add("AW");
		arrctry.add("AU");
		arrctry.add("AT");
		arrctry.add("AZ");
		arrctry.add("BS");
		arrctry.add("BH");
		arrctry.add("BD");
		arrctry.add("BB");
		arrctry.add("BY");
		arrctry.add("BE");
		arrctry.add("BZ");
		arrctry.add("BJ");
		arrctry.add("BM");
		arrctry.add("BT");
		arrctry.add("BO");
		arrctry.add("BA");
		arrctry.add("BW");
		arrctry.add("BV");
		arrctry.add("BR");
		arrctry.add("IO");
		arrctry.add("BN");
		arrctry.add("BG");
		arrctry.add("BF");
		arrctry.add("BI");
		arrctry.add("KH");
		arrctry.add("CM");
		arrctry.add("CA");
		arrctry.add("CV");
		arrctry.add("KY");
		arrctry.add("CF");
		arrctry.add("TD");
		arrctry.add("CL");
		arrctry.add("CN");
		arrctry.add("CX");
		arrctry.add("CC");
		arrctry.add("CO");
		arrctry.add("KM");
		arrctry.add("CG");
		arrctry.add("CD");
		arrctry.add("CK");
		arrctry.add("CR");
		arrctry.add("CI");
		arrctry.add("HR");
		arrctry.add("CU");
		arrctry.add("CY");
		arrctry.add("CZ");
		arrctry.add("DK");
		arrctry.add("DJ");
		arrctry.add("DM");
		arrctry.add("DO");
		arrctry.add("EC");
		arrctry.add("EG");
		arrctry.add("SV");
		arrctry.add("GQ");
		arrctry.add("ER");
		arrctry.add("EE");
		arrctry.add("ET");
		arrctry.add("FK");
		arrctry.add("FO");
		arrctry.add("FJ");
		arrctry.add("FI");
		arrctry.add("FR");
		arrctry.add("GF");
		arrctry.add("PF");
		arrctry.add("TF");
		arrctry.add("GA");
		arrctry.add("GM");
		arrctry.add("GE");
		arrctry.add("DE");
		arrctry.add("GH");
		arrctry.add("GI");
		arrctry.add("GR");
		arrctry.add("GL");
		arrctry.add("GD");
		arrctry.add("GP");
		arrctry.add("GU");
		arrctry.add("GT");
		arrctry.add("GG");
		arrctry.add("GN");
		arrctry.add("GW");
		arrctry.add("GY");
		arrctry.add("HT");
		arrctry.add("HM");
		arrctry.add("VA");
		arrctry.add("HN");
		arrctry.add("HK");
		arrctry.add("HU");
		arrctry.add("IS");
		arrctry.add("IN");
		arrctry.add("ID");
		arrctry.add("IR");
		arrctry.add("IQ");
		arrctry.add("IE");
		arrctry.add("IM");
		arrctry.add("IL");
		arrctry.add("IT");
		arrctry.add("JM");
		arrctry.add("JP");
		arrctry.add("JE");
		arrctry.add("JO");
		arrctry.add("KZ");
		arrctry.add("KE");
		arrctry.add("KI");
		arrctry.add("KP");
		arrctry.add("KR");
		arrctry.add("KW");
		arrctry.add("KG");
		arrctry.add("LA");
		arrctry.add("LV");
		arrctry.add("LB");
		arrctry.add("LS");
		arrctry.add("LR");
		arrctry.add("LY");
		arrctry.add("LI");
		arrctry.add("LT");
		arrctry.add("LU");
		arrctry.add("MO");
		arrctry.add("MK");
		arrctry.add("MG");
		arrctry.add("MW");
		arrctry.add("MY");
		arrctry.add("MV");
		arrctry.add("ML");
		arrctry.add("MT");
		arrctry.add("MH");
		arrctry.add("MQ");
		arrctry.add("MR");
		arrctry.add("MU");
		arrctry.add("YT");
		arrctry.add("MX");
		arrctry.add("FM");
		arrctry.add("MD");
		arrctry.add("MC");
		arrctry.add("MN");
		arrctry.add("ME");
		arrctry.add("MS");
		arrctry.add("MA");
		arrctry.add("MZ");
		arrctry.add("MM");
		arrctry.add("NA");
		arrctry.add("NR");
		arrctry.add("NP");
		arrctry.add("NL");
		arrctry.add("AN");
		arrctry.add("NC");
		arrctry.add("NZ");
		arrctry.add("NI");
		arrctry.add("NE");
		arrctry.add("NG");
		arrctry.add("NU");
		arrctry.add("NF");
		arrctry.add("MP");
		arrctry.add("NO");
		arrctry.add("OM");
		arrctry.add("PK");
		arrctry.add("PW");
		arrctry.add("PS");
		arrctry.add("PA");
		arrctry.add("PG");
		arrctry.add("PY");
		arrctry.add("PE");
		arrctry.add("PH");
		arrctry.add("PN");
		arrctry.add("PL");
		arrctry.add("PT");
		arrctry.add("PR");
		arrctry.add("QA");
		arrctry.add("RE");
		arrctry.add("RO");
		arrctry.add("RU");
		arrctry.add("RW");
		arrctry.add("BL");
		arrctry.add("SH");
		arrctry.add("KN");
		arrctry.add("LC");
		arrctry.add("MF");
		arrctry.add("PM");
		arrctry.add("VC");
		arrctry.add("WS");
		arrctry.add("SM");
		arrctry.add("ST");
		arrctry.add("SA");
		arrctry.add("SN");
		arrctry.add("RS");
		arrctry.add("SC");
		arrctry.add("SL");
		arrctry.add("SG");
		arrctry.add("SK");
		arrctry.add("SI");
		arrctry.add("SB");
		arrctry.add("SO");
		arrctry.add("ZA");
		arrctry.add("GS");
		arrctry.add("ES");
		arrctry.add("LK");
		arrctry.add("SD");
		arrctry.add("SR");
		arrctry.add("SJ");
		arrctry.add("SZ");
		arrctry.add("SE");
		arrctry.add("CH");
		arrctry.add("SY");
		arrctry.add("TW");
		arrctry.add("TJ");
		arrctry.add("TZ");
		arrctry.add("TH");
		arrctry.add("TL");
		arrctry.add("TG");
		arrctry.add("TK");
		arrctry.add("TO");
		arrctry.add("TT");
		arrctry.add("TN");
		arrctry.add("TR");
		arrctry.add("TM");
		arrctry.add("TC");
		arrctry.add("TV");
		arrctry.add("UG");
		arrctry.add("UA");
		arrctry.add("AE");
		arrctry.add("GB");
		arrctry.add("US");
		arrctry.add("UM");
		arrctry.add("UY");
		arrctry.add("UZ");
		arrctry.add("VU");
		arrctry.add("VE");
		arrctry.add("VN");
		arrctry.add("VG");
		arrctry.add("VI");
		arrctry.add("WF");
		arrctry.add("EH");
		arrctry.add("YE");
		arrctry.add("ZM");
		arrctry.add("ZW");
		arrctry.add("XX");
		arrctry.add("ZZ");

		return arrctry;

	}

	public ArrayList<String> checkState() {
		ArrayList<String> statecode = new ArrayList<String>();

		statecode.add("AN");
		statecode.add("AP");
		statecode.add("AR");
		statecode.add("AS");
		statecode.add("BR");
		statecode.add("CH");
		statecode.add("CG");
		statecode.add("DN");
		statecode.add("DD");
		statecode.add("DL");
		statecode.add("GA");
		statecode.add("GJ");
		statecode.add("HR");
		statecode.add("HP");
		statecode.add("JK");
		statecode.add("JH");
		statecode.add("KA");
		statecode.add("KL");
		statecode.add("LD");
		statecode.add("MP");
		statecode.add("MH");
		statecode.add("MN");
		statecode.add("ML");
		statecode.add("MZ");
		statecode.add("NL");
		statecode.add("OR");
		statecode.add("PY");
		statecode.add("PB");
		statecode.add("RJ");
		statecode.add("SK");
		statecode.add("TN");
		statecode.add("TR");
		statecode.add("UP");
		statecode.add("UA");
		statecode.add("WB");
		statecode.add("XX");
		statecode.add("ZZ");

		return statecode;
	}

	public void createFile(String ticketId, String reportType, String gos,
			String doi, String soa, String poc, String ct, String nor, String ft) {
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		PreparedStatement branchstat = null;
		PreparedStatement reportStat = null;
		PreparedStatement tranStat = null;
		CCRBranchDatabean ccrBranchDatabean = null;
		String bRefNo = "";
		Integer lineNumber = 0;
		String month = "";
		String year = "";
		Statement stmtBranch = null;
		Statement stmtReport = null;
		Statement stmtCustomer = null;
		Statement stmtProcess = null;
		ResultSet rsBranch = null;
		Statement stmtTransaction = null;
		ResultSet rsTran = null;
		ResultSet rsReport = null;
		ResultSet rsCustomer = null;
		String custName = "";
		String sol = "";
		String dt = "";
		String walkinName = "";
		String walkinDob = "";
		String walkinId = "";
		String fromDate = "";
		String toDate = "";

		try {
			stmt = connection.createStatement();

			sql = "select to_char(sysdate,'YYYY-MM-DD') as dt from DUAL";
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				dt = rs.getString(1);
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}

			month = getCurrentMonth();
			year = getCurrentYear();

			stmt = connection.createStatement();

			rs = stmt
					.executeQuery("select walkin_name,to_char(walkin_dob,'dd-mon-yy'),walkin_id,to_char(RULE_START_DATE,'dd-mon-yy'),to_char(RULE_end_DATE,'dd-mon-yy') from aml_walkin_ticket where ticket_id  = '"
							+ ticketId + "'");
			while (rs.next()) {
				walkinName = rs.getString(1);
				walkinDob = rs.getString(2);
				walkinId = rs.getString(3);
				fromDate = rs.getString(4);
				toDate = rs.getString(5);
			}

			sql = "select distinct(B.SOL_ID) as sol,B.SOL_DESC,B.ADDR_1,B.ADDR_2,"
					+ "(select REF_DESC from AML_RCT where ref_rec_type = '01' and ref_code=b.CITY_CODE) as CITY_CODE,"
					+ "(select REF_DESC from AML_RCT where ref_rec_type = '02' and ref_code=b.STATE_CODE) as STATE_CODE,b.PIN_CODE,nvl(b.UNIFORM_BR_CODE,'0000000') as UNIFORM_BR_CODE"
					+ " from AML_SOL b,aml_walkin A where a.SOL_ID=b.SOL_ID and walkin_name='"
					+ walkinName
					+ "' and walkin_dob='"
					+ walkinDob
					+ "' and walkin_id='"
					+ walkinId
					+ "' and to_date(a.rcre_time,'dd-mon-yy') between '"
					+ fromDate + "' and '" + toDate + "'";

			// System.out.println(sql);

			rs = stmt.executeQuery(sql);

			lineNumber = 1;

			sql = "INSERT INTO AML_WALKIN_BRANCH_FILE (CBWT_SEQ_NO,RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD,LINE_NO, NAME_OF_BRANCH, BRANCH_REF_NO, "
					+ "UID_FIU, BRANCH_ADDRESS1, BRANCH_ADDRESS2,BRANCH_ADDRESS3, BRANCH_ADDRESS4, BRANCH_ADDRESS5,"
					+ " BRANCH_PINCODE, BRANCH_TELEPHONE, BRANCH_FAX, BRANCH_EMAIL,BRANCH_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			branchstat = connection.prepareStatement(sql);

			while (rs.next()) {
				sol = rs.getString("sol");

				bRefNo = String
						.format("%-7s",
								rs.getString("UNIFORM_BR_CODE") != null
										&& rs.getString("UNIFORM_BR_CODE")
												.length() > 6 ? rs
										.getString("UNIFORM_BR_CODE") : "")
						.substring(0, 7).replace("\'", " ").replace("\"", " ");

				ccrBranchDatabean = new CCRBranchDatabean();
				ccrBranchDatabean
						.setBranchAdd1((rs.getString("ADDR_1") != null ? rs
								.getString("ADDR_1") : "-NA-")
								.replace("\'", "").replace("\"", ""));
				ccrBranchDatabean
						.setBranchAdd2(rs.getString("ADDR_2") != null ? rs
								.getString("ADDR_2").replace("\'", "")
								.replace("\" ", "") : "-NA-");
				ccrBranchDatabean.setBranchAdd3(String.format("%-45s", ""));
				ccrBranchDatabean
						.setBranchAdd4((rs.getString("CITY_CODE") != null ? rs
								.getString("CITY_CODE") : "")
								.replace("\'", " ").replace("\" ", " "));
				ccrBranchDatabean
						.setBranchAdd5((rs.getString("STATE_CODE") != null ? rs
								.getString("STATE_CODE") : "").replace("\'",
								" ").replace("\" ", " "));
				ccrBranchDatabean.setBranchRefNo(bRefNo.replace("\'", "")
						.replace("\" ", " "));
				ccrBranchDatabean.setEmailAdd(String.format("%-50s", ""));
				ccrBranchDatabean.setFaxNo(String.format("%-30s", ""));
				ccrBranchDatabean.setLineNumber(String.format("%6s",
						lineNumber.toString()).replace(' ', '0'));
				ccrBranchDatabean.setMonthOfReport(String
						.format("%-2s", month.toString()).substring(0, 2)
						.replace("\'", " ").replace("\" ", " "));
				ccrBranchDatabean
						.setNameOfBranch((rs.getString("SOL_DESC") != null ? rs
								.getString("SOL_DESC") : "").replace("\'", " ")
								.replace("\"", " "));
				ccrBranchDatabean
						.setPincode((rs.getString("PIN_CODE") != null ? isIntNumber(rs
								.getString("PIN_CODE")) ? rs
								.getString("PIN_CODE") : "000000" : "000000")
								.replace("\'", " ").replace("\"", " "));
				ccrBranchDatabean.setReportType("BRC");
				ccrBranchDatabean.setTelephone(String.format("%-30s", " "));
				ccrBranchDatabean.setUID("XXXXXXXXXX");
				ccrBranchDatabean.setYearOfReport(String
						.format("%-4s", year.toString()).substring(0, 4)
						.replace("\'", " ").replace("\"", " "));

				// Table entry....
				branchstat.setString(1, ticketId);
				branchstat.setString(2, ccrBranchDatabean.getReportType());
				branchstat.setString(3, ccrBranchDatabean.getMonthOfReport());
				branchstat.setString(4, ccrBranchDatabean.getYearOfReport());
				branchstat.setString(5, ccrBranchDatabean.getLineNumber());
				branchstat.setString(6, ccrBranchDatabean.getNameOfBranch());
				branchstat.setString(7, ccrBranchDatabean.getBranchRefNo());
				branchstat.setString(8, ccrBranchDatabean.getUID());
				branchstat.setString(9, ccrBranchDatabean.getBranchAdd1());
				branchstat.setString(10, ccrBranchDatabean.getBranchAdd2());
				branchstat.setString(11, ccrBranchDatabean.getBranchAdd3());
				branchstat.setString(12, ccrBranchDatabean.getBranchAdd4());
				branchstat.setString(13, ccrBranchDatabean.getBranchAdd5());
				branchstat.setString(14, ccrBranchDatabean.getPincode());
				branchstat.setString(15, ccrBranchDatabean.getTelephone());
				branchstat.setString(16, ccrBranchDatabean.getFaxNo());
				branchstat.setString(17, ccrBranchDatabean.getEmailAdd());
				branchstat.setString(18, sol);
				branchstat.executeUpdate();

				lineNumber++;
			}

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (branchstat != null) {
				branchstat.close();
				branchstat = null;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			String header = "";

			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from aml_walkin_header_master");
			while (rs.next()) {
				header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
						+ "<Batch>" + "<ReportType>"
						+ reportType
						+ "</ReportType>"
						+ "<ReportFormatType>"
						+ rs.getString("ReportFormatType")
						+ "</ReportFormatType>"
						+ "<BatchHeader>"
						+ "<DataStructureVersion>2</DataStructureVersion>"
						+ "<GenerationUtilityVersion>0.1</GenerationUtilityVersion>"
						+ "<DataSource>txt</DataSource>"
						+ "</BatchHeader>"
						+ "<ReportingEntity>"
						+ "<ReportingEntityName>"
						+ rs.getString("REPORTINGENTITYNAME")
						+ "</ReportingEntityName>"
						+ "<ReportingEntityCategory>"
						+ rs.getString("REPORTINGENTITYCATEGORY")
						+ "</ReportingEntityCategory>"
						+ "<RERegistrationNumber>"
						+ rs.getString("REREGISTRATIONNUMBER")
						+ "</RERegistrationNumber>"
						+ "<FIUREID>"
						+ rs.getString("FIUREID")
						+ "</FIUREID>"
						+ "</ReportingEntity>"
						+ "<PrincipalOfficer>"
						+ "<POName>"
						+ rs.getString("PONAME")
						+ "</POName>"
						+ "<PODesignation>"
						+ rs.getString("PODESIGNATION")
						+ "</PODesignation>"
						+ "<POAddress>"
						+ "<Address>"
						+ rs.getString("ADDRESS")
						+ "</Address>"
						+ "<City>"
						+ rs.getString("CITY")
						+ "</City>"
						+ "<StateCode>"
						+ rs.getString("STATECODE")
						+ "</StateCode>"
						+ "<PinCode>"
						+ rs.getString("PINCODE")
						+ "</PinCode>"
						+ "<CountryCode>"
						+ rs.getString("COUNTRY")
						+ "</CountryCode>"
						+ "</POAddress>"
						+ "<POPhone>"
						+ "<Telephone>"
						+ rs.getString("TELEPHONE")
						+ "</Telephone>"
						+ "<Mobile>"
						+ rs.getString("MOBILE")
						+ "</Mobile>"
						+ "<Fax>"
						+ rs.getString("FAX")
						+ "</Fax>"
						+ "</POPhone>"
						+ "<POEmail>"
						+ rs.getString("EMAIL")
						+ "</POEmail>"
						+ "</PrincipalOfficer>"
						+ "<BatchDetails>"
						+ "<BatchNumber>"
						+ ticketId.substring(3)
						+ "</BatchNumber>"
						+ "<BatchDate>"
						+ dt
						+ "</BatchDate>"
						+ "<MonthOfReport>"
						+ month
						+ "</MonthOfReport>"
						+ "<YearOfReport>"
						+ year
						+ "</YearOfReport>"
						+ "<OperationalMode>P</OperationalMode>"
						+ "<BatchType>N</BatchType>"
						+ "<OriginalBatchID>0</OriginalBatchID>"
						+ "<ReasonOfRevision>N</ReasonOfRevision>"
						+ "</BatchDetails>";
			}

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			String report = "";
			int cnt = 0;
			String branch = "";
			String reportSummary = "";

			report += header;

			stmt = connection.createStatement();
			stmtTransaction = connection.createStatement();
			stmtCustomer = connection.createStatement();
			stmtBranch = connection.createStatement();
			stmtProcess = connection.createStatement();

			int iwrd = 0;

			rs = stmt
					.executeQuery("select sol_id,walkin_name from aml_walkin where walkin_name='"
							+ walkinName
							+ "' and walkin_dob='"
							+ walkinDob
							+ "' and walkin_id='"
							+ walkinId
							+ "' and to_date(rcre_time,'dd-mon-yy') between '"
							+ fromDate + "' and '" + toDate + "'");
			while (rs.next()) {
				String solid = rs.getString("sol_id");
				custName = rs.getString("walkin_name") == null ? "XXX" : rs
						.getString("walkin_name");

				reportSummary = "<Report>";
				reportSummary += "<ReportSerialNum>" + (++cnt);
				reportSummary += " </ReportSerialNum>";
				reportSummary += "<OriginalReportSerialNum>0</OriginalReportSerialNum>";
				reportSummary += "<MainPersonName>"
						+ (custName.length() > 80 ? (custName.substring(0, 79))
								: custName) + "</MainPersonName>";

				reportSummary += "<SuspicionDetails>";
				reportSummary += "<SourceOfAlert>";
				reportSummary += soa;
				reportSummary += "</SourceOfAlert>";
				reportSummary += "<SuspicionDueToProceedsOfCrime>";
				reportSummary += poc;
				reportSummary += "</SuspicionDueToProceedsOfCrime>";
				reportSummary += "<SuspicionDueToComplexTrans>";
				reportSummary += ct;
				reportSummary += "</SuspicionDueToComplexTrans>";
				reportSummary += "<SuspicionDueToNoEcoRationale>";
				reportSummary += nor;
				reportSummary += "</SuspicionDueToNoEcoRationale>";
				reportSummary += "<SuspicionOfFinancingOfTerrorism>";
				reportSummary += ft;
				reportSummary += "</SuspicionOfFinancingOfTerrorism>";
				reportSummary += "<AttemptedTransaction>Y</AttemptedTransaction>";
				reportSummary += "<GroundsOfSuspicion>";
				reportSummary += gos;
				reportSummary += "</GroundsOfSuspicion>";
				reportSummary += "<DetailsOfInvestigations>";
				reportSummary += doi;
				reportSummary += "</DetailsOfInvestigations>";
				reportSummary += "<LEAInformed>S</LEAInformed>";
				reportSummary += "<PriorityRating>P2</PriorityRating>";
				reportSummary += "<ReportCoverage>X</ReportCoverage>";
				reportSummary += "<AdditionalDocuments>N</AdditionalDocuments>";
				reportSummary += "</SuspicionDetails>";

				rsTran = stmtTransaction
						.executeQuery("select to_char(a.rcre_time,'YYYY-MM-DD') as tran_date,to_char(a.rcre_time,'HH24:MI:SS') as tran_time,'P' as tran_type,decode(purpose,'NEFT','F',decode(purpose,'DRFT','C',decode(purpose,'DD','C','Z'))) instrument_type,sol_desc bank_name,a.sol_id as sol_id,state_code as state_code,'IN' as bank_cntry_code,tot_cash_amt as amount_inr,'' as amount_foreign ,'INR' as crncy_code,decode(purpose,'NEFT','NATIONAL ELECTRONIC FUND TRANSFER',decode(purpose,'DRFT','DEMAND DRAFT',decode(purpose,'DD','DEMAND DRAFT','OTHERS'))) purpose_desc,nvl(purpose,'XXXXX') purpose_of_rem,walkin_name as cust_name,'XX' as cust_id,'XX' as account_no,walkin_addr as cust_address,'IN' cust_cntry_code,decode(walkin_id,'DL','E',decode(walkin_id,'PAN','C',decode(walkin_id,'VOTID','B','Z'))) as idType,nvl(walkin_pan,'') as pan from"
								+ " aml_walkin a,aml_sol b where a.sol_id=b.sol_id and walkin_name='"
								+ walkinName
								+ "' and walkin_dob='"
								+ walkinDob
								+ "' and walkin_id='"
								+ walkinId
								+ "' and to_date(a.rcre_time,'dd-mon-yy') between '"
								+ fromDate
								+ "' and '"
								+ toDate
								+ "' union all"
								+ " select to_char(a.rcre_time,'YYYY-MM-DD') as tran_date,to_char(a.rcre_time,'HH24:MI:SS') as tran_time,'R' as tran_type,decode(purpose,'NEFT','F',decode(purpose,'DRFT','C',decode(purpose,'DD','C','Z'))) instrument_type,sol_desc bank_name,a.sol_id as sol_id,bene_state as state_code,'IN' as bank_cntry_code,tot_cash_amt as amount_inr,'' as amount_foreign ,'INR' as crncy_code,decode(purpose,'NEFT','NATIONAL ELECTRONIC FUND TRANSFER',decode(purpose,'DRFT','DEMAND DRAFT',decode(purpose,'DD','DEMAND DRAFT','OTHERS'))) purpose_desc,nvl(purpose,'XXXXX') purpose_of_rem,bene_name as cust_name,'XX' as cust_id,'XX' as account_no,'XXX' as cust_address,'IN' cust_cntry_code,'Z' as idtype,'' pan from"
								+ " aml_walkin a,aml_sol b where a.sol_id=b.sol_id and walkin_name='"
								+ walkinName
								+ "' and walkin_dob='"
								+ walkinDob
								+ "' and walkin_id='"
								+ walkinId
								+ "' and to_date(a.rcre_time,'dd-mon-yy') between '"
								+ fromDate + "' and '" + toDate + "'");
				while (rsTran.next()) {
					reportSummary += "<Transaction>";
					reportSummary += "<TransactionDate>"
							+ rsTran.getString("tran_date")
							+ "</TransactionDate>";
					reportSummary += "<TransactionTime>"
							+ rsTran.getString("tran_time")
							+ "</TransactionTime>";
					reportSummary += "<TransactionType>"
							+ rsTran.getString("tran_type")
							+ "</TransactionType>";
					reportSummary += "<InstrumentType>"
							+ rsTran.getString("instrument_type")
							+ "</InstrumentType>";
					reportSummary += "<TransactionInstitutionName>"
							+ rsTran.getString("bank_name")
							+ "</TransactionInstitutionName>";
					reportSummary += "<TransactionInstitutionRefNum>"
							+ rsTran.getString("sol_id")
							+ "</TransactionInstitutionRefNum>";
					reportSummary += "<TransactionStateCode>"
							+ (checkState().contains(
									rsTran.getString("state_code")) ? rsTran
									.getString("state_code") : "XX")
							+ "</TransactionStateCode>";
					reportSummary += "<TransactionCountryCode>"
							+ (checkCountry().contains(
									rsTran.getString("bank_cntry_code")) ? rsTran
									.getString("bank_cntry_code") : "ZZ")
							+ "</TransactionCountryCode>";
					reportSummary += "<InstrumentCountryCode>"
							+ (checkCountry().contains(
									rsTran.getString("bank_cntry_code")) ? rsTran
									.getString("bank_cntry_code") : "ZZ")
							+ "</InstrumentCountryCode>";
					reportSummary += "<AmountRupees>"
							+ rsTran.getString("amount_inr")
							+ "</AmountRupees>";
					reportSummary += "<AmountForeignCurrency>0</AmountForeignCurrency>";
					reportSummary += "<CurrencyOfTransaction>"
							+ rsTran.getString("crncy_code")
							+ "</CurrencyOfTransaction>";
					reportSummary += "<PurposeOfTransaction>";
					reportSummary += rsTran.getString("purpose_desc") == null ? "XXXXX"
							: rsTran.getString("purpose_desc");
					reportSummary += "</PurposeOfTransaction>";
					reportSummary += "<PurposeCode>";
					reportSummary += "XXXXX";
					reportSummary += "</PurposeCode>";
					reportSummary += "<RiskRating>XX</RiskRating>";
					reportSummary += "<CustomerDetails>";
					reportSummary += "<CustomerName>"
							+ (rsTran.getString("cust_name") == null ? "XXX"
									: rsTran.getString("cust_name").length() > 80 ? (rsTran
											.getString("cust_name").substring(
											0, 79)) : rsTran
											.getString("cust_name"))
							+ "</CustomerName>";
					reportSummary += "<IdentificationType>";
					reportSummary += rsTran.getString("idtype");
					reportSummary += "</IdentificationType>";
					reportSummary += "<PAN>";
					reportSummary += rsTran.getString("pan");
					reportSummary += "</PAN>";
					reportSummary += "<CustomerAddress>";
					reportSummary += "<Address>";
					reportSummary += rsTran.getString("cust_address") == null ? "XX"
							: rsTran.getString("cust_address");
					reportSummary += "</Address>";
					reportSummary += "<StateCode>"
							+ (checkState().contains(
									rsTran.getString("state_code")) ? rsTran
									.getString("state_code") : "XX")
							+ "</StateCode>";
					reportSummary += "<CountryCode>"
							+ (checkCountry().contains(
									rsTran.getString("cust_cntry_Code")) ? rsTran
									.getString("cust_cntry_Code") : "ZZ")
							+ "</CountryCode>";
					reportSummary += "</CustomerAddress>";
					reportSummary += "<Phone>";
					reportSummary += "<Telephone></Telephone>";
					reportSummary += "<Mobile></Mobile>";
					reportSummary += "<Fax></Fax>";
					reportSummary += "</Phone>";
					reportSummary += "<Email></Email>";
					reportSummary += "</CustomerDetails>";
					reportSummary += "</Transaction>";
				}

				rsBranch = stmtBranch
						.executeQuery("select * from aml_walkin_branch_file a,aml_regulatory_state_master b where upper(trim(a.branch_address5)) = upper(trim(state))and branch_id='"
								+ solid
								+ "' AND CBWT_SEQ_NO = '"
								+ ticketId
								+ "'");
				while (rsBranch.next()) {
					reportSummary += "<Branch>";
					reportSummary += "<InstitutionName>Oriental Bank Of Commerce</InstitutionName>";
					reportSummary += "<InstitutionBranchName>"
							+ rsBranch.getString("NAME_OF_BRANCH")
							+ "</InstitutionBranchName>";
					reportSummary += "<InstitutionRefNum>"
							+ rsBranch.getString("BRANCH_REF_NO")
							+ "</InstitutionRefNum>";
					reportSummary += "<ReportingRole>B</ReportingRole>";
					reportSummary += "<BIC></BIC>";
					reportSummary += "<BranchAddress>" + "<Address>"
							+ rsBranch.getString("BRANCH_ADDRESS1") + " "
							+ rsBranch.getString("BRANCH_ADDRESS2")
							+ "</Address>" + "<City>"
							+ rsBranch.getString("BRANCH_ADDRESS4") + "</City>"
							+ "<StateCode>" + rsBranch.getString("state_code")
							+ "</StateCode>" + "<PinCode>"
							+ rsBranch.getString("BRANCH_PINCODE")
							+ "</PinCode>" + "<CountryCode>IN</CountryCode>"
							+ "</BranchAddress>";
					reportSummary += "<Phone>";
					reportSummary += "<Telephone>"
							+ rsBranch.getString("BRANCH_TELEPHONE")
							+ "</Telephone>";
					reportSummary += "<Mobile></Mobile>";
					reportSummary += "<Fax></Fax>";
					reportSummary += "</Phone>";
					reportSummary += "<Email></Email>";
					reportSummary += "<Remarks></Remarks>";
					reportSummary += "</Branch>";
				}
				reportSummary += "</Report>";

				report += reportSummary;

				stmtProcess
						.executeUpdate("update aml_walkin_ticket set REPORTED = 'Y' where ticket_id = '"
								+ ticketId + "'");
				connection.commit();
			}
			report += "</Batch>";

			report = report.replace("&", " &amp; ");

			ResourceBundle bundle = ResourceBundle
					.getBundle("com.idbi.intech.iaml.walkin.walkdtl");
			String dir = bundle.getString("WALKINDIR");

			FileWriter fw = new FileWriter(dir + ticketId + ".xml");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(report);
			bw.close();
			fw.close();
			bw = null;
			fw = null;

			System.out.println("File Created...");

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtBranch != null) {
					stmtBranch.close();
					stmtBranch = null;
				}
				if (stmtTransaction != null) {
					stmtTransaction.close();
					stmtTransaction = null;
				}
				if (stmtProcess != null) {
					stmtProcess.close();
					stmtProcess = null;
				}
				if (stmtCustomer != null) {
					stmtCustomer.close();
					stmtCustomer = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (rsBranch != null) {
					rsBranch.close();
					rsBranch = null;
				}
				if (rsTran != null) {
					rsTran.close();
					rsTran = null;
				}
				if (rsCustomer != null) {
					rsCustomer.close();
					rsCustomer = null;
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		WalkinTRF walkObj = new WalkinTRF();
		makeConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String tickId = "";
		String reportType = "";
		String gos = "NA";
		String doi = "NA";
		String soa = "XX";
		String poc = "N";
		String ct = "N";
		String nor = "N";
		String ft = "N";

		try {
			System.out.println("Please input the ticket id");
			tickId = br.readLine();
			System.out.println("Please input the report type(CTR/STR)");
			reportType = br.readLine().toUpperCase();
			if (reportType.equalsIgnoreCase("STR")) {
				System.out.println("Please input source of alert");
				soa = br.readLine();
				System.out
						.println("Please input suspicion due to proceeds of crime (Y/N)");
				poc = br.readLine().toUpperCase();
				System.out
						.println("Please input suspicion due to complex transaction (Y/N)");
				ct = br.readLine().toUpperCase();
				System.out
						.println("Please input suspicion due to no rationale (Y/N)");
				nor = br.readLine().toUpperCase();
				System.out
						.println("Please input suspicion due to financing of terrorism (Y/N)");
				ft = br.readLine().toUpperCase();
				System.out.println("Please input ground of suspicion");
				gos = br.readLine();
				System.out.println("Please input details of investigation");
				doi = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		walkObj.createFile(tickId, reportType, gos, doi, soa, poc, ct, nor, ft);
	}

}

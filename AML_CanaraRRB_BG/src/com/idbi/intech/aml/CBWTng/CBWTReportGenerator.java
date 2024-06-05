package com.idbi.intech.aml.CBWTng;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.CCR.CCRBranchDatabean;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CBWTReportGenerator implements Runnable {

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
		String sql = "select to_char(sysdate-30,'MM') as result from dual";

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
		String sql = "select to_char(sysdate-30,'YYYY') as result from dual";

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

	public String callCreateFile() {
		Statement stmt = null;
		ResultSet rs = null;
		String flg = null;
		try {
			stmt = connection.createStatement();

			rs = stmt
					.executeQuery("select decode(count(1),0,'N','Y') from cbwt_inward_receiver where process_flg = 'N'");
			while (rs.next()) {
				flg = rs.getString(1);
			}

			if (flg.equals("N")) {
				rs = stmt
						.executeQuery("select decode(count(1),0,'N','Y') from  cbwt_outward_sender where process_flg = 'N'");
				while (rs.next()) {
					flg = rs.getString(1);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return flg;
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

	public void createFile() {
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		PreparedStatement branchstat = null;
		CCRBranchDatabean ccrBranchDatabean = null;
		String bRefNo = "";
		Integer lineNumber = 0;
		String month = "";
		String year = "";
		String seqNextVal = "";
		Statement stmtBranch = null;
		Statement stmtCustomer = null;
		Statement stmtProcess = null;
		ResultSet rsBranch = null;
		Statement stmtTransaction = null;
		ResultSet rsTran = null;
		ResultSet rsCustomer = null;
		String custName = "";
		String sol = "";
		String dt = "";
		String occp = "";
		String address = "";

		try {
			stmt = connection.createStatement();

			sql = "select CBWT_FILE_SEQUENCE.NEXTVAL,to_char(sysdate,'YYYY-MM-DD') as dt from DUAL";
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				seqNextVal = rs.getString(1);
				dt = rs.getString(2);
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

			sql = "select distinct(B.SOL_ID) as sol,B.SOL_DESC,B.ADDR_1,B.ADDR_2,"
					+ " (select REF_DESC from AML_RCT where ref_rec_type = '01' and ref_code=b.CITY_CODE) as CITY_CODE,"
					+ " (select REF_DESC from AML_RCT where ref_rec_type = '02' and ref_code=b.STATE_CODE) as STATE_CODE,b.PIN_CODE,nvl(substr(b.UNIFORM_BR_CODE,0,7),b.sol_id) as UNIFORM_BR_CODE"
					+ " from AML_SOL b,cbwt_inward_receiver A where a.SOL_ID=b.SOL_ID"
					+ " union "
					+ " select distinct(B.SOL_ID) as sol,B.SOL_DESC,B.ADDR_1,B.ADDR_2,"
					+ "             (select REF_DESC from AML_RCT where ref_rec_type = '01' and ref_code=b.CITY_CODE) as CITY_CODE,"
					+ "			(select REF_DESC from AML_RCT where ref_rec_type = '02' and ref_code=b.STATE_CODE) as STATE_CODE,b.PIN_CODE,nvl(substr(b.UNIFORM_BR_CODE,0,7),b.sol_id) as UNIFORM_BR_CODE "
					+ "			from AML_SOL b,cbwt_outward_sender A where a.SOL_ID=b.SOL_ID";

			// System.out.println(sql);

			rs = stmt.executeQuery(sql);

			lineNumber = 1;

			sql = "INSERT INTO AML_CBWT_BRANCH_FILE (CBWT_SEQ_NO,RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD,LINE_NO, NAME_OF_BRANCH, BRANCH_REF_NO, "
					+ "UID_FIU, BRANCH_ADDRESS1, BRANCH_ADDRESS2,BRANCH_ADDRESS3, BRANCH_ADDRESS4, BRANCH_ADDRESS5,"
					+ " BRANCH_PINCODE, BRANCH_TELEPHONE, BRANCH_FAX, BRANCH_EMAIL,BRANCH_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			branchstat = connection.prepareStatement(sql);

			while (rs.next()) {
				sol = rs.getString("sol");

				bRefNo = rs.getString("UNIFORM_BR_CODE");

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
				ccrBranchDatabean.setTelephone("");
				ccrBranchDatabean.setUID("");
				ccrBranchDatabean.setYearOfReport(String
						.format("%-4s", year.toString()).substring(0, 4)
						.replace("\'", " ").replace("\"", " "));

				// Table entry....
				branchstat.setString(1, seqNextVal);
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
			rs = stmt.executeQuery("select * from aml_cbwt_header_master");
			while (rs.next()) {
				header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
						+ "<Batch>\n" + "<ReportType>"
						+ rs.getString("REPORTTYPE")
						+ "</ReportType>\n"
						+ "<ReportFormatType>"
						+ rs.getString("ReportFormatType")
						+ "</ReportFormatType>\n"
						+ "<BatchHeader>\n"
						+ "<DataStructureVersion>2</DataStructureVersion>\n"
						+ "<GenerationUtilityVersion>0.1</GenerationUtilityVersion>\n"
						+ "<DataSource>txt</DataSource>\n"
						+ "</BatchHeader>\n"
						+ "<ReportingEntity>\n"
						+ "<ReportingEntityName>"
						+ rs.getString("REPORTINGENTITYNAME")
						+ "</ReportingEntityName>\n"
						+ "<ReportingEntityCategory>"
						+ rs.getString("REPORTINGENTITYCATEGORY")
						+ "</ReportingEntityCategory>\n"
						+ "<RERegistrationNumber>"
						+ rs.getString("REREGISTRATIONNUMBER")
						+ "</RERegistrationNumber>\n"
						+ "<FIUREID>"
						+ rs.getString("FIUREID")
						+ "</FIUREID>"
						+ "</ReportingEntity>\n"
						+ "<PrincipalOfficer>\n"
						+ "<POName>"
						+ rs.getString("PONAME")
						+ "</POName>\n"
						+ "<PODesignation>"
						+ rs.getString("PODESIGNATION")
						+ "</PODesignation>\n"
						+ "<POAddress>\n"
						+ "<Address>"
						+ rs.getString("ADDRESS")
						+ "</Address>\n"
						+ "<City>"
						+ rs.getString("CITY")
						+ "</City>\n"
						+ "<StateCode>"
						+ rs.getString("STATECODE")
						+ "</StateCode>\n"
						+ "<PinCode>"
						+ rs.getString("PINCODE")
						+ "</PinCode>\n"
						+ "<CountryCode>"
						+ rs.getString("COUNTRY")
						+ "</CountryCode>\n"
						+ "</POAddress>\n"
						+ "<POPhone>\n"
						+ "<Telephone>"
						+ rs.getString("TELEPHONE")
						+ "</Telephone>\n"
						+ "<Mobile>"
						+ rs.getString("MOBILE")
						+ "</Mobile>\n"
						+ "<Fax>"
						+ rs.getString("FAX")
						+ "</Fax>\n"
						+ "</POPhone>\n"
						+ "<POEmail>"
						+ rs.getString("EMAIL")
						+ "</POEmail>\n"
						+ "</PrincipalOfficer>\n"
						+ "<BatchDetails>\n"
						+ "<BatchNumber>"
						+ seqNextVal
						+ "</BatchNumber>\n"
						+ "<BatchDate>"
						+ dt
						+ "</BatchDate>\n"
						+ "<MonthOfReport>"
						+ month
						+ "</MonthOfReport>\n"
						+ "<YearOfReport>"
						+ year
						+ "</YearOfReport>\n"
						+ "<OperationalMode>P</OperationalMode>\n"
						+ "<BatchType>N</BatchType>\n"
						+ "<OriginalBatchID>0</OriginalBatchID>\n"
						+ "<ReasonOfRevision>N</ReasonOfRevision>\n"
						+ "</BatchDetails>\n";
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
			String reportSummary = "";

			report += header;

			stmt = connection.createStatement();
			stmtTransaction = connection.createStatement();
			stmtCustomer = connection.createStatement();
			stmtBranch = connection.createStatement();
			stmtProcess = connection.createStatement();

			int iwrd = 0;

			rs = stmt
					.executeQuery("select distinct bill_id,sol_id,cust_name from cbwt_inward_receiver where process_flg = 'N' and rownum<1001");
			System.out.println("Processing Inward files...");
			while (rs.next()) {

				String bId = rs.getString("bill_id");
				String solid = rs.getString("sol_id");
				custName = rs.getString("cust_name") == null ? "" : rs
						.getString("cust_name");

				// System.out.println("Inward :: " + bId + " : " + (++iwrd));

				reportSummary = "<Report>\n";
				reportSummary += "<ReportSerialNum>" + (++cnt);
				reportSummary += "</ReportSerialNum>\n";
				reportSummary += "<OriginalReportSerialNum>0</OriginalReportSerialNum>\n";
				reportSummary += "<MainPersonName>"
						+ (custName.length() > 80 ? (custName.substring(0, 79))
								: custName) + "</MainPersonName>\n";

				rsTran = stmtTransaction
						.executeQuery("select to_char(tran_date,'YYYY-MM-DD') as tran_date,to_char(tran_time,'HH24:MI:SS') as tran_time,tran_type,instrument_type,bank_name,nvl(sol_id,'') as sol_id,bank_state_code,nvl(nvl(bank_cntry_code,cntry_code),'XX') as bank_cntry_code,amount_inr,amount_foreign,crncy_code,purpose_desc,nvl(purpose_of_rem,'XXXXX') purpose_of_rem,cust_name,nvl(cust_id,'NA') cust_id,account_no,"
								+ " (case when length(cust_address)<=15 then"
								+ "  rpad(cust_address,18,' ')"
								+ "  else"
								+ "  cust_address"
								+ "  end) cust_address,nvl(cust_cntry_code,'XX')cust_cntry_code,swiftcode from "
								+ "cbwt_inward_receiver where bill_id='"
								+ bId
								+ "' union all "
								+ "select to_char(tran_date,'YYYY-MM-DD') as tran_date,to_char(tran_time,'HH24:MI:SS') as tran_time,tran_type,instrument_type,nvl(bank_name,'XXXXXXXXX') bank_name,nvl(sol_id,'000') as sol_id,bank_state_code,nvl(nvl(bank_cntry_code,cntry_code),'XX') as bank_cntry_code,amount_inr,amount_foreign,crncy_code,purpose_desc,nvl(purpose_of_rem,'XXXXX') purpose_of_rem,cust_name,cust_id,account_no,"
								+ " (case when length(cust_address)<=15 then"
								+ "  rpad(cust_address,18,' ')"
								+ "  else"
								+ "  cust_address"
								+ "  end) cust_address,nvl(cust_cntry_code,'XX')cust_cntry_code,swiftcode from "
								+ "cbwt_inward_sender where bill_id='"
								+ bId
								+ "'");
				while (rsTran.next()) {
					reportSummary += "<Transaction>\n";
					reportSummary += "<TransactionDate>"
							+ rsTran.getString("tran_date")
							+ "</TransactionDate>\n";
					reportSummary += "<TransactionTime>"
							+ rsTran.getString("tran_time")
							+ "</TransactionTime>\n";
					reportSummary += "<TransactionRefNum>" + bId
							+ "</TransactionRefNum>\n";
					reportSummary += "<TransactionType>"
							+ rsTran.getString("tran_type")
							+ "</TransactionType>\n";
					reportSummary += "<InstrumentType>"
							+ rsTran.getString("instrument_type")
							+ "</InstrumentType>\n";
					reportSummary += "<TransactionInstitutionName>"
							+ rsTran.getString("bank_name")
							+ "</TransactionInstitutionName>\n";
					reportSummary += "<TransactionInstitutionRefNum>"
							+ rsTran.getString("swiftcode")
							+ "</TransactionInstitutionRefNum>\n";
					reportSummary += "<TransactionStateCode>"
							+ (checkState().contains(
									rsTran.getString("bank_state_code")) ? rsTran
									.getString("bank_state_code") : "XX")
							+ "</TransactionStateCode>\n";
					reportSummary += "<TransactionCountryCode>"
							+ (checkCountry().contains(
									rsTran.getString("bank_cntry_code")) ? rsTran
									.getString("bank_cntry_code") : "XX")
							+ "</TransactionCountryCode>\n";
					reportSummary += "<PaymentInstrumentNum></PaymentInstrumentNum>\n";
					reportSummary += "<PaymentInstrumentIssueInstitutionName></PaymentInstrumentIssueInstitutionName>\n";
					reportSummary += "<InstrumentIssueInstitutionRefNum></InstrumentIssueInstitutionRefNum>\n";
					reportSummary += "<InstrumentCountryCode>"
							+ (checkCountry().contains(
									rsTran.getString("bank_cntry_code")) ? rsTran
									.getString("bank_cntry_code") : "ZZ")
							+ "</InstrumentCountryCode>\n";
					reportSummary += "<AmountRupees>"
							+ rsTran.getString("amount_inr")
							+ "</AmountRupees>\n";
					reportSummary += "<AmountForeignCurrency>"
							+ rsTran.getString("amount_foreign")
							+ "</AmountForeignCurrency>\n";
					reportSummary += "<CurrencyOfTransaction>"
							+ rsTran.getString("crncy_code")
							+ "</CurrencyOfTransaction>\n";
					reportSummary += "<PurposeOfTransaction>";
					reportSummary += rsTran.getString("purpose_desc") == null ? ""
							: rsTran.getString("purpose_desc");
					reportSummary += "</PurposeOfTransaction>\n";
					reportSummary += "<PurposeCode>";
					reportSummary += rsTran.getString("purpose_of_rem") == null ? "XXXXX"
							: rsTran.getString("purpose_of_rem");
					reportSummary += "</PurposeCode>\n";

					String custId = rsTran.getString("cust_id") == null ? "NA"
							: rsTran.getString("cust_id");

					// System.out.println(custId);

					if (!custId.equals("NA")) {

						int checkCust = 0;
						rsCustomer = stmtCustomer
								.executeQuery("select count(1) from aml_cust_master where cust_id='"
										+ custId + "'");
						while (rsCustomer.next()) {
							checkCust = rsCustomer.getInt(1);
						}

						rsCustomer.close();

						if (checkCust > 0) {

							rsCustomer = stmtCustomer
									.executeQuery("select (select case when cust_risk_rating ='RL_1' then 'T3' when cust_risk_rating like 'RL1%' then 'T3' when cust_risk_rating ='RL_2' then 'T2' when cust_risk_rating like 'RL2%' then 'T2' when cust_risk_rating ='RL_3' then 'T1' when cust_risk_rating like 'RL3%' then 'T1' else 'XX' end risk from aml_kyc where cust_id=a.cust_id) risk,"
											+ " (select ref_desc from aml_rct where ref_rec_type='21' and ref_code=cust_occp_code) occupation,"
											+ " cust_dob,nvl(decode(cust_sex,'O','X',cust_sex),'X') gender,nvl(to_char(cust_dob,'YYYY-MM-DD'),'') dateofbirth,"
											+ " nvl(cust_commu_cntry_code,'ZZ') nationality,"
											+ " case when length(trim(nvl(cust_pan_no,cust_passport_no)))=10 then 'C'"
											+ " when length(trim(nvl(cust_pan_no,cust_passport_no)))<10 then 'A' else 'Z' end identfication_type,"
											+ " nvl(cust_pan_no,cust_passport_no) identification_num,"
											+ " (case when instr(cust_pan_no,'FORM')>0 then ''"
											+ " when cust_pan_no='PANNOTAVBL' then ''"
											+ " when cust_pan_no is null then '' "
											+ " else cust_pan_no end)as cust_pan_no,email_id,"
											+ " (case when length(cust_commu_addr1||''||cust_commu_addr2)<=15 then"
											+ " rpad(cust_commu_addr1||''||cust_commu_addr2,18,'X')"
											+ " else cust_commu_addr1||''||cust_commu_addr2 end) address,"
											+ " nvl((select ref_desc from aml_rct where ref_rec_type='01' and ref_code=cust_commu_city_code),'') city,"
											+ " nvl(decode(cust_commu_state_code,'.','XX',cust_commu_state_code),'XX') state,"
											+ " nvl(cust_commu_pin_code,'000000') pincode,nvl(cust_commu_cntry_code,'XX') cntrycode,"
											+ " (case when length(nvl(cust_commu_phone_num_1,cust_commu_phone_num_2))>11 then"
											+ " lpad(substr(replace(replace(replace(replace(replace(replace(trim(nvl(cust_commu_phone_num_1,cust_commu_phone_num_2)),'-',''),' ',''),'/',''),'.',''),',',''),':',''),3,length(nvl(cust_commu_phone_num_1,cust_commu_phone_num_2))),11,'0')"
											+ " else"
											+ " lpad(nvl(replace(replace(replace(replace(replace(replace(trim(nvl(cust_commu_phone_num_1,cust_commu_phone_num_2)),'-',''),' ',''),'/',''),'.',''),',',''),':',''),'00'),11,'0')  end) phone,"
											+ " (case when length(cust_pager_no)>11 then substr(cust_pager_no,3) else ''  end) mobile "
											+ " from aml_cust_master a "
											+ " where a.cust_id='"
											+ custId
											+ "'");
							while (rsCustomer.next()) {

								reportSummary += "<RiskRating>"
										+ rsCustomer.getString("risk")
										+ "</RiskRating>\n";
								reportSummary += "<CustomerDetails>\n";
								reportSummary += "<CustomerName>"
										+ (rsTran.getString("cust_name") == null ? ""
												: rsTran.getString("cust_name")
														.length() > 80 ? (rsTran
														.getString("cust_name")
														.substring(0, 79))
														: rsTran.getString("cust_name"))
										+ "</CustomerName>\n";
								reportSummary += "<CustomerId>"
										+ rsTran.getString("cust_id")
										+ "</CustomerId>\n";
								occp = rsCustomer.getString("occupation") == null ? ""
										: rsCustomer.getString("occupation");
								reportSummary += "<Occupation>"
										+ (occp.length() > 50 ? (occp
												.substring(0, 50).replace("&",
												" ")) : occp.replace("&", " "))
										+ "</Occupation>\n";
								String custDob = rsCustomer
										.getString("dateofbirth") == null ? ""
										: rsCustomer.getString("dateofbirth");

								if (!custDob.trim().equals("")) {
									if (!custDob.equals("null")) {
										reportSummary += "<DateOfBirth>";
										reportSummary += custDob;
										reportSummary += "</DateOfBirth>\n";
									}
								}
								reportSummary += "<Gender>"
										+ rsCustomer.getString("gender")
										+ "</Gender>\n";
								String nation = rsCustomer
										.getString("nationality");
								if (nation.contains(".")) {
									nation = "ZZ";
								}
								reportSummary += "<Nationality>" + nation
										+ "</Nationality>\n";
								reportSummary += "<IdentificationType>";
								reportSummary += rsCustomer
										.getString("identfication_type") == null ? "Z"
										: rsCustomer
												.getString("identfication_type");
								reportSummary += "</IdentificationType>\n";
								reportSummary += "<IdentificationNumber>"
										+ rsCustomer
												.getString("identification_num")
										+ "</IdentificationNumber>\n";
								reportSummary += "<IssuingAuthority></IssuingAuthority>\n";
								reportSummary += "<PlaceOfIssue></PlaceOfIssue>\n";
								reportSummary += "<PAN>"
										+ rsCustomer.getString("cust_pan_no")
										+ "</PAN>\n";
								reportSummary += "<UIN></UIN>\n";
								reportSummary += "<CustomerAddress>\n";
								reportSummary += "<Address>";
								address = rsCustomer.getString("address") == null ? " "
										: rsCustomer.getString("address");
								reportSummary += address;
								reportSummary += "</Address>\n";
								reportSummary += "<City>"
										+ rsCustomer.getString("city")
										+ "</City>\n";
								reportSummary += "<StateCode>"
										+ (checkState().contains(
												rsCustomer.getString("state")) ? rsCustomer
												.getString("state") : "XX")
										+ "</StateCode>\n";
								reportSummary += "<PinCode>"
										+ rsCustomer.getString("pincode")
										+ "</PinCode>\n";
								String ctryCode = rsCustomer
										.getString("cntryCode");
								if (ctryCode.contains(".")) {
									ctryCode = "ZZ";
								}
								ctryCode = checkCountry().contains(ctryCode) ? ctryCode
										: "ZZ";
								reportSummary += "<CountryCode>" + ctryCode
										+ "</CountryCode>\n";
								reportSummary += "</CustomerAddress>\n";
								reportSummary += "<Phone>\n";
								// reportSummary += "<Telephone>"
								// + rsCustomer.getString("phone")
								// + "</Telephone>\n";
								reportSummary += "<Telephone></Telephone>\n";
								reportSummary += "<Mobile>"
										+ (rsCustomer.getString("mobile") == null ? ""
												: rsCustomer
														.getString("mobile"))
										+ "</Mobile>\n";
								reportSummary += "<Fax></Fax>\n";
								reportSummary += "</Phone>\n";
								reportSummary += "<Email>"
										+ rsCustomer.getString("email_id")
										+ "</Email>\n";
								reportSummary += "</CustomerDetails>\n";
							}
						} else {
							reportSummary += "<RiskRating>XX</RiskRating>\n";
							reportSummary += "<CustomerDetails>\n";
							reportSummary += "<CustomerName>NA</CustomerName>\n";
							reportSummary += "<CustomerId></CustomerId>\n";
							reportSummary += "<CustomerAddress>\n";
							reportSummary += "<Address>XXXXXXXXXXXXXXXX</Address>\n";
							reportSummary += "<City></City>\n";
							reportSummary += "<StateCode>ZZ</StateCode>\n";
							reportSummary += "<PinCode></PinCode>\n";
							reportSummary += "<CountryCode>ZZ</CountryCode>\n";
							reportSummary += "</CustomerAddress>\n";
							reportSummary += "<Phone>\n";
							reportSummary += "<Telephone></Telephone>\n";
							reportSummary += "<Mobile></Mobile>\n";
							reportSummary += "<Fax></Fax>\n";
							reportSummary += "</Phone>\n";
							reportSummary += "<Email></Email>\n";
							reportSummary += "</CustomerDetails>\n";
						}
					} else {
						reportSummary += "<RiskRating>XX</RiskRating>\n";
						reportSummary += "<CustomerDetails>\n";
						reportSummary += "<CustomerName>"
								+ (rsTran.getString("cust_name") == null ? ""
										: rsTran.getString("cust_name")
												.length() > 80 ? (rsTran
												.getString("cust_name")
												.substring(0, 79)) : rsTran
												.getString("cust_name"))
								+ "</CustomerName>\n";
						reportSummary += "<CustomerId></CustomerId>\n";
						// reportSummary += "<Occupation></Occupation>";
						// reportSummary += "<Gender>X</Gender>";
						// reportSummary += "<Nationality>ZZ</Nationality>";
						// reportSummary += "<IdentificationType>";
						// reportSummary += "Z";
						// reportSummary += "</IdentificationType>";
						// reportSummary +=
						// "<IdentificationNumber></IdentificationNumber>";
						// reportSummary +=
						// "<IssuingAuthority></IssuingAuthority>";
						// reportSummary += "<PlaceOfIssue></PlaceOfIssue>";
						// reportSummary += "<PAN>XXXXX0000X</PAN>";
						// reportSummary += "<UIN></UIN>";
						reportSummary += "<CustomerAddress>\n";
						reportSummary += "<Address>";
						address = rsTran.getString("cust_address").trim() == null ? ""
								: rsTran.getString("cust_address");
						reportSummary += address;
						reportSummary += "</Address>\n";
						reportSummary += "<City></City>\n";
						reportSummary += "<StateCode>"
								+ (checkState().contains(
										rsTran.getString("bank_state_code")) ? rsTran
										.getString("bank_state_code") : "XX")
								+ "</StateCode>\n";
						reportSummary += "<PinCode></PinCode>\n";
						reportSummary += "<CountryCode>"
								+ (checkCountry().contains(
										rsTran.getString("cust_cntry_Code")) ? rsTran
										.getString("cust_cntry_Code") : "ZZ")
								+ "</CountryCode>\n";
						reportSummary += "</CustomerAddress>\n";
						reportSummary += "<Phone>\n";
						reportSummary += "<Telephone></Telephone>\n";
						reportSummary += "<Mobile></Mobile>\n";
						reportSummary += "<Fax></Fax>\n";
						reportSummary += "</Phone>\n";
						reportSummary += "<Email></Email>\n";
						reportSummary += "</CustomerDetails>\n";
					}

					String acctNo = rsTran.getString("account_no");

					if (acctNo != null) {
						reportSummary += "<AccountNumber>"
								+ rsTran.getString("account_no")
								+ "</AccountNumber>\n";
						reportSummary += "<AccountWithInstitutionName>"
								+ rsTran.getString("bank_name")
								+ "</AccountWithInstitutionName>\n";
						reportSummary += "<AccountWithInstitutionRefNum>"
								+ rsTran.getString("sol_id")
								+ "</AccountWithInstitutionRefNum>\n";
					}
					reportSummary += "<RelatedInstitutionName></RelatedInstitutionName>\n";
					reportSummary += "<InstitutionRelationFlag>X</InstitutionRelationFlag>\n";
					reportSummary += "<RelatedInstitutionRefNum></RelatedInstitutionRefNum>\n";
					reportSummary += "<Remarks></Remarks>\n";
					reportSummary += "</Transaction>\n";
				}

				rsBranch = stmtBranch
						.executeQuery("select * from aml_cbwt_branch_file a,aml_regulatory_state_master b where upper(trim(a.branch_address5)) = upper(trim(state))and branch_id='"
								+ solid
								+ "' AND CBWT_SEQ_NO = '"
								+ seqNextVal
								+ "'");
				while (rsBranch.next()) {
					reportSummary += "<Branch>\n";
					reportSummary += "<InstitutionName>IDBI BANK Ltd</InstitutionName>\n";
					reportSummary += "<InstitutionBranchName>"
							+ rsBranch.getString("NAME_OF_BRANCH")
							+ "</InstitutionBranchName>\n";
					reportSummary += "<InstitutionRefNum>"
							+ rsBranch.getString("BRANCH_REF_NO")
							+ "</InstitutionRefNum>\n";
					reportSummary += "<ReportingRole>B</ReportingRole>\n";
					reportSummary += "<BIC></BIC>\n";
					reportSummary += "<BranchAddress>\n" + "<Address>"
							+ rsBranch.getString("BRANCH_ADDRESS1") + " "
							+ rsBranch.getString("BRANCH_ADDRESS2")
							+ "</Address>\n" + "<City>"
							+ rsBranch.getString("BRANCH_ADDRESS4")
							+ "</City>\n" + "<StateCode>"
							+ rsBranch.getString("state_code")
							+ "</StateCode>\n" + "<PinCode>"
							+ rsBranch.getString("BRANCH_PINCODE")
							+ "</PinCode>\n"
							+ "<CountryCode>IN</CountryCode>\n"
							+ "</BranchAddress>\n";
					reportSummary += "<Phone>\n";
					reportSummary += "<Telephone></Telephone>\n";
					reportSummary += "<Mobile></Mobile>\n";
					reportSummary += "<Fax></Fax>\n";
					reportSummary += "</Phone>\n";
					reportSummary += "<Email></Email>\n";
					reportSummary += "<Remarks></Remarks>\n";
					reportSummary += "</Branch>\n";
				}

				reportSummary += "</Report>\n";

				report += reportSummary;

				stmtProcess
						.executeUpdate("update cbwt_inward_receiver set process_flg = 'Y' where BILL_ID = '"
								+ bId + "'");
				connection.commit();
			}

			System.out.println("Inward files processing completed...");

			// Outward remittance
			int outwrd = 0;

			rs = stmt
					.executeQuery("select distinct a.bill_id,a.sol_id,a.cust_name,b.cust_name as partyname from cbwt_outward_sender a,cbwt_outward_receiver b where a.bill_id=b.bill_id and  process_flg = 'N' and rownum<1001");
			System.out.println("Processing Outward Files...");
			while (rs.next()) {
				String bId = rs.getString("bill_id");
				String solid = rs.getString("sol_id");
				custName = rs.getString("cust_name") == null ? "" : rs
						.getString("cust_name");
				String partyName = rs.getString("partyname") == null ? "" : rs
						.getString("partyname");

				// System.out.println("Outward :: " + bId + " : " + (++outwrd));

				reportSummary = "<Report>\n";
				reportSummary += "<ReportSerialNum>" + (++cnt);
				reportSummary += "</ReportSerialNum>\n";
				reportSummary += "<OriginalReportSerialNum>0</OriginalReportSerialNum>\n";
				reportSummary += "<MainPersonName>"
						+ (custName.length() > 80 ? (custName.substring(0, 79))
								: custName) + "</MainPersonName>\n";

				rsTran = stmtTransaction
						.executeQuery("select to_char(tran_date,'YYYY-MM-DD') as tran_date,to_char(tran_time,'HH24:MI:SS') as tran_time,tran_type,instrument_type,nvl(bank_name,'XXXXXXXXX') bank_name,nvl(sol_id,'000') as sol_id,nvl(trim(bank_state_code),'XX') bank_state_code,nvl(nvl(bank_cntry_code,cntry_code),'XX') as bank_cntry_code,amount_inr,amount_foreign,crncy_code,purpose_desc,purpose_of_rem,cust_name,cust_id,account_no,"
								+ " (case when length(cust_address)<=15 then"
								+ " rpad(cust_address,18,' ')"
								+ " else"
								+ " cust_address"
								+ " end) cust_address,nvl(cust_cntry_code,'XX')cust_cntry_code,swiftcode from "
								+ "cbwt_outward_sender where bill_id='"
								+ bId
								+ "' union all "
								+ "select to_char(tran_date,'YYYY-MM-DD') as tran_date,to_char(tran_time,'HH24:MI:SS') as tran_time,tran_type,instrument_type,nvl(bank_name,'XXXXXXXXX') bank_name,nvl(sol_id,'000') as sol_id,nvl(trim(bank_state_code),'XX') bank_state_code,nvl(nvl(bank_cntry_code,cntry_code),'XX') as bank_cntry_code,amount_inr,amount_foreign,crncy_code,purpose_desc,purpose_of_rem,cust_name,nvl(cust_id,'NA') cust_id,account_no,"
								+ " (case when length(cust_address)<=15 then"
								+ " rpad(cust_address,18,' ')"
								+ " else"
								+ " cust_address"
								+ " end) cust_address,nvl(cust_cntry_code,'XX')cust_cntry_code,swiftcode from "
								+ " cbwt_outward_receiver where bill_id='"
								+ bId + "'");
				while (rsTran.next()) {
					reportSummary += "<Transaction>\n";
					reportSummary += "<TransactionDate>"
							+ rsTran.getString("tran_date")
							+ "</TransactionDate>\n";
					reportSummary += "<TransactionTime>"
							+ rsTran.getString("tran_time")
							+ "</TransactionTime>\n";
					reportSummary += "<TransactionRefNum>" + bId
							+ "</TransactionRefNum>\n";
					reportSummary += "<TransactionType>"
							+ rsTran.getString("tran_type")
							+ "</TransactionType>\n";
					reportSummary += "<InstrumentType>"
							+ rsTran.getString("instrument_type")
							+ "</InstrumentType>\n";
					reportSummary += "<TransactionInstitutionName>"
							+ rsTran.getString("bank_name")
							+ "</TransactionInstitutionName>\n";
					reportSummary += "<TransactionInstitutionRefNum>"
							+ rsTran.getString("swiftcode")
							+ "</TransactionInstitutionRefNum>\n";
					reportSummary += "<TransactionStateCode>"
							+ (checkState().contains(
									rsTran.getString("bank_state_code")) ? rsTran
									.getString("bank_state_code") : "XX")
							+ "</TransactionStateCode>\n";
					reportSummary += "<TransactionCountryCode>"
							+ (checkCountry().contains(
									rsTran.getString("bank_cntry_code")) ? rsTran
									.getString("bank_cntry_code") : "ZZ")
							+ "</TransactionCountryCode>\n";
					reportSummary += "<PaymentInstrumentNum></PaymentInstrumentNum>\n";
					reportSummary += "<PaymentInstrumentIssueInstitutionName></PaymentInstrumentIssueInstitutionName>\n";
					reportSummary += "<InstrumentIssueInstitutionRefNum></InstrumentIssueInstitutionRefNum>\n";
					reportSummary += "<InstrumentCountryCode>"
							+ (checkCountry().contains(
									rsTran.getString("bank_cntry_code")) ? rsTran
									.getString("bank_cntry_code") : "ZZ")
							+ "</InstrumentCountryCode>\n";
					reportSummary += "<AmountRupees>"
							+ rsTran.getString("amount_inr")
							+ "</AmountRupees>\n";
					reportSummary += "<AmountForeignCurrency>"
							+ rsTran.getString("amount_foreign")
							+ "</AmountForeignCurrency>\n";
					reportSummary += "<CurrencyOfTransaction>"
							+ rsTran.getString("crncy_code")
							+ "</CurrencyOfTransaction>\n";
					reportSummary += "<PurposeOfTransaction>";
					reportSummary += rsTran.getString("purpose_desc") == null ? ""
							: rsTran.getString("purpose_desc");
					reportSummary += "</PurposeOfTransaction>\n";
					reportSummary += "<PurposeCode>";
					reportSummary += rsTran.getString("purpose_of_rem") == null ? "XXXXX"
							: rsTran.getString("purpose_of_rem");
					reportSummary += "</PurposeCode>\n";

					String custId = rsTran.getString("cust_id") == null ? "NA"
							: rsTran.getString("cust_id");

					if (!custId.equals("NA")) {
						int checkCust = 0;
						rsCustomer = stmtCustomer
								.executeQuery("select count(1) from aml_cust_master where cust_id='"
										+ custId + "'");
						while (rsCustomer.next()) {
							checkCust = rsCustomer.getInt(1);
						}

						rsCustomer.close();

						if (checkCust > 0) {

							rsCustomer = stmtCustomer
									.executeQuery("select (select case when cust_risk_rating ='RL_1' then 'T3' when cust_risk_rating like 'RL1%' then 'T3' when cust_risk_rating ='RL_2' then 'T2' when cust_risk_rating like 'RL2%' then 'T2' when cust_risk_rating ='RL_3' then 'T1' when cust_risk_rating like 'RL3%' then 'T1' else 'XX' end risk from aml_kyc where cust_id=a.cust_id) risk,"
											+ " (select ref_desc from aml_rct where ref_rec_type='21' and ref_code=cust_occp_code) occupation,"
											+ " cust_dob,nvl(decode(cust_sex,'O','X',cust_sex),'X') gender,nvl(to_char(cust_dob,'YYYY-MM-DD'),'') dateofbirth,"
											+ " nvl(cust_commu_cntry_code,'ZZ') nationality,"
											+ " case when length(trim(nvl(cust_pan_no,cust_passport_no)))=10 then 'C'"
											+ " when length(trim(nvl(cust_pan_no,cust_passport_no)))<10 then 'A' else 'Z' end identfication_type,"
											+ " nvl(cust_pan_no,cust_passport_no) identification_num,"
											+ " (case when instr(cust_pan_no,'FORM')>0 then ''"
											+ " when cust_pan_no='PANNOTAVBL' then ''"
											+ " when cust_pan_no is null then '' "
											+ " else cust_pan_no end)as cust_pan_no,email_id,"
											+ " (case when length(cust_commu_addr1||''||cust_commu_addr2)<=15 then"
											+ " rpad(cust_commu_addr1||''||cust_commu_addr2,18,'X')"
											+ " else cust_commu_addr1||''||cust_commu_addr2 end) address,"
											+ " nvl((select ref_desc from aml_rct where ref_rec_type='01' and ref_code=cust_commu_city_code),'XXXX') city,"
											+ " nvl(decode(cust_commu_state_code,'.','XX',cust_commu_state_code),'XX') state,"
											+ " nvl(cust_commu_pin_code,'000000') pincode,nvl(cust_commu_cntry_code,'XX') cntrycode,"
											+ " (case when length(nvl(cust_commu_phone_num_1,cust_commu_phone_num_2))>11 then"
											+ " lpad(substr(replace(replace(replace(replace(replace(replace(trim(nvl(cust_commu_phone_num_1,cust_commu_phone_num_2)),'-',''),' ',''),'/',''),'.',''),',',''),':',''),3,length(nvl(cust_commu_phone_num_1,cust_commu_phone_num_2))),11,'0')"
											+ " else"
											+ " lpad(nvl(replace(replace(replace(replace(replace(replace(trim(nvl(cust_commu_phone_num_1,cust_commu_phone_num_2)),'-',''),' ',''),'/',''),'.',''),',',''),':',''),'00'),11,'0')  end) phone,"
											+ " (case when length(cust_pager_no)>11 then substr(cust_pager_no,3) else ''  end) mobile "
											+ " from aml_cust_master a "
											+ " where a.cust_id='"
											+ custId
											+ "'");
							while (rsCustomer.next()) {
								String occupation = rsCustomer
										.getString("occupation") == null ? ""
										: rsCustomer.getString("occupation");
								reportSummary += "<RiskRating>"
										+ rsCustomer.getString("risk")
										+ "</RiskRating>\n";
								reportSummary += "<CustomerDetails>\n";
								reportSummary += "<CustomerName>"
										+ (rsTran.getString("cust_name") == null ? " "
												: rsTran.getString("cust_name")
														.length() > 80 ? (rsTran
														.getString("cust_name")
														.substring(0, 79))
														: rsTran.getString("cust_name"))
										+ "</CustomerName>\n";
								reportSummary += "<CustomerId>"
										+ rsTran.getString("cust_id")
										+ "</CustomerId>\n";
								reportSummary += "<Occupation>"
										+ (occupation.length() > 50 ? (occupation
												.substring(0, 50).replace("&",
												" ")) : occupation.replace("&",
												" ")) + "</Occupation>\n";
								String custDob = rsCustomer
										.getString("dateofbirth") == null ? ""
										: rsCustomer.getString("dateofbirth");
								// System.out.println("dob:"+custDob);
								if (!custDob.trim().equals("")) {
									if (!custDob.equals("null")) {
										reportSummary += "<DateOfBirth>";
										reportSummary += custDob;
										reportSummary += "</DateOfBirth>\n";
									}
								}
								reportSummary += "<Gender>"
										+ rsCustomer.getString("gender")
										+ "</Gender>\n";
								String nation = rsCustomer
										.getString("nationality") == null ? "ZZ"
										: rsCustomer.getString("nationality");
								if (nation.contains(".")) {
									nation = "ZZ";
								}
								reportSummary += "<Nationality>" + nation
										+ "</Nationality>\n";
								reportSummary += "<IdentificationType>";
								reportSummary += rsCustomer
										.getString("identfication_type") == null ? "Z"
										: rsCustomer
												.getString("identfication_type");
								reportSummary += "</IdentificationType>\n";
								reportSummary += "<IdentificationNumber>"
										+ rsCustomer
												.getString("identification_num")
										+ "</IdentificationNumber>\n";
								reportSummary += "<IssuingAuthority></IssuingAuthority>\n";
								reportSummary += "<PlaceOfIssue></PlaceOfIssue>\n";
								reportSummary += "<PAN>"
										+ rsCustomer.getString("cust_pan_no")
										+ "</PAN>\n";
								reportSummary += "<UIN></UIN>\n";
								reportSummary += "<CustomerAddress>\n";
								reportSummary += "<Address>";
								address = rsCustomer.getString("address")
										.trim() == null ? "" : rsCustomer
										.getString("address");
								reportSummary += address;
								reportSummary += "</Address>\n";
								reportSummary += "<City>"
										+ rsCustomer.getString("city")
										+ "</City>\n";
								reportSummary += "<StateCode>"
										+ (checkState().contains(
												rsCustomer.getString("state")) ? rsCustomer
												.getString("state") : "XX")
										+ "</StateCode>\n";
								reportSummary += "<PinCode>"
										+ rsCustomer.getString("pincode")
										+ "</PinCode>\n";
								String ctryCode = rsCustomer
										.getString("cntryCode");
								if (ctryCode.contains(".")) {
									ctryCode = "ZZ";
								}
								ctryCode = checkCountry().contains(ctryCode) ? ctryCode
										: "ZZ";
								reportSummary += "<CountryCode>" + ctryCode
										+ "</CountryCode>\n";
								reportSummary += "</CustomerAddress>\n";
								reportSummary += "<Phone>\n";
								// reportSummary += "<Telephone>"
								// + rsCustomer.getString("phone")
								// + "</Telephone>\n";
								reportSummary += "<Telephone></Telephone>\n";
								reportSummary += "<Mobile>"
										+ (rsCustomer.getString("mobile") == null ? ""
												: rsCustomer
														.getString("mobile"))
										+ "</Mobile>\n";
								reportSummary += "<Fax></Fax>\n";
								reportSummary += "</Phone>\n";
								reportSummary += "<Email>"
										+ rsCustomer.getString("email_id")
										+ "</Email>\n";
								reportSummary += "</CustomerDetails>\n";
							}
						} else {
							reportSummary += "<RiskRating>XX</RiskRating>\n";
							reportSummary += "<CustomerDetails>\n";
							reportSummary += "<CustomerName></CustomerName>\n";
							reportSummary += "<CustomerId></CustomerId>\n";
							reportSummary += "<CustomerAddress>\n";
							reportSummary += "<Address>XXXXXXXXXXXXXXXX</Address>\n";
							reportSummary += "<City></City>\n";
							reportSummary += "<StateCode>ZZ</StateCode>\n";
							reportSummary += "<PinCode></PinCode>\n";
							reportSummary += "<CountryCode>ZZ</CountryCode>\n";
							reportSummary += "</CustomerAddress>\n";
							reportSummary += "<Phone>\n";
							reportSummary += "<Telephone></Telephone>\n";
							reportSummary += "<Mobile></Mobile>\n";
							reportSummary += "<Fax></Fax>\n";
							reportSummary += "</Phone>\n";
							reportSummary += "<Email></Email>\n";
							reportSummary += "</CustomerDetails>\n";
						}
					} else {
						reportSummary += "<RiskRating>XX</RiskRating>\n";
						reportSummary += "<CustomerDetails>\n";
						reportSummary += "<CustomerName>"
								+ (partyName.length() > 80 ? (partyName
										.substring(0, 79)) : partyName)
								+ "</CustomerName>\n";
						reportSummary += "<CustomerId></CustomerId>\n";
						// reportSummary += "<Occupation></Occupation>";
						// reportSummary += "<Gender>X</Gender>";
						// reportSummary += "<Nationality>ZZ</Nationality>";
						// reportSummary += "<IdentificationType>";
						// reportSummary += "Z";
						// reportSummary += "</IdentificationType>";
						// reportSummary +=
						// "<IdentificationNumber></IdentificationNumber>";
						// reportSummary +=
						// "<IssuingAuthority></IssuingAuthority>";
						// reportSummary += "<PlaceOfIssue></PlaceOfIssue>";
						// reportSummary += "<PAN></PAN>";
						// reportSummary += "<UIN></UIN>";
						reportSummary += "<CustomerAddress>\n";
						reportSummary += "<Address>";
						address = rsTran.getString("cust_address").trim() == null ? ""
								: rsTran.getString("cust_address");
						reportSummary += address;
						reportSummary += "</Address>\n";
						reportSummary += "<City></City>\n";
						reportSummary += "<StateCode>"
								+ (checkState().contains(
										rsTran.getString("bank_state_code")) ? rsTran
										.getString("bank_state_code") : "XX")
								+ "</StateCode>\n";
						reportSummary += "<PinCode></PinCode>\n";
						reportSummary += "<CountryCode>"
								+ (checkCountry().contains(
										rsTran.getString("cust_cntry_Code")) ? rsTran
										.getString("cust_cntry_Code") : "ZZ")
								+ "</CountryCode>\n";
						reportSummary += "</CustomerAddress>\n";
						reportSummary += "<Phone>\n";
						reportSummary += "<Telephone></Telephone>\n";
						reportSummary += "<Mobile></Mobile>\n";
						reportSummary += "<Fax></Fax>\n";
						reportSummary += "</Phone>";
						reportSummary += "<Email></Email>\n";
						reportSummary += "</CustomerDetails>\n";
					}

					String acctNo = rsTran.getString("account_no");

					if (acctNo != null) {
						reportSummary += "<AccountNumber>"
								+ rsTran.getString("account_no")
								+ "</AccountNumber>\n";
						reportSummary += "<AccountWithInstitutionName>"
								+ rsTran.getString("bank_name")
								+ "</AccountWithInstitutionName>\n";
						reportSummary += "<AccountWithInstitutionRefNum>"
								+ rsTran.getString("sol_id")
								+ "</AccountWithInstitutionRefNum>\n";
					}
					reportSummary += "<RelatedInstitutionName></RelatedInstitutionName>\n";
					reportSummary += "<InstitutionRelationFlag>X</InstitutionRelationFlag>\n";
					reportSummary += "<RelatedInstitutionRefNum></RelatedInstitutionRefNum>\n";
					reportSummary += "<Remarks></Remarks>\n";
					reportSummary += "</Transaction>\n";
				}

				rsBranch = stmtBranch
						.executeQuery("select * from aml_cbwt_branch_file a,aml_regulatory_state_master b where upper(trim(a.branch_address5)) = upper(trim(state))and branch_id='"
								+ solid
								+ "' AND CBWT_SEQ_NO = '"
								+ seqNextVal
								+ "'");
				while (rsBranch.next()) {
					reportSummary += "<Branch>\n";
					reportSummary += "<InstitutionName>IDBI BANK Ltd</InstitutionName>\n";
					reportSummary += "<InstitutionBranchName>"
							+ rsBranch.getString("NAME_OF_BRANCH")
							+ "</InstitutionBranchName>\n";
					reportSummary += "<InstitutionRefNum>"
							+ rsBranch.getString("BRANCH_REF_NO")
							+ "</InstitutionRefNum>\n";
					reportSummary += "<ReportingRole>B</ReportingRole>\n";
					reportSummary += "<BIC></BIC>\n";
					reportSummary += "<BranchAddress>\n" + "<Address>"
							+ rsBranch.getString("BRANCH_ADDRESS1") + " "
							+ rsBranch.getString("BRANCH_ADDRESS2")
							+ "</Address>\n" + "<City>"
							+ rsBranch.getString("BRANCH_ADDRESS4")
							+ "</City>\n" + "<StateCode>"
							+ rsBranch.getString("state_code")
							+ "</StateCode>\n" + "<PinCode>"
							+ rsBranch.getString("BRANCH_PINCODE")
							+ "</PinCode>\n"
							+ "<CountryCode>IN</CountryCode>\n"
							+ "</BranchAddress>\n";
					reportSummary += "<Phone>\n";
					reportSummary += "<Telephone></Telephone>\n";
					reportSummary += "<Mobile></Mobile>\n";
					reportSummary += "<Fax></Fax>\n";
					reportSummary += "</Phone>\n";
					reportSummary += "<Email></Email>\n";
					reportSummary += "<Remarks></Remarks>\n";
					reportSummary += "</Branch>\n";
				}

				reportSummary += "</Report>\n";

				report += reportSummary;

				// add updation logic
				stmtProcess
						.executeUpdate("update cbwt_outward_sender set process_flg = 'Y' where BILL_ID = '"
								+ bId + "'");
				connection.commit();
			}

			report += "</Batch>\n";

			report = report.replace("&", " &amp; ");

			System.out.println("Outward Files processing Completed...");

			ResourceBundle bundle = ResourceBundle
					.getBundle("com.idbi.intech.aml.CBWTng.cbwtdtl");
			String dir = bundle.getString("CBWTDIR");

			FileWriter fw = new FileWriter(dir + "CBWT_EFT" + seqNextVal
					+ ".xml");
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
		CBWTReportGenerator cbwtObj = new CBWTReportGenerator();
		makeConnection();
		Thread t = new Thread(cbwtObj);
		t.start();
	}

	@Override
	public void run() {
		while (true) {
			if (callCreateFile().equals("Y")) {
				createFile();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

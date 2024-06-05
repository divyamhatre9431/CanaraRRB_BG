package com.idbi.intech.iaml.CCR;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class CCRGenerator {
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
		String sql = "select to_char(sysdate-30,'yyyy') as result from dual";// ,
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

	public void createFile() {
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
		String seqNextVal = "";
		Statement stmtBranch = null;
		Statement stmtReport = null;
		ResultSet rsBranch = null;
		Statement stmtTransaction = null;
		ResultSet rsTran = null;
		ResultSet rsReport = null;
		String sol = "";
		String dt = "";

		try {
			stmt = connection.createStatement();

			sql = "select CCR_FILE_SEQUENCE.NEXTVAL,to_char(sysdate,'YYYY-MM-DD') as dt from DUAL";
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
					+ "(select REF_DESC from AML_RCT where ref_rec_type = '01' and ref_code=b.CITY_CODE) as CITY_CODE,"
					+ "(select REF_DESC from AML_RCT where ref_rec_type = '02' and ref_code=b.STATE_CODE) as STATE_CODE,b.PIN_CODE,nvl(b.UNIFORM_BR_CODE,'0000000') as UNIFORM_BR_CODE "
					+ " from P_AML_SOL b,AML_CCR_TBL A where a.SOL=b.SOL_ID";

			rs = stmt.executeQuery(sql);

			lineNumber = 1;

			sql = "INSERT INTO AML_CCR_BRANCH_FILE (CTR_SEQ_NO,RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD,LINE_NO, NAME_OF_BRANCH, BRANCH_REF_NO, "
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

			String demon = "";

			String two2K = "";
			String thouK = "";
			String five5K = "";
			String hundK = "";
			String fifK = "";
			String twenK = "";
			String tenK = "";
			String fiveK = "";
			String dod = "";
			String chest = "";
			String firNo = "";
			String tenderName = "";
			String tenderAc = "";
			String priority = "";
			String breach = "";
			String fir = "";
			String srlNo = "";
			String solId = "";

			sql = "select DENOMATION,to_char(to_date(DETECTION_DATE,'DD-MM-YY'),'YYYY-MM-DD')as dod,"
					+ "(select decode(count(1),0,'B','C') "
					+ "from aml_ccr_tbl  where sol = a.sol and instr(upper(branch_cc),"
					+ "'CURRENCY CHEST')>0) as chest,"
					+ "decode(upper(a.FIR),'NON-FIR','N','Y') as fir,fir_no,TENDERER_NAME,"
					+ "TENDERER_AC_NO,"
					+ "'XX' as priority,serial_no,sol from aml_ccr_tbl a";

			stmt = connection.createStatement();
			reportStat = connection
					.prepareStatement("insert into aml_ccr_report_file values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				demon = rs.getString("DENOMATION") == null ? "0" : rs
						.getString("DENOMATION");
				dod = rs.getString("dod");
				chest = rs.getString("chest");
				fir = rs.getString("fir");
				firNo = rs.getString("fir_no");
				tenderName = rs.getString("tenderer_name")==null?"":rs.getString("tenderer_name");
				tenderAc = rs.getString("tenderer_ac_no");
				priority = rs.getString("priority");
				srlNo = rs.getString("serial_no");
				solId = rs.getString("sol");

				two2K = "0";
				thouK = "0";
				five5K = "0";
				hundK = "0";
				fifK = "0";
				twenK = "0";
				tenK = "0";
				fiveK = "0";
				
				if (demon.equals("2000")) {
					two2K = "1";
					thouK = "0";
					five5K = "0";
					hundK = "0";
					fifK = "0";
					twenK = "0";
					tenK = "0";
					fiveK = "0";
				}

				if (demon.equals("1000")) {
					two2K = "0";
					thouK = "1";
					five5K = "0";
					hundK = "0";
					fifK = "0";
					twenK = "0";
					tenK = "0";
					fiveK = "0";
				}
				if (demon.equals("500")) {
					two2K = "0";
					thouK = "0";
					five5K = "1";
					hundK = "0";
					fifK = "0";
					twenK = "0";
					tenK = "0";
					fiveK = "0";
				}
				if (demon.equals("100")) {
					two2K = "0";
					thouK = "0";
					five5K = "0";
					hundK = "1";
					fifK = "0";
					twenK = "0";
					tenK = "0";
					fiveK = "0";
				}
				if (demon.equals("50")) {
					two2K = "0";
					thouK = "0";
					five5K = "0";
					hundK = "0";
					fifK = "1";
					twenK = "0";
					tenK = "0";
					fiveK = "0";
				}
				if (demon.equals("20")) {
					two2K = "0";
					thouK = "0";
					five5K = "0";
					hundK = "0";
					fifK = "0";
					twenK = "1";
					tenK = "0";
					fiveK = "0";
				}
				if (demon.equals("10")) {
					two2K = "0";
					thouK = "0";
					five5K = "0";
					hundK = "0";
					fifK = "0";
					twenK = "0";
					tenK = "1";
					fiveK = "0";
				}
				if (demon.equals("5")) {
					two2K = "0";
					thouK = "0";
					five5K = "0";
					hundK = "0";
					fifK = "0";
					twenK = "0";
					tenK = "0";
					fiveK = "1";
				}

				reportStat.setString(1, seqNextVal);
				reportStat.setString(2, thouK);
				reportStat.setString(3, five5K);
				reportStat.setString(4, hundK);
				reportStat.setString(5, fifK);
				reportStat.setString(6, twenK);
				reportStat.setString(7, tenK);
				reportStat.setString(8, fiveK);
				reportStat.setString(9, demon);
				reportStat.setString(10, dod);
				reportStat.setString(11, dod);
				reportStat.setString(12, chest);
				reportStat.setString(13, fir);
				reportStat.setString(14, firNo);
				reportStat.setString(15, tenderName.replace("&", "&amp;"));
				reportStat.setString(16, tenderAc);
				reportStat.setString(17, priority);
				reportStat.setString(18, srlNo);
				reportStat.setString(19, solId);
				reportStat.setString(20, two2K);
				reportStat.executeUpdate();
			}

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (reportStat != null) {
				reportStat.close();
				reportStat = null;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			sql = "select denomation,serial_no,secu_breached,sol,to_char(DETECTION_DATE,'YYYY-MM-DD') as DETECTION_DATE,TENDERER_NAME from aml_ccr_tbl";

			String remark = "";
			String detectDate = "";
			String tendName = "";
			String repSol = "";

			stmt = connection.createStatement();

			tranStat = connection
					.prepareStatement("insert into aml_ccr_tran_file values (?,?,?,?,?,?,?)");

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				demon = rs.getString("denomation");
				
				srlNo = rs.getString("serial_no");
				remark = rs.getString("secu_breached")==null?"-":rs.getString("secu_breached").replace("&", "&amp;");
				repSol = rs.getString("sol");
				tendName = rs.getString("TENDERER_NAME")==null?"":rs.getString("TENDERER_NAME");
				detectDate = rs.getString("DETECTION_DATE");

				tranStat.setString(1, seqNextVal);
				tranStat.setString(2, demon);
				tranStat.setString(3, srlNo);
				tranStat.setString(4, remark.replace("&", "&amp;"));
				tranStat.setString(5, repSol);
				tranStat.setString(6, detectDate);
				tranStat.setString(7, tendName.replace("&", "&amp;"));

				tranStat.executeUpdate();

			}

			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (tranStat != null) {
				tranStat.close();
				tranStat = null;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			connection.commit();

			// Creating Report

			// creating Header
			String header = "";

			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from aml_ccr_header_master");
			while (rs.next()) {
				header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
						+ "<Batch>" + "<ReportType>"
						+ rs.getString("REPORTTYPE")
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
						+ seqNextVal
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

			stmt = connection.createStatement();
			stmtBranch = connection.createStatement();
			stmtTransaction = connection.createStatement();
			stmtReport = connection.createStatement();

			String report = "";
			int cnt = 0;
			String branch = "";
			String reportSummmary = "";
			String noteSrlNo = "";
			String tranDtls = "";

			report += header;

			rs = stmt
					.executeQuery("select distinct sol_id,DATEOFDETECTION,TENDERINGPERSON from aml_ccr_report_file where ctr_seq_no='"
							+ seqNextVal + "'");
			while (rs.next()) {
				// noteSrlNo = rs.getString("SERIAL_NO");

				report += "<Report>"
						+ "<ReportSerialNum>"
						+ (++cnt)
						+ "</ReportSerialNum>"
						+ "<OriginalReportSerialNum>0</OriginalReportSerialNum>";
				rsBranch = stmtBranch
						.executeQuery("select * from aml_ccr_branch_file a,aml_regulatory_state_master b where upper(trim(a.branch_address5)) = upper(trim(state))and branch_id='"
								+ rs.getString("sol_id")
								+ "' AND CTR_SEQ_NO = '" + seqNextVal + "'");
				while (rsBranch.next()) {
					branch = "<Branch>"
							+ "<BranchRefNumType>Z</BranchRefNumType>"
							+ "<BranchRefNum>"
							+ rsBranch.getString("BRANCH_REF_NO")
							+ "</BranchRefNum>"
							+ "<BranchDetails>"
							+ "<BranchName>"
							+ rsBranch.getString("NAME_OF_BRANCH")
							+ "</BranchName>"
							+ "<BranchAddress>"
							+ "<Address>"
							+ rsBranch.getString("BRANCH_ADDRESS1")
							+ " "
							+ rsBranch.getString("BRANCH_ADDRESS2")
							+ "</Address>"
							+ "<City>"
							+ rsBranch.getString("BRANCH_ADDRESS4")
							+ "</City>"
							+ "<StateCode>"
							+ rsBranch.getString("state_code")
							+ "</StateCode>"
							+ "<PinCode>"
							+ rsBranch.getString("BRANCH_PINCODE")
							+ "</PinCode>"
							+ "<CountryCode>IN</CountryCode>"
							+ "</BranchAddress>"
							+ "<BranchPhone>"
							+ "<Telephone>"
							+ rsBranch.getString("BRANCH_TELEPHONE")
							+ "</Telephone>"
							+ "<Mobile></Mobile>"
							+ "<Fax>"
							+ rsBranch.getString("BRANCH_FAX")
							+ "</Fax>"
							+ "</BranchPhone>"
							+ "<BranchEmail>"
							+ rsBranch.getString("BRANCH_EMAIL")
							+ "</BranchEmail>"
							+ "</BranchDetails>"
							+ "</Branch>";
				}
				report += branch;

				String dDetect = rs.getString("DATEOFDETECTION");
				String dSol = rs.getString("sol_id");
				String dTend = rs.getString("TENDERINGPERSON");

				rsReport = stmtReport
						.executeQuery("select sum(inr2000notecount) inr2000notecount,sum(inr1000notecount) inr1000notecount,sum(inr500notecount) inr500notecount,sum(inr100notecount) inr100notecount,sum(inr50notecount) inr50notecount,sum(inr20notecount) inr20notecount,sum(inr10notecount) inr10notecount,sum(inr5notecount) inr5notecount,sum(ficnvalue) ficnvalue,policeinformed,policereportdetail,ctr_seq_no,accountnumber,PRIORITYRATING,DATEOFTENDERING,DETECTEDAT from aml_ccr_report_file where  dateofdetection='"
								+ dDetect
								+ "' and sol_id='"
								+ dSol
								+ "' and tenderingperson='"
								+ dTend
								+ "' and ctr_seq_no='"
								+ seqNextVal
								+ "' group by policeinformed,policereportdetail,ctr_seq_no,accountnumber,PRIORITYRATING,DATEOFTENDERING,DETECTEDAT");

				while (rsReport.next()) {
					reportSummmary = "<ReportSummary>" + "<INR2000NoteCount>"
							+ rsReport.getString("INR2000NOTECOUNT")
							+ "</INR2000NoteCount>" + "<INR1000NoteCount>"
							+ rsReport.getString("INR1000NOTECOUNT")
							+ "</INR1000NoteCount>" + "<INR500NoteCount>"
							+ rsReport.getString("INR500NOTECOUNT")
							+ "</INR500NoteCount>" + "<INR100NoteCount>"
							+ rsReport.getString("INR100NOTECOUNT")
							+ "</INR100NoteCount>" + "<INR50NoteCount>"
							+ rsReport.getString("INR50NOTECOUNT")
							+ "</INR50NoteCount>" + "<INR20NoteCount>"
							+ rsReport.getString("INR20NOTECOUNT")
							+ "</INR20NoteCount>" + "<INR10NoteCount>"
							+ rsReport.getString("INR10NOTECOUNT")
							+ "</INR10NoteCount>" + "<INR5NoteCount>"
							+ rsReport.getString("INR5NOTECOUNT")
							+ "</INR5NoteCount>" + "<FICNValue>"
							+ rsReport.getString("FICNVALUE") + "</FICNValue>"
							+ "<DateOfTendering>"
							+ dDetect
							+ "</DateOfTendering>" + "<DateOfDetection>"
							+ dDetect
							+ "</DateOfDetection>" + "<DetectedAt>"
							+ rsReport.getString("DETECTEDAT")
							+ "</DetectedAt>" + "<PoliceInformed>"
							+ rsReport.getString("POLICEINFORMED")
							+ "</PoliceInformed>" + "<PoliceReportDetail>"
							+ rsReport.getString("POLICEREPORTDETAIL")
							+ "</PoliceReportDetail>" + "<TenderingPerson>"
							+ dTend
							+ "</TenderingPerson>";
					if (rsReport.getString("ACCOUNTNUMBER") != null)
						reportSummmary += "<AccountNumber>"
								+ rsReport.getString("ACCOUNTNUMBER")
								+ "</AccountNumber>";

					reportSummmary += " <PriorityRating>"
							+ rsReport.getString("PRIORITYRATING")
							+ "</PriorityRating>" + "</ReportSummary>";
				}

				report += reportSummmary;

				rsTran = stmtTransaction
						.executeQuery("select * from aml_ccr_tran_file where ctr_seq_no='"
								+ seqNextVal
								+ "' and sol='"
								+ rs.getString("sol_id")
								+ "' and detection_date='"
								+ rs.getString("DATEOFDETECTION")
								+ "' and tenderer_name='"
								+ rs.getString("TENDERINGPERSON") + "'");
				while (rsTran.next()) {
					String cRemark = rsTran.getString("CURRENCYREMARKS");

					if (cRemark.length() > 49) {
						cRemark = cRemark.substring(0, 49);
					}

					tranDtls = "<TransactionDetails>" + "<Denomination>"
							+ rsTran.getString("DENOMINATION")
							+ "</Denomination>" + "<CurrencySerialNum>"
							+ rsTran.getString("CURRENCYSERIALNUM")
							+ "</CurrencySerialNum>" + "<CurrencyRemarks>"
							+ cRemark + "</CurrencyRemarks>"
							+ "</TransactionDetails>";

					report += tranDtls;
				}

				report += "</Report>";
			}

			report += "</Batch>";

			//System.out.println(report);

			ResourceBundle bundle = ResourceBundle
					.getBundle("com.idbi.intech.iaml.CCR.ccrdtl");
			String dir = bundle.getString("CCRDIR");

			FileWriter fw = new FileWriter(dir + seqNextVal + ".xml");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(report);
			bw.close();
			fw.close();

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
				if (stmtBranch != null) {
					stmtBranch.close();
					stmtBranch = null;
				}
				if (stmtTransaction != null) {
					stmtTransaction.close();
					stmtTransaction = null;
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

	public static void main(String args[]) {
		makeConnection();
		CCRGenerator ccrObj = new CCRGenerator();
		ccrObj.createFile();
	}

}

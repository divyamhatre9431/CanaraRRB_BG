package com.idbi.intech.aml.bg_process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBException;

import com.idbi.intech.aml.CTR.GenerateCTR;
import com.idbi.intech.aml.util.InfoLogger;
import com.idbi.intech.aml.util.InfoLoggerDebug;
import com.idbi.intech.aml.util.InfoLoggerExceptions;
import com.idbi.intech.aml.util.InfoLoggerNormal;
import com.idbi.intech.aml.util.InfoLoggerTrace;
import com.idbi.intech.aml.util.InfoLoggerVerbose;
import com.idbi.intech.aml.util.User;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class GenerateCTRFiles implements Runnable {

	private static String fromDate;
	private static String toDate;
	private static Connection con_aml = null;
	private static String ctrId;

	// Logger Parameters
	static String logFileName = InfoLogger.generateLogFileName(
			"GenerateCTRFiles", "AMLBGProcess");
	static User user = new User();
	static int logLevel = 0;
	static InfoLogger log = null;
	// Logger Parameters End.

	// private static ResourceBundle bundle_aml =
	// ResourceBundle.getBundle("com.idbi.intech.aml.bg_process.aml11gdb");

	// private static ResourceBundle bundle_aml =
	// ResourceBundle.getBundle("com.idbi.intech.aml.bg_process.aml_live");
	private static ResourceBundle bundle_aml = ResourceBundle
			.getBundle("com.idbi.intech.aml.bg_process.aml_live");

	private static GenerateCTRFiles process_run = new GenerateCTRFiles();

	// private static String logFile =
	// BgProcessLogger.generateLogFileName("GenerateCTRFiles");

	/**
	 * This method is for Setting Database Connection.
	 */
	private static void LoadDatabaseAml() {
		// BgProcessLogger.writeToFile(logFile,
		// "GenerateCTRFiles","LoadDatabaseAml","Start");
		//log.logVerboseText("GenerateCTRFiles", "LoadDatabaseAml", "Start");
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLiveThread();
			con_aml.setAutoCommit(false);

			// Load Resource Bundle
			bundle_aml = ResourceBundle.getBundle("AMLProp");
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception :" + sqlExp);
		}
		// try {
		// Class.forName("oracle.jdbc.driver.OracleDriver");
		// String connection = bundle_aml.getString("aml_conn");
		// String ip = bundle_aml.getString("aml_ip");
		// String port = bundle_aml.getString("aml_port");
		// String dbname = bundle_aml.getString("aml_dbname");
		// String username = bundle_aml.getString("aml_username");
		// String password = bundle_aml.getString("aml_password");
		// con_aml = DriverManager.getConnection(connection + ip + port
		// + dbname, username, password);
		// con_aml.setAutoCommit(false);
		// } catch (SQLException sqlExp) {
		// log.logExceptionText("GenerateCTRFiles", "LoadDatabaseAml",
		// "Exception e:"+sqlExp);
		//
		//
		// } catch (ClassNotFoundException cnfExp) {
		// log.logExceptionText("GenerateCTRFiles", "LoadDatabaseAml",
		// "Exception e:"+cnfExp);
		//
		//
		// }
		//log.logVerboseText("GenerateCTRFiles", "LoadDatabaseAml", "End");
	}

	/**
	 * This is for setting stored procedure for generate CTR
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static boolean callSPForGenerateCTR(String fromDate, String toDate) {
		log.logVerboseText("GenerateCTRFiles", "callSPForGenerateCTR", "Start");

		CallableStatement statement = null;
		Statement statement1 = null;
		ResultSet rs = null;
		boolean success = false;
		try {
			log.logNormalText("GenerateCTRFiles", "callSPForGenerateCTR",
					"Connection set auto commit false");

			con_aml.setAutoCommit(false);

			String query = "delete from AML_CTR_DETAILS";
			statement1 = con_aml.createStatement();
			int ret = statement1.executeUpdate(query);
			log.logNormalText("GenerateCTRFiles", "callSPForGenerateCTR",
					"Empty table aml_ctr_details flag:" + ret);

			String conQuery = "select CASE WHEN to_date('"
					+ fromDate.substring(0, 10) + "','yyyy-MM-dd') > to_date('"
					+ toDate.substring(0, 10) + "','yyyy-MM-dd') THEN 0 "
					+ " WHEN to_date('" + fromDate.substring(0, 10)
					+ "','yyyy-MM-dd') < to_date('" + toDate.substring(0, 10)
					+ "','yyyy-MM-dd') THEN 1 END as result " + "from dual";

			log.logNormalText("GenerateCTRFiles", "callSPForGenerateCTR",
					"Query is : " + conQuery);

			statement1 = con_aml.createStatement();
			rs = statement1.executeQuery(conQuery);
			int result = 0;
			while (rs.next()) {
				result = rs.getInt("result");
			}

			log.logNormalText("GenerateCTRFiles", "callSPForGenerateCTR",
					"Result is : " + result);

			if (result == 1) {
				statement = con_aml
						.prepareCall("{call GENERATE_CTR_FILES1(to_date('"
								+ fromDate.substring(0, 10)
								+ "','yyyy-MM-dd'),to_date('"
								+ toDate.substring(0, 10) + "','yyyy-MM-dd'))}");

				statement.execute();
				success = true;
			} else {
				success = false;
				log.logNormalText("GenerateCTRFiles", "callSPForGenerateCTR",
						"CTR Already Generated till date, result is : "
								+ result);

			}
			log.logNormalText("GenerateCTRFiles", "callSPForGenerateCTR",
					"success is : " + success);

		} catch (SQLException e) {
			e.printStackTrace();
			log.logExceptionText("GenerateCTRFiles", "callSPForGenerateCTR",
					"callSPForGenerateCTR" + e);

			// e.printStackTrace();
			success = false;

		} catch (Exception e) {
			e.printStackTrace();
			log.logExceptionText("GenerateCTRFiles", "callSPForGenerateCTR",
					"callSPForGenerateCTR" + e);

			// e.printStackTrace();
			success = false;
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (statement1 != null) {
					statement1.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				log.logExceptionText("GenerateCTRFiles",
						"callSPForGenerateCTR", "callSPForGenerateCTR" + ex);

				//
				success = false;
			}
		}
		log.logNormalText("GenerateCTRFiles", "callSPForGenerateCTR", "End");

		return success;
	}

	/**
	 * Getting Current Month in MM format
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getCurrentMonth() throws Exception {
		String sql = "select to_char(sysdate-30,'MM') as result from dual";// ,
																			// to_char(sysdate,'YYYY')as
																			// year
		ResultSet rs = null;
		PreparedStatement statement = null;
		String dat = null;
		try {
			statement = con_aml.prepareStatement(sql);
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

	/**
	 * For Getting Current year in yyyy format
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getCurrentYear() throws Exception {
		String sql = "select to_char(sysdate-30,'yyyy') as result from dual";// ,
																				// to_char(sysdate,'YYYY')as
																				// year
		ResultSet rs = null;
		PreparedStatement statement = null;
		String dat = null;
		try {
			statement = con_aml.prepareStatement(sql);
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

	/**
	 * 
	 * @return boolean flag that the CTR Files has Generated Successfully.
	 */
	public static boolean createFiles() {
		// General Variables for creating files and inserting data into tables
		// for CTR.
		log.logVerboseText("GenerateCTRFiles", "createFiles", "Start");
		log.logVerboseText("GenerateCTRFiles", "createFiles",
				"Creating Files for CTR");

		// String webInfFolder = "D://";
		Statement statement = null;
		ResultSet rs = null;
		boolean isFilesGenerated = false;
		// ResourceBundle rb = ResourceBundle.getBundle("AMLProp");
		String strDirectory = bundle_aml.getString("CTRDIR");
		File destinationDir;
		String seqNextVal = "";
		String sql = "";
		String branchCount = "";
		destinationDir = new File(strDirectory);

		try {
			con_aml.setAutoCommit(false);
			log.logNormalText("GenerateCTRFiles", "createFiles",
					"Connection set auto commit false");

			// CTR_FILE_SEQUENCE
			sql = "select CTR_FILE_SEQUENCE.NEXTVAL from DUAL";
			statement = con_aml.createStatement();
			rs = statement.executeQuery(sql);

			if (rs != null) {
				rs.next();
				seqNextVal = rs.getString(1);
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (statement != null) {
				statement.close();
				statement = null;
			}

			sql = "select value from p_aml_general where name='ACTIVE_SOL_COUNT'";
			statement = con_aml.createStatement();
			rs = statement.executeQuery(sql);

			if (rs != null) {
				rs.next();
				branchCount = rs.getString(1);
			}
			// TODO CHange this
			// branchCount = "991";

			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (statement != null) {
				statement.close();
				statement = null;
			}

			log.logNormalText("GenerateCTRFiles", "createFiles",
					"next sequence of ctr is :" + seqNextVal);

			ctrId = seqNextVal;
			// Creating Directory for CTR Files
			strDirectory = strDirectory + seqNextVal;// + "/";
			destinationDir = new File(strDirectory);

			log.logNormalText("GenerateCTRFiles", "createFiles",
					"Destination directory : " + strDirectory);

			boolean success = new File(strDirectory).mkdir();

			log.logNormalText("GenerateCTRFiles", "createFiles",
					"creating new directory for CTR:" + success);

			String month = getCurrentMonth();
			String year = getCurrentYear();

			if (success) {
				// Process to create control file.
				// ResourceBundle rb1 = ResourceBundle.getBundle("AMLProp");
				CTRControlDatabean ctrControlDatabean = new CTRControlDatabean();
				ctrControlDatabean.setReportName("CBA");
				ctrControlDatabean.setSrNoOfReport("00000079");
				ctrControlDatabean.setRecordType("CTL");
				ctrControlDatabean.setMonthOfReport(month);
				ctrControlDatabean.setYearOfReport(year);
				ctrControlDatabean
						.setCompleteNameOfBank(String
								.format("%-80s",
										bundle_aml.getString("NameOfBank"))
								.substring(0, 80).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean.setCategoryOfBank(String
						.format("%-1s", bundle_aml.getString("CategoryOfBank"))
						.substring(0, 1).replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setBsrCode(String
						.format("%-7s", bundle_aml.getString("BSRCode"))
						.substring(0, 7).replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setUniqueId(String.format("%-10s", "")
						.substring(0, 10).replace(' ', 'X').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean
						.setPoName(String
								.format("%-80s",
										bundle_aml
												.getString("NameOfPrincipalOfficer"))
								.substring(0, 80).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean
						.setPoDesignation(String
								.format("%-80s",
										bundle_aml.getString("Designation"))
								.substring(0, 80).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean
						.setPoAddress1(String
								.format("%-45s",
										bundle_aml.getString("Address"))
								.substring(0, 45).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean
						.setPoAddress2(String
								.format("%-45s", bundle_aml.getString("Street"))
								.substring(0, 45).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean
						.setPoAddress3(String
								.format("%-45s",
										bundle_aml.getString("Locality"))
								.substring(0, 45).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean
						.setPoAddress4(String
								.format("%-45s", bundle_aml.getString("City"))
								.substring(0, 45).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean
						.setPoAddress5(String
								.format("%-45s", bundle_aml.getString("State"))
								.substring(0, 45).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean.setPoPincode(String
						.format("%6s", bundle_aml.getString("Pin"))
						.substring(0, 6).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean
						.setPoTelephone(String
								.format("%-30s", bundle_aml.getString("Tel"))
								.substring(0, 30).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean
						.setPoFax(String
								.format("%-30s", bundle_aml.getString("Fax"))
								.substring(0, 30).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean
						.setPoEmail(String
								.format("%-50s", bundle_aml.getString("Email"))
								.substring(0, 50).replace("\'", " ")
								.replace("\"", " "));
				ctrControlDatabean.setReportType(String.format("%-1s", "N")
						.substring(0, 1).replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setReasonForReplacement(String
						.format("%-1s", "N").substring(0, 1).replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setSrNoOfOriginalReport(String
						.format("%8s", "").substring(0, 8).replace(' ', '0')
						.replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setOperationMode(String
						.format("%-1s", bundle_aml.getString("OperationMode"))
						.substring(0, 1).replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setDataStructurVersion(String
						.format("%-1s", "1").substring(0, 1).replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setNoOfTotalBranches(String
						.format("%8s", branchCount).replace(' ', '0')
						.substring(0, 8).replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setNoOfBranchesSentReports(String
						.format("%8s", "").replace(' ', '0').substring(0, 8)
						.replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setNoOfBranchesSubmittedCTRs(String
						.format("%8s", "").replace(' ', '0').substring(0, 8)
						.replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setNoOfCTRs(String
						.format("%8s", Integer.toString(0)).substring(0, 8)
						.replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setNoOfTransactions(String
						.format("%8s", Integer.toString(0)).substring(0, 8)
						.replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setNoOfIndividualPersons(String
						.format("%8s", Integer.toString(0)).substring(0, 8)
						.replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setNoOfLegalPersions(String
						.format("%8s", Integer.toString(0)).substring(0, 8)
						.replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setAcknowledgementNo(String
						.format("%10s", "").replace(' ', '0').substring(0, 10)
						.replace("\'", " ").replace("\"", " "));
				ctrControlDatabean.setDateOfAcknowledge(String
						.format("%8s", "").substring(0, 8).replace(' ', 'X')
						.replace("\'", " ").replace("\"", " "));

				log.logNormalText("GenerateCTRFiles", "createFiles",
						"control file started.");

				// Inserting Control data to tables.

				// if (ctrControlDatabean != null) {
				// log.logVerboseText("GenerateCTRFiles", "createFiles",
				// "inserting data into control file.");
				//
				// statement = con_aml.createStatement();
				// sql =
				// "INSERT INTO P_AML_CTR_CTRL_FILE (CTR_SEQ_NO,REPORT_NAME, SERIAL_NUMBER, RECORD_TYPE,MONTH_OF_RECORD, YEAR_OF_RECORD, COMP_NAME_OF_BANK, "
				// +
				// "CATEGORY_BANK, BSR_CODE, UID_FIU, PO_NAME, PO_DESIGNATION, PO_ADDRESS1,PO_ADDRESS2, PO_ADDRESS3, PO_ADDRESS4,"
				// +
				// " PO_ADDRESS5, PO_PINCODE, PO_TELEPHONE,PO_FAX, PO_EMAIL, REPORT_TYPE, REASON_FOR_REPLACEMENT, SRNO_ORIGINAL_REPORT,"
				// +
				// " OPERATION_MODE,DS_VERSION, TOTAL_BRANCH_NO, B_NO_INCLUDING_NIL, B_NO_EXCLUDING_NIL, NO_OF_CTRS, NO_OF_TRANSACTIONS,"
				// +
				// " NO_OF_INDIVIDUALS, NO_OF_LEGALS, ACK_NO,ACK_DATE,CREATED_BY,CREATED_DATE,STATUS)  VALUES "
				// + "('" + seqNextVal+ "','"
				// + ctrControlDatabean.getReportName()+ "','"+
				// ctrControlDatabean.getSrNoOfReport()+ "','"
				// + ctrControlDatabean.getRecordType()+ "', " + "'"+
				// ctrControlDatabean.getMonthOfReport()+ "','"
				// + ctrControlDatabean.getYearOfReport()+ "','"+
				// ctrControlDatabean.getCompleteNameOfBank()+ "',"+ "'"
				// + ctrControlDatabean.getCategoryOfBank()+ "','" +
				// ctrControlDatabean.getBsrCode()+ "','"
				// + ctrControlDatabean.getUniqueId()+ "','"+
				// ctrControlDatabean.getPoName()+ "'," + "'"
				// + ctrControlDatabean.getPoDesignation() + "','"+
				// ctrControlDatabean.getPoAddress1()+ "','"
				// + ctrControlDatabean.getPoAddress2()+ "'"+ ",'"+
				// ctrControlDatabean.getPoAddress3() + "','"
				// + ctrControlDatabean.getPoAddress4()+ "','" +
				// ctrControlDatabean.getPoAddress5()+ "'"+ ",'"
				// + ctrControlDatabean.getPoPincode() + "','" +
				// ctrControlDatabean.getPoTelephone()+ "','"
				// + ctrControlDatabean.getPoFax() + "'"+ ",'" +
				// ctrControlDatabean.getPoEmail()+ "','"
				// + ctrControlDatabean.getReportType()+ "','" +
				// ctrControlDatabean.getReasonForReplacement() + "'"+ ",'"
				// + ctrControlDatabean.getSrNoOfOriginalReport() + "','" +
				// ctrControlDatabean.getOperationMode() + "','"
				// + ctrControlDatabean.getDataStructurVersion()+ "'" +
				// ",'"+ctrControlDatabean.getNoOfTotalBranches()+"','"
				// + ctrControlDatabean.getNoOfTotalBranches()+ "','"+
				// ctrControlDatabean.getNoOfBranchesSubmittedCTRs() + "','"
				// + ctrControlDatabean.getNoOfCTRs()+ "','"+
				// ctrControlDatabean.getNoOfTransactions() + "','"
				// + ctrControlDatabean.getNoOfIndividualPersons() + "','"+
				// ctrControlDatabean.getNoOfLegalPersions()+ "','"
				// + ctrControlDatabean.getAcknowledgementNo() + "','" +
				// ctrControlDatabean.getDateOfAcknowledge() +
				// "','"+AMLConstants.SYSTEM+"',sysdate,'P')";
				//
				// int returnVal = statement.executeUpdate(sql);
				// log.logVerboseText("GenerateCTRFiles", "createFiles",
				// "Control file return value."+returnVal);
				//
				// }
				//
				// if(statement!=null)
				// {
				// statement.close();
				// statement=null;
				// }
				//
				//
				// Query for transaction file.
				sql = " SELECT A.TRAN_ID, A.PART_TRAN_SRL_NUM, to_char(A.TRAN_DATE,'ddMMyyyy') as TRAN_DATE, A.REMARK, A.BRANCH_ID, A.CUST_ID,"
						+ "  A.TRAN_AC_NO, A.TRAN_TYPE, A.CRE_DEB_FLG, A.TRAN_AMT, A.TRAN_CURRENCY FROM AML_CTR_DETAILS A WHERE A.TRAN_AMT > 50000 ";

				log.logVerboseText("GenerateCTRFiles", "createFiles",
						"Query of transaction file:" + sql);

				statement = con_aml.createStatement();
				rs = statement.executeQuery(sql);
				// ArrayList<CTRTransactionDatabean> cTRTransactionBeans = new
				// ArrayList<CTRTransactionDatabean>();
				Integer lineNumber = 1;
				// Integer transactionCount = 0;
				sql = "INSERT INTO P_AML_CTR_TRAN_FILE (CTR_SEQ_NO,RECORD_TYPE, LINE_NO, BRANCH_REF_NO,AC_NO, TRAN_ID, DATE_OF_TRAN,"
						+ " MODE_OF_TRAN, DEB_CRE_FLG, AMOUNT,CURR_OF_TRAN, DISPOSITION_OF_FUNDS, REMARKS,SOL_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				PreparedStatement tempStatement = con_aml.prepareStatement(sql);
				String bRefNo = "";

				// Fetching Data for Transaction File
				CTRTransactionDatabean ctrTransactionDatabean = null;

				log.logNormalText("GenerateCTRFiles", "createFiles",
						"Transaction data fatched.");

				// Transaction File.
				File file = new File(destinationDir, "CBATRN.txt");
				file.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				String fileData = "";
				String sql1_bsrCode = "";
				PreparedStatement statement_bsrCode = null;
				ResultSet rsbsrCode = null;
				sql1_bsrCode = "select case when length(uniform_br_code)>7 then substr(UNIFORM_BR_CODE,0,instr(UNIFORM_BR_CODE,'-')-1) else uniform_br_code end from p_aml_sol where sol_id=? ";
				statement_bsrCode = con_aml.prepareStatement(sql1_bsrCode);
				while (rs.next()) {

					try {
						statement_bsrCode.setString(1,
								rs.getString("BRANCH_ID"));
						rsbsrCode = statement_bsrCode.executeQuery();
						if (rsbsrCode != null) {
							rsbsrCode.next();
							bRefNo = rsbsrCode.getString(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					ctrTransactionDatabean = new CTRTransactionDatabean();
					ctrTransactionDatabean.setRecordType("TRN");

					ctrTransactionDatabean.setLineNumber(String.format("%6s",
							lineNumber.toString()).replace(' ', '0'));
					ctrTransactionDatabean.setBranchRefNo(String
							.format("%-7s", bRefNo).replace(' ', '0')
							.substring(0, 7).replace("\'", " ")
							.replace("\"", " "));
					ctrTransactionDatabean.setTranAcNo(String
							.format("%-20s",
									rs.getString("TRAN_AC_NO") != null ? rs
											.getString("TRAN_AC_NO") : "")
							.substring(0, 20).replace("\'", " ")
							.replace("\"", " "));
					ctrTransactionDatabean.setTranId(String
							.format("%-20s",
									rs.getString("TRAN_ID") != null ? rs
											.getString("TRAN_ID") : "")
							.substring(0, 20).replace("\'", " ")
							.replace("\"", " "));
					ctrTransactionDatabean.setTranDate(String
							.format("%-8s",
									rs.getString("TRAN_DATE") != null ? rs
											.getString("TRAN_DATE") : "")
							.substring(0, 8).replace("\'", " ")
							.replace("\"", " "));
					ctrTransactionDatabean.setTranType(rs
							.getString("TRAN_TYPE") != null ? rs
							.getString("TRAN_TYPE") : "");
					ctrTransactionDatabean.setCreDebFlg(String
							.format("%-1s",
									rs.getString("CRE_DEB_FLG") != null ? rs
											.getString("CRE_DEB_FLG") : "")
							.substring(0, 1).replace("\'", " ")
							.replace("\"", " "));
					ctrTransactionDatabean.setTranAmt(String
							.format("%20s",
									rs.getString("TRAN_AMT") != null ? rs
											.getString("TRAN_AMT") : "")
							.replace(" ", "0").substring(0, 20)
							.replace("\'", " ").replace("\"", " "));
					ctrTransactionDatabean.setTranCurrency(String
							.format("%-3s",
									rs.getString("TRAN_CURRENCY") != null ? rs
											.getString("TRAN_CURRENCY") : "")
							.substring(0, 3).replace("\'", " ")
							.replace("\"", " "));
					ctrTransactionDatabean.setDisPositionOfFunds("X");
					ctrTransactionDatabean.setRemark(String
							.format("%-10s",
									rs.getString("REMARK") != null ? rs
											.getString("REMARK") : "")
							.substring(0, 10).replace("\'", " ")
							.replace("\"", " "));
					ctrTransactionDatabean.setBranchId(rs
							.getString("BRANCH_ID") != null ? rs
							.getString("BRANCH_ID") : "");
					// ctrTransactionDatabean.setCustId(rs.getString("CUST_ID")!=null?rs.getString("CUST_ID"):"");
					// ctrTransactionDatabean.setPartTranSrlNo(rs.getString("PART_TRAN_SRL_NUM")!=null?rs.getString("PART_TRAN_SRL_NUM"):"");

					// fileData = "";
					// fileData = ctrTransactionDatabean.getRecordType() +
					// ctrTransactionDatabean.getLineNumber()
					// + ctrTransactionDatabean.getBranchRefNo() +
					// ctrTransactionDatabean.getTranAcNo()
					// + ctrTransactionDatabean.getTranId() +
					// ctrTransactionDatabean.getTranDate() +
					// ctrTransactionDatabean.getTranType()
					// + ctrTransactionDatabean.getCreDebFlg() +
					// ctrTransactionDatabean.getTranAmt()
					// + ctrTransactionDatabean.getTranCurrency() +
					// ctrTransactionDatabean.getDisPositionOfFunds()
					// + ctrTransactionDatabean.getRemark();
					// out.write(fileData);
					// out.write("\n");
					// out.flush();

					// Inserting into P_AML_CTR_TRAN_FILE table...
					tempStatement.setString(1, seqNextVal);
					tempStatement.setString(2,
							ctrTransactionDatabean.getRecordType());
					tempStatement.setString(3,
							ctrTransactionDatabean.getLineNumber());
					tempStatement.setString(4,
							ctrTransactionDatabean.getBranchRefNo());
					tempStatement.setString(5,
							ctrTransactionDatabean.getTranAcNo());
					tempStatement.setString(6,
							ctrTransactionDatabean.getTranId());
					tempStatement.setString(7,
							ctrTransactionDatabean.getTranDate());
					tempStatement.setString(8,
							ctrTransactionDatabean.getTranType());
					tempStatement.setString(9,
							ctrTransactionDatabean.getCreDebFlg());
					tempStatement.setString(10,
							ctrTransactionDatabean.getTranAmt());
					tempStatement.setString(11,
							ctrTransactionDatabean.getTranCurrency());
					tempStatement.setString(12,
							ctrTransactionDatabean.getDisPositionOfFunds());
					tempStatement.setString(13,
							ctrTransactionDatabean.getRemark());
					tempStatement.setString(14,
							ctrTransactionDatabean.getBranchId());
					tempStatement.executeUpdate();

					// cTRTransactionBeans.add(ctrTransactionDatabean);
					ctrTransactionDatabean = null;
					if (lineNumber % 1000 == 0) {
						log.logVerboseText("GenerateCTRFiles", "createFiles",
								"Transaction data inserted is : " + lineNumber);
					}
					lineNumber++;

				}
				lineNumber--;
				log.logNormalText("GenerateCTRFiles", "createFiles",
						"Transaction data inserted is : " + lineNumber);

				if (statement != null) {
					statement.close();
					statement = null;
				}
				// Updating Number Of Transactions into P_AML_CTR_CTRL_FILE
				// table.
				sql = "UPDATE P_AML_CTR_CTRL_FILE SET NO_OF_TRANSACTIONS='"
						+ lineNumber + "' where CTR_SEQ_NO='" + seqNextVal
						+ "'";
				statement = con_aml.createStatement();
				statement.executeUpdate(sql);
				ctrControlDatabean.setNoOfTransactions(String
						.format("%8s", Integer.toString(lineNumber))
						.substring(0, 8).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));

				log.logNormalText("GenerateCTRFiles", "createFiles",
						"Transaction Completed.");

				out.close();
				if (rsbsrCode != null) {
					rsbsrCode.close();
					rsbsrCode = null;
				}
				if (statement_bsrCode != null) {
					statement_bsrCode.close();
					statement_bsrCode = null;
				}

				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}

				// Branch File Started.
				file = new File(destinationDir, "CBABRC.txt");
				file.createNewFile();
				out = new BufferedWriter(new FileWriter(file));

				// Fetching data for branch file.
				sql = "select distinct(B.SOL_ID),B.SOL_DESC,B.ADDR_1,B.ADDR_2,"
						+ "(select REF_DESC from AML_RCT where ref_rec_type = '01' and ref_code=b.CITY_CODE) as CITY_CODE,"
						+ "(select REF_DESC from AML_RCT where ref_rec_type = '02' and ref_code=b.STATE_CODE) as STATE_CODE,b.PIN_CODE,b.UNIFORM_BR_CODE,phone_num,fax_num,'bm'||b.sol_id||'@obc.co.in' br_email "
						+ " from AML_SOL b,AML_CTR_DETAILS A,aml_bct c where a.BRANCH_ID=b.SOL_ID"
						+ " and b.bank_code=c.bank_code"
						+ " and b.br_code=c.br_code";

				// Branch Reference number
				/*
				 * select substr(UNIFORM_BR_CODE,0,instr(UNIFORM_BR_CODE,'-')-1)
				 * from P_AML_SOL where sol_id =
				 */

				log.logNormalText("GenerateCTRFiles", "createFiles",
						"Branch filr query:" + sql);

				statement = con_aml.createStatement();
				rs = statement.executeQuery(sql);

				// ArrayList<CTRBranchDatabean> ctrBranchDatabeans = new
				// ArrayList<CTRBranchDatabean>();
				lineNumber = 1;
				sql = "INSERT INTO P_AML_CTR_BRANCH_FILE (CTR_SEQ_NO,RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD,LINE_NO, NAME_OF_BRANCH, BRANCH_REF_NO, "
						+ "UID_FIU, BRANCH_ADDRESS1, BRANCH_ADDRESS2,BRANCH_ADDRESS3, BRANCH_ADDRESS4, BRANCH_ADDRESS5,"
						+ " BRANCH_PINCODE, BRANCH_TELEPHONE, BRANCH_FAX, BRANCH_EMAIL) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement branchstat = con_aml.prepareStatement(sql);

				CTRBranchDatabean ctrBranchDatabean = null;
				while (rs.next()) {
					bRefNo = String
							.format("%-7s",
									rs.getString("UNIFORM_BR_CODE") != null
											&& rs.getString("UNIFORM_BR_CODE")
													.length() > 6 ? rs
											.getString("UNIFORM_BR_CODE") : "")
							.substring(0, 7).replace("\'", " ")
							.replace("\"", " ");

					ctrBranchDatabean = new CTRBranchDatabean();
					ctrBranchDatabean
							.setBranchAdd1((rs.getString("ADDR_1") != null ? rs
									.getString("ADDR_1") : "-NA-").replace(
									"\'", "").replace("\"", ""));
					// ctrBranchDatabean.setBranchAdd2(String.format("%-45s",
					// rs.getString("ADDR_2") != null ? rs.getString("ADDR_2") :
					// "").substring(0, 44).replace("\'", "").replace("\" ",
					// ""));
					ctrBranchDatabean
							.setBranchAdd2(rs.getString("ADDR_2") != null ? rs
									.getString("ADDR_2").replace("\'", "")
									.replace("\" ", "") : "-NA-");
					ctrBranchDatabean.setBranchAdd3(String.format("%-45s", ""));
					ctrBranchDatabean
							.setBranchAdd4((rs.getString("CITY_CODE") != null ? rs
									.getString("CITY_CODE") : "").replace("\'",
									" ").replace("\" ", " "));
					ctrBranchDatabean
							.setBranchAdd5((rs.getString("STATE_CODE") != null ? rs
									.getString("STATE_CODE") : "").replace(
									"\'", " ").replace("\" ", " "));
					ctrBranchDatabean.setBranchRefNo(bRefNo.replace("\'", "")
							.replace("\" ", " "));
					ctrBranchDatabean.setEmailAdd(rs.getString("br_email"));
					ctrBranchDatabean.setFaxNo(rs.getString("fax_num"));
					ctrBranchDatabean.setLineNumber(String.format("%6s",
							lineNumber.toString()).replace(' ', '0'));
					ctrBranchDatabean.setMonthOfReport(String
							.format("%-2s", month.toString()).substring(0, 2)
							.replace("\'", " ").replace("\" ", " "));
					ctrBranchDatabean
							.setNameOfBranch((rs.getString("SOL_DESC") != null ? rs
									.getString("SOL_DESC") : "").replace("\'",
									" ").replace("\"", " "));
					ctrBranchDatabean
							.setPincode((rs.getString("PIN_CODE") != null ? isIntNumber(rs
									.getString("PIN_CODE")) ? rs
									.getString("PIN_CODE") : "000000"
									: "000000").replace("\'", " ").replace(
									"\"", " "));
					ctrBranchDatabean.setReportType("BRC");
					ctrBranchDatabean.setTelephone(rs.getString("phone_num"));
					ctrBranchDatabean.setUID("XXXXXXXXXX");
					ctrBranchDatabean.setYearOfReport(String
							.format("%-4s", year.toString()).substring(0, 4)
							.replace("\'", " ").replace("\"", " "));

					// Writing into file
					// fileData = "";
					// fileData = ctrBranchDatabean.getReportType() +
					// ctrBranchDatabean.getMonthOfReport() +
					// ctrBranchDatabean.getYearOfReport()
					// + String.format("%6s", ctrBranchDatabean.getLineNumber())
					// + String.format("%-80s",
					// ctrBranchDatabean.getNameOfBranch())
					// + String.format("%-7s",
					// ctrBranchDatabean.getBranchRefNo()) +
					// ctrBranchDatabean.getUID()
					// + String.format("%-45s",
					// ctrBranchDatabean.getBranchAdd1())
					// + String.format("%-45s",
					// ctrBranchDatabean.getBranchAdd2()) +
					// ctrBranchDatabean.getBranchAdd3()
					// + String.format("%-45s",
					// ctrBranchDatabean.getBranchAdd4())
					// + String.format("%-45s",
					// ctrBranchDatabean.getBranchAdd5()) +
					// String.format("%-6s", ctrBranchDatabean.getPincode())
					// + ctrBranchDatabean.getTelephone() +
					// ctrBranchDatabean.getFaxNo() +
					// ctrBranchDatabean.getEmailAdd();
					// out.write(fileData);
					// out.write("\n");
					// out.flush();
					// BgProcessLogger.writeToFile(logFile, "GenerateCTRFiles",
					// "createFiles",
					// "Branch address2 is :"+ctrBranchDatabean.getBranchAdd2()+":");
					// BgProcessLogger.writeToFile(logFile, "GenerateCTRFiles",
					// "createFiles",
					// "Branch address2 length is :"+ctrBranchDatabean.getBranchAdd2().length()+":");

					// Table entry....
					branchstat.setString(1, seqNextVal);
					branchstat.setString(2, ctrBranchDatabean.getReportType());
					branchstat.setString(3,
							ctrBranchDatabean.getMonthOfReport());
					branchstat
							.setString(4, ctrBranchDatabean.getYearOfReport());
					branchstat.setString(5, ctrBranchDatabean.getLineNumber());
					branchstat
							.setString(6, ctrBranchDatabean.getNameOfBranch());
					branchstat.setString(7, ctrBranchDatabean.getBranchRefNo());
					branchstat.setString(8, ctrBranchDatabean.getUID());
					branchstat.setString(9, ctrBranchDatabean.getBranchAdd1());
					branchstat.setString(10, ctrBranchDatabean.getBranchAdd2());
					branchstat.setString(11, ctrBranchDatabean.getBranchAdd3());
					branchstat.setString(12, ctrBranchDatabean.getBranchAdd4());
					branchstat.setString(13, ctrBranchDatabean.getBranchAdd5());
					branchstat.setString(14, ctrBranchDatabean.getPincode());
					branchstat.setString(15, ctrBranchDatabean.getTelephone());
					branchstat.setString(16, ctrBranchDatabean.getFaxNo());
					branchstat.setString(17, ctrBranchDatabean.getEmailAdd());
					branchstat.executeUpdate();

					// ctrBranchDatabeans.add(ctrBranchDatabean);
					ctrBranchDatabean = null;
					if (lineNumber % 500 == 0) {
						log.logVerboseText("GenerateCTRFiles", "createFiles",
								"Branch data inserted is : " + lineNumber);
					}
					lineNumber++;
				}

				log.logNormalText("GenerateCTRFiles", "createFiles",
						"Branch file Completed.");

				if (branchstat != null) {
					branchstat.close();
					branchstat = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				// Updating Number Of Transactions into P_AML_CTR_CTRL_FILE
				// table.
				sql = "UPDATE P_AML_CTR_CTRL_FILE SET TOTAL_BRANCH_NO='"
						+ --lineNumber + "' where CTR_SEQ_NO='" + seqNextVal
						+ "'";
				statement = con_aml.createStatement();
				statement.executeUpdate(sql);

				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				ctrControlDatabean.setNoOfBranchesSubmittedCTRs(String
						.format("%8s", Integer.toString(lineNumber))
						.substring(0, 8).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));

				log.logNormalText("GenerateCTRFiles", "createFiles",
						"Branch file Created");

				// Fetching data for account file.Individual file and legal
				// persons file

				// Account File Parameter
				File fileAcc = new File(destinationDir, "CBAACC.txt");
				fileAcc.createNewFile();
				BufferedWriter outAcc = new BufferedWriter(new FileWriter(
						fileAcc));

				// Individual File Parameter
				File fileInd = new File(destinationDir, "CBAINP.txt");
				fileInd.createNewFile();
				BufferedWriter outInd = new BufferedWriter(new FileWriter(
						fileInd));

				// LegalPersonParameter
				File fileLgl = new File(destinationDir, "CBALPE.txt");
				fileLgl.createNewFile();
				BufferedWriter outLgl = new BufferedWriter(new FileWriter(
						fileLgl));

				// Account File Query is...
				sql = "SELECT DISTINCT (a.tran_ac_no), ac.acct_name,b.cust_name, ac.CUST_AC_TYPE, b.cust_type_code,b.CUST_COMMU_CNTRY_CODE, "
						+ " to_char(ac.ACCT_OPN_DATE,'ddMMyyyy') as cust_ac_open_date, e.risk CUST_RISK_RATING,b.CUST_ID,b.CUST_OCCP_CODE, "
						+ " to_char(b.CUST_DOB,'ddMMyyyy') as CUST_DOB,to_char(b.CUST_OPN_DATE,'ddMMyyyy') as CUST_OPN_DATE, b.CUST_SEX,b.CUST_COMMU_CNTRY_CODE as CUST_RESIDENT_COUNTRY, "
						+ " b.CUST_PAN_NO,b.CUST_COMMU_ADDR1,b.CUST_COMMU_ADDR2,(select REF_DESC from AML_RCT where ref_rec_type = '01' and ref_code =b.cust_commu_city_code) as cust_commu_city_code,(select REF_DESC from AML_RCT where ref_rec_type = '02' and ref_code =b.cust_commu_state_code) as cust_commu_state_code,(select REF_DESC from AML_RCT where ref_rec_type = '03' and ref_code =b.CUST_COMMU_CNTRY_CODE) as CUST_COMMU_CNTRY_CODE, b.CUST_COMMU_PIN_CODE, "
						+ " nvl(b.CUST_COMMU_PHONE_NUM_1,b.CUST_COMMU_PHONE_NUM_2) as CUST_PHN_NO,b.EMAIL_ID as CUST_EMAIL_ID,ac.CUST_AC_NO,b.CUST_CONST AS CUST_CONST,ac.CUST_ACID as CUST_ACID,"
						+ " (select case when length(uniform_br_code)>7 then substr(UNIFORM_BR_CODE,0,instr(UNIFORM_BR_CODE,'-')-1) else uniform_br_code end from p_aml_sol where sol_id=ac.branch_id) branch_id"
						+ " from aml_ctr_details a,aml_cust_master b,aml_ac_master ac,aml_kyc e "
						+ " WHERE a.TRAN_AC_NO=ac.CUST_AC_NO and ac.CUST_ID=b.CUST_ID and b.CUST_ID=e.CUST_ID";

				log.logNormalText("GenerateCTRFiles", "createFiles",
						"account file query is : " + sql);

				statement = con_aml.createStatement();
				rs = statement.executeQuery(sql);

				lineNumber = 1;
				Integer linInd = 1;
				Integer linleg = 1;
				CTRAccountDatabean ctrAccountDatabean = null;
				CTRIndividualDatabean ctrIndividualDatabean = null;
				CTRLegalPersionDatabean ctrLegalPersionDatabean = null;
				String toDateWithoutTime[] = toDate.split(" ");
				String[] toDateQuery = toDateWithoutTime[0].split("-");
				String useToDateforQuery = toDateQuery[2] + "-"
						+ toDateQuery[1] + "-" + toDateQuery[0];
				log.logNormalText("GenerateCTRFiles", "createFiles",
						"useToDateforQuery " + useToDateforQuery);
				// Getting cumulativeCashDeposit
				// String ccdQuery =
				// "select ROUND(sum(t.TRAN_AMT)) from  t where  t.ACID=?"
				// +
				// " and t.TRAN_DATE between (select to_date(TDS_START_DATE, 'dd-MM-yy') from GCT@live_fin)  and (select to_date('"
				// + useToDateforQuery +
				// "','dd-MM-yy') from dual) and t.PART_TRAN_TYPE='C' and t.TRAN_TYPE='C'";
				String ccdQuery = "SELECT round(SUM (tran_amt)) FROM ((SELECT tran_amt FROM aml_htd "
						+ " WHERE tran_date between (select to_date(tds_start_date,'dd-mm-yy') from p_aml_gct) and '30-apr-14' "
						+ "  AND tran_acid = ?"
						+ " AND tran_type ='C' AND cre_deb_flg = 'C' AND del_flg='N')"
						+ " UNION all"
						+ " (SELECT tran_amt FROM aml_transaction_master_h "
						+ " WHERE tran_acid = ?"
						+ "  AND tran_date <= (select to_date('"
						+ useToDateforQuery
						+ "','dd-MM-yy') from dual)"
						+ " AND tran_type ='C'  AND cre_deb_flg = 'C' AND del_flg='N'))";
				PreparedStatement ccdStmt = con_aml.prepareStatement(ccdQuery);
				// log.logNormalText("GenerateCTRFiles", "createFiles",
				// "ccdQuery "+ccdQuery);
				// Getting cumulativeCashWithdrawl
				// String ccwQuery =
				// "select ROUND(sum(t.TRAN_AMT)) from HTD@LIVE_FIN t where  t.ACID=?"
				// +
				// " and t.TRAN_DATE between (select to_date(TDS_START_DATE, 'dd-MM-yy') from GCT@LIVE_FIN)  and (select to_date('"
				// + useToDateforQuery +
				// "','dd-MM-yy') from dual) and t.PART_TRAN_TYPE='D' and t.TRAN_TYPE='C'";
				String ccwQuery = "SELECT round(SUM (tran_amt)) FROM ((SELECT tran_amt FROM aml_htd "
						+ " WHERE tran_date between (select to_date(tds_start_date,'dd-mm-yy') from p_aml_gct) and '30-apr-14' "
						+ "  AND tran_acid = ?"
						+ " AND tran_type ='C' AND cre_deb_flg = 'D' AND del_flg='N')"
						+ " UNION all"
						+ " (SELECT tran_amt FROM aml_transaction_master_h "
						+ " WHERE tran_acid = ?"
						+ "  AND tran_date <= (select to_date('"
						+ useToDateforQuery
						+ "','dd-MM-yy') from dual)"
						+ " AND tran_type ='C'  AND cre_deb_flg = 'D' AND del_flg='N'))";
				PreparedStatement ccwStmt = con_aml.prepareStatement(ccwQuery);
				// log.logNormalText("GenerateCTRFiles", "createFiles",
				// "ccdQuery "+ccwQuery);
				// Getting cumulativeCredit
				// String ccQuery =
				// "select ROUND(sum(t.TRAN_AMT)) from HTD@LIVE_FIN t where  t.ACID=?"
				// +
				// " and t.TRAN_DATE between (select to_date(TDS_START_DATE, 'dd-MM-yy') from GCT@LIVE_FIN)  and (select to_date('"
				// + useToDateforQuery +
				// "','dd-MM-yy') from dual) and t.PART_TRAN_TYPE='C'";
				String ccQuery = "SELECT round(SUM (tran_amt)) FROM ((SELECT tran_amt FROM aml_htd "
						+ " WHERE tran_date between (select to_date(tds_start_date,'dd-mm-yy') from p_aml_gct) and '30-apr-14' "
						+ "  AND tran_acid = ?"
						+ " AND cre_deb_flg = 'C' AND del_flg='N')"
						+ " UNION all"
						+ " (SELECT tran_amt FROM aml_transaction_master_h "
						+ " WHERE tran_acid = ?"
						+ "  AND tran_date <= (select to_date('"
						+ useToDateforQuery
						+ "','dd-MM-yy') from dual)"
						+ " AND cre_deb_flg = 'C' AND del_flg='N'))";
				PreparedStatement ccStmt = con_aml.prepareStatement(ccQuery);
				// log.logNormalText("GenerateCTRFiles", "createFiles",
				// "ccdQuery "+ccQuery);
				// Getting cumulativeDebit
				// String cdQuery =
				// "select ROUND(sum(t.TRAN_AMT)) from HTD@LIVE_FIN t where  t.ACID=?"
				// +
				// " and t.TRAN_DATE between (select to_date(TDS_START_DATE, 'dd-MM-yy') from GCT@LIVE_FIN)  and (select to_date('"
				// + useToDateforQuery +
				// "','dd-MM-yy') from dual) and t.PART_TRAN_TYPE='D'";
				String cdQuery = "SELECT round(SUM (tran_amt)) FROM ((SELECT tran_amt FROM aml_htd "
						+ " WHERE tran_date between (select to_date(tds_start_date,'dd-mm-yy') from p_aml_gct) and '30-apr-14' "
						+ "  AND tran_acid = ?"
						+ " AND cre_deb_flg = 'D' AND del_flg='N')"
						+ " UNION all"
						+ " (SELECT tran_amt FROM aml_transaction_master_h "
						+ " WHERE tran_acid = ?"
						+ "  AND tran_date <= (select to_date('"
						+ useToDateforQuery
						+ "','dd-MM-yy') from dual)"
						+ " AND cre_deb_flg = 'D' AND del_flg='N'))";
				// log.logNormalText("GenerateCTRFiles", "createFiles",
				// "ccdQuery "+cdQuery);
				PreparedStatement cdStmt = con_aml.prepareStatement(cdQuery);

				// Account insert query.
				String acctQuery = "INSERT INTO P_AML_CTR_ACCOUNT_FILE (CTR_SEQ_NO,RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD, LINE_NO, BRANCH_REF_NO, ACCOUNT_NO,"
						+ "FIRST_SOL_AC_HOLDER, TYPE_OF_AC, TYPE_OF_AC_HOLDER, DATE_OF_AC_OPENING, RISK_CATEGORY, CUMULATIVE_CR_TO, CUMULATIVE_DB_TO, "
						+ "CUMULATIVE_CASH_DEP_TO, CUMULATIVE_CASH_WD_TO) VALUES ("
						+ "?,?,?,?,?," + "?,?,?,?,?," + "?,?,?,?,?,?)";
				// Individual insert query.
				String indQuery = "INSERT INTO P_AML_CTR_INDIVIDUAL_FILE (CTR_SEQ_NO,RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD, "
						+ "LINE_NO, BRANCH_REF_NO, AC_NO,RELATION_FLG, FULLNAME_INDIVIDUAL, CUST_ID_NO, NAME_OF_FATHER_OR_SPOUSE, OCCUPATION, DOB,"
						+ " SEX, NATIONALITY, TYPE_OF_ID, ID_NO, ISSUING_AUTHORITY, PLACE_OF_ISSUE, PAN, COMMUNICATION_ADD1, COMMUNICATION_ADD2, "
						+ "COMMUNICATION_ADD3, COMMUNICATION_ADD4, COMMUNICATION_ADD5, COMMUNICATION_PIN, CONT_TEL, CONT_MB_NO, "
						+ "CONT_EMAIL, PLACE_OF_WORK, SEC_ADD1,SEC_ADD2, SEC_ADD3, SEC_ADD4, SEC_ADD5, SEC_PIN, SEC_TEL)  VALUES("
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,?,?)";
				// Legal person insert query is there.
				String lglQuery = "INSERT INTO P_AML_CTR_LGLPERSIONL_FILE (CTR_SEQ_NO,RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD, "
						+ " LINE_NO, BRANCH_REF_NO, AC_NO, RELATION_FLG, LGLPER_NAME, CUST_ID_NO, NATURE_OF_BUSINESS,"
						+ " DATE_OF_INCORPORATION, TYPE_OF_CONSTI, REG_NO, REG_AUTHORITY, REG_PLACE, PAN, COMMUNICATION_ADD1,"
						+ " COMMUNICATION_ADD2, COMMUNICATION_ADD3, COMMUNICATION_ADD4, COMMUNICATION_ADD5, COMMUNICATION_PIN,"
						+ " CONT_TELEPHONE, CONT_FAX, CONT_EMAIL, REG_ADD1, REG_ADD2, REG_ADD3, REG_ADD4, REG_ADD5,"
						+ " REG_PIN, REG_OFF_TELEPHONE, REG_OFF_FAX)  VALUES ("
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?,?,"
						+ "?,?,?,?)";

				String authoSignatoryQuery = "select trim(nvl(decode(acct_poa_as_name,'''','NA',acct_poa_as_name),nvl(acct_name,'NA'))) as  text_line_1,a.cust_id,acct_poa_as_rec_type "
					+ "from aml_aas a,aml_ac_master b where a.acid = b.cust_acid and a.acid=(select cust_acid from aml_ac_master where cust_ac_no=?) and acct_poa_as_rec_type <>'M'";
				
				PreparedStatement acctStmt = con_aml
						.prepareStatement(acctQuery);
				PreparedStatement indStmt = con_aml.prepareStatement(indQuery);
				PreparedStatement lglStmt = con_aml.prepareStatement(lglQuery);

				PreparedStatement authoSignatoryStmt = con_aml
						.prepareStatement(authoSignatoryQuery);

				while (rs.next()) {
					ctrAccountDatabean = new CTRAccountDatabean();
					String typeOfAccount = "";
					String typeOfConstitution = "";
					String typeOfCustomer = "";
					String riskCategory = "";

					typeOfAccount = rs.getString("CUST_AC_TYPE") != null ? rs
							.getString("CUST_AC_TYPE") : "";
					typeOfConstitution = rs.getString("CUST_CONST") != null ? rs
							.getString("CUST_CONST") : "";

					if (typeOfAccount.equalsIgnoreCase("SBA")) {
						typeOfAccount = "AB";
					} else if (typeOfAccount.equalsIgnoreCase("CCA")) {
						typeOfAccount = "CB";
					} else if (typeOfAccount.equalsIgnoreCase("CAA")) {
						typeOfAccount = "BB";
					} else if (typeOfAccount.equalsIgnoreCase("LAA")) {
						typeOfAccount = "LB";
					} else if (typeOfAccount.equalsIgnoreCase("TDA")) {
						typeOfAccount = "TB";
					} else if (typeOfAccount.equalsIgnoreCase("ODA")) {
						typeOfAccount = "RB";
					} else {
						typeOfAccount = "ZZ";
					}
					
					if (typeOfConstitution.equalsIgnoreCase("01")
							|| typeOfConstitution.equalsIgnoreCase("02")) {
						typeOfConstitution = "A";
					} else if (typeOfConstitution.equalsIgnoreCase("03")) {
						typeOfConstitution = "B";
					} else if (typeOfConstitution.equalsIgnoreCase("04")) {
						typeOfConstitution = "C";
					} else if (typeOfConstitution.equalsIgnoreCase("05")) {
						typeOfConstitution = "D";
					} else if (typeOfConstitution.equalsIgnoreCase("06")
							|| typeOfConstitution.equalsIgnoreCase("25")
							|| typeOfConstitution.equalsIgnoreCase("13")
							|| typeOfConstitution.equalsIgnoreCase("14")) {
						typeOfConstitution = "E";
					} else if (typeOfConstitution.equalsIgnoreCase("07")
							|| typeOfConstitution.equalsIgnoreCase("08")) {
						typeOfConstitution = "F";
					} else if (typeOfConstitution.equalsIgnoreCase("09")) {
						typeOfConstitution = "H";
					} else if (typeOfConstitution.equalsIgnoreCase("11")
							|| typeOfConstitution.equalsIgnoreCase("12")) {
						typeOfConstitution = "G";
					} else if (typeOfConstitution.equalsIgnoreCase("26")) {
						typeOfConstitution = "J";
					} else {
						typeOfConstitution = "Z";
					}

					typeOfCustomer = rs.getString("cust_type_code") != null ? rs
							.getString("cust_type_code") : "";
					String custTypeToDistringuishFiles = rs
							.getString("cust_type_code") != null ? rs
							.getString("cust_type_code") : "";
					typeOfCustomer = typeOfCustomer.equals("INDIV") ? "A"
							: typeOfCustomer.equals("GOVTC") ? "C" : "B";

					riskCategory = rs.getString("CUST_RISK_RATING") != null ? rs
							.getString("CUST_RISK_RATING") : "";
					if (riskCategory.trim().equalsIgnoreCase("C3".trim())
							|| riskCategory.trim()
									.equalsIgnoreCase("C4".trim())) {
						riskCategory = "C";
					} else if (riskCategory.trim()
							.equalsIgnoreCase("C2".trim())) {
						riskCategory = "B";
					} else if (riskCategory.trim()
							.equalsIgnoreCase("C1".trim())) {
						riskCategory = "A";
					}
					String cumulativeCredit = "";
					String cumulativeDebit = "";
					String cumulativeCashDeposit = "";
					String cumulativeCashWithdrawl = "";

					// Getting cumulativeCashDeposit
					ccdStmt.setString(
							1,
							rs.getString("CUST_ACID") != null ? rs
									.getString("CUST_ACID") : "");
					ccdStmt.setString(
							2,
							rs.getString("CUST_ACID") != null ? rs
									.getString("CUST_ACID") : "");
					ResultSet tors = null;
					tors = ccdStmt.executeQuery();

					if (tors.next()) {
						cumulativeCashDeposit = tors.getString(1) != null ? tors
								.getString(1) : "";
					}

					tors.close();

					// Getting cumulativeCashWithdrawl
					tors = null;
					ccwStmt.setString(
							1,
							rs.getString("CUST_ACID") != null ? rs
									.getString("CUST_ACID") : "");
					ccwStmt.setString(
							2,
							rs.getString("CUST_ACID") != null ? rs
									.getString("CUST_ACID") : "");
					tors = ccwStmt.executeQuery();

					if (tors.next()) {
						cumulativeCashWithdrawl = tors.getString(1) != null ? tors
								.getString(1) : "";
					}

					tors.close();

					// Getting cumulativeCredit
					tors = null;
					ccStmt.setString(
							1,
							rs.getString("CUST_ACID") != null ? rs
									.getString("CUST_ACID") : "");
					ccStmt.setString(
							2,
							rs.getString("CUST_ACID") != null ? rs
									.getString("CUST_ACID") : "");
					tors = ccStmt.executeQuery();
					if (tors.next()) {
						cumulativeCredit = tors.getString(1) != null ? tors
								.getString(1) : "";
					}
					tors.close();

					// Getting cumulativeDebit
					tors = null;
					cdStmt.setString(
							1,
							rs.getString("CUST_ACID") != null ? rs
									.getString("CUST_ACID") : "");
					cdStmt.setString(
							2,
							rs.getString("CUST_ACID") != null ? rs
									.getString("CUST_ACID") : "");
					tors = cdStmt.executeQuery();

					if (tors.next()) {
						cumulativeDebit = tors.getString(1) != null ? tors
								.getString(1) : "";
					}
					tors.close();

					ctrAccountDatabean.setAccountNo(String
							.format("%-20s",
									rs.getString("tran_ac_no") != null ? rs
											.getString("tran_ac_no") : "")
							.substring(0, 20).replace("\'", " ")
							.replace("\"", " "));
					ctrAccountDatabean.setBranchRefNumber(String
							.format("%-7s", rs.getString("branch_id"))
							.substring(0, 7).replace("\'", " ")
							.replace("\"", " "));
					ctrAccountDatabean.setCumCashDepTurnover(String
							.format("%20s", cumulativeCashDeposit)
							.substring(0, 20).replace("\'", " ")
							.replace("\"", " ").replace(" ", "0"));
					ctrAccountDatabean.setCumCashWidTurnOver(String
							.format("%20s", cumulativeCashWithdrawl)
							.substring(0, 20).replace("\'", " ")
							.replace("\"", " ").replace(" ", "0"));
					ctrAccountDatabean.setCumCreTurnover(String
							.format("%20s", cumulativeCredit).substring(0, 20)
							.replace("\'", " ").replace("\"", " ")
							.replace(" ", "0"));
					ctrAccountDatabean.setCumDebTurnover(String
							.format("%20s", cumulativeDebit).substring(0, 20)
							.replace("\'", " ").replace("\"", " ")
							.replace(" ", "0"));
					ctrAccountDatabean
							.setDateOfAccountOpening(String
									.format("%-8s",
											rs.getString("cust_ac_open_date") != null ? rs
													.getString("cust_ac_open_date")
													: "").substring(0, 8)
									.replace("\'", " ").replace("\"", " "));
					ctrAccountDatabean.setLineNumber(String
							.format("%6s", lineNumber.toString())
							.substring(0, 6).replace(' ', '0'));
					ctrAccountDatabean.setMonthOfRecord(month);
					ctrAccountDatabean.setNameOfAccHolder(String
							.format("%-80s",
									rs.getString("acct_name") != null ? rs
											.getString("acct_name") : "")
							.substring(0, 80).replace("\'", " ")
							.replace("\"", " "));
					ctrAccountDatabean.setRecordType("RAC");
					ctrAccountDatabean.setRiskCategory(String
							.format("%1s", riskCategory).substring(0, 1)
							.replace("\'", " ").replace("\"", " "));
					ctrAccountDatabean.setTypeOfAccount(String
							.format("%1s", typeOfAccount).substring(0, 1)
							.replace("\'", " ").replace("\"", " "));
					ctrAccountDatabean.setTypeOfAccountHolder(String
							.format("%1s", typeOfCustomer).substring(0, 1)
							.replace("\'", " ").replace("\"", " "));
					ctrAccountDatabean.setYearOfRecord(year);
					// ctrAccountDatabeans.add(ctrAccountDatabean);

					String custSex = rs.getString("CUST_SEX") != null ? rs
							.getString("CUST_SEX") : "M";

					if (!custSex.equalsIgnoreCase("M")
							&& !custSex.equalsIgnoreCase("F")) {
						custSex = "O";
					}

					if (typeOfConstitution.equalsIgnoreCase("A")) {
						ctrIndividualDatabean = new CTRIndividualDatabean();
						ctrIndividualDatabean.setReportType("INP");
						ctrIndividualDatabean.setMonthOfRecord(month);
						ctrIndividualDatabean.setYearOfRecord(year);
						ctrIndividualDatabean.setLineNumber(String
								.format("%6s", linInd.toString())
								.substring(0, 6).replace(' ', '0'));
						ctrIndividualDatabean.setBranchRefNo(String
								.format("%-7s", rs.getString("branch_id"))
								.substring(0, 7).replace("\'", " ")
								.replace("\"", " "));
						ctrIndividualDatabean.setAccountNo(String
								.format("%-20s",
										rs.getString("CUST_AC_NO") != null ? rs
												.getString("CUST_AC_NO") : "")
								.substring(0, 20).replace("\'", " ")
								.replace("\"", " "));
						ctrIndividualDatabean.setRelationFlag(String
								.format("%1s", "A").substring(0, 1)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setFullName(String
								.format("%-80s",
										rs.getString("cust_name") != null ? rs
												.getString("cust_name") : "")
								.substring(0, 80).replace("\'", " ")
								.replace("\"", " "));
						ctrIndividualDatabean.setCustId(String
								.format("%-10s",
										rs.getString("CUST_ID") != null ? rs
												.getString("CUST_ID") : "")
								.substring(0, 10).replace("\'", " ")
								.replace("\"", " "));
						ctrIndividualDatabean.setNameOfFatherOrSpouse(String
								.format("%-80s", "").substring(0, 80)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean
								.setOccupation(String
										.format("%-50s",
												rs.getString("CUST_OCCP_CODE") != null ? rs
														.getString("CUST_OCCP_CODE")
														: "").substring(0, 50)
										.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setDateOfBirth(String
								.format("%-8s",
										rs.getString("CUST_DOB") != null ? rs
												.getString("CUST_DOB") : "")
								.substring(0, 8).replace("\'", " ")
								.replace("\"", " "));
						ctrIndividualDatabean.setSex(String
								.format("%1s", custSex).substring(0, 1)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean
								.setNationality(String
										.format("%-2s",
												rs.getString("CUST_COMMU_CNTRY_CODE") != null ? rs
														.getString("CUST_COMMU_CNTRY_CODE")
														: "").substring(0, 2)
										.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setTypeOfIdentification(String
								.format("%1s", "").substring(0, 1)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setIdentificationNumber(String
								.format("%-10s", "").substring(0, 10)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setIssuingAuthority(String
								.format("%-20s", "").substring(0, 20)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setPlaceOfIssue(String
								.format("%-20s", "").substring(0, 20)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean
								.setPanNo(String
										.format("%-10s",
												rs.getString("CUST_PAN_NO") != null ? rs
														.getString("CUST_PAN_NO")
														: "").substring(0, 10)
										.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean
								.setCommunicationAddress1(String
										.format("%-45s",
												rs.getString("CUST_COMMU_ADDR1") != null ? rs
														.getString("CUST_COMMU_ADDR1")
														: "-NA-")
										.substring(0, 45).replace("\'", " ")
										.replace("\"", " "));
						ctrIndividualDatabean
								.setCommunicationAddress2(String
										.format("%-45s",
												rs.getString("CUST_COMMU_ADDR2") != null ? rs
														.getString("CUST_COMMU_ADDR2")
														: "-NA-")
										.substring(0, 45).replace("\'", " ")
										.replace("\"", " "));
						ctrIndividualDatabean
								.setCommunicationAddress3(String
										.format("%-45s",
												rs.getString("cust_commu_city_code") != null ? rs
														.getString("cust_commu_city_code")
														: "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean
								.setCommunicationAddress4(String
										.format("%-45s",
												rs.getString("cust_commu_state_code") != null ? rs
														.getString("cust_commu_state_code")
														: "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean
								.setCommunicationAddress5(String
										.format("%-45s",
												rs.getString("CUST_COMMU_CNTRY_CODE") != null ? rs
														.getString("CUST_COMMU_CNTRY_CODE")
														: "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean
								.setCommunicationPincode(String
										.format("%-6s",
												rs.getString("CUST_COMMU_PIN_CODE") != null ? isIntNumber(rs
														.getString("CUST_COMMU_PIN_CODE")) ? rs
														.getString("CUST_COMMU_PIN_CODE")
														: "000000"
														: "000000")
										.substring(0, 6).replace("\'", " ")
										.replace("\"", " "));
						ctrIndividualDatabean
								.setContactTelephone(String
										.format("%-30s",
												rs.getString("CUST_PHN_NO") != null ? rs
														.getString("CUST_PHN_NO")
														: "").substring(0, 30)
										.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setContactMb(String
								.format("%-30s", "").substring(0, 30)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean
								.setContactEmail(String
										.format("%-50s",
												rs.getString("CUST_EMAIL_ID") != null ? rs
														.getString("CUST_EMAIL_ID")
														: "").substring(0, 50)
										.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setPlaceOfWork(String
								.format("%-80s", "").substring(0, 80)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setSecAddress1(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setSecAddress2(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setSecAddress3(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setSecAddress4(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setSecAddress5(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setSecPincode(String
								.format("%-6s", "").substring(0, 6)
								.replace("\'", " ").replace("\"", " "));
						ctrIndividualDatabean.setSecTelephone(String
								.format("%-30s", "").substring(0, 30)
								.replace("\'", " ").replace("\"", " "));

						// File Writing into Individual...
						// fileData = "";
						// fileData = ctrIndividualDatabean.getReportType() +
						// ctrIndividualDatabean.getMonthOfRecord()
						// + ctrIndividualDatabean.getYearOfRecord() +
						// ctrIndividualDatabean.getLineNumber()
						// + ctrIndividualDatabean.getBranchRefNo() +
						// ctrIndividualDatabean.getAccountNo()
						// + ctrIndividualDatabean.getRelationFlag() +
						// ctrIndividualDatabean.getFullName()
						// + ctrIndividualDatabean.getCustId() +
						// ctrIndividualDatabean.getNameOfFatherOrSpouse()
						// + ctrIndividualDatabean.getOccupation() +
						// ctrIndividualDatabean.getDateOfBirth()
						// + ctrIndividualDatabean.getSex() +
						// ctrIndividualDatabean.getNationality()
						// + ctrIndividualDatabean.getTypeOfIdentification() +
						// ctrIndividualDatabean.getIdentificationNumber()
						// + ctrIndividualDatabean.getIssuingAuthority() +
						// ctrIndividualDatabean.getPlaceOfIssue()
						// + ctrIndividualDatabean.getPanNo() +
						// ctrIndividualDatabean.getCommunicationAddress1()
						// + ctrIndividualDatabean.getCommunicationAddress2() +
						// ctrIndividualDatabean.getCommunicationAddress3()
						// + ctrIndividualDatabean.getCommunicationAddress4() +
						// ctrIndividualDatabean.getCommunicationAddress5()
						// + ctrIndividualDatabean.getCommunicationPincode() +
						// ctrIndividualDatabean.getContactTelephone()
						// + ctrIndividualDatabean.getContactMb() +
						// ctrIndividualDatabean.getContactEmail()
						// + ctrIndividualDatabean.getPlaceOfWork() +
						// ctrIndividualDatabean.getSecAddress1()
						// + ctrIndividualDatabean.getSecAddress2() +
						// ctrIndividualDatabean.getSecAddress3()
						// + ctrIndividualDatabean.getSecAddress4() +
						// ctrIndividualDatabean.getSecAddress5()
						// + ctrIndividualDatabean.getSecPincode() +
						// ctrIndividualDatabean.getSecTelephone();
						// outInd.write(fileData);
						// outInd.write("\n");
						// outInd.flush();
						// Inserting data into table.
						indStmt.setString(1, seqNextVal);
						indStmt.setString(2,
								ctrIndividualDatabean.getReportType());
						indStmt.setString(3,
								ctrIndividualDatabean.getMonthOfRecord());
						indStmt.setString(4,
								ctrIndividualDatabean.getYearOfRecord());
						indStmt.setString(5,
								ctrIndividualDatabean.getLineNumber());
						indStmt.setString(6,
								ctrIndividualDatabean.getBranchRefNo());
						indStmt.setString(7,
								ctrIndividualDatabean.getAccountNo());
						indStmt.setString(8,
								ctrIndividualDatabean.getRelationFlag());
						indStmt.setString(9,
								ctrIndividualDatabean.getFullName());
						indStmt.setString(10, ctrIndividualDatabean.getCustId());
						indStmt.setString(11,
								ctrIndividualDatabean.getNameOfFatherOrSpouse());
						indStmt.setString(12,
								ctrIndividualDatabean.getOccupation());
						indStmt.setString(13,
								ctrIndividualDatabean.getDateOfBirth());
						indStmt.setString(14, ctrIndividualDatabean.getSex());
						indStmt.setString(15,
								ctrIndividualDatabean.getNationality());
						indStmt.setString(16,
								ctrIndividualDatabean.getTypeOfIdentification());
						indStmt.setString(17,
								ctrIndividualDatabean.getIdentificationNumber());
						indStmt.setString(18,
								ctrIndividualDatabean.getIssuingAuthority());
						indStmt.setString(19,
								ctrIndividualDatabean.getPlaceOfIssue());
						indStmt.setString(20, ctrIndividualDatabean.getPanNo());
						indStmt.setString(21, ctrIndividualDatabean
								.getCommunicationAddress1());
						indStmt.setString(22, ctrIndividualDatabean
								.getCommunicationAddress2());
						indStmt.setString(23, ctrIndividualDatabean
								.getCommunicationAddress3());
						indStmt.setString(24, ctrIndividualDatabean
								.getCommunicationAddress4());
						indStmt.setString(25, ctrIndividualDatabean
								.getCommunicationAddress5());
						indStmt.setString(26,
								ctrIndividualDatabean.getCommunicationPincode());
						indStmt.setString(27,
								ctrIndividualDatabean.getContactTelephone());
						indStmt.setString(28,
								ctrIndividualDatabean.getContactMb());
						indStmt.setString(29,
								ctrIndividualDatabean.getContactEmail());
						indStmt.setString(30,
								ctrIndividualDatabean.getPlaceOfWork());
						indStmt.setString(31,
								ctrIndividualDatabean.getSecAddress1());
						indStmt.setString(32,
								ctrIndividualDatabean.getSecAddress2());
						indStmt.setString(33,
								ctrIndividualDatabean.getSecAddress3());
						indStmt.setString(34,
								ctrIndividualDatabean.getSecAddress4());
						indStmt.setString(35,
								ctrIndividualDatabean.getSecAddress5());
						indStmt.setString(36,
								ctrIndividualDatabean.getSecPincode());
						indStmt.setString(37,
								ctrIndividualDatabean.getSecTelephone());

						indStmt.executeUpdate();
						if (linInd % 500 == 0) {
							log.logVerboseText("GenerateCTRFiles",
									"createFiles",
									"Individual data inserted is : " + linInd);

						}
						linInd++;
					} else {
						ctrLegalPersionDatabean = new CTRLegalPersionDatabean();
						ctrLegalPersionDatabean.setReportType("LPE");
						ctrLegalPersionDatabean.setMonthOfRecord(month);
						ctrLegalPersionDatabean.setYearOfRecord(year);
						ctrLegalPersionDatabean.setLineNumber(String
								.format("%6s", linleg.toString())
								.substring(0, 6).replace(' ', '0'));
						ctrLegalPersionDatabean.setBranchRefNo(String
								.format("%-7s", rs.getString("branch_id"))
								.substring(0, 7).replace("\'", " ")
								.replace("\"", " "));
						ctrLegalPersionDatabean.setAccountNo(String
								.format("%-20s",
										rs.getString("CUST_AC_NO") != null ? rs
												.getString("CUST_AC_NO") : "")
								.substring(0, 20).replace("\'", " ")
								.replace("\"", " "));
						ctrLegalPersionDatabean.setRelationFlag(String
								.format("%1s", "A").substring(0, 1)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setFullName(String
								.format("%-80s",
										rs.getString("cust_name") != null ? rs
												.getString("cust_name") : "")
								.substring(0, 80).replace("\'", " ")
								.replace("\"", " "));
						ctrLegalPersionDatabean.setCustId(String
								.format("%-10s",
										rs.getString("CUST_ID") != null ? rs
												.getString("CUST_ID") : "")
								.substring(0, 10).replace("\'", " ")
								.replace("\"", " "));
						ctrLegalPersionDatabean.setNatureOfBusiness(String
								.format("%-50s", "").substring(0, 50)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setDateOfIncorporation(String
								.format("%-8s",
										rs.getString("CUST_DOB") != null ? rs
												.getString("CUST_DOB") : "")
								.substring(0, 8).replace("\'", " ")
								.replace("\"", " ").replace(" ", "0"));
						ctrLegalPersionDatabean.setTypeOfConstitution(String
								.format("%1s", typeOfConstitution)
								.substring(0, 1).replace("\'", " ")
								.replace("\"", " "));
						ctrLegalPersionDatabean.setRegNo(String
								.format("%-20s", "").substring(0, 20)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegAuthority(String
								.format("%-20s", "").substring(0, 20)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setPlaceOfRegistration(String
								.format("%-20s", "").substring(0, 20)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean
								.setPanNo(String
										.format("%-10s",
												rs.getString("CUST_PAN_NO") != null ? rs
														.getString("CUST_PAN_NO")
														: "").substring(0, 10)
										.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean
								.setCommunicationAdd1(String
										.format("%-45s",
												rs.getString("CUST_COMMU_ADDR1") != null ? rs
														.getString("CUST_COMMU_ADDR1")
														: "-NA-")
										.substring(0, 45).replace("\'", " ")
										.replace("\"", " "));
						ctrLegalPersionDatabean
								.setCommunicationAdd2(String
										.format("%-45s",
												rs.getString("CUST_COMMU_ADDR2") != null ? rs
														.getString("CUST_COMMU_ADDR2")
														: "-NA-")
										.substring(0, 45).replace("\'", " ")
										.replace("\"", " "));
						ctrLegalPersionDatabean
								.setCommunicationAdd3(String
										.format("%-45s",
												rs.getString("cust_commu_city_code") != null ? rs
														.getString("cust_commu_city_code")
														: "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean
								.setCommunicationAdd4(String
										.format("%-45s",
												rs.getString("cust_commu_state_code") != null ? rs
														.getString("cust_commu_state_code")
														: "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean
								.setCommunicationAdd5(String
										.format("%-45s",
												rs.getString("CUST_COMMU_CNTRY_CODE") != null ? rs
														.getString("CUST_COMMU_CNTRY_CODE")
														: "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean
								.setCommunicationPinCode(String
										.format("%-6s",
												rs.getString("CUST_COMMU_PIN_CODE") != null ? isIntNumber(rs
														.getString("CUST_COMMU_PIN_CODE")) ? rs
														.getString("CUST_COMMU_PIN_CODE")
														: "000000"
														: "000000")
										.substring(0, 6).replace("\'", " ")
										.replace("\"", " "));
						ctrLegalPersionDatabean
								.setContactTelephone(String
										.format("%-30s",
												rs.getString("CUST_PHN_NO") != null ? rs
														.getString("CUST_PHN_NO")
														: "").substring(0, 30)
										.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setContactFax(String
								.format("%-30s", "").substring(0, 30)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean
								.setContactEmail(String
										.format("%-50s",
												rs.getString("CUST_EMAIL_ID") != null ? rs
														.getString("CUST_EMAIL_ID")
														: "").substring(0, 50)
										.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegisteredAdd1(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegisteredAdd2(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegisteredAdd3(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegisteredAdd4(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegisteredAdd5(String
								.format("%-45s", "").substring(0, 45)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegisteredPincode(String
								.format("%-6s", "").substring(0, 6)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegisteredOffTel(String
								.format("%-30s", "").substring(0, 30)
								.replace("\'", " ").replace("\"", " "));
						ctrLegalPersionDatabean.setRegisteredOffFax(String
								.format("%-30s", "").substring(0, 30)
								.replace("\'", " ").replace("\"", " "));

						// fileData = "";
						// fileData = ctrLegalPersionDatabean.getReportType() +
						// ctrLegalPersionDatabean.getMonthOfRecord()
						// + ctrLegalPersionDatabean.getYearOfRecord() +
						// ctrLegalPersionDatabean.getLineNumber()
						// + ctrLegalPersionDatabean.getBranchRefNo() +
						// ctrLegalPersionDatabean.getAccountNo()
						// + ctrLegalPersionDatabean.getRelationFlag() +
						// ctrLegalPersionDatabean.getFullName()
						// + ctrLegalPersionDatabean.getCustId() +
						// ctrLegalPersionDatabean.getNatureOfBusiness()
						// + ctrLegalPersionDatabean.getDateOfIncorporation() +
						// ctrLegalPersionDatabean.getTypeOfConstitution()
						// + ctrLegalPersionDatabean.getRegNo() +
						// ctrLegalPersionDatabean.getRegAuthority()
						// + ctrLegalPersionDatabean.getPlaceOfRegistration() +
						// ctrLegalPersionDatabean.getPanNo()
						// + ctrLegalPersionDatabean.getCommunicationAdd1() +
						// ctrLegalPersionDatabean.getCommunicationAdd2()
						// + ctrLegalPersionDatabean.getCommunicationAdd3() +
						// ctrLegalPersionDatabean.getCommunicationAdd4()
						// + ctrLegalPersionDatabean.getCommunicationAdd5() +
						// ctrLegalPersionDatabean.getCommunicationPinCode()
						// + ctrLegalPersionDatabean.getContactTelephone() +
						// ctrLegalPersionDatabean.getContactFax()
						// + ctrLegalPersionDatabean.getContactEmail() +
						// ctrLegalPersionDatabean.getRegisteredAdd1()
						// + ctrLegalPersionDatabean.getRegisteredAdd2() +
						// ctrLegalPersionDatabean.getRegisteredAdd3()
						// + ctrLegalPersionDatabean.getRegisteredAdd4() +
						// ctrLegalPersionDatabean.getRegisteredAdd5()
						// + ctrLegalPersionDatabean.getRegisteredPincode() +
						// ctrLegalPersionDatabean.getRegisteredOffTel()
						// + ctrLegalPersionDatabean.getRegisteredOffFax();
						// outLgl.write(fileData);
						// outLgl.write("\n");
						// outLgl.flush();
						// Legal Persion Database Entry

						lglStmt.setString(1, seqNextVal);
						lglStmt.setString(2,
								ctrLegalPersionDatabean.getReportType());
						lglStmt.setString(3,
								ctrLegalPersionDatabean.getMonthOfRecord());
						lglStmt.setString(4,
								ctrLegalPersionDatabean.getYearOfRecord());
						lglStmt.setString(5,
								ctrLegalPersionDatabean.getLineNumber());
						lglStmt.setString(6,
								ctrLegalPersionDatabean.getBranchRefNo());
						lglStmt.setString(7,
								ctrLegalPersionDatabean.getAccountNo());
						lglStmt.setString(8,
								ctrLegalPersionDatabean.getRelationFlag());
						lglStmt.setString(9,
								ctrLegalPersionDatabean.getFullName());
						lglStmt.setString(10,
								ctrLegalPersionDatabean.getCustId());
						lglStmt.setString(11,
								ctrLegalPersionDatabean.getNatureOfBusiness());
						lglStmt.setString(12, ctrLegalPersionDatabean
								.getDateOfIncorporation());
						lglStmt.setString(13,
								ctrLegalPersionDatabean.getTypeOfConstitution());
						lglStmt.setString(14,
								ctrLegalPersionDatabean.getRegNo());
						lglStmt.setString(15,
								ctrLegalPersionDatabean.getRegAuthority());
						lglStmt.setString(16, ctrLegalPersionDatabean
								.getPlaceOfRegistration());
						lglStmt.setString(17,
								ctrLegalPersionDatabean.getPanNo());
						lglStmt.setString(18,
								ctrLegalPersionDatabean.getCommunicationAdd1());
						lglStmt.setString(19,
								ctrLegalPersionDatabean.getCommunicationAdd2());
						lglStmt.setString(20,
								ctrLegalPersionDatabean.getCommunicationAdd3());
						lglStmt.setString(21,
								ctrLegalPersionDatabean.getCommunicationAdd4());
						lglStmt.setString(22,
								ctrLegalPersionDatabean.getCommunicationAdd5());
						lglStmt.setString(23, ctrLegalPersionDatabean
								.getCommunicationPinCode());
						lglStmt.setString(24,
								ctrLegalPersionDatabean.getContactTelephone());
						lglStmt.setString(25,
								ctrLegalPersionDatabean.getContactFax());
						lglStmt.setString(26,
								ctrLegalPersionDatabean.getContactEmail());
						lglStmt.setString(27,
								ctrLegalPersionDatabean.getRegisteredAdd1());
						lglStmt.setString(28,
								ctrLegalPersionDatabean.getRegisteredAdd2());
						lglStmt.setString(29,
								ctrLegalPersionDatabean.getRegisteredAdd3());
						lglStmt.setString(30,
								ctrLegalPersionDatabean.getRegisteredAdd4());
						lglStmt.setString(31,
								ctrLegalPersionDatabean.getRegisteredAdd5());
						lglStmt.setString(32,
								ctrLegalPersionDatabean.getRegisteredPincode());
						lglStmt.setString(33,
								ctrLegalPersionDatabean.getRegisteredOffTel());
						lglStmt.setString(34,
								ctrLegalPersionDatabean.getRegisteredOffFax());

						lglStmt.executeUpdate();

						if (linleg % 500 == 0) {
							log.logVerboseText("GenerateCTRFiles",
									"createFiles", "Legal data inserted is : "
											+ linleg);

						}

						linleg++;

						/*
						 * "For every legal person with Relation flag as 'A', there should be at least one individual person record with Relation flag as 'B'(Authorised signatory)"
						 */
						authoSignatoryStmt.setString(1,
								rs.getString("CUST_AC_NO"));
						ResultSet rsAuthoSignatory = authoSignatoryStmt
								.executeQuery();

						Statement indcuststmt = con_aml.createStatement();
						ResultSet rsindcustid = null;

						while (rsAuthoSignatory.next()) {
							ctrIndividualDatabean = new CTRIndividualDatabean();
							ctrIndividualDatabean.setReportType("INP");
							ctrIndividualDatabean.setMonthOfRecord(month);
							ctrIndividualDatabean.setYearOfRecord(year);
							ctrIndividualDatabean.setLineNumber(String
									.format("%6s", linInd.toString())
									.substring(0, 6).replace(' ', '0'));
							ctrIndividualDatabean.setBranchRefNo(String
									.format("%-7s", rs.getString("branch_id"))
									.substring(0, 7).replace("\'", " ")
									.replace("\"", " "));
							ctrIndividualDatabean
									.setAccountNo(String
											.format("%-20s",
													rs.getString("CUST_AC_NO") != null ? rs
															.getString("CUST_AC_NO")
															: "")
											.substring(0, 20)
											.replace("\'", " ")
											.replace("\"", " "));
							String relation = rsAuthoSignatory.getString("acct_poa_as_rec_type");
							
							if(relation.equals("M"))
								relation="A";
							else if(relation.equals("A"))
								relation="B";
							else if(relation.equals("P"))
								relation="C";
							else if(relation.equals("J"))
								relation="P";
							else
								relation="Z";
							
							ctrIndividualDatabean.setRelationFlag(String
									.format("%1s", relation).substring(0, 1)
									.replace("\'", " ").replace("\"", " "));
							ctrIndividualDatabean
									.setFullName(String
											.format("%-80s",
													rsAuthoSignatory
															.getString("TEXT_LINE_1") != null ? rsAuthoSignatory
															.getString(
																	"TEXT_LINE_1")
															.replace("<", " ")
															.replace(">", " ")
															.replace("?", " ")
															: "")
											.substring(0, 80)
											.replace("\'", " ")
											.replace("\"", " "));
							String incustid = rsAuthoSignatory
									.getString("CUST_ID") != null ? rsAuthoSignatory
									.getString("CUST_ID") : "";
							ctrIndividualDatabean.setCustId(String
									.format("%-10s", incustid).substring(0, 10)
									.replace("\'", " ").replace("\"", " "));
							ctrIndividualDatabean
									.setNameOfFatherOrSpouse(String
											.format("%-80s", "")
											.substring(0, 80)
											.replace("\'", " ")
											.replace("\"", " "));
							if (!incustid.equalsIgnoreCase("")) {
								String indcustquery = "select cust_occp_code,to_char(CUST_DOB,'ddMMyyyy') as CUST_DOB,cust_sex,cust_commu_cntry_code,cust_pan_no,"
										+ " CUST_COMMU_ADDR1,CUST_COMMU_ADDR2,(select REF_DESC from AML_RCT where ref_rec_type = '01' and ref_code =cust_commu_city_code) as cust_commu_city_code,cust_commu_state_code,CUST_COMMU_CNTRY_CODE,CUST_COMMU_PIN_CODE,"
										+ " nvl(CUST_COMMU_PHONE_NUM_1,CUST_COMMU_PHONE_NUM_2) as CUST_PHN_NO,EMAIL_ID as CUST_EMAIL_ID"
										+ " from aml_cust_master where cust_id='"
										+ incustid + "'";
								rsindcustid = indcuststmt
										.executeQuery(indcustquery);
								while (rsindcustid.next()) {
									ctrIndividualDatabean
											.setOccupation(String
													.format("%-50s",
															rsindcustid
																	.getString("CUST_OCCP_CODE") != null ? rsindcustid
																	.getString("CUST_OCCP_CODE")
																	: "")
													.substring(0, 50)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setDateOfBirth(String
													.format("%-8s",
															rsindcustid
																	.getString("CUST_DOB") != null ? rsindcustid
																	.getString("CUST_DOB")
																	: "")
													.substring(0, 8)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean.setSex(String
											.format("%1s", rsindcustid.getString("cust_sex"))
											.substring(0, 1).replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean
											.setNationality(String
													.format("%-2s",
															rsindcustid
																	.getString("CUST_COMMU_CNTRY_CODE") != null ? rsindcustid
																	.getString("CUST_COMMU_CNTRY_CODE")
																	: "")
													.substring(0, 2)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setTypeOfIdentification(String
													.format("%1s", "")
													.substring(0, 1)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setIdentificationNumber(String
													.format("%-10s", "")
													.substring(0, 10)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setIssuingAuthority(String
													.format("%-20s", "")
													.substring(0, 20)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setPlaceOfIssue(String
													.format("%-20s", "")
													.substring(0, 20)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setPanNo(String
													.format("%-10s",
															rsindcustid
																	.getString("CUST_PAN_NO") != null ? rsindcustid
																	.getString("CUST_PAN_NO")
																	: "")
													.substring(0, 10)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setCommunicationAddress1(String
													.format("%-45s",
															rsindcustid
																	.getString("CUST_COMMU_ADDR1") != null ? rsindcustid
																	.getString("CUST_COMMU_ADDR1")
																	: "-NA-")
													.substring(0, 45)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setCommunicationAddress2(String
													.format("%-45s",
															rsindcustid
																	.getString("CUST_COMMU_ADDR2") != null ? rsindcustid
																	.getString("CUST_COMMU_ADDR2")
																	: "-NA-")
													.substring(0, 45)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setCommunicationAddress3(String
													.format("%-45s",
															rsindcustid
																	.getString("cust_commu_city_code") != null ? rsindcustid
																	.getString("cust_commu_city_code")
																	: "XX")
													.substring(0, 45)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setCommunicationAddress4(String
													.format("%-45s",
															rsindcustid
																	.getString("cust_commu_state_code") != null ? rsindcustid
																	.getString("cust_commu_state_code")
																	: "ZZ")
													.substring(0, 45)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setCommunicationAddress5(String
													.format("%-45s",
															rsindcustid
																	.getString("CUST_COMMU_CNTRY_CODE") != null ? rsindcustid
																	.getString("CUST_COMMU_CNTRY_CODE")
																	: "ZZ")
													.substring(0, 45)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setCommunicationPincode(String
													.format("%-6s",
															rsindcustid
																	.getString("CUST_COMMU_PIN_CODE") != null ? isIntNumber(rsindcustid
																	.getString("CUST_COMMU_PIN_CODE")) ? rsindcustid
																	.getString("CUST_COMMU_PIN_CODE")
																	: "000000"
																	: "000000")
													.substring(0, 6)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean
											.setContactTelephone(String
													.format("%-30s",
															rsindcustid
																	.getString("CUST_PHN_NO") != null ? rsindcustid
																	.getString("CUST_PHN_NO")
																	: "")
													.substring(0, 30)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean.setContactMb(String
											.format("%-30s", "")
											.substring(0, 30)
											.replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean
											.setContactEmail(String
													.format("%-50s",
															rsindcustid
																	.getString("CUST_EMAIL_ID") != null ? rsindcustid
																	.getString("CUST_EMAIL_ID")
																	: "")
													.substring(0, 50)
													.replace("\'", " ")
													.replace("\"", " "));
									ctrIndividualDatabean.setPlaceOfWork(String
											.format("%-80s", "")
											.substring(0, 80)
											.replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean.setSecAddress1(String
											.format("%-45s", "")
											.substring(0, 45)
											.replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean.setSecAddress2(String
											.format("%-45s", "")
											.substring(0, 45)
											.replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean.setSecAddress3(String
											.format("%-45s", "")
											.substring(0, 45)
											.replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean.setSecAddress4(String
											.format("%-45s", "")
											.substring(0, 45)
											.replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean.setSecAddress5(String
											.format("%-45s", "")
											.substring(0, 45)
											.replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean.setSecPincode(String
											.format("%-6s", "").substring(0, 6)
											.replace("\'", " ")
											.replace("\"", " "));
									ctrIndividualDatabean
											.setSecTelephone(String
													.format("%-30s", "")
													.substring(0, 30)
													.replace("\'", " ")
													.replace("\"", " "));
								}
							} else {
								ctrIndividualDatabean.setOccupation(String
										.format("%-10s", ""));
								ctrIndividualDatabean.setDateOfBirth(String
										.format("%-8s", ""));
								ctrIndividualDatabean.setSex(String.format(
										"%1s", ""));
								ctrIndividualDatabean.setNationality(String
										.format("%-2s", ""));
								ctrIndividualDatabean
										.setTypeOfIdentification(String
												.format("%1s", "")
												.substring(0, 1)
												.replace("\'", " ")
												.replace("\"", " "));
								ctrIndividualDatabean
										.setIdentificationNumber(String
												.format("%-10s", "")
												.substring(0, 10)
												.replace("\'", " ")
												.replace("\"", " "));
								ctrIndividualDatabean
										.setIssuingAuthority(String
												.format("%-20s", "")
												.substring(0, 20)
												.replace("\'", " ")
												.replace("\"", " "));
								ctrIndividualDatabean.setPlaceOfIssue(String
										.format("%-20s", "").substring(0, 20)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setPanNo(String
										.format("%-10s", "").substring(0, 10)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean
										.setCommunicationAddress1(String
												.format("%-45s", "-NA-")
												.substring(0, 45)
												.replace("\'", " ")
												.replace("\"", " "));
								ctrIndividualDatabean
										.setCommunicationAddress2(String
												.format("%-45s", "-NA-")
												.substring(0, 45)
												.replace("\'", " ")
												.replace("\"", " "));
								ctrIndividualDatabean
										.setCommunicationAddress3(String
												.format("%-10s", "XX"));
								ctrIndividualDatabean
										.setCommunicationAddress4(String
												.format("%-10s", "ZZ"));
								ctrIndividualDatabean
										.setCommunicationAddress5(String
												.format("%-45s", "ZZ"));
								ctrIndividualDatabean
										.setCommunicationPincode(String.format(
												"%-6s", "000000"));
								ctrIndividualDatabean
										.setContactTelephone(String.format(
												"%-10s", ""));
								ctrIndividualDatabean.setContactMb(String
										.format("%-30s", "").substring(0, 30)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setContactEmail(String
										.format("%-10s", ""));
								ctrIndividualDatabean.setPlaceOfWork(String
										.format("%-80s", "").substring(0, 80)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setSecAddress1(String
										.format("%-45s", "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setSecAddress2(String
										.format("%-45s", "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setSecAddress3(String
										.format("%-45s", "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setSecAddress4(String
										.format("%-45s", "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setSecAddress5(String
										.format("%-45s", "").substring(0, 45)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setSecPincode(String
										.format("%-6s", "").substring(0, 6)
										.replace("\'", " ").replace("\"", " "));
								ctrIndividualDatabean.setSecTelephone(String
										.format("%-30s", "").substring(0, 30)
										.replace("\'", " ").replace("\"", " "));
							}

							// File Writing into Individual...
							// fileData = "";
							// fileData = ctrIndividualDatabean.getReportType()
							// + ctrIndividualDatabean.getMonthOfRecord()
							// + ctrIndividualDatabean.getYearOfRecord() +
							// ctrIndividualDatabean.getLineNumber()
							// + ctrIndividualDatabean.getBranchRefNo() +
							// ctrIndividualDatabean.getAccountNo()
							// + ctrIndividualDatabean.getRelationFlag() +
							// ctrIndividualDatabean.getFullName()
							// + ctrIndividualDatabean.getCustId() +
							// ctrIndividualDatabean.getNameOfFatherOrSpouse()
							// + ctrIndividualDatabean.getOccupation() +
							// ctrIndividualDatabean.getDateOfBirth()
							// + ctrIndividualDatabean.getSex() +
							// ctrIndividualDatabean.getNationality()
							// + ctrIndividualDatabean.getTypeOfIdentification()
							// + ctrIndividualDatabean.getIdentificationNumber()
							// + ctrIndividualDatabean.getIssuingAuthority() +
							// ctrIndividualDatabean.getPlaceOfIssue()
							// + ctrIndividualDatabean.getPanNo() +
							// ctrIndividualDatabean.getCommunicationAddress1()
							// +
							// ctrIndividualDatabean.getCommunicationAddress2()
							// +
							// ctrIndividualDatabean.getCommunicationAddress3()
							// +
							// ctrIndividualDatabean.getCommunicationAddress4()
							// +
							// ctrIndividualDatabean.getCommunicationAddress5()
							// + ctrIndividualDatabean.getCommunicationPincode()
							// + ctrIndividualDatabean.getContactTelephone()
							// + ctrIndividualDatabean.getContactMb() +
							// ctrIndividualDatabean.getContactEmail()
							// + ctrIndividualDatabean.getPlaceOfWork() +
							// ctrIndividualDatabean.getSecAddress1()
							// + ctrIndividualDatabean.getSecAddress2() +
							// ctrIndividualDatabean.getSecAddress3()
							// + ctrIndividualDatabean.getSecAddress4() +
							// ctrIndividualDatabean.getSecAddress5()
							// + ctrIndividualDatabean.getSecPincode() +
							// ctrIndividualDatabean.getSecTelephone();
							// outInd.write(fileData);
							// outInd.write("\n");
							// outInd.flush();
							// Inserting data into table.
							indStmt.setString(1, seqNextVal);
							indStmt.setString(2,
									ctrIndividualDatabean.getReportType());
							indStmt.setString(3,
									ctrIndividualDatabean.getMonthOfRecord());
							indStmt.setString(4,
									ctrIndividualDatabean.getYearOfRecord());
							indStmt.setString(5,
									ctrIndividualDatabean.getLineNumber());
							indStmt.setString(6,
									ctrIndividualDatabean.getBranchRefNo());
							indStmt.setString(7,
									ctrIndividualDatabean.getAccountNo());
							indStmt.setString(8,
									ctrIndividualDatabean.getRelationFlag());
							indStmt.setString(9,
									ctrIndividualDatabean.getFullName());
							indStmt.setString(10,
									ctrIndividualDatabean.getCustId());
							indStmt.setString(11, ctrIndividualDatabean
									.getNameOfFatherOrSpouse());
							indStmt.setString(12,
									ctrIndividualDatabean.getOccupation());
							indStmt.setString(13,
									ctrIndividualDatabean.getDateOfBirth());
							indStmt.setString(14,
									ctrIndividualDatabean.getSex());
							indStmt.setString(15,
									ctrIndividualDatabean.getNationality());
							indStmt.setString(16, ctrIndividualDatabean
									.getTypeOfIdentification());
							indStmt.setString(17, ctrIndividualDatabean
									.getIdentificationNumber());
							indStmt.setString(18,
									ctrIndividualDatabean.getIssuingAuthority());
							indStmt.setString(19,
									ctrIndividualDatabean.getPlaceOfIssue());
							indStmt.setString(20,
									ctrIndividualDatabean.getPanNo());
							indStmt.setString(21, ctrIndividualDatabean
									.getCommunicationAddress1());
							indStmt.setString(22, ctrIndividualDatabean
									.getCommunicationAddress2());
							indStmt.setString(23, ctrIndividualDatabean
									.getCommunicationAddress3());
							indStmt.setString(24, ctrIndividualDatabean
									.getCommunicationAddress4());
							indStmt.setString(25, ctrIndividualDatabean
									.getCommunicationAddress5());
							indStmt.setString(26, ctrIndividualDatabean
									.getCommunicationPincode());
							indStmt.setString(27,
									ctrIndividualDatabean.getContactTelephone());
							indStmt.setString(28,
									ctrIndividualDatabean.getContactMb());
							indStmt.setString(29,
									ctrIndividualDatabean.getContactEmail());
							indStmt.setString(30,
									ctrIndividualDatabean.getPlaceOfWork());
							indStmt.setString(31,
									ctrIndividualDatabean.getSecAddress1());
							indStmt.setString(32,
									ctrIndividualDatabean.getSecAddress2());
							indStmt.setString(33,
									ctrIndividualDatabean.getSecAddress3());
							indStmt.setString(34,
									ctrIndividualDatabean.getSecAddress4());
							indStmt.setString(35,
									ctrIndividualDatabean.getSecAddress5());
							indStmt.setString(36,
									ctrIndividualDatabean.getSecPincode());
							indStmt.setString(37,
									ctrIndividualDatabean.getSecTelephone());

							indStmt.executeUpdate();
							if (linInd % 500 == 0) {
								log.logVerboseText("GenerateNTRFiles",
										"createFiles",
										"Individual data inserted is : "
												+ linInd);
							}
							linInd++;
						}
						if (indcuststmt != null) {
							indcuststmt.close();
						}
						if (rsAuthoSignatory != null) {
							rsAuthoSignatory.close();
						}
						if (rsindcustid != null) {
							rsindcustid.close();
						}

					}

					// fileData = "";
					// fileData = ctrAccountDatabean.getRecordType() +
					// ctrAccountDatabean.getMonthOfRecord() +
					// ctrAccountDatabean.getYearOfRecord()
					// + ctrAccountDatabean.getLineNumber() +
					// ctrAccountDatabean.getBranchRefNumber() +
					// ctrAccountDatabean.getAccountNo()
					// + ctrAccountDatabean.getNameOfAccHolder() +
					// ctrAccountDatabean.getTypeOfAccount()
					// + ctrAccountDatabean.getTypeOfAccountHolder() +
					// ctrAccountDatabean.getDateOfAccountOpening()
					// + ctrAccountDatabean.getRiskCategory() +
					// ctrAccountDatabean.getCumCreTurnover()
					// + ctrAccountDatabean.getCumDebTurnover() +
					// ctrAccountDatabean.getCumCashDepTurnover()
					// + ctrAccountDatabean.getCumCashWidTurnOver();
					// outAcc.write(fileData);
					// outAcc.write("\n");

					// Database Entry for Account file.

					acctStmt.setString(1, seqNextVal);
					acctStmt.setString(2, ctrAccountDatabean.getRecordType());
					acctStmt.setString(3, ctrAccountDatabean.getMonthOfRecord());
					acctStmt.setString(4, ctrAccountDatabean.getYearOfRecord());
					acctStmt.setString(5, ctrAccountDatabean.getLineNumber());
					acctStmt.setString(6,
							ctrAccountDatabean.getBranchRefNumber());
					acctStmt.setString(7, ctrAccountDatabean.getAccountNo());
					acctStmt.setString(8,
							ctrAccountDatabean.getNameOfAccHolder());
					acctStmt.setString(9, ctrAccountDatabean.getTypeOfAccount());
					acctStmt.setString(10,
							ctrAccountDatabean.getTypeOfAccountHolder());
					acctStmt.setString(11,
							ctrAccountDatabean.getDateOfAccountOpening());
					acctStmt.setString(12, ctrAccountDatabean.getRiskCategory());
					acctStmt.setString(13,
							ctrAccountDatabean.getCumCreTurnover());
					acctStmt.setString(14,
							ctrAccountDatabean.getCumDebTurnover());
					acctStmt.setString(15,
							ctrAccountDatabean.getCumCashDepTurnover());
					acctStmt.setString(16,
							ctrAccountDatabean.getCumCashWidTurnOver());
					acctStmt.executeUpdate();

					if (lineNumber % 1000 == 0) {
						log.logVerboseText("GenerateCTRFiles", "createFiles",
								"Account data inserted is : " + lineNumber);

					}
					lineNumber++;
				}
				lineNumber--;
				linleg--;
				linInd--;

				if (acctStmt != null) {
					acctStmt.close();
					acctStmt = null;
				}
				if (lglStmt != null) {
					lglStmt.close();
					lglStmt = null;
				}
				if (indStmt != null) {
					indStmt.close();
					indStmt = null;
				}
				if (authoSignatoryStmt != null) {
					authoSignatoryStmt.close();
					authoSignatoryStmt = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				log.logNormalText("GenerateCTRFiles", "createFiles",
						"account file datafatched");

				// Create control file
				file = new File(destinationDir, "CBACTL.txt");
				file.createNewFile();

				out = new BufferedWriter(new FileWriter(file));

				// setting data to particular control file.
				ctrControlDatabean.setNoOfCTRs(String
						.format("%8s", Integer.toString(lineNumber))
						.substring(0, 8).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setNoOfLegalPersions(String
						.format("%8s", Integer.toString(linleg))
						.substring(0, 8).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setNoOfIndividualPersons(String
						.format("%8s", Integer.toString(linInd))
						.substring(0, 8).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				// fileData = ctrControlDatabean.getReportName() +
				// ctrControlDatabean.getSrNoOfReport() +
				// ctrControlDatabean.getRecordType()
				// + ctrControlDatabean.getMonthOfReport() +
				// ctrControlDatabean.getYearOfReport()
				// + ctrControlDatabean.getCompleteNameOfBank() +
				// ctrControlDatabean.getCategoryOfBank() +
				// ctrControlDatabean.getBsrCode()
				// + ctrControlDatabean.getUniqueId() +
				// ctrControlDatabean.getPoName() +
				// ctrControlDatabean.getPoDesignation()
				// + ctrControlDatabean.getPoAddress1() +
				// ctrControlDatabean.getPoAddress2() +
				// ctrControlDatabean.getPoAddress3()
				// + ctrControlDatabean.getPoAddress4() +
				// ctrControlDatabean.getPoAddress5() +
				// ctrControlDatabean.getPoPincode()
				// + ctrControlDatabean.getPoTelephone() +
				// ctrControlDatabean.getPoFax() +
				// ctrControlDatabean.getPoEmail()
				// + ctrControlDatabean.getReportType() +
				// ctrControlDatabean.getReasonForReplacement()
				// + ctrControlDatabean.getSrNoOfOriginalReport() +
				// ctrControlDatabean.getOperationMode()
				// + ctrControlDatabean.getDataStructurVersion() +
				// ctrControlDatabean.getNoOfTotalBranches()
				// + ctrControlDatabean.getNoOfTotalBranches() +
				// ctrControlDatabean.getNoOfBranchesSubmittedCTRs()
				// + ctrControlDatabean.getNoOfCTRs() +
				// ctrControlDatabean.getNoOfTransactions()
				// + ctrControlDatabean.getNoOfIndividualPersons() +
				// ctrControlDatabean.getNoOfLegalPersions()
				// + ctrControlDatabean.getAcknowledgementNo() +
				// ctrControlDatabean.getDateOfAcknowledge();

				// out.write(fileData);
				// out.write("\n");
				// out.close();

				if (ctrControlDatabean != null) {
					log.logVerboseText("GenerateCTRFiles", "createFiles",
							"inserting data into control file.");

					statement = con_aml.createStatement();
					sql = "INSERT INTO P_AML_CTR_CTRL_FILE (CTR_SEQ_NO,REPORT_NAME, SERIAL_NUMBER, RECORD_TYPE,MONTH_OF_RECORD, YEAR_OF_RECORD, COMP_NAME_OF_BANK, "
							+ "CATEGORY_BANK, BSR_CODE, UID_FIU, PO_NAME, PO_DESIGNATION, PO_ADDRESS1,PO_ADDRESS2, PO_ADDRESS3, PO_ADDRESS4,"
							+ " PO_ADDRESS5, PO_PINCODE, PO_TELEPHONE,PO_FAX, PO_EMAIL, REPORT_TYPE, REASON_FOR_REPLACEMENT, SRNO_ORIGINAL_REPORT,"
							+ " OPERATION_MODE,DS_VERSION, TOTAL_BRANCH_NO, B_NO_INCLUDING_NIL, B_NO_EXCLUDING_NIL, NO_OF_CTRS, NO_OF_TRANSACTIONS,"
							+ " NO_OF_INDIVIDUALS, NO_OF_LEGALS, ACK_NO,ACK_DATE,CREATED_BY,CREATED_DATE,STATUS)  VALUES "
							+ "('"
							+ seqNextVal
							+ "','"
							+ ctrControlDatabean.getReportName()
							+ "','"
							+ ctrControlDatabean.getSrNoOfReport()
							+ "','"
							+ ctrControlDatabean.getRecordType()
							+ "', "
							+ "'"
							+ ctrControlDatabean.getMonthOfReport()
							+ "','"
							+ ctrControlDatabean.getYearOfReport()
							+ "','"
							+ ctrControlDatabean.getCompleteNameOfBank()
							+ "',"
							+ "'"
							+ ctrControlDatabean.getCategoryOfBank()
							+ "','"
							+ ctrControlDatabean.getBsrCode()
							+ "','"
							+ ctrControlDatabean.getUniqueId()
							+ "','"
							+ ctrControlDatabean.getPoName()
							+ "',"
							+ "'"
							+ ctrControlDatabean.getPoDesignation()
							+ "','"
							+ ctrControlDatabean.getPoAddress1()
							+ "','"
							+ ctrControlDatabean.getPoAddress2()
							+ "'"
							+ ",'"
							+ ctrControlDatabean.getPoAddress3()
							+ "','"
							+ ctrControlDatabean.getPoAddress4()
							+ "','"
							+ ctrControlDatabean.getPoAddress5()
							+ "'"
							+ ",'"
							+ ctrControlDatabean.getPoPincode()
							+ "','"
							+ ctrControlDatabean.getPoTelephone()
							+ "','"
							+ ctrControlDatabean.getPoFax()
							+ "'"
							+ ",'"
							+ ctrControlDatabean.getPoEmail()
							+ "','"
							+ ctrControlDatabean.getReportType()
							+ "','"
							+ ctrControlDatabean.getReasonForReplacement()
							+ "'"
							+ ",'"
							+ ctrControlDatabean.getSrNoOfOriginalReport()
							+ "','"
							+ ctrControlDatabean.getOperationMode()
							+ "','"
							+ ctrControlDatabean.getDataStructurVersion()
							+ "'"
							+ ",'"
							+ Integer.parseInt(ctrControlDatabean
									.getNoOfTotalBranches())
							+ "','"
							+ Integer.parseInt(ctrControlDatabean
									.getNoOfTotalBranches())
							+ "','"
							+ Integer.parseInt(ctrControlDatabean
									.getNoOfBranchesSubmittedCTRs())
							+ "','"
							+ ctrControlDatabean.getNoOfCTRs()
							+ "','"
							+ ctrControlDatabean.getNoOfTransactions()
							+ "','"
							+ ctrControlDatabean.getNoOfIndividualPersons()
							+ "','"
							+ ctrControlDatabean.getNoOfLegalPersions()
							+ "','"
							+ ctrControlDatabean.getAcknowledgementNo()
							+ "','"
							+ ctrControlDatabean.getDateOfAcknowledge()
							+ "','" + AMLConstants.SYSTEM + "',sysdate,'P')";

					int returnVal = statement.executeUpdate(sql);
					log.logVerboseText("GenerateCTRFiles", "createFiles",
							"Control file return value." + returnVal);

				}

				if (statement != null) {
					statement.close();
					statement = null;
				}

				log.logNormalText("GenerateCTRFiles", "createFiles",
						"control file created.");

				sql = "UPDATE P_AML_CTR_CTRL_FILE SET NO_OF_CTRS='"
						+ lineNumber + "',NO_OF_INDIVIDUALS='" + linInd
						+ "',NO_OF_LEGALS='" + linleg + "' where CTR_SEQ_NO='"
						+ seqNextVal + "'";
				statement = con_aml.createStatement();
				statement.executeUpdate(sql);
				ctrControlDatabean.setNoOfCTRs(String
						.format("%8s", Integer.toString(lineNumber))
						.substring(0, 8).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setNoOfIndividualPersons(String
						.format("%8s", Integer.toString(linInd))
						.substring(0, 8).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));
				ctrControlDatabean.setNoOfLegalPersions(String
						.format("%8s", Integer.toString(linleg))
						.substring(0, 8).replace(' ', '0').replace("\'", " ")
						.replace("\"", " "));

				outInd.close();
				outLgl.close();
				outAcc.close();

				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (statement != null) {
					statement.close();
					statement = null;
				}
				con_aml.commit();
				log.logNormalText("GenerateCTRFiles", "createFiles",
						"Connection.commit called.");

				isFilesGenerated = true;

			}
		} catch (SQLException sqle) {
			log.logExceptionText("GenerateCTRFiles", "createFiles",
					"Exception e:" + sqle);
			try {
				if (con_aml != null) {
					con_aml.rollback();
				}
			} catch (SQLException e) {
				log.logExceptionText("GenerateCTRFiles", "createFiles",
						"Exception e:" + e);

			}
			sqle.printStackTrace();

		} catch (Exception e) {
			log.logExceptionText("GenerateCTRFiles", "createFiles",
					"Exception e:" + e);

			try {
				if (con_aml != null) {
					con_aml.rollback();
				}
			} catch (SQLException ez) {
				log.logExceptionText("GenerateCTRFiles", "createFiles",
						"Exception e:" + e);
			}
			e.printStackTrace();

		} finally {
			try {

				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}

			} catch (Exception ex) {
				log.logExceptionText("GenerateCTRFiles", "createFiles",
						"Exception e:" + ex);
				ex.printStackTrace();
			}
		}
		return isFilesGenerated;
	}

	/**
	 * This is for Getting From and To date for generating CTR based on the
	 * table values.
	 */
	public static void getFromToDate() {
		// For Guidence oracle query is
		// "select trunc(to_date('29/2/2008','dd/MM/yyyy'),'MM'),last_day (to_date('29/2/2008','dd/MM/yyyy')) from dual"
		log.logVerboseText("GenerateCTRFiles", "getFromToDate", "Start");
		Statement statement = null;
		String query = "";
		ResultSet rs = null;
		// String lastCTRDate = "";
		try {
			// Actaul Query
			// Need to change in actual patch -30 cut
			query = "select to_char(max(LAST_CTR_TODATE)+1,'yyyy-MM-dd hh:mm:ss') as fromDate, "
					+ "trunc(to_date(to_char(sysdate,'dd-MM-yyyy'),'dd-MM-yyyy'),'MM')-1 as toDate from P_AML_CTR_CONTROLMAP where LAST_CTR_STATUS='A'";

			// query =
			// "select distinct (select to_char(max(last_ctr_todate)+1,'yyyy-MM-dd hh:mi:ss') from p_aml_ctr_controlmap where last_ctr_todate <> (select max(last_ctr_todate) from p_aml_ctr_controlmap)) as fromDate, "
			// +
			// "trunc(to_date(to_char(sysdate,'dd-MM-yyyy'),'dd-MM-yyyy'),'MM')-1 as toDate from P_AML_CTR_CONTROLMAP where LAST_CTR_STATUS='A'";

			// System.out.println("query == " + query);

			log.logVerboseText("GenerateCTRFiles", "getFromToDate",
					"Query is : " + query);

			statement = con_aml.createStatement();

			rs = statement.executeQuery(query);

			log.logVerboseText("GenerateCTRFiles", "getFromToDate",
					"At result set");

			while (rs.next()) {
				toDate = rs.getString("toDate") != null ? rs
						.getString("toDate") : "";
				fromDate = rs.getString("fromDate") != null ? rs
						.getString("fromDate") : "";
			}

		} catch (SQLException e) {
			log.logExceptionText("GenerateCTRFiles", "getFromToDate",
					"Exception" + e);

		} catch (Exception e) {
			log.logExceptionText("GenerateCTRFiles", "getFromToDate",
					"Exception" + e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (Exception e) {
				log.logExceptionText("GenerateCTRFiles", "getFromToDate",
						"Exception" + e);

			}
		}
		log.logVerboseText("GenerateCTRFiles", "getFromToDate", "End");

	}

	private static int createCTRMonthWise() {
		log.logVerboseText("GenerateCTRFiles", "createCTRMonthWise", "Start");

		ResultSet rs = null;
		Statement stat = null;// con_aml.createStatement();
		String originalFromDate = fromDate;
		String originalToDate = toDate;

		int taskFinish = 0;
		boolean toContinue = true;
		try {
			con_aml.setAutoCommit(false);
			while (toContinue) {

				String sql = "SELECT floor(MONTHS_BETWEEN(to_date('"
						+ toDate.substring(0, 10) + "','yyyy-MM-dd'),to_date('"
						+ fromDate.substring(0, 10)
						+ "','yyyy-MM-dd'))) AS MONTHS_BETWEEN FROM Dual";
				System.out.println("sql == " + sql);
				stat = con_aml.createStatement();
				rs = stat.executeQuery(sql);
				int monthsBetween = 0;
				if (rs.next()) {
					monthsBetween = rs.getInt("MONTHS_BETWEEN");
				}

				if (monthsBetween >= 0) {

					String query = "select trunc(to_date('"
							+ fromDate.substring(0, 10)
							+ "','yyyy-MM-dd'),'MM') as firstDay ,last_day (to_date('"
							+ fromDate.substring(0, 10)
							+ "','yyyy-MM-dd')) as lastday from dual";

					ResultSet newRs = null;
					Statement newStat = con_aml.createStatement();

					String newFromDate = "";
					String newToDate = "";

					newRs = newStat.executeQuery(query);

					if (newRs.next()) {

						newFromDate = newRs.getString("firstDay");
						newToDate = newRs.getString("lastday");

						// calling stored procedure for CTR
						callSPForGenerateCTR(newFromDate, newToDate);
						boolean filesResult = createFiles();

						if (ctrId != null && !ctrId.equalsIgnoreCase("")) {
							ResourceBundle rb = ResourceBundle
									.getBundle("AMLProp");
							String strDirectory = rb.getString("CTRDIR");
							String ctrFilePath = strDirectory + ctrId;
							ZipFolder.zipFolder(ctrFilePath);

							// Creating CTR XML files.
							// GenerateCTR gctr = new GenerateCTR();
							// try {
							// gctr.mainCTR(ctrId, log);
							// } catch (JAXBException e) {
							// log.logExceptionText("GenerateCTRFiles","Main",
							// "JAXBException e:"+e);
							//
							// }
							// To generate PDF file from template for CTR.
							RegulatoryReportsPdf.generatePdfFiles(log, ctrId,
									"CTR");

							// To generate report as excel for CTR for amount is
							// greate then or equals to 10000000
							RegulatoryReportsPdf.generateExcelFile(log, ctrId,
									"CTR");

						}
						// Set CTR Entry to the control map table.
						if (filesResult) {
							taskFinish = setCTRControlMap(newFromDate,
									newToDate);
						}

					}
					newRs.close();

					String frdtQuery = "select to_date('"
							+ newToDate.substring(0, 10)
							+ "','yyyy-MM-dd')+1 as fromDate from dual";

					newRs = newStat.executeQuery(frdtQuery);

					if (newRs.next()) {
						fromDate = newRs.getString("fromDate");

					}

					String stopLoopQuery = "select case when to_date('"
							+ fromDate.substring(0, 10)
							+ "','yyyy-MM-dd')<to_date('"
							+ toDate.substring(0, 10)
							+ "','yyyy-MM-dd') then '1' "
							+ " when to_date('"
							+ fromDate.substring(0, 10)
							+ "','yyyy-MM-dd')<to_date('"
							+ toDate.substring(0, 10)
							+ "','yyyy-MM-dd') then '0' end as result from dual";

					ResultSet lastRs = null;
					Statement lastStat = null;
					lastStat = con_aml.createStatement();

					lastRs = lastStat.executeQuery(stopLoopQuery);
					String cont = "";
					if (lastRs.next()) {
						cont = lastRs.getString("result") != null ? lastRs
								.getString("result") : "5";
					}
					if (cont.equalsIgnoreCase("1")) {
						toContinue = true;
					} else {
						toContinue = false;
					}
				} else {
					toContinue = false;
				}

			}

			fromDate = originalFromDate;
			toDate = originalToDate;

		} catch (SQLException e) {
			e.printStackTrace();
			log.logExceptionText("GenerateCTRFiles", "createCTRMonthWise",
					"Exception e:" + e);

			try {
				con_aml.rollback();
			} catch (SQLException e1) {
				log.logExceptionText("GenerateCTRFiles", "createCTRMonthWise",
						"Exception e:" + e1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.logExceptionText("GenerateCTRFiles", "createCTRMonthWise",
					"Exception e:" + e);

			try {
				con_aml.rollback();
			} catch (SQLException e1) {
				log.logExceptionText("GenerateCTRFiles", "createCTRMonthWise",
						"Exception e:" + e1);

			}

		} finally {
			try {
				if (stat != null) {
					stat.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					con_aml.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
					log.logExceptionText("GenerateCTRFiles",
							"createCTRMonthWise", "Exception e:" + e1);

				}
				log.logExceptionText("GenerateCTRFiles", "createCTRMonthWise",
						"Exception e:" + e);

			}
		}

		log.logNormalText("GenerateCTRFiles", "createCTRMonthWise", "End");
		return taskFinish;
	}

	/**
	 * 
	 * @return int flag for successful entry of CTR generation and Control map
	 *         entry
	 */
	private static int setCTRControlMap(String newFromDate, String newToDate) {
		log.logVerboseText("GenerateCTRFiles", "setCTRControlMap", "Start");

		Statement statement = null;
		String query = "";
		int returnVal = 1234;
		PreparedStatement pstmt = null;
		try {
			con_aml.setAutoCommit(false);

			query = "INSERT INTO P_AML_CTR_CONTROLMAP (LAST_CTR_DATE, LAST_CTR_STATUS, LAST_CTR_FROMDATE, LAST_CTR_TODATE, CTR_SEQ_NO) "
					+ "VALUES (sysdate,'A',to_date(?,'yyyy-MM-dd'),to_date(?,'yyyy-MM-dd'),?)";

			pstmt = con_aml.prepareStatement(query);
			pstmt.setString(1, newFromDate.substring(0, 10));
			pstmt.setString(2, newToDate.substring(0, 10));
			pstmt.setString(3, ctrId);

			returnVal = pstmt.executeUpdate();

			con_aml.commit();

		} catch (SQLException e) {
			log.logExceptionText("GenerateCTRFiles", "setCTRControlMap",
					"Exception e:" + e);

			try {
				con_aml.rollback();
			} catch (SQLException e1) {
				log.logExceptionText("GenerateCTRFiles", "setCTRControlMap",
						"Exception e:" + e1);
			}
		} catch (Exception e) {
			log.logExceptionText("GenerateCTRFiles", "setCTRControlMap",
					"Exception e:" + e);

			try {
				con_aml.rollback();
			} catch (SQLException e1) {
				log.logExceptionText("GenerateCTRFiles", "setCTRControlMap",
						"Exception e:" + e1);
			}
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (Exception e) {
				try {
					con_aml.rollback();
				} catch (SQLException e1) {
					log.logExceptionText("GenerateCTRFiles",
							"setCTRControlMap", "Exception e:" + e1);
				}
				log.logExceptionText("GenerateCTRFiles", "setCTRControlMap",
						"Exception e:" + e);
			}
		}
		log.logNormalText("GenerateCTRFiles", "setCTRControlMap", "End");

		return returnVal;
	}

	public static void generateCTRCustomerReports(String custType) {
		ResultSet rs = null;
		PreparedStatement insertStmt = null;
		try {
			log.logNormalText("GenerateCTRFiles", "generateCTRCustomerReports",
					"Start");
			insertStmt = con_aml
					.prepareStatement("insert into P_AML_CTR_CUST_REPORT values (?,?,?,?,?,?,?,?,?,?)");
			String tableName = "";
			String thresholdAmount = "";

			if (custType.equals("I")) {
				tableName = " p_aml_ctr_individual_file ";
				thresholdAmount = " 5000000 ";
			} else {
				tableName = " P_AML_CTR_LGLPERSIONL_FILE ";
				thresholdAmount = " 100000000 ";
			}
			String query = " select dtls,rm from ( select dtls,rownum rm from ("
					+ " select distinct b.sol_desc||'~'||c.acct_name||'~'||a.ac_no||'~'||a.tran_id||'~'||a.date_of_tran||'~'||a.mode_of_tran||'~'||a.deb_cre_flg||'~'||a.amount||'~'||a.curr_of_tran dtls  "
					+ " FROM p_aml_ctr_tran_file a, "
					+ " p_aml_sol b, "
					+ " aml_ac_master c,"
					+ tableName
					+ " d, P_AML_CTR_CTRL_FILE e"
					+ " where b.SOL_ID = a.SOL_ID"
					+ " and d.AC_NO=a.AC_NO"
					+ " and c.CUST_AC_NO = trim(a.AC_NO)  and a.CTR_SEQ_NO =e.CTR_SEQ_NO and e.STATUS='A' "
					+ " and a.AC_NO in ( select ac_no from ( select sum(amount) sm,ac_no from p_aml_ctr_tran_file  group by ac_no ) where sm> "
					+ thresholdAmount + "))  ) ";
			System.out.println(query);

			rs = con_aml.createStatement().executeQuery(query);
			int counter = 0;
			while (rs.next()) {

				String details[] = rs.getString(1).split("~");

				insertStmt.setString(1, details[0]);
				insertStmt.setString(2, details[1]);
				insertStmt.setString(3, details[2]);
				insertStmt.setString(4, details[3]);
				insertStmt.setString(5, details[4]);
				insertStmt.setString(6, details[5]);
				insertStmt.setString(7, details[6]);
				insertStmt.setString(8, details[7]);
				insertStmt.setString(9, details[8]);
				insertStmt.setString(10, custType);
				insertStmt.executeUpdate();
				counter++;
				if (counter % 50000 == 0) {
					log.logNormalText("GenerateCTRFiles",
							"generateCTRCustomerReports", " " + custType
									+ " :: " + counter);
					con_aml.commit();
					con_aml.setAutoCommit(false);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			try {
				con_aml.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			log.logExceptionText("GenerateCTRFiles",
					"generateCTRCustomerReports", "Exception e:" + e);
		} finally {
			if (insertStmt != null) {
				try {
					insertStmt.close();
				} catch (SQLException e) {

					e.printStackTrace();
					log.logExceptionText("GenerateCTRFiles",
							"generateCTRCustomerReports",
							"Finally-Exception e:" + e);
				}
			}
		}
		log.logNormalText("GenerateCTRFiles", "generateCTRCustomerReports",
				"End");
	}

	public static void generateCTRCustomerSixMnthReport() {
		ResultSet rs = null;
		PreparedStatement insertStmt = null;
		try {
			log.logNormalText("GenerateCTRFiles",
					"generateCTRCustomerSixMnthReport", "Start");
			insertStmt = con_aml
					.prepareStatement("insert into P_AML_CTR_SIXMNT_REPORT  values (?,?,?,?,?,?,?,?,?,?)");

			String query = " SELECT dtls, rm "
					+ " FROM (SELECT dtls, ROWNUM rm  "
					+ " FROM (SELECT DISTINCT    b.sol_desc "
					+ "       || '~' "
					+ "                                || c.acct_name "
					+ "                                || '~' "
					+ "                                || a.ac_no  "
					+ "                                || '~' "
					+ "             || a.tran_id "
					+ "             || '~' "
					+ "             || a.date_of_tran "
					+ "             || '~' "
					+ "             || a.mode_of_tran "
					+ "             || '~' "
					+ "             || a.deb_cre_flg "
					+ "             || '~' "
					+ "             || a.amount "
					+ "             || '~' "
					+ "             || a.curr_of_tran "
					+ "             || '~' "
					+ "             || e.ctr_seq_no "
					+ "              dtls  "
					+ "        FROM p_aml_ctr_tran_file a, "
					+ "             p_aml_sol b, "
					+ "             aml_ac_master c, "
					+ "             p_aml_ctr_account_file d, "
					+ "             p_aml_ctr_ctrl_file e "
					+ "       WHERE b.sol_id = a.sol_id "
					+ "         AND d.account_no = a.ac_no "
					+ "         AND c.cust_ac_no = TRIM (a.ac_no) "
					+ "         AND a.ctr_seq_no = e.ctr_seq_no "
					+ "         AND e.status = 'A' "
					+ "         AND e.CREATED_DATE between sysdate-180 and sysdate "
					+ "         AND a.ac_no IN ( SELECT account_no "
					+ " FROM (SELECT   COUNT (ctr_seq_no) cnt, account_no "
					+ " FROM p_aml_ctr_account_file "
					+ " WHERE ctr_seq_no IN ( "
					+ "  SELECT ctr_seq_no "
					+ "   FROM p_aml_ctr_ctrl_file "
					+ "  WHERE status = 'A' "
					+ "    AND created_date BETWEEN SYSDATE - 180 AND SYSDATE) "
					+ " GROUP BY account_no) " + " WHERE cnt > 4 " + " ))) ";
			System.out.println(query);

			rs = con_aml.createStatement().executeQuery(query);
			int counter = 0;
			while (rs.next()) {

				String details[] = rs.getString(1).split("~");

				insertStmt.setString(1, details[0]);
				insertStmt.setString(2, details[1]);
				insertStmt.setString(3, details[2]);
				insertStmt.setString(4, details[3]);
				insertStmt.setString(5, details[4]);
				insertStmt.setString(6, details[5]);
				insertStmt.setString(7, details[6]);
				insertStmt.setString(8, details[7]);
				insertStmt.setString(9, details[8]);
				insertStmt.setString(10, details[9]);
				insertStmt.executeUpdate();
				counter++;
				if (counter % 5000 == 0) {
					log.logNormalText("GenerateCTRFiles",
							"generateCTRCustomerSixMnthReport", " Inserted :: "
									+ counter);
					con_aml.commit();
					con_aml.setAutoCommit(false);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			try {
				con_aml.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			log.logExceptionText("GenerateCTRFiles",
					"generateCTRCustomerSixMnthReport", "Exception e:" + e);
		} finally {
			if (insertStmt != null) {
				try {
					insertStmt.close();
				} catch (SQLException e) {

					e.printStackTrace();
					log.logExceptionText("GenerateCTRFiles",
							"generateCTRCustomerSixMnthReport",
							"Finally-Exception e:" + e);
				}
			}
		}
		log.logNormalText("GenerateCTRFiles",
				"generateCTRCustomerSixMnthReport", "End");
	}

	public static void callReport() {
		try {

			// logLevel = user.getLogLevel();
			// System.out.println("logLevel =" + logLevel);
			// if (logLevel == 0) {
			// user.setInfoLogger(new InfoLoggerExceptions());
			// } else if (logLevel == 1) {
			// user.setInfoLogger(new InfoLoggerNormal());
			// } else if (logLevel == 2) {
			// user.setInfoLogger(new InfoLoggerTrace());
			// } else if (logLevel == 3) {
			// user.setInfoLogger(new InfoLoggerDebug());
			// } else if (logLevel == 4) {
			// user.setInfoLogger(new InfoLoggerVerbose());
			// } else {
			// user.setInfoLogger(new InfoLoggerVerbose());
			// }
			//
			// log = user.getInfoLogger();
			// log.setLogFileName(logFileName);
			//
			// log.logExceptionText("GenerateCTRFiles", "main",
			// "Exception(0) Text");
			// log.logNormalText("GenerateCTRFiles", "main", "Normal(1) Text");
			// log.logTraceText("GenerateCTRFiles", "main", "Trace(2) Text");
			// log.logDebugText("GenerateCTRFiles", "main", "Debug(3) Text");
			// log.logVerboseText("GenerateCTRFiles", "main",
			// "Verbose(4) Text");
			//
			//
			// LoadDatabaseAml();
			if (con_aml.createStatement().executeUpdate(
					"delete from P_AML_CTR_CUST_REPORT") >= 0) {
				generateCTRCustomerReports("I");
				generateCTRCustomerReports("N");
			}
			if (con_aml.createStatement().executeUpdate(
					"delete from P_AML_CTR_SIXMNT_REPORT") >= 0) {
				generateCTRCustomerSixMnthReport();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isIntNumber(String num) {
		try {
			Integer.valueOf(num);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static void main(String args[]) {
		LoadDatabaseAml();
		GenerateCTRFiles ctrObj = new GenerateCTRFiles();
		Thread tobj = new Thread(ctrObj);
		tobj.start();
	}

	public String checkDate() {
		Statement stmt = null;
		ResultSet rs = null;
		String flag = "";
		try {
			stmt = con_aml.createStatement();

			rs = stmt
					.executeQuery("select decode(to_char(sysdate,'dd'),value,'Y','N') from p_aml_general where name='CTRDATE'");
			while (rs.next()) {
				flag = rs.getString(1);
			}

			if (flag.equals("Y")) {
				rs = stmt
						.executeQuery("select decode(count(1),0,'Y','N') from p_aml_ctr_ctrl_file where month_of_record = to_char(add_months(sysdate,-1),'mm')");
				while (rs.next()) {
					flag = rs.getString(1);
				}
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
		return flag;
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
			LoadDatabaseAml();
			GenerateCTRFiles ctrObj = new GenerateCTRFiles();
			Thread tobj = new Thread(ctrObj);
			tobj.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	@Override
	public void run() {
		while (true) {
			if (checkDate().equals("Y")) {
				logLevel = user.getLogLevel();
				System.out.println("logLevel =" + logLevel);
				if (logLevel == 0) {
					user.setInfoLogger(new InfoLoggerExceptions());
				} else if (logLevel == 1) {
					user.setInfoLogger(new InfoLoggerNormal());
				} else if (logLevel == 2) {
					user.setInfoLogger(new InfoLoggerTrace());
				} else if (logLevel == 3) {
					user.setInfoLogger(new InfoLoggerDebug());
				} else if (logLevel == 4) {
					user.setInfoLogger(new InfoLoggerVerbose());
				} else {
					user.setInfoLogger(new InfoLoggerVerbose());
				}

				log = user.getInfoLogger();
				log.setLogFileName(logFileName);

				log.logExceptionText("GenerateCTRFiles", "main",
						"Exception(0) Text");
				log.logNormalText("GenerateCTRFiles", "main", "Normal(1) Text");
				log.logTraceText("GenerateCTRFiles", "main", "Trace(2) Text");
				log.logDebugText("GenerateCTRFiles", "main", "Debug(3) Text");
				log.logVerboseText("GenerateCTRFiles", "main",
						"Verbose(4) Text");

				boolean spResult = false;
				boolean filesResult = false;
				// boolean tablesResult = false;

				try {
					log.logNormalText("GenerateCTRFiles", "main", "Start");

					// Getting database For Connection and all other things.
					LoadDatabaseAml();
					// System.out.println("Database loaded");
					// Getting From and To Date for Stored procedures.
					getFromToDate();
					// System.out.println("getFromToDate done");
					// Calling CTR Month Wise
					createCTRMonthWise();
					// System.out.println("createCTRMOnth wise done");
					// Calling Generate CTR Stored procedure.
					spResult = false;// callSPForGenerateCTR(fromDate,toDate);

					// Generating Files And database for CTR Transactions.
					if (spResult) {
						filesResult = createFiles();

						// Set CTR Entry to the control map table.
						int taskFinish = 0;

						if (filesResult) {
							taskFinish = setCTRControlMap(fromDate, toDate);
						}
						if (taskFinish == 1) {
							log.logNormalText("GenerateCTRFiles", "main",
									"All Task Finished For CTR Generation.");
						}
					} else {
						// BgProcessLogger.writeToFile(logFile,
						// "GenerateCTRFiles",
						// "main", "No CTR Generation called.");
					}
					log.logNormalText("GenerateCTRFiles", "main",
							"All Task Finished For CTR Generation.");
					try {
						if (ctrId != null && !ctrId.equalsIgnoreCase("")) {
							ResourceBundle rb = ResourceBundle
									.getBundle("AMLProp");
							String strDirectory = rb.getString("CTRDIR");
							String ctrFilePath = strDirectory + ctrId;
							ZipFolder.zipFolder(ctrFilePath);

							GenerateCTR gctr = new GenerateCTR();
							try {
								gctr.mainCTR(log, ctrId, "CTR", "Y");
							} catch (JAXBException e) {
								log.logExceptionText("GenerateCTRFiles",
										"Main", "JAXBException e:" + e);

							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					log.logNormalText("GenerateCTRFiles", "main",
							"################# CTR FINISHED #############:: REPORT GENERATION STARTED");
					callReport();
					log.logNormalText("GenerateCTRFiles", "main", "End");

				} catch (Exception e) {
					log.logExceptionText("GenerateCTRFiles", "Main",
							"Exception e:" + e);

				}
			}
			try {
				Thread.sleep(60 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

package com.idbi.intech.aml.dao;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.idbi.intech.aml.bg_process.AMLConstants;
import com.idbi.intech.aml.databeans.Account;
import com.idbi.intech.aml.databeans.CTRPdfDataBean;
import com.idbi.intech.aml.databeans.STRAccountFileBean;
import com.idbi.intech.aml.databeans.STRBranchFileBean;
import com.idbi.intech.aml.databeans.STRConfigFileBean;
import com.idbi.intech.aml.databeans.STRIndividualFileBean;
import com.idbi.intech.aml.databeans.STRLPEFIleBean;
import com.idbi.intech.aml.databeans.STRTransactionFileBean;
import com.idbi.intech.aml.databeans.STRWalkInBranchDataBean;
import com.idbi.intech.aml.databeans.STRWalkInControlDataBean;
import com.idbi.intech.aml.databeans.STRWalkInCustomerDataBean;
import com.idbi.intech.aml.databeans.STRWalkInTransactionDataBean;
import com.idbi.intech.aml.util.InfoLogger;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AMLUserDAO {

	/**
	 * 
	 * @param logger
	 * @param reportId
	 * @param reportType
	 * @return
	 * @throws Exception
	 */

	public STRWalkInControlDataBean strWalkInDetails() throws Exception {

		Connection connection = null;
		ResultSet rs = null;
		STRWalkInControlDataBean controlDataBean = new STRWalkInControlDataBean();

		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
			connection.setAutoCommit(false);
		} catch (SQLException sqlExp) {

			sqlExp.printStackTrace();
			System.err.println("Exception :" + sqlExp);
		} catch (Exception cnfExp) {

			cnfExp.printStackTrace();
			System.err.println("Exception :" + cnfExp);
		}

		try {
			ArrayList<STRWalkInTransactionDataBean> tranList = new ArrayList<STRWalkInTransactionDataBean>();
			ArrayList<STRWalkInBranchDataBean> branchList = new ArrayList<STRWalkInBranchDataBean>();
			ArrayList<STRWalkInCustomerDataBean> custList = new ArrayList<STRWalkInCustomerDataBean>();

			rs = connection.createStatement().executeQuery("select * from aml_str_wi_cust_file_TMP");
			while (rs.next()) {
				STRWalkInCustomerDataBean customerDataBean = new STRWalkInCustomerDataBean();

				customerDataBean.setLineNumber(rs.getString(1) != null ? rs.getString(1).trim() : "0");
				customerDataBean.setReportSerialNum(rs.getString(2) != null ? rs.getString(2).trim() : "0");
				customerDataBean.setOriginalReportSerialNum(rs.getString(3) != null ? rs.getString(3).trim() : "0");
				customerDataBean.setMainPersonName(rs.getString(4) != null ? rs.getString(4).trim() : "0");
				customerDataBean.setSourceOfAlert(rs.getString(5) != null ? rs.getString(5).trim() : "0");
				customerDataBean.setAlertIndicator1(rs.getString(6) != null ? rs.getString(6).trim() : "0");
				customerDataBean.setAlertIndicator2(rs.getString(7) != null ? rs.getString(7).trim() : "0");
				customerDataBean.setAlertIndicator3(rs.getString(8) != null ? rs.getString(8).trim() : "0");
				customerDataBean
						.setSuspicionDueToProceedsOfCrime(rs.getString(9) != null ? rs.getString(9).trim() : "0");
				customerDataBean
						.setSuspicionDueToComplexTrans(rs.getString(10) != null ? rs.getString(10).trim() : "0");
				customerDataBean
						.setSuspicionDueToNoEcoRationale(rs.getString(11) != null ? rs.getString(11).trim() : "0");
				customerDataBean
						.setSuspicionOfFinancingOfTerrorism(rs.getString(12) != null ? rs.getString(12).trim() : "0");
				customerDataBean.setAttemptedTransaction(rs.getString(13) != null ? rs.getString(13).trim() : "0");
				customerDataBean.setGroundsOfSuspicion(rs.getString(14) != null ? rs.getString(14).trim() : "0");
				customerDataBean.setDetailsOfInvestigations(rs.getString(15) != null ? rs.getString(15).trim() : "0");
				customerDataBean.setLEAInformed(rs.getString(16) != null ? rs.getString(16).trim() : "0");
				customerDataBean.setLEADetails(rs.getString(17) != null ? rs.getString(17).trim() : "0");
				customerDataBean.setPriorityRating(rs.getString(18) != null ? rs.getString(18).trim() : "0");
				customerDataBean.setReportCoverage(rs.getString(19) != null ? rs.getString(19).trim() : "0");
				customerDataBean.setAdditionalDocuments(rs.getString(20) != null ? rs.getString(20).trim() : "0");
				custList.add(customerDataBean);
			}

			rs = connection.createStatement().executeQuery("select * from aml_str_wi_tran_file_tmp");
			while (rs.next()) {
				STRWalkInTransactionDataBean transactionDataBean = new STRWalkInTransactionDataBean();
				transactionDataBean.setLineNumber(rs.getString(1) != null ? rs.getString(1).trim() : "0");
				transactionDataBean.setReportSerialNum(rs.getString(2) != null ? rs.getString(2).trim() : "0");
				transactionDataBean.setTransactionDate(rs.getString(3) != null ? rs.getString(3).trim() : "0");
				transactionDataBean.setTransactionTime(rs.getString(4) != null ? rs.getString(4).trim() : "0");
				transactionDataBean.setTransactionRefNo(rs.getString(5) != null ? rs.getString(5).trim() : "0");
				transactionDataBean.setTransactionType(rs.getString(6) != null ? rs.getString(6).trim() : "0");
				transactionDataBean.setInstrumentType(rs.getString(7) != null ? rs.getString(7).trim() : "0");
				transactionDataBean
						.setTransactionInstitutionName(rs.getString(8) != null ? rs.getString(8).trim() : "0");
				transactionDataBean
						.setTransactionInstitutionRefNum(rs.getString(9) != null ? rs.getString(9).trim() : "0");
				transactionDataBean.setTransactionStateCode(rs.getString(10) != null ? rs.getString(10).trim() : "0");
				transactionDataBean.setTransactionCountryCode(rs.getString(11) != null ? rs.getString(11).trim() : "0");
				transactionDataBean
						.setPaymentInstrumentNumber(rs.getString(12) != null ? rs.getString(12).trim() : "0");
				transactionDataBean.setPaymentInstrumentIssueInstituteName(
						rs.getString(13) != null ? rs.getString(13).trim() : "0");
				transactionDataBean
						.setInstrumentIssueInstitutionRefNum(rs.getString(14) != null ? rs.getString(14).trim() : "0");
				transactionDataBean.setInstrumentCountryCode(rs.getString(15) != null ? rs.getString(15).trim() : "0");
				transactionDataBean.setAmountRupees(rs.getString(16) != null ? rs.getString(16).trim() : "0");
				transactionDataBean.setAmountForeignCurrency(rs.getString(17) != null ? rs.getString(17).trim() : "0");
				transactionDataBean.setCurrencyOfTransaction(rs.getString(18) != null ? rs.getString(18).trim() : "0");
				transactionDataBean.setPurposeOfTransaction(rs.getString(19) != null ? rs.getString(19).trim() : "0");
				transactionDataBean.setPurposeCode(rs.getString(20) != null ? rs.getString(20).trim() : "0");
				transactionDataBean.setRiskRating(rs.getString(21) != null ? rs.getString(21).trim() : "0");
				transactionDataBean.setCustomerName(rs.getString(22) != null ? rs.getString(22).trim() : "0");
				transactionDataBean.setCustomerId(rs.getString(23) != null ? rs.getString(23).trim() : "0");
				transactionDataBean.setOccupation(rs.getString(24) != null ? rs.getString(24).trim() : "0");
				transactionDataBean.setDateOfBirth(rs.getString(25) != null ? rs.getString(25).trim() : "0");
				transactionDataBean.setGender(rs.getString(26) != null ? rs.getString(26).trim() : "0");
				transactionDataBean.setNationality(rs.getString(27) != null ? rs.getString(27).trim() : "0");
				transactionDataBean.setIdentificationType(rs.getString(28) != null ? rs.getString(28).trim() : "0");
				transactionDataBean.setIdentificationNumber(rs.getString(29) != null ? rs.getString(29).trim() : "0");
				transactionDataBean.setIssuingAuthority(rs.getString(30) != null ? rs.getString(30).trim() : "0");
				transactionDataBean.setPlaceOfIssue(rs.getString(31) != null ? rs.getString(31).trim() : "0");
				transactionDataBean.setPAN(rs.getString(32) != null ? rs.getString(32).trim() : "0");
				transactionDataBean.setUIN(rs.getString(33) != null ? rs.getString(33).trim() : "0");
				transactionDataBean.setAddress(rs.getString(34) != null ? rs.getString(34).trim() : "0");
				transactionDataBean.setCity(rs.getString(35) != null ? rs.getString(35).trim() : "0");
				transactionDataBean.setStateCode(rs.getString(36) != null ? rs.getString(36).trim() : "ZZ");
				transactionDataBean.setPinCode(rs.getString(37) != null ? rs.getString(37).trim() : "0");
				transactionDataBean.setCountryCode(rs.getString(38) != null ? rs.getString(38).trim() : "0");
				transactionDataBean.setTelephone(rs.getString(39) != null ? rs.getString(39).trim() : "0");
				transactionDataBean.setMobile(rs.getString(40) != null ? rs.getString(40).trim() : "0");
				transactionDataBean.setFax(rs.getString(41) != null ? rs.getString(41).trim() : "0");
				transactionDataBean.setEmail(rs.getString(42) != null ? rs.getString(42).trim() : "0");
				transactionDataBean.setAccountNumber(rs.getString(43) != null ? rs.getString(43).trim() : "0");
				transactionDataBean
						.setAccountWithInstitutionName(rs.getString(44) != null ? rs.getString(44).trim() : "0");
				transactionDataBean
						.setAccountWithInstitutionRefNum(rs.getString(45) != null ? rs.getString(45).trim() : "0");
				transactionDataBean.setRelatedInstitutionName(rs.getString(46) != null ? rs.getString(46).trim() : "0");
				transactionDataBean
						.setInstitutionRelationFlag(rs.getString(47) != null ? rs.getString(47).trim() : "0");
				transactionDataBean
						.setRelatedInstitutionRefNum(rs.getString(48) != null ? rs.getString(48).trim() : "0");
				transactionDataBean.setRemarks(rs.getString(49) != null ? rs.getString(49).trim() : "0");
				tranList.add(transactionDataBean);
			}
			controlDataBean.setTransactionDataBeans(tranList);

			rs = connection.createStatement().executeQuery("select * from aml_str_wi_branch_file_tmp");
			while (rs.next()) {
				STRWalkInBranchDataBean bean = new STRWalkInBranchDataBean();

				bean.setLineNumber(rs.getString(1) != null ? rs.getString(1).trim() : "0");
				bean.setInstitutionName(rs.getString(2) != null ? rs.getString(2).trim() : "0");
				bean.setInstitutionBranchName(rs.getString(3) != null ? rs.getString(3).trim() : "0");
				bean.setInstitutionRefNum(rs.getString(4) != null ? rs.getString(4).trim() : "0");
				bean.setReportingRole(rs.getString(5) != null ? rs.getString(5).trim() : "0");
				bean.setBIC(rs.getString(6) != null ? rs.getString(6).trim() : "0");
				bean.setAddress(rs.getString(7) != null ? rs.getString(7).trim() : "0");
				bean.setCity(rs.getString(8) != null ? rs.getString(8).trim() : "0");
				bean.setStateCode(rs.getString(9) != null ? rs.getString(9).trim() : "0");
				bean.setPinCode(rs.getString(10) != null ? rs.getString(10).trim() : "0");
				bean.setCountryCode(rs.getString(11) != null ? rs.getString(11).trim() : "0");
				bean.setTelephone(rs.getString(12) != null ? rs.getString(12).trim() : "0");
				bean.setMobile(rs.getString(13) != null ? rs.getString(13).trim() : "0");
				bean.setFax(rs.getString(14) != null ? rs.getString(14).trim() : "0");
				bean.setEmail(rs.getString(15) != null ? rs.getString(15).trim() : "0");
				bean.setRemarks(rs.getString(16) != null ? rs.getString(16).trim() : "0");
				branchList.add(bean);

			}

			controlDataBean.setBranchDataBeans(branchList);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return controlDataBean;
	}

	public STRConfigFileBean ctrXMLDetails(InfoLogger logger, String reportId, String reportType, String isMaster)
			throws Exception {
		logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", "Start");

		Connection connection = null;

		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
			connection.setAutoCommit(false);
		} catch (SQLException sqlExp) {
			logger.logExceptionText("GenerateCTRFiles", "LoadDatabaseAml", "Exception e:" + sqlExp);

			sqlExp.printStackTrace();
			System.err.println("Exception :" + sqlExp);
		} catch (Exception cnfExp) {
			logger.logExceptionText("GenerateCTRFiles", "LoadDatabaseAml", "Exception e:" + cnfExp);

			cnfExp.printStackTrace();
			System.err.println("Exception :" + cnfExp);
		}

		PreparedStatement statement = null, branchStmt = null, indiStmt = null, lglStmt = null, tranStmt = null;
		ResultSet rs = null, branchRs = null, indiRs = null, lglRs = null, tranRs = null;
		STRConfigFileBean bean = new STRConfigFileBean();

		String tableName = "AML_" + reportType + "_CTRL_FILE";

		if (!reportType.equalsIgnoreCase("STR")) {
			tableName = "P_" + tableName;
		}

		if (isMaster.equalsIgnoreCase("N")) {
			tableName = tableName + "_TEMP";
		}

		try {
			String sql = null;
			boolean cnt = false;

			if (reportType.equalsIgnoreCase("STR")) {

				sql = "select " + reportType + "_SEQ_NO, COMMENTS from " + tableName + " where " + reportType
						+ "_SEQ_NO = '" + reportId + "'";

				logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", " main sql is:" + sql);
				statement = connection.prepareStatement(sql);
				// statement.setString(1,reportId);
				cnt = false;

				rs = statement.executeQuery();

				if (rs != null && rs.next()) {

					logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", " inside rs.next checking. ");

					cnt = true;

					bean.setReport_id(
							rs.getString(reportType + "_SEQ_NO") != null ? rs.getString(reportType + "_SEQ_NO") : "");
					bean.setComments(rs.getString("COMMENTS") != null ? rs.getString("COMMENTS") : "");
				}

			} else {

				sql = "select " + reportType + "_SEQ_NO from " + tableName + " where " + reportType + "_SEQ_NO = '"
						+ reportId + "'";
				System.out.println(sql);
				logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", " main sql is:" + sql);
				statement = connection.prepareStatement(sql);
				// statement.setString(1,reportId);
				cnt = false;

				rs = statement.executeQuery();

				if (rs != null && rs.next()) {

					logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", " inside rs.next checking. ");

					cnt = true;

					bean.setReport_id(
							rs.getString(reportType + "_SEQ_NO") != null ? rs.getString(reportType + "_SEQ_NO") : "");
				}
			}

			logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", " control File data fetched ");
			logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", " cnt is:" + cnt);

			if (true) {

				// account
				tableName = "AML_" + reportType + "_ACCOUNT_FILE";

				if (!reportType.equalsIgnoreCase("STR")) {
					tableName = "P_" + tableName;
				}

				if (isMaster.equalsIgnoreCase("N")) {
					tableName = tableName + "_TEMP";
				}

				sql = "select RECORD_TYPE, LINE_NO, BRANCH_REF_NO, ACCOUNT_NO, FIRST_SOL_AC_HOLDER, "
						+ " TYPE_OF_AC, TYPE_OF_AC_HOLDER, AC_STATUS, DATE_OF_AC_OPENING, RISK_CATEGORY, CUMULATIVE_CR_TO, CUMULATIVE_DB_TO, "
						+ " CUMULATIVE_CASH_DEP_TO, CUMULATIVE_CASH_WD_TO, " + reportType + "_SEQ_NO from " + tableName
						+ " where " + reportType + "_SEQ_NO = '" + reportId + "'";
				logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", " Account file sql is:" + sql);
				statement = connection.prepareStatement(sql);
				rs = statement.executeQuery();

				// branch
				tableName = "AML_" + reportType + "_BRANCH_FILE";

				if (!reportType.equalsIgnoreCase("STR")) {
					tableName = "P_" + tableName;
				}

				if (isMaster.equalsIgnoreCase("N")) {
					tableName = tableName + "_TEMP";
				}

				sql = "select RECORD_TYPE, LINE_NO, NAME_OF_BRANCH, BRANCH_REF_NO, UID_FIU, "
						+ "BRANCH_ADDRESS1, BRANCH_ADDRESS2, BRANCH_ADDRESS3, BRANCH_ADDRESS4, BRANCH_ADDRESS5, BRANCH_PINCODE, BRANCH_TELEPHONE, "
						+ "BRANCH_FAX, BRANCH_EMAIL, " + reportType + "_SEQ_NO from " + tableName + " where "
						+ reportType + "_SEQ_NO = '" + reportId + "' and BRANCH_REF_NO = ?";

				branchStmt = connection.prepareStatement(sql);

				// individual
				tableName = "AML_" + reportType + "_INDIVIDUAL_FILE";

				if (!reportType.equalsIgnoreCase("STR")) {
					tableName = "P_" + tableName;
				}

				if (isMaster.equalsIgnoreCase("N")) {
					tableName = tableName + "_TEMP";
				}

				sql = "SELECT RECORD_TYPE, LINE_NO, BRANCH_REF_NO, AC_NO,RELATION_FLG, FULLNAME_INDIVIDUAL, CUST_ID_NO, "
						+ "NAME_OF_FATHER_OR_SPOUSE, OCCUPATION, DOB, SEX, NATIONALITY, TYPE_OF_ID, ID_NO, ISSUING_AUTHORITY, PLACE_OF_ISSUE,PAN, "
						+ "COMMUNICATION_ADD1, COMMUNICATION_ADD2, COMMUNICATION_ADD3, COMMUNICATION_ADD4, COMMUNICATION_ADD5, COMMUNICATION_PIN, "
						+ "CONT_TEL, CONT_MB_NO, CONT_EMAIL, PLACE_OF_WORK, SEC_ADD1, SEC_ADD2, SEC_ADD3, SEC_ADD4, SEC_ADD5, SEC_PIN, SEC_TEL, "
						+ reportType + "_SEQ_NO FROM " + tableName + " where " + reportType
						+ "_SEQ_NO = ? and AC_NO = ?";

				indiStmt = connection.prepareStatement(sql);

				// legal person
				tableName = "AML_" + reportType + "_LGLPERSIONL_FILE";

				if (!reportType.equalsIgnoreCase("STR")) {
					tableName = "P_" + tableName;
				}

				if (isMaster.equalsIgnoreCase("N")) {
					tableName = tableName + "_TEMP";
				}

				sql = "SELECT RECORD_TYPE, LINE_NO, BRANCH_REF_NO, AC_NO, nvl(RELATION_FLG, 'Z') as RELATION_FLG, LGLPER_NAME, CUST_ID_NO, "
						+ "NATURE_OF_BUSINESS, DATE_OF_INCORPORATION, TYPE_OF_CONSTI, REG_NO, "
						+ "REG_AUTHORITY, REG_PLACE,PAN, COMMUNICATION_ADD1, COMMUNICATION_ADD2, COMMUNICATION_ADD3, COMMUNICATION_ADD4, "
						+ "COMMUNICATION_ADD5, COMMUNICATION_PIN, CONT_TELEPHONE, CONT_FAX, CONT_EMAIL, REG_ADD1, REG_ADD2, REG_ADD3, "
						+ "REG_ADD4, REG_ADD5, REG_PIN, REG_OFF_TELEPHONE, REG_OFF_FAX, " + reportType + "_SEQ_NO FROM "
						+ tableName + " where " + reportType + "_SEQ_NO = ? and AC_NO = ?";

				lglStmt = connection.prepareStatement(sql);

				// transaction
				tableName = "AML_" + reportType + "_TRAN_FILE";

				if (!reportType.equalsIgnoreCase("STR")) {
					tableName = "P_" + tableName;
				}

				if (isMaster.equalsIgnoreCase("N")) {
					tableName = tableName + "_TEMP";
				}

				sql = "SELECT RECORD_TYPE, LINE_NO, BRANCH_REF_NO, AC_NO, TRAN_ID, DATE_OF_TRAN, MODE_OF_TRAN, DEB_CRE_FLG, "
						+ "AMOUNT, CURR_OF_TRAN, DISPOSITION_OF_FUNDS, REMARKS, " + reportType + "_SEQ_NO FROM "
						+ tableName + " where " + reportType + "_SEQ_NO = ? and AC_NO = ?";

				tranStmt = connection.prepareStatement(sql);
				// System.out.println("Account File :: "+sql);

				//

				ArrayList<Account> accounts = new ArrayList<Account>();
				Account account = null;

				STRAccountFileBean accountFileBean = null;
				STRBranchFileBean branchFileBean = null;

				ArrayList<STRIndividualFileBean> individualFileBeans = null;
				ArrayList<STRLPEFIleBean> lpeFileBeans = null;
				ArrayList<STRTransactionFileBean> strTransactionFileBeans = null;

				STRIndividualFileBean individualFileBean = null;
				STRLPEFIleBean lglFileBean = null;
				STRTransactionFileBean transactionFileBean = null;

				while (rs.next()) {

					account = new Account();

					accountFileBean = new STRAccountFileBean();
					individualFileBeans = new ArrayList<STRIndividualFileBean>();
					lpeFileBeans = new ArrayList<STRLPEFIleBean>();
					strTransactionFileBeans = new ArrayList<STRTransactionFileBean>();

					accountFileBean.setStraf_record_type(
							rs.getString("RECORD_TYPE") != null ? rs.getString("RECORD_TYPE") : "");
					accountFileBean.setStraf_report_date(bean.getCreatedDate());
					accountFileBean
							.setStraf_line_number(rs.getString("LINE_NO") != null ? rs.getString("LINE_NO") : "");
					accountFileBean.setStraf_branch_ref_no(
							rs.getString("BRANCH_REF_NO") != null ? rs.getString("BRANCH_REF_NO") : "");
					accountFileBean
							.setStraf_acc_number(rs.getString("ACCOUNT_NO") != null ? rs.getString("ACCOUNT_NO") : "");
					accountFileBean.setStraf_name_acc_holder(
							rs.getString("FIRST_SOL_AC_HOLDER") != null ? rs.getString("FIRST_SOL_AC_HOLDER") : "");
					accountFileBean
							.setStraf_type_of_acc(rs.getString("TYPE_OF_AC") != null ? rs.getString("TYPE_OF_AC") : "");
					accountFileBean.setStraf_type_of_accholder(
							rs.getString("TYPE_OF_AC_HOLDER") != null ? rs.getString("TYPE_OF_AC_HOLDER") : "");
					accountFileBean
							.setStraf_ac_status(rs.getString("AC_STATUS") != null ? rs.getString("AC_STATUS") : "Z");
					accountFileBean.setStraf_date_of_opening(
							rs.getString("DATE_OF_AC_OPENING") != null ? rs.getString("DATE_OF_AC_OPENING") : "");
					accountFileBean.setStraf_risk_category(
							rs.getString("RISK_CATEGORY") != null ? rs.getString("RISK_CATEGORY") : "");
					accountFileBean.setStraf_cum_cr_turnover(
							rs.getString("CUMULATIVE_CR_TO") != null ? rs.getString("CUMULATIVE_CR_TO") : "0");
					accountFileBean.setStraf_cum_dr_turnover(
							rs.getString("CUMULATIVE_DB_TO") != null ? rs.getString("CUMULATIVE_DB_TO") : "0");
					accountFileBean.setStraf_cash_dep_turnover(
							rs.getString("CUMULATIVE_CASH_DEP_TO") != null ? rs.getString("CUMULATIVE_CASH_DEP_TO")
									: "0");
					accountFileBean.setStraf_cash_wdrwl_turnover(
							rs.getString("CUMULATIVE_CASH_WD_TO") != null ? rs.getString("CUMULATIVE_CASH_WD_TO")
									: "0");
					accountFileBean.setMain_caseid(
							rs.getString(reportType + "_SEQ_NO") != null ? rs.getString(reportType + "_SEQ_NO") : "");

					account.setAccountFileBean(accountFileBean);

					/************************** branch **************************/

					branchStmt.setString(1, rs.getString("BRANCH_REF_NO"));
					branchRs = branchStmt.executeQuery();

					while (branchRs.next()) {

						branchFileBean = new STRBranchFileBean();

						branchFileBean.setStr_date(bean.getCreatedDate());
						branchFileBean.setStrbf_record_type(branchRs.getString("RECORD_TYPE"));
						branchFileBean.setStrbf_report_date(bean.getCreatedDate());
						branchFileBean.setStrbf_line_number(
								branchRs.getString("LINE_NO") != null ? branchRs.getString("LINE_NO") : "");
						branchFileBean.setStrbf_branch_name(
								branchRs.getString("NAME_OF_BRANCH") != null ? branchRs.getString("NAME_OF_BRANCH")
										: "");
						branchFileBean.setStrbf_branch_ref_no(
								branchRs.getString("BRANCH_REF_NO") != null ? branchRs.getString("BRANCH_REF_NO") : "");
						branchFileBean.setStrbf_fiu_id(
								branchRs.getString("UID_FIU") != null ? branchRs.getString("UID_FIU") : "");
						branchFileBean.setStrbf_branch_address1(
								branchRs.getString("BRANCH_ADDRESS1") != null ? branchRs.getString("BRANCH_ADDRESS1")
										: "");
						branchFileBean.setStrbf_branch_address2(
								branchRs.getString("BRANCH_ADDRESS2") != null ? branchRs.getString("BRANCH_ADDRESS2")
										: "");
						branchFileBean.setStrbf_branch_address3(
								branchRs.getString("BRANCH_ADDRESS3") != null ? branchRs.getString("BRANCH_ADDRESS3")
										: "");
						branchFileBean.setStrbf_branch_address4(
								branchRs.getString("BRANCH_ADDRESS4") != null ? branchRs.getString("BRANCH_ADDRESS4")
										: "");
						branchFileBean.setStrbf_branch_address5(
								branchRs.getString("BRANCH_ADDRESS5") != null ? branchRs.getString("BRANCH_ADDRESS5")
										: "");

						branchFileBean.setStrbf_branch_pincode(
								branchRs.getString("BRANCH_PINCODE") != null ? branchRs.getString("BRANCH_PINCODE")
										: "");
						branchFileBean.setStrbf_branch_telno(
								branchRs.getString("BRANCH_TELEPHONE") != null ? branchRs.getString("BRANCH_TELEPHONE")
										: "");
						branchFileBean.setStrbf_branch_fax(
								branchRs.getString("BRANCH_FAX") != null ? branchRs.getString("BRANCH_FAX") : "");
						branchFileBean.setStrbf_branch_email(
								branchRs.getString("BRANCH_EMAIL") != null ? branchRs.getString("BRANCH_EMAIL") : "");
						branchFileBean.setMain_caseid(branchRs.getString(reportType + "_SEQ_NO") != null
								? branchRs.getString(reportType + "_SEQ_NO")
								: "");

						account.setBranchFileBean(branchFileBean);

					}

					/************************** individual **************************/

					indiStmt.setString(1, reportId);
					indiStmt.setString(2, rs.getString("ACCOUNT_NO"));
					indiRs = indiStmt.executeQuery();

					while (indiRs.next()) {

						individualFileBean = new STRIndividualFileBean();

						individualFileBean.setStr_date(bean.getCreatedDate());
						individualFileBean.setStrif_record_type(
								indiRs.getString("RECORD_TYPE") != null ? indiRs.getString("RECORD_TYPE") : "");
						individualFileBean.setStrif_report_date(bean.getCreatedDate());
						individualFileBean.setStrif_line_number(
								indiRs.getString("LINE_NO") != null ? indiRs.getString("LINE_NO") : "");
						individualFileBean.setStrif_branch_ref_no(
								indiRs.getString("BRANCH_REF_NO") != null ? indiRs.getString("BRANCH_REF_NO") : "");
						individualFileBean.setStrif_acc_number(
								indiRs.getString("AC_NO") != null ? indiRs.getString("AC_NO") : "");
						individualFileBean.setStrif_relation_flag(
								indiRs.getString("RELATION_FLG") != null ? indiRs.getString("RELATION_FLG") : "A");
						individualFileBean.setStrif_full_name_ind(indiRs.getString("FULLNAME_INDIVIDUAL") != null
								? indiRs.getString("FULLNAME_INDIVIDUAL")
								: "");
						individualFileBean.setStrif_customer_id(
								indiRs.getString("CUST_ID_NO") != null ? indiRs.getString("CUST_ID_NO") : "");
						individualFileBean.setStrif_father_name(indiRs.getString("NAME_OF_FATHER_OR_SPOUSE") != null
								? indiRs.getString("NAME_OF_FATHER_OR_SPOUSE")
								: "");
						individualFileBean.setStrif_occupation(
								indiRs.getString("OCCUPATION") != null ? indiRs.getString("OCCUPATION") : "");
						individualFileBean.setStrif_dob(indiRs.getString("DOB") != null ? indiRs.getString("DOB") : "");
						individualFileBean.setStrif_sex(indiRs.getString("SEX") != null ? indiRs.getString("SEX") : "");
						individualFileBean
								.setStrif_nationality(indiRs.getString("NATIONALITY") != null
										? (indiRs.getString("NATIONALITY").trim().equalsIgnoreCase("") ? "ZZ"
												: indiRs.getString("NATIONALITY"))
										: "ZZ");
						individualFileBean.setStrif_identification_type(
								indiRs.getString("TYPE_OF_ID") != null ? indiRs.getString("TYPE_OF_ID") : "");
						individualFileBean.setStrif_identification_num(
								indiRs.getString("ID_NO") != null ? indiRs.getString("ID_NO") : "");
						individualFileBean.setStrif_issue_authority(
								indiRs.getString("ISSUING_AUTHORITY") != null ? indiRs.getString("ISSUING_AUTHORITY")
										: "");
						individualFileBean.setStrif_issue_place(
								indiRs.getString("PLACE_OF_ISSUE") != null ? indiRs.getString("PLACE_OF_ISSUE") : "");
						individualFileBean.setStrif_pan(indiRs.getString("PAN") != null ? indiRs.getString("PAN") : "");
						individualFileBean.setStrif_comm_address1(
								indiRs.getString("COMMUNICATION_ADD1") != null ? indiRs.getString("COMMUNICATION_ADD1")
										: "");
						individualFileBean.setStrif_comm_address2(
								indiRs.getString("COMMUNICATION_ADD2") != null ? indiRs.getString("COMMUNICATION_ADD2")
										: "");

						individualFileBean.setStrif_comm_address3(
								indiRs.getString("COMMUNICATION_ADD3") != null ? indiRs.getString("COMMUNICATION_ADD3")
										: "");
						individualFileBean.setStrif_comm_address4(
								indiRs.getString("COMMUNICATION_ADD4") != null ? indiRs.getString("COMMUNICATION_ADD4")
										: "");
						individualFileBean.setStrif_comm_address5(indiRs.getString("COMMUNICATION_ADD5") != null
								? (indiRs.getString("COMMUNICATION_ADD5").equalsIgnoreCase("XXX") ? "XX"
										: indiRs.getString("COMMUNICATION_ADD5"))
								: "ZZ");

						individualFileBean.setStrif_comm_pincode(
								indiRs.getString("COMMUNICATION_PIN") != null ? indiRs.getString("COMMUNICATION_PIN")
										: "");
						individualFileBean.setStrif_contact_telno(
								indiRs.getString("CONT_TEL") != null ? indiRs.getString("CONT_TEL") : "");
						individualFileBean.setStrif_contact_mobile(
								indiRs.getString("CONT_MB_NO") != null ? indiRs.getString("CONT_MB_NO") : "");
						individualFileBean.setStrif_contact_email(
								indiRs.getString("CONT_EMAIL") != null ? indiRs.getString("CONT_EMAIL") : "");
						individualFileBean.setStrif_work_place(
								indiRs.getString("PLACE_OF_WORK") != null ? indiRs.getString("PLACE_OF_WORK") : "");
						individualFileBean.setStrif_second_address1(
								indiRs.getString("SEC_ADD1") != null ? indiRs.getString("SEC_ADD1") : "");
						individualFileBean.setStrif_second_address2(
								indiRs.getString("SEC_ADD2") != null ? indiRs.getString("SEC_ADD2") : "");
						individualFileBean.setStrif_second_address3(
								indiRs.getString("SEC_ADD3") != null ? indiRs.getString("SEC_ADD3") : "");
						individualFileBean.setStrif_second_address4(
								indiRs.getString("SEC_ADD4") != null ? indiRs.getString("SEC_ADD4") : "");
						individualFileBean.setStrif_second_address5(
								indiRs.getString("SEC_ADD5") != null ? indiRs.getString("SEC_ADD5") : "");
						individualFileBean.setStrif_second_pincode(
								indiRs.getString("SEC_PIN") != null ? indiRs.getString("SEC_PIN") : "");
						individualFileBean.setStrif_second_telno(
								indiRs.getString("SEC_TEL") != null ? indiRs.getString("SEC_TEL") : "");
						individualFileBean.setMain_caseid(indiRs.getString(reportType + "_SEQ_NO") != null
								? indiRs.getString(reportType + "_SEQ_NO")
								: "");

						individualFileBeans.add(individualFileBean);
					}

					account.setIndividualFileBeans(individualFileBeans);

					/************************** legal person **************************/

					lglStmt.setString(1, reportId);
					lglStmt.setString(2, rs.getString("ACCOUNT_NO"));
					lglRs = lglStmt.executeQuery();

					while (lglRs.next()) {

						lglFileBean = new STRLPEFIleBean();
						lglFileBean.setStr_date(bean.getCreatedDate());
						lglFileBean.setStrlf_record_type(
								lglRs.getString("RECORD_TYPE") != null ? lglRs.getString("RECORD_TYPE") : "");
						lglFileBean.setStrlf_report_date(bean.getCreatedDate());
						lglFileBean.setStrlf_line_number(
								lglRs.getString("LINE_NO") != null ? lglRs.getString("LINE_NO") : "");
						lglFileBean.setStrlf_branch_ref_no(
								lglRs.getString("BRANCH_REF_NO") != null ? lglRs.getString("BRANCH_REF_NO") : "");
						lglFileBean
								.setStrlf_acc_number(lglRs.getString("AC_NO") != null ? lglRs.getString("AC_NO") : "");
						lglFileBean.setStrlf_relation_flag(lglRs.getString("RELATION_FLG"));
						lglFileBean.setStrlf_full_name_lpe(
								lglRs.getString("LGLPER_NAME") != null ? lglRs.getString("LGLPER_NAME") : "");
						lglFileBean.setStrlf_customer_id(
								lglRs.getString("CUST_ID_NO") != null ? lglRs.getString("CUST_ID_NO") : "");
						lglFileBean.setStrlf_nature_of_business(
								lglRs.getString("NATURE_OF_BUSINESS") != null ? lglRs.getString("NATURE_OF_BUSINESS")
										: "");
						lglFileBean.setStrlf_incorporation_date(lglRs.getString("DATE_OF_INCORPORATION") != null
								? lglRs.getString("DATE_OF_INCORPORATION")
								: "");
						lglFileBean.setStrlf_constitution_type(
								lglRs.getString("TYPE_OF_CONSTI") != null ? lglRs.getString("TYPE_OF_CONSTI") : "");
						lglFileBean
								.setStrlf_regn_num(lglRs.getString("REG_NO") != null ? lglRs.getString("REG_NO") : "");
						lglFileBean.setStrlf_regn_authority(
								lglRs.getString("REG_AUTHORITY") != null ? lglRs.getString("REG_AUTHORITY") : "");
						lglFileBean.setStrlf_regn_place(
								lglRs.getString("REG_PLACE") != null ? lglRs.getString("REG_PLACE") : "");
						lglFileBean.setStrlf_pan(lglRs.getString("PAN") != null ? lglRs.getString("PAN") : "");

						lglFileBean.setStrlf_comm_address1(
								lglRs.getString("COMMUNICATION_ADD1") != null ? lglRs.getString("COMMUNICATION_ADD1")
										: "");
						lglFileBean.setStrlf_comm_address2(
								lglRs.getString("COMMUNICATION_ADD2") != null ? lglRs.getString("COMMUNICATION_ADD2")
										: "");
						lglFileBean.setStrlf_comm_address3(
								lglRs.getString("COMMUNICATION_ADD3") != null ? lglRs.getString("COMMUNICATION_ADD3")
										: "");
						lglFileBean.setStrlf_comm_address4(
								lglRs.getString("COMMUNICATION_ADD4") != null ? lglRs.getString("COMMUNICATION_ADD4")
										: "");
						lglFileBean
								.setStrlf_comm_address5(lglRs.getString("COMMUNICATION_ADD5") != null
										? (lglRs.getString("COMMUNICATION_ADD5").trim().equalsIgnoreCase("XXX") ? "XX"
												: lglRs.getString("COMMUNICATION_ADD5").trim())
										: "ZZ");

						lglFileBean.setStrlf_comm_pincode(
								lglRs.getString("COMMUNICATION_PIN") != null ? lglRs.getString("COMMUNICATION_PIN")
										: "");
						lglFileBean.setStrlf_contact_telno(
								lglRs.getString("CONT_TELEPHONE") != null ? lglRs.getString("CONT_TELEPHONE") : "");
						lglFileBean.setStrlf_contact_fax(
								lglRs.getString("CONT_FAX") != null ? lglRs.getString("CONT_FAX") : "");
						lglFileBean.setStrlf_contact_email(
								lglRs.getString("CONT_EMAIL") != null ? lglRs.getString("CONT_EMAIL") : "");
						lglFileBean.setStrlf_second_address1(
								lglRs.getString("REG_ADD1") != null ? lglRs.getString("REG_ADD1") : "");
						lglFileBean.setStrlf_second_address2(
								lglRs.getString("REG_ADD2") != null ? lglRs.getString("REG_ADD2") : "");
						lglFileBean.setStrlf_second_address3(
								lglRs.getString("REG_ADD3") != null ? lglRs.getString("REG_ADD3") : "");
						lglFileBean.setStrlf_second_address4(
								lglRs.getString("REG_ADD4") != null ? lglRs.getString("REG_ADD4") : "");
						lglFileBean.setStrlf_second_address5(
								lglRs.getString("REG_ADD5") != null ? lglRs.getString("REG_ADD5") : "");
						lglFileBean.setStrlf_second_pincode(
								lglRs.getString("REG_PIN") != null ? lglRs.getString("REG_PIN") : "");
						lglFileBean.setStrlf_second_telno(
								lglRs.getString("REG_OFF_TELEPHONE") != null ? lglRs.getString("REG_OFF_TELEPHONE")
										: "");
						lglFileBean.setStrlf_second_fax(
								lglRs.getString("REG_OFF_FAX") != null ? lglRs.getString("REG_OFF_FAX") : "");
						lglFileBean.setMain_caseid(lglRs.getString(reportType + "_SEQ_NO") != null
								? lglRs.getString(reportType + "_SEQ_NO")
								: "");
						lglFileBean.setActual_flag("");// TODO

						lpeFileBeans.add(lglFileBean);

					}

					account.setStrlpefIleBeans(lpeFileBeans);

					/************************** transaction **************************/

					// System.out.println("Tran File reportId "+ reportId);
					String taccount = rs.getString("ACCOUNT_NO") != null ? rs.getString("ACCOUNT_NO") : "";
					// System.out.println("Tran File reportId "+ taccount);

					tranStmt.setString(1, reportId);
					tranStmt.setString(2, taccount);
					tranRs = tranStmt.executeQuery();

					while (tranRs.next()) {

						transactionFileBean = new STRTransactionFileBean();

						transactionFileBean.setStr_date(bean.getCreatedDate());
						transactionFileBean.setStrtf_record_type(
								tranRs.getString("RECORD_TYPE") != null ? tranRs.getString("RECORD_TYPE") : "");
						transactionFileBean.setStrtf_line_number(
								tranRs.getString("LINE_NO") != null ? tranRs.getString("LINE_NO") : "");
						transactionFileBean.setStrtf_branch_ref_no(
								tranRs.getString("BRANCH_REF_NO") != null ? tranRs.getString("BRANCH_REF_NO") : "");
						transactionFileBean.setStrtf_acc_number(
								tranRs.getString("AC_NO") != null ? tranRs.getString("AC_NO") : "");
						transactionFileBean.setStrtf_tran_id(
								tranRs.getString("TRAN_ID") != null ? tranRs.getString("TRAN_ID") : "");
						transactionFileBean.setStrtf_tran_date(
								tranRs.getString("DATE_OF_TRAN") != null ? tranRs.getString("DATE_OF_TRAN") : "");
						transactionFileBean.setStrtf_tran_mode(
								tranRs.getString("MODE_OF_TRAN") != null ? tranRs.getString("MODE_OF_TRAN") : "Z");
						transactionFileBean.setStrtf_dr_cr(
								tranRs.getString("DEB_CRE_FLG") != null ? tranRs.getString("DEB_CRE_FLG") : "");

						transactionFileBean.setStrtf_amount(
								tranRs.getString("AMOUNT") != null && !tranRs.getString("AMOUNT").equalsIgnoreCase("")
										? (tranRs.getString("AMOUNT").contains(".")
												? tranRs.getString("AMOUNT").substring(0,
														tranRs.getString("AMOUNT").indexOf("."))
												: tranRs.getString("AMOUNT"))
										: "0");

						transactionFileBean.setStrtf_tran_currency(
								tranRs.getString("CURR_OF_TRAN") != null ? tranRs.getString("CURR_OF_TRAN") : "");
						transactionFileBean.setStrtf_funds_disposition(tranRs.getString("DISPOSITION_OF_FUNDS") != null
								? tranRs.getString("DISPOSITION_OF_FUNDS")
								: "");
						transactionFileBean.setStrtf_remarks(
								tranRs.getString("REMARKS") != null ? tranRs.getString("REMARKS") : "");
						transactionFileBean.setMain_caseid(tranRs.getString(reportType + "_SEQ_NO") != null
								? tranRs.getString(reportType + "_SEQ_NO")
								: "");

						strTransactionFileBeans.add(transactionFileBean);

					}

					account.setTransactionFileBeans(strTransactionFileBeans);

					accounts.add(account);
				}

				bean.setAccounts(accounts);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", "Exception" + e);
			throw new Exception(e);
		} finally {
			try {

				if (tranRs != null) {
					tranRs.close();
					tranRs = null;
				}

				if (lglRs != null) {
					lglRs.close();
					lglRs = null;
				}

				if (indiRs != null) {
					indiRs.close();
					indiRs = null;
				}

				if (branchRs != null) {
					branchRs.close();
					branchRs = null;
				}

				if (rs != null) {
					rs.close();
					rs = null;
				}

				if (statement != null) {
					statement.close();
					statement = null;
				}

				if (branchStmt != null) {
					branchStmt.close();
					branchStmt = null;
				}

				if (indiStmt != null) {
					indiStmt.close();
					indiStmt = null;
				}

				if (lglStmt != null) {
					lglStmt.close();
					lglStmt = null;
				}

				if (tranStmt != null) {
					tranStmt.close();
					tranStmt = null;
				}

				if (connection != null) {
					connection.close();
					connection = null;
				}

			} catch (Exception e) {
				logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", "Finally Exception" + e);
				e.printStackTrace();
			}
		}

		logger.logVerboseText("AMLUserDAO", "ctrXMLDetails", " END ");

		return bean;

	}// method end : ctrXMLDetails

	// public static String ctrXMLDetailsNew(String reportId, InfoLogger logger)
	// throws Exception {
	// String tryString = "";
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew", " Start ");
	// Account account = null;
	// AccountDetails acDetails = null;
	// List<PersonDetails> listPersonDet = null;
	// PersonDetails personDetails = null;
	// ProductTransaction productTransaction = null;
	// Transaction transactionTemp = null;
	// Branch branch = null;
	// BranchDetails branchDetails = null;
	// Individual indi = null;
	// LegalPerson legalPerson = null;
	// List<Transaction> listTransaction = null;
	// List<Account> listAccount = new ArrayList<Account>();
	// // List<Branch> branchList=null;
	// ArrayList<STRAccountFileBean> strAccountFileBeans = new
	// ArrayList<STRAccountFileBean>();
	// String createdDate = "";
	// Address addressTemp = null;
	// Connection connection = null;
	// try {
	// connection = DAOFactory.makeConnectionAMLLive();
	// connection.setAutoCommit(false);
	// // Class.forName("oracle.jdbc.driver.OracleDriver");
	// // String conn = bundle_aml.getString("aml_conn");
	// // String ip = bundle_aml.getString("aml_ip");
	// // String port = bundle_aml.getString("aml_port");
	// // String dbname = bundle_aml.getString("aml_dbname");
	// // String username = bundle_aml.getString("aml_username");
	// // String password = bundle_aml.getString("aml_password");
	// // connection = DriverManager.getConnection(conn + ip + port +
	// // dbname,
	// // username, password);
	// // System.out.println("connection :"+ connection);
	// // connection.setAutoCommit(false);
	// } catch (SQLException sqlExp) {
	// logger.logExceptionText("AMLUserDAO", "ctrXMLDetailsNew", "Exception e:"
	// +
	// sqlExp);
	//
	//
	// } catch (Exception cnfExp) {
	// logger.logExceptionText("AMLUserDAO", "ctrXMLDetailsNew", "Exception e:"
	// +
	// cnfExp);
	//
	//
	// }
	//
	// PreparedStatement statement = null;
	// ResultSet resultSet = null;
	//
	// XMLGregorianCalendar calenderTemp = null;
	// try {
	//
	// ResourceBundle rb = ResourceBundle.getBundle("AMLProp");
	// // String NAMEOFBANK = rb.getString("NameOfBank");
	//
	// ObjectFactory factory = new ObjectFactory();
	// Batch batch = factory.createBatch();
	// batch.setReportType("CTR");
	// batch.setReportFormatType("ARF");
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew", "1");
	// PrincipalOfficer principleOfficer = factory.createPrincipalOfficer();
	// Address assistantPrincipleOfficer = new Address();
	//
	// assistantPrincipleOfficer.setAddress(rb.getString("Address"));
	// assistantPrincipleOfficer.setCity(rb.getString("City"));
	// assistantPrincipleOfficer.setPinCode("Pin");
	// assistantPrincipleOfficer.setCountryCode(CountryCode.valueOf("IN"));
	// assistantPrincipleOfficer.setStateCode(StateCode.valueOf("MH"));
	//
	// principleOfficer.setPODesignation(rb.getString("Designation"));
	// principleOfficer.setPOEmail(rb.getString("NameOfBank"));
	// principleOfficer.setPOName(rb.getString("NameOfPrincipalOfficer"));
	// Phone phPo = new Phone();
	// phPo.setFax(rb.getString("Fax"));
	// phPo.setMobile(rb.getString("Tel"));
	// phPo.setTelephone(rb.getString("Tel"));
	// principleOfficer.setPOPhone(phPo);
	// principleOfficer.setPOAddress(assistantPrincipleOfficer);
	// batch.setPrincipalOfficer(principleOfficer);
	//
	// Report report = factory.createBatchReport();
	//
	// // Marshaller and other parameters setted
	// JAXBContext context = JAXBContext.newInstance("com.idbi.intech.aml.STR");
	//
	// Marshaller marshaller = context.createMarshaller();
	// marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
	// String ctrDirectory = rb.getString("CTRDIR");
	// File newXml = new File(ctrDirectory + "//" + reportId + ".xml");
	//
	// BufferedWriter output = new BufferedWriter(new FileWriter(newXml));
	//
	// report.setMainPersonName("");// TODO setting main person name.
	// report.setReportSerialNum(new BigInteger(reportId));
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew", "2");
	//
	// // I found No comments as STR control file holds comments.
	// // Fetching data from control file.
	//
	// String sql =
	// "select CREATED_DATE from P_AML_CTR_CTRL_FILE where CTR_SEQ_NO=?";
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew", " main sql is:" +
	// sql);
	// statement = connection.prepareStatement(sql);
	// statement.setString(1, reportId);
	// resultSet = statement.executeQuery();
	// while (resultSet.next()) {
	// createdDate = resultSet.getString("CREATED_DATE") != null ?
	// resultSet.getString("CREATED_DATE") : "";
	// }
	// if (resultSet != null) {
	// resultSet.close();
	// resultSet = null;
	// }
	// if (statement != null) {
	// statement.close();
	// statement = null;
	// }
	//
	// // Setting all prepare statements for report generation.....
	// PreparedStatement branchStatement = null;
	// sql =
	// "select RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD, LINE_NO,
	// NAME_OF_BRANCH, BRANCH_REF_NO,UID_FIU, "
	// +
	// "BRANCH_ADDRESS1, BRANCH_ADDRESS2,BRANCH_ADDRESS3, BRANCH_ADDRESS4,
	// BRANCH_ADDRESS5,BRANCH_PINCODE, BRANCH_TELEPHONE,"
	// +
	// " BRANCH_FAX,BRANCH_EMAIL, CTR_SEQ_NO from P_AML_CTR_BRANCH_FILE where
	// CTR_SEQ_NO=? and BRANCH_REF_NO=?";
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetails",
	// " branch file sql is:" +
	// sql);
	// branchStatement = connection.prepareStatement(sql);
	//
	// PreparedStatement statAdd3 = null;
	// String sqlAdd3 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '01' and
	// REF_DESC=?";
	// statAdd3 = connection.prepareStatement(sqlAdd3);
	// ResultSet rsAdd3 = null;
	//
	// PreparedStatement statAdd4 = null;
	// String sqlAdd4 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '02' and
	// REF_DESC=?";
	// statAdd4 = connection.prepareStatement(sqlAdd4);
	// ResultSet rsAdd4 = null;
	//
	// PreparedStatement statAdd5 = null;
	// String sqlAdd5 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '03' and
	// REF_DESC=?";
	// statAdd5 = connection.prepareStatement(sqlAdd5);
	// ResultSet rsAdd5 = null;
	//
	// // For individual....
	// sql =
	// "SELECT RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD,LINE_NO, BRANCH_REF_NO,
	// AC_NO,RELATION_FLG, FULLNAME_INDIVIDUAL, CUST_ID_NO,"
	// +
	// " NAME_OF_FATHER_OR_SPOUSE, OCCUPATION, DOB,SEX, NATIONALITY,
	// TYPE_OF_ID,ID_NO,ISSUING_AUTHORITY, PLACE_OF_ISSUE,PAN,"
	// +
	// " COMMUNICATION_ADD1, COMMUNICATION_ADD2,COMMUNICATION_ADD3,
	// COMMUNICATION_ADD4, COMMUNICATION_ADD5,COMMUNICATION_PIN, "
	// +
	// " CONT_TEL, CONT_MB_NO,CONT_EMAIL, PLACE_OF_WORK, SEC_ADD1,SEC_ADD2,
	// SEC_ADD3, SEC_ADD4,SEC_ADD5, SEC_PIN, SEC_TEL,CTR_SEQ_NO "
	// + " FROM P_AML_CTR_INDIVIDUAL_FILE where CTR_SEQ_NO =? AND AC_NO=?";
	//
	// PreparedStatement individualStatement = null;
	// individualStatement = connection.prepareStatement(sql);
	//
	// // For legal person
	//
	// sql =
	// "SELECT RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD,LINE_NO, BRANCH_REF_NO,
	// AC_NO, "
	// +
	// " RELATION_FLG, LGLPER_NAME, CUST_ID_NO,NATURE_OF_BUSINESS,
	// DATE_OF_INCORPORATION, TYPE_OF_CONSTI, REG_NO, "
	// +
	// " REG_AUTHORITY, REG_PLACE,PAN, COMMUNICATION_ADD1,
	// COMMUNICATION_ADD2,COMMUNICATION_ADD3, COMMUNICATION_ADD4, "
	// +
	// " COMMUNICATION_ADD5,COMMUNICATION_PIN, CONT_TELEPHONE, CONT_FAX,CONT_EMAIL,
	// REG_ADD1, REG_ADD2, REG_ADD3, "
	// +
	// " REG_ADD4, REG_ADD5,REG_PIN, REG_OFF_TELEPHONE, REG_OFF_FAX,CTR_SEQ_NO FROM
	// "
	// + " P_AML_CTR_LGLPERSIONL_FILE where CTR_SEQ_NO=? AND AC_NO=?";
	//
	// PreparedStatement legalStatement = null;
	// legalStatement = connection.prepareStatement(sql);
	//
	// // For transactions
	//
	// sql =
	// "SELECT RECORD_TYPE, LINE_NO, BRANCH_REF_NO, AC_NO, TRAN_ID, DATE_OF_TRAN,
	// MODE_OF_TRAN, DEB_CRE_FLG,"
	// +
	// " round(AMOUNT) as AMOUNT,CURR_OF_TRAN, DISPOSITION_OF_FUNDS, REMARKS,
	// CTR_SEQ_NO FROM P_AML_CTR_TRAN_FILE where "
	// + " CTR_SEQ_NO = ? and AC_NO=?";
	// PreparedStatement transactionStatement = null;
	// transactionStatement = connection.prepareStatement(sql);
	//
	// // Setting all prepare statements for report generation end.....
	//
	// sql =
	// "select RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD, LINE_NO, BRANCH_REF_NO,
	// ACCOUNT_NO, FIRST_SOL_AC_HOLDER,"
	// +
	// " TYPE_OF_AC, TYPE_OF_AC_HOLDER,DATE_OF_AC_OPENING, RISK_CATEGORY,
	// CUMULATIVE_CR_TO, CUMULATIVE_DB_TO,"
	// +
	// " CUMULATIVE_CASH_DEP_TO, CUMULATIVE_CASH_WD_TO, CTR_SEQ_NO from
	// P_AML_CTR_ACCOUNT_FILE where CTR_SEQ_NO='"
	// + reportId + "'";
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew",
	// " Account file sql is:" + sql);
	//
	// ResultSet rSet = connection.createStatement().executeQuery(sql);
	//
	// while (rSet != null && rSet.next()) {
	//
	// account = factory.createBatchReportAccount();
	// acDetails = factory.createBatchReportAccountAccountDetails();
	// acDetails.setAccountNumber(rSet.getString("ACCOUNT_NO") != null ?
	// rSet.getString("ACCOUNT_NO") : "");
	// //
	// acDetails.setAccountType(AccountType.valueOf(rSet.getString("TYPE_OF_AC")
	// // != null ? rSet
	// // .getString("TYPE_OF_AC") : ""));
	// acDetails.setHolderName(rSet.getString("TYPE_OF_AC_HOLDER") != null ?
	// rSet.getString("TYPE_OF_AC_HOLDER") : "");
	// acDetails.setAccountHolderType(AccountHolderType.valueOf(rSet.getString("TYPE_OF_AC")
	// != null ? rSet.getString("TYPE_OF_AC") : ""));
	// String dateOfOpening = rSet.getString("DATE_OF_AC_OPENING") != null ?
	// rSet.getString("DATE_OF_AC_OPENING") : "";
	//
	// String opnDate[] = { (new String(dateOfOpening.substring(0, 2))), (new
	// String(dateOfOpening.substring(2, 4))),
	// (new String(dateOfOpening.substring(4, 8))) };
	// XMLGregorianCalendar calender1 =
	// DatatypeFactory.newInstance().newXMLGregorianCalendar(
	// new GregorianCalendar(Integer.parseInt(opnDate[0]),
	// Integer.parseInt(opnDate[1]), Integer.parseInt(opnDate[2])));
	// acDetails.setDateOfOpening(calender1);
	// String riskcat = rSet.getString("RISK_CATEGORY") != null ?
	// rSet.getString("RISK_CATEGORY") : "";
	// String tempBranchRefNo = rSet.getString("BRANCH_REF_NO") != null ?
	// rSet.getString("BRANCH_REF_NO") : "";
	//
	// String riskRate = riskcat.equals("A") ? "A_1" : riskcat.equals("B") ?
	// "A_2" : "A_3";
	//
	// acDetails.setRiskRating(AccountRiskRating.valueOf(riskRate));
	// acDetails.setCumulativeCreditTurnover(new
	// BigInteger(rSet.getString("CUMULATIVE_CR_TO") != null ?
	// rSet.getString("CUMULATIVE_CR_TO")
	// : ""));
	// acDetails.setCumulativeDebitTurnover(new
	// BigInteger(rSet.getString("CUMULATIVE_DB_TO") != null ?
	// rSet.getString("CUMULATIVE_DB_TO")
	// : ""));
	// acDetails.setCumulativeCashDepositTurnover(new
	// BigInteger(rSet.getString("CUMULATIVE_CASH_DEP_TO") != null ? rSet
	// .getString("CUMULATIVE_CASH_DEP_TO") : ""));
	//
	// acDetails.setCumulativeCashWithdrawalTurnover(new
	// BigInteger(rSet.getString("CUMULATIVE_CASH_WD_TO") != null ? rSet
	// .getString("CUMULATIVE_CASH_WD_TO") : ""));
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew",
	// " Account File data fetched ");
	//
	// /*********************************** Branch Data Fetched
	// ******************************************************/
	//
	// branchStatement.setString(1, reportId);
	// branchStatement.setString(2, tempBranchRefNo);
	// resultSet = branchStatement.executeQuery();
	//
	// while (resultSet.next()) {
	// branch = factory.createBatchReportAccountBranch();
	// branchDetails = new BranchDetails();
	// branch.setBranchRefNum((resultSet.getString("BRANCH_REF_NO") != null ?
	// resultSet.getString("BRANCH_REF_NO") : ""));
	// addressTemp = new Address();
	// String add1 = resultSet.getString("BRANCH_ADDRESS1") != null ?
	// resultSet.getString("BRANCH_ADDRESS1") : "";
	// String add2 = resultSet.getString("BRANCH_ADDRESS2") != null ?
	// resultSet.getString("BRANCH_ADDRESS2") : "";
	//
	// String add3 = "";
	// statAdd3.setString(1, (resultSet.getString("BRANCH_ADDRESS3") != null ?
	// resultSet.getString("BRANCH_ADDRESS3") : ""));
	// rsAdd3 = statAdd3.executeQuery();
	// while (rsAdd3.next()) {
	// add3 = rsAdd3.getString(1);
	// }
	// if (rsAdd3 != null) {
	// rsAdd3.close();
	// rsAdd3 = null;
	// }
	//
	// String add4 = "";
	//
	// statAdd4.setString(1, (resultSet.getString("BRANCH_ADDRESS4") != null ?
	// resultSet.getString("BRANCH_ADDRESS4") : ""));
	// rsAdd4 = statAdd4.executeQuery();
	// while (rsAdd4.next()) {
	// add4 = rsAdd4.getString(1);
	// }
	// if (rsAdd4 != null) {
	// rsAdd4.close();
	// rsAdd4 = null;
	// }
	//
	// String add5 = "";
	//
	// statAdd5.setString(1, (resultSet.getString("BRANCH_ADDRESS5") != null ?
	// resultSet.getString("BRANCH_ADDRESS5") : ""));
	// rsAdd5 = statAdd5.executeQuery();
	// while (rsAdd5.next()) {
	// add5 = rsAdd5.getString(1);
	// }
	// if (rsAdd5 != null) {
	// rsAdd5.close();
	// rsAdd5 = null;
	// }
	//
	// addressTemp.setAddress(add1 + " " + add2);
	// addressTemp.setCity(add3);
	// // addressTemp.setStateCode(StateCode.valueOf(add4));
	// // addressTemp.setCountryCode(CountryCode.valueOf(add5));
	// addressTemp.setPinCode(resultSet.getString("BRANCH_PINCODE") != null ?
	// resultSet.getString("BRANCH_PINCODE") : "");
	// branchDetails.setBranchAddress(addressTemp);
	// branchDetails.setBranchName(resultSet.getString("NAME_OF_BRANCH") != null
	// ? resultSet.getString("NAME_OF_BRANCH") : "");
	// branch.setBranchDetails(branchDetails);
	//
	// account.setBranch(branch);
	// addressTemp = null;
	// branch = null;
	// branchDetails = null;
	// }
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetails",
	// " Branch File data fetched ");
	//
	// /******************************************************************************************/
	//
	// /********************************************* Individual and legal
	// person files... **************************************************/
	// listPersonDet = new ArrayList<PersonDetails>();
	//
	// individualStatement.setString(1, reportId);
	// individualStatement.setString(2, acDetails.getAccountNumber());
	// if (resultSet != null) {
	// resultSet.close();
	// resultSet = null;
	// }
	// resultSet = individualStatement.executeQuery();
	//
	// while (resultSet.next()) {
	//
	// personDetails = factory.createBatchReportAccountPersonDetails();
	// personDetails.setPersonName(resultSet.getString("FULLNAME_INDIVIDUAL") !=
	// null ? resultSet.getString("FULLNAME_INDIVIDUAL") : "");
	// personDetails.setCustomerId(resultSet.getString("CUST_ID_NO") != null ?
	// resultSet.getString("CUST_ID_NO") : "");
	// //
	// personDetails.setRelationFlag(RelationalFlag.valueOf(resultSet.getString("RELATION_FLG")
	// // != null
	// // ? resultSet.getString("RELATION_FLG") : "Z"));
	//
	// String add1 = "";
	// String add2 = "";
	// String add3 = "";
	// String add4 = "";
	// String add5 = "";
	// add1 = resultSet.getString("COMMUNICATION_ADD1") != null ?
	// resultSet.getString("COMMUNICATION_ADD1") : "";
	// add2 = resultSet.getString("COMMUNICATION_ADD2") != null ?
	// resultSet.getString("COMMUNICATION_ADD2") : "";
	//
	// statAdd3.setString(1, (resultSet.getString("COMMUNICATION_ADD3") != null
	// ? resultSet.getString("COMMUNICATION_ADD3") : ""));
	// rsAdd3 = statAdd3.executeQuery();
	// while (rsAdd3.next()) {
	// add3 = rsAdd3.getString(1);
	// }
	// if (rsAdd3 != null) {
	// rsAdd3.close();
	// rsAdd3 = null;
	// }
	//
	// statAdd4.setString(1, (resultSet.getString("COMMUNICATION_ADD4") != null
	// ? resultSet.getString("COMMUNICATION_ADD4") : ""));
	// rsAdd4 = statAdd4.executeQuery();
	// while (rsAdd4.next()) {
	// add4 = rsAdd4.getString(1);
	// }
	// if (rsAdd4 != null) {
	// rsAdd4.close();
	// rsAdd4 = null;
	// }
	//
	// statAdd5.setString(1, (resultSet.getString("COMMUNICATION_ADD5") != null
	// ? resultSet.getString("COMMUNICATION_ADD5") : ""));
	// rsAdd5 = statAdd4.executeQuery();
	// while (rsAdd5.next()) {
	// add5 = rsAdd5.getString(1);
	// }
	// if (rsAdd5 != null) {
	// rsAdd5.close();
	// rsAdd5 = null;
	// }
	//
	// /***/
	//
	// addressTemp = new Address();
	// addressTemp.setAddress(add1 + " " + add2);
	// addressTemp.setCity(add3);
	// // addressTemp.setStateCode(StateCode.valueOf(add4));
	// // addressTemp.setCountryCode(CountryCode.valueOf(add5));
	// addressTemp.setPinCode(resultSet.getString("COMMUNICATION_PIN") != null ?
	// resultSet.getString("COMMUNICATION_PIN") : "");
	//
	// personDetails.setCommunicationAddress(addressTemp);
	// addressTemp = null;
	// Phone phn = new Phone();
	// phn.setMobile(resultSet.getString("CONT_MB_NO") != null ?
	// resultSet.getString("CONT_MB_NO") : "");
	// phn.setTelephone(resultSet.getString("CONT_TEL") != null ?
	// resultSet.getString("CONT_TEL") : "");
	// personDetails.setPhone(phn);
	// personDetails.setEmail(resultSet.getString("CONT_EMAIL") != null ?
	// resultSet.getString("CONT_EMAIL") : "");
	//
	// add1 = resultSet.getString("SEC_ADD1") != null ?
	// resultSet.getString("SEC_ADD1") : "";
	// add2 = resultSet.getString("SEC_ADD2") != null ?
	// resultSet.getString("SEC_ADD2") : "";
	//
	// statAdd3.setString(1, (resultSet.getString("SEC_ADD3") != null ?
	// resultSet.getString("SEC_ADD3") : ""));
	// rsAdd3 = statAdd3.executeQuery();
	// while (rsAdd3.next()) {
	// add3 = rsAdd3.getString(1);
	// }
	// if (rsAdd3 != null) {
	// rsAdd3.close();
	// rsAdd3 = null;
	// }
	//
	// statAdd4.setString(1, (resultSet.getString("SEC_ADD4") != null ?
	// resultSet.getString("SEC_ADD4") : ""));
	// rsAdd4 = statAdd4.executeQuery();
	// while (rsAdd4.next()) {
	// add4 = rsAdd4.getString(1);
	// }
	// if (rsAdd4 != null) {
	// rsAdd4.close();
	// rsAdd4 = null;
	// }
	//
	// statAdd5.setString(1, (resultSet.getString("SEC_ADD5") != null ?
	// resultSet.getString("SEC_ADD5") : ""));
	// rsAdd5 = statAdd5.executeQuery();
	// while (rsAdd5.next()) {
	// add5 = rsAdd5.getString(1);
	// }
	// if (rsAdd5 != null) {
	// rsAdd5.close();
	// rsAdd5 = null;
	// }
	//
	// addressTemp = new Address();
	// addressTemp.setAddress(add1 + " " + add2);
	// addressTemp.setCity(add3);
	// // addressTemp.setStateCode(StateCode.valueOf(add4));
	// // addressTemp.setCountryCode(CountryCode.valueOf(add5));
	// addressTemp.setPinCode(resultSet.getString("SEC_PIN") != null ?
	// resultSet.getString("SEC_PIN") : "");
	//
	// personDetails.setSecondAddress(addressTemp);
	//
	// // pd.setSecondAddress(a1);
	// personDetails.setPAN((resultSet.getString("PAN") != null ?
	// resultSet.getString("PAN") : ""));
	// // pd.setUIN("UIN NO1 ");
	//
	// /*********************************** INDIVIDUAL
	// *******************************/
	// indi = factory.createBatchReportAccountPersonDetailsIndividual();
	// // Gender g=null;
	// // XMLGregorianCalendar GC=
	// // DatatypeFactory.newInstance().newXMLGregorianCalendar(new
	// // GregorianCalendar(2008,10,1));
	// String dobStr = resultSet.getString("DOB") != null ?
	// resultSet.getString("DOB").trim() : "";
	// if (dobStr.length() >= 7) {
	// calenderTemp = DatatypeFactory.newInstance().newXMLGregorianCalendar(
	// new GregorianCalendar(Integer.parseInt(dobStr.substring(0, 2)),
	// Integer.parseInt(dobStr.substring(2, 4)), Integer
	// .parseInt(dobStr.substring(4, 8))));
	// }
	// // indi.setGender(g.M);
	// indi.setGender(Gender.valueOf(resultSet.getString("SEX") != null ?
	// resultSet.getString("SEX") : ""));
	// indi.setDateOfBirth(calenderTemp);
	// indi.setFatherOrSpouse(resultSet.getString("NAME_OF_FATHER_OR_SPOUSE") !=
	// null ? resultSet.getString("NAME_OF_FATHER_OR_SPOUSE")
	// : "");
	// indi.setPlaceOfWork(resultSet.getString("PLACE_OF_WORK") != null ?
	// resultSet.getString("PLACE_OF_WORK") : "");
	// indi.setOccupation(resultSet.getString("OCCUPATION") != null ?
	// resultSet.getString("OCCUPATION") : "");
	//
	// personDetails.setIndividual(indi);
	// listPersonDet.add(personDetails);
	// personDetails = null;
	// calenderTemp = null;
	// addressTemp = null;
	//
	// }
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetails",
	// " Individual File data fetched ");
	//
	// legalStatement.setString(1, reportId);
	// legalStatement.setString(2, acDetails.getAccountNumber());
	// resultSet = legalStatement.executeQuery();
	//
	// while (resultSet.next()) {
	//
	// personDetails = factory.createBatchReportAccountPersonDetails();
	// personDetails.setPersonName(resultSet.getString("LGLPER_NAME") != null ?
	// resultSet.getString("LGLPER_NAME") : "");
	// personDetails.setCustomerId(resultSet.getString("CUST_ID_NO") != null ?
	// resultSet.getString("CUST_ID_NO") : "");
	// //
	// personDetails.setRelationFlag(RelationalFlag.valueOf(resultSet.getString("RELATION_FLG")
	// // != null ?
	// // resultSet.getString("RELATION_FLG").trim() : "Z"));
	//
	// /***/
	//
	// String add1 = "";
	// String add2 = "";
	// String add3 = "";
	// String add4 = "";
	// String add5 = "";
	// add1 = resultSet.getString("COMMUNICATION_ADD1") != null ?
	// resultSet.getString("COMMUNICATION_ADD1") : "";
	// add2 = resultSet.getString("COMMUNICATION_ADD2") != null ?
	// resultSet.getString("COMMUNICATION_ADD2") : "";
	//
	// statAdd3.setString(1, (resultSet.getString("COMMUNICATION_ADD3") != null
	// ? resultSet.getString("COMMUNICATION_ADD3") : ""));
	// rsAdd3 = statAdd3.executeQuery();
	// while (rsAdd3.next()) {
	// add3 = rsAdd3.getString(1);
	// }
	// if (rsAdd3 != null) {
	// rsAdd3.close();
	// rsAdd3 = null;
	// }
	//
	// statAdd4.setString(1, (resultSet.getString("COMMUNICATION_ADD4") != null
	// ? resultSet.getString("COMMUNICATION_ADD4") : ""));
	// rsAdd4 = statAdd4.executeQuery();
	// while (rsAdd4.next()) {
	// add4 = rsAdd4.getString(1);
	// }
	// if (rsAdd4 != null) {
	// rsAdd4.close();
	// rsAdd4 = null;
	// }
	//
	// statAdd5.setString(1, (resultSet.getString("COMMUNICATION_ADD5") != null
	// ? resultSet.getString("COMMUNICATION_ADD5") : ""));
	// rsAdd5 = statAdd5.executeQuery();
	// while (rsAdd5.next()) {
	// add5 = rsAdd5.getString(1);
	// }
	// if (rsAdd5 != null) {
	// rsAdd5.close();
	// rsAdd5 = null;
	// }
	//
	// /***/
	//
	// addressTemp = new Address();
	// addressTemp.setAddress(add1 + " " + add2);
	// addressTemp.setCity(add3);
	// // addressTemp.setStateCode(StateCode.valueOf(add4));
	// // addressTemp.setCountryCode(CountryCode.valueOf(add5));
	// addressTemp.setPinCode(resultSet.getString("COMMUNICATION_PIN") != null ?
	// resultSet.getString("COMMUNICATION_PIN") : "");
	//
	// personDetails.setCommunicationAddress(addressTemp);
	// addressTemp = null;
	// Phone phn = new Phone();
	//
	// phn.setTelephone(resultSet.getString("CONT_TELEPHONE") != null ?
	// resultSet.getString("CONT_TELEPHONE") : "");
	// phn.setFax(resultSet.getString("CONT_FAX") != null ?
	// resultSet.getString("CONT_FAX") : "");
	// personDetails.setPhone(phn);
	// personDetails.setEmail(resultSet.getString("CONT_EMAIL") != null ?
	// resultSet.getString("CONT_EMAIL") : "");
	//
	// add1 = resultSet.getString("REG_ADD1") != null ?
	// resultSet.getString("REG_ADD1") : "";
	// add2 = resultSet.getString("REG_ADD2") != null ?
	// resultSet.getString("REG_ADD2") : "";
	//
	// statAdd3.setString(1, (resultSet.getString("REG_ADD3") != null ?
	// resultSet.getString("REG_ADD3") : ""));
	// rsAdd3 = statAdd3.executeQuery();
	// while (rsAdd3.next()) {
	// add3 = rsAdd3.getString(1);
	// }
	// if (rsAdd3 != null) {
	// rsAdd3.close();
	// rsAdd3 = null;
	// }
	//
	// statAdd4.setString(1, (resultSet.getString("REG_ADD4") != null ?
	// resultSet.getString("REG_ADD4") : ""));
	// rsAdd4 = statAdd4.executeQuery();
	// while (rsAdd4.next()) {
	// add4 = rsAdd4.getString(1);
	// }
	// if (rsAdd4 != null) {
	// rsAdd4.close();
	// rsAdd4 = null;
	// }
	//
	// statAdd5.setString(1, (resultSet.getString("REG_ADD5") != null ?
	// resultSet.getString("REG_ADD5") : ""));
	// rsAdd5 = statAdd5.executeQuery();
	// while (rsAdd5.next()) {
	// add5 = rsAdd5.getString(1);
	// }
	// if (rsAdd5 != null) {
	// rsAdd5.close();
	// rsAdd5 = null;
	// }
	//
	// addressTemp = new Address();
	// addressTemp.setAddress(add1 + " " + add2);
	// addressTemp.setCity(add3);
	// // addressTemp.setStateCode(StateCode.valueOf(add4));
	// // addressTemp.setCountryCode(CountryCode.valueOf(add5));
	// addressTemp.setPinCode(resultSet.getString("REG_PIN") != null ?
	// resultSet.getString("REG_PIN") : "");
	//
	// personDetails.setSecondAddress(addressTemp);
	//
	// personDetails.setPAN((resultSet.getString("PAN") != null ?
	// resultSet.getString("PAN") : ""));
	//
	// legalPerson = factory.createBatchReportAccountPersonDetailsLegalPerson();
	//
	// //
	// legalPerson.setConstitutionType(ConstitutionType.valueOf(resultSet.getString("TYPE_OF_CONSTI")!=null?
	// // resultSet.getString("TYPE_OF_CONSTI"):""));
	//
	// String dateOfIncorporation = resultSet.getString("DATE_OF_INCORPORATION")
	// != null ? resultSet.getString("DATE_OF_INCORPORATION")
	// .trim() : "";
	// if (dateOfIncorporation.length() >= 7) {
	// calenderTemp = DatatypeFactory.newInstance().newXMLGregorianCalendar(
	// new GregorianCalendar(Integer.parseInt(dateOfIncorporation.substring(0,
	// 2).trim()), Integer
	// .parseInt(dateOfIncorporation.substring(2, 4).trim()),
	// Integer.parseInt(dateOfIncorporation.substring(4, 8)
	// .trim())));
	// legalPerson.setDateOfIncorporation(calenderTemp);
	// }
	//
	// legalPerson.setNatureOfBusiness(resultSet.getString("NATURE_OF_BUSINESS")
	// != null ? resultSet.getString("NATURE_OF_BUSINESS")
	// : "");
	// legalPerson.setPlaceOfRegistration(resultSet.getString("REG_PLACE") !=
	// null ? resultSet.getString("REG_PLACE") : "");
	// legalPerson.setRegistrationNumber(resultSet.getString("REG_NO") != null ?
	// resultSet.getString("REG_NO") : "");
	// personDetails.setLegalPerson(legalPerson);
	//
	// listPersonDet.add(personDetails);
	// personDetails = null;
	// calenderTemp = null;
	// addressTemp = null;
	// }
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetails",
	// " LPE file data fetched ");
	//
	// if (resultSet != null) {
	// resultSet.close();
	// resultSet = null;
	// }
	//
	// if (statement != null) {
	// statement.close();
	// statement = null;
	// }
	// /**********************************************************************************************/
	//
	// /********************************* transaction data file
	// ***************************************/
	//
	// listTransaction = new ArrayList<Batch.Report.Account.Transaction>();
	//
	// transactionStatement.setString(1, reportId);
	// transactionStatement.setString(2, acDetails.getAccountNumber());
	//
	// resultSet = transactionStatement.executeQuery();
	//
	// while (resultSet.next()) {
	//
	// transactionTemp = factory.createBatchReportAccountTransaction();
	// JAXBElement<String> tranID = factory
	// .createBatchReportAccountTransactionTransactionID(resultSet.getString("TRAN_ID")
	// != null ? resultSet.getString("TRAN_ID")
	// : "");
	// transactionTemp.setTransactionID(tranID);
	// transactionTemp.setTransactionMode(TransactionMode.valueOf(resultSet.getString("MODE_OF_TRAN")
	// != null ? resultSet
	// .getString("MODE_OF_TRAN") : ""));
	// transactionTemp.setDebitCredit(DebitCredit.valueOf(resultSet.getString("DEB_CRE_FLG")
	// != null ? resultSet
	// .getString("DEB_CRE_FLG") : ""));
	// transactionTemp.setAmount(new BigInteger(resultSet.getString("AMOUNT") !=
	// null ? resultSet.getString("AMOUNT") : ""));
	// transactionTemp.setCurrency(CurrencyCode.valueOf(resultSet.getString("CURR_OF_TRAN")
	// != null ? resultSet
	// .getString("CURR_OF_TRAN") : ""));
	// String dot = resultSet.getString("DATE_OF_TRAN") != null ?
	// resultSet.getString("DATE_OF_TRAN") : "";
	// calenderTemp = DatatypeFactory.newInstance().newXMLGregorianCalendar(
	// new GregorianCalendar(Integer.parseInt(dot.substring(0, 2)),
	// Integer.parseInt(dot.substring(2, 4)), Integer.parseInt(dot
	// .substring(4, 8))));
	// transactionTemp.setDateOfTransaction(calenderTemp);
	// transactionTemp.setDispositionOfFunds(resultSet.getString("DISPOSITION_OF_FUNDS")
	// != null ? resultSet
	// .getString("DISPOSITION_OF_FUNDS") : "");
	//
	// // ********************* PRODUCT TRANSACTION
	// // *****************
	// productTransaction =
	// factory.createBatchReportAccountTransactionProductTransaction();
	//
	// productTransaction.setProductType(ProductType.CD);
	// productTransaction.setIdentifier("New Product");
	// productTransaction.setRate(new BigDecimal("345453"));
	//
	// productTransaction.setTransactionType(ProductTransactionType.DI);
	// productTransaction.setUnits(new BigInteger("234234"));
	// transactionTemp.setProductTransaction(productTransaction);
	//
	// transactionTemp.setRelatedAccountNum(resultSet.getString("AC_NO") != null
	// ? resultSet.getString("AC_NO") : "");
	// transactionTemp.setRelatedInstitutionName("NONAME");
	// // transactionTemp.setRelatedInstitutionRefNum("RELREF0001");
	// transactionTemp.setRemarks(resultSet.getString("REMARKS") != null ?
	// resultSet.getString("REMARKS") : "");
	// listTransaction.add(transactionTemp);
	// transactionTemp = null;
	// productTransaction = null;
	// calenderTemp = null;
	//
	// }
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetails",
	// " Transaction file data fetched ");
	//
	// if (resultSet != null) {
	// resultSet.close();
	// resultSet = null;
	// }
	//
	// /**********************************************************************************************/
	// // Transaction, branch and person details goes here
	//
	// account.setTransaction(listTransaction);
	// account.setAccountDetails(acDetails);
	// account.setPersonDetails(listPersonDet);
	//
	// listPersonDet = null;
	//
	// listAccount.add(account);
	//
	// listTransaction = null;
	// account = null;
	// acDetails = null;
	//
	// // New tring to marshellling report for optimization.
	// // ArrayList<Batch.Report> list = new ArrayList<Batch.Report>();
	// // list.add(report);
	// // report.setAccount(listAccount);
	//
	// // list= null;
	//
	// }
	// report.setAccount(listAccount);
	// StringWriter stringWriter = new StringWriter();
	//
	// batch.getReport().add(report);
	// JAXBElement<Batch> element = factory.createBatch(batch);
	//
	// marshaller.marshal(element, stringWriter);
	// // tryString = stringWriter.toString();
	// System.out.println("this is xml:" + "\n" + stringWriter.toString());
	// output.write(stringWriter.toString());
	// if (rSet != null) {
	// rSet.close();
	// rSet = null;
	// }
	// if (statAdd3 != null) {
	// statAdd3.close();
	// statAdd3 = null;
	// }
	// if (statAdd4 != null) {
	// statAdd4.close();
	// statAdd4 = null;
	// }
	// if (statAdd5 != null) {
	// statAdd5.close();
	// statAdd5 = null;
	// }
	//
	// // listAccount = null;
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew", "9");
	// /****************************** BATCH *************************/
	// // List<Report> reportList = new ArrayList<Report>();
	// // reportList.add(report);
	// //
	// // batch.setReport(reportList);
	//
	// // report = null;
	//
	// output.close();
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew", "10");
	// logger.logVerboseText("GenerateCTR", "mainCTR", "End");
	// connection.commit();
	//
	// } catch (Exception e) {
	// connection.rollback();
	// e.printStackTrace();
	// logger.logExceptionText("GenerateCTR", "mainCTR", "Exception : " + e);
	// } finally {
	// connection.close();
	// }
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetailsNew", " End ");
	// return tryString;
	//
	// }

	// /**
	// *
	// * @param reportId
	// * @param logger
	// * @return
	// * @throws Exception
	// */
	// public STRConfigFileBean ntrXMLDetails(String reportId, InfoLogger
	// logger) throws Exception {
	//
	// logger.logVerboseText("AMLUserDAO", "ntrXMLDetails", " START ");
	//
	// STRConfigFileBean bean = new STRConfigFileBean();
	// Connection connection = null;
	// try {
	// connection = DAOFactory.makeConnectionAMLLive();
	// connection.setAutoCommit(false);
	// // Class.forName("oracle.jdbc.driver.OracleDriver");
	// // String conn = bundle_aml.getString("aml_conn");
	// // String ip = bundle_aml.getString("aml_ip");
	// // String port = bundle_aml.getString("aml_port");
	// // String dbname = bundle_aml.getString("aml_dbname");
	// // String username = bundle_aml.getString("aml_username");
	// // String password = bundle_aml.getString("aml_password");
	// // connection = DriverManager.getConnection(conn + ip + port +
	// // dbname,
	// // username, password);
	// // connection.setAutoCommit(false);
	// } catch (SQLException sqlExp) {
	// logger.logExceptionText("AMLUserDAO", "ntrXMLDetails", "Exception e:" +
	// sqlExp);
	//
	//
	// } catch (Exception cnfExp) {
	// logger.logExceptionText("AMLUserDAO", "ntrXMLDetails", "Exception e:" +
	// cnfExp);
	//
	//
	// }
	//
	// PreparedStatement statement = null;
	// ResultSet rs = null;
	// // PreparedStatement statement1 = null;
	// // ResultSet rs1 = null;
	//
	// try {
	// // I found No comments as STR control file holds comments.
	// // Fetching data from control file.
	// String sql =
	// "select NTR_SEQ_NO,RECORD_TYPE,REPORT_NAME,STATUS,CREATED_DATE from
	// P_AML_NTR_CTRL_FILE where NTR_SEQ_NO=?";
	// statement = connection.prepareStatement(sql);
	// statement.setString(1, reportId);
	// boolean cnt = false;
	// rs = statement.executeQuery();
	// while (rs.next()) {
	// cnt = true;
	// bean.setReport_id(rs.getString("NTR_SEQ_NO") != null ?
	// rs.getString("NTR_SEQ_NO") : "");
	// bean.setReport_type(rs.getString("RECORD_TYPE") != null ?
	// rs.getString("RECORD_TYPE") : "");
	// bean.setReport_name(rs.getString("REPORT_NAME") != null ?
	// rs.getString("REPORT_NAME") : "");
	// bean.setReport_status(rs.getString("STATUS") != null ?
	// rs.getString("STATUS") : "");
	// bean.setCreatedDate(rs.getString("CREATED_DATE") != null ?
	// rs.getString("CREATED_DATE") : "");
	// }
	//
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetails",
	// " control File data fetched ");
	//
	// if (cnt) {
	// sql =
	// "select RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD, LINE_NO, BRANCH_REF_NO,
	// ACCOUNT_NO, FIRST_SOL_AC_HOLDER,"
	// +
	// " TYPE_OF_AC, TYPE_OF_AC_HOLDER,DATE_OF_AC_OPENING, RISK_CATEGORY,
	// CUMULATIVE_CR_TO, CUMULATIVE_DB_TO,"
	// +
	// " CUMULATIVE_CASH_DEP_TO, CUMULATIVE_CASH_WD_TO, NTR_SEQ_NO from
	// P_AML_NTR_ACCOUNT_FILE where NTR_SEQ_NO=?";
	// statement = connection.prepareStatement(sql);
	// statement.setString(1, reportId);
	// rs = statement.executeQuery();
	// ArrayList<STRAccountFileBean> strAccountFileBeans = new
	// ArrayList<STRAccountFileBean>();
	// STRAccountFileBean accountFileBean = null;
	// while (rs.next()) {
	// accountFileBean = new STRAccountFileBean();
	// accountFileBean.setStr_date(bean.getCreatedDate());
	// accountFileBean.setStraf_record_type(rs.getString("RECORD_TYPE") != null
	// ? rs.getString("RECORD_TYPE") : "");
	// accountFileBean.setStraf_report_date(bean.getCreatedDate());
	// accountFileBean.setStraf_line_number(rs.getString("LINE_NO") != null ?
	// rs.getString("LINE_NO") : "");
	// accountFileBean.setStraf_branch_ref_no(rs.getString("BRANCH_REF_NO") !=
	// null ? rs.getString("BRANCH_REF_NO") : "");
	// accountFileBean.setStraf_acc_number(rs.getString("ACCOUNT_NO") != null ?
	// rs.getString("ACCOUNT_NO") : "");
	// accountFileBean.setStraf_name_acc_holder(rs.getString("FIRST_SOL_AC_HOLDER")
	// != null ? rs.getString("FIRST_SOL_AC_HOLDER") : "");
	// accountFileBean.setStraf_type_of_acc(rs.getString("TYPE_OF_AC") != null ?
	// rs.getString("TYPE_OF_AC") : "");
	// accountFileBean.setStraf_type_of_accholder(rs.getString("TYPE_OF_AC_HOLDER")
	// != null ? rs.getString("TYPE_OF_AC_HOLDER") : "");
	// accountFileBean.setStraf_date_of_opening(rs.getString("DATE_OF_AC_OPENING")
	// != null ? rs.getString("DATE_OF_AC_OPENING") : "");
	// accountFileBean.setStraf_risk_category(rs.getString("RISK_CATEGORY") !=
	// null ? rs.getString("RISK_CATEGORY") : "");
	// accountFileBean.setStraf_cum_cr_turnover(rs.getString("CUMULATIVE_CR_TO")
	// != null ? rs.getString("CUMULATIVE_CR_TO") : "");
	// accountFileBean.setStraf_cum_dr_turnover(rs.getString("CUMULATIVE_DB_TO")
	// != null ? rs.getString("CUMULATIVE_DB_TO") : "");
	// accountFileBean.setStraf_cash_dep_turnover(rs.getString("CUMULATIVE_CASH_DEP_TO")
	// != null ? rs
	// .getString("CUMULATIVE_CASH_DEP_TO") : "");
	// accountFileBean.setStraf_cash_wdrwl_turnover(rs.getString("CUMULATIVE_CASH_WD_TO")
	// != null ? rs
	// .getString("CUMULATIVE_CASH_WD_TO") : "");
	// accountFileBean.setMain_caseid(rs.getString("NTR_SEQ_NO") != null ?
	// rs.getString("NTR_SEQ_NO") : "");
	// strAccountFileBeans.add(accountFileBean);
	// accountFileBean = null;
	// }
	//
	// bean.setAccountFileBeans(strAccountFileBeans);
	// strAccountFileBeans = null;
	// if (rs != null) {
	// rs.close();
	// rs = null;
	// }
	// if (statement != null) {
	// statement.close();
	// statement = null;
	// }
	// logger.logVerboseText("AMLUserDAO", "ctrXMLDetails",
	// " Account File data fetched ");
	//
	// sql =
	// "select RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD, LINE_NO,
	// NAME_OF_BRANCH, BRANCH_REF_NO,UID_FIU, "
	// +
	// "BRANCH_ADDRESS1, BRANCH_ADDRESS2,BRANCH_ADDRESS3, BRANCH_ADDRESS4,
	// BRANCH_ADDRESS5,BRANCH_PINCODE, BRANCH_TELEPHONE,"
	// +
	// " BRANCH_FAX,BRANCH_EMAIL, NTR_SEQ_NO from P_AML_NTR_BRANCH_FILE where
	// NTR_SEQ_NO=?";
	// statement = connection.prepareStatement(sql);
	// statement.setString(1, reportId);
	// rs = statement.executeQuery();
	// ArrayList<STRBranchFileBean> strBranchFileBeans = new
	// ArrayList<STRBranchFileBean>();
	// STRBranchFileBean branchFileBean = null;
	//
	// PreparedStatement statAdd3 = null;
	// String sqlAdd3 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '01' and
	// REF_DESC=?";
	// statAdd3 = connection.prepareStatement(sqlAdd3);
	// ResultSet rsAdd3 = null;
	//
	// PreparedStatement statAdd4 = null;
	// String sqlAdd4 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '02' and
	// REF_DESC=?";
	// statAdd4 = connection.prepareStatement(sqlAdd4);
	// ResultSet rsAdd4 = null;
	//
	// PreparedStatement statAdd5 = null;
	// String sqlAdd5 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '03' and
	// REF_DESC=?";
	// statAdd5 = connection.prepareStatement(sqlAdd5);
	// ResultSet rsAdd5 = null;
	//
	// while (rs.next()) {
	// branchFileBean = new STRBranchFileBean();
	// branchFileBean.setStr_date(bean.getCreatedDate());
	// branchFileBean.setStrbf_record_type(rs.getString("RECORD_TYPE"));
	// branchFileBean.setStrbf_report_date(bean.getCreatedDate());
	// branchFileBean.setStrbf_line_number(rs.getString("LINE_NO") != null ?
	// rs.getString("LINE_NO") : "");
	// branchFileBean.setStrbf_branch_name(rs.getString("NAME_OF_BRANCH") !=
	// null ? rs.getString("NAME_OF_BRANCH") : "");
	// branchFileBean.setStrbf_branch_ref_no(rs.getString("BRANCH_REF_NO") !=
	// null ? rs.getString("BRANCH_REF_NO") : "");
	// branchFileBean.setStrbf_fiu_id(rs.getString("UID_FIU") != null ?
	// rs.getString("UID_FIU") : "");
	// branchFileBean.setStrbf_branch_address1(rs.getString("BRANCH_ADDRESS1")
	// != null ? rs.getString("BRANCH_ADDRESS1") : "");
	// branchFileBean.setStrbf_branch_address2(rs.getString("BRANCH_ADDRESS2")
	// != null ? rs.getString("BRANCH_ADDRESS2") : "");
	//
	// statAdd3.setString(1, (rs.getString("BRANCH_ADDRESS3") != null ?
	// rs.getString("BRANCH_ADDRESS3") : ""));
	// rsAdd3 = statAdd3.executeQuery();
	// while (rsAdd3.next()) {
	// branchFileBean.setStrbf_branch_address3(rsAdd3.getString(1));
	// }
	// if (rsAdd3 != null) {
	// rsAdd3.close();
	// rsAdd3 = null;
	// }
	//
	// statAdd4.setString(1, (rs.getString("BRANCH_ADDRESS4") != null ?
	// rs.getString("BRANCH_ADDRESS4") : ""));
	// rsAdd4 = statAdd4.executeQuery();
	// while (rsAdd4.next()) {
	// branchFileBean.setStrbf_branch_address4(rsAdd4.getString(1));
	// }
	// if (rsAdd4 != null) {
	// rsAdd4.close();
	// rsAdd4 = null;
	// }
	//
	// statAdd5.setString(1, (rs.getString("BRANCH_ADDRESS5") != null ?
	// rs.getString("BRANCH_ADDRESS5") : ""));
	// rsAdd5 = statAdd5.executeQuery();
	// while (rsAdd5.next()) {
	// branchFileBean.setStrbf_branch_address5(rsAdd5.getString(1));
	// }
	// if (rsAdd5 != null) {
	// rsAdd5.close();
	// rsAdd5 = null;
	// }
	//
	// branchFileBean.setStrbf_branch_pincode(rs.getString("BRANCH_PINCODE") !=
	// null ? rs.getString("BRANCH_PINCODE") : "");
	// branchFileBean.setStrbf_branch_telno(rs.getString("BRANCH_TELEPHONE") !=
	// null ? rs.getString("BRANCH_TELEPHONE") : "");
	// branchFileBean.setStrbf_branch_fax(rs.getString("BRANCH_FAX") != null ?
	// rs.getString("BRANCH_FAX") : "");
	// branchFileBean.setStrbf_branch_email(rs.getString("BRANCH_EMAIL") != null
	// ? rs.getString("BRANCH_EMAIL") : "");
	// branchFileBean.setMain_caseid(rs.getString("NTR_SEQ_NO") != null ?
	// rs.getString("NTR_SEQ_NO") : "");
	// strBranchFileBeans.add(branchFileBean);
	// branchFileBean = null;
	// }
	// bean.setBranchFileBeans(strBranchFileBeans);
	// strBranchFileBeans = null;
	// logger.logVerboseText("AMLUserDAO", "ntrXMLDetails",
	// " Branch File data fetched ");
	//
	// if (statAdd3 != null) {
	// statAdd3.close();
	// statAdd3 = null;
	// }
	// if (statAdd4 != null) {
	// statAdd4.close();
	// statAdd4 = null;
	// }
	// if (statAdd5 != null) {
	// statAdd5.close();
	// statAdd5 = null;
	// }
	// if (rs != null) {
	// rs.close();
	// rs = null;
	// }
	// if (statement != null) {
	// statement.close();
	// statement = null;
	// }
	//
	// sql =
	// "SELECT RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD,LINE_NO, BRANCH_REF_NO,
	// AC_NO,RELATION_FLG, FULLNAME_INDIVIDUAL, CUST_ID_NO,"
	// +
	// " NAME_OF_FATHER_OR_SPOUSE, OCCUPATION, DOB,SEX, NATIONALITY,
	// TYPE_OF_ID,ID_NO,ISSUING_AUTHORITY, PLACE_OF_ISSUE,PAN,"
	// +
	// " COMMUNICATION_ADD1, COMMUNICATION_ADD2,COMMUNICATION_ADD3,
	// COMMUNICATION_ADD4, COMMUNICATION_ADD5,COMMUNICATION_PIN, "
	// +
	// " CONT_TEL, CONT_MB_NO,CONT_EMAIL, PLACE_OF_WORK, SEC_ADD1,SEC_ADD2,
	// SEC_ADD3, SEC_ADD4,SEC_ADD5, SEC_PIN, SEC_TEL,NTR_SEQ_NO "
	// + " FROM P_AML_NTR_INDIVIDUAL_FILE where NTR_SEQ_NO =?";
	//
	// statement = connection.prepareStatement(sql);
	// statement.setString(1, reportId);
	// rs = statement.executeQuery();
	//
	// ArrayList<STRIndividualFileBean> strIndividualFileBeans = new
	// ArrayList<STRIndividualFileBean>();
	// STRIndividualFileBean individualFileBean = null;
	//
	// PreparedStatement statComm3 = null;
	// String sqlComm3 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '01' and
	// REF_DESC=?";
	// statComm3 = connection.prepareStatement(sqlComm3);
	// ResultSet rsComm3 = null;
	//
	// PreparedStatement statComm4 = null;
	// String sqlComm4 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '02' and
	// REF_DESC=?";
	// statComm4 = connection.prepareStatement(sqlComm4);
	// ResultSet rsComm4 = null;
	//
	// PreparedStatement statComm5 = null;
	// String sqlComm5 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '03' and
	// REF_DESC=?";
	// statComm5 = connection.prepareStatement(sqlComm5);
	// ResultSet rsComm5 = null;
	//
	// while (rs.next()) {
	// individualFileBean = new STRIndividualFileBean();
	// individualFileBean.setStr_date(bean.getCreatedDate());
	// individualFileBean.setStrif_record_type(rs.getString("RECORD_TYPE") !=
	// null ? rs.getString("RECORD_TYPE") : "");
	// individualFileBean.setStrif_report_date(bean.getCreatedDate());
	// individualFileBean.setStrif_line_number(rs.getString("LINE_NO") != null ?
	// rs.getString("LINE_NO") : "");
	// individualFileBean.setStrif_branch_ref_no(rs.getString("BRANCH_REF_NO")
	// != null ? rs.getString("BRANCH_REF_NO") : "");
	// individualFileBean.setStrif_acc_number(rs.getString("AC_NO") != null ?
	// rs.getString("AC_NO") : "");
	// individualFileBean.setStrif_relation_flag(rs.getString("RELATION_FLG") !=
	// null ? rs.getString("RELATION_FLG") : "");
	// individualFileBean.setStrif_full_name_ind(rs.getString("FULLNAME_INDIVIDUAL")
	// != null ? rs.getString("FULLNAME_INDIVIDUAL") : "");
	// individualFileBean.setStrif_customer_id(rs.getString("CUST_ID_NO") !=
	// null ? rs.getString("CUST_ID_NO") : "");
	// individualFileBean.setStrif_father_name(rs.getString("NAME_OF_FATHER_OR_SPOUSE")
	// != null ? rs
	// .getString("NAME_OF_FATHER_OR_SPOUSE") : "");
	// individualFileBean.setStrif_occupation(rs.getString("OCCUPATION") != null
	// ? rs.getString("OCCUPATION") : "");
	// individualFileBean.setStrif_dob(rs.getString("DOB") != null ?
	// rs.getString("DOB") : "");
	// individualFileBean.setStrif_sex(rs.getString("SEX") != null ?
	// rs.getString("SEX") : "");
	// individualFileBean.setStrif_nationality(rs.getString("NATIONALITY") !=
	// null ? rs.getString("NATIONALITY") : "");
	// individualFileBean.setStrif_identification_type(rs.getString("TYPE_OF_ID")
	// != null ? rs.getString("TYPE_OF_ID") : "");
	// individualFileBean.setStrif_identification_num(rs.getString("ID_NO") !=
	// null ? rs.getString("ID_NO") : "");
	// individualFileBean.setStrif_issue_authority(rs.getString("ISSUING_AUTHORITY")
	// != null ? rs.getString("ISSUING_AUTHORITY") : "");
	// individualFileBean.setStrif_issue_place(rs.getString("PLACE_OF_ISSUE") !=
	// null ? rs.getString("PLACE_OF_ISSUE") : "");
	// individualFileBean.setStrif_pan(rs.getString("PAN") != null ?
	// rs.getString("PAN") : "");
	// individualFileBean.setStrif_comm_address1(rs.getString("COMMUNICATION_ADD1")
	// != null ? rs.getString("COMMUNICATION_ADD1") : "");
	// individualFileBean.setStrif_comm_address2(rs.getString("COMMUNICATION_ADD2")
	// != null ? rs.getString("COMMUNICATION_ADD2") : "");
	//
	// statComm3.setString(1, (rs.getString("COMMUNICATION_ADD3") != null ?
	// rs.getString("COMMUNICATION_ADD3") : ""));
	// rsComm3 = statComm3.executeQuery();
	// while (rsComm3.next()) {
	// individualFileBean.setStrif_comm_address3(rsComm3.getString(1));
	// }
	// if (rsComm3 != null) {
	// rsComm3.close();
	// rsComm3 = null;
	// }
	//
	// statComm4.setString(1, (rs.getString("COMMUNICATION_ADD4") != null ?
	// rs.getString("COMMUNICATION_ADD4") : ""));
	// rsComm4 = statComm4.executeQuery();
	// while (rsComm4.next()) {
	// individualFileBean.setStrif_comm_address4(rsComm4.getString(1));
	// }
	// if (rsComm4 != null) {
	// rsComm4.close();
	// rsComm4 = null;
	// }
	//
	// statComm5.setString(1, (rs.getString("COMMUNICATION_ADD5") != null ?
	// rs.getString("COMMUNICATION_ADD5") : ""));
	// rsComm5 = statComm4.executeQuery();
	// while (rsComm5.next()) {
	// individualFileBean.setStrif_comm_address5(rsComm5.getString(1));
	// }
	// if (rsComm5 != null) {
	// rsComm5.close();
	// rsComm5 = null;
	// }
	//
	// /*
	// * sql =
	// *
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '01' and
	// REF_DESC='"
	// * + (rs.getString("COMMUNICATION_ADD3")!=null?rs.getString(
	// * "COMMUNICATION_ADD3"):"") + "'"; statement1 =
	// * connection.prepareStatement(sql); rs1 =
	// * statement1.executeQuery(); while (rs1.next()) {
	// * individualFileBean
	// * .setStrif_comm_address3(rs.getString("ref_code"
	// * )!=null?rs.getString("ref_code"):""); } sql =
	// *
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '02' and
	// REF_DESC='"
	// * + (rs.getString("COMMUNICATION_ADD4")!=null?rs.getString(
	// * "COMMUNICATION_ADD4"):"") + "'"; statement1 =
	// * connection.prepareStatement(sql); rs1 =
	// * statement1.executeQuery(); while (rs1.next()) {
	// * individualFileBean
	// * .setStrif_comm_address4(rs.getString("ref_code"
	// * )!=null?rs.getString("ref_code"):""); } sql =
	// *
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '03' and
	// REF_DESC='"
	// * + (rs.getString("COMMUNICATION_ADD5")!=null?rs.getString(
	// * "COMMUNICATION_ADD5"):"") + "'"; statement1 =
	// * connection.prepareStatement(sql); rs1 =
	// * statement1.executeQuery(); while (rs1.next()) {
	// * individualFileBean
	// * .setStrif_comm_address5(rs.getString("ref_code"
	// * )!=null?rs.getString("ref_code"):""); }
	// */
	//
	// individualFileBean.setStrif_comm_pincode(rs.getString("COMMUNICATION_PIN")
	// != null ? rs.getString("COMMUNICATION_PIN") : "");
	// individualFileBean.setStrif_contact_telno(rs.getString("CONT_TEL") !=
	// null ? rs.getString("CONT_TEL") : "");
	// individualFileBean.setStrif_contact_mobile(rs.getString("CONT_MB_NO") !=
	// null ? rs.getString("CONT_MB_NO") : "");
	// individualFileBean.setStrif_contact_email(rs.getString("CONT_EMAIL") !=
	// null ? rs.getString("CONT_EMAIL") : "");
	// individualFileBean.setStrif_work_place(rs.getString("PLACE_OF_WORK") !=
	// null ? rs.getString("PLACE_OF_WORK") : "");
	// individualFileBean.setStrif_second_address1(rs.getString("SEC_ADD1") !=
	// null ? rs.getString("SEC_ADD1") : "");
	// individualFileBean.setStrif_second_address2(rs.getString("SEC_ADD2") !=
	// null ? rs.getString("SEC_ADD2") : "");
	// individualFileBean.setStrif_second_address3(rs.getString("SEC_ADD3") !=
	// null ? rs.getString("SEC_ADD3") : "");
	// individualFileBean.setStrif_second_address4(rs.getString("SEC_ADD4") !=
	// null ? rs.getString("SEC_ADD4") : "");
	// individualFileBean.setStrif_second_address5(rs.getString("SEC_ADD5") !=
	// null ? rs.getString("SEC_ADD5") : "");
	// individualFileBean.setStrif_second_pincode(rs.getString("SEC_PIN") !=
	// null ? rs.getString("SEC_PIN") : "");
	// individualFileBean.setStrif_second_telno(rs.getString("SEC_TEL") != null
	// ? rs.getString("SEC_TEL") : "");
	// individualFileBean.setMain_caseid(rs.getString("NTR_SEQ_NO") != null ?
	// rs.getString("NTR_SEQ_NO") : "");
	// // TODO No Actual flag into CTR.
	// //
	// individualFileBean.setActual_flag(rs.getString("")!=null?rs.getString(""):"");
	// }
	// if (statComm3 != null) {
	// statComm3.close();
	// statComm3 = null;
	// }
	// if (statComm4 != null) {
	// statComm4.close();
	// statComm4 = null;
	// }
	// if (statComm5 != null) {
	// statComm5.close();
	// statComm5 = null;
	// }
	// if (rs != null) {
	// rs.close();
	// rs = null;
	// }
	// if (statement != null) {
	// statement.close();
	// statement = null;
	// }
	//
	// strIndividualFileBeans.add(individualFileBean);
	// individualFileBean = null;
	// logger.logVerboseText("AMLUserDAO", "ntrXMLDetails",
	// " Individual File data fetched ");
	//
	// sql =
	// "SELECT RECORD_TYPE, MONTH_OF_RECORD, YEAR_OF_RECORD,LINE_NO, BRANCH_REF_NO,
	// AC_NO, "
	// +
	// " RELATION_FLG, LGLPER_NAME, CUST_ID_NO,NATURE_OF_BUSINESS,
	// DATE_OF_INCORPORATION, TYPE_OF_CONSTI, REG_NO, "
	// +
	// " REG_AUTHORITY, REG_PLACE,PAN, COMMUNICATION_ADD1,
	// COMMUNICATION_ADD2,COMMUNICATION_ADD3, COMMUNICATION_ADD4, "
	// +
	// " COMMUNICATION_ADD5,COMMUNICATION_PIN, CONT_TELEPHONE, CONT_FAX,CONT_EMAIL,
	// REG_ADD1, REG_ADD2, REG_ADD3, "
	// +
	// " REG_ADD4, REG_ADD5,REG_PIN, REG_OFF_TELEPHONE, REG_OFF_FAX,NTR_SEQ_NO FROM
	// P_AML_NTR_LGLPERSIONL_FILE where NTR_SEQ_NO=? ";
	//
	// statement = connection.prepareStatement(sql);
	// statement.setString(1, reportId);
	// rs = statement.executeQuery();
	// ArrayList<STRLPEFIleBean> strlpefIleBeans = new
	// ArrayList<STRLPEFIleBean>();
	// STRLPEFIleBean ileBean = null;
	//
	// PreparedStatement statAdLpl3 = null;
	// String sqlAdLpl3 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '01' and
	// REF_DESC=?";
	// statAdLpl3 = connection.prepareStatement(sqlAdLpl3);
	// ResultSet rsAdLpl3 = null;
	//
	// PreparedStatement statAdLpl4 = null;
	// String sqlAdLpl4 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '02' and
	// REF_DESC=?";
	// statAdLpl4 = connection.prepareStatement(sqlAdLpl4);
	// ResultSet rsAdLpl4 = null;
	//
	// PreparedStatement statAdLpl5 = null;
	// String sqlAdLpl5 =
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '03' and
	// REF_DESC=?";
	// statAdLpl5 = connection.prepareStatement(sqlAdLpl5);
	// ResultSet rsAdLpl5 = null;
	//
	// while (rs.next()) {
	// ileBean = new STRLPEFIleBean();
	// ileBean.setStr_date(bean.getCreatedDate());
	// ileBean.setStrlf_record_type(rs.getString("RECORD_TYPE") != null ?
	// rs.getString("RECORD_TYPE") : "");
	// ileBean.setStrlf_report_date(bean.getCreatedDate());
	// ileBean.setStrlf_line_number(rs.getString("LINE_NO") != null ?
	// rs.getString("LINE_NO") : "");
	// ileBean.setStrlf_branch_ref_no(rs.getString("BRANCH_REF_NO") != null ?
	// rs.getString("BRANCH_REF_NO") : "");
	// ileBean.setStrlf_acc_number(rs.getString("AC_NO") != null ?
	// rs.getString("AC_NO") : "");
	// ileBean.setStrlf_relation_flag(rs.getString("RELATION_FLG") != null ?
	// rs.getString("RELATION_FLG") : "");
	// ileBean.setStrlf_full_name_lpe(rs.getString("LGLPER_NAME") != null ?
	// rs.getString("LGLPER_NAME") : "");
	// ileBean.setStrlf_customer_id(rs.getString("CUST_ID_NO") != null ?
	// rs.getString("CUST_ID_NO") : "");
	// ileBean.setStrlf_nature_of_business(rs.getString("NATURE_OF_BUSINESS") !=
	// null ? rs.getString("NATURE_OF_BUSINESS") : "");
	// ileBean.setStrlf_incorporation_date(rs.getString("DATE_OF_INCORPORATION")
	// != null ? rs.getString("DATE_OF_INCORPORATION") : "");
	// ileBean.setStrlf_constitution_type(rs.getString("TYPE_OF_CONSTI") != null
	// ? rs.getString("TYPE_OF_CONSTI") : "");
	// ileBean.setStrlf_regn_num(rs.getString("REG_NO") != null ?
	// rs.getString("REG_NO") : "");
	// ileBean.setStrlf_regn_authority(rs.getString("REG_AUTHORITY") != null ?
	// rs.getString("REG_AUTHORITY") : "");
	// ileBean.setStrlf_regn_place(rs.getString("REG_PLACE") != null ?
	// rs.getString("REG_PLACE") : "");
	// ileBean.setStrlf_pan(rs.getString("PAN") != null ? rs.getString("PAN") :
	// "");
	// ileBean.setStrlf_comm_address1(rs.getString("COMMUNICATION_ADD1") != null
	// ? rs.getString("COMMUNICATION_ADD1") : "");
	// ileBean.setStrlf_comm_address2(rs.getString("COMMUNICATION_ADD2") != null
	// ? rs.getString("COMMUNICATION_ADD2") : "");
	//
	// statAdLpl3.setString(1, (rs.getString("COMMUNICATION_ADD3") != null ?
	// rs.getString("COMMUNICATION_ADD3") : ""));
	// rsAdLpl3 = statAdLpl3.executeQuery();
	// while (rsAdLpl3.next()) {
	// ileBean.setStrlf_comm_address3(rsAdLpl3.getString(1));
	// }
	// if (rsAdLpl3 != null) {
	// rsAdLpl3.close();
	// rsAdLpl3 = null;
	// }
	// statAdLpl4.setString(1, (rs.getString("COMMUNICATION_ADD4") != null ?
	// rs.getString("COMMUNICATION_ADD4") : ""));
	// rsAdLpl4 = statAdLpl4.executeQuery();
	// while (rsAdLpl4.next()) {
	// ileBean.setStrlf_comm_address4(rsAdLpl4.getString(1));
	// }
	// if (rsAdLpl4 != null) {
	// rsAdLpl4.close();
	// rsAdLpl4 = null;
	// }
	//
	// statAdLpl5.setString(1, (rs.getString("COMMUNICATION_ADD5") != null ?
	// rs.getString("COMMUNICATION_ADD5") : ""));
	// rsAdLpl5 = statAdLpl5.executeQuery();
	// while (rsAdLpl5.next()) {
	// ileBean.setStrlf_comm_address5(rsAdLpl5.getString(1));
	// }
	// if (rsAdLpl5 != null) {
	// rsAdLpl5.close();
	// rsAdLpl5 = null;
	// }
	//
	// /*
	// * sql =
	// *
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '01' and
	// REF_DESC='"
	// * +
	// * (rs.getString("RECORD_TYPE")!=null?rs.getString("RECORD_TYPE"
	// * ):"")+ "'"; statement1 =
	// * connection.prepareStatement(sql); rs1 =
	// * statement1.executeQuery(); while (rs1.next()) {
	// * ileBean.setStrlf_comm_address3(rs1.getString(1)); } sql =
	// *
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '02' and
	// REF_DESC='"
	// * +
	// * (rs.getString("RECORD_TYPE")!=null?rs.getString("RECORD_TYPE"
	// * ):"") + "'"; statement1 =
	// * connection.prepareStatement(sql); rs1 =
	// * statement1.executeQuery(); while (rs1.next()) {
	// * ileBean.setStrlf_comm_address4(rs1.getString(1)); } sql =
	// *
	// "select ref_code from AML_RCT_MASTER where ref_rec_type = '03' and
	// REF_DESC='"
	// * +
	// * (rs.getString("RECORD_TYPE")!=null?rs.getString("RECORD_TYPE"
	// * ):"") + "'"; statement1 =
	// * connection.prepareStatement(sql); rs1 =
	// * statement1.executeQuery(); while (rs1.next()) {
	// * ileBean.setStrlf_comm_address5(rs1.getString(1)); }
	// */
	//
	// ileBean.setStrlf_comm_pincode(rs.getString("COMMUNICATION_PIN") != null ?
	// rs.getString("COMMUNICATION_PIN") : "");
	// ileBean.setStrlf_contact_telno(rs.getString("CONT_TELEPHONE") != null ?
	// rs.getString("CONT_TELEPHONE") : "");
	// ileBean.setStrlf_contact_fax(rs.getString("CONT_FAX") != null ?
	// rs.getString("CONT_FAX") : "");
	// ileBean.setStrlf_contact_email(rs.getString("CONT_EMAIL") != null ?
	// rs.getString("CONT_EMAIL") : "");
	// ileBean.setStrlf_second_address1(rs.getString("REG_ADD1") != null ?
	// rs.getString("REG_ADD1") : "");
	// ileBean.setStrlf_second_address2(rs.getString("REG_ADD2") != null ?
	// rs.getString("REG_ADD2") : "");
	// ileBean.setStrlf_second_address3(rs.getString("REG_ADD3") != null ?
	// rs.getString("REG_ADD3") : "");
	// ileBean.setStrlf_second_address4(rs.getString("REG_ADD4") != null ?
	// rs.getString("REG_ADD4") : "");
	// ileBean.setStrlf_second_address5(rs.getString("REG_ADD5") != null ?
	// rs.getString("REG_ADD5") : "");
	// ileBean.setStrlf_second_pincode(rs.getString("REG_PIN") != null ?
	// rs.getString("REG_PIN") : "");
	// ileBean.setStrlf_second_telno(rs.getString("REG_OFF_TELEPHONE") != null ?
	// rs.getString("REG_OFF_TELEPHONE") : "");
	// ileBean.setStrlf_second_fax(rs.getString("REG_OFF_FAX") != null ?
	// rs.getString("REG_OFF_FAX") : "");
	// ileBean.setMain_caseid(rs.getString("NTR_SEQ_NO") != null ?
	// rs.getString("NTR_SEQ_NO") : "");
	// ileBean.setActual_flag("");// TODO
	// strlpefIleBeans.add(ileBean);
	// ileBean = null;
	// }
	// bean.setStrlpefIleBeans(strlpefIleBeans);
	// strlpefIleBeans = null;
	// logger.logVerboseText("AMLUserDAO", "ntrXMLDetails",
	// " LPE file data fetched ");
	//
	// if (statAdLpl3 != null) {
	// statAdLpl3.close();
	// statAdLpl3 = null;
	// }
	// if (statAdLpl4 != null) {
	// statAdLpl4.close();
	// statAdLpl4 = null;
	// }
	// if (statAdLpl5 != null) {
	// statAdLpl5.close();
	// statAdLpl5 = null;
	// }
	// if (rs != null) {
	// rs.close();
	// rs = null;
	// }
	// if (statement != null) {
	// statement.close();
	// statement = null;
	// }
	//
	// sql =
	// "SELECT RECORD_TYPE, LINE_NO, BRANCH_REF_NO, AC_NO, TRAN_ID, DATE_OF_TRAN,
	// MODE_OF_TRAN, DEB_CRE_FLG,"
	// +
	// " AMOUNT,CURR_OF_TRAN, DISPOSITION_OF_FUNDS, REMARKS, NTR_SEQ_NO FROM
	// P_AML_NTR_TRAN_FILE where NTR_SEQ_NO = ? ";
	// statement = connection.prepareStatement(sql);
	// statement.setString(1, reportId);
	// rs = statement.executeQuery();
	// ArrayList<STRTransactionFileBean> strTransactionFileBeans = new
	// ArrayList<STRTransactionFileBean>();
	// STRTransactionFileBean transactionFileBean = new
	// STRTransactionFileBean();
	// while (rs.next()) {
	// transactionFileBean = new STRTransactionFileBean();
	// transactionFileBean.setStr_date(bean.getCreatedDate());
	// transactionFileBean.setStrtf_record_type(rs.getString("RECORD_TYPE") !=
	// null ? rs.getString("RECORD_TYPE") : "");
	// transactionFileBean.setStrtf_line_number(rs.getString("LINE_NO") != null
	// ? rs.getString("LINE_NO") : "");
	// transactionFileBean.setStrtf_branch_ref_no(rs.getString("BRANCH_REF_NO")
	// != null ? rs.getString("BRANCH_REF_NO") : "");
	// transactionFileBean.setStrtf_acc_number(rs.getString("AC_NO") != null ?
	// rs.getString("AC_NO") : "");
	// transactionFileBean.setStrtf_tran_id(rs.getString("TRAN_ID") != null ?
	// rs.getString("TRAN_ID") : "");
	// transactionFileBean.setStrtf_tran_date(rs.getString("DATE_OF_TRAN") !=
	// null ? rs.getString("DATE_OF_TRAN") : "");
	// transactionFileBean.setStrtf_tran_mode(rs.getString("MODE_OF_TRAN") !=
	// null ? rs.getString("MODE_OF_TRAN") : "");
	// transactionFileBean.setStrtf_dr_cr(rs.getString("DEB_CRE_FLG") != null ?
	// rs.getString("DEB_CRE_FLG") : "");
	// if (rs.getString("AMOUNT") != null &&
	// rs.getString("AMOUNT").equalsIgnoreCase("")) {
	// if (rs.getString("AMOUNT").indexOf(".") != -1) {
	// transactionFileBean.setStrtf_amount(rs.getString("AMOUNT").substring(0,
	// rs.getString("AMOUNT").indexOf(".")));
	// }
	//
	// }
	//
	// transactionFileBean.setStrtf_tran_currency(rs.getString("CURR_OF_TRAN")
	// != null ? rs.getString("CURR_OF_TRAN") : "");
	// transactionFileBean.setStrtf_funds_disposition(rs.getString("DISPOSITION_OF_FUNDS")
	// != null ? rs
	// .getString("DISPOSITION_OF_FUNDS") : "");
	// transactionFileBean.setStrtf_remarks(rs.getString("REMARKS") != null ?
	// rs.getString("REMARKS") : "");
	// transactionFileBean.setMain_caseid(rs.getString("NTR_SEQ_NO") != null ?
	// rs.getString("NTR_SEQ_NO") : "");
	// strTransactionFileBeans.add(transactionFileBean);
	// transactionFileBean = null;
	// }
	// bean.setTransactionFileBeans(strTransactionFileBeans);
	// strTransactionFileBeans = null;
	//
	// logger.logVerboseText("AMLUserDAO", "ntrXMLDetails",
	// " Transaction file data fetched ");
	//
	// if (rs != null) {
	// rs.close();
	// rs = null;
	// }
	// if (statement != null) {
	// statement.close();
	// statement = null;
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.logVerboseText("AMLUserDAO", "ntrXMLDetails", "Exception" + e);
	// throw new Exception(e);
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (statement != null) {
	// statement.close();
	// }
	// if (connection != null) {
	// connection.close();
	// }
	//
	// } catch (Exception e) {
	// logger.logVerboseText("AMLUserDAO", "ntrXMLDetails", "Finally Exception"
	// +
	// e);
	// e.printStackTrace();
	// }
	// }
	//
	// logger.logVerboseText("AMLUserDAO", "ntrXMLDetails", " END ");
	//
	// return bean;
	// }

	/**
	 * 
	 * @param infoLogger
	 * @param reportId
	 * @return STRDataBean
	 * @throws Exception
	 */
	public CTRPdfDataBean getCtrPdfData(InfoLogger infoLogger, String reportId, String reportType) throws Exception {

		infoLogger.logVerboseText("AMLUserDAO", "getCtrPdfData", "Start");

		PreparedStatement statement = null;
		Connection connection = null;

		CTRPdfDataBean dataBean = new CTRPdfDataBean();

		ResultSet rs = null;

		String sql = null;

		try {
			try {
				connection = ConnectionFactory.makeConnectionAMLLiveThread();
				connection.setAutoCommit(false);
				// Class.forName("oracle.jdbc.driver.OracleDriver");
				// String conn = bundle_aml.getString("aml_conn");
				// String ip = bundle_aml.getString("aml_ip");
				// String port = bundle_aml.getString("aml_port");
				// String dbname = bundle_aml.getString("aml_dbname");
				// String username = bundle_aml.getString("aml_username");
				// String password = bundle_aml.getString("aml_password");
				// connection = DriverManager.getConnection(conn + ip + port
				// + dbname, username, password);
				// connection.setAutoCommit(false);
			} catch (SQLException sqlExp) {
				infoLogger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception e:" + sqlExp);

				sqlExp.printStackTrace();
				System.err.println("Exception :" + sqlExp);
			} catch (Exception cnfExp) {
				infoLogger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception e:" + cnfExp);

				cnfExp.printStackTrace();
				System.err.println("Exception :" + cnfExp);
			}

			infoLogger.logVerboseText("AMLUserDAO", "getCtrPdfData : part 1 start", "...");

			// part 1

			sql = "select MONTH_OF_RECORD, YEAR_OF_RECORD from P_AML_" + reportType.toUpperCase() + "_CTRL_FILE where "
					+ reportType.toUpperCase() + "_SEQ_NO = '" + reportId + "'";

			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs != null && rs.next()) {

				String monthOfRecord = rs.getString(1);
				String yearOfRecord = rs.getString(2);

				dataBean.setMonth1(Character.toString(monthOfRecord.charAt(0)));
				dataBean.setMonth2(Character.toString(monthOfRecord.charAt(1)));

				dataBean.setYear1(Character.toString(yearOfRecord.charAt(2)));
				dataBean.setYear2(Character.toString(yearOfRecord.charAt(3)));

				dataBean.setSupplementaryYN("No");
			}

			infoLogger.logVerboseText("AMLUserDAO", "getCtrPdfData : part 1 complete", "...");

			// part 4

			sql = "select TOTAL_BRANCH_NO, B_NO_INCLUDING_NIL, B_NO_EXCLUDING_NIL, REPORT_TYPE, NO_OF_"
					+ reportType.toUpperCase() + "S from P_AML_" + reportType.toUpperCase() + "_CTRL_FILE where "
					+ reportType.toUpperCase() + "_SEQ_NO = '" + reportId + "'";
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs != null && rs.next()) {
				dataBean.setBranchesTotal(rs.getString(1));
				dataBean.setBranchesReported(rs.getString(2));
				dataBean.setBranchesCTR(rs.getString(3));

				if (rs.getString(4).equalsIgnoreCase(AMLConstants.NEW)) {
					dataBean.setCtrOriginal(rs.getString(5));
					dataBean.setCtrReplacement("0");
				} else {
					dataBean.setCtrOriginal("0");
					dataBean.setCtrReplacement(rs.getString(5));
				}
			}

			infoLogger.logVerboseText("AMLUserDAO", "getCtrPdfData : part 3 complete", "...");

		} catch (SQLException e) {
			infoLogger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception" + e);
			throw new Exception();
		} catch (Exception e) {
			infoLogger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception" + e);
			throw new Exception();
		} finally {
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (Exception ex) {
				infoLogger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception" + ex);
			}
		}
		infoLogger.logVerboseText("AMLUserDAO", "getCtrPdfData", "End");

		return dataBean;
	} // method end : getCtrPdfData

	/**
	 * To generate Excel file from passed Id.
	 * 
	 * @param logger
	 * @param reportId
	 * @param reportType
	 * @throws Exception
	 */
	public void generateExcelFile(InfoLogger logger, String reportId, String reportType) throws Exception {
		logger.logVerboseText("AMLUserDAO", "generateExcelFile", " Start ");

		PreparedStatement statement = null;
		Connection connection = null;

		ResultSet rs = null;

		String ExcelPath = "";
		ResourceBundle rb = ResourceBundle.getBundle("AMLProp");

		if (reportType.equals("CTR")) {
			ExcelPath = rb.getString("CTRDIR");
		} else if (reportType == "NTR") {
			ExcelPath = rb.getString("NTRDIR");
		} else if (reportType.equalsIgnoreCase("SIDBI")) {
			ExcelPath = rb.getString("SIDBICTRDIR");
		}

		String sql = "SELECT c.comp_name_of_bank, (SELECT s.sol_desc FROM p_aml_sol s WHERE s.sol_id = t.sol_id) AS branch_name, "
				+ " t.line_no, t.branch_ref_no,(SELECT a.acct_name FROM aml_ac_master a WHERE a.cust_ac_no = trim(t.ac_no)) AS account_holder_name, t.ac_no, "
				+ " t.tran_id, t.date_of_tran, t.mode_of_tran, t.deb_cre_flg, round(t.amount) as amount, t.curr_of_tran, t.remarks FROM p_aml_"
				+ reportType + "_ctrl_file c,  p_aml_" + reportType + "_tran_file t WHERE c." + reportType
				+ "_seq_no = t." + reportType + "_seq_no  and to_number(t.AMOUNT) >" + AMLConstants.CTRExcelReportAmount
				+ " and t." + reportType + "_seq_no = '" + reportId + "'";

		logger.logVerboseText("AMLUserDAO", "generateExcelFile", "query :" + sql);

		try {
			try {
				connection = ConnectionFactory.makeConnectionAMLLiveThread();
				connection.setAutoCommit(false);
				// Class.forName("oracle.jdbc.driver.OracleDriver");
				// String conn = bundle_aml.getString("aml_conn");
				// String ip = bundle_aml.getString("aml_ip");
				// String port = bundle_aml.getString("aml_port");
				// String dbname = bundle_aml.getString("aml_dbname");
				// String username = bundle_aml.getString("aml_username");
				// String password = bundle_aml.getString("aml_password");
				// connection = DriverManager.getConnection(conn + ip + port
				// + dbname, username, password);
				// connection.setAutoCommit(false);
			} catch (SQLException sqlExp) {
				logger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception e:" + sqlExp);

				sqlExp.printStackTrace();
				System.err.println("Exception :" + sqlExp);
			} catch (Exception cnfExp) {
				logger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception e:" + cnfExp);

				cnfExp.printStackTrace();
				System.err.println("Exception :" + cnfExp);
			}

			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			HSSFWorkbook wb = new HSSFWorkbook();
			String aFileName = ExcelPath + reportId + ".xls";

			HSSFSheet sheet = wb.createSheet();

			sheet.setColumnWidth(0, 35 * 256);
			sheet.setColumnWidth(1, 40 * 256);
			sheet.setColumnWidth(2, 40 * 256);
			sheet.setColumnWidth(3, 40 * 256);
			sheet.setColumnWidth(4, 15 * 256);
			sheet.setColumnWidth(5, 15 * 256);
			sheet.setColumnWidth(6, 15 * 256);
			sheet.setColumnWidth(7, 15 * 256);
			sheet.setColumnWidth(8, 15 * 256);
			sheet.setColumnWidth(9, 15 * 256);
			sheet.setColumnWidth(10, 15 * 256);
			sheet.setColumnWidth(11, 15 * 256);
			sheet.setColumnWidth(12, 15 * 256);

			HSSFRow row = sheet.createRow((short) 0);
			HSSFCellStyle s = wb.createCellStyle();
			s.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
			//s.setFillPattern(CellStyle.SOLID_FOREGROUND2);
			HSSFCell c = row.createCell(0);
			c.setCellValue("Bank Name");
			c.setCellStyle(s);

			c = row.createCell(1);
			c.setCellValue("Branch Name");
			c.setCellStyle(s);

			c = row.createCell(2);
			c.setCellValue("Line Number");
			c.setCellStyle(s);

			c = row.createCell(3);
			c.setCellValue("Branch Reference Number");
			c.setCellStyle(s);

			c = row.createCell(4);
			c.setCellValue("Holder Name");
			c.setCellStyle(s);

			c = row.createCell(5);
			c.setCellValue("Account Number");
			c.setCellStyle(s);

			c = row.createCell(6);
			c.setCellValue("Transaction Id");
			c.setCellStyle(s);

			c = row.createCell(7);
			c.setCellValue("Transaction Date");
			c.setCellStyle(s);

			c = row.createCell(8);
			c.setCellValue("Transaction Mode");
			c.setCellStyle(s);

			c = row.createCell(9);
			c.setCellValue("Credit Debit Flag");
			c.setCellStyle(s);

			c = row.createCell(10);
			c.setCellValue("Amount");
			c.setCellStyle(s);

			c = row.createCell(11);
			c.setCellValue("Currency");
			c.setCellStyle(s);

			c = row.createCell(12);
			c.setCellValue("Remarks");
			c.setCellStyle(s);

			int i = 1;
			while (rs.next()) {
				HSSFRow detailsRow = sheet.createRow(i++);

				CellStyle style = wb.createCellStyle();
				style = wb.createCellStyle();
				style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
				//style.setFillPattern(CellStyle.SOLID_FOREGROUND2);

				HSSFCell cell = detailsRow.createCell(0);
				cell.setCellValue(rs.getString("comp_name_of_bank") != null ? rs.getString("comp_name_of_bank")
						: AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(1);
				cell.setCellValue(
						rs.getString("branch_name") != null ? rs.getString("branch_name") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(2);
				cell.setCellValue(rs.getString("line_no") != null ? rs.getString("line_no") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(3);
				cell.setCellValue(
						rs.getString("branch_ref_no") != null ? rs.getString("branch_ref_no") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(4);
				cell.setCellValue(rs.getString("account_holder_name") != null ? rs.getString("account_holder_name")
						: AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(5);
				cell.setCellValue(rs.getString("ac_no") != null ? rs.getString("ac_no") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(6);
				cell.setCellValue(rs.getString("tran_id") != null ? rs.getString("tran_id") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(7);
				cell.setCellValue(
						rs.getString("date_of_tran") != null ? rs.getString("date_of_tran") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(8);
				cell.setCellValue(
						rs.getString("mode_of_tran") != null ? rs.getString("mode_of_tran") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(9);
				cell.setCellValue(
						rs.getString("deb_cre_flg") != null ? rs.getString("deb_cre_flg") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(10);
				cell.setCellValue(rs.getString("amount") != null ? rs.getString("amount") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(11);
				cell.setCellValue(
						rs.getString("curr_of_tran") != null ? rs.getString("curr_of_tran") : AMLConstants.NONE);
				cell.setCellStyle(style);

				cell = detailsRow.createCell(12);
				cell.setCellValue(rs.getString("remarks") != null ? rs.getString("remarks") : AMLConstants.NONE);
				cell.setCellStyle(style);

				// After every 32000 rows it will create new sheet in same work
				// book.
				if (i == 32000) {

					if (sheet != null) {
						sheet = null;
					}
					sheet = wb.createSheet();
					i = 1;

					HSSFRow row1 = sheet.createRow((short) 0);
					HSSFCellStyle s1 = wb.createCellStyle();
					s1.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
					//s1.setFillPattern(CellStyle.SOLID_FOREGROUND2);

					HSSFCell c1 = row1.createCell(0);
					c1.setCellValue(rs.getString("comp_name_of_bank") != null ? rs.getString("comp_name_of_bank")
							: AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(1);
					c1.setCellValue(
							rs.getString("branch_name") != null ? rs.getString("branch_name") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(2);
					c1.setCellValue(rs.getString("line_no") != null ? rs.getString("line_no") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(3);
					c1.setCellValue(
							rs.getString("branch_ref_no") != null ? rs.getString("branch_ref_no") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(4);
					c1.setCellValue(rs.getString("account_holder_name") != null ? rs.getString("account_holder_name")
							: AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(5);
					c1.setCellValue(rs.getString("ac_no") != null ? rs.getString("ac_no") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(6);
					c1.setCellValue(rs.getString("tran_id") != null ? rs.getString("tran_id") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c = row1.createCell(7);
					c.setCellValue(
							rs.getString("date_of_tran") != null ? rs.getString("date_of_tran") : AMLConstants.NONE);
					c.setCellStyle(s);

					c1 = row1.createCell(8);
					c1.setCellValue(
							rs.getString("mode_of_tran") != null ? rs.getString("mode_of_tran") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(9);
					c1.setCellValue(
							rs.getString("deb_cre_flg") != null ? rs.getString("deb_cre_flg") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(10);
					c1.setCellValue(rs.getString("amount") != null ? rs.getString("amount") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(11);
					c1.setCellValue(
							rs.getString("curr_of_tran") != null ? rs.getString("curr_of_tran") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					c1 = row1.createCell(12);
					c1.setCellValue(rs.getString("remarks") != null ? rs.getString("remarks") : AMLConstants.NONE);
					c1.setCellStyle(s1);

					if (c1 != null) {
						c1 = null;
					}
					if (s1 != null) {
						s1 = null;
					}

				}

			}

			FileOutputStream fileOut = new FileOutputStream(aFileName);
			wb.write(fileOut);
			fileOut.close();

		} catch (SQLException e) {
			e.printStackTrace();
			logger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception" + e);
			throw new Exception();
		} catch (Exception e) {
			logger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception" + e);
			throw new Exception();
		} finally {
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (Exception ex) {
				logger.logExceptionText("AMLUserDAO", "getCtrPdfData", "Exception" + ex);
			}
		}

		logger.logVerboseText("AMLUserDAO", "generateExcelFile", " End ");
	}// Method generateExcelFile End

}// class End

package com.idbi.intech.iaml.finnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import com.idbi.intech.iaml.regulatory.FilesCompresser;

public class GenerateStrCSVFile implements Runnable {

	Properties amlProp = new Properties();
	String dir = System.getProperty("user.dir");
	FilesCompresser compresser = new FilesCompresser();
	private static final String INKEYVALUE = "INKEYVALUE";

	private void generateRegCsvFile() {

		Statement stmtCheck = null;
		String reqId = "";
		String reportType = "";
		ResultSet rs = null;
		ResultSet rsBlock = null;
		String blockNo = null;
		ResultSet rsFileRefNo = null;
		PreparedStatement ptstFileRefNo = null;
		String GetRequestId = "SELECT REQUEST_ID,REG_REPORT_TYPE FROM PNBFINNET.REG_REQUEST_DTLS WHERE DWLD_FLG ='R'";

		List<String> blockList = new ArrayList<>();
		List<String> strRequestIdList = new ArrayList<>();

		/* Make List for STR REQUEST ID */
		List<StrRequestDtlsBean> strDtlsList = new ArrayList<>();
		String filePath = "";
		try (Connection connection = ConnectionFactory.makeConnectionAMLLive();
				InputStream is = new FileInputStream(dir + "/aml-config.properties");) {

			amlProp.load(is);
			is.close();
			stmtCheck = connection.createStatement();
			rs = stmtCheck.executeQuery(GetRequestId);

			// System.out.println("till here correct");
			while (rs.next()) {
				reqId = rs.getString(1);
				reportType = rs.getString(2);
				// Make List for only Str Request Id
				strRequestIdList.add(reqId);
			}

			if (!strRequestIdList.isEmpty()) {

				Set<String> uniqueRefFileNo = new HashSet<>();
				List<String> reqList = new ArrayList<>();
				String reqString = null;

				System.out.println("Generating STR CSV file for Request ID " + reqId);

				for (String req : strRequestIdList) {
					reqList.add("'" + req + "'");
				}
				reqString = reqList.toString().replace("[", "").replace("]", "");

				String query = "select req_id,file_ref_no from PNBFINNET.str_request_dtls where req_id IN (INKEYVALUE)";
				String mainQuery = query.replace(INKEYVALUE, reqString);

				System.out.println("mainQuery " + mainQuery);
				ptstFileRefNo = connection.prepareStatement(mainQuery);
				rsFileRefNo = ptstFileRefNo.executeQuery();

				while (rsFileRefNo.next()) {

					StrRequestDtlsBean strRequestBean = new StrRequestDtlsBean();
					strRequestBean.setReqId(rsFileRefNo.getString(1));
					strRequestBean.setFileRefNo(rsFileRefNo.getString(2));

					// Save into Unique SET
					uniqueRefFileNo.add(strRequestBean.getFileRefNo());

					// Make List of all STR Request List
					strDtlsList.add(strRequestBean);
				}

				for (String refFileNo : uniqueRefFileNo) {

					if (strDtlsList != null) {
						List<String> reqIdListForFileRef = new ArrayList<>();
						reqIdListForFileRef = strDtlsList.stream()
								.filter(p -> p.getFileRefNo().equalsIgnoreCase(refFileNo)).map(p -> p.getReqId())
								.collect(Collectors.toList());

						generateStrFiles(reqIdListForFileRef, reportType, refFileNo);
						updateReqStatusForStr(reqIdListForFileRef);
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (ptstFileRefNo != null) {
					ptstFileRefNo.close();
					ptstFileRefNo = null;
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

	/* This Method is used for Download FIles into Specified Path */
	public void generateStrFiles(List<String> reqIdList, String reportType, String refFileNo) {
		List<String> fileList = new ArrayList<>();
		List<String> tableList = null;
		String filePath = null;

		try (InputStream is = new FileInputStream(dir + "/aml-config.properties");
				Connection connection = ConnectionFactory.makeConnectionAMLLive();) {
			amlProp.load(is);
			is.close();

			filePath = amlProp.getProperty("STR_FIN_FILES") + refFileNo.replace("/", "") + "\\";

			tableList = getTableList(reportType);
			for (String tableData : tableList) {
				String[] tabDtls = tableData.split("~");

				generateStr(tabDtls[0], tabDtls[1], tabDtls[2], reqIdList, reportType, tabDtls[3], refFileNo);
				fileList.add(filePath + tabDtls[1].replace("/", "") + ".txt");
			}

			if (!fileList.isEmpty()) {
				String[] fileArr = new String[fileList.size()];
				fileList.toArray(fileArr);
				System.out.println("filePath : " + filePath);

				// FilesCompresser.compress(filePath + reqId.replace("/", ""), fileArr);
				FilesCompresser.compress(filePath + refFileNo.replace("/", ""), fileArr);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void generateStr(String tableName, String fileName, String columnName, List<String> reqIdList,
			String reportType, String fileType, String fileRefNo) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection connection = null;

		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			String ctrFilePath = amlProp.getProperty("STR_FIN_FILES") + fileRefNo.replace("/", "") + "\\";
			// System.out.println(tableName + "-" + fileName + "-" + columnName + "-" +
			// reqId);
			String sql = null;

			if (tableName.equalsIgnoreCase("STR_REG_KC1")) {
				sql = "select report_ref_num\"Report Reference Number\",ucic_id \"UCIC\",cust_id \"Customer ID\",pan_no \"PAN\",decode(pan_no,null,pan_declaration,null) \"Declaration (If PAN is not available)\",\r\n"
						+ "ckyc_no \"CKYC Number\",decode(ckyc_no,null,ckyc_declaration,null) \"Declaration (If CKYC is not available)\",passport_no \"Passport Number\",voter_id \"Voter ID\",other_occupation \"Other Occupation\",\r\n"
						+ "drivers_license_num \"Drivers License Number\",\r\n"
						+ "nrega_card \"NREGA Card\",din_dpin \"DIN / DPIN\",first_name \"First Name\",middle_name \"Middle Name\",last_name \"Last Name\",decode(last_name,null,last_name_declaration,null)  \"Declaration (If Last name is not available)\",\r\n"
						+ "father_name \"Name of Father\",mother_name \"Name of Mother\",spouse_partner_name \"Spouse/Partner Name\",initcap(gender) \"Gender\",date_of_birth \"Date of Birth\",nationality \"Nationality\",\r\n"
						+ "mobile_no \"Mobile Number\",alt_mobile_no \"Alternate Mobile Number\",tel_no \"Telephone Number\",email_id \"Email ID\",\r\n"
						+ "primary_addr \"Primary Address 1\",primary_addr_country \"Primary Address Country\",primary_addr_pin_code \"Primary Address Pin Code\",primary_addr_locality \"Primary Address Locality\",\r\n"
						+ "primary_addr_state \"Primary Address State\",primary_addr_district \"Primary Address District\",primary_addr_city \"Primary Address City\",\r\n"
						+ "secondary_addr \"Secondary Address 1\",secondary_addr_country \"Secondary Address Country\",secondary_addr_pin_code \"Secondary Address Pin Code\",secondary_addr_locality \"Secondary Address Locality\",\r\n"
						+ "secondary_addr_state \"Secondary Address State\",secondary_addr_district \"Secondary Address District\",secondary_addr_city \"Secondary Address City\", \r\n"
						+ "idntity_verfd_wth_aadhaar \"Identity verified using Aadhaar ID\",\r\n"
						+ "cust_type \"Customer Type\",annual_income \"Annual Income (INR)\",occupation \"Occupation\",employee_name \"Employer Name\",employee_addr \"Employer Address 1\",\r\n"
						+ "employee_addr_country \"Employer Address Country\",employee_addr_pin_code \"Employer Address Pin Code\",employee_addr_locality \"Employer Address Locality\",\r\n"
						+ "employee_addr_state \"Employer Address State\",employee_addr_district \"Employer Address District\",employee_addr_city \"Employer Address City\",\r\n"
						+ "cust_onboarding_dt \"Date of Customer On-boarding\",last_kyc_dt \"Date of Last KYC / re-KYC\",initcap(cust_risk) \"Customer Risk Level\",npr_no \"NPR\",pekrn_no \"PEKRN\",\r\n"
						+ "primary_state_name \"Primary State Name\",primary_district_name \"Primary District Name\",primary_city_name \"Primary City Name\",\r\n"
						+ "secondary_state_name \"Secondary State Name\",secondary_district_name \"Secondary District Name\",secondary_city_name \"Secondary City Name\",\r\n"
						+ "employee_state_name \"Employee State Name\",employee_district_name \"Employee District Name\",employee_city_name \"Employee City Name\"\r\n"
						+ ",other_cust_type \"Other Customer Type\",report_type \"Report Type\"\r\n" + "from "
						+ tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_KC2")) {
				sql = "select report_ref_num\"Report Reference Number\",ucic_id \"UCIC\",entity_name \"Entity Name\",unique_company_id \"Company ID Number\", company_id_type \"Company ID Type\",\r\n"
						+ "pan_no \"PAN Number\",pan_declaration \"Declaration (If PAN is not available)\",tan_no \"TAN\",gstin_no \"GSTIN\",\r\n"
						+ "iec_code \"IEC\",iec_declaration \"Declaration (If IEC is not available)\", No_identifier_available \"No identifier available\",telephone_no \"Telephone Number\",mobile_no \"Mobile Number\",email_id \"Email ID\",\r\n"
						+ "company_website \"Company Website\",country \"Country\", address_line \"Address Line 1\",addr_pin_code \"PIN Code\",addr_locality \"Locality\",addr_state \"State\",\r\n"
						+ "addr_district \"District\",addr_city \"City / Village / Town\",line_of_bussiness \"Line of Business\", fcra_status \"FCRA Status\",fcra_reg_state \"FCRA Registration State\",fcra_reg_no \"FCRA Registration Number\",\r\n"
						+ "cust_id \"Customer ID\",reg_country \"Registered Country\",reg_addr \"Registered Address\",reg_pin_code \"Registered PIN Code\",reg_locality \"Registered Locality\",reg_state \"Registered State\",\r\n"
						+ "reg_district \"Registered District\",reg_city \"Registered City / Village / Town\",date_of_incorp \"Date Of Incorporation\",cust_type \"Customer Type\", other_cust_type \"Other Customer Type\",\r\n"
						+ "last_kyc_dt \"Last KYC Date\",onboarding_dt \"Onboarding Date\",cust_risk_level \"Customer Risk Level\",ubo \"UBO\",ubo_declaration \"Declaration (If UBO is not available)\",\r\n"
						+ "pekrn \"PEKRN\",state_name \"State Name\",district_name \"District Name\",city_name \"City / Village / Town Name\",registered_state_name \"Registered State Name\",registered_district_name \"Registered District Name\",\r\n"
						+ "registered_city_name \"Registered City / Village / Town Name\",report_type \"Report Type\"\r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_ACCOUNT_DTLS")) {
				sql = "select report_type \"Report Type\",\r\n"
						+ "report_ref_num\"Report Reference Number\" ,account_type \"Account Type\",account_number \"Account Number\",branch_code \"Branch Code of Account\",\r\n"
						+ "date_of_ac_opn \"Date of Account Opening\",date_of_ac_cls \"Date of Account Closing\",upper(account_status) \"Account Status\",reason_for_freeze \"Reason for Account Freeze\",\r\n"
						+ "no_of_debit \"No Of Debits (In last 12 months)\",total_debit_amt \"Total Debit Amount (In last 12 months)\",no_of_credit \"No Of Credits (In last 12 months)\",total_credit_amt \"Total Credit Amount (In last 12 months)\",\r\n"
						+ "no_of_cash_txn \"No Of Cash Transaction (In last 12 months)\",total_cash_credit \"Total Cash Deposit (In last 12 months)\",total_cash_debit \"Total Cash Withdrawal (In last 12 months) Amount\"\r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_ACCOUNT_PERSON_RELTN")) {
				sql = "select report_type \"Report Type\",\r\n"
						+ "report_ref_num\"Report Reference Number\" ,account_number \"Account Number\",related_person_type \"Relationship Type\",unique_ref_no \"Unique Reference Number\",\r\n"
						+ "ind_nid_status \"Individual / Non-individual\",name_of_noncustomer \"Name of Non-customer\"\r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_TC1")) {
				sql = "select relationship_flg \"Relationship Flag\",transaction_status \"Transaction Status\",transaction_id \"Transaction ID\",transaction_date \"Transaction Date\",\r\n"
						+ "transaction_time \"Transaction Time\",deposit_withdrawal \"Deposit/Withdrawal\",transaction_amount \"Transaction Amount\",account_number \"Account Number\",\r\n"
						+ "card_type \"Card Type\",trim(card_number) \"Card Number\",card_issue_cntry \"Card Issuing Country\",card_issue_bank \"Card Issuing Bank\",\r\n"
						+ "report_ref_num \"Report Reference Number\",switch_txn_id \"Switch Provider Transaction ID (If applicable)\",atm_cam_id \"ATM ID/ CAM ID\",branch_id_ac \"Branch Code of Account\",report_type \"Report Type\" \r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_TC2")) {
				sql = "select report_type \"Report Type\",report_ref_num \"Report Reference Number\",branch_id_ac \"Branch Code of Account\",account_number \"Account Number\",\r\n"
						+ "tran_branch_id \"Transaction Branch Code\",deposit_withdrawal \"Deposit/Withdrawal\",transaction_id \"Transaction ID\",transaction_date \"Transaction Date\",\r\n"
						+ "transaction_time \"Transaction Time\",transaction_amount \"Transaction Amount\",\r\n"
						+ "instrument_type \"Instrument Type\",trim(instrument_id) \"Instrument ID (Card Number/ Cash/ Cheque/ DD/ Others)\",\r\n"
						+ "non_cust_ref_num \"Non-Customer Reference Number\",relationship_flg \"Relationship Flag\",\r\n"
						+ "third_party_pan \"Third Party PAN (Non-Customer)\", THIRD_PARTY_PAN_DECLR \"Declaration (If Third Party PAN (Non-Customer) is not available)\" \r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_TS1")) {
				sql = "select \r\n"
						+ "REPORT_REF_NUM \"Report Reference Number\", RELATIONSHIP_FLG \"Relationship Flag\", TRANSACTION_ID \"Transaction ID\", REPORT_TYPE \"Report Type\",\r\n"
						+ "TRANSACTION_TYPE \"Transaction Type\", TRANSACTION_AMOUNT \"Transaction Amount\", TRANSACTION_DATE \"Transaction Date\", TRANSACTION_TIME \"Transaction Time\",\r\n"
						+ "SENDER_NAME \"Sender Name\", SENDER_IFSC_CODE \"Sender IFSC\", SENDER_ACCOUNT_NO \"Sender Account Number\", BENEF_NAME \"Beneficiary Name\",\r\n"
						+ "BENEF_IFSC_CODE \"Beneficiary IFSC\", BENEF_ACCOUNT_NO \"Beneficiary Account Number\", NARRATION \"Narration\"\r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_TS2")) {
				sql = "SELECT RELATIONSHIP_FLG \"Relationship Flag\", REPORT_REF_NUM \"Report Reference Number\", TRANSACTION_TYPE \"Transaction Type\",TRANSACTION_ID \"Transaction ID\",\r\n"
						+ "TRANSACTION_AMOUNT \"Transaction Amount\", TRANSACTION_DATE \"Transaction Date\",TRANSACTION_TIME \"Transaction Time\",SENDER_NAME \"Sender Name\",\r\n"
						+ "SENDER_ACCOUNT_NO \"Sender Account number\",SENDER_MMID \"Sender MMID\",SENDER_IFSC_CODE \"Sender IFSC\",SENDER_MOBILE_NO \"Sender Mobile Number\",\r\n"
						+ "BENEF_NAME \"Beneficiary Name\",BENEF_MMID \"Beneficiary MMID\",BENEF_ACCOUNT_NO \"Beneficiary Account number\",BENEF_MOBILE_NO \"Beneficiary Mobile Number\",\r\n"
						+ "BENEF_IFSC_CODE \"Beneficiary IFSC\",NARRATION \"Narration\",REPORT_TYPE \"Report Type\"\r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_TS3")) {
				sql = "SELECT REPORT_REF_NUM \"Report Reference Number\",RELATIONSHIP_FLG \"Relationship Flag\",TRANSACTION_ID \"Transaction ID\",TRANSACTION_TYPE \"Transaction Type\",\r\n"
						+ "TRANSACTION_DATE \"Transaction Date\",TRANSACTION_TIME \"Transaction Time\",TRANSACTION_AMOUNT \"Transaction Amount\", SENDER_NAME \"Sender Name\",\r\n"
						+ "SENDER_VPA \"Sender VPA\",SENDER_VPA_DECLARE \"Declaration (If Sender VPA is not available)\", SENDER_MOBILE_NO \"Sender Mobile Number\",\r\n"
						+ "SENDER_IFSC_CODE \"Sender IFSC\",SENDER_ACCOUNT_NO \"Sender Account Number\", BENEF_NAME \"Beneficiary Name\",BENEF_VPA \"Beneficiary VPA\",\r\n"
						+ "BENEF_MOBILE_NO \"Beneficiary Mobile Number\",BENEF_ACCOUNT_NO \"Beneficiary Account Number\",REPORT_TYPE \"Report Type\", BENEF_IFSC_CODE \"Beneficiary IFSC\",\r\n"
						+ "BENEF_AC_BRANCH_CODE \"Beneficiary Account Branch Code\",NARRATION \"Narration\",MERCHANT_CODE \"Merchant Category Code\",BENEF_ACCOUNT_TYPE \"Beneficiary Account Type\"\r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_GT1")) {
				sql = "select REPORT_REF_NUM \"Report Reference Number\",RELATIONSHIP_FLG \"Relationship Flag\", INSTRUMENT_TYPE \"Instrument Type\",\r\n"
						+ "INSTRUMENT_ISSUE_NAME \"Instrument Issuer Institute Name\",INSTRUMENT_ID \"Instrument ID\",TRANSACTION_DATE \"Transaction Date\",\r\n"
						+ "TRANSACTION_TIME \"Transaction Time\",\r\n"
						+ "AMOUNT_INR \"Amount INR\", AMOUNT_FC \"Amount FC\",FC_CODE \"FC Code\",\r\n"
						+ "decode(trim(PART_TRAN_TYPE),'I',BENEF_ACCOUNT_NO,'O',SENDER_ACCOUNT_NO) \"Account Number\",\r\n"
						+ "PURPOSE_OF_TXN \"Purpose of Transaction\",\r\n"
						+ "SENDER_NAME \"Sender Name\",SENDER_NAME_DECLARE \"Declaration (If Sender name is not available)\",SENDER_MOBILE_NO \"Sender Mobile Number\",\r\n"
						+ "SENDER_IFSC_BR_MICR \"Sender IFSC/Branch ID/MICR\",SENDER_IFSC_DECLARE \"Declaration (If Sender IFSC/Branch ID/MICR is not available)\",\r\n"
						+ "SENDER_ACCOUNT_NO \"Sender Account Number\", SENDER_ACCOUNT_DECLARE \"Declaration (If Sender Account Number is not available)\",\r\n"
						+ "BENEF_NAME \"Beneficiary Name\",BENEF_NAME_DECLARE \"Declaration (If Beneficiary Name is not available)\",BENEF_MOBILE_NO \"Beneficiary Mobile Number\",\r\n"
						+ "BENEF_IFSC_BR_MICR \"Beneficiary IFSC/Branch ID/MICR\", BENEF_IFSC_DECLARE \"Declaration (If Beneficiary IFSC/Branch ID/MICR is not available)\",\r\n"
						+ "BENEF_ACCOUNT_NO \"Beneficiary Account Number\",BENEF_ACCOUNT_DECLARE \"Declaration (If Beneficiary Account Number is not available)\",\r\n"
						+ "REPORT_TYPE \"Report Type\", TRANSACTION_ID \"Transaction ID\"\r\n" + "from " + tableName
						+ " where " + columnName + " in ";

			} else if (tableName.equalsIgnoreCase("STR_REG_GS1")) {
				sql = "select report_ref_num\"Report Reference Number\",KYC_SOURCE_OF_FUND \"KYC Source of Funds\",KYC_DEST_OF_FUND \"KYC Destination of Funds\r\n"
						+ "\",SUSP_DUE_TO \" Suspicion Due To\", SOURCE_OF_ALERT \"Source of Alert\",\r\n"
						+ "RED_FLG_INDICATOR \"Red Flag Indicator\",OTHER_RFI \"Other Red Flag Indicator\",Narration \"Narration\",TYPE_OF_SUSP \"Type of Offence Suspected\",OTHER_OFFENCE \"Other Offence Type\"\r\n"
						+ "from " + tableName + " where " + columnName + " in ";

			}

			System.out.println("SQL :" + sql);
			StringBuilder parameterBuilder = new StringBuilder();
			parameterBuilder.append(" (");
			for (int i = 0; i < reqIdList.size(); i++) {
				parameterBuilder.append("?");
				if (reqIdList.size() > i + 1) {
					parameterBuilder.append(",");
				}
			}
			parameterBuilder.append(")");
			pstmt = connection.prepareStatement(sql + parameterBuilder.toString());
			for (int i = 1; i < reqIdList.size() + 1; i++) {
				pstmt.setString(i, reqIdList.get(i - 1));
			}

			rs = pstmt.executeQuery();
			// System.out.println("ctrFilePath :" + ctrFilePath);
			File file = new File(ctrFilePath);
			boolean bool = file.mkdirs();

			if (bool) {
				System.out.println("Directory created successfully");
			}

			resultToCsv(rs, ctrFilePath + fileName + ".txt");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public List<String> getTableList(String reportType) {

		Statement stmtCheck = null;
		ResultSet rs = null;
		List<String> ctrList = new ArrayList<String>();
		String StrTables = "SELECT TABLE_NAME ,FILE_NAME ,COLUMN_NAME ,ACTIVE_FLG,FILE_TYPE  FROM PNBFINNET.FINNET_STR_TABLES where active_flg='Y' and column_name is not null";
		try (Connection connection = ConnectionFactory.makeConnectionAMLLive();) {
			stmtCheck = connection.createStatement();

			if (reportType.equalsIgnoreCase("STR")) {

				rs = stmtCheck.executeQuery(StrTables);
				while (rs.next()) {
					ctrList.add(
							rs.getString(1) + "~" + rs.getString(2) + "~" + rs.getString(3) + "~" + rs.getString(5));
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmtCheck != null) {
					stmtCheck.close();
					stmtCheck = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return ctrList;
	}

	public static void resultToCsv(ResultSet rs, String outputFile) throws SQLException, FileNotFoundException {
		PrintWriter csvWriter = null;
		csvWriter = new PrintWriter(new File(outputFile));

		try {
			ResultSetMetaData meta = rs.getMetaData();
			int numberOfColumns = meta.getColumnCount();
			String dataHeaders = meta.getColumnName(1);
			for (int i = 2; i < numberOfColumns + 1; i++) {
				dataHeaders += "|" + meta.getColumnName(i).replaceAll("\"", "\\\"");
			}
			csvWriter.print(dataHeaders);

			while (rs.next()) {
				String data = rs.getString(1) == null ? "" : rs.getString(1);
				String row = data.trim().replaceAll("\"", "\\\"");
				for (int i = 2; i < numberOfColumns + 1; i++) {
					if (rs.getString(i) != null) {
						row += "|" + rs.getString(i).trim().replaceAll("\"", "\\\"") + "";
					} else {
						row += "|";
					}
				}
				csvWriter.print("\n");
				csvWriter.print(row);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			csvWriter.close();
		}
	}

	public void updateReqStatusForStr(List<String> reqNoList) {
		PreparedStatement stmt = null;
		List<String> reqStrList = new ArrayList<>();
		String reqString = null;
		try (Connection connection = ConnectionFactory.makeConnectionAMLLive();) {

			for (String req : reqNoList) {
				reqStrList.add("'" + req + "'");
			}
			reqString = reqStrList.toString().replace("[", "").replace("]", "");

			String query = "UPDATE PNBFINNET.REG_REQUEST_DTLS SET DWLD_FLG='Y' WHERE REQUEST_ID IN (INKEYVALUE)";
			String mainQuery = query.replace(INKEYVALUE, reqString);

			stmt = connection.prepareStatement(mainQuery);
			stmt.executeUpdate();
			connection.commit();
			System.out.println("Data sucessfully Updated for Request ID = " + reqString);

		} catch (SQLException ex) {
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

	public static void main(String[] args) {
		Thread t = new Thread(new GenerateStrCSVFile());
		t.start();
	}

	/*
	 * @Override public void run() { generateRegCsvFile(); }
	 */

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			new GenerateStrCSVFile().generateRegCsvFile();

			try {
				Thread.sleep(1000 * 60);
				// Thread.sleep(Integer.parseInt(commonUtility.getAppParameterValue("THREAD",
				// "SLEEP")));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

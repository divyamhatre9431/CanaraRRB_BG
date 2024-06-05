package com.idbi.intech.iaml.EDPMSUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class EDPMSUploadThread implements Runnable {
	static EDPMSUploadThread processRun = new EDPMSUploadThread();

	public void insertEDPMSData() throws IOException, SQLException {
		FileInputStream inputStream = null;
		Scanner sc = null;
		Statement stmt = null;
		ResultSet rsFile = null;
		Connection conn = null;
		boolean fileFlg = true;
		
		try {
			try {
				Properties prop = new Properties();
				String dir = System.getProperty("user.dir");
				System.out.println("Directory Path :-" + dir);
				InputStream input = new FileInputStream(dir + "/aml-config.properties");
				prop.load(input);
				conn = ConnectionFactory.makeConnectionAMLLive();
				stmt = conn.createStatement();
				
				String fetchQuery = "select req_id,file_name,file_type from aml_edpms_idpms_file_upload where process_flg='N'";
				System.out.println(fetchQuery);
				rsFile = stmt.executeQuery(fetchQuery);
				while (rsFile.next()) {
					try {
						String updateStatus = "";
						String reqId = rsFile.getString(1);
						String fileName = rsFile.getString(2);
						String fileType = rsFile.getString(3);
						System.out.println("File Name " + fileName + " File Type :" + fileType);
						inputStream = new FileInputStream(prop.getProperty("EDPMSFILESOURCE") + fileName);
						String file = prop.getProperty("EDPMSFILESOURCE") + fileName;
						System.out.println("file :" + file);
						sc = new Scanner(inputStream, "UTF-8");

						if (sc.hasNextLine()) {
							sc.nextLine();
						}
						if (fileType.equalsIgnoreCase("IRM")) {
							syncData(fileType,"X");
							fileFlg = insertIRMData(file, reqId);
							syncData(fileType,"Y");
						} else if (fileType.equalsIgnoreCase("BOE")) {
							syncData(fileType,"X");
							fileFlg = insertBOEData(file, reqId);
							syncData(fileType,"Y");
						} else if (fileType.equalsIgnoreCase("PP")) {
							syncData(fileType,"X");
							fileFlg = insertPPData(file, reqId);
							syncData(fileType,"Y");
						} else if (fileType.equalsIgnoreCase("PAD")) {
							syncData(fileType,"X");
							fileFlg = insertPADData(file, reqId);
							syncData(fileType,"Y");
						} else if (fileType.equalsIgnoreCase("ORM")) {
							syncData(fileType,"X");
							fileFlg = insertORMData(file, reqId);
							syncData(fileType,"Y");
						}

						System.out.println("File Upload Flg :" + fileFlg);
						if (fileFlg) {
							updateStatus = "Y";
							// System.out.println("here ..."+reqId);
							PreparedStatement updateStatusStmt = conn.prepareStatement(
									"update aml_edpms_idpms_file_upload set update_time=sysdate,process_flg=? where req_id=?");
							updateStatusStmt.setString(1, updateStatus);
							updateStatusStmt.setString(2, reqId);
							updateStatusStmt.executeUpdate();
							System.out.println("Upload Done");
							// con.commit();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			/*
			 * if (sc.ioException() != null) { throw sc.ioException(); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
	}

	public boolean insertBOEData(String fileName, String reqId) {
		File file = new File(fileName);
		Connection conn = null;
		PreparedStatement pstatement = null;
		int j = 0;
		boolean flg = true;
		try {
			// Coming For Insert BOE Data
			System.out.println("Coming For Insert BOE Data");
			conn = ConnectionFactory.makeConnectionAMLLive();
			String updateStatus = "P";
			PreparedStatement updateStatusStmt = conn.prepareStatement(
					"update aml_edpms_idpms_file_upload set update_time=sysdate,process_flg=? where req_id=?");
			updateStatusStmt.setString(1, updateStatus);
			updateStatusStmt.setString(2, reqId);
			updateStatusStmt.executeUpdate();
			
			String query = "INSERT INTO aml_boe_temp VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstatement = (PreparedStatement) conn.prepareStatement(query);

			DataFormatter formatter = new DataFormatter();
			int cnt = 0;
			Workbook workbook = null;
			workbook = WorkbookFactory.create(file);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (Row row : sheet) {
					cnt++;
					System.out.println(cnt);
					try
					{
						if (row.getRowNum() != 0) {

							String ADCode = formatter.formatCellValue(row.getCell(0)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(0));
							String NewAdCode = formatter.formatCellValue(row.getCell(1)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(1));
							String SOLID = formatter.formatCellValue(row.getCell(2)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(2));
							String SOLNAME = formatter.formatCellValue(row.getCell(3)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(3));
							String CIRCLE = formatter.formatCellValue(row.getCell(4)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(4));
							String ZONE = formatter.formatCellValue(row.getCell(5)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(5));
							String BANK = formatter.formatCellValue(row.getCell(6)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(6));
							String Pivot = formatter.formatCellValue(row.getCell(7)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(7));
							String PortOfDischarge = formatter.formatCellValue(row.getCell(8)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(8));
							String ImportAgency = formatter.formatCellValue(row.getCell(9)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(9));
							String BOENumber = formatter.formatCellValue(row.getCell(10)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(10));
							String BOEDate = formatter.formatCellValue(row.getCell(11)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(11));
							String day = formatter.formatCellValue(row.getCell(12)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(12));
							String month = formatter.formatCellValue(row.getCell(13)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(13));
							String year = formatter.formatCellValue(row.getCell(14)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(14));
							String IECode = formatter.formatCellValue(row.getCell(15)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(15));
							String IEName = formatter.formatCellValue(row.getCell(16)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(16));
							String IEAddress = formatter.formatCellValue(row.getCell(17)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(17));
							String IEPAN = formatter.formatCellValue(row.getCell(18)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(18));
							String ShippingPort = formatter.formatCellValue(row.getCell(19)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(19));
							String BillAmountINR = formatter.formatCellValue(row.getCell(20)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(20));
							String SettledBillAmountINR = formatter.formatCellValue(row.getCell(21)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(21));
							String MasterStatus = formatter.formatCellValue(row.getCell(22)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(22));

							pstatement.setString(1, ADCode);
							pstatement.setString(2, NewAdCode);
							pstatement.setString(3, SOLID);
							pstatement.setString(4, SOLNAME);
							pstatement.setString(5, CIRCLE);
							pstatement.setString(6, ZONE);
							pstatement.setString(7, BANK);
							pstatement.setString(8, Pivot);
							pstatement.setString(9, PortOfDischarge);
							pstatement.setString(10, ImportAgency);
							pstatement.setString(11, BOENumber);
							pstatement.setString(12, BOEDate);
							pstatement.setString(13, day);
							pstatement.setString(14, month);
							pstatement.setString(15, year);
							pstatement.setString(16, IECode);
							pstatement.setString(17, IEName);
							pstatement.setString(18, IEAddress);
							pstatement.setString(19, IEPAN);
							pstatement.setString(20, ShippingPort);
							pstatement.setString(21, BillAmountINR);
							pstatement.setString(22, SettledBillAmountINR);
							pstatement.setString(23, MasterStatus);

							j = pstatement.executeUpdate();

						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			System.out.println("J :" + j);
			if (j >= 1) {

				flg = true;
				// System.out.println("Flg =" + flg);
				conn.commit();
			} else {
				flg = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flg = false;
		} finally {
			try {

				if (pstatement != null) {
					pstatement.close();
					pstatement = null;
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

	public boolean insertData(String fileName, String reqId) {
		File file = new File(fileName);
		Connection conn = null;
		PreparedStatement pstm = null;
		int j = 0;
		boolean flg = true;
		try {
			// Coming for insert
			System.out.println("Coming for insert");
			conn = ConnectionFactory.makeConnectionAMLLive();
			
			String updateStatus = "P";
			PreparedStatement updateStatusStmt = conn.prepareStatement(
					"update aml_edpms_idpms_file_upload set update_time=sysdate,process_flg=? where req_id=?");
			updateStatusStmt.setString(1, updateStatus);
			updateStatusStmt.setString(2, reqId);
			updateStatusStmt.executeUpdate();

			String query = "INSERT INTO test_edpms_idpms VALUES(?,?,?,?)";

			pstm = (PreparedStatement) conn.prepareStatement(query);

			DataFormatter formatter = new DataFormatter();
			Workbook workbook = null;
			workbook = WorkbookFactory.create(file);

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (Row row : sheet) {
					if (row.getRowNum() != 0) {

						String ClientAdd = formatter.formatCellValue(row.getCell(0)).equals("null") ? ""
								: formatter.formatCellValue(row.getCell(0));
						String Page = formatter.formatCellValue(row.getCell(1)).equals("null") ? ""
								: formatter.formatCellValue(row.getCell(1));
						String AccessDate = formatter.formatCellValue(row.getCell(2)).equals("null") ? ""
								: formatter.formatCellValue(row.getCell(2));
						String ProcessTime = formatter.formatCellValue(row.getCell(3)).equals("null") ? ""
								: formatter.formatCellValue(row.getCell(3));
						pstm.setString(1, ClientAdd);
						pstm.setString(2, Page);
						pstm.setString(3, AccessDate);
						pstm.setString(4, ProcessTime);
						j = pstm.executeUpdate();
					}
				}
			}
			// System.out.println("J :" + j);
			if (j >= 1) {

				flg = true;
				// System.out.println("Flg =" + flg);
				conn.commit();
			} else {
				flg = false;
			}
		} catch (Exception e) {
			flg = false;
		} finally {
			try {

				if (pstm != null) {
					pstm.close();
					pstm = null;
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

	public boolean insertIRMData(String fileName, String reqId) {
		File file = new File(fileName);
		Connection conn = null;
		PreparedStatement pstatement = null;
		int j = 0;
		boolean flg = true;
		try {
			// Coming For Insert IRM Data
			System.out.println("Coming For Insert IRM Data");
			conn = ConnectionFactory.makeConnectionAMLLive();
			
			String updateStatus = "P";
			PreparedStatement updateStatusStmt = conn.prepareStatement(
					"update aml_edpms_idpms_file_upload set update_time=sysdate,process_flg=? where req_id=?");
			updateStatusStmt.setString(1, updateStatus);
			updateStatusStmt.setString(2, reqId);
			updateStatusStmt.executeUpdate();
			conn.commit();

			String query = "INSERT INTO aml_irm_temp VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			pstatement = (PreparedStatement) conn.prepareStatement(query);
			DataFormatter formatter = new DataFormatter();
			int cnt = 0;
			Workbook workbook = null;
			workbook = WorkbookFactory.create(file);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (Row row : sheet) {
					cnt++;
					System.out.println(cnt);
					try
					{
						if (row.getRowNum() != 0) {

							String REMITTANCE_AD_CODE = formatter.formatCellValue(row.getCell(0)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(0));
							String ADCODE_2 = formatter.formatCellValue(row.getCell(1)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(1));
							String SOL_ID = formatter.formatCellValue(row.getCell(2)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(2));
							String SOL_NAME = formatter.formatCellValue(row.getCell(3)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(3));
							String CIRCLE = formatter.formatCellValue(row.getCell(4)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(4));
							String ZONE = formatter.formatCellValue(row.getCell(5)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(5));
							String BANK = formatter.formatCellValue(row.getCell(6)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(6));
							String PIVOT = formatter.formatCellValue(row.getCell(7)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(7));
							String IRM_NUMBER = formatter.formatCellValue(row.getCell(8)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(8));
							String DOE = formatter.formatCellValue(row.getCell(9)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(9));
							String BANK_NAME = formatter.formatCellValue(row.getCell(10)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(10));
							String REMITTANCE_DATE = formatter.formatCellValue(row.getCell(11)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(11));
							String D = formatter.formatCellValue(row.getCell(12)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(12));
							String M = formatter.formatCellValue(row.getCell(13)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(13));
							String Y = formatter.formatCellValue(row.getCell(14)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(14));
							String IE_CODE = formatter.formatCellValue(row.getCell(15)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(15));
							String IE_NAME = formatter.formatCellValue(row.getCell(16)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(16));
							String CURRENCY = formatter.formatCellValue(row.getCell(17)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(17));
							String REMITTANCE_AMOUNT = formatter.formatCellValue(row.getCell(18)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(18));
							String AMOUNT_UTILIZED = formatter.formatCellValue(row.getCell(19)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(19));
							String AMOUNT_UNUTILIZED = formatter.formatCellValue(row.getCell(20)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(20));
							String REMITTER_NAME = formatter.formatCellValue(row.getCell(21)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(21));
							String REMITTER_COUNTRY = formatter.formatCellValue(row.getCell(22)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(22));
							String PURPOSE_OF_REMITTANCE = formatter.formatCellValue(row.getCell(23)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(23));
							String EXPECTED_UTILIZATION_DATE = formatter.formatCellValue(row.getCell(24)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(24));
							String IMT_NO = formatter.formatCellValue(row.getCell(25)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(25));

							pstatement.setString(1, REMITTANCE_AD_CODE);
							pstatement.setString(2, ADCODE_2);
							pstatement.setString(3, SOL_ID);
							pstatement.setString(4, SOL_NAME);
							pstatement.setString(5, CIRCLE);
							pstatement.setString(6, ZONE);
							pstatement.setString(7, BANK);
							pstatement.setString(8, PIVOT);
							pstatement.setString(9, IRM_NUMBER);
							pstatement.setString(10, DOE);
							pstatement.setString(11, BANK_NAME);
							pstatement.setString(12, REMITTANCE_DATE);
							pstatement.setString(13, D);
							pstatement.setString(14, M);
							pstatement.setString(15, Y);
							pstatement.setString(16, IE_CODE);
							pstatement.setString(17, IE_NAME);
							pstatement.setString(18, CURRENCY);
							pstatement.setString(19, REMITTANCE_AMOUNT);
							pstatement.setString(20, AMOUNT_UTILIZED);
							pstatement.setString(21, AMOUNT_UNUTILIZED);
							pstatement.setString(22, REMITTER_NAME);
							pstatement.setString(23, REMITTER_COUNTRY);
							pstatement.setString(24, PURPOSE_OF_REMITTANCE);
							pstatement.setString(25, EXPECTED_UTILIZATION_DATE);
							pstatement.setString(26, IMT_NO);

							j = pstatement.executeUpdate();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			System.out.println("J :" + j);
			if (j >= 1) {

				flg = true;
				// System.out.println("Flg =" + flg);
				conn.commit();
			} else {
				flg = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flg = false;
		} finally {
			try {

				if (pstatement != null) {
					pstatement.close();
					pstatement = null;
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
	
	
	public boolean insertORMData(String fileName, String reqId) {
		File file = new File(fileName);
		Connection conn = null;
		PreparedStatement pstatement = null;
		int j = 0;
		boolean flg = true;
		try {
			// Coming For Insert ORM Data
			System.out.println("Coming For Insert ORM Data");
			
			conn = ConnectionFactory.makeConnectionAMLLive();
			String updateStatus = "P";
			PreparedStatement updateStatusStmt = conn.prepareStatement(
					"update aml_edpms_idpms_file_upload set update_time=sysdate,process_flg=? where req_id=?");
			updateStatusStmt.setString(1, updateStatus);
			updateStatusStmt.setString(2, reqId);
			updateStatusStmt.executeUpdate();

			String query = "INSERT INTO aml_orm_temp VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstatement = (PreparedStatement) conn.prepareStatement(query);
			DataFormatter formatter = new DataFormatter();
			int cnt = 0;
			Workbook workbook = null;
			workbook = WorkbookFactory.create(file);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (Row row : sheet) {
					cnt++;
					System.out.println(cnt);
					try
					{
						if (row.getRowNum() != 0) {

							String AD_CODE = formatter.formatCellValue(row.getCell(0)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(0));
							String NEW_ADCODE = formatter.formatCellValue(row.getCell(1)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(1));
							String SOL_ID = formatter.formatCellValue(row.getCell(2)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(2));
							String SOL_NAME = formatter.formatCellValue(row.getCell(3)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(3));
							String CIRCLE = formatter.formatCellValue(row.getCell(4)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(4));
							String ZONE = formatter.formatCellValue(row.getCell(5)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(5));
							String BANK = formatter.formatCellValue(row.getCell(6)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(6));
							String PIVOT = formatter.formatCellValue(row.getCell(7)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(7));
							String ORM_NUMBER = formatter.formatCellValue(row.getCell(8)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(8));
							String DOE_STATUS = formatter.formatCellValue(row.getCell(9)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(9));
							String ORM_DATE = formatter.formatCellValue(row.getCell(10)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(10));
							String DAY = formatter.formatCellValue(row.getCell(11)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(11));
							String MONTH = formatter.formatCellValue(row.getCell(12)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(12));
							String YEAR = formatter.formatCellValue(row.getCell(13)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(13));
							String ORM_CURRENCY = formatter.formatCellValue(row.getCell(14)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(14));
							String ORM_AMOUNT = formatter.formatCellValue(row.getCell(15)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(15));
							String UNUTILIZED_AMOUNT = formatter.formatCellValue(row.getCell(16)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(16));
							String UTILIZED_AMOUNT = formatter.formatCellValue(row.getCell(17)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(17));
							String IE_CODE = formatter.formatCellValue(row.getCell(18)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(18));
							String IE_NAME = formatter.formatCellValue(row.getCell(19)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(19));
							String IE_ADDRESS = formatter.formatCellValue(row.getCell(20)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(20));
							String IE_PAN = formatter.formatCellValue(row.getCell(21)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(21));
							String BENIFICIARY_NAME = formatter.formatCellValue(row.getCell(22)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(22));
							String BENIFICIARY_ACCOUNT = formatter.formatCellValue(row.getCell(23)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(23));
							String SWIFT_CODE = formatter.formatCellValue(row.getCell(24)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(24));
							String PURPOSE_CODE = formatter.formatCellValue(row.getCell(25)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(25));
							String PAYMENT_TERMS = formatter.formatCellValue(row.getCell(26)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(26));
							String REMARKS = formatter.formatCellValue(row.getCell(27)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(27));
							String ORM_STATUS = formatter.formatCellValue(row.getCell(28)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(28));

							pstatement.setString(1, AD_CODE);
							pstatement.setString(2, NEW_ADCODE);
							pstatement.setString(3, SOL_ID);
							pstatement.setString(4, SOL_NAME);
							pstatement.setString(5, CIRCLE);
							pstatement.setString(6, ZONE);
							pstatement.setString(7, BANK);
							pstatement.setString(8, PIVOT);
							pstatement.setString(9, ORM_NUMBER);
							pstatement.setString(10, DOE_STATUS);
							pstatement.setString(11, ORM_DATE);
							pstatement.setString(12, DAY);
							pstatement.setString(13, MONTH);
							pstatement.setString(14, YEAR);
							pstatement.setString(15, ORM_CURRENCY);
							pstatement.setString(16, ORM_AMOUNT);
							pstatement.setString(17, UNUTILIZED_AMOUNT);
							pstatement.setString(18, UTILIZED_AMOUNT);
							pstatement.setString(19, IE_CODE);
							pstatement.setString(20, IE_NAME);
							pstatement.setString(21, IE_ADDRESS);
							pstatement.setString(22, IE_PAN);
							pstatement.setString(23, BENIFICIARY_NAME);
							pstatement.setString(24, BENIFICIARY_ACCOUNT);
							pstatement.setString(25, SWIFT_CODE);
							pstatement.setString(26, PURPOSE_CODE);
							pstatement.setString(27, PAYMENT_TERMS);
							pstatement.setString(28, REMARKS);
							pstatement.setString(29, ORM_STATUS);

							j = pstatement.executeUpdate();

						}
					
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			System.out.println("J :" + j);
			if (j >= 1) {

				flg = true;
				// System.out.println("Flg =" + flg);
				conn.commit();
			} else {
				flg = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flg = false;
		} finally {
			try {

				if (pstatement != null) {
					pstatement.close();
					pstatement = null;
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

	public boolean insertPADData(String fileName, String reqId) {
		File file = new File(fileName);
		Connection conn = null;
		PreparedStatement pstatement = null;
		int j = 0;
		boolean flg = true;
		try {
			// Coming For Insert PAD Data
			System.out.println("Coming For Insert PAD Data");
			conn = ConnectionFactory.makeConnectionAMLLive();
			
			String updateStatus = "P";
			PreparedStatement updateStatusStmt = conn.prepareStatement(
					"update aml_edpms_idpms_file_upload set update_time=sysdate,process_flg=? where req_id=?");
			updateStatusStmt.setString(1, updateStatus);
			updateStatusStmt.setString(2, reqId);
			updateStatusStmt.executeUpdate();

			String query = "INSERT INTO aml_pad_temp VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			// String query = "INSERT INTO aml_boe (AD_Code,New_Ad_Code,SOL_ID)
			// VALUES(?,?,?)";
			pstatement = (PreparedStatement) conn.prepareStatement(query);

			DataFormatter formatter = new DataFormatter();
			int cnt = 0;
			Workbook workbook = null;
			workbook = WorkbookFactory.create(file);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (Row row : sheet) {
					cnt++;
					System.out.println(cnt);
					try
					{
						if (row.getRowNum() != 0) {

							String AD_CODE = formatter.formatCellValue(row.getCell(0)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(0));
							String ADCODE_2 = formatter.formatCellValue(row.getCell(1)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(1));
							String SOL_ID = formatter.formatCellValue(row.getCell(2)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(2));
							String SOL_NAME = formatter.formatCellValue(row.getCell(3)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(3));
							String CIRCLE = formatter.formatCellValue(row.getCell(4)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(4));
							String ZONE = formatter.formatCellValue(row.getCell(5)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(5));
							String BANK = formatter.formatCellValue(row.getCell(6)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(6));
							String PIVOT = formatter.formatCellValue(row.getCell(7)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(7));
							String EXPORT_AGENCY = formatter.formatCellValue(row.getCell(8)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(8));
							String BANK_NAME = formatter.formatCellValue(row.getCell(9)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(9));
							String AD_BILL_NO = formatter.formatCellValue(row.getCell(10)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(10));
							String SHIPPING_BILL_NO = formatter.formatCellValue(row.getCell(11)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(11));
							String DOE = formatter.formatCellValue(row.getCell(12)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(12));
							String SHIPPING_BILL_DATE = formatter.formatCellValue(row.getCell(13)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(13));
							String SHIPPING_BILL_DAY = formatter.formatCellValue(row.getCell(14)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(14));
							String MONTH = formatter.formatCellValue(row.getCell(15)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(15));
							String YEAR = formatter.formatCellValue(row.getCell(16)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(16));
							String FORM_NO = formatter.formatCellValue(row.getCell(17)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(17));
							String PORT_CODE = formatter.formatCellValue(row.getCell(18)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(18));
							String IE_CODE = formatter.formatCellValue(row.getCell(19)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(19));
							String IE_NAME = formatter.formatCellValue(row.getCell(20)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(20));
							String IE_ADDRESS = formatter.formatCellValue(row.getCell(21)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(21));
							String AD_NAME = formatter.formatCellValue(row.getCell(22)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(22));
							String LEO_DATE = formatter.formatCellValue(row.getCell(23)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(23));
							String PROCESSING_STATUS = formatter.formatCellValue(row.getCell(24)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(24));
							String BUYER_NAME = formatter.formatCellValue(row.getCell(25)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(25));
							String BUYER_COUNTRY = formatter.formatCellValue(row.getCell(26)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(26));
							String REMITTER_NAME = formatter.formatCellValue(row.getCell(27)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(27));
							String REMITTER_COUNTRY = formatter.formatCellValue(row.getCell(28)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(28));
							String INVOICE_NO = formatter.formatCellValue(row.getCell(29)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(29));
							String INVOICE_SERIAL_NO = formatter.formatCellValue(row.getCell(30)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(30));
							String INVOICE_DATE = formatter.formatCellValue(row.getCell(31)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(31));
							String IRM_NUMBER = formatter.formatCellValue(row.getCell(32)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(32));
							String FIRC_NUMBER = formatter.formatCellValue(row.getCell(33)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(33));
							String REMITTANCE_AD_CODE = formatter.formatCellValue(row.getCell(34)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(34));
							String PAYMENT_TYPE = formatter.formatCellValue(row.getCell(35)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(35));
							String FOB_CURRENCY = formatter.formatCellValue(row.getCell(36)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(36));
							String FOBVALUE = formatter.formatCellValue(row.getCell(37)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(37));
							String REALIZEDFOBCURRENCY = formatter.formatCellValue(row.getCell(38)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(38));
							String REALIZEDFOBVALUE = formatter.formatCellValue(row.getCell(39)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(39));
							String EQUIVALENTFOBVALUE = formatter.formatCellValue(row.getCell(40)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(40));
							String FREIGHTCURRENCY = formatter.formatCellValue(row.getCell(41)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(41));
							String FREIGHTVALUE = formatter.formatCellValue(row.getCell(42)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(42));
							String REALIZEDFREIGHTCURRENCY = formatter.formatCellValue(row.getCell(43)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(43));
							String REALIZEDFREIGHTVALUE = formatter.formatCellValue(row.getCell(44)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(44));
							String EQUIVALENTFREIGHTVALUE = formatter.formatCellValue(row.getCell(45)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(45));
							String INSURANCECURRENCY = formatter.formatCellValue(row.getCell(46)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(46));
							String INSURANCEVALUE = formatter.formatCellValue(row.getCell(47)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(47));
							String REALIZEDINSURANCECURRENCY = formatter.formatCellValue(row.getCell(48)).equals("null")
									? ""
									: formatter.formatCellValue(row.getCell(48));
							String REALIZEDINSURANCEVALUE = formatter.formatCellValue(row.getCell(49)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(49));
							String EQUIVALENTINSURANCEVALUE = formatter.formatCellValue(row.getCell(50)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(50));
							String BANKING_CHARGES = formatter.formatCellValue(row.getCell(51)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(51));
							String EXPECTED_PAYMENT_LAST_DATE = formatter.formatCellValue(row.getCell(52)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(52));
							String ADDED_DATE = formatter.formatCellValue(row.getCell(53)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(53));
							String SB_IEC = formatter.formatCellValue(row.getCell(54)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(54));

							pstatement.setString(1, AD_CODE);
							pstatement.setString(2, ADCODE_2);
							pstatement.setString(3, SOL_ID);
							pstatement.setString(4, SOL_NAME);
							pstatement.setString(5, CIRCLE);
							pstatement.setString(6, ZONE);
							pstatement.setString(7, BANK);
							pstatement.setString(8, PIVOT);
							pstatement.setString(9, EXPORT_AGENCY);
							pstatement.setString(10, BANK_NAME);
							pstatement.setString(11, AD_BILL_NO);
							pstatement.setString(12, SHIPPING_BILL_NO);
							pstatement.setString(13, DOE);
							pstatement.setString(14, SHIPPING_BILL_DATE);
							pstatement.setString(15, SHIPPING_BILL_DAY);
							pstatement.setString(16, MONTH);
							pstatement.setString(17, YEAR);
							pstatement.setString(18, FORM_NO);
							pstatement.setString(19, PORT_CODE);
							pstatement.setString(20, IE_CODE);
							pstatement.setString(21, IE_NAME);
							pstatement.setString(22, IE_ADDRESS);
							pstatement.setString(23, AD_NAME);
							pstatement.setString(24, LEO_DATE);
							pstatement.setString(25, PROCESSING_STATUS);
							pstatement.setString(26, BUYER_NAME);
							pstatement.setString(27, BUYER_COUNTRY);
							pstatement.setString(28, REMITTER_NAME);
							pstatement.setString(29, REMITTER_COUNTRY);
							pstatement.setString(30, INVOICE_NO);
							pstatement.setString(31, INVOICE_SERIAL_NO);
							pstatement.setString(32, INVOICE_DATE);
							pstatement.setString(33, IRM_NUMBER);
							pstatement.setString(34, FIRC_NUMBER);
							pstatement.setString(35, REMITTANCE_AD_CODE);
							pstatement.setString(36, PAYMENT_TYPE);
							pstatement.setString(37, FOB_CURRENCY);
							pstatement.setString(38, FOBVALUE);
							pstatement.setString(39, REALIZEDFOBCURRENCY);
							pstatement.setString(40, REALIZEDFOBVALUE);
							pstatement.setString(41, EQUIVALENTFOBVALUE);
							pstatement.setString(42, FREIGHTCURRENCY);
							pstatement.setString(43, FREIGHTVALUE);
							pstatement.setString(44, REALIZEDFREIGHTCURRENCY);
							pstatement.setString(45, REALIZEDFREIGHTVALUE);
							pstatement.setString(46, EQUIVALENTFREIGHTVALUE);
							pstatement.setString(47, INSURANCECURRENCY);
							pstatement.setString(48, INSURANCEVALUE);
							pstatement.setString(49, REALIZEDINSURANCECURRENCY);
							pstatement.setString(50, REALIZEDINSURANCEVALUE);
							pstatement.setString(51, EQUIVALENTINSURANCEVALUE);
							pstatement.setString(52, BANKING_CHARGES);
							pstatement.setString(53, EXPECTED_PAYMENT_LAST_DATE);
							pstatement.setString(54, ADDED_DATE);
							pstatement.setString(55, SB_IEC);

							j = pstatement.executeUpdate();

						}
					
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			System.out.println("J :" + j);
			if (j >= 1) {

				flg = true;
				// System.out.println("Flg =" + flg);
				conn.commit();
			} else {
				flg = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flg = false;
		} finally {
			try {

				if (pstatement != null) {
					pstatement.close();
					pstatement = null;
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

	public boolean insertPPData(String fileName, String reqId) {
		File file = new File(fileName);
		Connection conn = null;
		PreparedStatement pstatement = null;
		int j = 0;
		boolean flg = true;
		try {
			// Coming For Insert PP Data
			System.out.println("Coming For Insert PP Data");
			conn = ConnectionFactory.makeConnectionAMLLive();
			String updateStatus = "P";
			PreparedStatement updateStatusStmt = conn.prepareStatement(
					"update aml_edpms_idpms_file_upload set update_time=sysdate,process_flg=? where req_id=?");
			updateStatusStmt.setString(1, updateStatus);
			updateStatusStmt.setString(2, reqId);
			updateStatusStmt.executeUpdate();

			String query = "INSERT INTO aml_pp_temp VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			// String query = "INSERT INTO aml_boe (AD_Code,New_Ad_Code,SOL_ID)
			// VALUES(?,?,?)";
			pstatement = (PreparedStatement) conn.prepareStatement(query);

			DataFormatter formatter = new DataFormatter();
			int cnt = 0;
			Workbook workbook = null;
			workbook = WorkbookFactory.create(file);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (Row row : sheet) {
					cnt++;
					System.out.println(cnt);
					try
					{
						if (row.getRowNum() != 0) {

							String AD_CODE = formatter.formatCellValue(row.getCell(0)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(0));
							String ADCODE_2 = formatter.formatCellValue(row.getCell(1)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(1));
							String SOL_ID = formatter.formatCellValue(row.getCell(2)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(2));
							String SOL_NAME = formatter.formatCellValue(row.getCell(3)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(3));
							String CIRCLE = formatter.formatCellValue(row.getCell(4)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(4));
							String ZONE = formatter.formatCellValue(row.getCell(5)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(5));
							String BANK = formatter.formatCellValue(row.getCell(6)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(6));
							String PIVOT = formatter.formatCellValue(row.getCell(7)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(7));
							String EXPORT_AGENCY = formatter.formatCellValue(row.getCell(8)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(8));
							String BANK_NAME = formatter.formatCellValue(row.getCell(9)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(9));
							String AD_BILL_NO = formatter.formatCellValue(row.getCell(10)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(10));
							String SHIPPING_BILL_NO = formatter.formatCellValue(row.getCell(11)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(11));
							String DOE = formatter.formatCellValue(row.getCell(12)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(12));
							String SHIPPING_BILL_ATE = formatter.formatCellValue(row.getCell(13)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(13));
							String SHIPPING_BILL_DAY = formatter.formatCellValue(row.getCell(14)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(14));
							String M = formatter.formatCellValue(row.getCell(15)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(15));
							String Y = formatter.formatCellValue(row.getCell(16)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(16));
							String FORM_NO = formatter.formatCellValue(row.getCell(17)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(17));
							String PORT_CODE = formatter.formatCellValue(row.getCell(18)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(18));
							String IE_CODE = formatter.formatCellValue(row.getCell(19)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(19));
							String IE_NAME = formatter.formatCellValue(row.getCell(20)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(20));
							String IE_ADDRESS = formatter.formatCellValue(row.getCell(21)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(21));
							String AD_NAME = formatter.formatCellValue(row.getCell(22)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(22));
							String LEO_DATE = formatter.formatCellValue(row.getCell(23)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(23));
							String PROCESSING_STATUS = formatter.formatCellValue(row.getCell(24)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(24));
							String BUYER_NAME = formatter.formatCellValue(row.getCell(25)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(25));
							String BUYER_COUNTRY = formatter.formatCellValue(row.getCell(26)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(26));
							String REMITTER_NAME = formatter.formatCellValue(row.getCell(27)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(27));
							String REMITTER_COUNTRY = formatter.formatCellValue(row.getCell(28)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(28));
							String INVOICE_NO = formatter.formatCellValue(row.getCell(29)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(29));
							String INVOICE_SERIAL_NO = formatter.formatCellValue(row.getCell(30)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(30));
							String INVOICE_DATE = formatter.formatCellValue(row.getCell(31)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(31));
							String IRM_NUMBER = formatter.formatCellValue(row.getCell(32)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(32));
							String FIRC_NUMBER = formatter.formatCellValue(row.getCell(33)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(33));
							String REMITTANCE_AD_CODE = formatter.formatCellValue(row.getCell(34)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(34));
							String PAYMENT_TYPE = formatter.formatCellValue(row.getCell(35)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(35));
							String FOB_CURRENCY = formatter.formatCellValue(row.getCell(36));
							String FOBVALUE = formatter.formatCellValue(row.getCell(37));
							String REALIZEDFOBCURRENCY = formatter.formatCellValue(row.getCell(38)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(38));
							String REALIZEDFOBVALUE = formatter.formatCellValue(row.getCell(39)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(39));
							String EQUIVALENTFOBVALUE = formatter.formatCellValue(row.getCell(40)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(40));
							String FREIGHTCURRENCY = formatter.formatCellValue(row.getCell(41)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(41));
							String FREIGHTVALUE = formatter.formatCellValue(row.getCell(42)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(42));
							String REALIZEDFREIGHTCURRENCY = formatter.formatCellValue(row.getCell(43)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(43));
							String REALIZEDFREIGHTVALUE = formatter.formatCellValue(row.getCell(44)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(44));
							String EQUIVALENTFREIGHTVALUE = formatter.formatCellValue(row.getCell(45)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(45));
							String INSURANCECURRENCY = formatter.formatCellValue(row.getCell(46)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(46));
							String INSURANCEVALUE = formatter.formatCellValue(row.getCell(47)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(47));
							String REALIZEDINSURANCECURRENCY = formatter.formatCellValue(row.getCell(48)).equals("null")
									? ""
									: formatter.formatCellValue(row.getCell(48));
							String REALIZEDINSURANCEVALUE = formatter.formatCellValue(row.getCell(49)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(49));
							String EQUIVALENTINSURANCEVALUE = formatter.formatCellValue(row.getCell(50)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(50));
							String BANKING_CHARGES = formatter.formatCellValue(row.getCell(51)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(51));
							String EXPECTED_PAYMENT_LAST_DATE = formatter.formatCellValue(row.getCell(52)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(52));
							String ADDED_DATE = formatter.formatCellValue(row.getCell(53)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(53));
							String SB_IEC = formatter.formatCellValue(row.getCell(54)).equals("null") ? ""
									: formatter.formatCellValue(row.getCell(54));

							pstatement.setString(1, AD_CODE);
							pstatement.setString(2, ADCODE_2);
							pstatement.setString(3, SOL_ID);
							pstatement.setString(4, SOL_NAME);
							pstatement.setString(5, CIRCLE);
							pstatement.setString(6, ZONE);
							pstatement.setString(7, BANK);
							pstatement.setString(8, PIVOT);
							pstatement.setString(9, EXPORT_AGENCY);
							pstatement.setString(10, BANK_NAME);
							pstatement.setString(11, AD_BILL_NO);
							pstatement.setString(12, SHIPPING_BILL_NO);
							pstatement.setString(13, DOE);
							pstatement.setString(14, SHIPPING_BILL_ATE);
							pstatement.setString(15, SHIPPING_BILL_DAY);
							pstatement.setString(16, M);
							pstatement.setString(17, Y);
							pstatement.setString(18, FORM_NO);
							pstatement.setString(19, PORT_CODE);
							pstatement.setString(20, IE_CODE);
							pstatement.setString(21, IE_NAME);
							pstatement.setString(22, IE_ADDRESS);
							pstatement.setString(23, AD_NAME);
							pstatement.setString(24, LEO_DATE);
							pstatement.setString(25, PROCESSING_STATUS);
							pstatement.setString(26, BUYER_NAME);
							pstatement.setString(27, BUYER_COUNTRY);
							pstatement.setString(28, REMITTER_NAME);
							pstatement.setString(29, REMITTER_COUNTRY);
							pstatement.setString(30, INVOICE_NO);
							pstatement.setString(31, INVOICE_SERIAL_NO);
							pstatement.setString(32, INVOICE_DATE);
							pstatement.setString(33, IRM_NUMBER);
							pstatement.setString(34, FIRC_NUMBER);
							pstatement.setString(35, REMITTANCE_AD_CODE);
							pstatement.setString(36, PAYMENT_TYPE);
							pstatement.setString(37, FOB_CURRENCY);
							pstatement.setString(38, FOBVALUE);
							pstatement.setString(39, REALIZEDFOBCURRENCY);
							pstatement.setString(40, REALIZEDFOBVALUE);
							pstatement.setString(41, EQUIVALENTFOBVALUE);
							pstatement.setString(42, FREIGHTCURRENCY);
							pstatement.setString(43, FREIGHTVALUE);
							pstatement.setString(44, REALIZEDFREIGHTCURRENCY);
							pstatement.setString(45, REALIZEDFREIGHTVALUE);
							pstatement.setString(46, EQUIVALENTFREIGHTVALUE);
							pstatement.setString(47, INSURANCECURRENCY);
							pstatement.setString(48, INSURANCEVALUE);
							pstatement.setString(49, REALIZEDINSURANCECURRENCY);
							pstatement.setString(50, REALIZEDINSURANCEVALUE);
							pstatement.setString(51, EQUIVALENTINSURANCEVALUE);
							pstatement.setString(52, BANKING_CHARGES);
							pstatement.setString(53, EXPECTED_PAYMENT_LAST_DATE);
							pstatement.setString(54, ADDED_DATE);
							pstatement.setString(55, SB_IEC);

							j = pstatement.executeUpdate();

						}
					
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			System.out.println("J :" + j);
			if (j >= 1) {

				flg = true;
				// System.out.println("Flg =" + flg);
				conn.commit();
			} else {
				flg = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flg = false;
		} finally {
			try {

				if (pstatement != null) {
					pstatement.close();
					pstatement = null;
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
	
	private void syncData(String fileType,String reqType) 
	{
		Connection conn = null;
		CallableStatement stmt = null;
		
		try {
			conn = ConnectionFactory.makeConnectionAMLLive();
			
			if(reqType.equalsIgnoreCase("X"))
			{
				stmt = conn.prepareCall("call sync_edpms_idpms_data('"+ fileType+"')");
			}
			else
			{
				stmt = conn.prepareCall("call sync_"+fileType+"_data()");
			}
			stmt.execute();

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
			processRun.start();
		} else {
			processRun.stop();
		}
	}

	public void start() {
		try {
			Thread t = new Thread(new EDPMSUploadThread());
			t.start();
		} catch (MissingResourceException e) {
			System.out.println("Exception executing the process");
		}
	}

	public void stop() {

	}

	@Override
	public void run() {
		while (true) {
			try {
				insertEDPMSData();
			} catch (IOException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) throws IOException, SQLException {
		Thread t = new Thread(new EDPMSUploadThread());
		t.start();
	}
}
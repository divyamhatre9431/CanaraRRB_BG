package com.idbi.intech.iaml.internal.watchlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

public class FCRAInternalWatchListUploadThread implements Runnable {

	static FCRAInternalWatchListUploadThread processRun = new FCRAInternalWatchListUploadThread();

	public void insertInternalWatchListData() throws IOException, SQLException {
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
				conn = ConnectionFactory.makeConnectionNSAMLLive();
				stmt = conn.createStatement();

				String fetchQuery = "select req_id,file_name from AML_FCRA_INTERNAL_WATCHLIST_FILE_UPLOAD where process_flg='N'";
				System.out.println(fetchQuery);
				rsFile = stmt.executeQuery(fetchQuery);

				while (rsFile.next()) {
					try {
						String updateStatus = "";
						String reqId = rsFile.getString(1);
						String fileName = rsFile.getString(2);

						System.out.println("File Name " + fileName);
						System.out.println("Req Id" + reqId);

						inputStream = new FileInputStream(
								prop.getProperty("FCRAInternalWatchListUpload") + reqId + ".xlsx");
						String file = prop.getProperty("FCRAInternalWatchListUpload") + fileName;

						System.out.println("file :" + file);
						sc = new Scanner(inputStream, "UTF-8");

						if (sc.hasNextLine()) {
							sc.nextLine();
						}

						fileFlg = insertData(prop.getProperty("FCRAInternalWatchListUpload") + reqId + ".xlsx", reqId);

						System.out.println("File Upload Flg :" + fileFlg);
						if (fileFlg) {
							updateStatus = "Y";

							PreparedStatement updateStatusStmt = conn.prepareStatement(
									"update AML_FCRA_INTERNAL_WATCHLIST_FILE_UPLOAD set update_time=sysdate,process_flg=? where req_id=?");
							updateStatusStmt.setString(1, updateStatus);
							updateStatusStmt.setString(2, reqId);
							updateStatusStmt.executeUpdate();

						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

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

	public boolean insertData(String fileName, String reqId) {
		File file = new File(fileName);
		Connection conn = null;
		ResultSet rs1 = null;
		PreparedStatement pstatement = null;
		Statement stmt = null;
		int j = 0;
		boolean flg = true;
		String data_id = "";
		try {
			// Coming For Insert Data
			System.out.println("Coming For Insert Data");

			conn = ConnectionFactory.makeConnectionNSAMLLive();

			String updateStatus = "P";
			PreparedStatement updateStatusStmt = conn.prepareStatement(
					"update AML_FCRA_INTERNAL_WATCHLIST_FILE_UPLOAD set update_time=sysdate,process_flg=? where req_id=?");
			updateStatusStmt.setString(1, updateStatus);
			updateStatusStmt.setString(2, reqId);
			updateStatusStmt.executeUpdate();

			String query = "INSERT INTO FCRA_Internal_WatchList_Data VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
			pstatement = (PreparedStatement) conn.prepareStatement(query);
			stmt = conn.createStatement();

			DataFormatter formatter = new DataFormatter();
			int cnt = 0;
			String mainDataId = "";
			boolean AkaDataList = false;
			Workbook workbook = null;
			workbook = WorkbookFactory.create(file);

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);

				for (Row row : sheet) {
					cnt++;
					System.out.println(cnt);
					try {
						if (row.getRowNum() != 0) {

							if (formatter.formatCellValue(row.getCell(1)) != null) {
								if (formatter.formatCellValue(row.getCell(1)).contains("AKA")) {
									mainDataId = getDataIdByLastUploadDateTimestamp();
									//System.out.println("mainDataId" + mainDataId);
									String AkaNames = formatter.formatCellValue(row.getCell(2));
									String ListType = formatter.formatCellValue(row.getCell(0));
									String EntityType = formatter.formatCellValue(row.getCell(3)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(3));
									AkaDataList = insertDataForAkaList(mainDataId, ListType, EntityType, AkaNames);

								}

								else {

								
									rs1 = stmt.executeQuery("select 'DATA'||FCRAID_SEQ.nextval from dual");
									while (rs1.next()) {
										data_id = rs1.getString(1);
									}

									String list_type = formatter.formatCellValue(row.getCell(0)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(0));
									String srno = formatter.formatCellValue(row.getCell(1)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(1));
									String name = formatter.formatCellValue(row.getCell(2)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(2));
									String type_of_foreign_donor_entity_type = formatter.formatCellValue(row.getCell(3))
											.equals("null") ? "" : formatter.formatCellValue(row.getCell(3));
									String country = formatter.formatCellValue(row.getCell(4)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(4));
									String father_name = formatter.formatCellValue(row.getCell(5)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(5));
									String do_wo = formatter.formatCellValue(row.getCell(6)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(6));
									String dob = formatter.formatCellValue(row.getCell(7)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(7));
									String account_no = formatter.formatCellValue(row.getCell(8)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(8));
									String address = formatter.formatCellValue(row.getCell(9)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(9));
									String voter_id = formatter.formatCellValue(row.getCell(10)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(10));
									String aadhaar = formatter.formatCellValue(row.getCell(11)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(11));
									String passport = formatter.formatCellValue(row.getCell(12)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(12));
									String dl = formatter.formatCellValue(row.getCell(13)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(13));
									String pan = formatter.formatCellValue(row.getCell(14)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(14));
									String others_id1 = formatter.formatCellValue(row.getCell(15)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(15));
									String others_id2 = formatter.formatCellValue(row.getCell(16)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(16));
									String mobile = formatter.formatCellValue(row.getCell(17)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(17));
									String email = formatter.formatCellValue(row.getCell(18)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(18));
									String gst_number = formatter.formatCellValue(row.getCell(19)).equals("null") ? ""
											: formatter.formatCellValue(row.getCell(19));
									/*
									 * String list_received_date = formatter.formatCellValue(row.getCell(20))
									 * .equals("null") ? "" : formatter.formatCellValue(row.getCell(20));
									 */
									String list_received_date = formatter.formatCellValue(row.getCell(20))
											== null ? "-" : formatter.formatCellValue(row.getCell(20));
									
									System.out.println("list_received_date"+list_received_date.length());
									
								 if(list_received_date.length()>1) { 
									pstatement.setString(1, list_type);
									pstatement.setString(2, srno);
									pstatement.setString(3, name.toUpperCase());
									pstatement.setString(4, type_of_foreign_donor_entity_type);
									pstatement.setString(5, country);
									pstatement.setString(6, father_name);
									pstatement.setString(7, do_wo);
									pstatement.setString(8, dob);
									pstatement.setString(9, account_no);
									pstatement.setString(10, address);
									pstatement.setString(11, voter_id);
									pstatement.setString(12, aadhaar);
									pstatement.setString(13, passport);
									pstatement.setString(14, dl);
									pstatement.setString(15, pan);
									pstatement.setString(16, others_id1);
									pstatement.setString(17, others_id2);
									pstatement.setString(18, mobile);
									pstatement.setString(19, email);
									pstatement.setString(20, gst_number);
									pstatement.setString(21, list_received_date);
									pstatement.setString(22, data_id);
									j = pstatement.executeUpdate();
									if (j >= 1) {
										flg = true;
										conn.commit();
									} else {
										flg = false;
									}
									
								 }
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("J :" + j);
			

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

	public String getDataIdByLastUploadDateTimestamp() throws SQLException {
		Statement stmt = null;
		ResultSet rs3 = null;
		String dataId = "";
		Connection conn = null;
	
		try {
			conn = ConnectionFactory.makeConnectionNSAMLLive();
			stmt = conn.createStatement();
			String query = "select data_id from FCRA_Internal_WatchList_Data where upload_date = (select max(upload_date) from FCRA_Internal_WatchList_Data)";
			rs3 = stmt.executeQuery(query);
			while (rs3.next()) {
				dataId = rs3.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}

				if (rs3 != null) {
					rs3.close();
					rs3 = null;
				}

				if (conn != null) {
					conn.close();
					conn = null;
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return dataId;

	}

	public boolean insertDataForAkaList(String mainDataId, String ListType, String EntityType, String AkaNames) {
		Boolean flg = false;

		Connection conn = null;

		PreparedStatement pstmt_sdn_alert = null;

		try {
			conn = ConnectionFactory.makeConnectionNSAMLLive();
			// Insert into Alert master table
			pstmt_sdn_alert = conn.prepareStatement("insert into aml_wl_aka_list values(?,?,?,?,sysdate)");

			// Insert into ns screen activity table

			pstmt_sdn_alert.setString(1, mainDataId);
			pstmt_sdn_alert.setString(2, ListType);
			pstmt_sdn_alert.setString(3, EntityType);
			pstmt_sdn_alert.setString(4, AkaNames);

			int i = pstmt_sdn_alert.executeUpdate();

			conn.commit();
			if (i > 0) {
				flg = true;
			}
		} // catch (JsonProcessingException e) {
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pstmt_sdn_alert != null) {
					pstmt_sdn_alert.close();
					pstmt_sdn_alert = null;
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return flg;
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
			Thread t = new Thread(new FCRAInternalWatchListUploadThread());
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
				insertInternalWatchListData();
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
		Thread t = new Thread(new FCRAInternalWatchListUploadThread());
		t.start();
	}
}
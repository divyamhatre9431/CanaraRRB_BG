package com.idbi.intech.iaml.screening;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class ScanNameScreenData implements Runnable {

	// private static final Logger logger =
	// Logger.getLogger(NameScreenThread.class);
	private static Connection connection = null;

	public static void makeConnection() throws SQLException {
		connection = ConnectionFactory.makeConnectionAMLLive();
	}

	@Override
	public void run() {
		
		while(true)
		{
			try {
				scanCustomer();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	public ResponseJson scanId(String id, String idType) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String entId = "";
		Boolean flg = false;
		String entryCategory = "";
		ResponseJson responseJson = new ResponseJson();
		try {

			String checkSQL = "select ent_id,entrycategory from aml_ln_entities ";
			checkSQL += "where passportid ='" + id + "' or NATIONALID ='" + id + "' or OTHERID ='" + id
					+ "' and ENTRYCATEGORY='SANCTION LIST' ";
			stmt = connection.prepareStatement(checkSQL);
			stmt.setMaxRows(1);
			rs = stmt.executeQuery();

			if (rs != null) {
				if (rs.next()) {
					entId = rs.getString(1);
					entryCategory = rs.getString(2);
					flg = true;
				}
			}
			stmt.close();

			if (flg) {
				responseJson.setScanStatus("E");
				responseJson.setRejectReason("1_" + entId + "_" + entryCategory);
			} else {
				responseJson.setScanStatus("S");
				responseJson.setRejectReason("0");
			}
			responseJson.setErrorFlg(false);
		} catch (Exception ex) {
			responseJson.setErrorFlg(true);
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
		return responseJson;
	}

	public void scanCustomer() {
		ResponseJson responseJson = new ResponseJson();
		PreparedStatement pstmtLog = null;
		Statement stmtCust = null;
		ResultSet rsCust = null;
		InputStream inputStream = null;
		String fileName = "";
		String fileReqData = "";
		String respString = "";
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			inputStream = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(inputStream);
			stmtCust = connection.createStatement();
			String fuzzyMatchNameMax = amlProp.getProperty("fuzzyMatchNameMax");
			String rowNum = amlProp.getProperty("rowNum");
			String fetchCust = "Select file_name ,file_req_data,file_priority from "
					+ "(Select file_name ,file_req_data,file_priority from name_screen_file_log where file_process_flg='N' order by file_priority asc)"
					+ " where rownum< " + rowNum;
			// System.out.println(fetchCust);
			rsCust = stmtCust.executeQuery(fetchCust);
			pstmtLog = connection.prepareStatement(
					"update name_screen_file_log set file_res_data =? ,file_res_time=sysdate,file_process_flg=? where file_name=?");
			
			while (rsCust.next()) {
				String respFlg = "";
				fileName = rsCust.getString(1);
				fileReqData = rsCust.getString(2);
				System.out.println("fileName : "+fileName+"-"+fileReqData);
				String[] data = fileReqData.split("\\|", -1);
				RequestJson requestJson = new RequestJson();
				responseJson = new ResponseJson();
				requestJson.setUniqueSessionKey(data[0]);
				requestJson.setId(data[1]);
				requestJson.setIdType(data[2]);
				requestJson.setName(data[3].replaceAll("'", ""));
				requestJson.setAddrType1(data[4]);
				requestJson.setAddr1(data[5]);
				requestJson.setAddrType2(data[6]);
				requestJson.setAddr2(data[7]);
				requestJson.setAddrType3(data[8]);
				requestJson.setAddr3(data[9]);
				if (!requestJson.getId().trim().equals("")) {
					responseJson = scanId(requestJson.getId(), "O");
				} else {
					responseJson.setScanStatus("S");
				}
				if ("S".equalsIgnoreCase(responseJson.getScanStatus())) {
					responseJson = scanNameAddr(requestJson, fuzzyMatchNameMax);
				}
				if (responseJson.getErrorFlg()) {
					respFlg = "X";
				} else {
					respFlg = "S";
					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					String strDate = formatter.format(date);
					responseJson.setTimeStamp(strDate);

					respString = requestJson.getUniqueSessionKey() + "|" + requestJson.getId() + "|"
							+ requestJson.getIdType() + "|" + requestJson.getName() + "|" + requestJson.getAddrType1()
							+ "|" + requestJson.getAddr1() + "|" + requestJson.getAddrType2() + "|"
							+ requestJson.getAddr2() + "|" + requestJson.getAddrType3() + "|" + requestJson.getAddr3()
							+ "|" + responseJson.getScanStatus() + "|" + responseJson.getRejectReason() + "|"
							+ responseJson.getTimeStamp();
				}

				pstmtLog.setString(1, respString);
				pstmtLog.setString(2, respFlg);
				pstmtLog.setString(3, fileName);
				pstmtLog.executeUpdate();
				connection.commit();
			}
			connection.commit();
			System.out.println("Completed");
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}

				if (stmtCust != null) {
					stmtCust.close();
					stmtCust = null;
				}
				if (rsCust != null) {
					rsCust.close();
					rsCust = null;
				}
				if (pstmtLog != null) {
					pstmtLog.close();
					pstmtLog = null;
				}

			} catch (SQLException | IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public ResponseJson scanNameAddr(RequestJson requestJson, String fuzzyThreshold) {
		Statement stmtAddr = null;
		Statement stmtLn = null;
		ResultSet rsLn = null, rsAddr = null;
		String entId = "";
		String entryCategory = "";
		ResponseJson responseJson = new ResponseJson();
		HashMap<String, Double> getAddrMap = new HashMap<>();

		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			int nameLength = requestJson.getName().length();
			int minLength = Math.round(nameLength * 85 / 100);
			int maxLength = Math.round(nameLength * 120 / 100);
			String checkSQL = "Select ent_id,name,entrycategory from aml_ln_entities where ENTRYCATEGORY='SANCTION LIST' "
					+ " and length(name) between " + minLength + " and " + maxLength + "";
			stmtLn = connection.createStatement();
			rsLn = stmtLn.executeQuery(checkSQL);
			double resultName = 0.0;
			double resultAddr = 0.0;

			while (rsLn.next()) {
				try {
					System.out.println("entName : "+entId+" - "+rsLn.getString(2).toUpperCase());
					entId = rsLn.getString(1);
					FuzzySearch.tokenSetRatio(requestJson.getName().replace(",", "").toUpperCase(),
							rsLn.getString(2).toUpperCase());
					entryCategory = rsLn.getString(3);

					String checkAddrSQL = "Select ent_id,nvl(to_char(address),'-') from aml_ln_entitiesaddresses where ent_id='"
							+ entId
							+ "' and ent_id in (select ent_id from aml_ln_entities where ENTRYCATEGORY='SANCTION LIST') ";
					stmtAddr = connection.prepareStatement(checkAddrSQL);
					rsAddr = stmtAddr.executeQuery(checkAddrSQL);
					while (rsAddr.next()) {
						if (requestJson.getAddr1().length() > 2) {
							resultAddr = FuzzySearch.tokenSetRatio(
									requestJson.getAddr1().replace(",", "").toUpperCase(),
									rsAddr.getString(2).toUpperCase());
							getAddrMap.put(requestJson.getAddr1(), resultAddr);
						}

						if (requestJson.getAddr2().length() > 2) {
							resultAddr = FuzzySearch.tokenSetRatio(
									requestJson.getAddr2().replace(",", "").toUpperCase(),
									rsAddr.getString(2).toUpperCase());
							getAddrMap.put(requestJson.getAddr2(), resultAddr);
						}

						if (requestJson.getAddr3().length() > 2) {
							resultAddr = FuzzySearch.tokenSetRatio(
									requestJson.getAddr3().replace(",", "").toUpperCase(),
									rsAddr.getString(2).toUpperCase());
							getAddrMap.put(requestJson.getAddr3(), resultAddr);
						}
					}
				} finally {
					if (stmtAddr != null) {
						stmtAddr.close();
						stmtAddr = null;
					}
					if (rsAddr != null) {
						rsAddr.close();
						rsAddr = null;
					}
				}
			}
			if (!getAddrMap.values().isEmpty()) {
				double maxFuzzyInMap = (Collections.max(getAddrMap.values()));
				responseJson.setScanStatus("E");
				responseJson.setRejectReason("2_" + entId + "_" + entryCategory + "_" + requestJson.getName() + " ("
						+ resultName + ")_" + requestJson.getAddr1() + " (" + maxFuzzyInMap + ")");
			} else if (resultName >= Double.valueOf(fuzzyThreshold)) {
				responseJson.setScanStatus("E");
				responseJson.setRejectReason(
						"2_" + entId + "_" + entryCategory + "_" + requestJson.getName() + " (" + resultName + ")");
			} else {
				responseJson.setScanStatus("S");
				responseJson.setRejectReason("0");
			}
			responseJson.setErrorFlg(false);
		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.setErrorFlg(true);
		} finally {
			try {
				if (stmtLn != null) {
					stmtLn.close();
					stmtLn = null;
				}
				if (rsLn != null) {
					rsLn.close();
					rsLn = null;
				}
				if (stmtAddr != null) {
					stmtAddr.close();
					stmtAddr = null;
				}
				if (rsAddr != null) {
					rsAddr.close();
					rsAddr = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return responseJson;
	}

	public ResponseJson scanName(RequestJson requestJson) {
		PreparedStatement stmt = null;
		Statement stmtName = null;
		ResultSet rs = null, rsName = null;
		String entId = "";
		String entryCategory = "";
		Boolean flg = false;
		ResponseJson responseJson = new ResponseJson();
		try {

			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			String fuzzyMatchNameMax = amlProp.getProperty("fuzzyMatchNameMax");

			/*
			 * String checkName =
			 * "Select ent_id,entrycategory from aml_ln_entities where to_char(name) ='" +
			 * requestJson.getName().toUpperCase().trim() +
			 * "' and ENTRYCATEGORY='SANCTION LIST' ";
			 */
			String checkNameSQL = "select utl_match.edit_distance_similarity(name,'"
					+ requestJson.getName().replace(",", "").toUpperCase()
					+ "') match_percentage,ent_id,entrycategory from aml_ln_entities where ENTRYCATEGORY='SANCTION LIST' "
					+ "and utl_match.edit_distance_similarity(name,'"
					+ requestJson.getName().replace(",", "").toUpperCase() + "') > " + fuzzyMatchNameMax + " ";
			stmtName = connection.createStatement();
			rsName = stmtName.executeQuery(checkNameSQL);

			while (rsName.next()) {
				entId = rsName.getString(1);
				entryCategory = rsName.getString(2);
				flg = true;
			}

			if (flg) {
				responseJson.setScanStatus("E");
				responseJson.setRejectReason("3_" + entId + "_" + entryCategory + "_" + requestJson.getName());
			} else {
				responseJson.setScanStatus("S");
				responseJson.setRejectReason("0");
			}
			responseJson.setErrorFlg(false);
		} catch (Exception ex) {
			responseJson.setErrorFlg(true);
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
				if (stmtName != null) {
					stmtName.close();
					stmtName = null;
				}
				if (rsName != null) {
					rsName.close();
					rsName = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return responseJson;
	}

	public static void main(String args[]) {
		try {
			makeConnection();
			Thread threadObj = new Thread(new ScanNameScreenData());
			threadObj.start();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

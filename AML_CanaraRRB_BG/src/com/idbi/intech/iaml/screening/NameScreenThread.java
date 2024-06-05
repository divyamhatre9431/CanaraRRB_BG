package com.idbi.intech.iaml.screening;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

import com.idbi.intech.iaml.factory.ConnectionFactory;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class NameScreenThread implements Runnable {
	
	//private static final Logger logger = Logger.getLogger(NameScreenThread.class);
	private static Connection connection = null;
	
	public static void makeConnection() throws SQLException {
		connection = ConnectionFactory.makeConnectionAMLLive();
	}

	@Override
	public void run() {
		while (true) {
			try {
				scanCustomer();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
			checkSQL += "where passportid ='" + id + "' or NATIONALID ='" + id + "' or OTHERID ='" + id + "' and ENTRYCATEGORY='SANCTION LIST' ";
			//System.out.println("checkSQL : "+checkSQL);
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
				//System.out.println("entId : "+entId+"-"+entryCategory);
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
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is;
			is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			String inputFilePath = amlProp.getProperty("swiftRequestPath");
			String processedPath = amlProp.getProperty("swiftProcessed");
			String responsePath = amlProp.getProperty("swiftResponsePath");
			File reqFiles = new File(inputFilePath);
			String fname[] = reqFiles.list();
			//pstmtLog = connection.prepareStatement("insert into NAME_SCREEN_LOG values (?,?,?,?,sysdate,?)");
			for (String reqFile : fname) 
			{
				//logger.info("File name : "+reqFile);
				System.out.println("File name : "+reqFile);
				List<String> responseString = new ArrayList<String>();
				String fileNameWithOutExt = FilenameUtils.removeExtension(reqFile);
				//RequestJson requestJson = requestReader(inputFilePath + reqFile);
				List<RequestJson> requestJsonList = requestReaderList(inputFilePath + reqFile);
				
				for(RequestJson requestJson : requestJsonList)
				{
					//logger.info("Request Object : "+requestJson.toString());
					System.out.println("Request Object : "+requestJson.toString());
					
					if (!requestJson.getId().trim().equals("")) {
						responseJson = scanId(requestJson.getId(), "O");
					} else {
						responseJson.setScanStatus("S");
					}

					if ("S".equalsIgnoreCase(responseJson.getScanStatus())) {
						responseJson = scanNameAddr(requestJson);
						/*if ("S".equalsIgnoreCase(responseJson.getScanStatus())) {
							responseJson = scanName(requestJson);
						}*/
					}
					if (!responseJson.getErrorFlg()) {
						/*File srcFile = new File(inputFilePath + reqFile);
						File destDir = new File(processedPath + reqFile);
						Path src = srcFile.toPath();
						Files.move(src, destDir.toPath(), StandardCopyOption.REPLACE_EXISTING);*/
						Date date = new Date();
						SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						String strDate = formatter.format(date);
						responseJson.setTimeStamp(strDate);

						//Writer w = new FileWriter(responsePath + fileNameWithOutExt + ".RES");
						String respString = requestJson.getUniqueSessionKey() + "|" + requestJson.getId() + "|"
								+ requestJson.getIdType() + "|" + requestJson.getName() + "|" + requestJson.getAddrType1()
								+ "|" + requestJson.getAddr1() + "|" + requestJson.getAddrType2() + "|"
								+ requestJson.getAddr2() + "|" + requestJson.getAddrType3() + "|" + requestJson.getAddr3()
								+ "|" + responseJson.getScanStatus() + "|" + responseJson.getRejectReason() + "|"
								+ responseJson.getTimeStamp();
						//w.write(respString);
						//w.close();
						responseString.add(respString);
						
						/*pstmtLog.setString(1, requestJson.getUniqueSessionKey());
						pstmtLog.setString(2, responseJson.getScanStatus());
						pstmtLog.setString(3, requestJson.getReqString());
						pstmtLog.setString(4, respString);
						pstmtLog.setString(5, reqFile);
						pstmtLog.executeUpdate();*/
					}
					
					//logger.info("Response Object : "+responseJson.toString());
					System.out.println("Response Object : "+responseJson.toString());
				}
				
				Writer w = new FileWriter(responsePath + fileNameWithOutExt + ".RES");
				boolean processFlg = false;
				
				for(String response : responseString)
				{
					if(response.length() > 10)
					{
						processFlg = true;
						w.write(response+"\r\n");
					}
				}
				w.close();
				/*System.out.println("size:"+responseString.size());*/
				/*if(responseString != null && responseString.size() > 15)*/
				if(processFlg)
				{   
					File srcFile = new File(inputFilePath + reqFile);
					File destDir = new File(processedPath + reqFile);
					Path src = srcFile.toPath();
					Files.move(src, destDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}
			connection.commit();
		} catch (IOException | SQLException e) {
			//logger.info("Exception occured");
			e.printStackTrace();
		}

	}

	public RequestJson requestReader(String filePath) throws IOException {
		String lineText = null;
		BufferedReader lineReader = null;
		File file = null;
		RequestJson requestJson = new RequestJson();
		try {
			file = new File(filePath);
			String[] data = null;
			lineReader = new BufferedReader(new FileReader(file));
			while ((lineText = lineReader.readLine()) != null) {
				try {
					String noSpaceLineText = lineText.trim();
					data = noSpaceLineText.split("\\|", -1);
					requestJson.setReqString(lineText);
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
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (lineReader != null) {
				lineReader.close();
			}
		}
		return requestJson;
	}
	
	
	public List<RequestJson> requestReaderList(String filePath) throws IOException {
		String lineText = null;
		BufferedReader lineReader = null;
		File file = null;
		List<RequestJson> requestList = new ArrayList<RequestJson>();
		try {
			file = new File(filePath);
			String[] data = null;
			lineReader = new BufferedReader(new FileReader(file));
			
			while ((lineText = lineReader.readLine()) != null) {
				try {
					String noSpaceLineText = lineText.trim();
					data = noSpaceLineText.split("\\|", -1);
					RequestJson requestJson = new RequestJson();
					requestJson.setReqString(lineText);
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
					requestList.add(requestJson);
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (lineReader != null) {
				lineReader.close();
			}
		}
		return requestList;
	}
	

	public ResponseJson scanNameAddr(RequestJson requestJson) {
		PreparedStatement stmt = null;
		Statement stmtAddr = null;
		ResultSet rs = null, rsAddr = null;
		String entId = "";
		Boolean flg = false;
		String entryCategory = "";
		ResponseJson responseJson = new ResponseJson();
		HashMap<String,Double> getAddrMap = new HashMap<String,Double>();
		
		try {
			/*String checkAddr = "Select ent_id from aml_ln_entitiesaddresses where  to_char(address) ='"
					+ requestJson.getAddr1().toUpperCase() + "'";
			stmtAddr = connection.createStatement();
			rsAddr = stmtAddr.executeQuery(checkAddr);
			double resultName = 0.0;
			double resultAddr = 0.0;
			
			while (rsAddr.next()) {
				entId = rsAddr.getString(1);
				String checkSQL = "select ent_id,name,entrycategory from aml_ln_entities ";
				checkSQL += "where ent_id ='" + entId + "'";
				stmt = connection.prepareStatement(checkSQL);
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					//double result = 0.0;
					resultName = FuzzySearch.tokenSetRatio(requestJson.getName().replace(",", "").toUpperCase(),
							rs.getString(2).toUpperCase());
					entryCategory = rs.getString(3);
					if (resultName == 100) {
						flg = true;
					}
				}
			}*/
			
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			String fuzzyMatchNameMax = amlProp.getProperty("fuzzyMatchNameMax");
			
			double resultName = 0.0;
			double resultAddr = 0.0;

			//String checkNameSQL = "select ent_id,name,entrycategory from aml_ln_entities where ENTRYCATEGORY='SANCTION LIST' ";
			String checkNameSQL = "select utl_match.edit_distance_similarity(name,'"+requestJson.getName().replace(",", "").toUpperCase()+"') match_percentage,ent_id,entrycategory from aml_ln_entities where ENTRYCATEGORY='SANCTION LIST' "
					+ "and utl_match.edit_distance_similarity(name,'"+requestJson.getName().replace(",", "").toUpperCase()+"') > "+fuzzyMatchNameMax+" ";
			stmt = connection.prepareStatement(checkNameSQL);
			rs = stmt.executeQuery();

			while (rs.next()) {
				/*resultName = FuzzySearch.tokenSetRatio(requestJson.getName().replace(",", "").toUpperCase(),
						rs.getString(2).toUpperCase());
				resultName = rs.getDouble(1);
				entId = rs.getString(2);
				entryCategory = rs.getString(3); */
				
				resultName = rs.getDouble(1);
				
				//if (resultName == 100) {
					flg = true;
					String checkAddrSQL = "Select ent_id,to_char(address) from aml_ln_entitiesaddresses where ent_id='"+entId+"' and ent_id in (select ent_id from aml_ln_entities where ENTRYCATEGORY='SANCTION LIST') ";
					stmt = connection.prepareStatement(checkAddrSQL);
					rsAddr = stmt.executeQuery();
					
					while(rsAddr.next())
					{
						if(requestJson.getAddr1().length() > 2)
						{
							resultAddr = FuzzySearch.tokenSetRatio(requestJson.getAddr1().replace(",", "").toUpperCase(),
									rs.getString(2).toUpperCase());
							getAddrMap.put(requestJson.getAddr1(),resultAddr);
						}
						
						if(requestJson.getAddr2().length() > 2)
						{
							resultAddr = FuzzySearch.tokenSetRatio(requestJson.getAddr2().replace(",", "").toUpperCase(),
									rs.getString(2).toUpperCase());
							getAddrMap.put(requestJson.getAddr2(),resultAddr);
						}
						
						if(requestJson.getAddr3().length() > 2)
						{
							resultAddr = FuzzySearch.tokenSetRatio(requestJson.getAddr3().replace(",", "").toUpperCase(),
									rs.getString(2).toUpperCase());
							getAddrMap.put(requestJson.getAddr3(),resultAddr);
						}
					}
				//}
				
			}
			
			rs.close();
			stmt.close();
			
			if(getAddrMap.values().size() > 0) {
				double maxFuzzyInMap = (Collections.max(getAddrMap.values()));
				responseJson.setScanStatus("E");
				responseJson.setRejectReason("2_" + entId + "_" + entryCategory + "_" + requestJson.getName() + " ("+resultName+")_"
						+ requestJson.getAddr1()+" ("+maxFuzzyInMap+")");
			} else if(resultName >= Double.valueOf(fuzzyMatchNameMax)) {
				responseJson.setScanStatus("E");
				responseJson.setRejectReason("2_" + entId + "_" + entryCategory + "_" + requestJson.getName() + " ("+resultName+")");
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
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
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
			
			/*String checkName = "Select ent_id,entrycategory from aml_ln_entities where to_char(name) ='"
					+ requestJson.getName().toUpperCase().trim() + "' and ENTRYCATEGORY='SANCTION LIST' ";*/
			String checkNameSQL = "select utl_match.edit_distance_similarity(name,'"+requestJson.getName().replace(",", "").toUpperCase()+"') match_percentage,ent_id,entrycategory from aml_ln_entities where ENTRYCATEGORY='SANCTION LIST' "
					+ "and utl_match.edit_distance_similarity(name,'"+requestJson.getName().replace(",", "").toUpperCase()+"') > "+fuzzyMatchNameMax+" ";
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
			Thread threadObj = new Thread(new NameScreenThread());
			threadObj.start();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

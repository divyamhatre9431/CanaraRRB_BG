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

public class ScanningThread implements Runnable {
	//private static Connection connection = null;
	String reqFile = "";
	String inputFilePath = "";
	String processedPath = "";
	String responsePath = "";
	//Statement stmt = null;

	
	/*public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}*/
	
	public ScanningThread(String reqFile,String inputFilePath,String processedPath,String responsePath) {
		this.reqFile = reqFile;
		this.inputFilePath = inputFilePath;
		this.processedPath = processedPath;
		this.responsePath = responsePath;
	}
	
	

	public void executeProc(String reqFile,String inputFilePath,String processedPath,String responsePath) 
	{
		ResponseJson responseJson = new ResponseJson();
		PreparedStatement pstmtLog = null;
		Connection connection = null;
		
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			//logger.info("File name : "+reqFile);
			System.out.println("File name : "+reqFile);
			List<String> responseString = new ArrayList<String>();
			String fileNameWithOutExt = FilenameUtils.removeExtension(reqFile);
			//RequestJson requestJson = requestReader(inputFilePath + reqFile);
			List<RequestJson> requestJsonList = requestReaderList(inputFilePath + reqFile);
			pstmtLog = connection.prepareStatement("insert into NAME_SCREEN_LOG values (?,?,?,?,sysdate,?)");
			
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
					Date date = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					String strDate = formatter.format(date);
					responseJson.setTimeStamp(strDate);

					String respString = requestJson.getUniqueSessionKey() + "|" + requestJson.getId() + "|"
							+ requestJson.getIdType() + "|" + requestJson.getName() + "|" + requestJson.getAddrType1()
							+ "|" + requestJson.getAddr1() + "|" + requestJson.getAddrType2() + "|"
							+ requestJson.getAddr2() + "|" + requestJson.getAddrType3() + "|" + requestJson.getAddr3()
							+ "|" + responseJson.getScanStatus() + "|" + responseJson.getRejectReason() + "|"
							+ responseJson.getTimeStamp();

					responseString.add(respString);
					
					pstmtLog.setString(1, requestJson.getUniqueSessionKey());
					pstmtLog.setString(2, responseJson.getScanStatus());
					pstmtLog.setString(3, requestJson.getReqString());
					pstmtLog.setString(4, respString);
					pstmtLog.setString(5, reqFile);
					pstmtLog.executeUpdate();
					
					connection.commit();
					connection.close();
				}
				
				//logger.info("Response Object : "+responseJson.toString());
				System.out.println("Response Object : "+responseJson.toString());
			}
			
			/*Writer w = new FileWriter(responsePath + fileNameWithOutExt + ".RES");
			boolean processFlg = false;
			
			for(String response : responseString)
			{
				if(response.length() > 10)
				{
					processFlg = true;
					w.write(response+"\r\n");
				}
			}
			w.close();*/
			
			boolean processFlg = false;
			for(String response : responseString)
			{
				if(response.contains("|E|") || response.contains("|S|"))
				{
					processFlg = true;
				}
			}
			
			if(processFlg)
			{
				Writer w = new FileWriter(responsePath + fileNameWithOutExt + ".RES");
				for(String response : responseString)
				{
					w.write(response+"\r\n");
				}
				w.close();
			}
			
			if(processFlg)
			{   
				File srcFile = new File(inputFilePath + reqFile);
				File destDir = new File(processedPath + reqFile);
				Path src = srcFile.toPath();
				Files.move(src, destDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmtLog != null) {
					pstmtLog.close();
					pstmtLog = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
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
		Connection connection = null;
		PreparedStatement stmt = null;
		Statement stmtAddr = null;
		ResultSet rs = null, rsAddr = null;
		String entId = "";
		Boolean flg = false;
		String entryCategory = "";
		ResponseJson responseJson = new ResponseJson();
		HashMap<String,Double> getAddrMap = new HashMap<String,Double>();
		
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
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
			connection.close();
			
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
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return responseJson;
	}
	
	
	public ResponseJson scanId(String id, String idType) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String entId = "";
		Boolean flg = false;
		String entryCategory = "";
		ResponseJson responseJson = new ResponseJson();
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
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
			connection.close();
			
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
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return responseJson;
	}
	

	@Override
	public void run() {
		executeProc(reqFile,inputFilePath,processedPath,responsePath);
	}

}

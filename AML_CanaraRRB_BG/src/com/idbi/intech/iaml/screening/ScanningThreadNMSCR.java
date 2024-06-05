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

public class ScanningThreadNMSCR implements Runnable {
	String reqFile = "";
	String inputFilePath = "";
	String processedPath = "";
	String responsePath = "";
	String responseLogPath = "";
	
	public ScanningThreadNMSCR(String reqFile,String inputFilePath,String processedPath,String responsePath,String responseLogPath) {
		this.reqFile = reqFile;
		this.inputFilePath = inputFilePath;
		this.processedPath = processedPath;
		this.responsePath = responsePath;
		this.responseLogPath = responseLogPath;
	}

	public void executeProc(String reqFile,String inputFilePath,String processedPath,String responsePath,String responseLogPath) 
	{
		ResponseJson responseJson = new ResponseJson();
		PreparedStatement pstmtLog = null;
		//Connection connection = null;
		
		try {
			//connection = ConnectionFactory.makeConnectionAMLLive();
			//logger.info("File name : "+reqFile);
			System.out.println("File name : "+reqFile);
			List<String> responseString = new ArrayList<String>();
			String fileNameWithOutExt = FilenameUtils.removeExtension(reqFile);
			//RequestJson requestJson = requestReader(inputFilePath + reqFile);
			List<RequestJson> requestJsonList = requestReaderList(inputFilePath + reqFile);
			//pstmtLog = connection.prepareStatement("insert into NAME_SCREEN_LOG values (?,?,?,?,sysdate,?)");
			
			for(RequestJson requestJson : requestJsonList)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				Date requestDate = new Date();
				String strDate = formatter.format(requestDate);
				
				//logger.info("Request Object : "+requestJson.toString());
				System.out.println("Request Object : "+requestJson.toString()+"-------"+strDate);
				
				if (!requestJson.getId().trim().equals("")) {
					responseJson = scanId(requestJson.getId(), "O");
				} else {
					responseJson.setScanStatus("S");
				}

				if ("S".equalsIgnoreCase(responseJson.getScanStatus())) {
					responseJson = scanNameAddr(requestJson);
				}
				if (!responseJson.getErrorFlg()) {
					/*
					 * Date date = new Date(); SimpleDateFormat formatter = new
					 * SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); String strDate =
					 * formatter.format(date);
					 */
					responseJson.setTimeStamp(strDate);

					String respString = requestJson.getUniqueSessionKey() + "|" + requestJson.getId() + "|"
							+ requestJson.getIdType() + "|" + requestJson.getName() + "|" + requestJson.getAddrType1()
							+ "|" + requestJson.getAddr1() + "|" + requestJson.getAddrType2() + "|"
							+ requestJson.getAddr2() + "|" + requestJson.getAddrType3() + "|" + requestJson.getAddr3()
							+ "|" + responseJson.getScanStatus() + "|" + responseJson.getRejectReason() + "|"
							+ responseJson.getTimeStamp();

					responseString.add(respString);
					
					/*
					 * pstmtLog.setString(1, requestJson.getUniqueSessionKey());
					 * pstmtLog.setString(2, responseJson.getScanStatus()); pstmtLog.setString(3,
					 * requestJson.getReqString()); pstmtLog.setString(4, respString);
					 * pstmtLog.setString(5, reqFile); pstmtLog.executeUpdate();
					 * 
					 * connection.commit(); connection.close();
					 */
				}
				
				Date responseDate = new Date();
				String responseTime = formatter.format(responseDate);
				
				//logger.info("Response Object : "+responseJson.toString());
				System.out.println("Response Object : "+responseJson.toString()+"-------"+responseTime);
			}
			
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
				Writer w2 = new FileWriter(responseLogPath + fileNameWithOutExt + ".RES");
				
				for(String response : responseString)
				{
					w.write(response+"\r\n");
					w2.write(response+"\r\n");
				}
				
				w.close();
				w2.close();
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
				/*
				 * if (connection != null) { connection.close(); connection = null; }
				 */
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
			connection = ConnectionFactory.makeConnectionNSAMLLive();
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			is.close();
			String fuzzyMatchNameMax = amlProp.getProperty("fuzzyMatchNameMax");
			//String nameMatchLength = amlProp.getProperty("nameMatchLength");
			
			double resultName = 0.0;
			double resultAddr = 0.0;

			String checkNameSQL = "select utl_match.edit_distance_similarity(name,'"
					+ requestJson.getName().replace(",", "").toUpperCase()
					+ "') match_percentage,ent_id,entrycategory from "
					+ "(select FIRST_NAME||' '||SECOND_NAME||' '||THIRD_NAME name,data_id ent_id,UN_LIST_TYPE entrycategory from aml_wl_ind union all "
					+ "select FIRST_NAME name,data_id ent_id,UN_LIST_TYPE entrycategory from aml_wl_entity union all "
					+ "select LAST_NAME name,data_id ent_id,SDN_TYPE entrycategory from aml_wl_sdn union all "
					+ "  select name,data_id ent_id ,list_type entrycategory from fcra_internal_watchlist_data ) "
					+ "where utl_match.edit_distance_similarity(name,'"
					+ requestJson.getName().replace(",", "").toUpperCase() + "') > " + fuzzyMatchNameMax + " ";
			
			/*
			 * int reqNameLength = requestJson.getName().length(); int nameStartLength = 0;
			 * 
			 * if(reqNameLength > Integer.valueOf(nameMatchLength)) { nameStartLength =
			 * reqNameLength - Integer.valueOf(nameMatchLength); } else { nameStartLength =
			 * reqNameLength; } int nameEndLength = reqNameLength +
			 * Integer.valueOf(nameMatchLength);
			 * 
			 * String checkNameSQL = "select name,ent_id,entrycategory from " +
			 * "(select FIRST_NAME||' '||SECOND_NAME||' '||THIRD_NAME name,data_id ent_id,UN_LIST_TYPE entrycategory from aml_wl_ind union all "
			 * +
			 * "select FIRST_NAME name,data_id ent_id,UN_LIST_TYPE entrycategory from aml_wl_entity union all "
			 * +
			 * "select LAST_NAME name,data_id ent_id,SDN_TYPE entrycategory from aml_wl_sdn) where name is not null "
			 * + "and length(name) between "+nameStartLength+" and "+nameEndLength;
			 */
			
			System.out.println("checkNameSQL : "+checkNameSQL);
			
			stmt = connection.prepareStatement(checkNameSQL);
			rs = stmt.executeQuery();

			while (rs.next()) {
				/*
				 * resultName = FuzzySearch.tokenSetRatio(requestJson.getName().replace(",",
				 * "").toUpperCase(), rs.getString(1).toUpperCase()); entId = rs.getString(2);
				 * entryCategory = rs.getString(3);
				 */
				
				resultName = rs.getDouble(1);
				
				//if (resultName >= Double.valueOf(fuzzyMatchNameMax)) {
					System.out.println("resultName : "+(int) resultName+" - "+Integer.valueOf(fuzzyMatchNameMax));
					flg = true;
					String checkAddrSQL = "select ent_id,to_char(address) from "
							+ "(select data_id ent_id,nvl(street,'NA')||','||nvl(city,'NA')||','||nvl(STATE_PROVINCE,'NA')||','||nvl(COUNTRY,'NA')||','||nvl(ZIP_CODE,'NA') address from aml_wl_ind_addr where data_id='"+entId+"' union all "
							+ "select data_id ent_id,nvl(street,'NA')||','||nvl(city,'NA')||','||nvl(STATE_PROVINCE,'NA')||','||nvl(COUNTRY,'NA')||','||nvl(ZIP_CODE,'NA') address from aml_wl_entity_addr where data_id='"+entId+"' union all "
							+ "select data_id ent_id,nvl(city,'NA')||','||nvl(COUNTRY,'NA') address from aml_wl_sdn_addr where data_id='"+entId+"' union all "
									+ " select data_id ent_id, nvl(address,' ') address from fcra_internal_watchlist_data where data_id ='"+entId+"') ";
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
			
			
			/*
			 * if(getAddrMap.values().size() > 0) { double maxFuzzyInMap =
			 * (Collections.max(getAddrMap.values())); responseJson.setScanStatus("E");
			 * responseJson.setRejectReason("2_" + entId + "_" + entryCategory + "_" +
			 * requestJson.getName() + " ("+resultName+")_" +
			 * requestJson.getAddr1()+" ("+maxFuzzyInMap+")"); } else if(resultName >=
			 * Double.valueOf(fuzzyMatchNameMax)) { responseJson.setScanStatus("E");
			 * responseJson.setRejectReason("2_" + entId + "_" + entryCategory + "_" +
			 * requestJson.getName() + " ("+resultName+")"); } else {
			 * responseJson.setScanStatus("S"); responseJson.setRejectReason("0"); }
			 */
			 
			 if(resultName >= Double.valueOf(fuzzyMatchNameMax) && getAddrMap.values().size() > 0) {
				 double maxFuzzyInMap =(Collections.max(getAddrMap.values()));
				responseJson.setScanStatus("E");
				responseJson.setRejectReason("2_" + entId + "_" + entryCategory + "_" + requestJson.getName() + " ("+resultName+")_"
						+  requestJson.getAddr1()+" ("+maxFuzzyInMap+")");
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
			connection = ConnectionFactory.makeConnectionNSAMLLive();
			
			/*
			 * String checkSQL =
			 * "select a.data_id ent_id,a.UN_LIST_TYPE entrycategory from aml_wl_ind a,aml_wl_ind_doc b "
			 * ; checkSQL += "where a.data_id=b.data_id and DOC_NO ='" + id + "' ";
			 */
			 String checkSQL="select data_id ent_id ,UN_LIST_TYPE entrycategory ,doc_no from "
			 		+ "(select a.data_id ,b.doc_no doc_no,UN_LIST_TYPE  from aml_wl_ind a,aml_wl_ind_doc b "
			 		+ " where a.data_id=b.data_id and doc_no is not null "
			 		+ " union all select a.data_id,a.ID_NUMBER doc_no ,'NA'  from aml_wl_sdn_id a ,aml_wl_sdn b "
			 		+ " where a.data_id = b.data_id and id_number is not null "
			 		+ " union all select DATA_ID,pan doc_no ,list_type un_list_type from fcra_internal_watchlist_data where pan is not null "
			 		+ " union all select DATA_ID,passport doc_no ,list_type un_list_type from fcra_internal_watchlist_data where passport is not null "
			 		+ " union all select DATA_ID,aadhaar doc_no,list_type un_list_type from fcra_internal_watchlist_data where aadhaar is not null) "
			 		+ "where doc_no='"+id+"'";
			 		
			
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
		executeProc(reqFile,inputFilePath,processedPath,responsePath,responseLogPath);
	}

}

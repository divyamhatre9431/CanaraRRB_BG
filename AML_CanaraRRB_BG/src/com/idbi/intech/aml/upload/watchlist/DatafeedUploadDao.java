package com.idbi.intech.aml.upload.watchlist;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.idbi.intech.aml.upload.watchlist.sdn.address;
import com.idbi.intech.aml.upload.watchlist.sdn.aka;
import com.idbi.intech.aml.upload.watchlist.sdn.citizenship;
import com.idbi.intech.aml.upload.watchlist.sdn.dateOfBirthItem;
import com.idbi.intech.aml.upload.watchlist.sdn.id;
import com.idbi.intech.aml.upload.watchlist.sdn.placeOfBirthItem;
import com.idbi.intech.aml.upload.watchlist.sdn.placeOfBirthList;
import com.idbi.intech.aml.upload.watchlist.sdn.sdnEntry;
import com.idbi.intech.aml.upload.watchlist.sdn.sdnList;
import com.idbi.intech.iaml.factory.ConnectionFactory;

public class DatafeedUploadDao 
{
	
	private static Connection connection = null;

	public static void makeConnection() {
		try {
			connection = ConnectionFactory.makeConnectionNSAMLLive();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	
	public List<String> getUnprocessedRequest()
	{
		Statement stmt = null;
		ResultSet rs = null;
		List<String> reqIdList = new ArrayList<String>();
		
		try
		{
			makeConnection();
			String sql = "select req_id||'~'||file_type from watchlist_upload_req_tab where process_flg='N' ";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				reqIdList.add(rs.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
		
		return reqIdList;
	}
	
	
	public void updateRequestStatus(String reqNo,String statusFlg)
	{
		Statement stmt = null;
		
		try
		{
			makeConnection();
			String sql = "update watchlist_upload_req_tab set process_flg='"+statusFlg+"',update_time=sysdate where req_id='"+reqNo+"'";
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			connection.commit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
	
	
	public void uploadIndividualData(CONSOLIDATED_LIST consolObj,String requestId)
	{
		PreparedStatement pstmt = null;
		CallableStatement cstmt = null;
		
		try
		{
			makeConnection();
			
			for(INDIVIDUAL indObj : consolObj.getINDIVIDUALS().getINDIVIDUAL())
			{
				//-----------------------------INDIVIDUAL----------------------------------------
				String indQuery = "insert into aml_wl_ind_temp(data_id,version_num,name_original_script,first_name,second_name,third_name,un_list_type,reference_number,listed_on,gender,comments,goodquality,passport,address) "
						+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(indQuery);
				
				pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
				pstmt.setString(2, indObj.getVERSIONNUM() == null ? indObj.getVERSIONNUM() : indObj.getVERSIONNUM().toUpperCase());
				pstmt.setString(3, indObj.getNAME_ORIGINAL_SCRIPT() == null ? indObj.getNAME_ORIGINAL_SCRIPT() : indObj.getNAME_ORIGINAL_SCRIPT().toUpperCase());
				pstmt.setString(4, indObj.getFIRST_NAME() == null ? indObj.getFIRST_NAME() : indObj.getFIRST_NAME().toUpperCase());
				pstmt.setString(5, indObj.getSECOND_NAME() == null ? indObj.getSECOND_NAME() : indObj.getSECOND_NAME().toUpperCase());
				pstmt.setString(6, indObj.getTHIRD_NAME() == null ? indObj.getTHIRD_NAME() : indObj.getTHIRD_NAME().toUpperCase());
				pstmt.setString(7, indObj.getUN_LIST_TYPE() == null ? indObj.getUN_LIST_TYPE() : indObj.getUN_LIST_TYPE().toUpperCase());
				pstmt.setString(8, indObj.getREFERENCE_NUMBER() == null ? indObj.getREFERENCE_NUMBER() : indObj.getREFERENCE_NUMBER().toUpperCase());
				pstmt.setString(9, indObj.getLISTED_ON() == null ? indObj.getLISTED_ON() : indObj.getLISTED_ON().toUpperCase());
				pstmt.setString(10, indObj.getGENDER() == null ? indObj.getGENDER() : indObj.getGENDER().toUpperCase());
				pstmt.setString(11, indObj.getCOMMENTS1() == null ? indObj.getCOMMENTS1() : indObj.getCOMMENTS1().toUpperCase());
				pstmt.setString(12, indObj.getGOODQUALITY() == null ? indObj.getGOODQUALITY() : indObj.getGOODQUALITY().toUpperCase());
				pstmt.setString(13, indObj.getPASSPORT() == null ? indObj.getPASSPORT() : indObj.getPASSPORT().toUpperCase());
				pstmt.setString(14, indObj.getADDRESS() == null ? indObj.getADDRESS() : indObj.getADDRESS().toUpperCase());
				
				pstmt.executeUpdate();
				pstmt.close();
				
				//-----------------------------TITLE----------------------------------------
				String indTitleQuery = "insert into aml_wl_ind_title_temp(data_id,title_value) values(?,?)";
				pstmt = connection.prepareStatement(indTitleQuery);
				
				if(indObj.getTITLE() != null && indObj.getTITLE().getVALUE().size() > 0)
				{
					for(String title : indObj.getTITLE().getVALUE())
					{
						pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
						pstmt.setString(2, title == null ? title : title.toUpperCase());
						
						pstmt.executeUpdate();
					}
					pstmt.close();
				}
				
				//-----------------------------LAST_UPDATE_DATE----------------------------------------
				String indLastUpdateQuery = "insert into AML_WL_IND_LASTUPDATE_temp(data_id,update_value) values(?,?)";
				pstmt = connection.prepareStatement(indLastUpdateQuery);
				
				if(indObj.getLAST_DAY_UPDATED() != null &&  indObj.getLAST_DAY_UPDATED().getVALUE().size() > 0)
				{
					for(String lastUpdate : indObj.getLAST_DAY_UPDATED().getVALUE())
					{
						pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
						pstmt.setString(2, lastUpdate == null ? lastUpdate : lastUpdate.toUpperCase());
						
						pstmt.executeUpdate();
					}
					pstmt.close();
				}
				
				//-----------------------------LIST_TYPE----------------------------------------
				String indListTypeQuery = "insert into AML_WL_IND_LISTTYPE_temp(data_id,type_value) values(?,?)";
				pstmt = connection.prepareStatement(indListTypeQuery);
				
				pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
				pstmt.setString(2, indObj.getLIST_TYPE().getVALUE() == null ? indObj.getLIST_TYPE().getVALUE() : indObj.getLIST_TYPE().getVALUE().toUpperCase());
				
				pstmt.executeUpdate();
				pstmt.close();
				
				//-----------------------------NATIONALITY----------------------------------------
				String indNationalQuery = "insert into AML_WL_IND_NATIONALITY_temp(data_id,value) values(?,?)";
				pstmt = connection.prepareStatement(indNationalQuery);
				
				if(indObj.getNATIONALITY() != null && indObj.getNATIONALITY().getVALUE().size() > 0)
				{
					for(String national : indObj.getNATIONALITY().getVALUE())
					{
						pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
						pstmt.setString(2, national == null ? national : national.toUpperCase());
						
						pstmt.executeUpdate();
					}
					pstmt.close();
				}
				
				
				//-----------------------------DOCUMENT----------------------------------------
				String indDoc = "insert into AML_WL_IND_DOC_temp(data_id,TYPE_OF_DOCUMENT,TYPE_OF_DOCUMENT2,DOC_NO,ISSUING_COUNTRY,CITY_OF_ISSUE,DATE_OF_ISSUE,COUNTRY_OF_ISSUE,NOTE) "
						+ " values(?,?,?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(indDoc);
				
				if(indObj.getINDIVIDUAL_DOCUMENT() != null && indObj.getINDIVIDUAL_DOCUMENT().size() > 0)
				{
					for(INDIVIDUAL_DOCUMENT docObj : indObj.getINDIVIDUAL_DOCUMENT())
					{
						pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
						pstmt.setString(2, docObj.getTYPE_OF_DOCUMENT() == null ? docObj.getTYPE_OF_DOCUMENT() : docObj.getTYPE_OF_DOCUMENT().toUpperCase());
						pstmt.setString(3, docObj.getTYPE_OF_DOCUMENT2() == null ? docObj.getTYPE_OF_DOCUMENT2() : docObj.getTYPE_OF_DOCUMENT2().toUpperCase());
						pstmt.setString(4, docObj.getNUMBER() == null ? docObj.getNUMBER() : docObj.getNUMBER().toUpperCase());
						pstmt.setString(5, docObj.getISSUING_COUNTRY() == null ? docObj.getISSUING_COUNTRY() : docObj.getISSUING_COUNTRY().toUpperCase());
						pstmt.setString(6, docObj.getCITY_OF_ISSUE() == null ? docObj.getCITY_OF_ISSUE() : docObj.getCITY_OF_ISSUE().toUpperCase());
						pstmt.setString(7, docObj.getDATE_OF_ISSUE() == null ? docObj.getDATE_OF_ISSUE() : docObj.getDATE_OF_ISSUE().toUpperCase());
						pstmt.setString(8, docObj.getCOUNTRY_OF_ISSUE() == null ? docObj.getCOUNTRY_OF_ISSUE() : docObj.getCOUNTRY_OF_ISSUE().toUpperCase());
						pstmt.setString(9, docObj.getNOTE() == null ? docObj.getNOTE() : docObj.getNOTE().toUpperCase());
						
						pstmt.executeUpdate();
					}

					pstmt.close();
				}
				
				
				//-----------------------------POB----------------------------------------
				String indPob = "insert into AML_WL_IND_POB_temp(data_id,STATE_PROVINCE,COUNTRY,CITY) values(?,?,?,?)";
				pstmt = connection.prepareStatement(indPob);
				
				if(indObj.getINDIVIDUAL_PLACE_OF_BIRTH() != null && indObj.getINDIVIDUAL_PLACE_OF_BIRTH().size() > 0)
				{
					for(INDIVIDUAL_PLACE_OF_BIRTH pobObj : indObj.getINDIVIDUAL_PLACE_OF_BIRTH())
					{
						pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
						pstmt.setString(2, pobObj.getSTATE_PROVINCE() == null ? pobObj.getSTATE_PROVINCE() : pobObj.getSTATE_PROVINCE().toUpperCase());
						pstmt.setString(3, pobObj.getCOUNTRY() == null ? pobObj.getCOUNTRY() : pobObj.getCOUNTRY().toUpperCase());
						pstmt.setString(4, pobObj.getCITY() == null ? pobObj.getCITY() : pobObj.getCITY().toUpperCase());
						
						pstmt.executeUpdate();
					}

					pstmt.close();
				}
				
				//-----------------------------DOB----------------------------------------
				String indDob = "insert into AML_WL_IND_DOB_temp(data_id,TYPE_OF_DATE,DOB,YEAR,FROM_YEAR,TO_YEAR,NOTE) values(?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(indDob);
				
				if(indObj.getINDIVIDUAL_DATE_OF_BIRTH() != null && indObj.getINDIVIDUAL_DATE_OF_BIRTH().size() > 0)
				{
					for(INDIVIDUAL_DATE_OF_BIRTH dobObj : indObj.getINDIVIDUAL_DATE_OF_BIRTH())
					{
						pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
						pstmt.setString(2, dobObj.getTYPE_OF_DATE() == null ? dobObj.getTYPE_OF_DATE() : dobObj.getTYPE_OF_DATE().toUpperCase());
						pstmt.setString(3, dobObj.getDATE() == null ? dobObj.getDATE() : dobObj.getDATE().toUpperCase());
						pstmt.setString(4, dobObj.getYEAR() == null ? dobObj.getYEAR() : dobObj.getYEAR().toUpperCase());
						pstmt.setString(5, dobObj.getFROM_YEAR() == null ? dobObj.getFROM_YEAR() : dobObj.getFROM_YEAR().toUpperCase());
						pstmt.setString(6, dobObj.getTO_YEAR() == null ? dobObj.getTO_YEAR() : dobObj.getTO_YEAR().toUpperCase());
						pstmt.setString(7, dobObj.getNOTE() == null ? dobObj.getNOTE() : dobObj.getNOTE().toUpperCase());
						
						pstmt.executeUpdate();
					}

					pstmt.close();
				}
				
				
				//-----------------------------ADDRESS----------------------------------------
				String indAddr = "insert into AML_WL_IND_ADDR_temp(data_id,COUNTRY,STREET,CITY,ZIP_CODE,STATE_PROVINCE,NOTE) values(?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(indAddr);
				
				if(indObj.getINDIVIDUAL_ADDRESS() != null && indObj.getINDIVIDUAL_ADDRESS().size() > 0)
				{
					for(INDIVIDUAL_ADDRESS addrObj : indObj.getINDIVIDUAL_ADDRESS())
					{
						pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
						pstmt.setString(2, addrObj.getCOUNTRY() == null ? addrObj.getCOUNTRY() : addrObj.getCOUNTRY().toUpperCase());
						pstmt.setString(3, addrObj.getSTREET() == null ? addrObj.getSTREET() : addrObj.getSTREET().toUpperCase());
						pstmt.setString(4, addrObj.getCITY() == null ? addrObj.getCITY() : addrObj.getCITY().toUpperCase());
						pstmt.setString(5, addrObj.getZIP_CODE() == null ? addrObj.getZIP_CODE() : addrObj.getZIP_CODE().toUpperCase());
						pstmt.setString(6, addrObj.getSTATE_PROVINCE() == null ? addrObj.getSTATE_PROVINCE() : addrObj.getSTATE_PROVINCE().toUpperCase());
						pstmt.setString(7, addrObj.getNOTE() == null ? addrObj.getNOTE() : addrObj.getNOTE().toUpperCase());
						
						pstmt.executeUpdate();
					}

					pstmt.close();
				}
				
				
				//-----------------------------ALIAS----------------------------------------
				String indAlias = "insert into AML_WL_IND_ALIAS_temp(data_id,QUALITY,ALIAS_NAME,DATE_OF_BIRTH,COUNTRY_OF_BIRTH,CITY_OF_BIRTH,NOTE) values(?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(indAlias);
				
				if(indObj.getINDIVIDUAL_ALIAS() != null && indObj.getINDIVIDUAL_ALIAS().size() > 0)
				{
					for(INDIVIDUAL_ALIAS aliasObj : indObj.getINDIVIDUAL_ALIAS())
					{
						pstmt.setString(1, indObj.getDATAID() == null ? indObj.getDATAID() : indObj.getDATAID().toUpperCase());
						pstmt.setString(2, aliasObj.getQUALITY() == null ? aliasObj.getQUALITY() : aliasObj.getQUALITY().toUpperCase());
						pstmt.setString(3, aliasObj.getALIAS_NAME() == null ? aliasObj.getALIAS_NAME() : aliasObj.getALIAS_NAME().toUpperCase());
						pstmt.setString(4, aliasObj.getDATE_OF_BIRTH() == null ? aliasObj.getDATE_OF_BIRTH() : aliasObj.getDATE_OF_BIRTH().toUpperCase());
						pstmt.setString(5, aliasObj.getCOUNTRY_OF_BIRTH() == null ? aliasObj.getCOUNTRY_OF_BIRTH() : aliasObj.getCOUNTRY_OF_BIRTH().toUpperCase());
						pstmt.setString(6, aliasObj.getCITY_OF_BIRTH() == null ? aliasObj.getCITY_OF_BIRTH() : aliasObj.getCITY_OF_BIRTH().toUpperCase());
						pstmt.setString(7, aliasObj.getNOTE() == null ? aliasObj.getNOTE() : aliasObj.getNOTE().toUpperCase());
						
						pstmt.executeUpdate();
					}

					pstmt.close();
				}
				
			}
			
			
			for(ENTITY entObj : consolObj.getENTITIES().getENTITY())
			{
				//-----------------------------ENTITY----------------------------------------
				String indQuery = "insert into aml_wl_entity_temp(data_id,version_num,name_original_script,first_name,second_name,third_name,un_list_type,reference_number,listed_on,gender,comments,goodquality,passport,address) "
						+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(indQuery);
				
				pstmt.setString(1, entObj.getDATAID() == null ? entObj.getDATAID() : entObj.getDATAID().toUpperCase());
				pstmt.setString(2, entObj.getVERSIONNUM() == null ? entObj.getVERSIONNUM() : entObj.getVERSIONNUM().toUpperCase());
				pstmt.setString(3, entObj.getNAME_ORIGINAL_SCRIPT() == null ? entObj.getNAME_ORIGINAL_SCRIPT() : entObj.getNAME_ORIGINAL_SCRIPT().toUpperCase());
				pstmt.setString(4, entObj.getFIRST_NAME() == null ? entObj.getFIRST_NAME() : entObj.getFIRST_NAME().toUpperCase());
				pstmt.setString(5, null);
				pstmt.setString(6, null);
				pstmt.setString(7, entObj.getUN_LIST_TYPE() == null ? entObj.getUN_LIST_TYPE() : entObj.getUN_LIST_TYPE().toUpperCase());
				pstmt.setString(8, entObj.getREFERENCE_NUMBER() == null ? entObj.getREFERENCE_NUMBER() : entObj.getREFERENCE_NUMBER().toUpperCase());
				pstmt.setString(9, entObj.getLISTED_ON() == null ? entObj.getLISTED_ON() : entObj.getLISTED_ON().toUpperCase());
				pstmt.setString(10, null);
				pstmt.setString(11, entObj.getCOMMENTS1() == null ? entObj.getCOMMENTS1() : entObj.getCOMMENTS1().toUpperCase());
				pstmt.setString(12, null);
				pstmt.setString(13, null);
				pstmt.setString(14, null);
				
				pstmt.executeUpdate();
				pstmt.close();
				
				
				//-----------------------------ENTITY_LAST_UPDATE_DATE----------------------------------------
				String entLastUpdateQuery = "insert into AML_WL_ENTITY_LASTUPDATE_temp(data_id,update_value) values(?,?)";
				pstmt = connection.prepareStatement(entLastUpdateQuery);
				
				if(entObj.getLAST_DAY_UPDATED() != null && entObj.getLAST_DAY_UPDATED().getVALUE().size() > 0)
				{
					for(String lastUpdate : entObj.getLAST_DAY_UPDATED().getVALUE())
					{
						pstmt.setString(1, entObj.getDATAID() == null ? entObj.getDATAID() : entObj.getDATAID().toUpperCase());
						pstmt.setString(2, lastUpdate == null ? lastUpdate : lastUpdate.toUpperCase());
						
						pstmt.executeUpdate();
					}
					pstmt.close();
				}
				
				
				//-----------------------------ENTITY_LIST_TYPE----------------------------------------
				String entListTypeQuery = "insert into AML_WL_ENTITY_LISTTYPE_temp(data_id,type_value) values(?,?)";
				pstmt = connection.prepareStatement(entListTypeQuery);
				
				pstmt.setString(1, entObj.getDATAID() == null ? entObj.getDATAID() : entObj.getDATAID().toUpperCase());
				pstmt.setString(2, entObj.getLIST_TYPE().getVALUE() == null ? entObj.getLIST_TYPE().getVALUE() : entObj.getLIST_TYPE().getVALUE().toUpperCase());
				
				pstmt.executeUpdate();
				pstmt.close();
				
				
				//-----------------------------ADDRESS----------------------------------------
				String entAddr = "insert into AML_WL_ENTITY_ADDR_temp(data_id,COUNTRY,STREET,CITY,ZIP_CODE,STATE_PROVINCE,NOTE) values(?,?,?,?,?,?,?)";
				pstmt = connection.prepareStatement(entAddr);
				
				if(entObj.getENTITY_ADDRESS() != null && entObj.getENTITY_ADDRESS().size() > 0)
				{
					for(ENTITY_ADDRESS addrObj : entObj.getENTITY_ADDRESS())
					{
						pstmt.setString(1, entObj.getDATAID() == null ? entObj.getDATAID() : entObj.getDATAID().toUpperCase());
						pstmt.setString(2, addrObj.getCOUNTRY() == null ? addrObj.getCOUNTRY() : addrObj.getCOUNTRY().toUpperCase());
						pstmt.setString(3, addrObj.getSTREET() == null ? addrObj.getSTREET() : addrObj.getSTREET().toUpperCase());
						pstmt.setString(4, addrObj.getCITY() == null ? addrObj.getCITY() : addrObj.getCITY().toUpperCase());
						pstmt.setString(5, addrObj.getZIP_CODE() == null ? addrObj.getZIP_CODE() : addrObj.getZIP_CODE().toUpperCase());
						pstmt.setString(6, addrObj.getSTATE_PROVINCE() == null ? addrObj.getSTATE_PROVINCE() : addrObj.getSTATE_PROVINCE().toUpperCase());
						pstmt.setString(7, addrObj.getNOTE() == null ? addrObj.getNOTE() : addrObj.getNOTE().toUpperCase());
						
						pstmt.executeUpdate();
					}

					pstmt.close();
				}
				
				
				//-----------------------------ENTITY_ALIAS----------------------------------------
				String indAlias = "insert into AML_WL_ENTITY_ALIAS_temp(data_id,QUALITY,ALIAS_NAME) values(?,?,?)";
				pstmt = connection.prepareStatement(indAlias);
				
				if(entObj.getENTITY_ALIAS() != null && entObj.getENTITY_ALIAS().size() > 0)
				{
					for(ENTITY_ALIAS aliasObj : entObj.getENTITY_ALIAS())
					{
						pstmt.setString(1, entObj.getDATAID() == null ? entObj.getDATAID() : entObj.getDATAID().toUpperCase());
						pstmt.setString(2, aliasObj.getQUALITY() == null ? aliasObj.getQUALITY() : aliasObj.getQUALITY().toUpperCase());
						pstmt.setString(3, aliasObj.getALIAS_NAME() == null ? aliasObj.getALIAS_NAME() : aliasObj.getALIAS_NAME().toUpperCase());
						
						pstmt.executeUpdate();
					}

					pstmt.close();
				}
				
			}
			
			connection.commit();
			
			//cstmt = connection.prepareCall("call update_ind_ent_data()");
			cstmt = connection.prepareCall("call update_ind_ent_data_untype('"+requestId+"')");
			cstmt.execute();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	public void uploadSdnData(sdnList sdnObj,String requestId)
	{
		PreparedStatement pstmt = null; 
		CallableStatement cstmt = null;
		
		try
		{
			makeConnection();
			
			for(sdnEntry sdnEntryObj : sdnObj.getSdnEntry())
			{
				//-----------------------------SDN_ENTRY----------------------------------------
				String sdnEntryQuery = "insert into AML_WL_SDN_temp(data_id,LAST_NAME,SDN_TYPE,first_name,remarks) values(?,?,?,?,?)";
				pstmt = connection.prepareStatement(sdnEntryQuery);
				
				pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() : sdnEntryObj.getUid().toUpperCase());
				pstmt.setString(2, sdnEntryObj.getLastName() == null ? sdnEntryObj.getLastName() : sdnEntryObj.getLastName().toUpperCase());
				pstmt.setString(3, sdnEntryObj.getSdnType() == null ? sdnEntryObj.getSdnType() : sdnEntryObj.getSdnType().toUpperCase());
				pstmt.setString(4, sdnEntryObj.getFirstName() == null ? sdnEntryObj.getFirstName() : sdnEntryObj.getFirstName().toUpperCase());
				pstmt.setString(5, sdnEntryObj.getRemarks() == null ? sdnEntryObj.getRemarks() : sdnEntryObj.getRemarks());
				
				pstmt.executeUpdate();
				pstmt.close();
				
				
				//-----------------------------ADDRESS----------------------------------------
				String sdnEntryAddr = "insert into AML_WL_SDN_ADDR_temp(DATA_ID,CITY,COUNTRY,ADDR1,ADDR2) values(?,?,?,?,?)";
				pstmt = connection.prepareStatement(sdnEntryAddr);
				
				if(sdnEntryObj.getAddressList() != null)
				{
					/*
					 * pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() :
					 * sdnEntryObj.getUid().toUpperCase()); pstmt.setString(2,
					 * sdnEntryObj.getAddressList().getAddress().getCity() == null ?
					 * sdnEntryObj.getAddressList().getAddress().getCity() :
					 * sdnEntryObj.getAddressList().getAddress().getCity().toUpperCase());
					 * pstmt.setString(3, sdnEntryObj.getAddressList().getAddress().getCountry() ==
					 * null ? sdnEntryObj.getAddressList().getAddress().getCountry() :
					 * sdnEntryObj.getAddressList().getAddress().getCountry().toUpperCase());
					 */
					
					//pstmt.executeUpdate();
					
					for(address aObj : sdnEntryObj.getAddressList().getAddress())
					{
						//System.out.println("getAddressList");
						
						pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() : sdnEntryObj.getUid().toUpperCase());
						pstmt.setString(2, aObj.getCity() == null ? aObj.getCity() : aObj.getCity().toUpperCase());
						pstmt.setString(3, aObj.getCountry() == null ? aObj.getCountry() : aObj.getCountry().toUpperCase());
						pstmt.setString(4, aObj.getAddress1() == null ? aObj.getAddress1() : aObj.getAddress1().toUpperCase());
						pstmt.setString(5, aObj.getAddress2() == null ? aObj.getAddress2() : aObj.getAddress2().toUpperCase());
						
						pstmt.executeUpdate();
					}
					
				}
				
				pstmt.close();
				
				
				//-----------------------------PROGRAM----------------------------------------
				String sdnEntryProg = "insert into AML_WL_SDN_PROGRAM_temp(DATA_ID,PROGRAM) values(?,?)";
				pstmt = connection.prepareStatement(sdnEntryProg);
				
				if(sdnEntryObj.getProgramList() != null)
				{
					/*
					 * pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() :
					 * sdnEntryObj.getUid().toUpperCase()); pstmt.setString(2,
					 * sdnEntryObj.getProgramList().getProgram() == null ?
					 * sdnEntryObj.getProgramList().getProgram() :
					 * sdnEntryObj.getProgramList().getProgram().toUpperCase());
					 * 
					 * pstmt.executeUpdate();
					 */
					
					for(String program : sdnEntryObj.getProgramList().getProgram())
					{
						//System.out.println("getProgramList");
						
						pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() : sdnEntryObj.getUid().toUpperCase());
						pstmt.setString(2, program == null ? program : program.toUpperCase());
						
						pstmt.executeUpdate();
					}
				}
				
				pstmt.close();
				
				
				//-----------------------------AKA----------------------------------------
				String sdnEntryAka = "insert into AML_WL_SDN_AKA_temp(DATA_ID,TYPE,CATEGORY,LAST_NAME,FIRST_NAME) values(?,?,?,?,?)";
				pstmt = connection.prepareStatement(sdnEntryAka);
				
				if(sdnEntryObj.getAkaList() != null)
				{
					/*
					 * pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() :
					 * sdnEntryObj.getUid().toUpperCase()); pstmt.setString(2,
					 * sdnEntryObj.getAkaList().getAka().getType() == null ?
					 * sdnEntryObj.getAkaList().getAka().getType() :
					 * sdnEntryObj.getAkaList().getAka().getType().toUpperCase());
					 * pstmt.setString(3, sdnEntryObj.getAkaList().getAka().getCategory() == null ?
					 * sdnEntryObj.getAkaList().getAka().getCategory() :
					 * sdnEntryObj.getAkaList().getAka().getCategory().toUpperCase());
					 * pstmt.setString(4, sdnEntryObj.getAkaList().getAka().getLastName() == null ?
					 * sdnEntryObj.getAkaList().getAka().getLastName() :
					 * sdnEntryObj.getAkaList().getAka().getLastName().toUpperCase());
					 * 
					 * pstmt.executeUpdate();
					 */
					
					for(aka akaObj : sdnEntryObj.getAkaList().getAka())
					{
						//System.out.println("getAkaList");
						
						pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() : sdnEntryObj.getUid().toUpperCase());
						pstmt.setString(2, akaObj.getType() == null ? akaObj.getType() : akaObj.getType().toUpperCase());
						pstmt.setString(3, akaObj.getCategory() == null ? akaObj.getCategory() : akaObj.getCategory().toUpperCase());
						pstmt.setString(4, akaObj.getLastName() == null ? akaObj.getLastName() : akaObj.getLastName().toUpperCase());
						pstmt.setString(5, akaObj.getFirstName() == null ? akaObj.getFirstName() : akaObj.getFirstName().toUpperCase());
						
						pstmt.executeUpdate();
					}
				}
				
				pstmt.close();
				
				
				//-----------------------------CITIZEN----------------------------------------
				
				String sdnEntryCitizen = "insert into AML_WL_SDN_citizen_temp(DATA_ID,COUNTRY,MAIN_ENTRY) values(?,?,?)";
				pstmt = connection.prepareStatement(sdnEntryCitizen);
				
				if(sdnEntryObj.getCitizenshipList() != null)
				{
					for(citizenship citizenObj : sdnEntryObj.getCitizenshipList().getCitizenship())
					{
						//System.out.println("getCitizenshipList");
						
						pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() : sdnEntryObj.getUid().toUpperCase());
						pstmt.setString(2, citizenObj.getCountry() == null ? citizenObj.getCountry() : citizenObj.getCountry().toUpperCase());
						pstmt.setString(3, citizenObj.getMainEntry() == null ? citizenObj.getMainEntry() : citizenObj.getMainEntry().toUpperCase());
						
						pstmt.executeUpdate();
					}
				}
				
				pstmt.close();
				
				//-----------------------------DOB----------------------------------------
				
				String sdnEntryBirth = "insert into aml_wl_sdn_birth_temp(DATA_ID,BIRTH_YEAR,MAIN_ENTRY) values(?,?,?)";
				pstmt = connection.prepareStatement(sdnEntryBirth);
				
				if(sdnEntryObj.getDateOfBirthList() != null)
				{
					for(dateOfBirthItem dobObj : sdnEntryObj.getDateOfBirthList().getDateOfBirthItem())
					{
						//System.out.println("getDateOfBirthList");
						
						pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() : sdnEntryObj.getUid().toUpperCase());
						pstmt.setString(2, dobObj.getDateOfBirth() == null ? dobObj.getDateOfBirth() : dobObj.getDateOfBirth().toUpperCase());
						pstmt.setString(3, dobObj.getMainEntry() == null ? dobObj.getMainEntry() : dobObj.getMainEntry().toUpperCase());
						
						pstmt.executeUpdate();
					}
				}
				
				pstmt.close();
				
				
				//-----------------------------POB----------------------------------------
				
				String sdnEntryPlace = "insert into aml_wl_sdn_birth_place_temp(DATA_ID,PLACE,MAIN_ENTRY) values(?,?,?)";
				pstmt = connection.prepareStatement(sdnEntryPlace);
				
				if(sdnEntryObj.getPlaceOfBirthList() != null)
				{
					for(placeOfBirthItem pobObj : sdnEntryObj.getPlaceOfBirthList().getPlaceOfBirthItem())
					{
						//System.out.println("getPlaceOfBirthList");
						
						pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() : sdnEntryObj.getUid().toUpperCase());
						pstmt.setString(2, pobObj.getPlaceOfBirth() == null ? pobObj.getPlaceOfBirth() : pobObj.getPlaceOfBirth().toUpperCase());
						pstmt.setString(3, pobObj.getMainEntry() == null ? pobObj.getMainEntry() : pobObj.getMainEntry().toUpperCase());
						
						pstmt.executeUpdate();
					}
				}
				
				pstmt.close();
				
				//---------------------------------------------------------------------
				
				String sdnEntryId = "insert into aml_wl_sdn_id_temp(DATA_ID,ID_TYPE,ID_NUMBER,ID_COUNTRY,EXPIRE_DATE) values(?,?,?,?,?)";
				pstmt = connection.prepareStatement(sdnEntryId);
				
				if(sdnEntryObj.getIdList() != null)
				{
					for(id idObj : sdnEntryObj.getIdList().getId())
					{
						//System.out.println("getIdList");
						
						pstmt.setString(1, sdnEntryObj.getUid() == null ? sdnEntryObj.getUid() : sdnEntryObj.getUid().toUpperCase());
						pstmt.setString(2, idObj.getIdType() == null ? idObj.getIdType() : idObj.getIdType().toUpperCase());
						pstmt.setString(3, idObj.getIdNumber() == null ? idObj.getIdNumber() : idObj.getIdNumber().toUpperCase());
						pstmt.setString(4, idObj.getIdCountry() == null ? idObj.getIdCountry() : idObj.getIdCountry().toUpperCase());
						pstmt.setString(5, idObj.getExpirationDate() == null ? idObj.getExpirationDate() : idObj.getExpirationDate().toUpperCase());
						
						pstmt.executeUpdate();
					}
				}
				
				pstmt.close();
				
			}
			connection.commit();
			
			//cstmt = connection.prepareCall("call update_sdn_data()");
			cstmt = connection.prepareCall("call update_sdn_data_untype('"+requestId+"')");
			cstmt.execute();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}
				if (cstmt != null) {
					cstmt.close();
					cstmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
}


package com.idbi.intech.aml.upload.watchlist;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD) 
public class INDIVIDUAL
{
	public List<INDIVIDUAL_ALIAS> INDIVIDUAL_ALIAS;
	public List<INDIVIDUAL_ADDRESS> INDIVIDUAL_ADDRESS;
	public List<INDIVIDUAL_DATE_OF_BIRTH> INDIVIDUAL_DATE_OF_BIRTH;
	public List<INDIVIDUAL_PLACE_OF_BIRTH> INDIVIDUAL_PLACE_OF_BIRTH;
	public List<INDIVIDUAL_DOCUMENT> INDIVIDUAL_DOCUMENT;
	@XmlElement
	public Object SORT_KEY;
	@XmlElement
	public Object SORT_KEY_LAST_MOD;
	@XmlElement
	public String DATAID;
	@XmlElement
	public String VERSIONNUM;
	@XmlElement
	public String NAME_ORIGINAL_SCRIPT;
	@XmlElement
	public String FIRST_NAME;
	@XmlElement
	public String SECOND_NAME;
	@XmlElement
	public String THIRD_NAME;
	@XmlElement
	public String UN_LIST_TYPE;
	@XmlElement
	public String REFERENCE_NUMBER;
	@XmlElement
	public String LISTED_ON;
	@XmlElement
	public String COMMENTS1;
	@XmlElement
	public String GOODQUALITY;
	@XmlElement
	public String PASSPORT;
	@XmlElement
	public NATIONALITY NATIONALITY;
	@XmlElement
	public LIST_TYPE LIST_TYPE;
	@XmlElement
	public LAST_DAY_UPDATED LAST_DAY_UPDATED;
	@XmlElement
	public TITLE TITLE;
	@XmlElement
	public String GENDER;
	@XmlElement
	public String ADDRESS;
	
	public List<INDIVIDUAL_ALIAS> getINDIVIDUAL_ALIAS() {
		return INDIVIDUAL_ALIAS;
	}
	public void setINDIVIDUAL_ALIAS(List<INDIVIDUAL_ALIAS> iNDIVIDUAL_ALIAS) {
		INDIVIDUAL_ALIAS = iNDIVIDUAL_ALIAS;
	}
	public List<INDIVIDUAL_ADDRESS> getINDIVIDUAL_ADDRESS() {
		return INDIVIDUAL_ADDRESS;
	}
	public void setINDIVIDUAL_ADDRESS(List<INDIVIDUAL_ADDRESS> iNDIVIDUAL_ADDRESS) {
		INDIVIDUAL_ADDRESS = iNDIVIDUAL_ADDRESS;
	}
	public List<INDIVIDUAL_DATE_OF_BIRTH> getINDIVIDUAL_DATE_OF_BIRTH() {
		return INDIVIDUAL_DATE_OF_BIRTH;
	}
	public void setINDIVIDUAL_DATE_OF_BIRTH(List<INDIVIDUAL_DATE_OF_BIRTH> iNDIVIDUAL_DATE_OF_BIRTH) {
		INDIVIDUAL_DATE_OF_BIRTH = iNDIVIDUAL_DATE_OF_BIRTH;
	}
	public List<INDIVIDUAL_PLACE_OF_BIRTH> getINDIVIDUAL_PLACE_OF_BIRTH() {
		return INDIVIDUAL_PLACE_OF_BIRTH;
	}
	public void setINDIVIDUAL_PLACE_OF_BIRTH(List<INDIVIDUAL_PLACE_OF_BIRTH> iNDIVIDUAL_PLACE_OF_BIRTH) {
		INDIVIDUAL_PLACE_OF_BIRTH = iNDIVIDUAL_PLACE_OF_BIRTH;
	}
	public List<INDIVIDUAL_DOCUMENT> getINDIVIDUAL_DOCUMENT() {
		return INDIVIDUAL_DOCUMENT;
	}
	public void setINDIVIDUAL_DOCUMENT(List<INDIVIDUAL_DOCUMENT> iNDIVIDUAL_DOCUMENT) {
		INDIVIDUAL_DOCUMENT = iNDIVIDUAL_DOCUMENT;
	}
	public Object getSORT_KEY() {
		return SORT_KEY;
	}
	public void setSORT_KEY(Object sORT_KEY) {
		SORT_KEY = sORT_KEY;
	}
	public Object getSORT_KEY_LAST_MOD() {
		return SORT_KEY_LAST_MOD;
	}
	public void setSORT_KEY_LAST_MOD(Object sORT_KEY_LAST_MOD) {
		SORT_KEY_LAST_MOD = sORT_KEY_LAST_MOD;
	}
	public String getDATAID() {
		return DATAID;
	}
	public void setDATAID(String dATAID) {
		DATAID = dATAID;
	}
	public String getVERSIONNUM() {
		return VERSIONNUM;
	}
	public void setVERSIONNUM(String vERSIONNUM) {
		VERSIONNUM = vERSIONNUM;
	}
	public String getNAME_ORIGINAL_SCRIPT() {
		return NAME_ORIGINAL_SCRIPT;
	}
	public void setNAME_ORIGINAL_SCRIPT(String nAME_ORIGINAL_SCRIPT) {
		NAME_ORIGINAL_SCRIPT = nAME_ORIGINAL_SCRIPT;
	}
	public String getFIRST_NAME() {
		return FIRST_NAME;
	}
	public void setFIRST_NAME(String fIRST_NAME) {
		FIRST_NAME = fIRST_NAME;
	}
	public String getSECOND_NAME() {
		return SECOND_NAME;
	}
	public void setSECOND_NAME(String sECOND_NAME) {
		SECOND_NAME = sECOND_NAME;
	}
	public String getTHIRD_NAME() {
		return THIRD_NAME;
	}
	public void setTHIRD_NAME(String tHIRD_NAME) {
		THIRD_NAME = tHIRD_NAME;
	}
	public String getUN_LIST_TYPE() {
		return UN_LIST_TYPE;
	}
	public void setUN_LIST_TYPE(String uN_LIST_TYPE) {
		UN_LIST_TYPE = uN_LIST_TYPE;
	}
	public String getREFERENCE_NUMBER() {
		return REFERENCE_NUMBER;
	}
	public void setREFERENCE_NUMBER(String rEFERENCE_NUMBER) {
		REFERENCE_NUMBER = rEFERENCE_NUMBER;
	}
	public String getLISTED_ON() {
		return LISTED_ON;
	}
	public void setLISTED_ON(String lISTED_ON) {
		LISTED_ON = lISTED_ON;
	}
	public String getCOMMENTS1() {
		return COMMENTS1;
	}
	public void setCOMMENTS1(String cOMMENTS1) {
		COMMENTS1 = cOMMENTS1;
	}
	public String getGOODQUALITY() {
		return GOODQUALITY;
	}
	public void setGOODQUALITY(String gOODQUALITY) {
		GOODQUALITY = gOODQUALITY;
	}
	public String getPASSPORT() {
		return PASSPORT;
	}
	public void setPASSPORT(String pASSPORT) {
		PASSPORT = pASSPORT;
	}
	public NATIONALITY getNATIONALITY() {
		return NATIONALITY;
	}
	public void setNATIONALITY(NATIONALITY nATIONALITY) {
		NATIONALITY = nATIONALITY;
	}
	public LIST_TYPE getLIST_TYPE() {
		return LIST_TYPE;
	}
	public void setLIST_TYPE(LIST_TYPE lIST_TYPE) {
		LIST_TYPE = lIST_TYPE;
	}
	public LAST_DAY_UPDATED getLAST_DAY_UPDATED() {
		return LAST_DAY_UPDATED;
	}
	public void setLAST_DAY_UPDATED(LAST_DAY_UPDATED lAST_DAY_UPDATED) {
		LAST_DAY_UPDATED = lAST_DAY_UPDATED;
	}
	public TITLE getTITLE() {
		return TITLE;
	}
	public void setTITLE(TITLE tITLE) {
		TITLE = tITLE;
	}
	public String getGENDER() {
		return GENDER;
	}
	public void setGENDER(String gENDER) {
		GENDER = gENDER;
	}
	public String getADDRESS() {
		return ADDRESS;
	}
	public void setADDRESS(String aDDRESS) {
		ADDRESS = aDDRESS;
	}
	
}

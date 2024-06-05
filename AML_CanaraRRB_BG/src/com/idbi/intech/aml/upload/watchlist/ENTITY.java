package com.idbi.intech.aml.upload.watchlist;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD) 
public class ENTITY {
	public List<ENTITY_ADDRESS> ENTITY_ADDRESS;
	
	@XmlElement
	public Object SORT_KEY;
	@XmlElement
	public Object SORT_KEY_LAST_MOD;
	@XmlElement
	public String DATAID;
	@XmlElement
	public String VERSIONNUM;
	@XmlElement
	public String FIRST_NAME;
	@XmlElement
	public String UN_LIST_TYPE;
	@XmlElement
	public String REFERENCE_NUMBER;
	@XmlElement
	public String LISTED_ON;
	@XmlElement
	public String COMMENTS1;
	@XmlElement
	public String NAME_ORIGINAL_SCRIPT;
	@XmlElement
	public LIST_TYPE LIST_TYPE;
	@XmlElement
	public LAST_DAY_UPDATED LAST_DAY_UPDATED;
	@XmlElement
	public List<ENTITY_ALIAS> ENTITY_ALIAS;
	@XmlElement
	public String SUBMITTED_ON;
	
	public List<ENTITY_ADDRESS> getENTITY_ADDRESS() {
		return ENTITY_ADDRESS;
	}
	public void setENTITY_ADDRESS(List<ENTITY_ADDRESS> eNTITY_ADDRESS) {
		ENTITY_ADDRESS = eNTITY_ADDRESS;
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
	public String getFIRST_NAME() {
		return FIRST_NAME;
	}
	public void setFIRST_NAME(String fIRST_NAME) {
		FIRST_NAME = fIRST_NAME;
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
	public String getNAME_ORIGINAL_SCRIPT() {
		return NAME_ORIGINAL_SCRIPT;
	}
	public void setNAME_ORIGINAL_SCRIPT(String nAME_ORIGINAL_SCRIPT) {
		NAME_ORIGINAL_SCRIPT = nAME_ORIGINAL_SCRIPT;
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
	public List<ENTITY_ALIAS> getENTITY_ALIAS() {
		return ENTITY_ALIAS;
	}
	public void setENTITY_ALIAS(List<ENTITY_ALIAS> eNTITY_ALIAS) {
		ENTITY_ALIAS = eNTITY_ALIAS;
	}
	public String getSUBMITTED_ON() {
		return SUBMITTED_ON;
	}
	public void setSUBMITTED_ON(String sUBMITTED_ON) {
		SUBMITTED_ON = sUBMITTED_ON;
	}
	
	
}

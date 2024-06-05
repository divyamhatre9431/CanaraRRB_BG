package com.idbi.intech.aml.upload.watchlist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ENTITY_ADDRESS {
	
	@XmlElement
	public String COUNTRY;
	@XmlElement
	public String STREET;
	@XmlElement
	public String CITY;
	@XmlElement
	public String NOTE;
	@XmlElement
	public String ZIP_CODE;
	@XmlElement
	public String STATE_PROVINCE;
	
	public String getCOUNTRY() {
		return COUNTRY;
	}
	public void setCOUNTRY(String cOUNTRY) {
		COUNTRY = cOUNTRY;
	}
	public String getSTREET() {
		return STREET;
	}
	public void setSTREET(String sTREET) {
		STREET = sTREET;
	}
	public String getCITY() {
		return CITY;
	}
	public void setCITY(String cITY) {
		CITY = cITY;
	}
	public String getNOTE() {
		return NOTE;
	}
	public void setNOTE(String nOTE) {
		NOTE = nOTE;
	}
	public String getZIP_CODE() {
		return ZIP_CODE;
	}
	public void setZIP_CODE(String zIP_CODE) {
		ZIP_CODE = zIP_CODE;
	}
	public String getSTATE_PROVINCE() {
		return STATE_PROVINCE;
	}
	public void setSTATE_PROVINCE(String sTATE_PROVINCE) {
		STATE_PROVINCE = sTATE_PROVINCE;
	}
	
	
}

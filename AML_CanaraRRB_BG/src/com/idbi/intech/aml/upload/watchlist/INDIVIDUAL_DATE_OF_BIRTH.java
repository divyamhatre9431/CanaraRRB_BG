package com.idbi.intech.aml.upload.watchlist;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) 
public class INDIVIDUAL_DATE_OF_BIRTH {
	
	@XmlElement
	public String TYPE_OF_DATE;
	@XmlElement
	public String DATE;
	@XmlElement
	public String YEAR;
	@XmlElement
	public String FROM_YEAR;
	@XmlElement
	public String TO_YEAR;
	@XmlElement
	public String NOTE;
	
	public String getTYPE_OF_DATE() {
		return TYPE_OF_DATE;
	}
	public void setTYPE_OF_DATE(String tYPE_OF_DATE) {
		TYPE_OF_DATE = tYPE_OF_DATE;
	}
	public String getDATE() {
		return DATE;
	}
	public void setDATE(String dATE) {
		DATE = dATE;
	}
	public String getYEAR() {
		return YEAR;
	}
	public void setYEAR(String yEAR) {
		YEAR = yEAR;
	}
	public String getFROM_YEAR() {
		return FROM_YEAR;
	}
	public void setFROM_YEAR(String fROM_YEAR) {
		FROM_YEAR = fROM_YEAR;
	}
	public String getTO_YEAR() {
		return TO_YEAR;
	}
	public void setTO_YEAR(String tO_YEAR) {
		TO_YEAR = tO_YEAR;
	}
	public String getNOTE() {
		return NOTE;
	}
	public void setNOTE(String nOTE) {
		NOTE = nOTE;
	}
	
	
}

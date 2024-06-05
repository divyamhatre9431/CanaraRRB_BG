package com.idbi.intech.aml.upload.watchlist;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD) 
public class INDIVIDUAL_ALIAS {
	
	@XmlElement
	public String QUALITY;
	@XmlElement
	public String ALIAS_NAME;
	@XmlElement
	public String NOTE;
	@XmlElement
	public String DATE_OF_BIRTH;
	@XmlElement
	public String COUNTRY_OF_BIRTH;
	@XmlElement
	public String CITY_OF_BIRTH;
	
	public String getQUALITY() {
		return QUALITY;
	}
	public void setQUALITY(String qUALITY) {
		QUALITY = qUALITY;
	}
	public String getALIAS_NAME() {
		return ALIAS_NAME;
	}
	public void setALIAS_NAME(String aLIAS_NAME) {
		ALIAS_NAME = aLIAS_NAME;
	}
	public String getNOTE() {
		return NOTE;
	}
	public void setNOTE(String nOTE) {
		NOTE = nOTE;
	}
	public String getDATE_OF_BIRTH() {
		return DATE_OF_BIRTH;
	}
	public void setDATE_OF_BIRTH(String dATE_OF_BIRTH) {
		DATE_OF_BIRTH = dATE_OF_BIRTH;
	}
	public String getCOUNTRY_OF_BIRTH() {
		return COUNTRY_OF_BIRTH;
	}
	public void setCOUNTRY_OF_BIRTH(String cOUNTRY_OF_BIRTH) {
		COUNTRY_OF_BIRTH = cOUNTRY_OF_BIRTH;
	}
	public String getCITY_OF_BIRTH() {
		return CITY_OF_BIRTH;
	}
	public void setCITY_OF_BIRTH(String cITY_OF_BIRTH) {
		CITY_OF_BIRTH = cITY_OF_BIRTH;
	}
	
	
}

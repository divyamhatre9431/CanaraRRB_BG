package com.idbi.intech.aml.upload.watchlist;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD) 
public class INDIVIDUAL_DOCUMENT {
	
	@XmlElement
	public String TYPE_OF_DOCUMENT;
	@XmlElement
	public String NUMBER;
	@XmlElement
	public String ISSUING_COUNTRY;
	@XmlElement
	public String TYPE_OF_DOCUMENT2;
	@XmlElement
	public String CITY_OF_ISSUE;
	@XmlElement
	public String DATE_OF_ISSUE;
	@XmlElement
	public String NOTE;
	@XmlElement
	public String COUNTRY_OF_ISSUE;
	
	public String getTYPE_OF_DOCUMENT() {
		return TYPE_OF_DOCUMENT;
	}
	public void setTYPE_OF_DOCUMENT(String tYPE_OF_DOCUMENT) {
		TYPE_OF_DOCUMENT = tYPE_OF_DOCUMENT;
	}
	public String getNUMBER() {
		return NUMBER;
	}
	public void setNUMBER(String nUMBER) {
		NUMBER = nUMBER;
	}
	public String getISSUING_COUNTRY() {
		return ISSUING_COUNTRY;
	}
	public void setISSUING_COUNTRY(String iSSUING_COUNTRY) {
		ISSUING_COUNTRY = iSSUING_COUNTRY;
	}
	public String getTYPE_OF_DOCUMENT2() {
		return TYPE_OF_DOCUMENT2;
	}
	public void setTYPE_OF_DOCUMENT2(String tYPE_OF_DOCUMENT2) {
		TYPE_OF_DOCUMENT2 = tYPE_OF_DOCUMENT2;
	}
	public String getCITY_OF_ISSUE() {
		return CITY_OF_ISSUE;
	}
	public void setCITY_OF_ISSUE(String cITY_OF_ISSUE) {
		CITY_OF_ISSUE = cITY_OF_ISSUE;
	}
	public String getDATE_OF_ISSUE() {
		return DATE_OF_ISSUE;
	}
	public void setDATE_OF_ISSUE(String dATE_OF_ISSUE) {
		DATE_OF_ISSUE = dATE_OF_ISSUE;
	}
	public String getNOTE() {
		return NOTE;
	}
	public void setNOTE(String nOTE) {
		NOTE = nOTE;
	}
	public String getCOUNTRY_OF_ISSUE() {
		return COUNTRY_OF_ISSUE;
	}
	public void setCOUNTRY_OF_ISSUE(String cOUNTRY_OF_ISSUE) {
		COUNTRY_OF_ISSUE = cOUNTRY_OF_ISSUE;
	}
	
	
}

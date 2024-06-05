package com.idbi.intech.aml.upload.watchlist.sdn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class id { 
	
	@XmlElement
	public String uid;
	@XmlElement
	public String idType;
	@XmlElement
	public String idNumber;
	@XmlElement
	public String idCountry;
	@XmlElement
	public String expirationDate;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getIdCountry() {
		return idCountry;
	}
	public void setIdCountry(String idCountry) {
		this.idCountry = idCountry;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	
}

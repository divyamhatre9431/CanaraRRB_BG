package com.idbi.intech.aml.upload.watchlist.sdn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class citizenship { 
	
	@XmlElement
	public String uid;
	@XmlElement
	public String country;
	@XmlElement
	public String mainEntry;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getMainEntry() {
		return mainEntry;
	}
	public void setMainEntry(String mainEntry) {
		this.mainEntry = mainEntry;
	}
	
	
}

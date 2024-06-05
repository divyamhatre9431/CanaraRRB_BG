package com.idbi.intech.aml.upload.watchlist.sdn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class placeOfBirthItem { 
	
	@XmlElement
	public String uid;
	@XmlElement
	public String placeOfBirth;
	@XmlElement
	public String mainEntry;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public String getMainEntry() {
		return mainEntry;
	}
	public void setMainEntry(String mainEntry) {
		this.mainEntry = mainEntry;
	}
	
	
	
	
}

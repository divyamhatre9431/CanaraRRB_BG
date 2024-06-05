package com.idbi.intech.aml.upload.watchlist.sdn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class nationalityList { 
	
	@XmlElement
	public nationality nationality;

	public nationality getNationality() {
		return nationality;
	}

	public void setNationality(nationality nationality) {
		this.nationality = nationality;
	}
	
	
}

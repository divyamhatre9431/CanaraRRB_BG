package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) 
public class EntitiesCountries {
	@XmlElement
	private String CountryID;
	@XmlElement
	private String CountryName;
	
	public String getCountryID() {
		return CountryID;
	}
	public void setCountryID(String countryID) {
		CountryID = countryID;
	}
	public String getCountryName() {
		return CountryName;
	}
	public void setCountryName(String countryName) {
		CountryName = countryName;
	}
	
	

}

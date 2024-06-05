package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) 
public class EntitiesSources {
	@XmlElement
	private String SourceID;
	@XmlElement
	private String Country;
	@XmlElement
	private String SourceName;
	@XmlElement
	private String SourceAbbrev;
	
	public String getSourceID() {
		return SourceID;
	}
	public void setSourceID(String sourceID) {
		SourceID = sourceID;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public String getSourceName() {
		return SourceName;
	}
	public void setSourceName(String sourceName) {
		SourceName = sourceName;
	}
	public String getSourceAbbrev() {
		return SourceAbbrev;
	}
	public void setSourceAbbrev(String sourceAbbrev) {
		SourceAbbrev = sourceAbbrev;
	}
	
	

}

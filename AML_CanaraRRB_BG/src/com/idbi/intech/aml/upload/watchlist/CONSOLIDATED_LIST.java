package com.idbi.intech.aml.upload.watchlist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="CONSOLIDATED_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class CONSOLIDATED_LIST {
	
	@XmlElement
	public INDIVIDUALS INDIVIDUALS;
	@XmlElement
	public ENTITIES ENTITIES;
	@XmlAttribute
	public String dateGenerated;
	public String text;
	
	public INDIVIDUALS getINDIVIDUALS() {
		return INDIVIDUALS;
	}
	public void setINDIVIDUALS(INDIVIDUALS iNDIVIDUALS) {
		INDIVIDUALS = iNDIVIDUALS;
	}
	public ENTITIES getENTITIES() {
		return ENTITIES;
	}
	public void setENTITIES(ENTITIES eNTITIES) {
		ENTITIES = eNTITIES;
	}
	public String getDateGenerated() {
		return dateGenerated;
	}
	public void setDateGenerated(String dateGenerated) {
		this.dateGenerated = dateGenerated;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}

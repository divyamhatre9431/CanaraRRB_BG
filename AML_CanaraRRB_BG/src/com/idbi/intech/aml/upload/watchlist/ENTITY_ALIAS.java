package com.idbi.intech.aml.upload.watchlist;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) 
public class ENTITY_ALIAS {
	
	@XmlElement
	public String QUALITY;
	@XmlElement
	public String ALIAS_NAME;
	
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
	
	
}

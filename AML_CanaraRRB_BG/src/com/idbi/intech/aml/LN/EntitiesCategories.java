package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) 
public class EntitiesCategories {
	@XmlElement
	private String ID;
	@XmlElement
	private String EntryCategory;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getEntryCategory() {
		return EntryCategory;
	}
	public void setEntryCategory(String entryCategory) {
		EntryCategory = entryCategory;
	}
	
	

}

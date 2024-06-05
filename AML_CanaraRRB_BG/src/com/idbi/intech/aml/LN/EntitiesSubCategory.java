package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) 
public class EntitiesSubCategory {
	@XmlElement
	private String ID;
	@XmlElement
	private String EntrySubCategory;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getEntrySubCategory() {
		return EntrySubCategory;
	}
	public void setEntrySubCategory(String entrySubCategory) {
		EntrySubCategory = entrySubCategory;
	}
	
	
	

}

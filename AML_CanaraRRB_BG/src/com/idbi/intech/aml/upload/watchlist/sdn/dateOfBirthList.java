package com.idbi.intech.aml.upload.watchlist.sdn;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class dateOfBirthList { 
	
	@XmlElement
	public List<dateOfBirthItem> dateOfBirthItem;

	public List<dateOfBirthItem> getDateOfBirthItem() {
		return dateOfBirthItem;
	}

	public void setDateOfBirthItem(List<dateOfBirthItem> dateOfBirthItem) {
		this.dateOfBirthItem = dateOfBirthItem;
	}

	
}


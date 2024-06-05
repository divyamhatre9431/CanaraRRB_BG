package com.idbi.intech.aml.upload.watchlist.sdn;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class placeOfBirthList { 
	
	@XmlElement
	public List<placeOfBirthItem> placeOfBirthItem;

	public List<placeOfBirthItem> getPlaceOfBirthItem() {
		return placeOfBirthItem;
	}

	public void setPlaceOfBirthItem(List<placeOfBirthItem> placeOfBirthItem) {
		this.placeOfBirthItem = placeOfBirthItem;
	}

	
	
}

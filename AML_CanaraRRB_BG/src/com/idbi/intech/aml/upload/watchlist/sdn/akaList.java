package com.idbi.intech.aml.upload.watchlist.sdn;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class akaList {
	
	@XmlElement
	public List<aka> aka;

	public List<aka> getAka() {
		return aka;
	}

	public void setAka(List<aka> aka) {
		this.aka = aka;
	}

}

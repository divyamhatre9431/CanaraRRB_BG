package com.idbi.intech.aml.upload.watchlist.sdn;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class idList { 
	
	@XmlElement
	public List<id> id;

	public List<id> getId() {
		return id;
	}

	public void setId(List<id> id) {
		this.id = id;
	}
	
	
}

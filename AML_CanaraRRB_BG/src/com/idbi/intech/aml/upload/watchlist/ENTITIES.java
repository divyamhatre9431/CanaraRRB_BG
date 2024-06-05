package com.idbi.intech.aml.upload.watchlist;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD) 
public class ENTITIES {
	public List<ENTITY> ENTITY;

	public List<ENTITY> getENTITY() {
		return ENTITY;
	}

	public void setENTITY(List<ENTITY> eNTITY) {
		ENTITY = eNTITY;
	}
}

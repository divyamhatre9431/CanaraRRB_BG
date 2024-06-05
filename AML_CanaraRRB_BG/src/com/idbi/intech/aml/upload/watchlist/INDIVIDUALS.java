package com.idbi.intech.aml.upload.watchlist;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD) 
public class INDIVIDUALS {
	public List<INDIVIDUAL> INDIVIDUAL;

	public List<INDIVIDUAL> getINDIVIDUAL() {
		return INDIVIDUAL;
	}

	public void setINDIVIDUAL(List<INDIVIDUAL> iNDIVIDUAL) {
		INDIVIDUAL = iNDIVIDUAL;
	}
	
	
}

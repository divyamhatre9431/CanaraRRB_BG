package com.idbi.intech.aml.upload.watchlist.sdn;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="sdnList")
@XmlAccessorType(XmlAccessType.FIELD)
public class sdnList {
	
	@XmlElement
	public publshInformation publshInformation;
	public List<sdnEntry> sdnEntry;
	public String xsi;
	public String xmlns;
	public String text;
	
	public publshInformation getPublshInformation() {
		return publshInformation;
	}
	public void setPublshInformation(publshInformation publshInformation) {
		this.publshInformation = publshInformation;
	}
	
	public List<sdnEntry> getSdnEntry() {
		return sdnEntry;
	}
	public void setSdnEntry(List<sdnEntry> sdnEntry) {
		this.sdnEntry = sdnEntry;
	}
	public String getXsi() {
		return xsi;
	}
	public void setXsi(String xsi) {
		this.xsi = xsi;
	}
	public String getXmlns() {
		return xmlns;
	}
	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}

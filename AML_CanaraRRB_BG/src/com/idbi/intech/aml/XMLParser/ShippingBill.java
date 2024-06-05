package com.idbi.intech.aml.XMLParser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "shippingBill")
@XmlAccessorType(XmlAccessType.FIELD) 
public class ShippingBill {
	
	 @XmlElement
	 private String exportAgency;
	 @XmlElement
	 private String exportType;
	 @XmlElement
	 private String recordIndicator;
	 @XmlElement
	 private String portCode;
	 @XmlElement
	 private String shippingBillNo;
	 @XmlElement
	 private String shippingBillDate;
	 @XmlElement
	 private String LEODate;
	 @XmlElement
	 private String custNo;
	 @XmlElement
	 private String formNo;
	 @XmlElement
	 private String IECode;
	 @XmlElement
	 private String adCode;
	 @XmlElement
	 private String countryOfDestination;
	 @XmlElement(name="invoices")
	 private Invoices invoices;
	 
	 public String getExportAgency() {
		return exportAgency;
	}
	public void setExportAgency(String exportAgency) {
		this.exportAgency = exportAgency;
	}
	public String getExportType() {
		return exportType;
	}
	public void setExportType(String exportType) {
		this.exportType = exportType;
	}
	public String getRecordIndicator() {
		return recordIndicator;
	}
	public void setRecordIndicator(String recordIndicator) {
		this.recordIndicator = recordIndicator;
	}
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getShippingBillNo() {
		return shippingBillNo;
	}
	public void setShippingBillNo(String shippingBillNo) {
		this.shippingBillNo = shippingBillNo;
	}
	public String getShippingBillDate() {
		return shippingBillDate;
	}
	public void setShippingBillDate(String shippingBillDate) {
		this.shippingBillDate = shippingBillDate;
	}
	public String getLEODate() {
		return LEODate;
	}
	public void setLEODate(String lEODate) {
		LEODate = lEODate;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getFormNo() {
		return formNo;
	}
	public void setFormNo(String formNo) {
		this.formNo = formNo;
	}
	public String getIECode() {
		return IECode;
	}
	public void setIECode(String iECode) {
		IECode = iECode;
	}
	public String getAdCode() {
		return adCode;
	}
	public void setAdCode(String adCode) {
		this.adCode = adCode;
	}
	public String getCountryOfDestination() {
		return countryOfDestination;
	}
	public void setCountryOfDestination(String countryOfDestination) {
		this.countryOfDestination = countryOfDestination;
	}
	public Invoices getInvoices() {
		return invoices;
	}
	public void setInvoices(Invoices invoices) {
		this.invoices = invoices;
	}

}

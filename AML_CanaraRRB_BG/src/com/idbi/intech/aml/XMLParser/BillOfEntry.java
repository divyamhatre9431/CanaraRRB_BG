package com.idbi.intech.aml.XMLParser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "billOfEntry")
@XmlAccessorType(XmlAccessType.FIELD) 
public class BillOfEntry {
//	@XmlElement
//	private String billOfEntry;
	@XmlElement
	private String portOfDischarge="";
	@XmlElement
	private String importAgency="";
	@XmlElement
	private String billOfEntryNumber="";
	@XmlElement
	private String billOfEntryDate="";
	@XmlElement
	private String ADCode="";
	@XmlElement(name="G-P")
	private String G_P="";
	@XmlElement
	private String IECode="";
	@XmlElement
	private String changeIECode="";
	@XmlElement
	private String IEName="";
	@XmlElement
	private String IEAddress="";
	@XmlElement
	private String IEPANNumber="";
	@XmlElement
	private String portOfShipment="";
	@XmlElement
	private String IGMNumber="";
	@XmlElement
	private String IGMDate="";
	@XmlElement(name="MAWB-MBLNumber")
	private String MAWB_MBLNumber="";
	@XmlElement(name="MAWB-MBLDate")
	private String MAWB_MBLDate="";
	@XmlElement(name="HAWB-HBLNumber")
	private String HAWB_HBLNumber="";
	@XmlElement(name="HAWB-HBLDate")
	private String HAWB_HBLDate="";
	@XmlElement
	private String recordIndicator="";
	@XmlElement(name="invoices")
	private Invoices invoices;
	@XmlElement
	private String paymentParty="";
	@XmlElement
	private String paymentReferenceNumber="";
	@XmlElement
	private String outwardReferenceNumber="";
	@XmlElement
	private String outwardReferenceADCode="";
	@XmlElement
	private String remittanceCurrency="";
	@XmlElement
	private String billClosureIndicator="";
	
	
	public String getChangeIECode() {
		return changeIECode;
	}
	public void setChangeIECode(String changeIECode) {
		this.changeIECode = changeIECode;
	}
	public String getPaymentParty() {
		return paymentParty;
	}
	public void setPaymentParty(String paymentParty) {
		this.paymentParty = paymentParty;
	}
	public String getPaymentReferenceNumber() {
		return paymentReferenceNumber;
	}
	public void setPaymentReferenceNumber(String paymentReferenceNumber) {
		this.paymentReferenceNumber = paymentReferenceNumber;
	}
	public String getOutwardReferenceNumber() {
		return outwardReferenceNumber;
	}
	public void setOutwardReferenceNumber(String outwardReferenceNumber) {
		this.outwardReferenceNumber = outwardReferenceNumber;
	}
	public String getOutwardReferenceADCode() {
		return outwardReferenceADCode;
	}
	public void setOutwardReferenceADCode(String outwardReferenceADCode) {
		this.outwardReferenceADCode = outwardReferenceADCode;
	}
	public String getRemittanceCurrency() {
		return remittanceCurrency;
	}
	public void setRemittanceCurrency(String remittanceCurrency) {
		this.remittanceCurrency = remittanceCurrency;
	}
	public String getBillClosureIndicator() {
		return billClosureIndicator;
	}
	public void setBillClosureIndicator(String billClosureIndicator) {
		this.billClosureIndicator = billClosureIndicator;
	}
	public Invoices getInvoices() {
		return invoices;
	}
	public void setInvoices(Invoices invoices) {
		this.invoices = invoices;
	}
	public String getPortOfDischarge() {
		return portOfDischarge;
	}
	public void setPortOfDischarge(String portOfDischarge) {
		this.portOfDischarge = portOfDischarge;
	}
	public String getImportAgency() {
		return importAgency;
	}
	public void setImportAgency(String importAgency) {
		this.importAgency = importAgency;
	}
	public String getBillOfEntryNumber() {
		return billOfEntryNumber;
	}
	public void setBillOfEntryNumber(String billOfEntryNumber) {
		this.billOfEntryNumber = billOfEntryNumber;
	}
	public String getBillOfEntryDate() {
		return billOfEntryDate;
	}
	public void setBillOfEntryDate(String billOfEntryDate) {
		this.billOfEntryDate = billOfEntryDate;
	}
	public String getADCode() {
		return ADCode;
	}
	public void setADCode(String aDCode) {
		ADCode = aDCode;
	}
	public String getG_P() {
		return G_P;
	}
	public void setG_P(String g_P) {
		G_P = g_P;
	}
	public String getIECode() {
		return IECode;
	}
	public void setIECode(String iECode) {
		IECode = iECode;
	}
	public String getIEName() {
		return IEName;
	}
	public void setIEName(String iEName) {
		IEName = iEName;
	}
	public String getIEAddress() {
		return IEAddress;
	}
	public void setIEAddress(String iEAddress) {
		IEAddress = iEAddress;
	}
	public String getIEPANNumber() {
		return IEPANNumber;
	}
	public void setIEPANNumber(String iEPANNumber) {
		IEPANNumber = iEPANNumber;
	}
	public String getPortOfShipment() {
		return portOfShipment;
	}
	public void setPortOfShipment(String portOfShipment) {
		this.portOfShipment = portOfShipment;
	}
	public String getIGMNumber() {
		return IGMNumber;
	}
	public void setIGMNumber(String iGMNumber) {
		IGMNumber = iGMNumber;
	}
	public String getIGMDate() {
		return IGMDate;
	}
	public void setIGMDate(String iGMDate) {
		IGMDate = iGMDate;
	}
	public String getMAWB_MBLNumber() {
		return MAWB_MBLNumber;
	}
	public void setMAWB_MBLNumber(String mAWB_MBLNumber) {
		MAWB_MBLNumber = mAWB_MBLNumber;
	}
	public String getMAWB_MBLDate() {
		return MAWB_MBLDate;
	}
	public void setMAWB_MBLDate(String mAWB_MBLDate) {
		MAWB_MBLDate = mAWB_MBLDate;
	}
	public String getHAWB_HBLNumber() {
		return HAWB_HBLNumber;
	}
	public void setHAWB_HBLNumber(String hAWB_HBLNumber) {
		HAWB_HBLNumber = hAWB_HBLNumber;
	}
	public String getHAWB_HBLDate() {
		return HAWB_HBLDate;
	}
	public void setHAWB_HBLDate(String hAWB_HBLDate) {
		HAWB_HBLDate = hAWB_HBLDate;
	}
	public String getRecordIndicator() {
		return recordIndicator;
	}
	public void setRecordIndicator(String recordIndicator) {
		this.recordIndicator = recordIndicator;
	}
//	public String getBillOfEntry() {
//		return billOfEntry;
//	}
//	public void setBillOfEntry(String billOfEntry) {
//		this.billOfEntry = billOfEntry;
//	}



}

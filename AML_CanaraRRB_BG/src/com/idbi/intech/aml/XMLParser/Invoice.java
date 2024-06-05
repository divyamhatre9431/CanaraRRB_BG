package com.idbi.intech.aml.XMLParser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice {
	@XmlElement(name = "invoiceSerialNo")
	private String invoiceSerialNo="";
	@XmlElement
	private String invoiceNo="";
	@XmlElement
	private String invoiceDate="";
	@XmlElement
	private String FOBCurrencyCode="";
	@XmlElement
	private String FOBAmt="";
	@XmlElement
	private String commissionCurrencyCode="";
	@XmlElement
	private String commissionAmt="";
	@XmlElement
	private String discountCurrencyCode="";
	@XmlElement
	private String discountAmt="";
	@XmlElement
	private String deductionsCurrencyCode="";
	@XmlElement
	private String deductionsAmt="";
	@XmlElement
	private String packagingCurrencyCode="";
	@XmlElement
	private String packagingChargesAmt="";
	@XmlElement
	private String termsOfInvoice="";
	@XmlElement
	private String supplierName="";
	@XmlElement
	private String supplierAddress="";
	@XmlElement
	private String supplierCountry="";
	@XmlElement
	private String sellerName="";
	@XmlElement
	private String sellerAddress="";
	@XmlElement
	private String sellerCountry="";
	@XmlElement
	private String invoiceAmount="";
	@XmlElement
	private String invoiceCurrency="";
	@XmlElement
	private String freightAmount="";
	@XmlElement
	private String freightCurrencyCode="";
	@XmlElement
	private String insuranceAmount="";
	@XmlElement
	private String insuranceCurrencyCode="";
	@XmlElement
	private String agencyCommission="";
	@XmlElement
	private String agencyCurrency="";
	@XmlElement
	private String discountCharges="";
	@XmlElement
	private String discountCurrency="";
	@XmlElement
	private String miscellaneousCharges="";
	@XmlElement
	private String miscellaneousCurrency="";
	@XmlElement
	private String utilizedAmount="";
	@XmlElement
	private String thirdPartyName="";
	@XmlElement
	private String thirdPartyAddress="";
	@XmlElement
	private String thirdPartyCountry="";
	@XmlElement
	private String invoiceAmt="";
	@XmlElement
	private String invoiceAmtIc="";
	
	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getFOBCurrencyCode() {
		return FOBCurrencyCode;
	}

	public void setFOBCurrencyCode(String fOBCurrencyCode) {
		FOBCurrencyCode = fOBCurrencyCode;
	}

	public String getFOBAmt() {
		return FOBAmt;
	}

	public void setFOBAmt(String fOBAmt) {
		FOBAmt = fOBAmt;
	}

	public String getCommissionCurrencyCode() {
		return commissionCurrencyCode;
	}

	public void setCommissionCurrencyCode(String commissionCurrencyCode) {
		this.commissionCurrencyCode = commissionCurrencyCode;
	}

	public String getCommissionAmt() {
		return commissionAmt;
	}

	public void setCommissionAmt(String commissionAmt) {
		this.commissionAmt = commissionAmt;
	}

	public String getDiscountCurrencyCode() {
		return discountCurrencyCode;
	}

	public void setDiscountCurrencyCode(String discountCurrencyCode) {
		this.discountCurrencyCode = discountCurrencyCode;
	}

	public String getDiscountAmt() {
		return discountAmt;
	}

	public void setDiscountAmt(String discountAmt) {
		this.discountAmt = discountAmt;
	}

	public String getDeductionsCurrencyCode() {
		return deductionsCurrencyCode;
	}

	public void setDeductionsCurrencyCode(String deductionsCurrencyCode) {
		this.deductionsCurrencyCode = deductionsCurrencyCode;
	}

	public String getDeductionsAmt() {
		return deductionsAmt;
	}

	public void setDeductionsAmt(String deductionsAmt) {
		this.deductionsAmt = deductionsAmt;
	}

	public String getPackagingCurrencyCode() {
		return packagingCurrencyCode;
	}

	public void setPackagingCurrencyCode(String packagingCurrencyCode) {
		this.packagingCurrencyCode = packagingCurrencyCode;
	}

	public String getPackagingChargesAmt() {
		return packagingChargesAmt;
	}

	public void setPackagingChargesAmt(String packagingChargesAmt) {
		this.packagingChargesAmt = packagingChargesAmt;
	}

	

	public String getInvoiceAmt() {
		return invoiceAmt;
	}

	public void setInvoiceAmt(String invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}

	public String getInvoiceAmtIc() {
		return invoiceAmtIc;
	}

	public void setInvoiceAmtIc(String invoiceAmtIc) {
		this.invoiceAmtIc = invoiceAmtIc;
	}

	public String getInvoiceSerialNo() {
		return invoiceSerialNo;
	}

	public void setInvoiceSerialNo(String invoiceSerialNo) {
		this.invoiceSerialNo = invoiceSerialNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getTermsOfInvoice() {
		return termsOfInvoice;
	}

	public void setTermsOfInvoice(String termsOfInvoice) {
		this.termsOfInvoice = termsOfInvoice;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	public String getSupplierCountry() {
		return supplierCountry;
	}

	public void setSupplierCountry(String supplierCountry) {
		this.supplierCountry = supplierCountry;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSellerAddress() {
		return sellerAddress;
	}

	public void setSellerAddress(String sellerAddress) {
		this.sellerAddress = sellerAddress;
	}

	public String getSellerCountry() {
		return sellerCountry;
	}

	public void setSellerCountry(String sellerCountry) {
		this.sellerCountry = sellerCountry;
	}

	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public String getInvoiceCurrency() {
		return invoiceCurrency;
	}

	public void setInvoiceCurrency(String invoiceCurrency) {
		this.invoiceCurrency = invoiceCurrency;
	}

	public String getFreightAmount() {
		return freightAmount;
	}

	public void setFreightAmount(String freightAmount) {
		this.freightAmount = freightAmount;
	}

	public String getFreightCurrencyCode() {
		return freightCurrencyCode;
	}

	public void setFreightCurrencyCode(String freightCurrencyCode) {
		this.freightCurrencyCode = freightCurrencyCode;
	}

	public String getInsuranceAmount() {
		return insuranceAmount;
	}

	public void setInsuranceAmount(String insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public String getInsuranceCurrencyCode() {
		return insuranceCurrencyCode;
	}

	public void setInsuranceCurrencyCode(String insuranceCurrencyCode) {
		this.insuranceCurrencyCode = insuranceCurrencyCode;
	}

	public String getAgencyCommission() {
		return agencyCommission;
	}

	public void setAgencyCommission(String agencyCommission) {
		this.agencyCommission = agencyCommission;
	}

	public String getAgencyCurrency() {
		return agencyCurrency;
	}

	public void setAgencyCurrency(String agencyCurrency) {
		this.agencyCurrency = agencyCurrency;
	}

	public String getDiscountCharges() {
		return discountCharges;
	}

	public void setDiscountCharges(String discountCharges) {
		this.discountCharges = discountCharges;
	}

	public String getDiscountCurrency() {
		return discountCurrency;
	}

	public void setDiscountCurrency(String discountCurrency) {
		this.discountCurrency = discountCurrency;
	}

	public String getMiscellaneousCharges() {
		return miscellaneousCharges;
	}

	public void setMiscellaneousCharges(String miscellaneousCharges) {
		this.miscellaneousCharges = miscellaneousCharges;
	}

	public String getMiscellaneousCurrency() {
		return miscellaneousCurrency;
	}

	public void setMiscellaneousCurrency(String miscellaneousCurrency) {
		this.miscellaneousCurrency = miscellaneousCurrency;
	}

	public String getUtilizedAmount() {
		return utilizedAmount;
	}

	public void setUtilizedAmount(String utilizedAmount) {
		this.utilizedAmount = utilizedAmount;
	}

	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public void setThirdPartyName(String thirdPartyName) {
		this.thirdPartyName = thirdPartyName;
	}

	public String getThirdPartyAddress() {
		return thirdPartyAddress;
	}

	public void setThirdPartyAddress(String thirdPartyAddress) {
		this.thirdPartyAddress = thirdPartyAddress;
	}

	public String getThirdPartyCountry() {
		return thirdPartyCountry;
	}

	public void setThirdPartyCountry(String thirdPartyCountry) {
		this.thirdPartyCountry = thirdPartyCountry;
	}

}

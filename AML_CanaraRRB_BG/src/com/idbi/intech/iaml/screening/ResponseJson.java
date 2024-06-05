package com.idbi.intech.iaml.screening;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public final class ResponseJson {

	private String uniqueSessionKey;
	private String id;
	private int idType;
	private String name;
	private String addrType1;
	private String addr1;
	private String addrType2;
	private String addr2;
	private String addrType3;
	private String addr3;
	private String scanStatus;
	private String rejectReason;
	private String timeStamp;
	private Boolean errorFlg;
	private String requestString;
	private String responseString;
	

	public String getUniqueSessionKey() {
		return uniqueSessionKey;
	}

	public void setUniqueSessionKey(String uniqueSessionKey) {
		this.uniqueSessionKey = uniqueSessionKey;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIdType() {
		return idType;
	}

	public void setIdType(int idType) {
		this.idType = idType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddrType1() {
		return addrType1;
	}

	public void setAddrType1(String addrType1) {
		this.addrType1 = addrType1;
	}

	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getAddrType2() {
		return addrType2;
	}

	public void setAddrType2(String addrType2) {
		this.addrType2 = addrType2;
	}

	public String getAddr2() {
		return addr2;
	}

	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	public String getAddrType3() {
		return addrType3;
	}

	public void setAddrType3(String addrType3) {
		this.addrType3 = addrType3;
	}

	public String getAddr3() {
		return addr3;
	}

	public void setAddr3(String addr3) {
		this.addr3 = addr3;
	}

	public String getScanStatus() {
		return scanStatus;
	}

	public void setScanStatus(String scanStatus) {
		this.scanStatus = scanStatus;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Boolean getErrorFlg() {
		return errorFlg;
	}

	public void setErrorFlg(Boolean errorFlg) {
		this.errorFlg = errorFlg;
	}

	public String getRequestString() {
		return requestString;
	}

	public void setRequestString(String requestString) {
		this.requestString = requestString;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	@Override
	public String toString() {
		return "ResponseJson [uniqueSessionKey=" + uniqueSessionKey + ", id=" + id + ", idType=" + idType + ", name="
				+ name + ", addrType1=" + addrType1 + ", addr1=" + addr1 + ", addrType2=" + addrType2 + ", addr2="
				+ addr2 + ", addrType3=" + addrType3 + ", addr3=" + addr3 + ", scanStatus=" + scanStatus
				+ ", rejectReason=" + rejectReason + ", timeStamp=" + timeStamp + ", errorFlg=" + errorFlg
				+ ", requestString=" + requestString + ", responseString=" + responseString + "]";
	}


	
	
}

package com.idbi.intech.iaml.screening;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public final class RequestJson {

	private String uniqueSessionKey;
	private String idType;
	private String id;
	private String name;
	private String addrType1;
	private String addr1;

	private String addrType2;
	private String addr2;

	private String addrType3;
	private String addr3;

	private String reqString;
	
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

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
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

	public String getReqString() {
		return reqString;
	}

	public void setReqString(String reqString) {
		this.reqString = reqString;
	}

	@Override
	public String toString() {
		return "RequestJson [uniqueSessionKey=" + uniqueSessionKey + ", idType=" + idType + ", id=" + id + ", name="
				+ name + ", addrType1=" + addrType1 + ", addr1=" + addr1 + ", addrType2=" + addrType2 + ", addr2="
				+ addr2 + ", addrType3=" + addrType3 + ", addr3=" + addr3 + ", reqString=" + reqString + "]";
	}

	
}

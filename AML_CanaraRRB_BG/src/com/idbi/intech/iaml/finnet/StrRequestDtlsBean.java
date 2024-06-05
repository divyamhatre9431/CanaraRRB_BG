package com.idbi.intech.iaml.finnet;

import lombok.Data;


@Data
public class StrRequestDtlsBean {
	private String reqId;
	private String regReportType;
	private String batchNo;
	private String month;
	private String year;
	private String reqStatus;
	private String userId;
	private String lastUserId;
	private String lastModDt;
	private String makerId;
	private String makerDt;
	private String checkerId;
	private String checkerDt;
	private String verifyId;
	private String verifyDt;
	private String userRoleId;
	private String dwldFlg;
	private String amlRefNo;
	private String reqTime;
	private String recordStatus;
	private String actionStatus;
	private String finnetNumber;
	private String fileRefNo;
}

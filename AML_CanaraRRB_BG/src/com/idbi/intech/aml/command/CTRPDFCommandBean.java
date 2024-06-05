/**
 * © Copyright IDBI intech Limited
 * 
 * File Name  : CTRPDFCommandBean.java
 * Created By : Pradip Garala
 * 
 * Modification History
 * 
 * 28-03-2012	Pradip Garala		Initial version
 */

package com.idbi.intech.aml.command;

import com.idbi.intech.aml.dao.AMLUserDAO;

import com.idbi.intech.aml.databeans.CTRPdfDataBean;
import com.idbi.intech.aml.util.InfoLogger;

public class CTRPDFCommandBean {

	private InfoLogger infoLogger = null;
	private CTRPdfDataBean ctrPdfDataBean = new CTRPdfDataBean();

	/**
	 * 
	 * @return InfoLogger
	 */
	public InfoLogger getInfoLogger() {
		return infoLogger;
	}

	/**
	 * 
	 * @param infoLogger
	 */
	public void setInfoLogger(InfoLogger infoLogger) {
		this.infoLogger = infoLogger;
	}

	public void setCtrPdfDataBean(CTRPdfDataBean ctrPdfDataBean) {
		this.ctrPdfDataBean = ctrPdfDataBean;
	}

	public CTRPdfDataBean getCtrPdfDataBean() {
		return ctrPdfDataBean;
	}

	/**
	 * 
	 * @param reportId
	 * @param isMaster
	 * @param bankName
	 * @throws SqlDAOException
	 * @throws AMLRecordAlreadyExistException
	 */
	public void getCTRPdfData(String reportId,String reportType) throws Exception {
		infoLogger.logVerboseText("CTRPDFCommandBean", "getCTRPdfData", "Start");
		
		AMLUserDAO userDAO = new AMLUserDAO();
		this.ctrPdfDataBean = userDAO.getCtrPdfData(infoLogger, reportId,reportType);
		infoLogger.logVerboseText("CTRPDFCommandBean", "getCTRPdfData", "End");
	}

}

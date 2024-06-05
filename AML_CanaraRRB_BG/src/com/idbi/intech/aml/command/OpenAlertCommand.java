package com.idbi.intech.aml.command;

import com.idbi.intech.aml.dao.AMLUserDAO;
import com.idbi.intech.aml.databeans.STRConfigFileBean;
import com.idbi.intech.aml.databeans.STRWalkInControlDataBean;
import com.idbi.intech.aml.util.InfoLogger;

public class OpenAlertCommand {

	private InfoLogger logger;
	private STRConfigFileBean strConfigFileBean = new STRConfigFileBean();
    private STRWalkInControlDataBean controlDataBean = new STRWalkInControlDataBean();
	
	/**
	 * For Getting CTR Based On Perticular ReportID
	 * 
	 * @param reportId
	 * @param reportType
	 * @throws Exception
	 * 
	 */
	public void ctrXMLDetails(String reportId, String reportType, String isMaster) throws Exception {
		logger.logVerboseText("OpenAlertCommand", "ctrXMLDetails", "Start");
		AMLUserDAO userDAO = new AMLUserDAO();
		strConfigFileBean = userDAO.ctrXMLDetails(logger, reportId, reportType, isMaster);
		logger.logVerboseText("OpenAlertCommand", "ctrXMLDetails", "End");

	}

	
	public void strWalkInDetails() throws Exception {
	//	logger.logVerboseText("OpenAlertCommand", "strWalkInDetails", "Start");
		AMLUserDAO userDAO = new AMLUserDAO();
		controlDataBean=userDAO.strWalkInDetails();
		//logger.logVerboseText("OpenAlertCommand", "strWalkInDetails", "End");

	}
	
	
	// /**
	// * For getting NTR based on perticular ReportId
	// *
	// * @param reportId
	// */
	// public void ntrXMLDetails(String reportId) {
	// try {
	// AMLUserDAO userDAO = new AMLUserDAO();
	// configFileBean = userDAO.ntrXMLDetails(reportId, logger);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public InfoLogger getLogger() {
		return logger;
	}

	public void setLogger(InfoLogger logger) {
		this.logger = logger;
	}

	public STRConfigFileBean getConfigFileBean() {
		return strConfigFileBean;
	}

	public void setConfigFileBean(STRConfigFileBean strConfigFileBean) {
		this.strConfigFileBean = strConfigFileBean;
	}


	public void setControlDataBean(STRWalkInControlDataBean controlDataBean) {
		this.controlDataBean = controlDataBean;
	}


	public STRWalkInControlDataBean getControlDataBean() {
		return controlDataBean;
	}
}

package com.idbi.intech.aml.bg_process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ResourceBundle;

import com.idbi.intech.aml.command.CTRPDFCommandBean;
import com.idbi.intech.aml.dao.AMLUserDAO;
import com.idbi.intech.aml.databeans.CTRPdfDataBean;
import com.idbi.intech.aml.util.InfoLogger;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;


public class RegulatoryReportsPdf {

	
	public static void generatePdfFiles(InfoLogger logger, String reportId,String reportType) {

		ResourceBundle rb = ResourceBundle.getBundle("AMLProp");
		
		String filepath =""; 
			
		if(reportType.equalsIgnoreCase("CTR"))
		{
			filepath=rb.getString("CTR_PDF_DIR");	
		}
		else if(reportType.equalsIgnoreCase("NTR"))
		{
			filepath=rb.getString("NTR_PDF_DIR");
		}else if ( reportType.equalsIgnoreCase("SIDBI")){
			filepath=rb.getString("SIDBICTR_PDF_DIR");
		}

		try {

			CTRPDFCommandBean commandBean = new CTRPDFCommandBean();
			commandBean.setInfoLogger(logger);

			new File(filepath).mkdir();

			String pdfName = reportId + ".pdf";

			String templatePath="";
			
			if(reportType.equalsIgnoreCase("CTR") || reportType.equalsIgnoreCase("SIDBI"))
			{
				templatePath=filepath+"//CBAS.pdf";
			}
			else
			{
				templatePath=filepath+"//NPR.pdf";
			}
			
			PdfReader pdfTemplate = new PdfReader(new FileInputStream(new File(
					templatePath)));//D:\\JavaWS\\NEW_AML_WS\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp1\\wtpwebapps\\AML\\WEB-INF\\STRFiles\\
			FileOutputStream fileOutputStream = new FileOutputStream(filepath + pdfName);

			PdfStamper stamper = new PdfStamper(pdfTemplate, fileOutputStream);
			stamper.setFormFlattening(true);

			filepath = filepath + pdfName;

			CTRPdfDataBean ctrDataBean = new CTRPdfDataBean();

			try {
				commandBean.getCTRPdfData(reportId,reportType);
				ctrDataBean = commandBean.getCtrPdfDataBean();
			} catch (Exception e) {
				logger.logExceptionText("CTRPdf_servlet", "performTask : bean fill up", "Exception" + e);
			}

			// part 1

			stamper.getAcroFields().setField("Month1", ctrDataBean.getMonth1());
			stamper.getAcroFields().setField("Month2", ctrDataBean.getMonth2());
			stamper.getAcroFields().setField("Year1", ctrDataBean.getYear1());
			stamper.getAcroFields().setField("Year", ctrDataBean.getYear2());

			stamper.getAcroFields().setField("SupplementaryYN", ctrDataBean.getSupplementaryYN());

			stamper.getAcroFields().setField("OrDate1", ctrDataBean.getOriginalReportDate1());
			stamper.getAcroFields().setField("OrDate2", ctrDataBean.getOriginalReportDate2());
			stamper.getAcroFields().setField("OrMonth1", ctrDataBean.getOriginalReportMonth1());
			stamper.getAcroFields().setField("OrMonth2", ctrDataBean.getOriginalReportMonth2());
			stamper.getAcroFields().setField("OrYear", ctrDataBean.getOriginalReportYear1());
			stamper.getAcroFields().setField("OrYear1", ctrDataBean.getOriginalReportYear2());

			// part 2

			stamper.getAcroFields().setField("NameEntity", rb.getString("NameOfBank"));
			stamper.getAcroFields().setField("UniqueCode", rb.getString("BSRCode"));
			stamper.getAcroFields().setField("IDFIU", rb.getString("IdAllotedByFIU"));

			stamper.getAcroFields().setField("CategoryCode", rb.getString("CategoryOfBank"));
			stamper.getAcroFields().setField("NamePrincipalOfficer", rb.getString("NameOfPrincipalOfficer"));
			stamper.getAcroFields().setField("DesignationPrincipalOfficer", rb.getString("Designation"));

			stamper.getAcroFields().setField("PrincipalAddress1", rb.getString("Address"));
			stamper.getAcroFields().setField("PrincipalAddress2", rb.getString("Street"));
			stamper.getAcroFields().setField("PrincipalAddress3", rb.getString("Locality"));
			stamper.getAcroFields().setField("PrincipalAddress4", rb.getString("City"));
			stamper.getAcroFields().setField("PrincipalAddress5", rb.getString("State"));

			stamper.getAcroFields().setField("PrincipalPIN", rb.getString("Pin"));
			stamper.getAcroFields().setField("PrincipalTel", rb.getString("Tel"));
			stamper.getAcroFields().setField("PrincipalFax", rb.getString("Fax"));
			stamper.getAcroFields().setField("PrincipalEmail", rb.getString("Email"));

			// part 3

			stamper.getAcroFields().setField("BranchesTotal", ctrDataBean.getBranchesTotal());
			stamper.getAcroFields().setField("BranchesReported", ctrDataBean.getBranchesReported());
			stamper.getAcroFields().setField("BranchesCTR", ctrDataBean.getBranchesCTR());
			stamper.getAcroFields().setField("CTROriginal", ctrDataBean.getCtrOriginal());
			stamper.getAcroFields().setField("CTRReplacement", ctrDataBean.getCtrReplacement());
			stamper.getAcroFields().setField("CTRForMonth", ctrDataBean.getCtrForMonth());

			stamper.close();
			pdfTemplate.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.logExceptionText("CTRPdf_servlet", "performTask", "Exception" + e);
		}

	}
	
	public static void generateExcelFile(InfoLogger logger, String reportId,String reportType) throws Exception {
		logger.logVerboseText("RegulatoryReportsPdf", "generateExcelFile", " Start");
		AMLUserDAO userDao = new AMLUserDAO();
		
		
		userDao.generateExcelFile(logger, reportId, reportType);
		
		logger.logVerboseText("RegulatoryReportsPdf", "generateExcelFile", " End");
	}
	
}

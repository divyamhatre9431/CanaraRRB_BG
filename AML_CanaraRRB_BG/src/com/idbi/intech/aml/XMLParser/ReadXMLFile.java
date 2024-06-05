package com.idbi.intech.aml.XMLParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class ReadXMLFile {

	public void readFile(File file, String source, String destination) {
		JAXBContext jaxbContext = null;
		Unmarshaller um = null;
		try {
			jaxbContext = JAXBContext.newInstance(Bank.class);
			um = jaxbContext.createUnmarshaller();
			Bank bank = (Bank) um.unmarshal(file);
			System.out.println("file.getName() : " + file.getName());
			bank.setFileName(file.getName());
			ReadXMLFile.insertFileDetails(bank, source, destination);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public static void insertFileDetails(Bank bank, String source,
			String destination) {
		Connection conn = null;
		Statement stmtinsert = null, stmtinsertEntry = null, stmtinsertInvoice = null, stmtinsertShipmnt = null, stmtinsrtShipmntInv = null;
		String fileId = null;

		try {
			conn = ConnectionFactory.makeConnectionAMLLive();
			stmtinsert = conn.createStatement();

			fileId = ReadXMLFile.insertIntoBOEMaster(conn, stmtinsert, bank);

			if (bank.getCheckSum().getNoOfbillOfEntry() > 0) {
				stmtinsertEntry = conn.createStatement();
				stmtinsertInvoice = conn.createStatement();
				for (BillOfEntrys entrys : bank.getBillOfEntrys()) {
					for (BillOfEntry entry : entrys.getBillOfEntry()) {

						ReadXMLFile.insertIntoBOE(conn, stmtinsertEntry, entry,
								fileId);

						for (Invoice invoice : entry.getInvoices().getInvoice()) {
							ReadXMLFile.insertIntoBOEInvoice(conn,
									stmtinsertInvoice, invoice,
									entry.getBillOfEntryNumber());
						}
					}
				}
			}
			if (bank.getCheckSum().getNoOfShippingBills() > 0) {
				stmtinsertShipmnt = conn.createStatement();
				stmtinsrtShipmntInv = conn.createStatement();
				for (ShippingBills shippingBills : bank.getShippingBills()) {
					for (ShippingBill singleBill : shippingBills
							.getShippingBill()) {
						ReadXMLFile.insertIntoShippment(conn,
								stmtinsertShipmnt, singleBill, fileId);
						for (Invoice invoice : singleBill.getInvoices()
								.getInvoice()) {
							ReadXMLFile.insertIntoShippmentInvoice(conn,
									stmtinsrtShipmntInv, invoice,
									singleBill.getShippingBillNo());
						}
					}
				}

			}
			try {
				Files.move(Paths.get(source), Paths.get(destination),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("File moved." + source + "destination : "
					+ destination);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String insertIntoBOEMaster(Connection conn,
			Statement stmtinsert, Bank bank) {
		Statement stmtSeq = null;
		int billCount = 0;
		ResultSet rs = null;
		String fileId = null;
		try {
			billCount = bank.getCheckSum().getNoOfbillOfEntry() == 0 ? bank
					.getCheckSum().getNoOfShippingBills() : bank.getCheckSum()
					.getNoOfbillOfEntry();

			stmtSeq = conn.createStatement();
			rs = stmtSeq
					.executeQuery("select lpad(BOEFILE.nextval,5,0) from dual");
			rs.next();
			fileId = rs.getString(1);
			stmtinsert.executeUpdate("insert into aml_boe_master values('"
					+ fileId + "','" + bank.getFileName() + "',sysdate,"
					+ billCount + "," + bank.getCheckSum().getNoOfInvoices()
					+ ",'')");
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmtSeq != null) {
					stmtSeq.close();
					stmtSeq = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return fileId;
	}

	public static void insertIntoBOE(Connection conn,
			Statement stmtinsertEntry, BillOfEntry entry, String fileId) {
		try {
			stmtinsertEntry.executeQuery("insert into aml_boe values('"
					+ fileId + "','" + entry.getBillOfEntryNumber() + "','"
					+ entry.getPortOfDischarge() + "','"
					+ entry.getImportAgency() + "','"
					+ ReadXMLFile.getDateFormat(entry.getBillOfEntryDate())
					+ "','" + entry.getADCode() + "','" + entry.getG_P()
					+ "','" + entry.getIECode() + "','" + entry.getIEName()
					+ "','" + entry.getIEAddress() + "','"
					+ entry.getIEPANNumber() + "','"
					+ entry.getPortOfShipment() + "','" + entry.getIGMNumber()
					+ "','" + ReadXMLFile.getDateFormat(entry.getIGMDate())
					+ "','" + entry.getMAWB_MBLNumber() + "','"
					+ ReadXMLFile.getDateFormat(entry.getMAWB_MBLDate())
					+ "','" + entry.getHAWB_HBLNumber() + "','"
					+ ReadXMLFile.getDateFormat(entry.getHAWB_HBLDate())
					+ "','" + entry.getRecordIndicator() + "','"
					+ entry.getPaymentParty() + "','"
					+ entry.getPaymentReferenceNumber() + "','"
					+ entry.getOutwardReferenceNumber() + "','"
					+ entry.getOutwardReferenceADCode() + "','"
					+ entry.getRemittanceCurrency() + "','"
					+ entry.getBillClosureIndicator() + "')");
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void insertIntoBOEInvoice(Connection conn,
			Statement stmtinsertInvoice, Invoice invoice, String billEntryNumber) {
		try {
			if (!invoice.getInvoiceAmt().equals("")) {
				invoice.setInvoiceAmount(invoice.getInvoiceAmt());
			}

			stmtinsertInvoice
					.executeUpdate("insert into aml_boe_invoice values('"
							+ invoice.getInvoiceNo() + "','" + billEntryNumber
							+ "','" + invoice.getInvoiceSerialNo() + "','"
							+ invoice.getTermsOfInvoice() + "','"
							+ invoice.getSupplierName() + "','"
							+ invoice.getSupplierAddress() + "','"
							+ invoice.getSupplierCountry() + "','"
							+ invoice.getSellerName() + "','"
							+ invoice.getSellerAddress() + "','"
							+ invoice.getSellerCountry() + "','"
							+ invoice.getInvoiceAmount() + "','"
							+ invoice.getInvoiceCurrency() + "','"
							+ invoice.getFreightAmount() + "','"
							+ invoice.getFreightCurrencyCode() + "','"
							+ invoice.getInsuranceAmount() + "','"
							+ invoice.getInsuranceCurrencyCode() + "','"
							+ invoice.getAgencyCommission() + "','"
							+ invoice.getAgencyCurrency() + "','"
							+ invoice.getDiscountCharges() + "','"
							+ invoice.getDiscountCurrency() + "','"
							+ invoice.getMiscellaneousCharges() + "','"
							+ invoice.getMiscellaneousCurrency() + "','"
							+ invoice.getUtilizedAmount() + "','"
							+ invoice.getThirdPartyName() + "','"
							+ invoice.getThirdPartyAddress() + "','"
							+ invoice.getThirdPartyCountry() + "','"
							+ invoice.getInvoiceAmtIc() + "')");
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void insertIntoShippment(Connection conn,
			Statement stmtinsertShipmnt, ShippingBill shippingBill,
			String fileId) {
		try {
			stmtinsertShipmnt
					.executeUpdate("insert into aml_shippment values('"
							+ fileId
							+ "','"
							+ shippingBill.getShippingBillNo()
							+ "','"
							+ shippingBill.getExportAgency()
							+ "','"
							+ shippingBill.getExportType()
							+ "','"
							+ shippingBill.getRecordIndicator()
							+ "','"
							+ shippingBill.getPortCode()
							+ "','"
							+ ReadXMLFile.getDateFormat(shippingBill
									.getShippingBillDate())
							+ "','"
							+ ReadXMLFile.getDateFormat(shippingBill
									.getLEODate()) + "','"
							+ shippingBill.getCustNo() + "','"
							+ shippingBill.getFormNo() + "','"
							+ shippingBill.getIECode() + "','"
							+ shippingBill.getAdCode() + "','"
							+ shippingBill.getCountryOfDestination() + "')");
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void insertIntoShippmentInvoice(Connection conn,
			Statement stmtinsrtShipmntInv, Invoice invoice, String shipBillNo) {
		try {
			stmtinsrtShipmntInv
					.executeUpdate("insert into aml_shippment_invoices values('"
							+ invoice.getInvoiceNo()
							+ "','"
							+ shipBillNo
							+ "','"
							+ ReadXMLFile.getDateFormat(invoice
									.getInvoiceDate())
							+ "','"
							+ invoice.getFOBCurrencyCode()
							+ "','"
							+ invoice.getFOBAmt()
							+ "','"
							+ invoice.getFreightCurrencyCode()
							+ "','"
							+ invoice.getFreightAmount()
							+ "','"
							+ invoice.getInsuranceCurrencyCode()
							+ "','"
							+ invoice.getInsuranceAmount()
							+ "','"
							+ invoice.getCommissionCurrencyCode()
							+ "','"
							+ invoice.getCommissionAmt()
							+ "','"
							+ invoice.getDiscountCurrencyCode()
							+ "','"
							+ invoice.getDiscountAmt()
							+ "','"
							+ invoice.getDeductionsCurrencyCode()
							+ "','"
							+ invoice.getDeductionsAmt()
							+ "','"
							+ invoice.getPackagingCurrencyCode()
							+ "','"
							+ invoice.getPackagingChargesAmt() + "')");
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getDateFormat(String dateinString) {
		Date date = null;
		String finalDate = "";
		try {
			if (dateinString != null) {
				if (!dateinString.equals("")) {
					date = new SimpleDateFormat("dd/MM/yyyy")
							.parse(dateinString);
					finalDate = new SimpleDateFormat("dd-MMM-yyyy")
							.format(date);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return finalDate;
	}

	public static void main(String[] args) {
		// ReadXMLFile read = new ReadXMLFile();
		System.out.println(getDateFormat("25/09/2018"));
	}

}

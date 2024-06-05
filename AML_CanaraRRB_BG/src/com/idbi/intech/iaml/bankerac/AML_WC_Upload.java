package com.idbi.intech.iaml.bankerac;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class AML_WC_Upload{
	public AML_WC_Upload(String filename, Connection conn) {
		try {
			new AML_WC_DeleteExist().readXML(filename, conn);
			System.out.println("Deleted existing record");
			conn.commit();
			new AML_WC_Master().readXML(filename, conn);
			System.out.println("Master Table Updated");
			new AML_WC_Name().readXML(filename, conn);
			System.out.println("Name Table Updated");
			new AML_WC_OrgSrc().readXML(filename, conn);
			System.out.println("Original Source Table Updated");
			new AML_WC_EntType().readXML(filename, conn);
			System.out.println("Entity Type Table Updated");
			new AML_WC_List().readXML(filename, conn);
			System.out.println("List Table Updated");
			new AML_WC_Alias().readXML(filename, conn);
			System.out.println("Alias Table Updated");
			new AML_WC_Address().readXML(filename, conn);
			System.out.println("Address Table Updated");
			new AML_WC_ID().readXML(filename, conn);
			System.out.println("Id Table Updated");
			new AML_WC_ChildId().readXML(filename, conn);
			System.out.println("Child Id Table Updated");
			new AML_WC_DOB().readXML(filename, conn);
			System.out.println("DOB Table Updated");
			new AML_WC_SDF().readXML(filename, conn);
			System.out.println("SDF Table Updated");
			new AML_WC_Title().readXML(filename, conn);
			System.out.println("Title Table Updated");
			new AML_WC_POB().readXML(filename, conn);
			System.out.println("POB Table Updated");
			new AML_WC_Nationality().readXML(filename, conn);
			System.out.println("Nationality Table Updated");
			conn.commit();
			new AML_WC_Dedup().deDupVal(conn);
			System.out.println("De-Duplication Completed");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (XMLStreamException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@SuppressWarnings("null")
	public static void enterLog(Connection con_aml, String update_type,
			String feed_type) {
		PreparedStatement pstmt_insert = null;
		try {
			pstmt_insert = con_aml
					.prepareStatement("insert into aml_wl_ctrl values('SYSTEM',sysdate,?,'Y',?,'Daily','D')");
			pstmt_insert.setString(1, update_type);
			pstmt_insert.setString(2, feed_type);
			pstmt_insert.executeUpdate();
			con_aml.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmt_insert == null) {
					pstmt_insert.close();
					pstmt_insert = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		Connection con_aml = null;
		File file = null;
		String extension = "";
		ResourceBundle rb = ResourceBundle.getBundle("AMLProp");
		String rootpath_feed = rb.getString("UPLOAD_WC_DATA_FILE");
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLive();
			con_aml.setAutoCommit(false);
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.exit(0);
		}
		try {
			try {
				file = new File(rootpath_feed);
				String fname[] = file.list();
				for (String element : fname) {
					int dotPos = element.lastIndexOf(".");
					extension = element.substring(dotPos);
					if (extension.equalsIgnoreCase(".xml")) {
						System.out.println("file ::: "+element);
						new AML_WC_Upload(rootpath_feed + element, con_aml);
						file = new File(rootpath_feed + element);
						file.delete();
						enterLog(con_aml, "Feed", element);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (con_aml != null) {
					con_aml.close();
					con_aml = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
}

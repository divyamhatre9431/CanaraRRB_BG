package com.idbi.intech.iaml.bankerac;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class AML_WC_SDF {
	static final String record = "entity";
	static final String uid = "id";

	PreparedStatement pstmt = null;

	String id = "";
	String ename = "";
	String name = "";

	public void readXML(String file, Connection connection)
			throws FileNotFoundException, XMLStreamException {
		try {
			pstmt = connection
					.prepareStatement("insert into aml_wl_sdf values (?,?,?)");
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = new FileInputStream(file);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {

					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart() == record) {
						id = "";

						@SuppressWarnings("unchecked")
						Iterator<Attribute> attributes = startElement
								.getAttributes();

						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals(uid)) {
								id = attribute.getValue();
							}
						}
					}
				}

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (startElement.getName().getLocalPart().equals("sdf")) {
						name = "";

						@SuppressWarnings("unchecked")
						Iterator<Attribute> attributes = startElement
								.getAttributes();

						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals("name")) {
								name = attribute.getValue();
							}
						}

						ename = eventReader.getElementText();

						if (!ename.equals("")) {
							pstmt.setString(1, id);
							pstmt.setString(2, name.toUpperCase());
							pstmt.setString(3, ename.toUpperCase());

							pstmt.executeUpdate();
							// System.out.println(id + "~" + ename);

							ename = "";
						}
						// System.out.println(id+"~"+ename);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}

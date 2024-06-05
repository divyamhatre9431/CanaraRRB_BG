package com.idbi.intech.aml.XMLParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class ReadCSVFile {

	public static void parseCSV(File file, String source, String destination,
			String delimiter) {

		Connection conn = null;
		Statement stmtinsert = null, stmtinsertEntry = null;

		Bank bank = new Bank();
		CheckSum checkSum = new CheckSum();
		bank.setFileName(file.getName());
		bank.setCheckSum(checkSum);
		BillOfEntry billOfEntry = new BillOfEntry();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();

			conn = ConnectionFactory.makeConnectionAMLLive();
			stmtinsert = conn.createStatement();
			stmtinsertEntry = conn.createStatement();

			String fileId = ReadXMLFile.insertIntoBOEMaster(conn, stmtinsert,
					bank);
			String[] fields = null;
			while ((line = br.readLine()) != null && !line.isEmpty()) {
				// System.out.println(line);
				line = line.replace("|", "~");
				fields = line.split("~");
				billOfEntry.setBillOfEntryNumber(fields[0]);
				billOfEntry.setBillOfEntryDate(fields[1]);
				billOfEntry.setPortOfDischarge(fields[2]);
				// System.out.println(fields[0]+" : "+fields[1]+" : "+fields[2]);
				ReadXMLFile.insertIntoBOE(conn, stmtinsertEntry, billOfEntry,
						fileId);
			}
			br.close();
			Files.move(Paths.get(source), Paths.get(destination),
					StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File moved." + source + "destination : "
					+ destination);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// public static void main(String args[]){
	//
	// parseCSV(file, source, destination, delimiter);
	//
	// }
}

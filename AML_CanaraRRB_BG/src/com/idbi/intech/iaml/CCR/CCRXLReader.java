package com.idbi.intech.iaml.CCR;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.idbi.intech.iaml.factory.ConnectionFactory;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class CCRXLReader {
	private static Connection conn=null;
	
	public static void makeConnection() {
		try {
			conn = ConnectionFactory.makeConnectionAMLLiveThread();
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}
	
	public void readXLFile(String path){
		PreparedStatement stmt=null;
		Statement delstmt=null;
		File file=null;
		Workbook workbook=null;
		Sheet sheet=null;
		try{
			file=new File(path);
			delstmt=conn.createStatement();
			delstmt.executeUpdate("delete from aml_ccr_tbl");
			conn.commit();
			System.out.println("Inserting the data...");
			stmt=conn.prepareStatement("insert into aml_ccr_tbl values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			workbook=Workbook.getWorkbook(file);
			for(int sheetno=0;sheetno<workbook.getNumberOfSheets();sheetno++)
            {
				sheet=workbook.getSheet(sheetno);
                for(int i=1;i<sheet.getRows();i++)
                {
                	 int col=sheet.getColumns();
                	 int k=1;
                     for(int j=0;j<col;j++)
                     {
                         Cell cell=sheet.getCell(j, i);
                         System.out.println(cell.getContents());
                         stmt.setString(k,cell.getContents());
                         k++;
                     }
                     stmt.executeUpdate();
                     k=1;
                }
            }
			workbook.close();
			conn.commit();
			delstmt.executeUpdate("delete from aml_ccr_tbl where branch_cc is null");
			conn.commit();
			
			System.out.println("Excel Data inserted sucessfully...");
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(conn!=null){
					conn.close();
					conn=null;
				}
				if(stmt!=null){
					stmt.close();
					stmt=null;
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}	
	}
	
	public static void main(String args[]){
		CCRXLReader xlreader=new CCRXLReader();
		makeConnection();
		ResourceBundle bundle = ResourceBundle.getBundle("com.idbi.intech.iaml.CCR.ccrdtl");
		String dir = bundle.getString("CCREXL");
		String filename="";
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		try{
			System.out.println("Enter the filename of CCR Excel");
			filename=br.readLine();
			xlreader.readXLFile(dir+filename+".xls");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
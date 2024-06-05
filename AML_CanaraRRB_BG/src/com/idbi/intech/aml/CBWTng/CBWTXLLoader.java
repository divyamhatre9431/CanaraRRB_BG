package com.idbi.intech.aml.CBWTng;

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

public class CBWTXLLoader {
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
		String data="";
		try{
			file=new File(path);
			delstmt=conn.createStatement();
			delstmt.executeUpdate("delete from aml_cbwt_wcc");
			conn.commit();
			System.out.println("Inserting the data...");
			stmt=conn.prepareStatement("insert into aml_cbwt_wcc values(?,?,to_date(?,'dd-mm-yy'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
                         if(j==2){
                        	 //System.out.println(cell.getContents());
                        	 data=cell.getContents();
                         }else{
                        	 data=cell.getContents();
                         }
                         stmt.setString(k,data);
                         k++;
                     }
                     stmt.executeUpdate();
                     k=1;
                }
            }
			workbook.close();
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
		CBWTXLLoader xlreader=new CBWTXLLoader();
		makeConnection();
		ResourceBundle bundle = ResourceBundle.getBundle("com.idbi.intech.aml.CBWTng.cbwtdtl");
		String dir = bundle.getString("CBWTXLDIR");
		String filename="";
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		try{
			System.out.println("***NOTE: Excel file should be in .xls format***");
			System.out.println("Enter the filename of CBWT Excel");
			filename=br.readLine();
			xlreader.readXLFile(dir+filename+".xls");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}


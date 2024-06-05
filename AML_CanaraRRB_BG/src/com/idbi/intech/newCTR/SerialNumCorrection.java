package com.idbi.intech.newCTR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

public class SerialNumCorrection {

	public static void main(String args[]) {
		ResourceBundle bundle = ResourceBundle
				.getBundle("com.idbi.intech.aml.CBWTng.cbwtdtl");
		String dir = bundle.getString("CBWTDIR");
		String newdir=bundle.getString("NEWCBWTDIR");
		BufferedReader brfile = null;
		BufferedReader br = null;
		FileWriter f1 =null;
		int srl = 0;
		String batch="";
		String filename="";
		try {
			br=new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("Enter the Serial number");
			srl=Integer.parseInt(br.readLine());
			
			System.out.println("Enter the new batch number");
			batch=br.readLine();
			
			//f1 = new FileWriter(newdir+"CBWT_EFT"+batch+".xml");
			f1 = new FileWriter(newdir+"NTR_ARF1222_1"+".xml");
			
			System.out.println("Enter the Filename for serial number correction");
			filename=br.readLine();
			
			String sCurrentLine;

			brfile = new BufferedReader(new FileReader(dir+filename+".xml"));

			while ((sCurrentLine = brfile.readLine()) != null) {
				//System.out.println(sCurrentLine);
				
				if(sCurrentLine.contains("/BatchNumber")){
					sCurrentLine = "<BatchNumber>"+batch+"</BatchNumber>";
				}
				
				if(sCurrentLine.contains("/ReportSerialNum"))
				{
					srl++;
					sCurrentLine = "<ReportSerialNum>"+srl+"</ReportSerialNum>";
					
				}
					

				f1.write(sCurrentLine+"\n");
				

			}
			System.out.println("Last Serial No. "+srl);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (brfile != null)
					brfile.close();
				if(f1!=null){
					f1.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}

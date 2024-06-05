package com.idbi.intech.iaml.misc;

import java.io.BufferedReader;
import java.io.FileReader;

public class TestGOSLength {	
	public static void main(String args[]){
		String filetxt="";
		String gos="";
		BufferedReader br=null;
		try{
		br=new BufferedReader(new FileReader("d:\\AML\\GOSTest\\gosTest.txt"));
		while((filetxt=br.readLine())!=null)
		gos+=filetxt;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
			if(br!=null){
				br.close();
			}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		System.out.println("Length of GOS : "+gos.length());
	}

}

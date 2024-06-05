package com.idbi.intech.iaml.swift;

import java.util.ArrayList;

public class Tester {

	/**
	 * @param args
	 */
	
	public void getTest(){
		ArrayList<String> arrNoise  =  new ArrayList<String>();
		arrNoise.add("TRUST OF ARAB");
		arrNoise.add("Co");
		arrNoise.add("United Nations ");
		
		ArrayList<String> tSet  =  new ArrayList<String>();
		tSet.add("United");
		tSet.add("Nations ");
		
		boolean flgNoise = false;
		boolean preFlgNoise = false;
		boolean finalResult = false;
		
		for(String noise : arrNoise){
			flgNoise = false;
			String []noiseColl = (noise.trim()).split(" ");
			
			for(String noiseSplit : noiseColl){
				//System.out.println("DATAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa :: "+noiseSplit);
				boolean finalFlg = false;
				boolean prevFinal =  true;
				boolean prevFlg = false;
				
				for(String word : tSet){
					
					//System.out.println("Word :: "+word);
					//System.out.println("noiseSplit :: "+noiseSplit);
					
					//System.out.println("prevFlg :: "+prevFlg);
					//System.out.println("noiseSplit.equalsIgnoreCase(word) :: "+noiseSplit.equalsIgnoreCase(word));
					
					
					finalFlg = prevFlg || (noiseSplit.equalsIgnoreCase(word.trim()));
					
					prevFlg = finalFlg;
					
					//System.out.println("Final :: "+finalFlg);
				}
				
				flgNoise = finalFlg && prevFinal;
				
				prevFinal = finalFlg;
				
			}
			
		
			//System.out.println("flgNoise :: "+flgNoise);
			//System.out.println("preFlgNoise :: "+preFlgNoise);
			
			finalResult = flgNoise || preFlgNoise;
			
			//System.out.println("flgNoise1 :: "+flgNoise);
			//System.out.println("preFlgNoise1 :: "+preFlgNoise);
			
			preFlgNoise = finalResult;
		}
		
		System.out.println("FinalResult :: "+finalResult);
		
		String data = "hdgfhdgfsdgfhsdgfhgsdfhgsdhjvghsdfghjdsgvch bcn bsdhvbhjsdvbhj vcnvbuidscgsdhfjsdvghsdbjsdgfhjsdhfsfvhasdbjhgdsfhgsdhjfgdshfghsdgfhjdsgfhsdgfhjgsdfhjgsdfhfgsdhgfiuyhf;dhgfysdgfhjsdfhgsdhfgdhfgsdjdhfidhfjhwefoweyfuitqjfgsdfhjdfhjsdhfjksahfjiagdjfhwejdfhjksdhfjadgvhjgsdfhsdfgsdjfgsdhjfgasdjfgsdjfgsdjtfjdsfhjasdhfuiwhjfhjdshfjadhfkhdkfljdslkfjlkdsjf";
		
		System.out.println(data.length());
		
	}
	
	public static void main(String[] args) {
		new Tester().getTest();

	}

}

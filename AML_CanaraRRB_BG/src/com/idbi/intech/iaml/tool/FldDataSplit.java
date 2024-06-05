package com.idbi.intech.iaml.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class FldDataSplit {

	
	public ArrayList<Set<String>> splitFld(String msg, int cnt) {
		SortedSet<String> hsToken = null;
		ArrayList<String> word = new ArrayList<String>();
		String line = "";
		ArrayList<Set<String>> al_Set = null;
		// int i1 = 0;

		BufferedReader br = new BufferedReader(new StringReader(msg));
		try {
			while ((line = br.readLine()) != null) {
				// i1++;
				// System.out.println(i1 +" : "+ line);
				StringTokenizer stToken = new StringTokenizer(line, " ");
				al_Set = new ArrayList<Set<String>>();
				while (stToken.hasMoreTokens()) {
					word.add(stToken.nextToken());
				}
			}
			// System.out.println(word);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int s = word.size() - cnt;
		if (s > cnt) {
			for (int i = 0; i < s; i++) {
				hsToken = new TreeSet<String>();
				for (int j = i; j <= i + cnt; j++) {
					if (j < word.size()) {
						hsToken.add(word.get(j));
					}
				}
				al_Set.add(hsToken);
			}
		} else {
			hsToken = new TreeSet<String>();
			for (String w : word) {
				hsToken.add(w);
			}
			al_Set.add(hsToken);
		}

		return al_Set;
	}
}

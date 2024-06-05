package com.idbi.intech.iaml.screening;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class NameMatchCheck {

	public static void main(String args[]) {
		NameMatchCheck checkName = new NameMatchCheck();
		checkName.checkMatch();
	}

	public void checkMatch() {
		Connection connection = null;
		Statement stmtMca = null, stmtName = null;
		ResultSet rsMca = null, rsName = null;

		String mcaListQuery = "", nameListQuery = "", query = "";
		int matchPercent = 0;
		//Date startDate = new Date();
		try {
			connection = ConnectionFactory.makeConnectionAMLLiveThread();
			stmtName = connection.createStatement();
			stmtMca = connection.createStatement();
			stmtMca.setFetchSize(4000);

			// idbi
			// nameListQuery =
			// "select cust_name from namelist where flg ='N' and match_percent is null";
			
			// dubai
			nameListQuery = "select cust_name from amldname";
			rsName = stmtName.executeQuery(nameListQuery);
			while (rsName.next()) {

				String name = rsName.getString(1).toUpperCase();
			
			//String name = "ESSAR OIL LTD";
			
				//System.out.println("cust name : " + name);
				String nameVal[] = name.replace("'", "\''").split("\\s+");
				int totalLgth = name.length();
				int percentLgth = (int) Math.round(totalLgth * 0.2);
				int minNameLgth = totalLgth - percentLgth;
				int maxNameLgth = totalLgth + percentLgth;
				
				// idbi
				/*
				 * mcaListQuery=
				 * "select cust_name from mcalist where length(cust_name) between "
				 * + minNameLgth + " and " + maxNameLgth; mcaListQuery+=
				 * " and ("; for(int i=0;i<nameVal.length;i++){
				 * if(nameVal[i].length()>=3){ if(nameVal.length==1)
				 * mcaListQuery += "instr(cust_name,'"+nameVal[i]+"')>0  "; else
				 * //query += " cust_name like '%"+nameVal[i]+"%' or";
				 * mcaListQuery += " instr(cust_name,'"+nameVal[i]+"')>0 or"; }
				 * } mcaListQuery = mcaListQuery.substring(0,
				 * mcaListQuery.length()-2); mcaListQuery+=") ";
				 */
				
				// dubai
				mcaListQuery = "select upper(name),person_id from aml_wc_name where length(name) between "
						+ minNameLgth + " and " + maxNameLgth;
				mcaListQuery += " and (";
				for (int i = 0; i < nameVal.length; i++) {
					if (nameVal[i].length() >= 3) {
						if (nameVal.length == 1)
							mcaListQuery += "instr(upper(name),'" + nameVal[i]
									+ "')>0  ";
						else
							// query += " cust_name like '%"+nameVal[i]+"%' or";
							mcaListQuery += " instr(upper(name),'" + nameVal[i]
									+ "')>0 or";
					}
				}
				mcaListQuery = mcaListQuery.substring(0,
						mcaListQuery.length() - 2);
				mcaListQuery += ") union ";
				mcaListQuery +="select upper(name),person_id from aml_wc_alias where length(name) between "
						+ minNameLgth + " and " + maxNameLgth;
				mcaListQuery += " and (";
				for (int i = 0; i < nameVal.length; i++) {
					if (nameVal[i].length() >= 3) {
						if (nameVal.length == 1)
							mcaListQuery += "instr(upper(name),'" + nameVal[i]
									+ "')>0  ";
						else
							// query += " cust_name like '%"+nameVal[i]+"%' or";
							mcaListQuery += " instr(upper(name),'" + nameVal[i]
									+ "')>0 or";
					}
				}
				mcaListQuery = mcaListQuery.substring(0,
						mcaListQuery.length() - 2);
				mcaListQuery += ")";
				//System.out.println(mcaListQuery);
				// System.out.println(startDate);
				// idbi
				// query="update namelist set flg='Y' where cust_name='"+name.replace("'",
				// "\''")+"'";
				// System.out.println(query);
				// stmtMca.executeUpdate(query);
				connection.commit();

				rsMca = stmtMca.executeQuery(mcaListQuery);
				while (rsMca.next()) {
					matchPercent = 0;
					/*sortPercent = 0;
					percent = 0;*/
					matchPercent = FuzzySearch.tokenSetRatio(
							name.toUpperCase(), rsMca.getString(1)
									.toUpperCase());
					//System.out.println(matchPercent);
					if (matchPercent >= 90) {
						// System.out.println(rsMca.getString(1)+" : "+
						// matchPercent+" : "+sortPercent +" : " +" : "+percent
						// +" : "+ "match found");
						System.out.println("name : "+rsMca.getString(1) + " personId : "+rsMca.getString(2)+ " : "
								+ matchPercent + " : " + name.toUpperCase() + " match found");
						
						//idbi
						/*query = "update namelist set flg='P',mca_list_name='"
								+ rsMca.getString(1).replace("'", "\''")
								+ "',match_percent=" + matchPercent
								+ " where cust_name='"
								+ name.replace("'", "\''") + "'";
						// System.out.println(query);
						stmtMca.executeUpdate(query);*/
						
						//dubai
						query = "insert into amldowname(person_name,matchpercent,cust_name,personid) values('"+rsMca.getString(1)+"',"+matchPercent+",'"+name.toUpperCase()+"','"+rsMca.getString(2)+"')";
				// System.out.println(query);
				stmtMca.executeUpdate(query);
						
						connection.commit();
					}
				}
				// System.out.println(name);

			}
			//Date endDate = new Date();
			// System.out.println(endDate);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		}

	}

}

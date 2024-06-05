package com.idbi.intech.iaml.bankerac;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AML_WC_Dedup {
	
	public void deDupVal(Connection connection){
		Statement st = null;
		try{
			st = connection.createStatement();
			st.executeUpdate("DELETE FROM aml_wl_name WHERE rowid not in (SELECT MIN(rowid) FROM aml_wl_name GROUP BY person_id)");
			connection.commit();
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			try{
				if(st!=null){
					st.close();
					st=null;
				}
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
	}

}

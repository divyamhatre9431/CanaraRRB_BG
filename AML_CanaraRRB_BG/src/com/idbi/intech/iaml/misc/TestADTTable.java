package com.idbi.intech.iaml.misc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class TestADTTable {

	public static void main(String args[]) {
		try {
			int days = 0;
			Connection connection = ConnectionFactory
					.makeConnectionAMLLiveThread();

			ResultSet rs = connection
					.createStatement()
					.executeQuery(
							"select no_of_days from aml_rule_master where rule_id = 'IBA0N1_A'");
			while (rs.next()) {
				days = rs.getInt(1);
			}

			connection
					.createStatement()
					.executeUpdate(
							"insert into aml_adt_kra (SELECT DISTINCT table_key,audit_date "
									+ " FROM adt@live_fin a"
									+ " WHERE table_name = 'CMG' and " 
									+ " audit_date BETWEEN  ( TO_DATE ('11-apr-2015', "
									+ "                              'dd-mon-yyyy') - "
									+ days
									+ ")"
									+ "                   + (0 / 24 / 60 / 60) "
									+ "              AND   ( TO_DATE ('11-apr-2015', "
									+ "                              'dd-mon-yyyy') - "
									+ days
									+ ")"
									+ "                  + (1 - 1 / 24 / 60 / 60)"
									+ " AND (   INSTR (UPPER (modified_fields_data),"
									+ "          'CUST_COMU_ADDR') > 0"
									+ "  OR INSTR (UPPER (modified_fields_data),"
									+ "  'CUST_PERM_ADDR') > 0"
									+ "   OR INSTR (UPPER (modified_fields_data), 'CUST_PAGER') >"
									+ "  0"
									+ "OR INSTR (UPPER (modified_fields_data),"
									+ "         'CUST_PERM_PHONE') > 0"
									+ "OR INSTR (UPPER (modified_fields_data),"
									+ "         'CUST_COMU_PHONE') > 0"
									+ "OR INSTR (UPPER (modified_fields_data), 'EMAIL_ID') > 0)"
									+ " AND func_code = 'M')");
			
			
			
			connection.commit();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}

package com.idbi.intech.iaml.misc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class RoleTest {

	public void getMenuCollection() {
		ResultSet rs = null;
		ResultSet rs_sMenu = null;
		ResultSet rs_node = null;
		ArrayList<String> submenu = new ArrayList<String>();

		Connection connection = null;
		try {
			connection = ConnectionFactory.makeConnectionAMLLive();
			// System.out.println("Over here ");
			LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> hsMenu = null;
			LinkedHashMap<String, ArrayList<String>> hsNode = null;
			ArrayList<String> endNode = null;
			ArrayList<LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>>> arrMenu = new ArrayList<LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>>>();
			rs = connection
					.createStatement()
					.executeQuery(
							"select seq||'~'||menu_name as menu_id from aml_menu_seq order by seq");
			while (rs.next()) {
				String menu_id = rs.getString("menu_id");
				String menu_info[] = menu_id.split("~");
				rs_sMenu = connection
						.createStatement()
						.executeQuery(
								"select subseq||'~'||submenu as sub_menu_id from aml_menu_sub_seq a,aml_menu_seq b where a.seq=b.seq and a.seq='"
										+ menu_info[0] + "'");
				hsNode = new LinkedHashMap<String, ArrayList<String>>();
				while (rs_sMenu.next()) {
					String sMenu = rs_sMenu.getString("sub_menu_id");
					String subMenu_info[] = sMenu.split("~");
					rs_node = connection
							.createStatement()
							.executeQuery(
									"select name||'~'||a.path as end_node from aml_menu_end_seq a,aml_menu_seq b where a.seq=b.seq and a.subseq='"
											+ subMenu_info[0]
											+ "' and b.seq = '"
											+ menu_info[0]
											+ "'");
					endNode = new ArrayList<String>();
					while (rs_node.next()) {
						endNode.add(rs_node.getString("end_node"));
					}
					hsNode.put(sMenu, endNode);
				}
				hsMenu = new LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>>();
				hsMenu.put(menu_id, hsNode);
				arrMenu.add(hsMenu);
			}
			System.out.println("Array Menu :: " + arrMenu);
			// beanObj.setArrMenu(arrMenu);

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (connection == null) {
					connection.close();
					connection = null;
				}
				if (rs == null) {
					rs.close();
					rs = null;
				}
				if (rs_sMenu == null) {
					rs_sMenu.close();
					rs_sMenu = null;
				}
				if (rs_node == null) {
					rs_node.close();
					rs_node = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		new RoleTest().getMenuCollection();
	}

}

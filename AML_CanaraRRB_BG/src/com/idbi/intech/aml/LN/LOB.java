package com.idbi.intech.aml.LN;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

public class LOB implements AutoCloseable {
    private final Connection connection;
    private final List<Blob> blobs;
    private final List<Clob> clobs;
    public LOB(Connection connection) {
        this.connection = connection;
        this.blobs = new ArrayList();
        this.clobs = new ArrayList();
    }
    public final Blob blob(byte[] bytes) 
    throws SQLException {
        Blob blob;
        // You may write more robust dialect 
        // detection here
        if (connection.getMetaData()
                      .getDatabaseProductName()
                      .toLowerCase()
                      .contains("oracle")) {
            blob = BLOB.createTemporary(connection, 
                       false, BLOB.DURATION_SESSION);
        }
        else {
            blob = connection.createBlob();
        }
        blob.setBytes(1, bytes);
        blobs.add(blob);
        return blob;
    }
    public final Clob clob(String string) 
    throws SQLException {
        Clob clob;
        if (connection.getMetaData()
                      .getDatabaseProductName()
                      .toLowerCase()
                      .contains("oracle")) {
            clob = CLOB.createTemporary(connection, 
                       false, CLOB.DURATION_SESSION);
        }
        else {
            clob = connection.createClob();
        }
        clob.setString(1, string);
        clobs.add(clob);
        return clob;
    }
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
    
}
package org.project.utils.jdbc;

import java.sql.Connection;
 
public class WrapperConnection {

	/** 数据库连接*/
	private Connection connection = null;
	/** 此连接是否正在使用的标志，默认没有正在使用  */  
    private boolean busy = false;  

    public WrapperConnection(Connection connection) {  
        this.connection = connection;  
    }  

    public Connection getConnection() {  
        return connection;  
    }  

    public void setConnection(Connection connection) {  
        this.connection = connection;  
    }  

    public boolean isBusy() {  
        return busy;  
    }  

    public void setBusy(boolean busy) {  
        this.busy = busy;  
    }  
    
}

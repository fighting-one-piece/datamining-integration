package org.project.utils.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.project.utils.ReflectUtils;

public class ConnectionPool {
	
	private Logger logger = Logger.getLogger(ConnectionPool.class);

	/** 数据库驱动 */
	private String jdbcDriver = "";
	/** 数据库URL*/
    private String dbUrl = "";
    /** 数据库用户名*/
    private String dbUsername = "";
    /** 数据库用户密码 */
    private String dbPassword = "";
    /** 测试连接是否可用的测试表名，默认没有测试表*/
    private String testTable = "";
    /** 连接池的初始大小*/
    private int initialConnections = 10; 
    /** 连接池自动增加的大小*/
    private int incrementalConnections = 5;
    /** 连接池最大的大小*/
    private int maxConnections = 50;
    /** 存放连接池中数据库连接的向量*/
    private Vector<WrapperConnection> connectionPool = null;
  
    /** 
     * 构造函数 
     * @param jdbcDriver String JDBC 驱动类串 
     * @param dbUrl String 数据库 URL 
     * @param dbUsername String 连接数据库用户名 
     * @param dbPassword String 连接数据库用户的密码 
     */  
    public ConnectionPool(String jdbcDriver, String dbUrl, String dbUsername, String dbPassword) {  
        this.jdbcDriver = jdbcDriver;  
        this.dbUrl = dbUrl;  
        this.dbUsername = dbUsername;  
        this.dbPassword = dbPassword;  
        try {  
            createPool();  
        } catch (Exception e) {  
        	logger.info(e.getMessage(), e);
        }   
    } 
    
    /** 
     * 构造函数 
     * @param jdbcDriver String JDBC 驱动类串 
     * @param dbUrl String 数据库 URL 
     * @param dbUsername String 连接数据库用户名 
     * @param dbPassword String 连接数据库用户的密码 
     * @param dbPassword String 连接数据库用户的密码 
     * @param dbPassword String 连接数据库用户的密码 
     */  
    public ConnectionPool(String jdbcDriver, String dbUrl, String dbUsername, String dbPassword,
    		int initialConnections, int maxConnections) {  
        this.jdbcDriver = jdbcDriver;  
        this.dbUrl = dbUrl;  
        this.dbUsername = dbUsername;  
        this.dbPassword = dbPassword;  
        this.initialConnections = initialConnections;
        this.maxConnections = maxConnections;
        try {  
            createPool();  
        } catch (Exception e) {  
        	logger.info(e.getMessage(), e);
        }   
    }  
  
    /** 
     * 返回连接池的初始大小 
     * @return 初始连接池中可获得的连接数量 
     */  
    public int getInitialConnections() {  
        return this.initialConnections;  
    }  
  
    /** 
     * 设置连接池的初始大小 
     * @param 用于设置初始连接池中连接的数量 
     */  
    public void setInitialConnections(int initialConnections) {  
        this.initialConnections = initialConnections;  
    }  
  
    /** 
     * 返回连接池自动增加的大小 、 
     * @return 连接池自动增加的大小 
     */  
    public int getIncrementalConnections() {  
        return this.incrementalConnections;  
    }  
  
    /** 
     * 设置连接池自动增加的大小 
     * @param 连接池自动增加的大小 
     */  
    public void setIncrementalConnections(int incrementalConnections) {  
        this.incrementalConnections = incrementalConnections;  
    }  
  
    /** 
     * 返回连接池中最大的可用连接数量 
     * @return 连接池中最大的可用连接数量 
     */  
    public int getMaxConnections() {  
        return this.maxConnections;  
    }  
  
    /** 
     * 设置连接池中最大可用的连接数量 
     * @param 设置连接池中最大可用的连接数量值 
     */  
    public void setMaxConnections(int maxConnections) {  
        this.maxConnections = maxConnections;  
    }  
  
    /** 
     * 获取测试数据库表的名字 
     * @return 测试数据库表的名字 
     */  
    public String getTestTable() {  
        return this.testTable;  
    }  
  
    /** 
     * 设置测试表的名字 
     * @param testTable String 测试表的名字 
     */  
    public void setTestTable(String testTable) {  
        this.testTable = testTable;  
    }  
  
    /** 
     * 创建一个数据库连接池，连接池中的可用连接的数量采用类成员initialConnections 中设置的值 
     */  
    public synchronized void createPool() {  
        // 确保连接池没有创建  如果连接池己经创建了，保存连接的向量 connections 不会为空  
        if (connectionPool != null) {  
            return; 
        }  
		try {
			Driver driver = (Driver) (Class.forName(this.jdbcDriver).newInstance());
			DriverManager.registerDriver(driver); //注册 JDBC 驱动程序  
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
        connectionPool = new Vector<WrapperConnection>();  
        createConnections(this.initialConnections);  
        logger.info(" 数据库连接池创建成功！ ");  
    }  
  
    /** 
     * 创建由 numConnections 指定数目的数据库连接 , 并把这些连接放入 connectionPool 向量中 
     * @param numConnections 要创建的数据库连接的数目 
     */  
    private void createConnections(int numConnections) {  
        for (int x = 0; x < numConnections; x++) {  
            // 是否连接池中的数据库连接的数量己经达到最大？最大值由类成员 maxConnections指出，如果 maxConnections 为 0 或负数，
        	//表示连接数量没有限制。 如果连接数己经达到最大，即退出。  
            if (this.maxConnections > 0  && this.connectionPool.size() >= this.maxConnections) {  
                break;  
            }  
            try {  
                connectionPool.addElement(new WrapperConnection(newConnection()));  
            } catch (SQLException e) {  
                logger.info(e.getMessage(), e); 
            }  
            logger.info(" 数据库连接己创建 ......");  
        }  
    }  
  
    /** 
     * 创建一个新的数据库连接并返回它 
     * @return 返回一个新创建的数据库连接 
     */  
    private Connection newConnection() throws SQLException {  
        Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);  
        if (connectionPool.size() == 0) {  
            DatabaseMetaData metaData = conn.getMetaData();  
            int driverMaxConnections = 0;
            if (ReflectUtils.isExistField(metaData, "maxConnections")) {
            	driverMaxConnections = metaData.getMaxConnections();  
            }
            // 数据库返回的 driverMaxConnections 若为 0 ，表示此数据库没有最大连接限制，或数据库的最大连接限制不知道driverMaxConnections 为返回的一个整数，表示此数据库允许客户连接的数目  
            // 如果连接池中设置的最大连接数量大于数据库允许的连接数目 , 则置连接池的最大连接数目为数据库允许的最大数目  
            if (driverMaxConnections > 0 && this.maxConnections > driverMaxConnections) {  
                this.maxConnections = driverMaxConnections;  
            }  
        }  
        return conn;
    }  
  
    /** 
     * 通过调用 getFreeConnection() 函数返回一个可用的数据库连接 , 
     * 如果当前没有可用的数据库连接，并且更多的数据库连接不能创建（如连接池大小的限制），此函数等待一会再尝试获取。 
     * @return 返回一个可用的数据库连接对象 
     */  
    public synchronized Connection getConnection() throws SQLException {  
        if (connectionPool == null) {  
            return null;
        }  
        Connection conn = getFreeConnection();  
        while (conn == null) {  
            wait(250);  
            conn = getFreeConnection(); 
        }  
        return conn; 
    }  
  
    /** 
     * 本函数从连接池向量 connections 中返回一个可用的的数据库连接，如果 当前没有可用的数据库连接，本函数则根据 incrementalConnections
     * 设置的值创建几个数据库连接，并放入连接池中。 如果创建后，所有的连接仍都在使用中，则返回 null 
     * @return 返回一个可用的数据库连接 
     */  
    private Connection getFreeConnection() throws SQLException {  
        Connection conn = findFreeConnection();  
        if (conn == null) {  
            createConnections(incrementalConnections);  
            // 重新从池中查找是否有可用连接  
            conn = findFreeConnection();  
            if (conn == null) {  
                return null;  
            }  
        }  
        return conn;  
    }  
  
    /** 
     * 查找连接池中所有的连接，查找一个可用的数据库连接， 如果没有可用的连接，返回 null 
     * @return 返回一个可用的数据库连接 
     */  
    private Connection findFreeConnection() throws SQLException {  
        Connection conn = null;  
        WrapperConnection pConn = null;  
        Enumeration<WrapperConnection> enumerate = connectionPool.elements();  
        while (enumerate.hasMoreElements()) {  
            pConn = (WrapperConnection) enumerate.nextElement();  
            if (!pConn.isBusy()) {  
                conn = pConn.getConnection();  
                pConn.setBusy(true);  
                // 测试此连接是否可用  
                if (!testConnection(conn)) {  
                    // 如果此连接不可再用了，则创建一个新的连接， 并替换此不可用的连接对象，如果创建失败，返回 null  
                    try {  
                        conn = newConnection();  
                    } catch (SQLException e) {  
                        logger.info(e.getMessage(), e);  
                        return null;  
                    }  
                    pConn.setConnection(conn);  
                }  
                break;
            }  
        }  
        return conn;
    }  
  
    /** 
     * 测试一个连接是否可用，如果不可用，关掉它并返回 false否则可用返回 true 
     * @param conn 需要测试的数据库连接 
     * @return 返回 true 表示此连接可用， false 表示不可用 
     */  
    private boolean testConnection(Connection conn) {  
        try {  
            if (testTable.equals("")) {  
                // 如果测试表为空，试着使用此连接的 setAutoCommit() 方法 来判断连接否可用（此方法只在部分数据库可用，如果不可用 ,抛出异常）
                conn.setAutoCommit(true);  
            } else {
                Statement stmt = conn.createStatement();  
                stmt.execute("select count(*) from " + testTable);  
            }  
        } catch (SQLException e) {  
            closeConnection(conn);  
            return false;  
        }  
        return true;  
    }  
  
    /** 
     * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。 
     * 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。 
     * @param 需返回到连接池中的连接对象 
     */  
    public void returnConnection(Connection conn) {  
        if (connectionPool == null) {  
            logger.info(" 连接池不存在，无法返回此连接到连接池中 !");  
            return;  
        }  
        WrapperConnection pConn = null;  
        Enumeration<WrapperConnection> enumerate = connectionPool.elements();  
        // 遍历连接池中的所有连接，找到这个要返回的连接对象  
        while (enumerate.hasMoreElements()) {  
            pConn = (WrapperConnection) enumerate.nextElement();  
            if (conn == pConn.getConnection()) {  
                pConn.setBusy(false);  
                break;  
            }  
        }  
    }  
  
    /** 刷新连接池中所有的连接对象 */  
    public synchronized void refreshConnections() throws SQLException {  
        if (connectionPool == null) {  
            logger.info(" 连接池不存在，无法刷新 !");  
            return;  
        }  
        WrapperConnection pConn = null;  
        Enumeration<WrapperConnection> enumerate = connectionPool.elements();  
        while (enumerate.hasMoreElements()) {  
            pConn = (WrapperConnection) enumerate.nextElement();  
            if (pConn.isBusy()) {  
                wait(5000); // 等 5 秒  
            }  
            // 关闭此连接，用一个新的连接代替它。  
            closeConnection(pConn.getConnection());  
            pConn.setConnection(newConnection());  
            pConn.setBusy(false);  
        }  
    }  
  
    /** 
     * 关闭连接池中所有的连接，并清空连接池。 
     */  
    public synchronized void closeConnectionPool() throws SQLException {  
        if (connectionPool == null) {  
            logger.info(" 连接池不存在，无法关闭 !");  
            return;  
        }  
        WrapperConnection pConn = null;  
        Enumeration<WrapperConnection> enumerate = connectionPool.elements();  
        while (enumerate.hasMoreElements()) {  
            pConn = (WrapperConnection) enumerate.nextElement();  
            if (pConn.isBusy()) {  
                wait(5000); // 等 5 秒  
            }  
            closeConnection(pConn.getConnection());  
            connectionPool.removeElement(pConn);  
        }  
        connectionPool = null;  
    }  
  
    /** 
     * 关闭一个数据库连接 
     * @param 需要关闭的数据库连接 
     */  
    private void closeConnection(Connection conn) {  
        try {  
            conn.close();  
        } catch (SQLException e) {  
            logger.info(" 关闭数据库连接出错： " + e.getMessage());  
        }  
    }  
  
    /** 
     * 使程序等待给定的毫秒数 
     * @param 给定的毫秒数 
     */  
    private void wait(int mSeconds) {  
        try {  
            Thread.sleep(mSeconds);  
        } catch (InterruptedException e) {  
  
        }  
    }  
  
}

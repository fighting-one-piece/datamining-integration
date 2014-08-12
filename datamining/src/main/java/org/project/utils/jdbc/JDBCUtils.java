package org.project.utils.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.project.utils.PropertiesUtils;

/** JDBC工具类*/
public class JDBCUtils {
	
	private static Logger logger = Logger.getLogger(JDBCUtils.class);
	
	private static ConnectionPool connectionPool = null;
	
	static{
		try{
			Properties properties = PropertiesUtils.newInstance("database/jdbc.properties");
			connectionPool = new ConnectionPool(
					properties.getProperty("driverClassName"), 
					properties.getProperty("url"),
					properties.getProperty("username"), 
					properties.getProperty("password"),
					Integer.parseInt(properties.getProperty("initialSize")), 
					Integer.parseInt(properties.getProperty("maxActive")));
		}catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}
	
	public static synchronized Connection obtainConnection() {
		Connection connection = null;
		try {
			connection = connectionPool.getConnection();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return connection;
	}
	
	/**
	 * 释放连接
	 * @param conn 连接
	 * @param st
	 * @param rs
	 */
	public static void release(Connection conn, Statement st, ResultSet rs) {
		try{
			if (null != rs) rs.close();
			if (null != st) st.close();
			if (null != conn) conn.close();
		}catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}
	
	/**
	 * 返回连接到连接池
	 * @param conn
	 */
	public static void returnConnection (Connection conn) {
		try{
			if (null != conn) connectionPool.returnConnection(conn);
		}catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}
	
	/**
	 * 执行无返回值语句
	 * @param sql 语句
	 * @param params 参数
	 * @return
	 */
	public static void execute(String sql, Object... params) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = obtainConnection();
			pstmt = conn.prepareStatement(sql);
			if(null != params){
				for(int i = 0; i < params.length; i++){
					pstmt.setObject(i+1, params[i]);
				}
			} 
			pstmt.execute();
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		} finally {
			returnConnection(conn);
			release(null, pstmt, null);
		}
	}
	
	/**
	 * 执行有返回值语句
	 * @param sql 语句
	 * @param handler 返回值处理
	 * @param params 参数
	 * @return
	 */
	public static Object execute(String sql, ResultSetHandler handler, Object... params) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try{
			conn = obtainConnection();
			pstmt = conn.prepareStatement(sql);
			if(null != params){
				for(int i = 0; i < params.length; i++){
					pstmt.setObject(i+1, params[i]);
				}
			}
			return handler.handle(pstmt.executeQuery());
		} catch (SQLException e) {
			logger.info(e.getMessage(), e);
		} finally{
			returnConnection(conn);
			release(null, pstmt, result);
		}
		return null;
	}
	
}

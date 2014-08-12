package org.project.utils.jdbc;

import java.sql.ResultSet;

public interface ResultSetHandler {
	
	/**
	 * 处理结果集
	 * @param resultSet
	 * @return
	 */
	public Object handle(ResultSet resultSet);
}

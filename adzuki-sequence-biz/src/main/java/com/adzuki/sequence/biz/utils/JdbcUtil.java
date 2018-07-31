package com.adzuki.sequence.biz.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JdbcUtil {
	
	
	public static final ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
	private static Properties pr = PropertiesFileUtil.project_config;

	/**
	 * 获取到数据库的连接Connection对象
	 * 
	 * @return Connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection conn = tl.get();
		try {
			Class.forName(pr.getProperty("datasource.driverClassName"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			if (conn == null) {
				Class.forName(pr.getProperty("datasource.driverClassName"));
				conn = DriverManager.getConnection(pr.getProperty("datasource.url"), pr.getProperty("datasource.username"), pr.getProperty("datasource.password"));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return conn;

	}

	/**
	 * 释放JDBC连接的ResuletSet,Statement,Connection对象
	 * 
	 * @param re
	 * @param st
	 * @param conn
	 * @throws SQLException
	 */
	private static void release(ResultSet re, Statement st, Connection conn) throws SQLException {
		if (re != null)
			try {
				re.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
	}

	@SuppressWarnings("resource")
	public static int getClusterName(final String cuuid, final String v_ip, final String v_mac, final String v_catalina_base) throws ClassNotFoundException, SQLException {
		int cluster_name = -1;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet re = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			// 插入成功则进行查询, 本地仍可进行唯一判断.保证一致性进行机器属性比对
			st = conn.prepareStatement("SELECT id,cid,cuuid,ip,mac,catalinaBase FROM sequence_instance WHERE CUUID=? AND IP=? AND MAC=? AND CATALINABASE=?");
			st.setString(1, cuuid);
			st.setString(2, v_ip);
			st.setString(3, v_mac);
			st.setString(4, v_catalina_base);
			re = st.executeQuery();
			if (re.next()) {
					// 对比一致,获取实例号
					cluster_name = re.getInt(1);
					
					if(re.next())
					{
						//如果数据库的唯一键失效
						throw new RuntimeException("获取数据库实例号失败，请检查数据库的唯一键是否失效!");
					}
			} else {
				st = conn.prepareStatement("INSERT INTO sequence_instance (CUUID, IP, MAC, CATALINABASE) VALUES (?,?,?,?)");
				st.setString(1, cuuid);
				st.setString(2, v_ip);
				st.setString(3, v_mac);
				st.setString(4, v_catalina_base);
				st.execute();
				st = conn.prepareStatement("SELECT LAST_INSERT_ID()");
				re=st.executeQuery();
				re.next();
				cluster_name=re.getInt(1);
			}
			conn.commit();
		} catch (ClassNotFoundException | SQLException | RuntimeException e) {
			conn.rollback();
			throw e;
		} finally {
			release(re, st, conn);
		}

		return cluster_name;
	}

}

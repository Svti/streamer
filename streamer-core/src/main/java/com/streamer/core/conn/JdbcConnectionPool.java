package com.streamer.core.conn;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.lang3.StringUtils;

public class JdbcConnectionPool {

	private static Map<String, Connection> connections = new ConcurrentHashMap<String, Connection>();

	public static Connection getConnection(String driver, String url, String user, String password) {
		String key = new String(DigestUtils.md5Hex(StringUtils.join(driver, url, user, password).getBytes()));
		try {
			if (connections.containsKey(key)) {
				Connection connection = connections.get(key);
				if (connection.isClosed()) {
					Class.forName(driver);
					connection = new DriverManagerConnectionFactory(url, user, password).createConnection();
				}
				return connection;
			} else {
				Class.forName(driver);
				Connection connection = new DriverManagerConnectionFactory(url, user, password).createConnection();
				connections.putIfAbsent(key, connection);
				return connection;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void cleanAll() {
		// 故意不关闭，等待系统回收.多线程中,不确定其他线程是否占用.
		connections.clear();
	}

}

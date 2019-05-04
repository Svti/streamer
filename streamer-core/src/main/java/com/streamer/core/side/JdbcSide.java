package com.streamer.core.side;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streamer.core.conn.JdbcConnectionPool;
import com.streamer.core.exception.StreammerException;
import com.streamer.core.parser.CreateSideParser.SqlParserResult;
import com.streamer.core.utils.CoreUtils;

public class JdbcSide extends SideFactory {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, Object> contextMap = new HashMap<>();

	public JdbcSide(String fun, String config) {

		SqlParserResult result = CoreUtils.readConfig(config);

		if (result == null) {
			throw new StreammerException("Jdbc Side table " + fun + " config error , config can not be null ! ");
		}

		contextMap.putAll(result.getPropMap());
		checkConfig();

	}

	public String query(Object a) {
		return this.process(a);
	}

	public String query(Object a, Object b) {
		return this.process(a, b);
	}

	public String query(Object a, Object b, Object c) {
		return this.process(a, b, c);
	}

	public String query(Object a, Object b, Object c, Object d) {
		return this.process(a, b, c, d);
	}

	public String query(Object a, Object b, Object c, Object d, Object e) {
		return this.process(a, b, c, d, e);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f) {
		return this.process(a, b, c, d, e, f);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g) {
		return this.process(a, b, c, d, e, f, g);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h) {
		return this.process(a, b, c, d, e, f, g, h);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i) {
		return this.process(a, b, c, d, e, f, g, h, i);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
			Object j) {
		return this.process(a, b, c, d, e, f, g, h, i, j);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
			Object j, Object k) {
		return this.process(a, b, c, d, e, f, g, h, i, j, k);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
			Object j, Object k, Object l) {
		return this.process(a, b, c, d, e, f, g, h, i, j, k, l);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
			Object j, Object k, Object l, Object m) {
		return this.process(a, b, c, d, e, f, g, h, i, j, k, l, m);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
			Object j, Object k, Object l, Object m, Object n) {
		return this.process(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
	}

	public String query(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i,
			Object j, Object k, Object l, Object m, Object n, Object o) {
		return this.process(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o);
	}

	@Override
	public void checkConfig() {

		if (contextMap.containsKey("url")) {
			contextMap.put("url", contextMap.get("url").toString());
		} else {
			throw new RuntimeException("The url is needed in jdbcSide and cant be empty, config:" + contextMap);
		}

		if (contextMap.containsKey("driver")) {
			contextMap.put("driver", contextMap.get("driver").toString());
		} else {
			throw new RuntimeException("The driver is needed in jdbcSide and cant be empty, config:" + contextMap);
		}

		if (contextMap.containsKey("user")) {
			contextMap.put("user", contextMap.get("user").toString());
		} else {
			throw new RuntimeException("The user is needed in jdbcSide and cant be empty, config:" + contextMap);
		}

		if (contextMap.containsKey("password")) {
			contextMap.put("password", contextMap.get("password").toString());
		} else {
			throw new RuntimeException("The password is needed in jdbcSide and cant be empty, config:" + contextMap);
		}

		if (contextMap.containsKey("read.sql")) {
			contextMap.put("read.sql", contextMap.get("read.sql").toString());
		} else {
			throw new RuntimeException("The read.sql is needed in jdbcSide and cant be empty, config:" + contextMap);
		}

		if (contextMap.containsKey("write.sql")) {
			contextMap.put("write.sql", contextMap.get("write.sql").toString());
		}

	}

	public String process(Object... params) {
		try {

			String driver = String.valueOf(contextMap.get("driver"));
			String url = String.valueOf(contextMap.get("url"));
			String user = String.valueOf(contextMap.get("user"));
			String password = String.valueOf(contextMap.get("password"));

			Connection connection = JdbcConnectionPool.getConnection(driver, url, user, password);

			String rsql = String.valueOf(contextMap.get("read.sql"));

			PreparedStatement rpstmt = connection.prepareStatement(rsql);

			int rpsize = rpstmt.getParameterMetaData().getParameterCount();

			if (params.length < rpsize) {
				return null;
			}

			// 处理参数超长的问题
			for (int i = 0; i < rpsize; i++) {
				rpstmt.setObject(i + 1, params[i]);
			}

			ResultSet rs = rpstmt.executeQuery();

			String result = null;
			if (rs.next()) {
				result = String.valueOf(rs.getObject(1));
				rpstmt.close();
			} else {
				String wsql = contextMap.get("write.sql") == null ? null : String.valueOf(contextMap.get("write.sql"));

				if (wsql != null && wsql.trim().length() > 1) {
					PreparedStatement wpstmt = connection.prepareStatement(wsql);

					// 处理参数超长的问题
					int wpsize = rpstmt.getParameterMetaData().getParameterCount();

					if (params.length >= wpsize) {

						for (int i = 0; i < wpsize; i++) {
							wpstmt.setObject(i + 1, params[i]);
						}

						// 不是自动提交的，设置提交
						if (connection.getAutoCommit() == false) {
							connection.commit();
						}
						wpstmt.executeUpdate();
						wpstmt.close();
					}
				}
			}
			return result;
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(
					"The jdbcSide table sql" + contextMap.get("read.sql") + " found Exceptions :" + e.getMessage());
		}
	}

}

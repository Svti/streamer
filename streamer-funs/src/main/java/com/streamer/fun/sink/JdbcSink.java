package com.streamer.fun.sink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streamer.core.conn.JdbcConnectionPool;
import com.streamer.core.message.Row;
import com.streamer.core.parser.CreateTableParser.SqlParserResult;
import com.streamer.core.parser.SqlTree;

public class JdbcSink extends BaseSink {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Object> contextMap = new HashMap<>();

    public JdbcSink(SqlTree sqlTree) {

        Map<String, SqlParserResult> map = sqlTree.getPreDealSinkMap();

        if (map.isEmpty()) {
            throw new RuntimeException("The JdbcSink config and cant be empty, config:" + map);
        }

        if (map.size() > 1) {
            throw new RuntimeException("The JdbcSink support one table only , But config :" + map);
        }

        String sinkTable = new ArrayList<>(map.keySet()).get(0);

        contextMap.putAll(map.get(sinkTable).getPropMap());

        checkConfig();
    }

    @Override
    public void checkConfig() {

        if (contextMap.containsKey("url")) {
            contextMap.put("url", contextMap.get("url").toString());
        } else {
            throw new RuntimeException("The url is needed in JdbcSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("driver")) {
            contextMap.put("driver", contextMap.get("driver").toString());
        } else {
            throw new RuntimeException("The driver is needed in JdbcSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("user")) {
            contextMap.put("user", contextMap.get("user").toString());
        } else {
            throw new RuntimeException("The user is needed in JdbcSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("password")) {
            contextMap.put("password", contextMap.get("password").toString());
        } else {
            throw new RuntimeException("The password is needed in JdbcSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("sql")) {
            contextMap.put("sql", contextMap.get("sql").toString());
        } else {
            throw new RuntimeException("The sql is needed in JdbcSink and cant be empty, config:" + contextMap);
        }
    }

    @Override
    public boolean process(List<List<Row>> list) throws Exception {

        String sql = String.valueOf(contextMap.get("sql"));
        String driver = String.valueOf(contextMap.get("driver"));
        String url = String.valueOf(contextMap.get("url"));
        String user = String.valueOf(contextMap.get("user"));
        String password = String.valueOf(contextMap.get("password"));

        int count = 0;
        boolean flag = false;

        while (!flag && count < 3) {
            count++;
            try {

                Connection connection = JdbcConnectionPool.getConnection(driver, url, user, password);
                connection.setAutoCommit(false);

                PreparedStatement pstmt = connection.prepareStatement(sql);

                for (List<Row> rows : list) {
                    for (int i = 0; i < rows.size(); i++) {
                        pstmt.setObject(i + 1, rows.get(i).getValue());
                    }
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                pstmt.close();
                connection.commit();

                flag = true;
            } catch (SQLException e) {
                logger.warn("Found SQL Exceptiion :" + e.getMessage() + ", Retry ...  " + count, e);
                // 等待5s后重试
                Thread.sleep(5 * 1000);
            }
        }
        return flag;
    }

    @Override
    public void destory() {

    }

}

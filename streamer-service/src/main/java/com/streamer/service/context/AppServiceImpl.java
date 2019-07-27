package com.streamer.service.context;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.streamer.core.support.Signal;
import com.streamer.service.core.StreamerRole;
import com.streamer.service.view.PaginationResult;

@Component
public class AppServiceImpl implements AppService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private Environment environment;

    @Override
    public int save(String node, String name, String sql, Date now) {
        return jdbcTemplate.update(
                "INSERT INTO jobs (`node`,`name`,`sql`,`status`, `token`, `createAt`) VALUES (?,?,?,?,?,?)", node, name,
                sql, 0, null, now);
    }

    @Override
    public int update(String node, String name, String sql) {
        return jdbcTemplate.update("UPDATE jobs SET `sql` = ? , `node` =? , `status` =  ? WHERE `name` =? ", sql, node,
                0, name);
    }

    @Override
    public int stop(String token, String node, String name) {
        if (Signal.map.get(token) != null) {
            Signal.map.get(token).set(0L);
        }
        Signal.map.remove(token);

        return jdbcTemplate.update("UPDATE jobs SET status = ? , token = ? WHERE node =? AND name =?", 0, null, node,
                name);
    }

    @Override
    public int stopAll(String node) {
        Signal.map.clear();
        return jdbcTemplate.update("UPDATE jobs SET status = ? , token = ? WHERE node =? ", 0, null, node);
    }

    @Override
    public List<Map<String, Object>> findJobByName(String name) {
        return jdbcTemplate.queryForList("SELECT * FROM jobs WHERE name = ? ", name);
    }

    @Override
    public boolean exsit(String name) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT 1 FROM jobs WHERE name = ? LIMIT 1", name);

        if (list.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int start(String node, String name, String token) {
        return jdbcTemplate.update("UPDATE jobs SET status = ? , token = ? WHERE name =? AND node =? ", 1, token, name,
                node);
    }

    @Override
    public PaginationResult<Map<String, Object>> findAll(String name, String node, String sql, int status, int current,
            int szie) {
        PaginationResult<Map<String, Object>> pr = new PaginationResult<>();

        StringBuffer query = new StringBuffer();
        List<Object> values = new ArrayList<>();
        if (StringUtils.isNotEmpty(name)) {
            query.append(" AND name LIKE ?");
            values.add("%" + name + "%");
        }

        if (StringUtils.isNotEmpty(node)) {
            query.append(" AND node LIKE ?");
            values.add("%" + node + "%");
        }

        if (StringUtils.isNotEmpty(sql)) {
            query.append(" AND `sql` LIKE ?");
            values.add("%" + sql + "%");
        }

        if (status >= 0) {
            query.append(" AND status = ?");
            values.add(status);
        }

        values.add(((current - 1) * szie));
        values.add(szie);

        pr.setListData(jdbcTemplate.queryForList("SELECT * FROM jobs WHERE 1=1 " + query.toString() + " LIMIT ?,?",
                values.toArray()));

        pr.setTotalRecord(jdbcTemplate.queryForObject("SELECT count(1) FROM jobs WHERE 1=1 " + query.toString(),
                Long.class, new ArrayList<Object>(values).subList(0, values.size() - 2).toArray()));
        return pr;
    }

    @Override
    public Map<String, Object> findTotal(int second) {
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList(
                        "SELECT 'total' as label,COUNT(1) as value FROM jobs "
                                + "UNION ALL SELECT 'stoped' as label,COUNT(1) as value FROM jobs WHERE status = 0 "
                                + "UNION ALL SELECT 'nodes' as label,COUNT(1) as value FROM nodes WHERE updateAt >= NOW()-interval ? second "
                                + "UNION ALL SELECT 'running' as label,COUNT(1) as value FROM jobs WHERE status = 1",
                        second);

        if (list.isEmpty()) {
            return null;
        } else {
            Map<String, Object> map = new HashMap<>();
            for (Map<String, Object> _map : list) {
                map.put(String.valueOf(_map.get("label")), _map.get("value"));
            }
            return map;
        }
    }

    @Override
    public int del(String name) {
        return jdbcTemplate.update("DELETE FROM jobs WHERE status = ? AND name =? ", 0, name);
    }

    @Override
    public int online(String node, String host, int port, StreamerRole role, Date date) {

        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT 1 FROM nodes WHERE name = ? ", node);

        if (list.isEmpty()) {
            return jdbcTemplate.update(
                    "INSERT INTO nodes (`name`, `host`, `port`, `role`,`createAt`,`updateAt`) VALUES (?,?,?,?,?,?)",
                    node, host, port, role.getRole(), date, date);
        } else {
            return jdbcTemplate.update("UPDATE nodes set updateAt = ? , host =? , port = ?  WHERE name =? ", date, host,
                    port, node);
        }

    }

    @Override
    public List<Map<String, Object>> findAlivableNode(int second) {
        return jdbcTemplate.queryForList("SELECT * FROM nodes WHERE role > 1 AND updateAt >= NOW()-interval ? second ",
                second);
    }

    @Override
    public List<String> expireNodes(int second) {
        jdbcTemplate.update("DELETE FROM nodes WHERE updateAt < NOW()-interval ? second", second);
        List<String> nodes = new ArrayList<>();
        for (Map<String, Object> map : jdbcTemplate
                .queryForList("SELECT node FROM jobs WHERE node NOT IN (SELECT name FROM nodes)")) {
            nodes.add(String.valueOf(map.get("node")));
        }
        return nodes;
    }

    @Override
    public Map<String, Object> findMasterNode(int second) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "SELECT * FROM nodes WHERE role = 1 AND updateAt >= NOW()-interval ? second ORDER BY updateAt DESC LIMIT 1",
                second);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public Map<String, Object> findNodesByName(String node) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM nodes WHERE name =? LIMIT 1", node);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }

    }

    @Override
    public List<Map<String, Object>> findJobsByNode(String node, int status) {
        return jdbcTemplate.queryForList("SELECT * FROM jobs WHERE node =? AND status = ? ", node, status);
    }

    @Override
    public boolean login(String username, String password) {
        String default_username = environment.getProperty("application.admin.username");
        String default_password = environment.getProperty("application.admin.password");
        if (username.equals(default_username) && password.equals(default_password)) {
            return true;
        } else {
            return false;
        }
    }

}

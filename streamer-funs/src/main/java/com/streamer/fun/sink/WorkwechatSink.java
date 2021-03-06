package com.streamer.fun.sink;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamer.core.message.Row;
import com.streamer.core.parser.CreateTableParser.SqlParserResult;
import com.streamer.core.parser.SqlTree;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WorkwechatSink extends BaseSink {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private OkHttpClient client;

    private static final int TIMEOUT = 10;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Map<String, Object> contextMap = new HashMap<>();

    public WorkwechatSink(SqlTree sqlTree) {

        // 初始化连接
        client = new OkHttpClient.Builder().connectTimeout(TIMEOUT, TimeUnit.MINUTES)
                .callTimeout(TIMEOUT, TimeUnit.MINUTES).readTimeout(TIMEOUT, TimeUnit.MINUTES)
                .writeTimeout(TIMEOUT, TimeUnit.MINUTES).build();

        MAPPER.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        Map<String, SqlParserResult> map = sqlTree.getPreDealSinkMap();

        if (map.isEmpty()) {
            throw new RuntimeException("The WorkwechatSink config and cant be empty, config:" + map);
        }

        if (map.size() > 1) {
            throw new RuntimeException("The WorkwechatSink config unsupport two sink table , config:" + map);
        }

        String sinkTable = new ArrayList<>(map.keySet()).get(0);

        contextMap.putAll(map.get(sinkTable).getPropMap());

        // 检查配置
        checkConfig();
    }

    @Override
    public void checkConfig() {

        if (contextMap.containsKey("corpid")) {
            contextMap.put("corpid", contextMap.get("corpid").toString());
        } else {
            throw new RuntimeException(
                    "The corpid is needed in WorkwechatSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("secret")) {
            contextMap.put("secret", contextMap.get("secret").toString());
        } else {
            throw new RuntimeException(
                    "The secret is needed in WorkwechatSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("agentid")) {
            contextMap.put("agentid", contextMap.get("agentid").toString());
        } else {
            throw new RuntimeException(
                    "The agentid is needed in WorkwechatSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("touser")) {
            contextMap.put("touser", contextMap.get("touser").toString());
        } else {
            throw new RuntimeException("The touser is needed in WorkechatSink and cant be empty, config:" + contextMap);
        }
    }

    @Override
    public boolean process(List<List<Row>> list) throws Exception {

        if (list.isEmpty()) {
            logger.warn("There are not found any result row: {}", list);
            return true;
        }

        String corpid = String.valueOf(contextMap.get("corpid"));
        String agentid = String.valueOf(contextMap.get("agentid"));
        String secret = String.valueOf(contextMap.get("secret"));
        String touser = String.valueOf(contextMap.get("touser"));

        try {

            String token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret="
                    + secret;
            Response token_resp = client.newCall(new Request.Builder().url(token_url).build()).execute();

            if (!token_resp.isSuccessful()) {
                logger.error("Could not send Qywx message for config corpid:{}, agentid:{}, secret:{}", corpid, agentid,
                        secret);
                return false;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

            JsonNode node = mapper.readTree(token_resp.body().string());

            if (!node.has("access_token")) {
                logger.error("Could not send Qywx message for config corpid:{}, agentid:{}, secret:{}", corpid, agentid,
                        secret);
                return false;
            }

            String token = node.get("access_token").asText();

            token_resp.close();

            String send_url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;

            for (List<Row> rows : list) {

                Map<String, Object> map = new HashMap<>();
                map.put("touser", touser);
                map.put("agentid", agentid);
                map.put("msgtype", "markdown");

                Map<String, Object> markdown = new HashMap<>();

                StringBuffer buf = new StringBuffer("");
                for (Row row : rows) {
                    buf.append(row.getValue());
                    buf.append("\n");
                }
                markdown.put("content", buf.toString());
                map.put("markdown", markdown);
                map.put("safe", 0);

                String json = mapper.writeValueAsString(map);
                RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
                Response send_resp = client.newCall(new Request.Builder().url(send_url).post(body).build()).execute();

                boolean flag = send_resp.isSuccessful();

                String resp = send_resp.body().string();

                if (flag == false) {
                    logger.error("Could not send Qywx message for config corpid:{}, agentid:{}, secret:{}", corpid,
                            agentid, secret);
                } else {
                    logger.info("Qywx message send success , resp:{}", resp);
                }

                send_resp.close();
            }

        } catch (SocketTimeoutException e) {
            logger.warn(e.getMessage(), e);
            // 初始化连接
            client = new OkHttpClient.Builder().connectTimeout(TIMEOUT, TimeUnit.MINUTES)
                    .callTimeout(TIMEOUT, TimeUnit.MINUTES).readTimeout(TIMEOUT, TimeUnit.MINUTES)
                    .writeTimeout(TIMEOUT, TimeUnit.MINUTES).build();
        }

        return true;
    }

    @Override
    public void destory() {
    }

}

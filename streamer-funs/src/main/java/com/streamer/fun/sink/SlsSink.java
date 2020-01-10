package com.streamer.fun.sink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.aliyun.log.producer.LogProducer;
import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;
import com.aliyun.openservices.aliyun.log.producer.ProjectConfig;
import com.aliyun.openservices.log.common.LogItem;
import com.streamer.core.message.Row;
import com.streamer.core.parser.CreateTableParser.SqlParserResult;
import com.streamer.core.parser.SqlTree;

public class SlsSink extends BaseSink {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private LogProducer producer;

    private Map<String, Object> contextMap = new HashMap<>();

    public SlsSink(SqlTree sqlTree) {

        Map<String, SqlParserResult> map = sqlTree.getPreDealSinkMap();

        if (map.isEmpty()) {
            throw new RuntimeException("The SlsSink config and cant be empty, config:" + map);
        }

        if (map.size() > 1) {
            throw new RuntimeException("The SlsSink config unsupport two sink table , config:" + map);
        }

        String sinkTable = new ArrayList<>(map.keySet()).get(0);

        contextMap.putAll(map.get(sinkTable).getPropMap());

        // 检查配置
        checkConfig();

        String project = String.valueOf(contextMap.get("project"));
        String endpoint = String.valueOf(contextMap.get("endpoint"));
        String accessKeyId = String.valueOf(contextMap.get("accessKeyId"));
        String accessKeySecret = String.valueOf(contextMap.get("accessKeySecret"));

        ProducerConfig producerConfig = new ProducerConfig();
        producerConfig.setBatchSizeThresholdInBytes(ProducerConfig.MAX_BATCH_SIZE_IN_BYTES);
        producerConfig.setBatchCountThreshold(ProducerConfig.MAX_BATCH_COUNT);
        producerConfig.setIoThreadCount(Runtime.getRuntime().availableProcessors());
        producerConfig.setTotalSizeInBytes(Integer.MAX_VALUE);

        producer = new LogProducer(producerConfig);
        producer.putProjectConfig(new ProjectConfig(project, endpoint, accessKeyId, accessKeySecret));
    }

    @Override
    public void checkConfig() {

        if (contextMap.containsKey("project")) {
            contextMap.put("project", contextMap.get("project").toString());
        } else {
            throw new RuntimeException("The project is needed in SlsSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("logStore")) {
            contextMap.put("logStore", contextMap.get("logStore").toString());
        } else {
            throw new RuntimeException("The logStore is needed in SlsSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("endpoint")) {
            contextMap.put("endpoint", contextMap.get("endpoint").toString());
        } else {
            throw new RuntimeException("The endpoint is needed in SlsSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("accessKeyId")) {
            contextMap.put("accessKeyId", contextMap.get("accessKeyId").toString());
        } else {
            throw new RuntimeException(
                    "The accessKeyId is needed in SlsSink and cant be empty, config:" + contextMap);
        }

        if (contextMap.containsKey("accessKeySecret")) {
            contextMap.put("accessKeySecret", contextMap.get("accessKeySecret").toString());
        } else {
            throw new RuntimeException(
                    "The accessKeySecret is needed in SlsSink and cant be empty, config:" + contextMap);
        }
    }

    @Override
    public boolean process(List<List<Row>> list) throws Exception {

        if (list.isEmpty()) {
            logger.warn("There are not found any result row: {}", list);
            return true;
        }

        List<LogItem> items = new ArrayList<>();
        for (List<Row> rows : list) {
            LogItem item = new LogItem();
            for (Row row : rows) {
                item.PushBack(row.getKey(), (row.getValue() == null ? "" : String.valueOf(row.getValue())));
            }
            items.add(item);
        }

        String project = String.valueOf(contextMap.get("project"));
        String logStore = String.valueOf(contextMap.get("logStore"));
        producer.send(project, logStore, "", "", items);
        return true;
    }

    @Override
    public void destory() {
        if (producer != null) {
            try {
                producer.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}

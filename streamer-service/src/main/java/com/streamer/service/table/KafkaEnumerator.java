package com.streamer.service.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.calcite.linq4j.Enumerator;

import com.fasterxml.jackson.databind.JsonNode;

import com.streamer.core.message.KafkaMessage;
import com.streamer.core.parser.TableColumn;
import com.streamer.core.support.Spliter;
import com.streamer.core.utils.CoreUtils;
import com.streamer.fun.scalar.core.JsonFunctions;


public class KafkaEnumerator<T> implements Enumerator<Object[]> {

	private int current = -1;

	private List<KafkaMessage> messages;

	private List<TableColumn> columns;

	private Spliter spliter;

	public KafkaEnumerator(List<KafkaMessage> messages, Spliter spliter, List<TableColumn> columns) {
		this.messages = messages;
		this.columns = columns;
		this.spliter = spliter;
	}

	@Override
	public void close() {
	}

	@Override
	public Object[] current() {
		KafkaMessage message = messages.get(current);
		List<Object> objs = new ArrayList<>();

		Map<String, Object> map = new HashMap<>();
		switch (spliter) {
		case JSON:
			Map<String, JsonNode> imap = new HashMap<>();
			try {
				CoreUtils.flatJson(imap, "", message.getValue());
			} catch (IOException e) {
			}
			for (Entry<String, JsonNode> entry : imap.entrySet()) {
				map.put(entry.getKey(), entry.getValue().asText());
			}
			break;
		case KV:
			for (String vals : message.getValue().split(spliter.getKey())) {
				String ff[] = vals.split(spliter.getValue());
				if (ff.length > 1) {
					map.put(ff[0], ff[1]);
				} else {
					continue;
				}
			}
			break;
		default:
			break;
		}

		// 此处要保证和struct 的结构顺序一致
		for (TableColumn column : columns) {

			switch (new String(column.getColumn().toLowerCase())) {
			case "_topic":
				objs.add(message.getTopic());
				break;
			case "_offset":
				objs.add(message.getOffset());
				break;
			case "_partition":
				objs.add(message.getPartition());
				break;
			case "_key":
				objs.add(message.getKey());
				break;
			case "_value":
				objs.add(message.getValue());
				break;
			case "_timestamp":
				objs.add(message.getTimestamp());
				break;
			default:
				switch (spliter) {
				case JSON:
				case KV:
					objs.add(map.containsKey(column.getColumn()) ? map.get(column.getColumn()) : null);
					break;
				default:
					break;
				}
				break;
			}
		}
		return objs.toArray();
	}

	@Override
	public boolean moveNext() {
		return ++current < messages.size();
	}

	@Override
	public void reset() {
	}
}

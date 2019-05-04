package com.streamer.spi.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonParser implements Parser {

	private ObjectMapper om = new ObjectMapper();

	@Override
	public PrettyTable parse(String text) throws IOException {

		JsonNode root = om.readTree(text);

		if (root.isArray()) {

			if (root.size() < 1) {
				return PrettyTable.fieldNames();
			}

			PrettyTable pt = PrettyTable.fieldNames(root.get(0).fieldNames());

			for (JsonNode c : root) {
				List<Object> values = new ArrayList<>();
				c.fields().forEachRemaining(f -> values.add(toStr(f)));
				pt.addRow(values.toArray());
			}
			return pt;

		} else {
			PrettyTable pt = PrettyTable.fieldNames("Name", "Value");
			root.fields().forEachRemaining(f -> pt.addRow(f.getKey(), toStr(f)));
			return pt;
		}
	}

	private static Object toStr(final Map.Entry<String, JsonNode> field) {
		JsonNode value = field.getValue();
		switch (value.getNodeType()) {
		case STRING:
			return value.asText();
		case NUMBER:
			if (value.toString().contains(".")) {
				return value.asDouble();
			} else {
				return value.asInt();
			}

		default:
			return value.toString();
		}
	}
}

package com.streamer.fun.scalar.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.streamer.fun.scalar.ScalarFunction;

public class JsonFunctions implements ScalarFunction {

	public static String get_json_object(String json, String path) {
		Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider())
				.jsonProvider(new JacksonJsonNodeJsonProvider()).build().addOptions(Option.SUPPRESS_EXCEPTIONS);
		JsonNode node = JsonPath.using(conf).parse(json).read(path);

		if (node != null) {
			if (node.isArray()) {
				return node.toString();
			} else if (node.isObject()) {
				return node.toString();
			} else {
				return node.asText();
			}

		} else {
			return null;
		}
	}
}

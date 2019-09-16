
package com.streamer.core.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.calcite.sql.type.SqlTypeName;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import com.streamer.core.exception.StreammerException;
import com.streamer.core.parser.CreateSideParser.SqlParserResult;
import com.streamer.core.parser.TableColumn;

import com.mysql.jdbc.StringUtils;

public class CoreUtils {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static List<String> splitIgnoreQuota(String str, char delimiter) {
		List<String> tokensList = new ArrayList<>();
		if (str == null) {
			return tokensList;
		}
		boolean inQuotes = false;
		boolean inSingleQuotes = false;
		StringBuilder b = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (c == delimiter) {
				if (inQuotes) {
					b.append(c);
				} else if (inSingleQuotes) {
					b.append(c);
				} else {
					tokensList.add(b.toString());
					b = new StringBuilder();
				}
			} else if (c == '\"') {
				inQuotes = !inQuotes;
				b.append(c);
			} else if (c == '\'') {
				inSingleQuotes = !inSingleQuotes;
				b.append(c);
			} else {
				b.append(c);
			}
		}
		tokensList.add(b.toString());
		return tokensList;
	}

	@SuppressWarnings("unchecked")
	public static <E> ArrayList<E> newArrayList(E... elements) {
		if (elements == null) {
			return new ArrayList<E>(0);
		}
		ArrayList<E> list = new ArrayList<E>(elements.length);
		Collections.addAll(list, elements);
		return list;
	}

	public static String upperCaseFirstChar(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String[] splitIgnoreQuotaBrackets(String str, String delimter) {
		String splitPatternStr = delimter + "(?![^()]*+\\))(?![^{}]*+})(?![^\\[\\]]*+\\])(?=(?:[^\"]|\"[^\"]*\")*$)";
		return str.split(splitPatternStr);
	}

	public static SqlTypeName strConverDataType(String filedType) {
		if (filedType == null) {
			throw new RuntimeException("The file type for " + filedType + " are not support");
		}
		switch (filedType.toLowerCase()) {
		case "boolean":
			return SqlTypeName.BOOLEAN;
		case "int":
			return SqlTypeName.INTEGER;

		case "bigint":
			return SqlTypeName.BIGINT;

		case "tinyint":
		case "byte":
			return SqlTypeName.TINYINT;

		case "short":
		case "smallint":
			return SqlTypeName.INTEGER;

		case "char":
		case "varchar":
		case "string":
			return SqlTypeName.VARCHAR;

		case "float":
			return SqlTypeName.FLOAT;

		case "double":
			return SqlTypeName.DOUBLE;

		case "date":
			return SqlTypeName.DATE;

		case "timestamp":
			return SqlTypeName.TIMESTAMP;

		default:
			throw new RuntimeException("The file type for " + filedType + " are not support");
		}
	}

	public static Object dataTypeToObj(SqlTypeName name, Object o) {
		if (null == o) {
			return o;
		}
		switch (name) {
		case BOOLEAN:
			return Boolean.valueOf(o.toString());

		case INTEGER:
			return (Integer) o;

		case BIGINT:
			return (Long) o;

		case CHAR:
		case VARCHAR:
			return (String) o;

		case FLOAT:
			return (Float) o;

		case DOUBLE:
			return (Double) o;

		case DATE:
			return ((Date) o);

		case TIMESTAMP:
			return new Timestamp(
					DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").parseDateTime(o.toString()).getMillis());

		default:
			throw new RuntimeException("The data type for " + name + " are not support");
		}
	}

	public static SqlParserResult readConfig(String config) {
		try {
			return MAPPER.readValue(config, SqlParserResult.class);
		} catch (IOException e) {
			throw new StreammerException("Found error readConfig " + config);
		}
	}

	public static String wirteConfig(SqlParserResult sqlParserResult) {
		try {
			return MAPPER.writeValueAsString(sqlParserResult);
		} catch (JsonProcessingException e) {
			throw new StreammerException("Found error wirteConfig " + sqlParserResult.getQueryName());
		}
	}

	public static List<TableColumn> genFields(List<TableColumn> list, String fields) {

		if (list == null) {
			throw new StreammerException("Could not ops list, because it is null");
		}

		if (fields == null) {
			throw new StreammerException("Found error table ddl " + fields);
		}

		String[] fieldRows = CoreUtils.splitIgnoreQuotaBrackets(fields, ",");

		for (String fieldRow : fieldRows) {
			fieldRow = fieldRow.trim();

			String[] filedInfoArr = fieldRow.split(" ");

			if (filedInfoArr.length < 2) {
				throw new StreammerException("Found error table ddl " + fields);
			}

			String filedName = filedInfoArr[0];
			String source = filedName;
			String alias = filedName;

			// as 别名
			if (filedName.contains(":")) {
				int index = filedName.indexOf(":");
				source = filedName.substring(0, index);
				alias = filedName.substring(index + 1, filedName.length());
			}

			String filedType = filedInfoArr[1].toLowerCase();

			TableColumn column = new TableColumn();
			column.setAlias(alias);
			column.setColumn(source);
			column.setTypeName(CoreUtils.strConverDataType(filedType));

			list.add(column);
		}

		return list;
	}

	public static void flatJson(Map<String, JsonNode> map, String parent, String message) throws IOException {
		JsonNode root = MAPPER.readTree(message);
		if (root.isObject()) {
			Iterator<Entry<String, JsonNode>> fiterator = root.fields();
			while (fiterator.hasNext()) {
				Entry<String, JsonNode> entry = fiterator.next();
				String key = entry.getKey().toLowerCase().trim();
				if (StringUtils.isStrictlyNumeric(key)) {
					continue;
				}
				JsonNode value = entry.getValue();
				if (value.elements().hasNext()) {
					flatJson(map, (StringUtils.isEmptyOrWhitespaceOnly(parent) ? key : parent + "." + key),
							value.toString());
				} else {
					if (!StringUtils.isEmptyOrWhitespaceOnly(parent)) {
						map.put(parent + "." + key, value);
					} else {
						map.put(key, value);
					}
				}
			}
		}
	}

}

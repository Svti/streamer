
package com.streamer.core.parser;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.streamer.core.utils.CoreUtils;

public class CreateSideParser implements IParser {

	private static final String PATTERN_STR = "(?i)create\\s+side\\s+(\\S+)\\s*\\((.+)\\)\\s*with\\s*\\((.+)\\)";

	private static final Pattern PATTERN = Pattern.compile(PATTERN_STR);

	public static CreateSideParser newInstance() {
		return new CreateSideParser();
	}

	@Override
	public boolean verify(String sql) {
		return PATTERN.matcher(sql).find();
	}

	@Override
	public void parseSql(String queryName, String sql, SqlTree sqlTree) {
		Matcher matcher = PATTERN.matcher(sql);
		if (matcher.find()) {
			String tableName = matcher.group(1);
			String fieldsInfoStr = matcher.group(2);
			String propsStr = matcher.group(3);
			Map<String, Object> props = parseProp(propsStr);

			SqlParserResult result = new SqlParserResult();
			result.setQueryName(queryName);
			result.setTableName(tableName);
			result.setFieldsInfoStr(fieldsInfoStr);
			result.setPropMap(props);

			sqlTree.addPreSideTableInfo(tableName, result);
		}
	}

	private Map<String, Object> parseProp(String propsStr) {
		String[] strs = propsStr.trim().split("'\\s*,");
		Map<String, Object> propMap = Maps.newHashMap();
		for (int i = 0; i < strs.length; i++) {
			List<String> ss = CoreUtils.splitIgnoreQuota(strs[i], '=');
			String key = ss.get(0).trim();
			String value = ss.get(1).trim().replaceAll("'", "").trim();
			propMap.put(key, value);
		}

		return propMap;
	}

	public static class SqlParserResult {

		private String queryName;

		private String tableName;

		private String fieldsInfoStr;

		private Map<String, Object> propMap;

		public String getQueryName() {
			return queryName;
		}

		public void setQueryName(String queryName) {
			this.queryName = queryName;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getFieldsInfoStr() {
			return fieldsInfoStr;
		}

		public void setFieldsInfoStr(String fieldsInfoStr) {
			this.fieldsInfoStr = fieldsInfoStr;
		}

		public Map<String, Object> getPropMap() {
			return propMap;
		}

		public void setPropMap(Map<String, Object> propMap) {
			this.propMap = propMap;
		}

	}
}
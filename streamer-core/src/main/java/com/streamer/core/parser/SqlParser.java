package com.streamer.core.parser;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.streamer.core.exception.StreammerException;
import com.streamer.core.utils.CoreUtils;

public class SqlParser {

	private static final char SQL_DELIMITER = ';';

	private static List<IParser> sqlParserList = CoreUtils.newArrayList(CreateFuncParser.newInstance(),
			CreateTableParser.newInstance(), InsertSqlParser.newInstance(), CreateSinkParser.newInstance(),
			CreateSideParser.newInstance());

	public static SqlTree parseSql(String queryName, String sql) {

		if (StringUtils.isBlank(sql)) {
			throw new StreammerException("The sql must be not null");
		}

		sql = sql.replaceAll("--.*", "").replaceAll("\r\n", " ").replaceAll("\n", " ").replace("\t", " ").trim();

		// 将整个sql文件按照';'分隔，分成几个sql
		List<String> sqlArr = CoreUtils.splitIgnoreQuota(sql, SQL_DELIMITER);
		SqlTree sqlTree = new SqlTree();

		for (String childSql : sqlArr) {
			if (StringUtils.isEmpty(childSql)) {
				continue;
			}
			boolean result = false;

			for (IParser sqlParser : sqlParserList) {
				if (!sqlParser.verify(childSql)) {
					continue;
				}
				sqlParser.parseSql(queryName, childSql, sqlTree);
				result = true;
			}

			if (!result) {
				throw new StreammerException(String.format(
						"%s:Syntax does not support,the format of SQL like insert into tb1 select * from tb2.",
						childSql));
			}
		}

		if (sqlTree.getExecSqlList().size() == 0 && sqlTree.getExecSql() == null) {
			throw new StreammerException("sql no executable statement");
		}

		return sqlTree;
	}
}
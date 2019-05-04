/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain HttpStreamSource copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamer.core.parser;

import static org.apache.calcite.sql.SqlKind.IDENTIFIER;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.commons.lang3.StringUtils;

import com.streamer.core.exception.StreammerException;

/**
 * 解析flink sql sql 只支持 insert 开头的
 */

public class InsertSqlParser implements IParser {

	static String querySql = "";

	public boolean verify(String sql) {
		return StringUtils.isNotBlank(sql) && sql.trim().toLowerCase().startsWith("insert");
	}

	public static InsertSqlParser newInstance() {
		InsertSqlParser parser = new InsertSqlParser();
		return parser;
	}

	@Override
	public void parseSql(String queryName, String sql, SqlTree sqlTree) {

		// 采用Mysql语法
		SqlParser.Config config = SqlParser.configBuilder().setLex(Lex.MYSQL).build();
		SqlParser sqlParser = SqlParser.create(sql, config);

		SqlNode sqlNode = null;
		try {
			sqlNode = sqlParser.parseStmt();
		} catch (SqlParseException e) {
			throw new StreammerException("Query:" + queryName, e);
		}

		SqlParseResult sqlParseResult = new SqlParseResult();
		parseNode(sqlNode, sqlParseResult);

		sqlParseResult.setExecSql(sqlNode.toString());
		sqlParseResult.setQuerySql(querySql);
		sqlTree.setExecSql(sqlParseResult);
	}

	private static void parseNode(SqlNode sqlNode, SqlParseResult sqlParseResult) {
		SqlKind sqlKind = sqlNode.getKind();
		switch (sqlKind) {
		case INSERT:
			SqlNode sqlTarget = ((SqlInsert) sqlNode).getTargetTable();
			SqlNode sqlSource = ((SqlInsert) sqlNode).getSource();
			querySql = sqlSource.toString();
			sqlParseResult.setTargetTable(sqlTarget.toString());
			parseNode(sqlSource, sqlParseResult);
			break;
		case SELECT:
			SqlNode sqlFrom = ((SqlSelect) sqlNode).getFrom();
			if (sqlFrom.getKind() == IDENTIFIER) {
				sqlParseResult.addSourceTable(sqlFrom.toString());
			} else {
				parseNode(sqlFrom, sqlParseResult);
			}
			break;
		case JOIN:
			SqlNode leftNode = ((SqlJoin) sqlNode).getLeft();
			SqlNode rightNode = ((SqlJoin) sqlNode).getRight();

			if (leftNode.getKind() == IDENTIFIER) {
				sqlParseResult.addSourceTable(leftNode.toString());
			} else {
				parseNode(leftNode, sqlParseResult);
			}

			if (rightNode.getKind() == IDENTIFIER) {
				sqlParseResult.addSourceTable(rightNode.toString());
			} else {
				parseNode(rightNode, sqlParseResult);
			}
			break;
		case AS:
			// 不解析column,所以 as 相关的都是表
			SqlNode identifierNode = ((SqlBasicCall) sqlNode).getOperands()[0];
			if (identifierNode.getKind() != IDENTIFIER) {
				parseNode(identifierNode, sqlParseResult);
			} else {
				sqlParseResult.addSourceTable(identifierNode.toString());
			}
			break;
		default:
			// do nothing
			break;
		}
	}

	public static class SqlParseResult {

		private List<String> sourceTableList = new ArrayList<String>();

		private List<String> targetTableList = new ArrayList<String>();

		private String targetTable;

		private String execSql;

		private String querySql;

		public void addSourceTable(String sourceTable) {
			sourceTableList.add(sourceTable);
		}

		public void addTargetTable(String targetTable) {
			targetTableList.add(targetTable);
		}

		public List<String> getSourceTableList() {
			return sourceTableList;
		}

		public List<String> getTargetTableList() {
			return targetTableList;
		}

		public String getTargetTable() {
			return targetTable;
		}

		public void setTargetTable(String targetTable) {
			this.targetTable = targetTable;
		}

		public String getExecSql() {
			return execSql;
		}

		public void setExecSql(String execSql) {
			this.execSql = execSql;
		}

		public String getQuerySql() {
			return querySql;
		}

		public void setQuerySql(String querySql) {
			this.querySql = querySql;
		}
	}
}

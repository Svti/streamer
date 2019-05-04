package com.streamer.core.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.streamer.core.parser.CreateFuncParser.SqlParserResult;
import com.streamer.core.parser.InsertSqlParser.SqlParseResult;

public class SqlTree {

	private List<CreateFuncParser.SqlParserResult> functionList = new ArrayList<SqlParserResult>();

	private Map<String, CreateTableParser.SqlParserResult> preDealTableMap = new HashMap<>();

	private Map<String, CreateTableParser.SqlParserResult> preDealSinkMap = new HashMap<>();

	private Map<String, CreateSideParser.SqlParserResult> preDealSideMap = new HashMap<>();

	private Map<String, TableInfo> tableInfoMap = new LinkedHashMap<String, TableInfo>();

	private List<InsertSqlParser.SqlParseResult> execSqlList = new ArrayList<SqlParseResult>();

	private InsertSqlParser.SqlParseResult execSql;

	public List<CreateFuncParser.SqlParserResult> getFunctionList() {
		return functionList;
	}

	public Map<String, CreateTableParser.SqlParserResult> getPreDealTableMap() {
		return preDealTableMap;
	}

	public Map<String, CreateTableParser.SqlParserResult> getPreDealSinkMap() {
		return preDealSinkMap;
	}

	public List<InsertSqlParser.SqlParseResult> getExecSqlList() {
		return execSqlList;
	}

	public Map<String, CreateSideParser.SqlParserResult> getPreDealSideMap() {
		return preDealSideMap;
	}

	public void addFunc(CreateFuncParser.SqlParserResult func) {
		functionList.add(func);
	}

	public void addPreDealTableInfo(String tableName, CreateTableParser.SqlParserResult table) {
		preDealTableMap.put(tableName, table);
	}

	public void addPreDealSinkInfo(String tableName, CreateTableParser.SqlParserResult table) {
		preDealSinkMap.put(tableName, table);
	}

	public void addExecSql(InsertSqlParser.SqlParseResult execSql) {
		execSqlList.add(execSql);
	}

	public Map<String, TableInfo> getTableInfoMap() {
		return tableInfoMap;
	}

	public InsertSqlParser.SqlParseResult getExecSql() {
		return execSql;
	}

	public void setExecSql(InsertSqlParser.SqlParseResult execSql) {
		this.execSql = execSql;
	}

	public void addTableInfo(String tableName, TableInfo tableInfo) {
		tableInfoMap.put(tableName, tableInfo);
	}

	public void addPreSideTableInfo(String tableName, CreateSideParser.SqlParserResult result) {
		preDealSideMap.put(tableName, result);
	}

}

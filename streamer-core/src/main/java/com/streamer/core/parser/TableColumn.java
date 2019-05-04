package com.streamer.core.parser;

import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.commons.lang3.StringUtils;

public class TableColumn {

	private String column;

	private String alias;

	private SqlTypeName typeName;

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getAlias() {
		if (StringUtils.isNotEmpty(alias)) {
			return alias;
		} else {
			return column;
		}
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public SqlTypeName getTypeName() {
		return typeName;
	}

	public void setTypeName(SqlTypeName typeName) {
		this.typeName = typeName;
	}

}

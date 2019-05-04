
package com.streamer.core.parser;

import java.util.ArrayList;
import java.util.List;

import com.streamer.core.exception.StreammerException;

public abstract class TableInfo {

	public static final String PARALLELISM_KEY = "parallelism";

	private String name;

	private String type;

	private String[] fields;

	private String[] fieldTypes;

	private Class<?>[] fieldClasses;

	private final List<String> fieldList = new ArrayList<String>();

	private final List<String> fieldTypeList = new ArrayList<String>();

	@SuppressWarnings("rawtypes")
	private final List<Class> fieldClassList = new ArrayList<Class>();

	private List<String> primaryKeys;

	private Integer parallelism = 1;

	public String[] getFieldTypes() {
		return fieldTypes;
	}

	public abstract boolean check();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getFields() {
		return fields;
	}

	public Class<?>[] getFieldClasses() {
		return fieldClasses;
	}

	public List<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParallelism() {
		return parallelism;
	}

	public void setParallelism(Integer parallelism) {
		if (parallelism == null) {
			return;
		}

		if (parallelism <= 0) {
			throw new StreammerException("Abnormal parameter settings: parallelism > 0");
		}

		this.parallelism = parallelism;
	}

	public void addField(String fieldName) {
		fieldList.add(fieldName);
	}

	public void addFieldClass(Class<?> fieldClass) {
		fieldClassList.add(fieldClass);
	}

	public void addFieldType(String fieldType) {
		fieldTypeList.add(fieldType);
	}

	public void finish() {
		this.fields = fieldList.toArray(new String[fieldList.size()]);
		this.fieldClasses = fieldClassList.toArray(new Class[fieldClassList.size()]);
		this.fieldTypes = fieldTypeList.toArray(new String[fieldTypeList.size()]);
	}
}

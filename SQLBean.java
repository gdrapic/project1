package com.bpadomain.persistence.statements;

import java.util.Arrays;

public class SQLBean {
	
	String sqlName;
	String sqlDescription;
	String sqlDatasourceName;
	String sqlTransactionName;
	String sql;
	Object[] params;
	
	Integer affectedRecords;
	
	Long startTime = 0l;
	Long endTime = 0l;
	
	
	public SQLBean(String sqlName, String sqlDescription,
			String sql, Object... params) {
		super();
		this.sqlName = sqlName;
		this.sqlDescription = sqlDescription;
		this.sql = sql;
		this.params = params;
	}
	
	private SQLBean(String sqlName, String sqlDescription,
			String sqlDatasourceName, String sqlTransactionName, String sql,
			Object[] params) {
		super();
		this.sqlName = sqlName;
		this.sqlDescription = sqlDescription;
		this.sqlDatasourceName = sqlDatasourceName;
		this.sqlTransactionName = sqlTransactionName;
		this.sql = sql;
		this.params = params;
	}

	public Integer getAffectedRecords() {
		return affectedRecords;
	}
	
	public void setAffectedRecords(Integer affectedRecords) {
		this.affectedRecords = affectedRecords;
	}

	public String getSqlName() {
		return sqlName;
	}

	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}

	public String getSqlDescription() {
		return sqlDescription;
	}

	public void setSqlDescription(String sqlDescription) {
		this.sqlDescription = sqlDescription;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
		
	public String getSqlDatasourceName() {
		return sqlDatasourceName;
	}

	public void setSqlDatasourceName(String sqlDatasourceName) {
		this.sqlDatasourceName = sqlDatasourceName;
	}

	public String getSqlTransactionName() {
		return sqlTransactionName;
	}

	public void setSqlTransactionName(String sqlTransactionName) {
		this.sqlTransactionName = sqlTransactionName;
	}

	@Override
	public String toString() {
		return "SQLBean [sqlName=" + sqlName + ", sqlDescription="
				+ sqlDescription + ", sqlDatasourceName=" + sqlDatasourceName
				+ ", sqlTransactionName=" + sqlTransactionName + ", sql=" + sql
				+ ", params=" + Arrays.toString(params) + ", affectedRecords="
				+ affectedRecords + ", startTime=" + startTime + ", endTime="
				+ endTime + "]";
	}
}

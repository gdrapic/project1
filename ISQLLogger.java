package com.bpadomain.persistence.statements;

public interface ISQLLogger {
	public abstract <T> T beforeStart(SQLBean sqlBean) throws Exception;
	public abstract <T> T afterFinish(SQLBean sqlBean) throws Exception;
	public abstract <T> T afterException(SQLBean sqlBean, Throwable e) throws Exception;
}

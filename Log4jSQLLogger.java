package com.bpadomain.persistence.statements;

import org.apache.log4j.Logger;

public class Log4jSQLLogger implements ISQLLogger{
	private static Logger log = Logger.getLogger(Log4jSQLLogger.class);;
	@Override
	public Long beforeStart(SQLBean sqlBean) throws Exception {
		sqlBean.setStartTime(System.currentTimeMillis());
		log.info(sqlBean.toString());
		return sqlBean.getStartTime();
	}

	@Override
	public Long afterFinish(SQLBean sqlBean) throws Exception {
		sqlBean.setEndTime(System.currentTimeMillis());
		log.info(sqlBean.toString());
		return sqlBean.endTime;
	}

	@Override
	public Long afterException(SQLBean sqlBean, Throwable e) throws Exception {
		sqlBean.setEndTime(System.currentTimeMillis());
		log.error(sqlBean.toString());
		return sqlBean.endTime;
	}

}

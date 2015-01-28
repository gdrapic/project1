package com.bpadomain.app.batch.toolbox.tasks.components.sql;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

import com.bpadomain.app.batch.workers.STATUS;
import com.bpadomain.persistence.cache.RecordDAO;
import com.bpadomain.persistence.db.BPA_SQL_RUNTIME_LOG_COL;
import com.bpadomain.persistence.db.TABLE;
import com.bpadomain.persistence.runners.SQLRunner;
import com.bpadomain.persistence.runners.SQLSequence;
import com.bpadomain.persistence.statements.ISQLLogger;
import com.bpadomain.persistence.statements.SQLBean;
import com.bpadomain.persistence.statements.SQLBuilder;

public class BpaSQLRuntimeLogger implements ISQLLogger{
	long runtimeId;
	long sqlRuntimeId;

//	String sqlComment;

	
	private BpaSQLRuntimeLogger(long runtimeId) {
		super();
		this.runtimeId = runtimeId;
	}	
	
	@Override
	public Integer beforeStart(SQLBean sqlBean) throws Exception {
		
		SQLBuilder sqlBuilder = new SQLBuilder(TABLE.BPA_SQL_RUNTIME_LOG);
		String sql = sqlBuilder.sqlInsert().toString();
//		System.err.println(sql);

		sqlRuntimeId = new SQLSequence("SEQ_SQL_RUNTIME_ID").nextValue();
		String sqlParametersAsText = sqlBean.getParams()==null?null:Arrays.toString(sqlBean.getParams());
		
		sqlBean.setStartTime(System.currentTimeMillis());  
		Object[] values = new Object[]{
				runtimeId,	            				//RUNTIME_ID LONG NOT NULL,
				sqlRuntimeId,           				//SQL_RUNTIME_ID	LONG NOT NULL, 
				sqlBean.getSqlName(),	            	//SQL_NAME VARCHAR2(200), 
				sqlBean.getSqlDescription(),         	//SQL_DESCRIPTION VARCHAR2(500),
				sqlBean.getSqlTransactionName(),     	//SQL_TRANSACTION_NAME VARCHAR2(200),
				sqlBean.getSqlDatasourceName(),    		//SQL_DATASOURCE_NAME VARCHAR2(100),
				STATUS.RUN.toString(),  				//STATUS, 
				new Timestamp(sqlBean.getStartTime()), 	//SQL_CREATION_TIME TIMESTAMP, TODO: to be removed
				new Timestamp(sqlBean.getStartTime()),	//SQL_START_TIME TIMESTAMP,			
				null,                   				//SQL_UPDATE_TIME	TIMESTAMP,
				0l,                     				//SQL_ROWS_AFFECTED LONG,
				0l,              						//SQL_RUN_MILLS LONG,
				sqlBean.getSql(),                		//SQL_TEXT CLOB,
				sqlParametersAsText,    				//SQL_PARAMETERS CLOB, 
				null                    				//SQL_COMMENT CLOB,
			};
			
		Integer retVal = new SQLRunner().update(sql, values);

		return retVal;
	}

	@Override
	public Integer afterFinish(SQLBean sqlBean) throws Exception {
		sqlBean.setEndTime(System.currentTimeMillis());
		
		RecordDAO sqlLogRecord = new RecordDAO(TABLE.BPA_SQL_RUNTIME_LOG).selectByPk(sqlRuntimeId);
				
		Long sqlRuntimeLength = sqlBean.getEndTime() - sqlBean.getStartTime();
		
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_UPDATE_TIME.columnName, new Time(sqlBean.getEndTime()));
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_RUN_MILLS.columnName, sqlRuntimeLength);
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_ROWS_AFFECTED.columnName, sqlBean.getAffectedRecords());
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_STATUS.columnName, STATUS.FAL.toString());
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_COMMENT.columnName, "Done");
		
		Integer retVal = sqlLogRecord.update();

		return retVal;
	}
	

	@Override
	public Integer afterException(SQLBean sqlBean, Throwable t) throws Exception {

		sqlBean.setEndTime(System.currentTimeMillis());
		
		String sqlComment = null;
		if(t!=null){			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			sqlComment = getRootCause(t).getMessage() + "\n" + pw.toString();
			sw.close();
		}
	
		RecordDAO sqlLogRecord = new RecordDAO(TABLE.BPA_SQL_RUNTIME_LOG).selectByPk(sqlRuntimeId);
		
		Long sqlRuntimeLength = sqlBean.getEndTime() - sqlBean.getStartTime();
		
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_UPDATE_TIME.columnName, new Time(sqlBean.getEndTime()));
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_RUN_MILLS.columnName, sqlRuntimeLength);
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_ROWS_AFFECTED.columnName, sqlBean.getAffectedRecords());
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_STATUS.columnName, STATUS.FAL.toString());
		sqlLogRecord.setValue(BPA_SQL_RUNTIME_LOG_COL.SQL_COMMENT.columnName, sqlComment);
		
		Integer retVal = sqlLogRecord.update();

		return retVal;
	}

	public long getRuntimeId() {
		return runtimeId;
	}

	public void setRuntimeId(long runtimeId) {
		this.runtimeId = runtimeId;
	}

	private Throwable getRootCause(Throwable t){
		if(t==null) return null;
		Throwable cause = null;
		while(cause !=null){
			cause = cause.getCause();
		}
		return cause;
	}

}

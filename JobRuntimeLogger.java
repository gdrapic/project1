package com.bpadomain.app.batch.workers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.bpadomain.BPA;
import com.bpadomain.BPAEngine;
import com.bpadomain.persistence.cache.RecordDAO;
import com.bpadomain.persistence.cache.RowBean;
import com.bpadomain.persistence.cache.TableCache;
import com.bpadomain.persistence.db.BPA_JOB_RUNTIME_LOCK_COL;
import com.bpadomain.persistence.db.BPA_JOB_RUNTIME_LOG_COL;
import com.bpadomain.persistence.db.TABLE;
import com.bpadomain.persistence.runners.SQLRunner;
import com.bpadomain.persistence.runners.SQLSequence;
import com.bpadomain.persistence.runners.TableCacheRSHandler;
import com.bpadomain.persistence.statements.SQLBuilder;
import com.bpadomain.support.utils.datetime.DateDecorator;



/**
 * Author: Goran Drapic
 * Date: April 28, 2009
 */
public class JobRuntimeLogger extends RowBean{
	
	private static final Logger log = Logger.getLogger(JobRuntimeLogger.class);
	
	public JobRuntimeLogger(long runtimeId) throws Exception {	
		super(TABLE.BPA_JOB_RUNTIME_LOG, runtimeId);		
	}
	
	public Long getRuntimeId() throws Exception{
		return getValue(BPA_JOB_RUNTIME_LOG_COL.RUNTIME_ID);
	}
	
	public Integer getJobId() throws Exception{
		return getValue(BPA_JOB_RUNTIME_LOG_COL.JOB_ID);
	}
	
	public Date getReportingDate() throws Exception{
		return getValue(BPA_JOB_RUNTIME_LOG_COL.REPORTING_DATE);
	}
	
	public String getPidAtHost() throws Exception{
		return getValue(BPA_JOB_RUNTIME_LOG_COL.PID_AT_HOST);
	}
	
	public STATUS getStatus() throws Exception{
		String status = getValue(BPA_JOB_RUNTIME_LOG_COL.STATUS);
		return Enum.valueOf(STATUS.class, status);
	}
	

	public synchronized String lock() throws Exception{
		
		String key = BPA.PID_AT_HOST.value + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID() ;		
		
		Long runtimeId = getValue(BPA_JOB_RUNTIME_LOG_COL.RUNTIME_ID);
		
		//insert key into BPA_JOB_RUNTIME_LOCK 
		RecordDAO runtimeLockRecord = null;
		try {
			runtimeLockRecord = 
					new RecordDAO(TABLE.BPA_JOB_RUNTIME_LOCK).insert(
							runtimeId,
							key,
							new Timestamp(System.currentTimeMillis())
						);
			
		} catch (Exception e) {
			log.error("Failed to obtain lock[" + key + "], RUNTIME_ID[" + runtimeId	+ "]!", e);
			return null;
		}
				
		String sql = "update BPA_JOB_RUNTIME_LOG set LOCK = ? where RUNTIME_ID = ? and LOCK is null";
		
		int rowsAffected = new SQLRunner().update(sql, key, runtimeId );
		if (rowsAffected !=1){
			log.error("Lock update failed! lock[" + key + "], RUNTIME_ID[" + runtimeId	+ "]");
			return null;
//			throw new Exception("Failed to obtain lock!");
		}
		
		load();
		return key;		
		
	}
	
	public synchronized boolean unlock(String key) throws Exception{
		
		//refresh cache
		load(); 
		
		String keyToValidate = getValue(BPA_JOB_RUNTIME_LOG_COL.LOCK);
		
		if(!key.equals(keyToValidate)){
			return false;
		}
		
		setValue(BPA_JOB_RUNTIME_LOG_COL.LOCK, null);
		
		int affected = update();
		
		if(affected==1){
			Integer deletedCount = new SQLRunner().update(
					"delete from " + TABLE.BPA_JOB_RUNTIME_LOCK + 
					"\n where " + BPA_JOB_RUNTIME_LOCK_COL.RUNTIME_ID + "= ?", 
					getValue(BPA_JOB_RUNTIME_LOG_COL.RUNTIME_ID));
			if (deletedCount == 1) return true;
		}
		
		return false;
	}

	public int updateStatus(STATUS status) throws Exception{
		return updateStatus(status,null,null);
	}
	
	public int updateStatus(STATUS status, String schedulerComment) throws Exception{
		return updateStatus(status,schedulerComment,null);
	}
	
	public int updateStatus(STATUS status, String schedulerComment, String detailedComment) throws Exception{
		return updateStatus(status,schedulerComment,null,null);
	}
	
	public int updateStatus(STATUS status, Throwable t) throws Exception{
		return updateStatus(status,null,"",t);
	}

	public int updateStatus(STATUS status, String schedulerComment, String detailedComment, Throwable t) throws Exception{
		
		if(t!=null){			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			if (detailedComment!=null) detailedComment = detailedComment.concat("\nERROR:\n").concat(sw.toString());
			else detailedComment = "".concat("ERROR:\n").concat(sw.toString());
		}
		
		setValue(BPA_JOB_RUNTIME_LOG_COL.STATUS, status.toString());
		setValue(BPA_JOB_RUNTIME_LOG_COL.PID_AT_HOST, BPA.PID_AT_HOST.value);
		setValue(BPA_JOB_RUNTIME_LOG_COL.ENGINE_ID, BPAEngine.ENGINE_ID);
		setValue(BPA_JOB_RUNTIME_LOG_COL.COMMENT, schedulerComment);
		setValue(BPA_JOB_RUNTIME_LOG_COL.DETAIL_COMMENT, detailedComment);
		setValue(BPA_JOB_RUNTIME_LOG_COL.UPDATE_TIME, new Timestamp(System.currentTimeMillis()));
	
		// To make sure no other thread has taken runtime_id for processing
		//if (status = STATUS.RUN) 
		//   String sqlUpdate = "update BPA_JOB_RUNTIME_LOG set STATUS = 'RUN' where RUNTIME_ID = ? and STATUS = 'RED'" ;
		int retVal = update();
		
		if(retVal == 1 && status == STATUS.FAL){			
			TableCacheRSHandler rsh = new TableCacheRSHandler();
			TableCache result = new SQLRunner().insert(
					TABLE.BPA_JOB_RUNTIME_ERROR_LOG.sqlInsert, 
					rsh, 
					getValue(BPA_JOB_RUNTIME_LOG_COL.RUNTIME_ID),
					getValue(BPA_JOB_RUNTIME_LOG_COL.LOCK),
					getValue(BPA_JOB_RUNTIME_LOG_COL.JOB_ID),
					getValue(BPA_JOB_RUNTIME_LOG_COL.STATUS),
					getValue(BPA_JOB_RUNTIME_LOG_COL.REPORTING_DATE),
					getValue(BPA_JOB_RUNTIME_LOG_COL.PID_AT_HOST),
					getValue(BPA_JOB_RUNTIME_LOG_COL.ENGINE_ID),
					getValue(BPA_JOB_RUNTIME_LOG_COL.ENGINE_USER),
					getValue(BPA_JOB_RUNTIME_LOG_COL.CREATED_BY),
					getValue(BPA_JOB_RUNTIME_LOG_COL.CREATION_TIME),
					getValue(BPA_JOB_RUNTIME_LOG_COL.START_TIME),
					getValue(BPA_JOB_RUNTIME_LOG_COL.UPDATE_TIME),
					getValue(BPA_JOB_RUNTIME_LOG_COL.DELETED),
					getValue(BPA_JOB_RUNTIME_LOG_COL.COMMENT),
					getValue(BPA_JOB_RUNTIME_LOG_COL.DETAIL_COMMENT)
					);
			
			log.error("Inserting error into " + TABLE.BPA_JOB_RUNTIME_ERROR_LOG, t);
			log.error(result);
		}
		
		return retVal;
	}
		
	public static JobRuntimeLogger createNew(int jobId,Date reportingDate) throws Exception{
		return createNew(jobId, reportingDate, null);
	}
	
	public static JobRuntimeLogger createNew(int jobId, Date reportingDate, String createdBy) throws Exception{
		
		SQLBuilder sqlBuilder = new SQLBuilder(TABLE.BPA_JOB_RUNTIME_LOG);
		String sql = sqlBuilder.sqlInsert().toString();

		Date now = new Date();
		
		long runtimeId = new SQLSequence("SEQ_RUNTIME_ID").nextValue();
		createdBy = createdBy == null?System.getProperty("user.name"):createdBy;

		Object[] values = new Object[]{
			runtimeId,	//RUNTIME_ID, //invoke sequence
			null, //LOCK 
			jobId,	//JOB_ID, 
			STATUS.NEW.toString(), //STATUS, 
			reportingDate, //REPORTING_DATE, 
			BPA.PID_AT_HOST.value, //PID_AT_HOST,			
			BPAEngine.ENGINE_ID, //ENGINE_ID
			System.getProperty("user.name"),//ENGINE_USER 
			createdBy, //CREATED_BY, 
			now, //CREATION_TIME, 
			null, //START_TIME, 
			now, //UPDATE_TIME, 
			"N", //DELETED, 
			"New Job", //COMMENT, 
			null, //DETAIL_COMMENT
		};
		
		TableCache result = new SQLRunner().insert(sql, new TableCacheRSHandler(), values);
		
		JobRuntimeLogger instance = new JobRuntimeLogger(runtimeId);
		
		return instance;
	}
	
	public static void main(String[] args) {
		try {
			// initialize   DBConnService
			com.bpadomain.BPAEngine.getInstance(); 
			JobRuntimeLogger runtimeLogger = JobRuntimeLogger.createNew(102, new DateDecorator("20150111","yyyyMMdd"), "gd");
			System.err.println(runtimeLogger);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}

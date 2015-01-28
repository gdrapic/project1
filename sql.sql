
--------------------------------------------------------
--------------------------------------------------------
DROP TABLE IF EXISTS BPA_JOB_TYPE_EXCLUSION CASCADE CONSTRAINTS;

CREATE TABLE BPA_JOB_TYPE_EXCLUSION
(
	JOB_TYPE_ID				INT NOT NULL,
	JOB_TYPE_ID_EXCLUED		INT NOT NULL,
	DESCRIPTION	VARCHAR2(100),
	PRIMARY KEY (JOB_TYPE_ID, JOB_TYPE_ID_EXCLUED),
  	FOREIGN KEY (JOB_TYPE_ID) REFERENCES BPA_JOB_TYPE (JOB_TYPE_ID),
  	FOREIGN KEY (JOB_TYPE_ID_EXCLUED) REFERENCES BPA_JOB_TYPE (JOB_TYPE_ID)
);

insert into BPA_JOB_TYPE values (0, '-', '-','Represent all types in exclusion table BPA_JOB_TYPE_EXCLUSION');

insert into BPA_JOB_TYPE_EXCLUSION values (1,0,'BAT excludes All job types');
insert into BPA_JOB_TYPE_EXCLUSION values (4,0,'DEL excludes All job types!');

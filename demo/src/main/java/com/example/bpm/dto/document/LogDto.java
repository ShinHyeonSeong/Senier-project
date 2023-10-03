package com.example.bpm.dto.document;

import com.example.bpm.entity.document.LogEntity;
import com.example.bpm.service.Logic.dateLogic.DateManager;
import lombok.Data;

import java.util.Date;

@Data
public class LogDto implements Comparable<LogDto>{

    private String logId;

    private String documentId;

    private String log;

    private String dateLog;

    private  String logType;

    public LogEntity toEntity() {
        LogEntity logEntity = new LogEntity();

        logEntity.setLogId(this.logId);
        logEntity.setDocumentId(this.documentId);
        logEntity.setLog(this.log);
        logEntity.setDateLog(this.dateLog);
        logEntity.setLogType(this.logType);

        return logEntity;
    }

    public void insertEntity(LogEntity logEntity){
        this.logId = logEntity.getLogId();
        this.documentId = logEntity.getDocumentId();
        this.log = logEntity.getLog();
        this.dateLog = logEntity.getDateLog();
        this.logType = logEntity.getLogType();
    }

    @Override
    public int compareTo(LogDto logDto) {

        DateManager dateManager = new DateManager();
        Date logDate = dateManager.LogTimeConvert(logDto.getDateLog());
        Date thisLogDate = dateManager.LogTimeConvert(this.dateLog);

        if (logDate.getTime() < thisLogDate.getTime()) {
            return 1;
        } else if (logDate.getTime() > thisLogDate.getTime()) {
            return -1;
        }
        return 0;
    }
}

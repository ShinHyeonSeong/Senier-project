package com.example.bpm.dto;

import com.example.bpm.entity.Log;
import com.example.bpm.service.dateLogic.DateManager;
import lombok.Data;

import java.util.Date;

@Data
public class LogDto implements Comparable<LogDto>{

    private String logId;

    private String documentId;

    private String log;

    private String dateLog;

    private  String logType;

    public Log toEntity() {
        Log log = new Log();

        log.setLogId(this.logId);
        log.setDocumentId(this.documentId);
        log.setLog(this.log);
        log.setDateLog(this.dateLog);
        log.setLogType(this.logType);

        return  log;
    }

    public void insertEntity(Log log){
        this.logId = log.getLogId();
        this.documentId = log.getDocumentId();
        this.log = log.getLog();
        this.dateLog = log.getDateLog();
        this.logType = log.getLogType();
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

package com.example.bpm.entity.document;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class LogEntity {

    @Id
    @Column(name = "logId")
    private String logId;

    @Column(name = "documentId")
    private String documentId;

    @Column(name = "logContent")
    private String log;

    @Column(name = "dateLog")
    private String dateLog;

    @Column(name = "logType")
    private  String logType;
}

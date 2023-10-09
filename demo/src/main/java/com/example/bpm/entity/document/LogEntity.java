package com.example.bpm.entity.document;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "log")
public class LogEntity {

    @Id
    @Column(name = "log_id")
    private String logId;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "log_content")
    private String log;

    @Column(name = "date_log")
    private String dateLog;

    @Column(name = "log_type")
    private  String logType;
}

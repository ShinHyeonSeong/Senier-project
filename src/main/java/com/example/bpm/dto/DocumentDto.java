package com.example.bpm.dto;

import com.example.bpm.entity.Document;
import lombok.Data;

@Data
public class DocumentDto{

    private String documentId;

    private String title;

    private String uuid;

    private String userName;

    private String dateDocument;

    public Document toEntity() {
        Document document = new Document();

        document.setDocumentId(this.documentId);
        document.setTitle(this.title);
        document.setUuid(this.uuid);
        document.setUserName(this.userName);
        document.setDateDocument(dateDocument);

        return document;
    }

    public void insertEntity(Document document){
        this.documentId = document.getDocumentId();
        this.title = document.getTitle();
        this.uuid = document.getUuid();
        this.userName = document.getUserName();
        this.dateDocument = document.getDateDocument();
    }
}

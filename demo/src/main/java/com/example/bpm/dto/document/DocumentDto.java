package com.example.bpm.dto.document;

import com.example.bpm.entity.document.DocumentEntity;
import lombok.Data;

@Data
public class DocumentDto{

    private String documentId;

    private String title;

    private String uuid;

    private String userName;

    private String dateDocument;

    public DocumentEntity toEntity() {
        DocumentEntity documentEntity = new DocumentEntity();

        documentEntity.setDocumentId(this.documentId);
        documentEntity.setTitle(this.title);
        documentEntity.setUuid(this.uuid);
        documentEntity.setUserName(this.userName);
        documentEntity.setDateDocument(dateDocument);

        return documentEntity;
    }

    public void insertEntity(DocumentEntity documentEntity){
        this.documentId = documentEntity.getDocumentId();
        this.title = documentEntity.getTitle();
        this.uuid = documentEntity.getUuid();
        this.userName = documentEntity.getUserName();
        this.dateDocument = documentEntity.getDateDocument();
    }
}

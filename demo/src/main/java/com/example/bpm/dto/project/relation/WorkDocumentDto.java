package com.example.bpm.dto.project.relation;

import com.example.bpm.entity.document.DocumentEntity;
import com.example.bpm.entity.project.relation.WorkDocumentEntity;
import com.example.bpm.entity.project.data.WorkEntity;
import lombok.Data;

@Data
public class WorkDocumentDto {
    WorkEntity workIdToWorkDocument;
    DocumentEntity documentIdToWorkDocumentEntity;

    public WorkDocumentEntity toEntity(){
        WorkDocumentEntity workDocumentEntity = new WorkDocumentEntity();

        workDocumentEntity.setWorkIdToWorkDocument(workIdToWorkDocument);
        workDocumentEntity.setDocumentIdToWorkDocument(documentIdToWorkDocumentEntity);

        return workDocumentEntity;
    }

    public void insertEntity(WorkDocumentEntity workDocumentEntity){
        this.workIdToWorkDocument = workDocumentEntity.getWorkIdToWorkDocument();
        this.documentIdToWorkDocumentEntity = workDocumentEntity.getDocumentIdToWorkDocument();
    }

}

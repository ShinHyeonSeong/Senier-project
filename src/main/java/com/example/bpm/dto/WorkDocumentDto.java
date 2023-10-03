package com.example.bpm.dto;

import com.example.bpm.entity.Document;
import com.example.bpm.entity.WorkDocumentEntity;
import com.example.bpm.entity.WorkEntity;
import lombok.Data;
import org.hibernate.jdbc.Work;

@Data
public class WorkDocumentDto {
    WorkEntity workIdToWorkDocument;
    Document documentIdToWorkDocument;

    public WorkDocumentDto toWorkDocumentDto (WorkDocumentEntity workDocumentEntity) {
        WorkDocumentDto workDocumentDto = new WorkDocumentDto();
        workDocumentDto.setWorkIdToWorkDocument(workDocumentEntity.getWorkIdToWorkDocument());
        workDocumentDto.setDocumentIdToWorkDocument(workDocumentEntity.getDocumentIdToWorkDocument());
        return workDocumentDto;
    }

    public WorkDocumentEntity toWorkDocumentEntity (WorkDocumentDto workDocumentDto) {
        WorkDocumentEntity workDocumentEntity = new WorkDocumentEntity();
        workDocumentEntity.setWorkIdToWorkDocument(workDocumentDto.getWorkIdToWorkDocument());
        workDocumentEntity.setDocumentIdToWorkDocument(workDocumentDto.getDocumentIdToWorkDocument());
        return workDocumentEntity;
    }
}

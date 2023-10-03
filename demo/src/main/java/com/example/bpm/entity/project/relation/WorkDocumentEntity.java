package com.example.bpm.entity.project.relation;

import com.example.bpm.entity.document.DocumentEntity;
import com.example.bpm.entity.project.pk.WorkDocumentPKEntity;
import com.example.bpm.entity.project.data.WorkEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "work_document")
@IdClass(WorkDocumentPKEntity.class)
public class WorkDocumentEntity {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "work_id")
    private WorkEntity workIdToWorkDocument;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    private DocumentEntity documentIdToWorkDocument;

}

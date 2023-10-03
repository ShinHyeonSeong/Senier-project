package com.example.bpm.entity;

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
    private Document documentIdToWorkDocument;

}

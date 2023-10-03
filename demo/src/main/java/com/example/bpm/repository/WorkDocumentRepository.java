package com.example.bpm.repository;

import com.example.bpm.entity.project.relation.WorkDocumentEntity;
import com.example.bpm.entity.project.pk.WorkDocumentPKEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface WorkDocumentRepository extends JpaRepository<WorkDocumentEntity, WorkDocumentPKEntity> {

    public List<WorkDocumentEntity> findAllByWorkIdToWorkDocument_WorkId(Long id);
    public WorkDocumentEntity findByDocumentIdToWorkDocument_DocumentId(String id);

    @Transactional
    public void deleteAllByWorkIdToWorkDocument_WorkId(Long id);

    @Transactional
    public void deleteAllByDocumentIdToWorkDocument_DocumentId(String id);
}

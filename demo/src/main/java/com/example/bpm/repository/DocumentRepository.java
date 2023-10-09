package com.example.bpm.repository;

import com.example.bpm.entity.document.DocumentEntity;
import javax.persistence.Table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Table(name = "document")
public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {
    DocumentEntity findByDocumentId(String id);

    List<DocumentEntity> findByUuid(String id);

    DocumentEntity deleteByDocumentId(String id);


}

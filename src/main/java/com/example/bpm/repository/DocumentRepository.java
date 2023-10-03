package com.example.bpm.repository;

import com.example.bpm.entity.Document;
import javax.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Table(name = "document")
public interface DocumentRepository extends JpaRepository<Document, String> {
    Document findByDocumentId(String id);

    List<Document> findByUuid(String id);


}

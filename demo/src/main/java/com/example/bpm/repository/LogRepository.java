package com.example.bpm.repository;

import com.example.bpm.entity.document.LogEntity;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Table(name = "log")
public interface LogRepository extends JpaRepository<LogEntity, String> {
    List<LogEntity> findByDocumentId(String id);
    LogEntity findBylogId(String id);
    List<LogEntity> findAllByDocumentId(String id);

    @Transactional
    void deleteAllByDocumentId(String id);
}

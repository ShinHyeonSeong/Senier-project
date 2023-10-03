package com.example.bpm.repository;

import com.example.bpm.entity.document.BlockEntity;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Table(name = "block")
public interface BlockRepository extends JpaRepository<BlockEntity, String> {
    List<BlockEntity> findByDocumentId(String id);
    List<BlockEntity> findAllByDocumentId(String id);

    @Transactional
    void deleteAllByDocumentId(String id);
}

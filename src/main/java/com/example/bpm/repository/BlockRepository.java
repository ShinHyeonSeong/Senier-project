package com.example.bpm.repository;

import com.example.bpm.entity.Block;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Table(name = "block")
public interface BlockRepository extends JpaRepository<Block, String> {
    List<Block> findByDocumentId(String id);
    List<Block> findAllByDocumentId(String id);

    @Transactional
    void deleteAllByDocumentId(String id);
}

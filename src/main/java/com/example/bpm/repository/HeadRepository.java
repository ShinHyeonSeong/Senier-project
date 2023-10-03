package com.example.bpm.repository;

import com.example.bpm.entity.HeadEntity;
import com.example.bpm.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HeadRepository extends JpaRepository<HeadEntity, Long> {
    @Query(value = "select * from head where title = :title", nativeQuery = true)
    Optional<HeadEntity> findByTitle(String title);

    public List<HeadEntity> findAllByProjectIdToHead_ProjectId(Long projectId);
}
package com.example.bpm.repository;

import com.example.bpm.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @Transactional
    public void deleteByProjectId(Long projectId);
}

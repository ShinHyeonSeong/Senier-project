package com.example.bpm.repository;

import com.example.bpm.entity.project.data.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @Transactional
    public void deleteByProjectId(Long projectId);

    public List<ProjectEntity> findByTitleContaining(String titleKeyWord);
}

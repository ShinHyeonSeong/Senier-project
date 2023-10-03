package com.example.bpm.repository;

import com.example.bpm.entity.project.request.WorkRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRequestRepository extends JpaRepository<WorkRequestEntity, Long> {
    
}

package com.example.bpm.repository;

import com.example.bpm.entity.WorkCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkCommentRepository extends JpaRepository<WorkCommentEntity, Long> {

    List<WorkCommentEntity> findAllByWorkIdToComment_WorkId(Long workId);
    List<WorkCommentEntity> deleteAllByWorkIdToComment_WorkId(Long workId);

}

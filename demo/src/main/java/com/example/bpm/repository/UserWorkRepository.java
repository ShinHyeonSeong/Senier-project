package com.example.bpm.repository;

import com.example.bpm.entity.project.data.WorkEntity;
import com.example.bpm.entity.user.relation.UserWorkEntity;
import com.example.bpm.entity.user.pk.UserWorkPKEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserWorkRepository extends JpaRepository<UserWorkEntity, UserWorkPKEntity> {

    public List<UserWorkEntity> findAllByUserIdToUserWork_Uuid(String uuid);
    public List<UserWorkEntity> findAllByWorkIdToUserWork_WorkId(Long workId);
    public UserWorkEntity findByWorkIdToUserWork_WorkId(Long id);

    @Transactional
    public void deleteAllByWorkIdToUserWork_WorkId(Long id);

    @Transactional
    public void deleteAllByUserIdToUserWork_Uuid(String uuid);

    @Transactional
    public void deleteByWorkIdToUserWork_WorkIdAndUserIdToUserWork_Uuid(Long id, String uuid);
}

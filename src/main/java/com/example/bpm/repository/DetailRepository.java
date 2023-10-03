package com.example.bpm.repository;

import com.example.bpm.entity.DetailEntity;
import com.example.bpm.entity.HeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetailRepository extends JpaRepository<DetailEntity, Long> {
    @Query(value = "select * from detail where title = :title", nativeQuery = true)
    Optional<DetailEntity> findByTitle(String title);

    public List<DetailEntity>findAllByHeadIdToDetail_HeadId(Long headId);
    public List<DetailEntity>findAllByProjectIdToDetail_ProjectId(Long projectId);
    @Transactional
    public void deleteAllByDetailId(Long id);
    public void deleteByDetailId(Long id);

}

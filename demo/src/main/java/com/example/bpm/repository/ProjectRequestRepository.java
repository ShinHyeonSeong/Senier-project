package com.example.bpm.repository;

import com.example.bpm.entity.project.request.ProjectRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRequestRepository extends JpaRepository<ProjectRequestEntity, String> {

    @Query(value =
            "delete FROM" +
                    " project_requst " +
                    "WHERE send_uuid = :sendUUID AND recv_uuid = :recvUUID AND project_id = :projectId ",
            nativeQuery = true )
    void deleteByAllId(String sendUUID, String recvUUID, Long projectId);

    @Query(value = "insert into project_requst(send_uuid, recv_uuid, project_id) " +
            "values ( :sendUser, :recvUser, :projectId)", nativeQuery = true)
    void addProjectRequest(String sendUser, String recvUser, Long projectId);


    @Query(value = "select * " +
            "from project_requst where send_uuid = :sendUser AND recv_uuid = :recvUser AND project_id = :projectId ", nativeQuery = true)
    Optional<ProjectRequestEntity> selectTorequestRow(String sendUser, String recvUser, Long projectId);

    @Query(value = "select * from project_requst where recv_uuid = :recvUUID", nativeQuery = true)
    List<ProjectRequestEntity> findProjectRequest(String recvUUID);

    public List<ProjectRequestEntity> findAllByProjectIdToRequest_ProjectId(Long projectId);
    public void deleteAllByProjectIdToRequest_ProjectId(Long projectId);

}

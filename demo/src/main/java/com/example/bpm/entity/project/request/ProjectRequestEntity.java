package com.example.bpm.entity.project.request;

import com.example.bpm.dto.project.request.ProjectRequestDto;
import javax.persistence.*;

import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.project.pk.ProjectRequestPKEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_requst")
@IdClass(ProjectRequestPKEntity.class)
public class ProjectRequestEntity {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "send_uuid")
    private UserEntity sendUUID;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recv_uuid")
    private UserEntity recvUUID;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private ProjectEntity projectIdToRequest;

}

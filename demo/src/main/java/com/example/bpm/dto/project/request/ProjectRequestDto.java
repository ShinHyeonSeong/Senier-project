package com.example.bpm.dto.project.request;

import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.project.request.ProjectRequestEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {
    private UserEntity sendUUID;
    private UserEntity recvUUID;
    private ProjectEntity projoectIdToRequest;

    public ProjectRequestEntity toEntity(){
        ProjectRequestEntity projectRequestEntity = new ProjectRequestEntity();

        projectRequestEntity.setSendUUID(sendUUID);
        projectRequestEntity.setRecvUUID(recvUUID);
        projectRequestEntity.setProjectIdToRequest(projoectIdToRequest);

        return projectRequestEntity;
    }

    public void insertEntity(ProjectRequestEntity projectRequestEntity){
        this.sendUUID = projectRequestEntity.getSendUUID();
        this.recvUUID = projectRequestEntity.getRecvUUID();
        this.projoectIdToRequest = projectRequestEntity.getProjectIdToRequest();
    }
}

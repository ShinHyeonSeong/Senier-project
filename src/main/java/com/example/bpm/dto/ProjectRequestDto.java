package com.example.bpm.dto;

import com.example.bpm.entity.ProjectEntity;
import com.example.bpm.entity.ProjectRequestEntity;
import com.example.bpm.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {
    private UserEntity sendUUID;
    private UserEntity recvUUID;
    private ProjectEntity prjoectIdToRequest;

    public static ProjectRequestDto toProjectRequestDto(ProjectRequestEntity projectRequestEntity) {
        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setSendUUID(projectRequestEntity.getSendUUID());
        projectRequestDto.setRecvUUID(projectRequestEntity.getRecvUUID());
        projectRequestDto.setPrjoectIdToRequest(projectRequestEntity.getProjectIdToRequest());
        return projectRequestDto;
    }
}

package com.example.bpm.entity.project.pk;

import com.example.bpm.entity.project.data.ProjectEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestPKEntity implements Serializable {
    private UserEntity sendUUID;
    private UserEntity recvUUID;
    private ProjectEntity projectIdToRequest;
}

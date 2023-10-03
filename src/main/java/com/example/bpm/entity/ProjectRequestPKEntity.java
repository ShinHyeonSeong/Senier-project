package com.example.bpm.entity;

import javax.persistence.*;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestPKEntity implements Serializable {
    private UserEntity sendUUID;
    private UserEntity recvUUID;
}

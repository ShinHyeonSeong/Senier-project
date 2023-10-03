package com.example.bpm.dto;

import com.example.bpm.entity.Document;
import com.example.bpm.entity.WorkCommentEntity;
import com.example.bpm.entity.UserEntity;
import com.example.bpm.entity.WorkEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkCommentDto {
    private Long workCommentId;

    private String comment;

    private WorkEntity workIdToComment;

    private UserEntity userIdToComment;

    public static WorkCommentDto toWorkCommentDto (WorkCommentEntity workCommentEntity){
        WorkCommentDto workCommentDto = new WorkCommentDto();
        workCommentDto.setWorkCommentId(workCommentEntity.getWorkCommentId());
        workCommentDto.setComment(workCommentEntity.getComment());
        workCommentDto.setWorkIdToComment(workCommentEntity.getWorkIdToComment());
        workCommentDto.setUserIdToComment(workCommentEntity.getUserIdToComment());
        return workCommentDto;
    }

    public static WorkCommentEntity toWorkCommentEntity (WorkCommentDto workCommentDto) {
        WorkCommentEntity workCommentEntity = new WorkCommentEntity();
        workCommentEntity.setWorkCommentId(workCommentDto.getWorkCommentId());
        workCommentEntity.setComment(workCommentDto.getComment());
        workCommentEntity.setWorkIdToComment(workCommentDto.getWorkIdToComment());
        workCommentEntity.setUserIdToComment(workCommentDto.getUserIdToComment());
        return workCommentEntity;
    }


}

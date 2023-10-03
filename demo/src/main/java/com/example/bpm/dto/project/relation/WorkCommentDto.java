package com.example.bpm.dto.project.relation;

import com.example.bpm.entity.project.data.work.WorkCommentEntity;
import com.example.bpm.entity.user.UserEntity;
import com.example.bpm.entity.project.data.WorkEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkCommentDto {
    private Long workCommentId;

    private String comment;

    private WorkEntity workIdToComment;

    private UserEntity userIdToComment;

    public WorkCommentEntity toEntity(){
        WorkCommentEntity workCommentEntity = new WorkCommentEntity();

        workCommentEntity.setWorkCommentId(workCommentId);
        workCommentEntity.setComment(comment);
        workCommentEntity.setWorkIdToComment(workIdToComment);
        workCommentEntity.setUserIdToComment(userIdToComment);

        return  workCommentEntity;
    }

    public void insertEntity(WorkCommentEntity workCommentEntity){
        this.workCommentId = workCommentEntity.getWorkCommentId();
        this.comment = workCommentEntity.getComment();
        this.workIdToComment = workCommentEntity.getWorkIdToComment();
        this.userIdToComment = workCommentEntity.getUserIdToComment();
    }
}

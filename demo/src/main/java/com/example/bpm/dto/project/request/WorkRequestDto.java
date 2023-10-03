package com.example.bpm.dto.project.request;

import com.example.bpm.entity.project.data.HeadEntity;
import com.example.bpm.entity.project.request.WorkRequestEntity;
import com.example.bpm.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkRequestDto {

    private Long workId;
    private String title;
    private Date startDay;
    private Date endDay;
    private int completion;
    private UserEntity userIdToWorkRequest;
    private HeadEntity headIdToWorkRequest;

    public WorkRequestEntity toEntity(){
        WorkRequestEntity workRequestEntity = new WorkRequestEntity();

        workRequestEntity.setWorkId(workId);
        workRequestEntity.setTitle(title);
        workRequestEntity.setStartDay(startDay);
        workRequestEntity.setEndDay(endDay);
        workRequestEntity.setCompletion(completion);
        workRequestEntity.setUserIdToWorkRequest(userIdToWorkRequest);
        workRequestEntity.setHeadIdToWorkRequest(headIdToWorkRequest);

        return workRequestEntity;
    }

    public void insertEntity(WorkRequestEntity workRequestEntity){
        this.workId = workRequestEntity.getWorkId();
        this.title = workRequestEntity.getTitle();
        this.startDay = workRequestEntity.getStartDay();
        this.endDay = workRequestEntity.getEndDay();
        this.completion = workRequestEntity.getCompletion();
        this.userIdToWorkRequest = workRequestEntity.getUserIdToWorkRequest();
        this.headIdToWorkRequest = workRequestEntity.getHeadIdToWorkRequest();
    }

}

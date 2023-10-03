package com.example.bpm.dto.project;

import com.example.bpm.entity.project.data.HeadEntity;
import com.example.bpm.entity.project.data.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

//목표 DTO
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HeadDto {

    private Long headId;

    private String title;

    private String discription;

    private Date startDay;

    private Date endDay;

    private int completion;

    private ProjectEntity projectIdToHead;

    public HeadEntity toEntity(){
        HeadEntity headEntity = new HeadEntity();

        headEntity.setHeadId(headId);
        headEntity.setTitle(title);
        headEntity.setDiscription(discription);
        headEntity.setStartDay(startDay);
        headEntity.setEndDay(endDay);
        headEntity.setCompletion(completion);
        headEntity.setProjectIdToHead(projectIdToHead);

        return headEntity;
    }

    public void insertEntity(HeadEntity headEntity){
        this.headId = headEntity.getHeadId();
        this.title = headEntity.getTitle();
        this.discription = headEntity.getDiscription();
        this.startDay = headEntity.getStartDay();
        this.endDay = headEntity.getEndDay();
        this.completion = headEntity.getCompletion();
        this.projectIdToHead = headEntity.getProjectIdToHead();
    }
}

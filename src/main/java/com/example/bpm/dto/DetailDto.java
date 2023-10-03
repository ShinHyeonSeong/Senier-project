package com.example.bpm.dto;

import com.example.bpm.entity.DetailEntity;
import com.example.bpm.entity.HeadEntity;
import com.example.bpm.entity.ProjectEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DetailDto {

    private Long detailId;

    private String title;

    private String discription;

    private Date startDay;

    private Date endDay;

    private int completion;

    private HeadEntity headIdToDetail;

    private ProjectEntity projectIdToDetail;


    public static DetailDto toDetailDto(DetailEntity detailEntity) {
        DetailDto detailDto = new DetailDto();
        detailDto.setDetailId(detailEntity.getDetailId());
        detailDto.setTitle(detailEntity.getTitle());
        detailDto.setDiscription(detailEntity.getDiscription());
        detailDto.setStartDay(detailEntity.getStartDay());
        detailDto.setEndDay(detailEntity.getEndDay());
        detailDto.setCompletion(detailEntity.getCompletion());
        detailDto.setHeadIdToDetail(detailEntity.getHeadIdToDetail());
        detailDto.setProjectIdToDetail(detailEntity.getProjectIdToDetail());
        return detailDto;
    }
}

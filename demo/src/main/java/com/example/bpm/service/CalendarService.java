package com.example.bpm.service;

import com.example.bpm.entity.project.data.HeadEntity;
import com.example.bpm.entity.project.data.WorkEntity;
import com.example.bpm.repository.HeadRepository;
import com.example.bpm.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarService {
    @Autowired
    HeadRepository headRepository;

    @Autowired
    WorkRepository workRepository;

    public List<Map<String, Object>> getEventList(Long projectId) {
        List<HeadEntity> headEntityList = headRepository.findAllByProjectIdToHead_ProjectId(projectId);

        HashMap<String, Object> event = new HashMap<String, Object>();
        List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (HeadEntity headEntity : headEntityList) {
            event = new HashMap<String, Object>();
            event.put("start", simpleDateFormat.format(headEntity.getStartDay()));
            event.put("title", headEntity.getTitle());
            event.put("end", simpleDateFormat.format(headEntity.getEndDay()));
            event.put("color","#6fa8dc");
            event.put("textColor", "#000000");
            eventList.add(event);
        }

        List<WorkEntity> workEntityList = workRepository.findAllByProjectIdToWork_ProjectId(projectId);

        for (WorkEntity workEntity: workEntityList) {
            event = new HashMap<String, Object>();
            event.put("start", simpleDateFormat.format(workEntity.getStartDay()));
            event.put("title", workEntity.getTitle());
            event.put("end", simpleDateFormat.format(workEntity.getEndDay()));
            event.put("color","#f6b26b");
            event.put("textColor", "#000000");
            eventList.add(event);
        }

        return eventList;
    }

}

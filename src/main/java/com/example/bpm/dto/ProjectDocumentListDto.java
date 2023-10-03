package com.example.bpm.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectDocumentListDto {

    private String workName;

    private List<DocumentDto> documentDtoList;
}

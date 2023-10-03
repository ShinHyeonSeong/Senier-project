package com.example.bpm.dto.project.relation;

import com.example.bpm.dto.document.DocumentDto;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDocumentListDto {

    private String workName;

    private List<DocumentDto> documentDtoList;
}

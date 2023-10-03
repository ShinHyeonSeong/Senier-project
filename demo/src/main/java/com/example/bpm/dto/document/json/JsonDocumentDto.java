package com.example.bpm.dto.document.json;

import com.example.bpm.entity.document.BlockEntity;
import com.example.bpm.entity.document.DocumentEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Data
public class JsonDocumentDto {

    private String title;

    private String id;

    private ArrayList<LinkedHashMap> blockList;

    public DocumentEntity documentEntityOut(){

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setTitle(this.title);
        documentEntity.setDocumentId(this.id);

        return documentEntity;
    }

    public List<BlockEntity> blockEntityOut(){

        List<BlockEntity> blockEntityEntityList = new ArrayList<>();

        for (int i = 0; i < blockList.size(); i++) {

            LinkedHashMap blockData = blockList.get(i);

            BlockEntity blockEntity = new BlockEntity();

            UUID uuid = UUID.randomUUID();
            blockEntity.setBlockId(uuid.toString());
            blockEntity.setDocumentId(this.id);
            blockEntity.setIndex(Integer.parseInt(blockData.get("index").toString()));
            blockEntity.setContent(blockData.get("content").toString());
            blockEntity.setCategory(blockData.get("category").toString());

            blockEntityEntityList.add(blockEntity);
        }

        return blockEntityEntityList;
    }
}

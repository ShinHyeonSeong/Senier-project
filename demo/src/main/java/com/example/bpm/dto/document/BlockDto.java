package com.example.bpm.dto.document;

import com.example.bpm.entity.document.BlockEntity;
import lombok.Data;

@Data
public class BlockDto implements Comparable<BlockDto> {

    private String blockId;

    private String documentId;

    private long index;

    private String content;

    private String category;

    public BlockEntity toEntity() {
        BlockEntity blockEntity = new BlockEntity();

        blockEntity.setBlockId(blockId);
        blockEntity.setDocumentId(documentId);
        blockEntity.setContent(content);
        blockEntity.setIndex(index);
        blockEntity.setContent(content);
        blockEntity.setCategory(category);

        return blockEntity;
    }

    public void insertEntity(BlockEntity blockEntity){
        this.blockId = blockEntity.getBlockId();
        this.documentId = blockEntity.getDocumentId();
        this.index = blockEntity.getIndex();
        this.content = blockEntity.getContent();
        this.category = blockEntity.getCategory();
    }

    @Override
    public int compareTo(BlockDto blockDto) {
        if (blockDto.index < this.index) {
            return 1;
        } else if (blockDto.index > this.index) {
            return -1;
        }
        return 0;
    }
}

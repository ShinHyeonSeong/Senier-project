package com.example.bpm.dto;

import com.example.bpm.entity.Block;
import lombok.Data;

@Data
public class BlockDto implements Comparable<BlockDto> {

    private String blockId;

    private String documentId;

    private long index;

    private String content;

    private String category;

    public Block toEntity() {
        Block block = new Block();

        block.setBlockId(blockId);
        block.setDocumentId(documentId);
        block.setContent(content);
        block.setIndex(index);
        block.setContent(content);
        block.setCategory(category);

        return block;
    }

    public void insertEntity(Block block){
        this.blockId = block.getBlockId();
        this.documentId = block.getDocumentId();
        this.index = block.getIndex();
        this.content = block.getContent();
        this.category = block.getCategory();
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

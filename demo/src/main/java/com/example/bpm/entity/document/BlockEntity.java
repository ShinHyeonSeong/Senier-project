package com.example.bpm.entity.document;

import javax.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class BlockEntity implements Serializable {

    @Id
    @Column(name = "blockId")
    private String blockId;

    @Column(name = "documentId")
    private String documentId;

    @Column(name = "index_num")
    private long index;

    @Column(name = "content")
    private String content;

    @Column(name = "category")
    private String category;

}

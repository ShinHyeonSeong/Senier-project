package com.example.bpm.entity;

import javax.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Block implements Serializable {

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

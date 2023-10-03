package com.example.bpm.service.Logic.logLogic;

import com.example.bpm.entity.document.BlockEntity;
import com.example.bpm.entity.document.DocumentEntity;

import java.io.*;
import java.util.Base64;

public class LogManager {
    public String changeBlockToString(BlockEntity blockEntity) {
        String blockString = "";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(blockEntity);

                //직렬화(byte array)
                byte[] serializedUser = baos.toByteArray();

                //byte array를 base64로 변환
                blockString = Base64.getEncoder().encodeToString(serializedUser);
            }
        } catch (IOException e) {
            return blockString;
        }

        return blockString;
    }

    public String changeDocumentToString(DocumentEntity documentEntity) {
        String documentString = "";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(documentEntity);

                //직렬화(byte array)
                byte[] serializedUser = baos.toByteArray();

                //byte array를 base64로 변환
                documentString = Base64.getEncoder().encodeToString(serializedUser);
            }
        } catch (IOException e) {
            return documentString;
        }

        return documentString;
    }

    public BlockEntity deserializeblock(String blockLog) {
        BlockEntity blockEntity = null;
        byte[] serializedBlock = Base64.getDecoder().decode(blockLog);

        try(ByteArrayInputStream bais = new ByteArrayInputStream(serializedBlock)) {
            try(ObjectInputStream ois = new ObjectInputStream(bais)) {
                //역직렬화(byte array -> object)
                Object objectBlock = ois.readObject();
                blockEntity = (BlockEntity) objectBlock;

                return blockEntity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return blockEntity;
    }

    public DocumentEntity deserializeDocument(String documentLog) {
        DocumentEntity documentEntity = null;
        byte[] serializedDocument = Base64.getDecoder().decode(documentLog);

        try(ByteArrayInputStream bais = new ByteArrayInputStream(serializedDocument)) {
            try(ObjectInputStream ois = new ObjectInputStream(bais)) {
                //역직렬화(byte array -> object)
                Object objectDocument = ois.readObject();
                documentEntity = (DocumentEntity) objectDocument;

                return documentEntity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return documentEntity;
    }
}

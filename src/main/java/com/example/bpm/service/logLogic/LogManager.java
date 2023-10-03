package com.example.bpm.service.logLogic;

import com.example.bpm.entity.Block;
import com.example.bpm.entity.Document;

import java.io.*;
import java.util.Base64;

public class LogManager {
    public String changeBlockToString(Block block) {
        String blockString = "";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(block);

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

    public String changeDocumentToString(Document document) {
        String documentString = "";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(document);

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

    public Block deserializeblock(String blockLog) {
        Block block = null;
        byte[] serializedBlock = Base64.getDecoder().decode(blockLog);

        try(ByteArrayInputStream bais = new ByteArrayInputStream(serializedBlock)) {
            try(ObjectInputStream ois = new ObjectInputStream(bais)) {
                //역직렬화(byte array -> object)
                Object objectBlock = ois.readObject();
                block = (Block) objectBlock;

                return block;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return block;
    }

    public Document deserializeDocument(String documentLog) {
        Document document = null;
        byte[] serializedDocument = Base64.getDecoder().decode(documentLog);

        try(ByteArrayInputStream bais = new ByteArrayInputStream(serializedDocument)) {
            try(ObjectInputStream ois = new ObjectInputStream(bais)) {
                //역직렬화(byte array -> object)
                Object objectDocument = ois.readObject();
                document = (Document) objectDocument;

                return document;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }
}

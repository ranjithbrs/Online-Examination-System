package com.examsystem.onlineexam.dto;

public class SnapshotItemDto {

    private String type;
    private String imageBase64;
    private String note;
    private String timestamp;

    public SnapshotItemDto() {
    }

    public SnapshotItemDto(String type, String imageBase64, String note, String timestamp) {
        this.type = type;
        this.imageBase64 = imageBase64;
        this.note = note;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

package com.borchowiec.notez.payload;

public class ReportRequest {
    private String content;
    private long songId;

    public ReportRequest() {
    }

    public ReportRequest(String content, long songId) {
        this.content = content;
        this.songId = songId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}

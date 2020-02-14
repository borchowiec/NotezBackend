package com.borchowiec.notez.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Entity that represents song.
 */
@Entity
public class Song {
    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String author;
    private String album;

    @Column(columnDefinition = "TEXT")
    private String content;

    @JsonIgnore
    private int views;

    public Song() {
    }

    public Song(long id, String name, String author, String album, String content, int views) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.album = album;
        this.content = content;
        this.views = views;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}

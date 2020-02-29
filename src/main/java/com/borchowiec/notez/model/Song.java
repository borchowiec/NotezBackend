package com.borchowiec.notez.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Entity that represents song.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Song {
    @Id
    @GeneratedValue
    private long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String author;

    @NotBlank
    @Size(max = 100)
    private String album;

    @Column(columnDefinition = "TEXT")
    private String content;

    @JsonIgnore
    private int views;

    @JsonIgnore
    @CreatedDate
    private long createdDate;

    @JsonIgnore
    @LastModifiedDate
    private long lastModifiedDate;

    @JsonIgnore
    @CreatedBy
    private long createdBy;

    @JsonIgnore
    @LastModifiedBy
    private long lastModifiedBy;

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

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}

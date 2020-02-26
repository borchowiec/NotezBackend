package com.borchowiec.notez.model;

import java.util.List;

//todo move it to payload
/**
 * Represents result of searching songs by given phrase. Phrase can be containing in name, author or album. That's why
 * there is three lists that represents these results.
 */
public class SearchResult {
    private List<Song> byName;
    private List<Song> byAuthor;
    private List<Song> byAlbum;

    public List<Song> getByName() {
        return byName;
    }

    public void setByName(List<Song> byName) {
        this.byName = byName;
    }

    public List<Song> getByAuthor() {
        return byAuthor;
    }

    public void setByAuthor(List<Song> byAuthor) {
        this.byAuthor = byAuthor;
    }

    public List<Song> getByAlbum() {
        return byAlbum;
    }

    public void setByAlbum(List<Song> byAlbum) {
        this.byAlbum = byAlbum;
    }
}

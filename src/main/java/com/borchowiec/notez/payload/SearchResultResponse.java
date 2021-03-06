package com.borchowiec.notez.payload;

import com.borchowiec.notez.model.Song;

import java.util.List;

/**
 * Represents result of searching songs by given phrase. Phrase can be containing in name, author or album. That's why
 * there is three lists that represents these results.
 */
public class SearchResultResponse {
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

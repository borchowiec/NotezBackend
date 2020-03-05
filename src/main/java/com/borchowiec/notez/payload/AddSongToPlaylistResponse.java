package com.borchowiec.notez.payload;

import com.borchowiec.notez.model.Song;

import java.util.Objects;

public class AddSongToPlaylistResponse {
    private Song song;
    private long playlistId;
    private int songIndex;

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public int getSongIndex() {
        return songIndex;
    }

    public void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddSongToPlaylistResponse that = (AddSongToPlaylistResponse) o;
        return playlistId == that.playlistId &&
                songIndex == that.songIndex &&
                Objects.equals(song, that.song);
    }

    @Override
    public int hashCode() {
        return Objects.hash(song, playlistId, songIndex);
    }
}

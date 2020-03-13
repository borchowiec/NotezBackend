package com.borchowiec.notez.payload;

import com.borchowiec.notez.model.Playlist;

import java.util.Objects;

public class PlaylistResponse {
    private boolean isOwner;
    private Playlist playlist;

    public PlaylistResponse() {
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistResponse that = (PlaylistResponse) o;
        return isOwner == that.isOwner &&
                Objects.equals(playlist, that.playlist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isOwner, playlist);
    }
}

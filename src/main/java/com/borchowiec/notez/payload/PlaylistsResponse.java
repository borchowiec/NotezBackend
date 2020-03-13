package com.borchowiec.notez.payload;

import com.borchowiec.notez.model.Playlist;

import java.util.List;
import java.util.Objects;

public class PlaylistsResponse {
    private boolean isOwner;
    private List<Playlist> playlists;

    public PlaylistsResponse() {
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistsResponse that = (PlaylistsResponse) o;
        return isOwner == that.isOwner &&
                Objects.equals(playlists, that.playlists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isOwner, playlists);
    }
}

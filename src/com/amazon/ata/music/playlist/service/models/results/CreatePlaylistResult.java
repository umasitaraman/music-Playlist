package com.amazon.ata.music.playlist.service.models.results;

import com.amazon.ata.music.playlist.service.models.PlaylistModel;

import java.util.List;
import java.util.Set;

public class CreatePlaylistResult {
    private PlaylistModel playlist;
    private String customerId;
    private String name;
    private Set<String> tags;

    public CreatePlaylistResult(Builder builder) {
        this.playlist = builder.playlist;
    }

    public PlaylistModel getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlaylistModel playlist) {
        this.playlist = playlist;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public static Builder builder() {return new Builder();}

    public static final class Builder {
        private PlaylistModel playlist;
        private String customerId;
        private String name;
        private List<String> tags;


        public Builder withPlaylist(PlaylistModel playlist) {
            this.playlist = playlist;
            return this;
        }

        public Builder withCustomerID(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public CreatePlaylistResult build() {return new CreatePlaylistResult(this);}
    }
}

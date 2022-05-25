package com.amazon.ata.music.playlist.service.converters;

import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.beust.jcommander.internal.Lists;


import java.util.*;

public class ModelConverter {
    /**
     * Converts a provided {@link Playlist} into a {@link PlaylistModel} representation.
     * @param playlist the playlist to convert
     * @return the converted playlist
     */
    public PlaylistModel toPlaylistModel(Playlist playlist) {

        Set<String> tags = playlist.getTags();
        List<String> tagsList = new ArrayList<>();
        if (tags == null) {
            tagsList = null;
        } else {
            tagsList = Lists.newArrayList(tags);
        }

        return PlaylistModel.builder()
            .withId(playlist.getId())
            .withName(playlist.getName())
            .withCustomerId(playlist.getCustomerId())
            .withSongCount(playlist.getSongCount())
            .withTags(tagsList)
            .build();
    }

    public SongModel toSongModel (AlbumTrack albumTrack) {

        return SongModel.builder()
                .withAlbum(albumTrack.getAlbumName())
                .withAsin(albumTrack.getAsin())
                .withTitle(albumTrack.getSongTitle())
                .withTrackNumber(albumTrack.getTrackNumber())
                .build();
    }
    public List<SongModel> toSongModelList (List<AlbumTrack> albumTrack) {
        List<SongModel> songModelList = new ArrayList<>();
        for (AlbumTrack song : albumTrack) {
            songModelList.add(toSongModel(song));
        }
        return songModelList;
    }

}

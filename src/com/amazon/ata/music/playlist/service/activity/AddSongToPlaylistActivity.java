package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.AddSongToPlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.AddSongToPlaylistResult;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Implementation of the AddSongToPlaylistActivity for the MusicPlaylistService's AddSongToPlaylist API.
 *
 * This API allows the customer to add a song to their existing playlist.
 */
public class AddSongToPlaylistActivity implements RequestHandler<AddSongToPlaylistRequest, AddSongToPlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;
    private final AlbumTrackDao albumTrackDao;

    /**
     * Instantiates a new AddSongToPlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     * @param albumTrackDao AlbumTrackDao to access the album_track table.
     */
    public AddSongToPlaylistActivity(PlaylistDao playlistDao, AlbumTrackDao albumTrackDao) {
        this.playlistDao = playlistDao;
        this.albumTrackDao = albumTrackDao;
    }

    /**
     * This method handles the incoming request by adding an additional song
     * to a playlist and persisting the updated playlist.
     * <p>
     * It then returns the updated song list of the playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the album track does not exist, this should throw an AlbumTrackNotFoundException.
     *
     * @param addSongToPlaylistRequest request object containing the playlist ID and an asin and track number
     *                                 to retrieve the song data
     * @return addSongToPlaylistResult result object containing the playlist's updated list of
     *                                 API defined {@link SongModel}s
     */
    @Override
    public AddSongToPlaylistResult handleRequest(final AddSongToPlaylistRequest addSongToPlaylistRequest, Context context) {
        log.info("Received AddSongToPlaylistRequest {} ", addSongToPlaylistRequest);

        //addSongActivity -> playlistDao : getPlayList(id)
        //playlistDao -> dynamoDB : lookup by playlist id
        Playlist playlist = playlistDao.getPlaylist(addSongToPlaylistRequest.getId());

        //if playlist does not exist --> dynamoDB --> playlistDao : return with no item data -->
        // playlistDao --> addSongActivity: throw PlayListNotFoundException
        if (playlist == null) {
            throw new PlaylistNotFoundException();
        }
        //dynamoDB --> playlistDao: return playlist data
        //addSongActivity -> albumTrackDao : getAlbumTrack(asin, trackNumber)
        // dynamoDB --> albumTrackDao: return with album_track data
        // albumTrackDao --> addSongActivity : return AlbumTrack object
        AlbumTrack albumTrack = albumTrackDao.getAlbumTrack(addSongToPlaylistRequest.getAsin(), addSongToPlaylistRequest.getTrackNumber());
        // albumTrackDao -> dynamoDB : lookup by asin, track_number
        // if song does not exist --> dynamoDB --> albumTrackDao: return with no album_track data -->
        // albumTrackDao --> addSongActivity : throw AlbumTrackNotFoundException
        // addSongActivity --> apiGateway: propagate AlbumTrackNotFoundException
        if (albumTrack == null) {
            throw new AlbumTrackNotFoundException();
        }
        // alt if request.queueNext is provided and true
        // addSongActivity -> addSongActivity: add song to front of playlist
        List<AlbumTrack> newSongList = playlist.getSongList();
        if(addSongToPlaylistRequest.isQueueNext()) {
            newSongList.add(0, albumTrack);
        } else {        // else addSongActivity -> addSongActivity: add song to back of playlist
            newSongList.add(albumTrack);
        }
        // addSongActivity -> addSongActivity: update playlist song count
        //Playlist playlist = new Playlist();
        playlist.setSongCount(newSongList.size());
        playlist.setSongList(newSongList);

        // addSongActivity -> playlistDao : savePlaylist(Playlist)
        // playlistDao -> dynamoDB : store Playlist
        playlistDao.savePlaylist(playlist);
        //playlistDao --> addSongActivity : return updated Playlist object
        // Update the Activity to use the ModelConverter to convert the AlbumTrack's to SongModel's as needed
        //loop for each song in playlist
        List<SongModel> songModelList = new ModelConverter().toSongModelList(playlist.getSongList());
        //addSongActivity -> addSongActivity: Create AddSongToPlaylistResult and set SongModel list

        return AddSongToPlaylistResult.builder()
                .withSongList(songModelList)
                .build();
    }
}

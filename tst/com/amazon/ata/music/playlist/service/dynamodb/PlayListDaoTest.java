package com.amazon.ata.music.playlist.service.dynamodb;
import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class PlayListDaoTest {

    private PlaylistDao playlistDao;
    private DynamoDBMapper dynamoDbMapper;
    String playListID = "PPT05";
    

    @BeforeEach
    public void setUp() {
        dynamoDbMapper = new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_EAST_2));
        playlistDao = new PlaylistDao(dynamoDbMapper);
        deleteTestData();
    }

    @AfterEach
    private void teardown() {
        deleteTestData();
    }

    @Test
    public void savePlaylist_newPlayList_returnsAsExpected() throws AlbumTrackNotFoundException {
        String playlistId = "PPT05";
        String playListName = "PPT05 playlist";
        String customerId = "4";
        Integer songCount = 4;
        AlbumTrackDao albumTrackDao = new AlbumTrackDao(dynamoDbMapper);
        Set<String> tags = new HashSet<>(Arrays.asList("PPT05 tags"));
        List<AlbumTrack> songList = new ArrayList<>();
        songList.add(albumTrackDao.getAlbumTrack("B07NJ3H27X", 1));

        Playlist playlist = new Playlist();
        playlist.setId(playListID);
        playlist.setName(playListName);
        playlist.setCustomerId(customerId);
        playlist.setSongCount(songCount);
        playlist.setTags(tags);
        playlist.setSongList(songList);

        playlistDao.savePlaylist(playlist);

        Playlist playlist1 = playlistDao.getPlaylist(playListID);

        assertEquals(playlist.getCustomerId(), playlist1.getCustomerId(), "Expect getting playlist after " +
                "saving it to return same playlist values");
        assertEquals(playlist.getName(), playlist1.getName(), "Expect getting playlist after saving it to " +
                "return same playlist values");
        assertEquals(playlist.getId(), playlist1.getId(), "Expect getting playlist after saving it to " +
                "return same playlist values");
        assertEquals(playlist.getSongCount(), playlist1.getSongCount(), "Expect getting playlist after " +
                "saving it to return same playlist values");
        assertEquals(playlist.getTags(), playlist1.getTags(), "Expect getting playlist after " +
                "saving it to return same playlist values");

    }

    @Test
    public void savePlaylist_existingPlayList_updatesExistingRecordWithNewValues() {
        String playListID = "PPT05";
        String playListName = "PPT05 playlist";
        String customerId = "4";
        Integer songCount = 4;
        Set<String> tags = new HashSet<>(Arrays.asList("PPT05 tags", "more Tags", "Some More Tags"));

        Playlist playlist = new Playlist();
        playlist.setId(playListID);
        playlist.setName(playListName);
        playlist.setCustomerId(customerId);
        playlist.setSongCount(songCount);
        playlist.setTags(tags);

        playlistDao.savePlaylist(playlist);

        String updatedName = "Updated PPT05 playlist";

        playlist.setName(updatedName);

        playlistDao.savePlaylist(playlist);

        assertEquals(playlist.getName(), updatedName, "Expect the name of the PlayList to be " +
                "updated with the new Values");
    }

    @Test
    public void savePlaylist_withNonExistentPlaylistId_throwsPlaylistNotFoundException() {
        String invalidPlayListID = "ABCDEF";

        assertThrows(PlaylistNotFoundException.class, () -> playlistDao.getPlaylist(invalidPlayListID));

    }

    private void deleteTestData() {
        Playlist playList = new Playlist();
        playList.setId(playListID);
        dynamoDbMapper.delete(playList);
    }
}








package com.amazon.ata.music.playlist.service.dynamodb;
import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PlayListDaoTest {

    private PlaylistDao playlistDao;
    private DynamoDBMapper dynamoDbMapper;
    String playListID = "PPT05";
    

    @BeforeEach
    public void setUp() {
        dynamoDbMapper = new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_EAST_2));
        playlistDao = new PlaylistDao(dynamoDbMapper);
        //deleteTestData();
    }

//    @AfterEach
//    private void teardown() {
//        deleteTestData();
//    }


    @Test
    public void getPlaylist_thatExists_returnsExpected() {
        String playListID = "PPT04";
        String playListName = "PPT04 playlist";
        String customerId = "1";
        int songCount = 1;

        Playlist playlist1 = playlistDao.getPlaylist(playListID);
        

        assertEquals(customerId, playlist1.getCustomerId(), "Expect getting playlist after saving it " +
                "to return same playlist values");
        assertEquals(playListName, playlist1.getName(), "Expect getting playlist after saving it to " +
                "return same playlist values");
        assertEquals(playListID, playlist1.getId(), "Expect getting playlist after saving it to " +
                "return same playlist values");
        assertEquals(songCount, playlist1.getSongCount(), "Expect getting playlist after saving it to " +
                "return same playlist values");
    }

    @Test
    public void savePlaylist_newPlayList_returnsAsExpected() {
        String playlistId = "PPT05";
        String playListName = "PPT05 playlist";
        String customerId = "4";
        Integer songCount = 4;
        Set<String> tags = new HashSet<>(Arrays.asList("PPT05 tags"));
        //List<String> songList = new ArrayList<>(Arrays.asList("whereImFrom", "wahoo", "All Mirrors"));

        Playlist playlist = new Playlist();
        playlist.setId(playListID);
        playlist.setName(playListName);
        playlist.setCustomerId(customerId);
        playlist.setSongCount(songCount);
        playlist.setTags(tags);

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
        String playListName = "New PPT05 playlist";
        String customerId = "4";
        Integer songCount = 4;
        Set<String> tags = new HashSet<>(Arrays.asList("PPT05 tags", "more Tags", "Some More Tags"));

        Playlist playlist = new Playlist();
        playlist.setId(playListID);
        playlist.setName(playListName);
        playlist.setCustomerId(customerId);
        playlist.setSongCount(songCount);
        playlist.setTags(tags);

        System.out.println("Playlist : " + playlist.getName());
        playlistDao.savePlaylist(playlist);

        Playlist playlist1 = playlistDao.getPlaylist(playListID);
        System.out.println("DB PlayList : " + playlist1.getName());

        assertEquals(playlist.getName(), playlist1.getName(), "Expect the name of the PlayList to be " +
                "updated with the new Values");
    }

    private void deleteTestData() {
        Playlist playList = new Playlist();
        playList.setId(playListID);
        dynamoDbMapper.delete(playList);
    }
}








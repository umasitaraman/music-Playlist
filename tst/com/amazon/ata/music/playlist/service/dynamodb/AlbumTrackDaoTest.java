package com.amazon.ata.music.playlist.service.dynamodb;
import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlbumTrackDaoTest {
    private AlbumTrackDao albumTrackDao;
    private DynamoDBMapper dynamoDbMapper;

    @BeforeEach
    public void setUp() {
        dynamoDbMapper = new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_EAST_2));
        albumTrackDao = new AlbumTrackDao(dynamoDbMapper);
        //deleteTestData();
    }

    @Test
    public void getPlaylist_thatExists_returnsExpected() throws AlbumTrackNotFoundException {
        String asin = "B07NJ3H27X";
        Integer trackNumber = 1;
        String song_title = "Cuz I Love You";
        String album_name = "Cuz I Love You";

        AlbumTrack albumTrack = albumTrackDao.getAlbumTrack(asin, trackNumber);

        assertEquals(asin, albumTrack.getAsin(), "Expected the asin from DynamoDB " +
                "to match the given asin");
        assertEquals(trackNumber, albumTrack.getTrackNumber(), "Expected the track number from DynamoDB " +
                "to match the given track number");
        assertEquals(song_title, albumTrack.getSongTitle(), "Expected the song title from DynamoDB " +
                        "to match the given song title");
        assertEquals(album_name, albumTrack.getAlbumName(), "Expected the album name from DynamoDB " +
                "to match the given album name");
    }
}

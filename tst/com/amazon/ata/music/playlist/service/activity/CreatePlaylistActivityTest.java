package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.models.requests.CreatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.CreatePlaylistResult;
import com.google.common.collect.Lists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

public class CreatePlaylistActivityTest {
    @Mock
    private PlaylistDao playlistDao;

    private CreatePlaylistActivity createPlaylistActivity;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        createPlaylistActivity = new CreatePlaylistActivity(playlistDao);
    }

    @Test
    public void handleRequest_withProperCustomerIDAndName_createsPlaylist() {
        // GIVEN
        String expectedId = "expectedId";
        String expectedName = "expectedName";
        String expectedCustomerId = "expectedCustomerId";
        int expectedSongCount = 0;
        List<String> expectedTags = Lists.newArrayList("tag");

        CreatePlaylistRequest request = CreatePlaylistRequest.builder()
                .withCustomerId(expectedCustomerId)
                .withName(expectedName)
                .withTags(expectedTags)
                .build();

        // WHEN
        CreatePlaylistResult result = createPlaylistActivity.handleRequest(request, null);


        // THEN
        assertEquals(expectedName, result.getPlaylist().getName());
        assertEquals(expectedCustomerId, result.getPlaylist().getCustomerId());
        assertEquals(expectedTags, result.getPlaylist().getTags());
    }

    @Test
    public void handleRequest_withIncorrectName_createsPlaylist() {
        // GIVEN
        String expectedName = "expectedName's";
        String expectedCustomerId = "expectedCustomerId";

        List<String> expectedTags = Lists.newArrayList("tag");

        CreatePlaylistRequest request = CreatePlaylistRequest.builder()
                .withCustomerId(expectedCustomerId)
                .withName(expectedName)
                .withTags(expectedTags)
                .build();


        // THEN
        assertThrows(InvalidAttributeValueException.class, () -> createPlaylistActivity.handleRequest(request, null));
    }

    @Test
    public void handleRequest_withIncorrectCustomerId_createsPlaylist() {
        // GIVEN
        String expectedName = "expectedName";
        String expectedCustomerId = "expectedCustomerId's";

        List<String> expectedTags = Lists.newArrayList("tag");

        CreatePlaylistRequest request = CreatePlaylistRequest.builder()
                .withCustomerId(expectedCustomerId)
                .withName(expectedName)
                .withTags(expectedTags)
                .build();


        // THEN
        assertThrows(InvalidAttributeValueException.class, () -> createPlaylistActivity.handleRequest(request, null));
    }
}
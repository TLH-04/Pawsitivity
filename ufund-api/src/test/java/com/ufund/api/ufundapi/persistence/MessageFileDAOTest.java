package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.chat.Message;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

/**
 * Test the Message DAO
 * 
 * @author Meghan
 */
public class MessageFileDAOTest {

    private MessageFileDAO messageFileDAO;
    private Message[] testMessages;
    private ObjectMapper mockObjectMapper;

    @BeforeEach
    public void setUpMessageFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        testMessages = new Message[3];
        testMessages[0] = new Message(0, "abc", "this is a message", Instant.now());
        testMessages[1] = new Message(1, "bob", "buisiness buisiness", Instant.now());
        testMessages[2] = new Message(2, "abc", "lame", Instant.now());

        when(mockObjectMapper
                .readValue(new File("fake_file.txt"), Message[].class))
                .thenReturn(testMessages);

        messageFileDAO = new MessageFileDAO("fake_file.txt", mockObjectMapper);
    }

    @Test
    public void testAppend() {
        Message appended = assertDoesNotThrow(() -> messageFileDAO.append("bob", "lock in"),
                "Unexpected excetion thrown");
        Message latest = assertDoesNotThrow(() -> messageFileDAO.listLatest(1).get(0), "Unexpected excetion thrown");
        assertEquals(appended, latest);
    }

    @Test
    public void testListLatest() {
        List<Message> result = assertDoesNotThrow(() -> messageFileDAO.listLatest(50), "Unexpected excetion thrown");
        assertEquals(Arrays.asList(testMessages), result);
    }

    @Test
    public void testGetCurrentMaxId() {
        long max = messageFileDAO.getCurrentMaxId();
        assertEquals(2, max);
    }
}

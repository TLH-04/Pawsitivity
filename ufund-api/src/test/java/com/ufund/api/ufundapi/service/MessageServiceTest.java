package com.ufund.api.ufundapi.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ufund.api.ufundapi.model.chat.Message;
import com.ufund.api.ufundapi.persistence.MessageDAO;

/**
 * Test the Message Service
 * 
 * @author Meghan Tomback
 */
public class MessageServiceTest {

    private MessageDAO messageDAO;
    private MessageService messageService;

    @BeforeEach
    public void setUpMessageService(){
        messageDAO = mock(MessageDAO.class);
        messageService = new MessageService(messageDAO);
    }

    @Test
    public void testPostHappy(){
        Message message = new Message(0, "abc", "cool", Instant.now());
        assertDoesNotThrow(() -> when(messageDAO.append("abc", "cool")).thenReturn(message), "IO Error");
        Message returned = messageService.post("abc", "cool");
        assertEquals(message, returned);
    }

    @Test
    public void testPostSad(){
        assertDoesNotThrow(() -> when(messageDAO.append("abc", "cool")).thenThrow(new IOException()), "IO Error");
        assertThrows(RuntimeException.class, () -> messageService.post("abc", "cool"));
    }

    @Test
    public void testLatestHappy(){
        List<Message> messages = new ArrayList<>();
        assertDoesNotThrow(() -> when(messageDAO.listLatest(50)).thenReturn(messages), "IO Error");
        List<Message> returned = messageService.latest(50);
        assertEquals(messages, returned);
    }

    @Test
    public void testLatestSad(){
        assertDoesNotThrow(() -> when(messageDAO.listLatest(50)).thenThrow(new IOException()), "IO Error");
        assertThrows(RuntimeException.class, () -> messageService.latest(50));
    }
}

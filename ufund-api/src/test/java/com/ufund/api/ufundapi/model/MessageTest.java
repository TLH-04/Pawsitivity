package com.ufund.api.ufundapi.model;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.ufund.api.ufundapi.model.chat.Message;


/**
 * Test the Message Model Class
 * @author Meghan
 */
public class MessageTest {
    
    @Test
    public void TestCtor(){
        Instant testing = Instant.now();
        long id = 1;
        String senderId = "abc";
        String content = "words words words";
        Message testMessage = new Message(id, senderId, content, testing);

        assertEquals(id, testMessage.getId());
        assertEquals(senderId, testMessage.getSenderId());
        assertEquals(content, testMessage.getContent());
        assertEquals(testing, testMessage.getCreatedAt());
        assertEquals(testMessage.toString(), testMessage.toString());
    }
}

package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.chat.Message;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.service.AuthService;
import com.ufund.api.ufundapi.service.MessageService;

/**
 * Test MessageController class
 * 
 * @author Meghan
 * 
 */
public class MessageControllerTest {
    
    private MessageController messageController;
    private MessageService messageService;
    private AuthService authService;

    @BeforeEach
    public void setupMessageController() {
        messageService = mock(MessageService.class);
        authService = mock(AuthService.class);
        messageController = new MessageController(messageService, authService);
    }

    @Test
    public void testPostSuccess(){
        Message response = mock(Message.class);
        Helper tester = new Helper("meghan", "whatever");
        when(authService.getLoggedUser("123")).thenReturn(tester);
        when(messageService.post("meghan", "this is a new message")).thenReturn(response);
        ResponseEntity<Message> result = messageController.post("this is a new message", "123");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testPostException(){
        Helper tester = new Helper("meghan", "whatever");
        when(authService.getLoggedUser("123")).thenReturn(tester);
        when(messageService.post("meghan", "this is a new message")).thenThrow(new RuntimeException("Failed to append message"));
        ResponseEntity<Message> result = messageController.post("this is a new message", "123");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void testGetAfterSuccess(){
        List<Message> messages = new ArrayList<>(50);
        when(messageService.latest(50)).thenReturn(messages);
        ResponseEntity<List<Message>> result = messageController.getAfter(50);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(messages, result.getBody());
    }

    @Test
    public void testGetAfterException(){
        when(messageService.latest(50)).thenThrow(new RuntimeException("Failed to read messages"));
        ResponseEntity<List<Message>> result = messageController.getAfter(50);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}

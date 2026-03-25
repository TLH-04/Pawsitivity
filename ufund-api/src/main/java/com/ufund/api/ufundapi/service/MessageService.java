package com.ufund.api.ufundapi.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ufund.api.ufundapi.model.chat.Message;
import com.ufund.api.ufundapi.persistence.MessageDAO;

/**
 * Handles the logic for message-based operations.
 * 
 * {@literal @}Service Spring annotation identifies this class as a REST API
 * method handler to the Spring framework
 * 
 * @author Anthony Lansing
 */
@Service
public class MessageService {
    
    private final MessageDAO dao;

    /**
     * Creates a service to respond to requests.
     * 
     * @param dao The {@link MessageDAO} to perform CRUD operations.
     */
    public MessageService(MessageDAO dao) {
        this.dao = dao;
    }

    /**
     * Takes a message and saves it.
     * 
     * @param senderId The creator of the message.
     * @param content The message content
     * @return the {@link Message message} created
     */
    public Message post(String senderId, String content) {

        try {
            return dao.append(senderId, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to append message", e);
        }

    }

    /**
     * Return a list of the most recent messages from the backlog.
     * 
     * @param limit the number of messages to return.
     * @return A list of {@link Message messages} from the backlog.
     */
    public List<Message> latest(int limit) {
        try {
            return dao.listLatest(limit);
        } catch (IOException e){
            throw new RuntimeException("Failed to read messages", e);
        }
        
    }
}
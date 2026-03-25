package com.ufund.api.ufundapi.persistence;

import java.io.IOException;
import java.util.List;

import com.ufund.api.ufundapi.model.chat.Message;

/**
 * Defines the interface for {@link Message} object persistance.
 * 
 * @author Anthony Lansing
 */
public interface MessageDAO {

    /**
     * Saves a {@link Message message} to the bottom of the backlog and local storage.
     * 
     * @param senderId the identity of the user creating the message.
     * @param content the body of the message being saved.
     * @return the {@link Message message} that was created.
     * @throws IOException if an issue with storage.
     */
    Message append(String senderId, String content) throws IOException;

    /**
     * Return a List of {@link Message messages} based on a given amount to be returned.
     * @param limit the number of most recent messages to be returned.
     * @return the List of {@link Message messages} retrieved from backlog.
     * @throws IOException if an issue with storage.
     */
    List<Message> listLatest(int limit) throws IOException;

    /**
     * Gets the maximum id value from the total {@link Message messages} already created. 
     * 
     * @return the maximum id value.
     */
    long getCurrentMaxId();
}
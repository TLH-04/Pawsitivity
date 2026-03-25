package com.ufund.api.ufundapi.model.chat;

import java.time.Instant;

/**
 * Representation of a Message the elements of a message needed to keep track
 * of who wrote the message and when.
 * 
 * @author Anthony Lansing
 */
public class Message {
    
    /**
     * The ID of of the message
     */
    private long id;

    /**
     * Who sent the message
     */
    private String senderId;

    /**
     * The message content.
     */
    private String content;

    /**
     * The instant in time the message was created.
     */
    private String createdAt;

    /**
     * Creates a new message.
     * 
     * @param id the id of the message
     * @param senderId who sent the message
     * @param content what the message states
     * @param createdAt when the message was created
     */
    public Message(long id, String senderId, String content, Instant createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt.toString();
    }

    /**
     * Return this message's id.
     * 
     * @return this message's id number
     */
    public long getId() {
        return this.id;
    }

    /**
     * Return this message's creator.
     * 
     * @return this message's sender id
     */
    public String getSenderId() {
        return this.senderId;
    }

    /**
     * Return the content in this message
     * 
     * @return this message's message
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Return the Instant object of this message
     * 
     * @return the Instant this message was created.
     */
    public String getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public String toString(){
        return senderId + " " + content + " " + createdAt;
    }

}
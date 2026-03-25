package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ufund.api.ufundapi.model.chat.Message;

/**
 * Implements the functionality for JSON file-based persistence for {@link Message messages}
 * 
 * {@literal @}Component Spring annotation instantiates a single instance of this
 * class and injects the isntance into other classes as needed
 * 
 * @author Anthony Lansing
 */
@Component
public class MessageFileDAO implements MessageDAO {
    
    private final String file;
    private final ObjectMapper objectMapper;
    private final AtomicLong idSeq = new AtomicLong(0);
    private final Deque<Message> tail = new ArrayDeque<>();
    private final ArrayList<Message> backlog;
    private final int maxTail = 5000;

    /**
     * Creates a Message File Data Access Object.
     * 
     * @param filename Filename to read/write
     * @param objectMapper provides JSON Object to/from Java Object
     * @throws IOException when the file cannot be accessed
     */
    public MessageFileDAO(@Value("${messages.file:./data/messages.json}") String filename, ObjectMapper objectMapper) throws IOException {
        this.file = filename;
        this.objectMapper = objectMapper;

        /*
         * The backlog holds all messages that have been created. This makes it possible to retrieve
         * older messages if necessary.
         */
        this.backlog = new ArrayList<>();

        /*
         * Make the printing of the JSON files readable.
         */
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.withObjectIndenter(new DefaultIndenter("  ", DefaultIndenter.SYS_LF)); 
        printer.withArrayIndenter(new DefaultPrettyPrinter.NopIndenter()); 
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.writer(printer);

        load();
    }

    /*
     * Loads in the entire backlog. Also prepares a smaller local list for quicker access.
     * Possible to throw and exception if issues with the file.
     */
    private void load() throws IOException {
        synchronized(this.tail) {
            // take in the entire backlog to go through
            Message[] messages = objectMapper.readValue(new File(this.file), Message[].class);
            for (Message msg : messages) {
                idSeq.updateAndGet(prev -> Math.max(prev, msg.getId())); // keep track of IDs for the creation of new messages
                this.backlog.addLast(msg);

                /*
                 * The tail is the local storage and is limited.
                 */
                if (this.tail.size() >= this.maxTail)
                    this.tail.removeFirst();
                this.tail.addLast(msg);
            }
        }
    }

    /*
     * Take the current state of the backlog and save to the JSON.
     * Possible to throw exception if issues with the file.
     */
    private void save() throws IOException {
        synchronized(this.backlog) {
            Message[] backLogToJSON = new Message[this.backlog.size()];
            this.backlog.toArray(backLogToJSON);
            objectMapper.writeValue(new File(file), backLogToJSON);
        }
    }

    /**
     * Saves a {@link Message message} to the bottom of the backlog and local storage.
     * 
     * @param senderId the identity of the user creating the message.
     * @param content the body of the message being saved.
     * @return the {@link Message message} that was created.
     * @throws IOException if an issue with storage.
     */
    @Override
    public synchronized Message append(String senderId, String content) throws IOException {
        synchronized(this.backlog) {
            long id = idSeq.incrementAndGet();
            Message msg = new Message(id, senderId, content, Instant.now());
            this.backlog.addLast(msg); // keep the backlog updated.
            save();

            /*
             * Also save the message to the smaller storage.
             */
            synchronized(tail) {
                if (tail.size() >= maxTail) tail.removeFirst();
                tail.addLast(msg);
            }

            return msg;
        }
    }

    /**
     * Return a List of {@link Message messages} based on a given amount to be returned.
     * @param limit the number of most recent messages to be returned.
     * @return the List of {@link Message messages} retrieved from backlog.
     * @throws IOException if an issue with storage.
     */
    @Override
    public synchronized List<Message> listLatest(int limit) throws IOException {
        
        Deque<Message> out = new ArrayDeque<>(limit);
        for (Message msg : this.backlog) {
            if (out.size() >= limit)
                out.removeFirst();
            out.addLast(msg);
        }
    
        return out.stream().sorted(Comparator.comparingLong(Message::getId)).toList();
    }

    /**
     * Gets the maximum id value from the total {@link Message messages} already created. 
     * 
     * @return the maximum id value.
     */
    @Override
    public long getCurrentMaxId() {
        OptionalLong maxInTail = tail.stream().mapToLong(Message::getId).max();
        return maxInTail.orElse(idSeq.get());
    }
}
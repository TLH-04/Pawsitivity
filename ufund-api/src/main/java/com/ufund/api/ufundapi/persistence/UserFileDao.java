package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ufund.api.ufundapi.model.user.SuperAdmin;
import com.ufund.api.ufundapi.model.user.User;

/**
 * Implements the functionality for JSON file-based persistence for Users
 * 
 * {@literal @}Component Spring annotation instantiates a single instance of this
 * class and injects the isntance into other classes as needed
 * 
 * @author Team 2
 */
@Component
public class UserFileDao implements UserDao {
    private final String filename;
    private final ObjectMapper objectMapper;
    private final Map<String,User> users;
    private final static SuperAdmin ADMIN = new SuperAdmin("SuperCool");

    /**
     * Creates a User File Data Access Object
     * @param filename Filename to read and write from
     * @param objectMapper Provides json strings for classes
     * @throws IOException of storage issues
     */
    public UserFileDao(@Value("${users.file}") String filename, ObjectMapper objectMapper) throws IOException{
        this.filename = filename;
        this.objectMapper = objectMapper;
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.withObjectIndenter(new DefaultIndenter("  ", DefaultIndenter.SYS_LF)); 
        printer.withArrayIndenter(new DefaultPrettyPrinter.NopIndenter()); 
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.writer(printer);
        users = new TreeMap<>();
        load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUser(User user) throws IOException{
        synchronized(users){
            if (users.containsKey(user.getUsername())){
                users.remove(user.getUsername());
                users.put(user.getUsername(),user);
                save();
            }
        }
    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUser(User user) throws IOException{
        synchronized(users){
            users.put(user.getUsername(),user);
            save();
        }
    }

    /**
     * loads data file
     * 
    */
    private void load() throws IOException {
        synchronized(users){
            User[] UserArray = objectMapper.readValue(new File(filename),User[].class);
    
            for (User user : UserArray){
                users.put(user.getUsername(), user);
            }

            if (!users.containsValue(ADMIN)){
                users.put(ADMIN.getUsername(), ADMIN);
                save();
            }
    
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public User[] getUsers() throws IOException {
        synchronized(users){
            ArrayList<User> list = new ArrayList<>();
            for (User user : users.values()){
                list.add(user);
            }
            User[] array = new User[list.size()];
            list.toArray(array);
            return array;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(String username) throws IOException {
        synchronized(users){
            if (users.containsKey(username)){
                return users.get(username);
            }
            return null;
        }
    }


    /**
     * save Users into userdata file
     * @return if save was successful
    */
    private boolean save() throws IOException {
        synchronized(users){
            User[] userArray = getUsers();
            objectMapper.writeValue(new File(filename),userArray);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUser(String username) throws IOException {
        synchronized(users){
            return users.containsKey(username);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCreateUser(String username) throws IOException {
        synchronized(users){
            Set<String> keys = users.keySet();
            keys = keys.stream().map(String::toLowerCase).collect(Collectors.toSet());
            
           
            if (!keys.contains(username.toLowerCase())){
                return true;
            }
        }
        return false;
    }
}

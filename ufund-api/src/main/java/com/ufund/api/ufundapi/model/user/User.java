package com.ufund.api.ufundapi.model.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Handles abstract operations for a user 
 * 
 * @author Jason Ugbaja
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "u-type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Admin.class, name = "Admin"),
    @JsonSubTypes.Type(value = Helper.class, name = "Helper"),
    @JsonSubTypes.Type(value = SuperAdmin.class, name = "SuperAdmin")
})
public abstract class User {
    @JsonProperty("type") final UserType type;
    @JsonProperty("name") final String username;
    @JsonProperty("password") final String password;
    @JsonProperty("hashed") final Boolean hashed;
     /**
     *  Creates a new user
     *  @param username the username of user
     *  @param type the type of user
     *  @param password the password of the user
     */
    public User(String username, String password, UserType type){
        this.username = username;
        this.type = type;
        this.password = createHashedPassword(username, password);
        this.hashed = true;
    }


    /**
     *  Creates a new user via a Json String
     *  @param username the username of user
     *  @param type the type of user
     *  @param password the password of the user
     *  @param hashed the status if the password is hashed
     */
    @JsonCreator
    public User(@JsonProperty("name") String username, @JsonProperty("password") String password, @JsonProperty("type") UserType type, @JsonProperty("hashed") Boolean hashed){
        this.username = username;
        this.type = type;
        this.password = Boolean.TRUE.equals(hashed) ? password : createHashedPassword(username, password);
        this.hashed = hashed;
    }


    /**
     * Retrieves the type of user
     * @return the type of user
     */
    public UserType getType() {
        return type;
    }

     /**
     * Retrieves the username
     * @return the username
     */
    public String getUsername() {
        return username;
    }
     /**
     * Retrieves the Password
     * @return the Password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Turns the user object into a string
     * @return String of users
     */
    @Override
    public String toString() {
        return "Username: " + this.username + " Type: " + this.type + " Password: " + this.password;
    }

    
    /**
     * Turns the user into a unique hashcode for operations
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Compares to user objects to see if they equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof User){
            User otherUser = (User) obj;
            return this.password.equals(otherUser.password) && this.username.equals(otherUser.username);
        }

        return false;
    }

    /**
     * Creates a hashed password with salting in order to differenctate passwords
     * @param username username used for salting
     * @param password pasword being saved
     * @return Hashed password
     */
    public static String createHashedPassword(String username, String password) {
        try {
            byte[] salt = (username.hashCode() + "").getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(salt);
            byte[] hashed = messageDigest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            
        }
        
        return password;
    }



}

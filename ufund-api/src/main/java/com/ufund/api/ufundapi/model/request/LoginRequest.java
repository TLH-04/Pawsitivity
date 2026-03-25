package com.ufund.api.ufundapi.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Json Body Class for Login Requests
 * 
 * Used for CRUD operations
 * 
 * @author Jason Ugbaja
 */
public class LoginRequest {
    @JsonProperty("username") String username; 
    @JsonProperty("password") String password; 

    /**
     * The Creation of a LoginRequest Request
     * 
     * @param username the user name in the request
     * @param password the password in the request
     */
    public LoginRequest(@JsonProperty("username") String username,@JsonProperty("password") String password){
        this.username = username;
        this.password = password;
    }

    /**
     * The password in the request
     * @return the password in the request body
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * the username in the request body 
     * @return the username in the request
     */
    public String getUsername() {
        return this.username;
    }

}

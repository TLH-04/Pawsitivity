package com.ufund.api.ufundapi.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents a Admin User
 * 
 * @author Jason Ugbaja
 */
public class Admin extends User {

    /**
     * creates a new Admin user
     * @param username the username of the admin
     * @param password the password of the admin
     * 
     */
    public Admin(String username, String password) {
        super(username, password, UserType.ADMIN);
    }

    /**
     * creates a new Admin user from a json string
     * @param username the username of the admin
     * @param password the password of the admin
     * @param hashed the status if the password is hashed
     * 
     */
    @JsonCreator
    public Admin(String username, String password, boolean hashed) {
        super(username, password, UserType.ADMIN, hashed);
    }

        
}

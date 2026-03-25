package com.ufund.api.ufundapi.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Represents the SuperAdmin user
 * 
 * @author Destiny Zeng
 */
public class SuperAdmin extends User {
    private final static String SUPERUSER = "Admin";

    /**
     * creates the SuperAdmin user
     * 
     * @param password the password of the admin
     * 
     */
    public SuperAdmin(String password) {
        super(SUPERUSER, password, UserType.SUPER);
    }

    /**
     * creates the SuperAdmin user from a Json String
     * 
     * @param password the password of the admin
     * 
     */
    @JsonCreator
    public SuperAdmin(String password,boolean hashed) {
        super(SUPERUSER, password, UserType.SUPER,hashed);
    }

}
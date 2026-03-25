package com.ufund.api.ufundapi.model.user;

/**
 * ENUM for login result
 */
public enum LoginResult {
    /**
     * Not Logged in enum
     */
    NOTLOGGEDIN("YOU ARE NOT LOGGED IN"),
    /**
     * wrong user enum
     */
    WRONGUSERTYPE("YOU ARE NOT THE RIGHT USER TYPE"),
    /**
     * invalid user enum
     */
    INVALIDUSER("INVALID USERNAME OR PASSWORD"),
    /**
     * Is logged in enum
     */
    ISLOGGEDIN("YOU MUST BE LOGGED OUT TO PREFORM THIS ACTION"),
    /**
     * Success enum
     */
    SUCCESS("SUCCESS");

    String message;

    private LoginResult(String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}

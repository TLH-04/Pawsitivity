package com.ufund.api.ufundapi.persistence;

import java.io.IOException;
import com.ufund.api.ufundapi.model.user.*;


/**
 * Defines the interface for the User Object presistence
 */
public interface UserDao {
    /**
     * Gets all users 
     * 
     * @return a array of users
     * @throws IOException if issues with storage
     */
   User[] getUsers() throws IOException;


    /**
     *
     * Gets the user by username
     *  
     * @param username username of the user you are looking for
     * @return user if found
     * @throws IOException if issues with storage
     */
   User getUser(String username) throws IOException;


     /**
     * Sees if a user with the provided username extists
     * 
     * @param username username of the user you are looking for
     * @return true if user is found
     * @throws IOException if issues with storage
     */
   boolean hasUser(String username) throws IOException;

    /**
    * Sees if a user with the provided username extists, checks for case-sense
    * 
    * @param username username of the user you are looking for
    * @return true if user is found
    * @throws IOException if issues with storage
    */
   boolean canCreateUser(String username) throws IOException;

   /**
    * adds user to data file
    * @param user the new username being created
    * @throws IOException if issues with storage
    */
   void addUser(User user) throws IOException;

   /**
    * update user in local cache
    * @param user the username of the user being updated
    * @throws IOException if issues with storage
    */
   void updateUser(User user) throws IOException;

   
}

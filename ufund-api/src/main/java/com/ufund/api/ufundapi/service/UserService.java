package com.ufund.api.ufundapi.service;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.ufund.api.ufundapi.model.user.Admin;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.User;
import com.ufund.api.ufundapi.persistence.UserDao;

/**
 * Handles the logic for user based operations
 * <p>
 * {@literal @}Service Spring annotation identifies this class as a REST API
 * method handler to the Spring framework
 * 
 * @author Jason Ugbaja
 */
@Service
public class UserService {
    private final UserDao userDao;

    /**
     * Creates a Service to reponds to requests
     * 
     * @param userDao The {@link UserDao} to perform CRUD operations
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public UserService(UserDao userDao){
        this.userDao = userDao;
    }

    /**
     * Creates a helper account and saves it
     * @param username name of helper
     * @param password the password of the new helper
     * @return Status of account creation
     */
    public LoginResult createUser(String username, String password){

        try {
            if (userDao.canCreateUser(username) && !username.isEmpty()){

                this.userDao.addUser(new Helper(username,password));

                return LoginResult.SUCCESS;
            }
            else 
                return LoginResult.INVALIDUSER;
        } catch (Exception e) {

        }

        return LoginResult.INVALIDUSER;
        
    }

    /**
     * Creates a Admin account and saves it
     * @param username name of Admin
     * @param password the password of the new Admin
     * @return Status of account creation
     */
    public LoginResult createAdmin(String username, String password) {
        try {
            if (userDao.canCreateUser(username) && !username.isEmpty()){

                Admin newAdmin = new Admin(username, password);
                this.userDao.addUser(newAdmin);

                return LoginResult.SUCCESS;
            }
            else 
                return LoginResult.INVALIDUSER;
        } catch (IOException e) {

        }
        return LoginResult.INVALIDUSER;
    }

    /**
     * Gets all users from UserFileDao
     * @return all users
     */
    public User[] getAllUsers(){
        try {
            return this.userDao.getUsers();
        } catch (IOException e) {
            return null;
        }
    }

}

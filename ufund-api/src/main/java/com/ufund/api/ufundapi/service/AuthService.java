package com.ufund.api.ufundapi.service;


import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.model.user.LoginResponse;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.User;
import com.ufund.api.ufundapi.model.user.UserType;
import com.ufund.api.ufundapi.model.user.permission.PermissionType;
import com.ufund.api.ufundapi.persistence.UserDao;


/**
 * Used to handle password authentication and login functionally 
 * 
 * @author Jason Ugbaja
 */
@Service
public class AuthService {
    private final int BYTE_SIZE = 64;
    private final UserDao userDao;
    private final static HashMap<String,User> loggedUsers = new HashMap<>();

    private final static HashMap<PermissionType,ArrayList<UserType>> permissions = new HashMap<>();

    static{
        permissions.put(PermissionType.SHARED, new ArrayList<>(List.of(UserType.ADMIN,UserType.HELPER,UserType.SUPER) ) );
        permissions.put(PermissionType.HELPER, new ArrayList<>(List.of(UserType.HELPER)));
        permissions.put(PermissionType.ADMIN, new ArrayList<>(List.of(UserType.ADMIN,UserType.SUPER)));
        permissions.put(PermissionType.SUPER, new ArrayList<>(List.of(UserType.SUPER)));
    }

   /**
     * Creates a Service to reponds to requests
     * 
     * @param userDao The {@link UserDao} to perform CRUD operations
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    
    /**
     * verify a sessionId based on permission type
     * @param sessionId the username
     * @param permissionType the perrmission type the function is checking for
     * @return Login Result of the given user
     */
    public LoginResult verifyUser(String sessionId,PermissionType permissionType) {
        if (!this.isLoggedIn(sessionId)) {
            return LoginResult.NOTLOGGEDIN;
        }
        if (!permissions.get(permissionType).contains(this.getCurrentType(sessionId))){
            return LoginResult.WRONGUSERTYPE;
        }
        return LoginResult.SUCCESS;
    }


    /**
     * Attempts to login with a valid string
     * @param loginRequest the login request from the  client
     * @return Status of login
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            String user = loginRequest.getUsername();
            String hashedpassword = User.createHashedPassword(user, loginRequest.getPassword());

            if (user == null || user.equals("") || !userDao.hasUser(user) || !userDao.getUser(user).getPassword().equals(hashedpassword) ){
                return this.generateLoginResponse(LoginResult.INVALIDUSER);
            } 

        

            if (loggedUsers.values().contains(new Helper(user, loginRequest.getPassword()))){
                return this.generateLoginResponse(LoginResult.ISLOGGEDIN);
            } else {
                String sessionId = this.createSessionId();
                User loggedUser = userDao.getUser(user);
                synchronized (loggedUsers){
                    loggedUsers.put(sessionId, loggedUser);
                }
                return this.generateLoginResponse(LoginResult.SUCCESS,sessionId);
            }
        } catch (Exception e) {
            
        }
        return this.generateLoginResponse(LoginResult.INVALIDUSER);
    }


    /**
     * Generates a login response including the session id
     * @param loginResult the login result of an operation
     * @param sessionId the sessionid in the response
     * @return A generated LoginResponse
     */
    public LoginResponse generateLoginResponse(LoginResult loginResult,String sessionId){
        return new LoginResponse() {

            @Override
            public LoginResult getLoginResult() {
                return loginResult;
            }

            @Override
            public String getSessionId() {
                return sessionId;
            }
            
        };
    }

    /**
     * Generates a login response with a null id
     * @param loginResult the login result of an operation
     * @return A generated LoginResponse
     */
    public LoginResponse generateLoginResponse(LoginResult loginResult){
        return new LoginResponse() {

            @Override
            public LoginResult getLoginResult() {
                return loginResult;
            }

            @Override
            public String getSessionId() {
                return null;
            }
            
        };
    }

    /**
     * checks if there is a current login
     * @param sessionId the session id of the request
     * @return if you are currently logged in
     */
    public boolean isLoggedIn(String sessionId){
        return loggedUsers.containsKey(sessionId);
    }

    /**
     * Gets the logged in user if there is a current user
     * @param sessionId the session id of the request
     * @return the logged in user
     */
    public User getLoggedUser(String sessionId){
        if (isLoggedIn(sessionId)){
            return loggedUsers.get(sessionId);
        }
        return null;
    }

    /**
     * Logs out
     * @param sessionId the sessionid of the person logging out
     * @return successful logout
     */
    public LoginResult logout(String sessionId) {
        if (!isLoggedIn(sessionId)){
            return LoginResult.NOTLOGGEDIN;
        }
        synchronized (loggedUsers) {
            loggedUsers.remove(sessionId);
        }
        return LoginResult.SUCCESS;
    }

    /**
     * gets the current type of user logged in
     * @param sessionId the username
     * @return usertype of user logged in
     */
    public UserType getCurrentType(String sessionId){
        return this.getLoggedUser(sessionId).getType();
    }

    /**
     * Updates the user stored data based on the provided session id
     * @param sessionId username 
     */
    public void updateUser(String sessionId){
        if (this.isLoggedIn(sessionId)){
            try {
                User logged = this.getLoggedUser(sessionId);
                this.userDao.updateUser(logged);
            } catch (IOException e) {

            }
        }
    }

    /**
     * Creates a session id for a user login
     * @return session id
     * @throws Exception on Session id generation failure
     */
    public String createSessionId() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[BYTE_SIZE] ;
        secureRandom.nextBytes(randomBytes);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] hashed = messageDigest.digest(randomBytes);
        return Base64.getEncoder().encodeToString(hashed);
    }


}

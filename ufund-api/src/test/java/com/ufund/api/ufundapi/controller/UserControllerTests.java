package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.Admin;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.SuperAdmin;
import com.ufund.api.ufundapi.model.user.User;
import com.ufund.api.ufundapi.service.UserService;

/**
 * The unit tests for the user services
 * ie. create account, log in, log out
 * 
 * @author Meghan Tomback
 */

public class UserControllerTests {

    private UserController userController;
    private UserService userService;

    @BeforeEach
    public void setupUserController() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void testCreateHelper(){
        when(userService.createUser("meghantomb","123")).thenReturn(LoginResult.SUCCESS);
        LoginRequest loginRequest = new LoginRequest("meghantomb", "123");
        ResponseEntity<String> response = userController.createHelper(loginRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("meghantomb", response.getBody());
    }

    @Test
    public void testCreateHelperAlreadyExist(){
        LoginRequest loginRequest = new LoginRequest("meghantomb", "123");
        when(userService.createUser("meghantomb","123")).thenReturn(LoginResult.SUCCESS).thenReturn(LoginResult.INVALIDUSER);

        userController.createHelper(loginRequest);
        ResponseEntity<String> response = userController.createHelper(loginRequest);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("meghantomb", response.getBody());
    }

    @Test
    public void testCreateAdmin(){
        when(userService.createAdmin("meghantomb","123")).thenReturn(LoginResult.SUCCESS);
        LoginRequest loginRequest = new LoginRequest("meghantomb", "123");
        ResponseEntity<String> response = userController.createAdmin(loginRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("meghantomb", response.getBody());
    }

    @Test
    public void testCreateAdminAlreadyExist(){
        LoginRequest loginRequest = new LoginRequest("meghantomb", "123");
        when(userService.createAdmin("meghantomb","123")).thenReturn(LoginResult.SUCCESS).thenReturn(LoginResult.INVALIDUSER);

        userController.createAdmin(loginRequest);
        ResponseEntity<String> response = userController.createAdmin(loginRequest);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("meghantomb", response.getBody());
    }

    @Test
    public void testGetAllUsers(){
        User[] testUsers = new User[1];
        testUsers[0] = new Admin("f","123");
   

        when(userService.getAllUsers()).thenReturn(testUsers);

       
        ResponseEntity<User[]> response = userController.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User[] result = response.getBody();

        for (int i = 0; i < testUsers.length;++i)
        {
            assertEquals(result[i],testUsers[i]);
        }
    }


}

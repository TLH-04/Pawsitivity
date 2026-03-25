package com.ufund.api.ufundapi.service;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ufund.api.ufundapi.model.user.Admin;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.SuperAdmin;
import com.ufund.api.ufundapi.model.user.User;
import com.ufund.api.ufundapi.persistence.UserDao;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserDao userDAO;

    private UserService userService;


    @BeforeEach
    void setUp() {
        userService = new UserService(userDAO); // fresh instance per test
    }

    @AfterEach
    void close() {
        reset(userDAO); // wipe stubbings/interactions so nothing leaks
    }


    /*
     * Testing createUser() functionality.
     */

    // Test by providing an already created user.
    @Test
    void testCreateUserExists() {
        // Setup the mocked behavior when accessing helper
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));

        LoginResult result = userService.createUser("Hotdog", "123");

        assertEquals(LoginResult.INVALIDUSER, result);
    }

    // Test by providing a new user.
    @Test
    void testCreateUserFails() {

        LoginResult result = userService.createUser("Hotdog","123");

        assertEquals(LoginResult.INVALIDUSER, result);
    }

    @Test
    void testCreateUser() {
        assertDoesNotThrow(() -> when(userDAO.canCreateUser("1231245")).thenReturn(true));
        assertDoesNotThrow(() -> when(userDAO.hasUser("1231245")).thenReturn(false));
        LoginResult result = userService.createUser("1231245","123");
        
        assertEquals(LoginResult.SUCCESS, result);
    }

    @Test
    void testCreateExtists() {
        assertDoesNotThrow(() -> when(userDAO.canCreateUser("Hotdog")).thenReturn(false));
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(false));
        LoginResult result = userService.createUser("Hotdog","123");
        LoginResult result2 = userService.createUser("","");
        assertEquals(LoginResult.INVALIDUSER, result);
        assertEquals(LoginResult.INVALIDUSER, result2);
    }

    // Test by providing an empty string.
    @Test
    void testCreateUserEmpty() {

        LoginResult result = userService.createUser("","");

        assertEquals(LoginResult.INVALIDUSER, result);
    }

    @Test
    void testCreateExtistAdmin() {
        // Setup the mocked behavior when accessing helper
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));

        LoginResult result = userService.createAdmin("Hotdog", "123");

        assertEquals(LoginResult.INVALIDUSER, result);
    }

    @Test
    void testCreateNewAdmin() {
       
        assertDoesNotThrow(() -> when(userDAO.hasUser("Steve")).thenReturn(false));
        assertDoesNotThrow(() -> when(userDAO.canCreateUser("Steve")).thenReturn(true));

        LoginResult result = userService.createAdmin("Steve", "123");

        assertEquals(LoginResult.SUCCESS, result);
    }

    @Test
    void testCreateEmptyAdmin() {
       
        assertDoesNotThrow(() -> when(userDAO.hasUser("Steve")).thenReturn(false));

        LoginResult result = userService.createAdmin("", "123");

        assertEquals(LoginResult.INVALIDUSER, result);
    }

    @Test
    void testCreateEmptyHelper() {
       
        assertDoesNotThrow(() -> when(userDAO.hasUser("Steve")).thenReturn(false));
        assertDoesNotThrow(() -> when(userDAO.canCreateUser("Steve")).thenReturn(true));

        LoginResult result = userService.createUser("", "123");

        assertEquals(LoginResult.INVALIDUSER, result);
    }

     @Test
    void testGetAllUsers() {
        User[] testUsers = new User[4];
        testUsers[0] = new Admin("f","123");
        testUsers[1] = new SuperAdmin("SuperCool");
        testUsers[2] = new Helper("alansing","123");
        testUsers[3] = new Helper("hotdog","123");

        assertDoesNotThrow(() -> when(userDAO.getUsers()).thenReturn(testUsers));
        

        User[] result = userService.getAllUsers();

        for (int i = 0; i < testUsers.length;++i)
        {
            assertEquals(result[i],testUsers[i]);
        }
    }



}

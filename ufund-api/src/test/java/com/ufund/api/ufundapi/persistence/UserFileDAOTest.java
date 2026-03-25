package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.user.Admin;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.model.user.SuperAdmin;
import com.ufund.api.ufundapi.model.user.User;

/**
 * Test the User File DAO class
 * 
 * @author Anthony
 */
@ExtendWith(MockitoExtension.class)
public class UserFileDAOTest {
    UserFileDao userFileDAO;
    User[] testUsers;
    ObjectMapper mockObjectMapper;

    @BeforeEach
    public void setUpUserFileDAO() {
        mockObjectMapper = mock(ObjectMapper.class);
        testUsers = new User[4];
        testUsers[0] = new Admin("f","123");
        testUsers[1] = new SuperAdmin("SuperCool");
        testUsers[2] = new Helper("alansing","123");
        testUsers[3] = new Helper("hotdog","123");
    

        assertDoesNotThrow(() -> when(mockObjectMapper
            .readValue(new File("fake.txt"),User[].class))
            .thenReturn(testUsers));

        userFileDAO = assertDoesNotThrow(() -> new UserFileDao("fake.txt", mockObjectMapper));
    }

    /*
     * Test getUsers
     */
    @Test
    public void testGetUsers() {
        User[] users = assertDoesNotThrow(() -> userFileDAO.getUsers());

        List<User> daoUsers = Arrays.asList(users);
        List<User> testsUsers = Arrays.asList(testUsers);

        assertEquals(users.length, testUsers.length);

        for (User u : daoUsers){
            assertTrue(testsUsers.contains(u));
        }

            
    }

    /*
     * Test getUser
     */
    @Test
    public void testGetUser() {
        User user = assertDoesNotThrow(() -> userFileDAO.getUser("hotdog"));

        assertEquals(user, testUsers[3]);
    }
    
    /*
     * Test getUser
     */
    @Test
    public void testGetUserBad() {
        User user = assertDoesNotThrow(() -> userFileDAO.getUser("colddog"));

        assertEquals(null, user);
    }

    /*
     * Series of test cases for hasUser
     */

     // Test hasUser when expected result is true
    @Test
    public void testHasUserTrue() {
        assertEquals(true, assertDoesNotThrow(() -> userFileDAO.hasUser("hotdog")));
    }

    // Test hasUser when expected result is false
    @Test
    public void testHasUserFalse() {
        assertEquals(false, assertDoesNotThrow(() -> userFileDAO.hasUser("tiramisu")));
    }

    /*
     * Test update user by passing a new user with same username but different
     * attributes and check result.
     */
    @Test
    public void testUpdateUser() {
        User newUser = new Helper("hotdog","123");
        assertDoesNotThrow(() -> userFileDAO.updateUser(newUser));

        User updatedUser = assertDoesNotThrow(() -> userFileDAO.getUser("hotdog"));
        assertEquals(newUser.getUsername(), updatedUser.getUsername());
        assertEquals(newUser.getType(), updatedUser.getType());
    }

    /*
     * Test update user by passing a user with same username but different
     * attributes and check result.
     */
    @Test
    public void testUpdateUserNotFound() {
        User newUser = new Helper("colddog","123");
        
        assertDoesNotThrow(() -> userFileDAO.updateUser(newUser));
    }

    @Test
    public void testAddUser() {
        User userToAdd = new Helper("addedUser","123");
        assertDoesNotThrow(() -> userFileDAO.addUser(userToAdd));

        User addedUser = assertDoesNotThrow(() -> userFileDAO.getUser("addedUser"));
        assertEquals(userToAdd.getUsername(), addedUser.getUsername());
        assertEquals(userToAdd.getType(), addedUser.getType());
    }

    @Test
    public void testCanAddUser() {
        boolean result = assertDoesNotThrow(() -> userFileDAO.canCreateUser("Alansing2"));
        boolean result2 = assertDoesNotThrow(() -> userFileDAO.canCreateUser("Alansing"));
        assertTrue(result);
        assertFalse(result2);
    }

  




    /*
     * save() and load() not strictly tested since they are used throughout all other tests.
     */

}

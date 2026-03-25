package com.ufund.api.ufundapi.service;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.model.user.LoginResponse;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.permission.PermissionType;
import com.ufund.api.ufundapi.persistence.UserDao;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    
    @Mock
    private UserDao userDAO;
    
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userDAO); // fresh instance per test
    }

    @AfterEach
    void close() {
        reset(userDAO); // wipe stubbings/interactions so nothing leaks
    }

    /*
     * Testing verifyUser() functionality.
     */

    // Test by not logging in as a user. 
    @Test
    @Order(2)
    void testVerifyNoLogin() {

        LoginResult result = authService.verifyUser("Hotdog", PermissionType.HELPER);

        assertEquals(LoginResult.NOTLOGGEDIN, result);
    }

    // Test with a valid user with the wrong permission type.
    @Test
    @Order(3)
    void testVerifyWrongPerm() {
       
      
        // Setup the mocked behavior when logging in as helper
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));
        assertDoesNotThrow(() -> when(userDAO.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123")));

        LoginResponse response =  authService.login(new LoginRequest("Hotdog", "123"));
        System.out.println(response.getLoginResult());
        System.out.println("BREAK");
        LoginResult result = authService.verifyUser(response.getSessionId(), PermissionType.ADMIN);

        assertEquals(LoginResult.WRONGUSERTYPE, result);

        authService.logout(response.getSessionId());
    }

    // Test with a valid user and valid permission type.
    @Test
    @Order(4)
    void testVerifyUser() {
      

        // Setup the mocked behavior when logging in as helper
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));
        assertDoesNotThrow(() -> when(userDAO.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123")));


        LoginResponse response =  authService.login(new LoginRequest("Hotdog", "123"));

       // when( authService.verifyUser(response.getSessionId(), PermissionType.HELPER) ).thenReturn(LoginResult.SUCCESS);

        
      
        LoginResult result = authService.verifyUser(response.getSessionId(), PermissionType.HELPER);

        assertEquals(LoginResult.SUCCESS, result);
        authService.logout(response.getSessionId());
    }

    /*
    * Testing login() functionality.
    */

    // Test by not providing a user.
    @Test
    void testLoginNoUser() {
        LoginResponse result = authService.login(null);
        assertEquals(LoginResult.INVALIDUSER, result.getLoginResult());
    }

    // Test by providing an empty string.
    @Test
    void testLoginEmptyString() {
        LoginResponse result = authService.login(new LoginRequest("", ""));
        assertEquals(LoginResult.INVALIDUSER, result.getLoginResult());
    }

    // Test by providing a user not in the system.
    @Test
    void testLoginWrongUser() {
        // Setup the mocked behavior when logging in as unknown helper
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(false));

        LoginResponse result = authService.login(new LoginRequest("Hotdog", "123"));

        assertEquals(LoginResult.INVALIDUSER, result.getLoginResult());
    }

    // Test by providing a correct user.
    @Test
    void testLogin() {
        // Setup the mocked behavior when logging in as unknown helper
        
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));
        assertDoesNotThrow(() -> when(userDAO.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123")));

        LoginResponse result = authService.login(new LoginRequest("Hotdog", "123"));
        
        assertEquals(LoginResult.SUCCESS, result.getLoginResult());

        authService.logout(result.getSessionId());
    }

    // Test by logging in first and trying to log in as a different user.
    @Test
    void testLoginAlreadyLoggedIn() {
        // Setup the mocked behavior when logging in as helper
        
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));
        assertDoesNotThrow(() -> when(userDAO.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123")));

        LoginResponse response = authService.login(new LoginRequest("Hotdog", "123"));
        LoginResponse result = authService.login(new LoginRequest("Hotdog", "123"));

        assertEquals(LoginResult.ISLOGGEDIN, result.getLoginResult());

        authService.logout(response.getSessionId());
    }

    /*
     * Testing getLoggedUser() functionality.
     */

    @Test
    void testGetNonLoggedUser() {
        // Setup the mocked behavior when logging in as helper
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(false));
        assertDoesNotThrow(() -> when(userDAO.getUser("Hotdog")).thenReturn(null));
        
        assertEquals(null, authService.getLoggedUser("Hotdog"));
    }

    /*
     * Testing logOut() functionality.
     */

    // Test by providing a user not logged in.
    @Test
    void testLogoutNotLogged() {

        LoginResult result = authService.logout("Hotdog");

        assertEquals(LoginResult.NOTLOGGEDIN, result);
    }

    // Test with a logged in user.
    @Test
    @Order(1)
    void testLogout_01() {
        // Setup the mocked behavior when logging in as unknown helper
        
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));
        assertDoesNotThrow(() -> when(userDAO.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123")));

        LoginResponse response =  authService.login(new LoginRequest("Hotdog", "123"));

        LoginResult result = authService.logout(response.getSessionId());

        assertEquals(LoginResult.SUCCESS, result);
    }

     /*
      * Testing updateUser() functionality.
      */
      @Test
      void testUpdateUser() {
        // Setup the mocked behavior when logging in as unknown helper
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));
        assertDoesNotThrow(() -> when(userDAO.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123")));

        authService.login(new LoginRequest("Hotdog", "123"));

        // Attempt to update
        authService.updateUser("Hotdog");
        assertDoesNotThrow(() -> userDAO.hasUser("Hotdog"));

        authService.logout("Hotdog");
      }

    @Test
      void testUpdateUserNotLogged() {
        // Setup the mocked behavior when logging in as unknown helper
        assertDoesNotThrow(() -> when(userDAO.hasUser("Hotdog")).thenReturn(true));
        assertDoesNotThrow(() -> when(userDAO.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123")));

        // Attempt to update
        authService.updateUser("Hotdog");
        assertDoesNotThrow(() -> userDAO.hasUser("Hotdog"));

      }
}

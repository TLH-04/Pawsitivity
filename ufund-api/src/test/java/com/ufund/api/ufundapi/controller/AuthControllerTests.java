package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.LoginResponse;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.UserType;
import com.ufund.api.ufundapi.service.AuthService;

/**
 * The unit tests for the user services
 * ie. create account, log in, log out
 * 
 * @author Meghan Tomback
 */

public class AuthControllerTests {

    

    private AuthController  authController;
    private AuthService authService;

    @BeforeEach
    public void setupAuthController   () {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
    }


    /**
     * Helper Test Functions
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
     * Helper Test Functions
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


    @Test
    public void testLoginSuccess(){
        LoginRequest catRequest = new LoginRequest("cat", "123");
        LoginResponse logResponse = generateLoginResponse(LoginResult.SUCCESS,"123");
        System.out.println(logResponse.getLoginResult());
  
        when(authService.login(catRequest)).thenReturn(logResponse);
        when(authService.getCurrentType("123")).thenReturn(UserType.HELPER);

        ResponseEntity<LoginResult> response = authController.login(catRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //assert that the cookie stores the correct username
        assertEquals(ResponseCookie.from("SessionId","123").path("/").build().toString(), response.getHeaders().getValuesAsList(HttpHeaders.SET_COOKIE).get(0));
    }

    @Test
    public void testLoginInavlidUser(){
        LoginRequest loginRequest = new LoginRequest("silly", "123");
        when(authService.login(loginRequest)).thenReturn(generateLoginResponse(LoginResult.INVALIDUSER));

        ResponseEntity<LoginResult> response = authController.login(loginRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testLoginAlreadyUnauthorized(){
        LoginRequest loginRequest = new LoginRequest("hotdog", "123");
        when(authService.login(loginRequest)).thenReturn(generateLoginResponse(LoginResult.ISLOGGEDIN));

        ResponseEntity<LoginResult> response = authController.login(loginRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    

    @Test
    public void testLogout(){
        when(authService.logout("hotdog")).thenReturn(LoginResult.SUCCESS);
        ResponseEntity<Boolean> response = authController.logout("hotdog");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    public void testLogoutAlreadyLoggedOut(){
        when(authService.logout("cucumber")).thenReturn(LoginResult.NOTLOGGEDIN);

        ResponseEntity<Boolean> response = authController.logout("cucumber");
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }



}

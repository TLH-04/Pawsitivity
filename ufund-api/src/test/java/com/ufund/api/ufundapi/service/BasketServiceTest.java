package com.ufund.api.ufundapi.service;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Basket;
import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.need.ResourceNeed;
import com.ufund.api.ufundapi.model.need.VolunteerNeed;
import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.Admin;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.model.user.LoginResponse;
import com.ufund.api.ufundapi.model.user.User;
import com.ufund.api.ufundapi.persistence.CupboardDAO;
import com.ufund.api.ufundapi.persistence.CupboardFileDAO;
import com.ufund.api.ufundapi.persistence.UserFileDao;

/**
 * Tests the basket service class
 * 
 * @author Jason Ugbaja
 */
public class BasketServiceTest {
    BasketService basketService;
    AuthService authService;
    CupboardDAO cupboardDAO;

    UserFileDao userDAO;
    User[] testUsers;
    Need[] testNeeds;
    ObjectMapper mockObjectMapper;

    @BeforeEach
    public void setUpUserFileDAO() throws IOException {
        Basket basket = new Basket();
        basket.addNeed(1, 20);
        basket.addNeed(4, 20);
    
        basket.addNeed(5, 1);

        mockObjectMapper = mock(ObjectMapper.class);
        testUsers = new User[9];
        testUsers[0] = new Admin("f","123");
        testUsers[1] = new Helper("hotdog","123");
        testUsers[2] = new Helper("Team2Helper","123");
        testUsers[3] = new Helper("Jason","123");
        testUsers[4] = new Helper("Hamburger","123");
        testUsers[5] = new Helper("Hamburger2","123",basket);
        testUsers[6] = new Helper("Hamburger3","123");
        testUsers[7] = new Helper("Team2Helper2","123",basket);
        testUsers[8] = new Helper("Jason2","123");

        testNeeds = new Need[4];
        testNeeds[0] = new ResourceNeed(1, "Canned Beans", "please give beans", 5);
        testNeeds[1] = new VolunteerNeed(2, "Clean the streets", "help clean the streets", 5);
        testNeeds[2] = new ResourceNeed(3, "Dog Food", "please give dog food", 5);
        testNeeds[3] = new ResourceNeed(5, "Dog Food2", "please give dog food", 5);
        testNeeds[3].setListed(false);
        when(mockObjectMapper
            .readValue(new File("fake.txt"),User[].class))
            .thenReturn(testUsers);
        when(mockObjectMapper
            .readValue(new File("fakeneeds.txt"),Need[].class))
            .thenReturn(testNeeds);

        
        

        userDAO = new UserFileDao("fake.txt", mockObjectMapper);
        authService = new AuthService(userDAO);
        cupboardDAO = new CupboardFileDAO("fakeneeds.txt",mockObjectMapper);
        basketService = new BasketService(authService, cupboardDAO);
    }


    @Test
    public void TestGetBasket(){
        Basket expected = new Basket();
        LoginResponse response = authService.login(new LoginRequest("Hamburger","123"));
        Basket actual = basketService.getUserBasket(response.getSessionId());
        authService.logout(response.getSessionId());
        assertEquals(expected, actual);
    }

    @Test
    @Order(1)
    public void TestAddToBasket() {
        Basket expected = new Basket();
        expected.addNeed(1, 3);

        LoginResponse response =  authService.login(new LoginRequest("Hamburger3","123"));
        assertDoesNotThrow(() ->  basketService.addNeed(response.getSessionId(), 1,3));
        Basket actual = basketService.getUserBasket(response.getSessionId());
        authService.logout(response.getSessionId());
        assertEquals(expected, actual);
    }


    @Test
    public void TestUpdateToBasket() {
        Basket expected = new Basket();
        expected.addNeed(1, 5);

        LoginResponse response =  authService.login(new LoginRequest("Team2Helper2","123"));
        Basket actual = basketService.getUserBasket(response.getSessionId());
        authService.logout(response.getSessionId());
        assertEquals(expected, actual);
    }

    @Test
    public void TestAddToBasketGreaterDelisted() {
        Basket expected = new Basket();
        expected.addNeed(1, 5);

        LoginResponse response =  authService.login(new LoginRequest("Team2Helper","123"));
        assertDoesNotThrow(() ->  basketService.addNeed(response.getSessionId(), 1,500));
        assertDoesNotThrow(() ->  basketService.addNeed(response.getSessionId(), 5,50));
        
        Basket actual = basketService.getUserBasket(response.getSessionId());

        assertEquals(expected, actual);
    }

    @Test
    public void TestAddNullToBasket() {
        Basket baskey = new Basket();

        LoginResponse response =  authService.login(new LoginRequest("Jason","123"));
        Boolean result = assertDoesNotThrow(() -> basketService.addNeed("Jason", 999));

        assertFalse(result);
    }

    @Test
    public void TestRemoveFromBasket() {
        Basket expected = new Basket();
        expected.addNeed(1, 2);

        LoginResponse response = authService.login(new LoginRequest("Jason2","123"));
        assertDoesNotThrow(() -> basketService.addNeed(response.getSessionId(), 1,3));
        assertDoesNotThrow(()-> basketService.removeNeed(response.getSessionId(), 1,1));
        Basket actual = basketService.getUserBasket(response.getSessionId());
        authService.logout(response.getSessionId());
        assertEquals(expected, actual);
    }

    @Test
    public void TestRemoveNothingFromBasket(){
        Basket baskey = new Basket();
        Need expected = null;

        LoginResponse response = assertDoesNotThrow(() -> authService.login(new LoginRequest("Jason","123")));
        Need actual = assertDoesNotThrow(()->basketService.removeNeed(response.getSessionId(), 999,1));
        authService.logout(response.getSessionId());
        assertEquals(expected, actual);
    }

    @Test
    public void TestCheckout(){

        Need expected = new VolunteerNeed(2, "Clean the streets", "help clean the streets", 5);
        expected.setProgress(2);

        LoginResponse response = authService.login(new LoginRequest("hotdog","123"));
        assertDoesNotThrow(() -> basketService.addNeed(response.getSessionId(), 2,3));
        assertDoesNotThrow(() -> basketService.userCheckout(response.getSessionId()));

        Need actual = assertDoesNotThrow(() -> cupboardDAO.getNeed(2));
        authService.logout(response.getSessionId());
        assertEquals(expected, actual);
    }

    @Test
    public void TestCheckoutException() throws Exception {
        LoginResponse response = authService.login(new LoginRequest("hotdog","123"));
        assertThrows(Exception.class, () -> basketService.userCheckout(response.getSessionId()));
        authService.logout(response.getSessionId());
    }

}

package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.Basket;
import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.request.BasketRequest;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.service.BasketService;

/**
 * Tests for the basket controller class
 * 
 * 
 * @author Destiny Zeng
 */
public class BasketControllerTest {
    private BasketController basketController;
    private BasketService basketService;
    
    
    @BeforeEach
    public void setupBasketController() {
        basketService = mock(BasketService.class);
        basketController = new BasketController(basketService);
    }

    @Test
    public void testGetBasket() {
        Helper helper = new Helper("hotdog","123");
        Basket mockBasket = new Basket();

        mockBasket.addNeed(0, 1);
        when(basketService.getUserBasket(helper.getUsername())).thenReturn(mockBasket);
        
        ResponseEntity<Basket> response = basketController.getBasket(helper.getUsername());
        
        assertEquals(HttpStatus.OK,response.getStatusCode());
        
        Basket responseBasket = response.getBody();
        assertNotNull(responseBasket); // make so it will never be null
        int amount = assertDoesNotThrow(() -> responseBasket.getNeedAmount(0));
        assertEquals(1, amount);
    }
    
    @Test
    public void testGetBasketEmpty() {
        Helper helper = new Helper("hotdog","123");
        Basket mockBasket = new Basket();
        
        when(basketService.getUserBasket(helper.getUsername())).thenReturn(mockBasket);
        ResponseEntity<Basket> response = basketController.getBasket(helper.getUsername());

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(mockBasket, response.getBody());
    }
    
    @Test
    public void testGetBasketServerError() {
        Helper helper = new Helper("hotdog","123");
        
        doThrow(new RuntimeException()).when(basketService).getUserBasket(helper.getUsername());

        ResponseEntity<Basket> response = basketController.getBasket(helper.getUsername());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testAddNeed() {
        Helper helper = new Helper("hotdog","123");
        BasketRequest request = new BasketRequest(0, 2);
        assertDoesNotThrow(() ->  
            when(basketService.addNeed(helper.getUsername(), request.getId(), request.getAmount())).thenReturn(true)
        );

        ResponseEntity<Void> response = basketController.addNeed(helper.getUsername(), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddNeedConflict() {
        Helper helper = new Helper("hotdog","123");
        BasketRequest request = new BasketRequest(0, 2);
        assertDoesNotThrow(() ->  
            when(basketService.addNeed(helper.getUsername(), request.getId(), request.getAmount())).thenReturn(false)
        );

        ResponseEntity<Void> response = basketController.addNeed(helper.getUsername(), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testAddNeedNotExist() {
        Helper helper = new Helper("hotdog","123");
        BasketRequest request = new BasketRequest(0, 2);
        assertDoesNotThrow(() ->
            when(basketService.addNeed(helper.getUsername(), request.getId(), request.getAmount())).thenThrow(new Exception())
        );
        
        ResponseEntity<Void> response = basketController.addNeed(helper.getUsername(), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveNeed() {
        Helper helper = new Helper("hotdog","123");
        BasketRequest request = new BasketRequest(0, 2);
        Need mockNeed = mock(Need.class);
        assertDoesNotThrow(() ->
            when(basketService.removeNeed(helper.getUsername(), request.getId(), request.getAmount())).thenReturn(mockNeed)
        );

        ResponseEntity<Need> response = basketController.removeNeed(helper.getUsername(), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockNeed, response.getBody());
    }
    
    @Test
    public void testRemoveNeedServerError() {
        Helper helper = new Helper("hotdog","123");
        BasketRequest request = new BasketRequest(1, 2);
        assertDoesNotThrow(() ->
            when(basketService.removeNeed(helper.getUsername(), request.getId(), request.getAmount())).thenThrow(new Exception())
        );

        ResponseEntity<Need> response = basketController.removeNeed(helper.getUsername(), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCheckout() {
        Helper helper = new Helper("hotdog","123");
        assertDoesNotThrow(() ->
            when(basketService.userCheckout(helper.getUsername())).thenReturn(true)
        );

        ResponseEntity<Void> response = basketController.checkout(helper.getUsername());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

        
    @Test
    public void testCheckoutServerError() {
        Helper helper = new Helper("hotdog","123");
        assertDoesNotThrow(() ->
            when(basketService.userCheckout(helper.getUsername())).thenThrow(new Exception())
        );

        ResponseEntity<Void> response = basketController.checkout(helper.getUsername());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    public void testCheckoutServerConflict() {
        Helper helper = new Helper("hotdog","123");
        assertDoesNotThrow(() ->
            when(basketService.userCheckout(helper.getUsername())).thenReturn(false)
        );

        ResponseEntity<Void> response = basketController.checkout(helper.getUsername());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}

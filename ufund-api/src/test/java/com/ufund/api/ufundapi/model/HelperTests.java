package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.model.user.User;
import com.ufund.api.ufundapi.model.user.UserType;

/**
 * The unit tests for the Helper class
 * 
 * @author Meghan Tomback
 */
public class HelperTests {
    
    @Test
    public void testCtor(){
        // Setup
        String expected_username = "hotdog";
        String expected_password = User.createHashedPassword("hotdog", "123");
        UserType expected_type = UserType.HELPER;
        Basket basket = new Basket();

        // Invoke
        Helper tester = new Helper("hotdog", "123" ,basket);

        // Analyze
        assertEquals(expected_username, tester.getUsername());
        assertEquals(expected_password, tester.getPassword());
        assertEquals(expected_type, tester.getType());
        assertEquals(basket, tester.getBasket());
    }

    @Test
    public void testCtor2(){
        // Setup
        String expected_username = "hotdog";
        String expected_password = User.createHashedPassword("hotdog", "123"); // Assuming this was already hashed
        UserType expected_type = UserType.HELPER;
        Basket basket = new Basket();

        // Invoke
        Helper tester = new Helper("hotdog", "123" ,basket,false);

        // Analyze
        assertEquals(expected_username, tester.getUsername());
        assertEquals(expected_password, tester.getPassword());
        assertEquals(expected_type, tester.getType());
        assertEquals(basket, tester.getBasket());
    }

    @Test
    public void Testequals(){
        // Setup
        boolean expected = true;
        Basket basket = new Basket();

        // Invoke
        Helper tester = new Helper("hotdog","123", basket);
        Helper tester2 = new Helper("hotdog","123", basket);
        
        // Analyze
        assertEquals(tester.equals(tester2),expected);
        assertEquals(tester.toString().equals(tester2.toString()),expected);
        assertEquals(tester.getContributedNeeds() == tester2.getContributedNeeds(),expected);
    }

    @Test
    public void Testequalsnull(){
        // Setup
        boolean expected = false;
        Basket basket = new Basket();

        // Invoke
        Helper tester = new Helper("hotdog","123", basket);
        
        // Analyze
        assertEquals(tester.equals(null),expected);
        assertEquals(tester.equals(""),expected);
    }

    @Test
    public void Testequalsfalse(){
        // Setup
        boolean expected = false;
        Basket basket = new Basket();

        // Invoke
        Helper tester = new Helper("hotdog","123", basket);
        Helper tester2 = new Helper("hotdog","1234", basket);
        
        // Analyze
        assertEquals(tester.equals(tester2),expected);
    }


    @Test
    public void Testequalsdiffuser(){
        // Setup
        boolean expected = false;
        Basket basket = new Basket();

        // Invoke
        Helper tester = new Helper("hotdog","123", basket);
        Helper tester2 = new Helper("hotdog2","123", basket);
        
        // Analyze
        assertEquals(tester.equals(tester2),expected);

    }

    @Test
    public void Testequalsdiffusers(){
        // Setup
        boolean expected = false;
        Basket basket = new Basket();

        // Invoke
        Helper tester = new Helper("hotdog","123", basket);
        Helper tester2 = new Helper("hotdog2","1234", basket);
        
        // Analyze
        assertEquals(tester.equals(tester2),expected);
        assertEquals(tester.hashCode() == tester2.hashCode(),expected);
    }

    @Test
    public void ZeroContributedNeeds(){
        // Setup
        int expected = 0;
        Basket basket = new Basket();

        // Invoke
        Helper tester = new Helper("hotdog","123", basket);
        tester.updateContributedNeeds(0);

        
        // Analyze

        assertEquals(tester.getContributedNeeds(), expected);
        
    }
}

package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.ufund.api.ufundapi.model.user.Admin;
import com.ufund.api.ufundapi.model.user.UserType;

/**
 * The unit tests for the Admin class
 * 
 * @author Meghan Tomback
 * 
 */
public class AdminTests {

    @Test
    public void testCtor() {

        // Invoke
        Admin tester = new Admin("f", "123");

        // Analyze
        assertEquals("f", tester.getUsername());
        assertEquals(UserType.ADMIN, tester.getType());
    }

    @Test
    public void testCtor2() {

        // Invoke
        Admin tester = new Admin("f", "123",true);

        // Analyze
        assertEquals("f", tester.getUsername());
        assertEquals(UserType.ADMIN, tester.getType());
    }
}

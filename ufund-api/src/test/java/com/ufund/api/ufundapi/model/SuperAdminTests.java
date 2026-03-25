package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.ufund.api.ufundapi.model.user.SuperAdmin;
import com.ufund.api.ufundapi.model.user.UserType;

/**
 * The unit tests for the Super Admin class
 * 
 * @author Jason Ugbaja
 * 
 */
public class SuperAdminTests {

    @Test
    public void testCtor() {

        // Invoke
        SuperAdmin tester = new SuperAdmin("123");

        // Analyze
        assertEquals("Admin", tester.getUsername());
        assertEquals(UserType.SUPER, tester.getType());
    }

    @Test
    public void testCtor2() {

        // Invoke
        SuperAdmin tester = new SuperAdmin("123",true);

        // Analyze
        assertEquals("Admin", tester.getUsername());
        assertEquals(UserType.SUPER, tester.getType());
    }
}

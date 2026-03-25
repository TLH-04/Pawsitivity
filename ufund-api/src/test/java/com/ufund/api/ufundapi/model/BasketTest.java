package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.need.ResourceNeed;

@Tag("Model-Tier")
public class BasketTest {
    @Test
    public void TestCreate(){   
        // Setup
        int expected_needs = 0;
        

        // Invoke
        Basket b = new Basket();

        // Analyze
        assertEquals(b.getBasketContents().size(), expected_needs);
        
    }

    @Test 
    public void Testequals(){
        // Setup
        boolean expected = true;
        boolean expected2 = false;
        

        // Invoke
        Basket b = new Basket();
        b.addNeed(new ResourceNeed(1, "Test", "dasd", 1),1);


        Basket b2 = new Basket();
        b2.addNeed(new ResourceNeed(1, "Test", "dasd", 1),1);

        Basket b3 = new Basket();
        b3.addNeed(new ResourceNeed(2, "Test", "dasd", 10),4);

        // Analyze
        boolean actual = b.equals(b2);
        boolean actual2 = b.equals(b3);
        boolean actual3 = b.equals("This should be false");

        assertEquals(actual,expected);  
        assertEquals(actual2, expected2); 
        assertEquals(actual3, expected2); 
    }

    @Test 
    public void TestDoesNotEquals(){
        // Setup
        boolean expected = false;
        

        // Invoke
        Basket b = new Basket();
        b.addNeed(new ResourceNeed(1, "Test", "dasd", 1),1);


        Basket b2 = new Basket();
        b2.addNeed(new ResourceNeed(3, "Test2", "dasd", 1),1);
        // Analyze
        boolean actual = b.equals(b2);
        boolean actual2 = b.equals(null);

        assertEquals(actual,expected);  
        assertEquals(actual2, expected); 
    }

    @Test
    public void TestAddNeed(){   
        // Setup
        int expected_needs = 1;
        

        // Invoke
        Basket b = new Basket();
        b.addNeed(new ResourceNeed(1, "Test", "dasd", 5),1);
        b.addNeed(1,3);
        // Analyze
        assertEquals(b.getBasketContents().size(),expected_needs);
       
    }

    @Test
    public void TestSetNeed(){   
        // Setup
        int expected_needs = 5;
        

        // Invoke
        Basket b = new Basket();
        b.addNeed(new ResourceNeed(1, "Test", "dasd", 10),1);
        b.setNeed(1, 5);

        // Analyze
       assertEquals(assertDoesNotThrow(() -> b.getNeedAmount(1)),expected_needs);
        
    }

    @Test
    public void TestAddRemove(){   
        // Setup
        int expected_needs = 1;
        

        // Invoke
        Basket b = new Basket();
        b.addNeed(new ResourceNeed(1, "Test", "dasd", 1),1);
        b.addNeed(new ResourceNeed(2, "Test", "dasd", 1),1);
       
        b.removeNeed(2,1);
        b.removeNeed(3,1);
      
        // Analyze
        assertEquals(b.getBasketContents().size(), expected_needs);
       // assertEquals(b.get.size(),expected_needs);
        
    }

    @Test
    public void TestRemoveAll(){   
        // Setup
        int expected_needs = 1;
        
        // Invoke
        Basket b = new Basket();
        Need testneed = (Need) new ResourceNeed(3, "Test3", "dasd", 1);
        b.addNeed(new ResourceNeed(1, "Test1", "dasd", 1),1);
        b.addNeed(new ResourceNeed(2, "Test2", "dasd", 1),5);
        b.addNeed(testneed,1);
        b.removeNeed(2,5);
        b.removeNeed(testneed);
        // Analyze
        assertEquals(b.getBasketContents().size(), expected_needs);
       // assertEquals(b.get.size(),expected_needs);
        
    }


}

package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.need.MonetaryNeed;
import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.need.ResourceNeed;
import com.ufund.api.ufundapi.model.need.VolunteerNeed;


/**
 * Tests for the Cupboard File DAO
 * 
 * @author Meghan
 */
public class CupboardFileDAOTest {
    
    private CupboardFileDAO cupboardFileDAO;
    private Need[] testNeeds;
    private ObjectMapper mockObjectMapper;

    @BeforeEach
    public void setUpCupboardFileDAO() throws IOException {
        mockObjectMapper = mock(ObjectMapper.class);
        testNeeds = new Need[3];
        testNeeds[0] = new VolunteerNeed(0, "help me please!", "i need help", 10);
        testNeeds[1] = new ResourceNeed(1, "give me things!", "please i need things!", 3);
        testNeeds[2] = new MonetaryNeed(2, "Please give me money", "i love money", 6);

        when(mockObjectMapper
            .readValue(new File("not_real.txt"), Need[].class))
            .thenReturn(testNeeds);

        cupboardFileDAO = new CupboardFileDAO("not_real.txt", mockObjectMapper);
    }

    @Test
    public void testGetNeeds() {
        Need[] needs = cupboardFileDAO.getNeeds();
        for (int i = 0; i < testNeeds.length;++i)
        {
            assertEquals(needs[i],testNeeds[i]);
        }
    }

    @Test
    public void testFindNeedsNoResults(){
        assertEquals(0, cupboardFileDAO.findNeeds("void").length);
    }
    
    @Test
    public void testFindNeedsResults(){
        Need[] returned = cupboardFileDAO.findNeeds("things");
        assertEquals(1, returned.length);
        assertEquals(testNeeds[1], returned[0]);
    }

    @Test
    public void testFindNeedsCaseSensitive(){
        Need[] returned = cupboardFileDAO.findNeeds("Please");
        Need[] expected = {testNeeds[0], testNeeds[2]};
        assertEquals(2, returned.length);
        assertEquals(returned[0], expected[0]);
        assertEquals(returned[1], expected[1]);
    }

    @Test
    public void tesGetNeedValid(){
        assertEquals(testNeeds[2], cupboardFileDAO.getNeed(2));
    }

    @Test
    public void testGetNeedInvalid(){
        assertNull(cupboardFileDAO.getNeed(8));
    }   
    
    @Test
    public void testCreateNeed(){
        Need newNeed = assertDoesNotThrow(() -> cupboardFileDAO.createNeed(new VolunteerNeed(3, "new need", "this need is so cool", 0)), "Unexpected exception thrown");
        assertEquals(cupboardFileDAO.getNeeds().length, 4);
        assertEquals(cupboardFileDAO.getNeed(3), newNeed);
    }


    //want to test the exception but how do i make a need with an invalid type..
    @Test
    public void testUpdateNeedExists(){
        Need update = new ResourceNeed(1, "updated!", "this need has been updated", 2);
        Need returned = assertDoesNotThrow(() -> cupboardFileDAO.updateNeed(update), "Unexpected excetion thrown");
        assertEquals(update, returned);
        assertEquals(update, cupboardFileDAO.getNeed(1));
        
    }
    //do we actually j want it to return null if the need doesn't exist?
    @Test
    public void testUpdateNeedDoesntExists(){
        Need update = new ResourceNeed(6, "fake!", "this shouldn't be updated", 2);
        Need returned = assertDoesNotThrow(() -> cupboardFileDAO.updateNeed(update), "Unexpected exception thrown");
        assertNull(returned);
    }

    @Test
    public void testDeleteNeedExists(){
        Need removedExpected = testNeeds[0];
        Need removedActual = assertDoesNotThrow(() -> cupboardFileDAO.deleteNeed(0), "Unexpected exception thrown");
        assertEquals(removedExpected, removedActual);
        assertNull(cupboardFileDAO.getNeed(0));
    }

    @Test
    public void testDeleteNeedNotExist(){
        Need result = assertDoesNotThrow(() -> cupboardFileDAO.deleteNeed(8), "Unexpected exception thrown");
        assertNull(result);
    }

    @Test
    public void testUpdateNeeds(){
        assertEquals(testNeeds[0].getProgress(), 0);
        assertEquals(testNeeds[1].getProgress(), 0);
        Map<Integer, Integer> needUpdates = new HashMap<>();
        needUpdates.put(0, 5);
        needUpdates.put(1, 3);
        assertDoesNotThrow(() -> cupboardFileDAO.updateNeeds(needUpdates), "Unexpected exception thrown");
        assertEquals(testNeeds[0].getProgress(), 5);
        assertEquals(testNeeds[1].getProgress(), 3);

    }

    @Test
    public void testGetListingStatus(){
        assertTrue(testNeeds[0].isListed());
    }

    @Test
    public void testUpdateListingStatus(){
        assertTrue(testNeeds[1].isListed());
        Need returned = assertDoesNotThrow(() -> cupboardFileDAO.updateListingStatus(testNeeds[1]), "Unexpected excetion thrown");
        assertFalse(returned.isListed());
    }

        @Test
    public void testUpdateListingStatus2(){
        Need returned = assertDoesNotThrow(() -> cupboardFileDAO.updateListingStatus(new ResourceNeed(300, "give me things!", "please i need things!", 3)), "Unexpected excetion thrown");
        assertNull(returned);
    }

    @Test
    public void testGetListingStatusID(){
        assertTrue(cupboardFileDAO.getListingStatus(testNeeds[2].getId()));
    }
}
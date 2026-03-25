package com.ufund.api.ufundapi.controller;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ufund.api.ufundapi.model.need.MonetaryNeed;
import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.need.ResourceNeed;
import com.ufund.api.ufundapi.model.need.VolunteerNeed;
import com.ufund.api.ufundapi.persistence.CupboardDAO;

/**
 * Test CupboardController class
 * 
 * @author Destiny Zeng
 * 
 */
public class CupboardControllerTest {
    private CupboardController cupboardController;
    private CupboardDAO mockCupboardDAO;


    /**
     * Before each test, create new CupboardController object and
     * inject mock Cupboard DAO
     * 
     */
    @BeforeEach
    public void setUpCupboardController() {
        mockCupboardDAO = mock(CupboardDAO.class);
        cupboardController = new CupboardController(mockCupboardDAO);
    }

    @Test
    public void testGetNeed() {
        VolunteerNeed testNeed = new VolunteerNeed( 1,"example need", "description", 10);
        assertDoesNotThrow(() ->
            when(mockCupboardDAO.getNeed(testNeed.getId())).thenReturn(testNeed)
        );
        
        ResponseEntity<Need> response = cupboardController.getNeed(testNeed.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testNeed, response.getBody());
    }
    
    @Test
    public void testGetNeedFail() {
        int fakeId = 34;
        assertDoesNotThrow(() ->
            when(mockCupboardDAO.getNeed(fakeId)).thenReturn(null)
        );

        ResponseEntity<Need> response = cupboardController.getNeed(fakeId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetNeedException()  {
        int fakeId = 34;
        assertDoesNotThrow(() ->
            doThrow(new IOException()).when(mockCupboardDAO).getNeed(fakeId)
        );

        ResponseEntity<Need> response = cupboardController.getNeed(fakeId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetNeeds() {
        Need[] testNeeds = new Need[2];
        testNeeds[0] = new ResourceNeed(1, "resource need", "resource description", 50);
        testNeeds[1] = new MonetaryNeed(2, "monetary need", "monetary description", 10);
        assertDoesNotThrow(() ->
            when(mockCupboardDAO.getNeeds()).thenReturn(testNeeds)
        );

        ResponseEntity<Need[]> response = cupboardController.getNeeds();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testNeeds, response.getBody());
    }   

    @Test
    public void testGetNeedsException() {
        assertDoesNotThrow(() ->
            doThrow(new IOException()).when(mockCupboardDAO).getNeeds()
        );
        
        ResponseEntity<Need[]> response = cupboardController.getNeeds();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testSearchNeeds() {
        Need[] testNeeds = new Need[2];
        testNeeds[0] = new ResourceNeed(1, "resource need", "resource description", 50);
        testNeeds[1] = new MonetaryNeed(2, "monetary need", "monetary description", 10);
        assertDoesNotThrow(() ->
            when(mockCupboardDAO.findNeeds("monetary")).thenReturn(testNeeds)
        );

        ResponseEntity<Need[]> response = cupboardController.searchNeeds("monetary");

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(testNeeds,response.getBody());
    }

    @Test
    public void testSearchNeedsException() {
        assertDoesNotThrow(() ->
            doThrow(new IOException()).when(mockCupboardDAO).findNeeds("does not exist")
        );

        ResponseEntity<Need[]> response = cupboardController.searchNeeds("does not exist");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testAddNeed()  {
        VolunteerNeed testNeed = new VolunteerNeed( 1,"example need", "description", 10);
        assertDoesNotThrow(() ->
            when(mockCupboardDAO.createNeed(testNeed)).thenReturn(testNeed)
        );
        
        ResponseEntity<Need> response = cupboardController.addNeed(testNeed);

        assertEquals(HttpStatus.CREATED,response.getStatusCode());
        assertEquals(testNeed, response.getBody());
    }

    @Test
    public void testAddNeedException() {
        VolunteerNeed testNeed = new VolunteerNeed( 1,"example need", "description", 10);
        assertDoesNotThrow(() ->
            doThrow(new IOException()).when(mockCupboardDAO).createNeed(testNeed)
        );

        ResponseEntity<Need> response = cupboardController.addNeed(testNeed);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testRemoveNeed() {
        int fakeId = 34;
        VolunteerNeed testNeed = new VolunteerNeed( 1,"example need", "description", 10);

        assertDoesNotThrow(() ->
            when(mockCupboardDAO.deleteNeed(fakeId)).thenReturn(testNeed)
        );

        ResponseEntity<Need> response = cupboardController.removeNeed(fakeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testNeed, response.getBody());
    }

    @Test
    public void testRemoveNeedFail() {
        int fakeId = 34;
        assertDoesNotThrow(() ->
            when(mockCupboardDAO.deleteNeed(fakeId)).thenReturn(null)
        );

        ResponseEntity<Need> response = cupboardController.removeNeed(fakeId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test 
    public void testRemoveNeedException() {
        int fakeId = 34;
        assertDoesNotThrow(() ->
            doThrow(new IOException()).when(mockCupboardDAO).deleteNeed(fakeId)
        );

        ResponseEntity<Need> response = cupboardController.removeNeed(fakeId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testUpdateNeed() {
        VolunteerNeed testNeed = new VolunteerNeed( 1,"example need", "description", 10);
        assertDoesNotThrow(() ->
            when(mockCupboardDAO.updateNeed(testNeed)).thenReturn(testNeed)
        );
        
        testNeed.setName("updated name");

        ResponseEntity<Need> response = cupboardController.updateNeed(testNeed);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(testNeed,response.getBody());
    }

    @Test
    public void testUpdateNeedFail() {
        VolunteerNeed testNeed = new VolunteerNeed( 1,"example need", "description", 10);
        assertDoesNotThrow(() ->
            when(mockCupboardDAO.updateNeed(testNeed)).thenReturn(null)
        );

        ResponseEntity<Need> response = cupboardController.updateNeed(testNeed);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateNeedException() {
        VolunteerNeed testNeed = new VolunteerNeed( 1,"example need", "description", 10);
        assertDoesNotThrow(() ->
            doThrow(new IOException()).when(mockCupboardDAO).updateNeed(testNeed)
        );

        ResponseEntity<Need> response = cupboardController.updateNeed(testNeed);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testUpdateListing() {
        VolunteerNeed testNeed = new VolunteerNeed(1, "potatoes", "startchy", 12);
        VolunteerNeed updatedNeed = new VolunteerNeed(1, "potatoes", "startchy", 12);

        updatedNeed.setListed(false);
        assertDoesNotThrow(() -> when(mockCupboardDAO.updateListingStatus(testNeed)).thenReturn(updatedNeed));

        ResponseEntity<Need> response = cupboardController.updateListing(testNeed);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Need updatedTestNeed = Objects.requireNonNull(response.getBody(), "Response body should not be null");
        assertFalse(updatedTestNeed.isListed());
    }

    @Test
    public void testUpdateListingException() {
        VolunteerNeed testNeed = new VolunteerNeed(1, "potatoes", "startchy", 12);

        assertDoesNotThrow(() -> doThrow(new IOException()).when(mockCupboardDAO).updateListingStatus(testNeed));

        ResponseEntity<Need> response = cupboardController.updateListing(testNeed);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetListingExists() {
        int needID = 1;

        assertDoesNotThrow(() -> when(mockCupboardDAO.getListingStatus(needID)).thenReturn(true));
        
        ResponseEntity<Boolean> response = cupboardController.getListing(needID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    public void testGetListingNotFound() {
        int needID = 2;

        assertDoesNotThrow(() -> when(mockCupboardDAO.getListingStatus(needID)).thenReturn(false));
        
        ResponseEntity<Boolean> response = cupboardController.getListing(needID); 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetListingException() {
        int needID = 3;

        assertDoesNotThrow(() -> doThrow(new IOException()).when(mockCupboardDAO).getListingStatus(needID));
        
        ResponseEntity<Boolean> response = cupboardController.getListing(needID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}

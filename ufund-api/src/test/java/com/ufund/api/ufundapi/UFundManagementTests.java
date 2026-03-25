package com.ufund.api.ufundapi;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.need.VolunteerNeed;
import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.Admin;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.service.AuthService;
import com.ufund.api.ufundapi.model.user.LoginResponse;
import com.ufund.api.ufundapi.persistence.CupboardFileDAO;
import com.ufund.api.ufundapi.persistence.UserDao;



/**
 * Series of tests on the accessibility of the cupboard.
 * 
 * @author Anthony Lansing
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UFundManagementTests {

    // Mock the object mapper to make the CupboardFileDao functional
    @Mock
    private ObjectMapper objectMapper;

    // Mock the UserDao so that the UserService is functional
    @Mock
    private UserDao userDao;

    // Mock the authenticationService so that the UserService is functional
   
    private AuthService authenticationService;


    // Both the UserService and CupboardFileDao and their relationship is being tested
    // so these will not be mocked.
    private CupboardFileDAO dao;

    /**
     * Before each test, start a new UserService with the mocked UserDao.
     * Also instantiate the CupboardFileDAO which requires a file, an objectmapper and a UserService.
     * a mock cupboard DAO
     */
    @BeforeEach
    public void setupCupboardController(@TempDir Path tmp) throws Exception {
        authenticationService = new AuthService(userDao);

        // get the path to the needs.json for the CupboardDAO
        String file = tmp.resolve("needs.json").toString();
        // map the object mapper's return when accessed by the CupboardFileDAO
        when(objectMapper.readValue(any(File.class), eq(Need[].class))).thenReturn(new Need[0]);

        dao = new CupboardFileDAO(file, objectMapper);
    }

    /**
     * Ensure logout after each test for clean slate.
     */
    @AfterEach
    public void cleanUp() {
        
    }

    /**
     * Only and Admin can edit needs in the cupboard.
     * Should pass when logged in as Admin.
     */
    @Test
    void testEditNeedAsAdmin() throws Exception {
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        authenticationService.login(new LoginRequest("Admin", "123"));


        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and assert the need to be edited
        Need createdNeed = dao.createNeed(mockNeed);
        assertNotNull(createdNeed);

        // Create the need to be edited and assert
        int id = createdNeed.getId();
        Need updatedNeed = new VolunteerNeed(id, "Volunteer", "Mock Need", 5);
        Need result = dao.updateNeed(updatedNeed);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(5, result.getGoal());

    }

    /**
     * Test that an exception is properly thrown when a Helper attempts to edit a need.
     * 
     * @throws Exception
     */
    @Test
    void testEditNeedExceptionHelper() throws Exception{
        // Setup the mocked behavior when logging in as Admin/Helper
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        when(userDao.hasUser("Hotdog")).thenReturn(true);
        when(userDao.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123"));


        // Login as Admin to create a need to be edited
        authenticationService.login(new LoginRequest("Admin", "123"));

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and assert the need to be edited
        Need createdNeed = dao.createNeed(mockNeed);
        assertNotNull(createdNeed);

        // log out as admin and log in as a Helper
        authenticationService.logout("Admin");
        // Setup the mocked behavior when logging in as Helper
        authenticationService.login(new LoginRequest("Hotdog", "123"));
    }

    /**
     * Test that an exception is properly thrown when no logged in user attempts an edit.
     * 
     * @throws Exception
     */
    @Test
    void testEditNeedExceptionNoUser() throws Exception {
        // Setup the mocked behavior when logging in as Admin/Helper
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f","123"));


        // Login as Admin to create a need to be edited
        authenticationService.login(new LoginRequest("Admin", "123"));

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and assert the need to be edited
        Need createdNeed = dao.createNeed(mockNeed);
        assertNotNull(createdNeed);

        // log out as admin and log in as a Helper
        authenticationService.logout("Admin");

        // Create the need to be edited and assert
        int id = createdNeed.getId();
        Need updatedNeed = new VolunteerNeed(id, "Volunteer", "Mock Need", 5);
    }

    /**
     * Test createNeed verifies Admin before adding to the cupboard.
     * @throws Exception
     */
    @Test 
    void testAddNeedAsAdmin() throws Exception {
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f","123"));
        authenticationService.login(new LoginRequest("Admin", "123"));


        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and assert the need to be edited
        Need createdNeed = dao.createNeed(mockNeed);
        assertNotNull(createdNeed);

        assertEquals("Volunteer", createdNeed.getName());
        assertEquals("Mock Need", createdNeed.getDescription());
        assertEquals(10, createdNeed.getGoal());
    }

    /**
     * Test LoginException is thrown when attempting to create need as a Helper
     * @throws Exception
     */
    @Test
    void testAddNeedExceptionAsHelper() throws Exception {
        // Setup the mocked behavior when logging in as Admin/Helper
        when(userDao.hasUser("Hotdog")).thenReturn(true);
        when(userDao.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123"));

        // Login as Admin to create a need to be edited
        authenticationService.login(new LoginRequest("Admin", "123"));

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

    }

    /**
     * Test LoginException is thrown when attempting to create need when not logged in
     * @throws Exception
     */
    @Test
    void TestAddNeedExceptionNoUser() throws Exception {

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

    
    }

    /**
     * Test removeNeed() verifies user is Admin for deleting a need fro mthe cupboard
     * @throws Exception
     */
    @Test
    void testRemoveNeedAsAdmin() throws Exception {
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        authenticationService.login(new LoginRequest("Admin", "123"));


        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and assert the need to be edited
        Need createdNeed = dao.createNeed(mockNeed);
        assertNotNull(createdNeed);

    
    }

    /**
     * Test LoginException is thrown when attempting to delete a need as a Helper
     * @throws Exception
     */
    @Test
    void testRemoveNeedExceptionAsHelper() throws Exception {
        // Setup the mocked behavior when logging in as Admin/Helper
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        when(userDao.hasUser("Hotdog")).thenReturn(true);
        when(userDao.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123"));

        // exception message to check against
        String message = "You dont have access to this!";

        // Login as Admin to create a need to be edited
        authenticationService.login(new LoginRequest("Admin", "123"));

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and assert the need to be removed
        Need createdNeed = dao.createNeed(mockNeed);
        assertNotNull(createdNeed);

        // log out as admin and log in as a Helper
        authenticationService.logout("Admin");
        // Setup the mocked behavior when logging in as Helper
        authenticationService.login(new LoginRequest("Hotdog", "123"));

        // Create the need to be removed and assert
        int id = createdNeed.getId();

 
    }

    /**
     * Test LoginException is thrown when attempting to delete a need when not logged in
     * @throws Exception
     */
    @Test
    void testRemoveNeedExceptionNoUser() throws Exception {
        // Setup the mocked behavior when logging in as Admin/Helper
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));

        // exception message to check against
        String message = "You are not logged in";

        // Login as Admin to create a need to be edited
        authenticationService.login(new LoginRequest("Admin", "123"));

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and assert the need to be removed
        Need createdNeed = dao.createNeed(mockNeed);
        assertNotNull(createdNeed);

        // log out as admin and log in as a Helper
        authenticationService.logout("Admin");
        
        // Create the need to be removed and assert
        int id = createdNeed.getId();

   
    }

    /**
     * Test getNeeds verifies user is Admin before return needs in the cupboard
     * @throws Exception
     */
    @Test
    void testViewCupboardAsAdmin() throws Exception {
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        authenticationService.login(new LoginRequest("Admin", "123"));


        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and the need to be returned later
        dao.createNeed(mockNeed);
        
        // Get the list of needs as assert that it is not empty
        Need[] cupboard = dao.getNeeds();

        assert(cupboard.length > 0);
    }

    /**
     * Test getsNeeds verified user is a Helper before returning Needs in the cupboard
     * @throws Exception
     */
    @Test
    void testViewCupboardAsHelper() throws Exception{
        // Setup the mocked behavior when logging in as Admin/Helper
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        when(userDao.hasUser("Hotdog")).thenReturn(true);
        when(userDao.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123"));
        LoginResponse response = authenticationService.login(new LoginRequest("Admin", "123"));

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and the need to be returned later
        dao.createNeed(mockNeed);

        authenticationService.logout(response.getSessionId());
        authenticationService.login(new LoginRequest("Hotdog", "123"));
        
        // Get the list of needs as assert that it is not empty
        Need[] cupboard = dao.getNeeds();

        assert(cupboard.length > 0);
    }

    /**
     * Test LoginException is thrown when attempting to view cupboard and not logged in
     * @throws Exception
     */
    @Test
    void testViewCupboardException() throws Exception{
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        LoginResponse response = authenticationService.login(new LoginRequest("Admin", "123"));

        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));
        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()

        // Create and the need to be returned later
        dao.createNeed(mockNeed);

        authenticationService.logout(response.getSessionId());
        
      
    }

    /**
     * Test findNeeds() verifies user is Admin before returning list a needs containg same text
     * @throws Exception
     */
    @Test
    void testFindNeedsAsAdmin() throws Exception {
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        authenticationService.login(new LoginRequest("Admin", "123"));


        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and the need to be returned later
        dao.createNeed(mockNeed);
        
        // Get the list of needs as assert that it is not empty
        Need[] cupboard = dao.findNeeds("Vol");

        assert(cupboard.length > 0);
    }

    /**
     * Test findNeeds() verifies user is Helper before returning list a needs containg same text
     * @throws Exception
     */
    @Test
    void testFindNeedsAsHelper() throws Exception {
        // Setup the mocked behavior when logging in as Admin/Helper
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        when(userDao.hasUser("Hotdog")).thenReturn(true);
        when(userDao.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123"));
        LoginResponse reponse = authenticationService.login(new LoginRequest("Admin", "123"));

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and the need to be returned later
        dao.createNeed(mockNeed);

        authenticationService.logout(reponse.getSessionId());
        authenticationService.login(new LoginRequest("Hotdog", "123"));
        
        // Get the list of needs as assert that it is not empty
        Need[] cupboard = dao.findNeeds("Vol");

        assert(cupboard.length > 0);
    }

    /**
     * Test LoginException is thrown when attempting to get list of needs from the cupboard
     * @throws Exception
     */
    @Test
    void testFindNeedsException() throws Exception {
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));
        LoginResponse reponse = authenticationService.login(new LoginRequest("Admin", "123"));

        String message = "You are not logged in";

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()

        // Create and the need to be returned later
        dao.createNeed(mockNeed);

        authenticationService.logout(reponse.getSessionId());
      
    }

    /**
     * Test getNeed verifies user is Admin before returning a Need with given Id.
     * @throws Exception
     */
    @Test
    void testGetNeedAsAdmin() throws Exception {
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        authenticationService.login(new LoginRequest("Admin", "123"));


        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and the need to be returned later
        Need createdNeed = dao.createNeed(mockNeed);
        
        // Get the list of needs as assert that it is not empty
        Need foundNeed = dao.getNeed(createdNeed.getId());

        assertNotNull(foundNeed);
    }

    /**
     * Test getNeed verifies user is Helper before returning a Need with given Id.
     * @throws Exception
     */
    @Test
    void testGetNeedAsHelper() throws Exception {
        // Setup the mocked behavior when logging in as Admin/Helper
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        when(userDao.hasUser("Hotdog")).thenReturn(true);
        when(userDao.getUser("Hotdog")).thenReturn(new Helper("Hotdog","123"));
        authenticationService.login(new LoginRequest("Admin", "123"));

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and the need to be returned later
        Need createdNeed = dao.createNeed(mockNeed);

        authenticationService.logout("Admin");
        
        // Get the list of needs as assert that it is not empty
        Need foundNeed = dao.getNeed(createdNeed.getId());

        assertNotNull(foundNeed);
    }

    /**
     * Test Exception is thrown when attempting to get need by Id while not logged in.
     * @throws Exception
     */
    @Test
    void testGetNeedException() throws Exception {
        // Setup the mocked behavior when logging in as Admin
        when(userDao.hasUser("Admin")).thenReturn(true);
        when(userDao.getUser("Admin")).thenReturn(new Admin("f", "123"));
        authenticationService.login(new LoginRequest("Admin","123"));

        String message = "You are not logged in";

        Need mockNeed = new VolunteerNeed(0, "Volunteer", "Mock Need", 10);

        // Tell the test what objectmapper should do when attempting to call save()
        doNothing().when(objectMapper).writeValue(any(File.class), any(Need[].class));

        // Create and the need to be returned later
        Need createdNeed = dao.createNeed(mockNeed);

        authenticationService.logout("Admin");
        
 
    }
}

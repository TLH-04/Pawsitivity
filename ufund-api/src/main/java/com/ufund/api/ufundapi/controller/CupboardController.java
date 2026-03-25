package com.ufund.api.ufundapi.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.user.permission.RequiresPermission;
import com.ufund.api.ufundapi.persistence.CupboardDAO;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.ufund.api.ufundapi.model.user.permission.PermissionType;

/**
 * 
 * Handles the REST API requests for the Need resource
 *
 * {@literal @}RestController Spring annotation identifies this class as a REST API
 * method handler to the Spring framework
 * 
 * @author Anthony Lansing
 */

@CrossOrigin(
    origins = "http://localhost:4200",
    allowCredentials = "true"
)
@RestController
@RequestMapping("cupboard")
public class CupboardController {
    private static final Logger LOG = Logger.getLogger(CupboardController.class.getName());
    private final CupboardDAO cupboardDAO;
    /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param cupboardDAO The {@link CupboardDAO Cupboard Data Access Object} to perform CRUD operations
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public CupboardController(CupboardDAO cupboardDAO) {
        this.cupboardDAO = cupboardDAO;
    }

    /**
     * Response to the GET request for a {@linkplain Need need} for the given id
     * 
     * @param id The id used to located the {@link Need need}
     * 
     * @return ResponseEntity with {@link Need need} object and HTTP status of OK if found
     * <br>
     * ResponseEntity with HTTP status of NOT_FOUND if not found
     * <br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/needs/id={id}")
    @RequiresPermission(permissionType = PermissionType.SHARED)
    public ResponseEntity<Need> getNeed(@PathVariable int id) {
        LOG.info("GET /cupboard/needs/id=" + id);
        try {
            Need need = cupboardDAO.getNeed(id);

            if (need != null)
                return new ResponseEntity<Need>(need, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Responds to the GET request for all {@linkplain Need needs}
     * 
     * @return ResponseEntity with array of {@link Need needs} objects (may be empty) and
     * HTTP status of OK<br> 
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/needs")
    @RequiresPermission(permissionType = PermissionType.SHARED)
    public ResponseEntity<Need[]> getNeeds() {
        LOG.info("GET /cupboard/needs");
        try {
    
            Need[] needs = cupboardDAO.getNeeds();    
            return new ResponseEntity<Need[]>(needs, HttpStatus.OK);
        
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Responds to the GET request for all {@linkplain Need needs} whose name contains the given text
     * 
     * @param name contains the text used in the search
     * 
     * @return ResponseEntity with array of {@link Need need} objects, may be empty
     * with HTTP status of OK
     * <br>
     * ResponseEntity with HTT{ status of INTERNAL_SERVER_ERROR otherwise}
     */
    @GetMapping("/needs/search")
    @RequiresPermission(permissionType = PermissionType.SHARED)
    public ResponseEntity<Need[]> searchNeeds(@RequestParam String name) {
        LOG.info("GET /cupboard/needs/search" + name);
        try {
           
            Need[] needs = cupboardDAO.findNeeds(name);
            return new ResponseEntity<Need[]>(needs, HttpStatus.OK);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Creates a {@linkplain Need need} with the provided need object
     * 
     * @param need - The {@link Need need} to create
     * 
     * @return ResponseEntity with created {@link Need need} object and HTTP status of CREATED<br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("/create")
    @RequiresPermission(permissionType = PermissionType.ADMIN)
    public ResponseEntity<Need> addNeed(@RequestBody Need need) {
        LOG.info("POST /cupboard/needs " + need);
        LOG.info("" + need.isListed());
        try {
            Need newNeed = cupboardDAO.createNeed(need);
            return new ResponseEntity<Need>(newNeed, HttpStatus.CREATED);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@linkplain Need need} with the given id
     * 
     * @param id The id of the {@link Need need} to be deleted
     * 
     * @return ResponseEntity HTTP status of OK if deleted
     * ResponseEntity with HTTP status of NOT_FOUND if not found
     * ResponstEntity with HTTP status of INTERNAL_SERVER_Error otherwise
     */
    @DeleteMapping("/remove/id={id}")
    @RequiresPermission(permissionType = PermissionType.ADMIN)
    public ResponseEntity<Need> removeNeed(@PathVariable int id) {
        LOG.info("DELETE /cupboard/remove/id=" + id);
        try {
            Need removed = cupboardDAO.deleteNeed(id);

            if(removed != null) 
                return new ResponseEntity<>(removed,HttpStatus.OK);
            else 
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the {@linkplain Need need} with the provided {@linkplain Need need} object if it exists
     * 
     * @param need The {@link Need need} to update
     * @return ResponseEntity with updated {@link Need need} object and HTTP status OK
     * ResponseEntity with HTTP status of NOT_FOUND if not found
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("/needs/update")
    @RequiresPermission(permissionType = PermissionType.ADMIN)
    public ResponseEntity<Need> updateNeed(@RequestBody Need need) {
        LOG.info("PUT /cupboard/needs/update " + need);
        try {
            Need updatedNeed = cupboardDAO.updateNeed(need);
            if(updatedNeed != null)
                return new ResponseEntity<>(updatedNeed, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Toggles the listing status of the {@linkplain Need need} with the provided {@linkplain Need need} object if it exists
     * 
     * @param need The {@link Need need} to update
     * @return ResponseEntity with updated {@link Need need} object and HTTP status OK
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("/needs/listing")
    @RequiresPermission(permissionType = PermissionType.ADMIN)
    public ResponseEntity<Need> updateListing(@RequestBody Need need) {
        LOG.info("PUT /cupboard/needs/listing " + need);
        try {
            Need updatedNeed = cupboardDAO.updateListingStatus(need);
            return new ResponseEntity<>(updatedNeed, HttpStatus.OK);
        
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves the current listing status of the {@linkplain Need need} with the provided ID if it exists
     * 
     * @param id The ID of the {@link Need need} whoms status is being checked
     * @return ResponseEntity with boolean listing status and HTTP status OK
     * ResponseEntity with HTTP status of NOT_FOUND if need cannot be found
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("/needs/listed/id={id}")
    @RequiresPermission(permissionType = PermissionType.SHARED)
    public ResponseEntity<Boolean> getListing(@PathVariable int id) {
        LOG.info("GET /cupboard/needs/listed/id=" + id);
        try {
            boolean status = cupboardDAO.getListingStatus(id);

            if (status)
                return new ResponseEntity<>(status, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
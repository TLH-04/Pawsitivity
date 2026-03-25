package com.ufund.api.ufundapi.controller;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.Basket;
import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.request.BasketRequest;
import com.ufund.api.ufundapi.model.user.permission.PermissionType;
import com.ufund.api.ufundapi.model.user.permission.RequiresPermission;
import com.ufund.api.ufundapi.service.BasketService;

/**
 * 
 * Handles the REST API requests for the Basket resource
 *
 * {@literal @}RestController Spring annotation identifies this class as a REST API
 * method handler to the Spring framework
 * 
 * @author Team 2
 */

@CrossOrigin(
    origins = "http://localhost:4200",
    allowCredentials = "true"
)
@RestController
@RequestMapping("basket")
public class BasketController {
    private static final Logger LOG = Logger.getLogger(BasketController.class.getName());
    private final BasketService basketService;


    /**
     * Creates a REST API controller to respond to requests
     * 
     * @param basketService The {@link BasketService Service Object}
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public BasketController(BasketService basketService){
        this.basketService = basketService;
    }


    /**
     * Responds to GET request for all {@linkplain Need needs} within a basket
     * 
     * @param sessionId the session of the user
     * 
     * @return ResponseEntity with list of {@link Need needs} objects (may be empty) and
     * HTTP status of OK<br>
     * 
     * ResponseEntity with HTTP status 500 (INTERNAL_SERVER_ERROR) otherwise
     */
    @GetMapping("")
    @RequiresPermission(permissionType = PermissionType.HELPER)
    public ResponseEntity<Basket> getBasket(@CookieValue("SessionId") String sessionId){
        LOG.info("GET /basket");
        try {
            Basket basket = basketService.getUserBasket(sessionId);
            return new ResponseEntity<>(basket, HttpStatus.OK);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    

    /**
     * Responds to POST request for adding a {@linkplain Need need} to basket
     * 
     * @param sessionId The sessionId cookie required for the operation
     * @param request The basket request {@link BasketRequest} used to preform CRUD operations
     * 
     * @return ResponseEntity of HTTP status 200 (OK) successfully adding
     * 
     * ResponseEntity of HTTP status 409 (CONFLICT) upon addition conflict
     * 
     * ResponseEntity with HTTP status 404 (NOT_FOUND) otherwise
     */
    @PostMapping("/need")
    @RequiresPermission(permissionType = PermissionType.HELPER)
    public ResponseEntity<Void> addNeed(@CookieValue("SessionId") String sessionId, @RequestBody BasketRequest request){
        int needID = request.getId();
        int amount = request.getAmount();
        String logmessage = "POST /basket/?add=" + needID + "&amount=" + amount;
        LOG.info(logmessage);
        try {
            Boolean status = basketService.addNeed(sessionId,needID, amount);
            if(!status){
                LOG.log(Level.INFO, "Entered Need Already Exists Within Basket");
            }
            return status ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Responds to DELETE request for removeing a {@linkplain Need need} from basket
     * 
     * @param sessionId The sessionId cookie required for the operation
     * @param request The basket request {@link BasketRequest} used to preform CRUD operations
     * 
     * @return ResponseEntity of HTTP status 200 (OK) successfully removing
     * 
     * ResponseEntity with HTTP status 500 (INTERNAL_SERVER_ERROR) otherwise
     */
    @PutMapping("/need")
    @RequiresPermission(permissionType = PermissionType.HELPER)
    public ResponseEntity<Need> removeNeed(@CookieValue("SessionId") String sessionId, @RequestBody BasketRequest request){
        int id = request.getId();
        int amount = request.getAmount();
        String logmessage = "DELETE /basket | id: " + id + " , amount: " + amount;
        LOG.info(logmessage);

        try {
            Need removedNeed = this.basketService.removeNeed(sessionId,id,amount);
            return new ResponseEntity<>(removedNeed, HttpStatus.OK);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to POST request for checkout out your basket
     * 
     * @param sessionId The sessionId cookie required for the operation
     * @return ResponseEntity of HTTP status 200 (OK) successfully checkout
     * 
     * 
     * ResponseEntity with HTTP status 500 (INTERNAL_SERVER_ERROR) otherwise
     */
    @PostMapping("/checkout")
    @RequiresPermission(permissionType = PermissionType.HELPER)
    public ResponseEntity<Void> checkout(@CookieValue("SessionId") String sessionId){
        LOG.info("POST /basket Attempt Checkout");

        try {
            Boolean status = this.basketService.userCheckout(sessionId);
            return status ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } 
    }


    
}

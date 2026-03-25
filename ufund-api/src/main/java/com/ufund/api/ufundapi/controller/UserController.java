package com.ufund.api.ufundapi.controller;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.permission.PermissionType;
import com.ufund.api.ufundapi.model.user.permission.RequiresPermission;
import com.ufund.api.ufundapi.service.UserService;
import com.ufund.api.ufundapi.model.user.User;

/**
 * Handles the REST API requests for the user resource
 * <p>
 * {@literal @}RestController Spring annotation identifies this class as a REST API
 * method handler to the Spring framework
 * 
 * @author Jason Ugbaja
 */


@CrossOrigin(
    origins = "http://localhost:4200",
    allowCredentials = "true"
)
@RestController()
@RequestMapping("users")
public class UserController {
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    private final UserService userService;
   

     /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param userService The {@link UserService} to perform CRUD operations
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Responds to the POST request for all {@linkplain LoginRequest loginRequest}
     * @param loginRequest the login request of the user attempting to login
     * @return ResponseEntity with String with the status of creating a new user
     * HTTP status of OK<br> 
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("/create/helper")
    public ResponseEntity<String> createHelper(@RequestBody LoginRequest loginRequest) {
        LOG.info("POST create new user ");
        
        LoginResult status = userService.createUser(loginRequest.getUsername(),loginRequest.getPassword());
        LOG.info(status.toString());
        if(status == LoginResult.SUCCESS)
            return new ResponseEntity<>(loginRequest.getUsername(),HttpStatus.CREATED);
        else
            return new ResponseEntity<>(loginRequest.getUsername(), HttpStatus.CONFLICT); 
    }

    /**
     * Responds to the POST request for Super Admin {@linkplain LoginRequest loginRequest}
     * @param loginRequest the login request of the user attempting to login
     * @return ResponseEntity with String with the status of creating a new user
     * HTTP status of OK<br> 
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("/create/admin")
    @RequiresPermission(permissionType = PermissionType.SUPER)
    public ResponseEntity<String> createAdmin(@RequestBody LoginRequest loginRequest) {
        LOG.info("POST create new user ");
        
        LoginResult status = userService.createAdmin(loginRequest.getUsername(),loginRequest.getPassword());
        LOG.info(status.toString());
        if(status == LoginResult.SUCCESS){
            return new ResponseEntity<>(loginRequest.getUsername(),HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(loginRequest.getUsername(), HttpStatus.CONFLICT);  
        } 
    }
    
    
    /**
     * Responds to the GET request  
     * @return ResponseEntity with User[] all stored users
     */
    @GetMapping("/all")
    public ResponseEntity<User[]> getAllUsers() {
        return new ResponseEntity<User[]>(userService.getAllUsers(), HttpStatus.OK);
    }

}

package com.ufund.api.ufundapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ufund.api.ufundapi.model.chat.Message;
import com.ufund.api.ufundapi.model.user.permission.PermissionType;
import com.ufund.api.ufundapi.model.user.permission.RequiresPermission;
import com.ufund.api.ufundapi.service.AuthService;
import com.ufund.api.ufundapi.service.MessageService;


/**
 * Handles the REST API requests for message resources.
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
@RequestMapping("/chat")
public class MessageController {

    private static final Logger LOG = Logger.getLogger(MessageController.class.getName());
    
    private final MessageService msgService;
    private final AuthService authService;

    /**
     * Creates a REST API controller to respond to requests.
     * 
     * @param msgService The {@link MessageService} to perform CRUD operations
     * @param authService The {@link AuthService} to perform CRUD operations
     */
    public MessageController(MessageService msgService, AuthService authService) {
        this.msgService = msgService;
        this.authService = authService;
    }

    /**
     * Responds to the POST request for all messages.
     * 
     * @param body The message content
     * @param sessionId The session id value for the user logged in
     * @return ResponseEntity with the {@link Message message} created with
     * HTTP status of OK<br> 
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping("/messages")
    @RequiresPermission(permissionType = PermissionType.ADMIN)
    public ResponseEntity<Message> post(@RequestBody String body, @CookieValue(value = "SessionId") String sessionId) {
        LOG.info("POST /messages " + body);
        
        String username = authService.getLoggedUser(sessionId).getUsername(); // get the username by using the sessionId
        try {
            Message msg = msgService.post(username, body);
            return new ResponseEntity<Message>(msg, HttpStatus.CREATED);
        } catch(Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The get request for the most recent {@link Message messages} given a number of messages to retrieve.
     * 
     * @param limit the number of {@linkplain Message messages} to retrieve
     * @return ResponseEntity with a List of {@link Message message} objects, may be empty
     * with HTTP status of OK
     * <br>
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise}
     */
    @GetMapping("/messages")
    @RequiresPermission(permissionType = PermissionType.ADMIN)
    public ResponseEntity<List<Message>> getAfter(@RequestParam(defaultValue = "50") int limit) {
        LOG.info("GET /messages");
        try {
            List<Message> messages = msgService.latest(limit);   
            return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
        
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }                        
    }
}

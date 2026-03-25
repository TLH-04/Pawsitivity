package com.ufund.api.ufundapi.controller;

import java.util.logging.Logger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.LoginResponse;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.User;
import com.ufund.api.ufundapi.model.user.UserType;
import com.ufund.api.ufundapi.model.user.permission.PermissionType;
import com.ufund.api.ufundapi.model.user.permission.RequiresPermission;
import com.ufund.api.ufundapi.service.AuthService;

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
@RequestMapping("login")
public class AuthController {
    private static final Logger LOG = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;
   

     /**
     * Creates a REST API controller to reponds to requests
     * 
     * @param authService The {@link AuthService} to perform CRUD operations
     * <br>
     * This dependency is injected by the Spring Framework
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    /**
     * Responds to the GET request for all {@linkplain  User user}
     * @param loginRequest the {@link LoginRequest} of the user
     * @return ResponseEntity with String with the status of the login
     * HTTP status of OK<br> 
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PostMapping()
    public ResponseEntity<LoginResult> login(@RequestBody LoginRequest loginRequest) {
        LOG.info("POST /login Logging in as " + loginRequest.getUsername());
       
        LoginResponse response = authService.login(loginRequest);
        LoginResult status = response.getLoginResult();
        switch (status) {
            case LoginResult.SUCCESS -> {
                UserType type = authService.getCurrentType(response.getSessionId());
                ResponseCookie sessionCookie =  ResponseCookie.from("SessionId",response.getSessionId()).path("/").build(); // SessionId cookie
                ResponseCookie permCookie =  ResponseCookie.from("Permission",type.toString()).path("/").build();// permission cookie 
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE, sessionCookie.toString());
                headers.add(HttpHeaders.SET_COOKIE, permCookie.toString());
                return new ResponseEntity<>(status,headers,HttpStatus.OK);
            }
            case LoginResult.INVALIDUSER -> {
                return new ResponseEntity<>(LoginResult.INVALIDUSER,HttpStatus.NOT_FOUND);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        
    }

     /**
     * Responds to the Getrequest for all 
     * @param sessionId the session of the user attempting to logout
     * @return ResponseEntity with Boolean of logging out
     * HTTP status of OK<br> 
     * ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @GetMapping("")
    public ResponseEntity<Boolean> logout(@CookieValue("SessionId") String sessionId)  {
        LOG.info("GET Logging out ");
        LoginResult status = authService.logout(sessionId);

        ResponseCookie sessionCookie =  ResponseCookie.from("SessionId","").path("/").maxAge(0).build(); // delete cookie
        ResponseCookie permCookie =  ResponseCookie.from("Permission","").path("/").maxAge(0).build(); // delete cookie
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, sessionCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, permCookie.toString());
        LOG.info(status.toString());
        if (status == LoginResult.SUCCESS){
            return new ResponseEntity<>(true,headers,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(headers,HttpStatus.BAD_REQUEST);
        }
    }

    
}

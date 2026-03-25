package com.ufund.api.ufundapi.service;

import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.UserType;
import com.ufund.api.ufundapi.model.user.permission.PermissionType;
import com.ufund.api.ufundapi.model.user.permission.RequiresPermission;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Handles permissions on controller methods
 * 
 * Verifies that user matches UserType in {@link RequiresPermission}
 * @author Jason Ugbaja
 */ 

@Aspect
@Service
public class PermissionService {
    private final AuthService authService;
    private static final Logger LOG = Logger.getLogger(PermissionService.class.getName());
    

    /**
     * Creates a permission handler to verify user permission before controller methods
     * 
     * @param authService The {@link AuthService} to perform CRUD operations
     */
    public PermissionService(AuthService authService){
        this.authService = authService;
    }

    /**
     * gets the SessionId cookie
     * @return SessionId cookie
     */
    private static Cookie getSessionCookie(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null){
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        if (request != null){
            Cookie[] cookies = request.getCookies();
        
            if (cookies != null){
                for (Cookie cookie : cookies){
                    if (cookie.getName().equals("SessionId")){
                        return cookie;
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * verify's if the client has the proper permission to run a controller method, if not return a response entity
     * @param joinPoint the proceeding join point
     * @param requiresPermission Annotation containing permission
     * @return ResponseEntity if user does not have the proper permission, otherwise returns joinPoint to proceed the function
     * @throws Throwable if issues with checking permission
     */
    @Around("@annotation(requiresPermission)")
    public Object verifyUser(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        PermissionType perm = requiresPermission.permissionType();
        String logmessage = "Requires Permission " + perm;
        LOG.info(logmessage);

        Cookie sessionIdCookie = getSessionCookie();

        HttpHeaders headers = new HttpHeaders();

        if (sessionIdCookie == null || !authService.isLoggedIn(sessionIdCookie.getValue())){ // delete session cookie if not logged in
            ResponseCookie sessionCookie =  ResponseCookie.from("SessionId","").maxAge(0).path("/").build();
            ResponseCookie permCookie =  ResponseCookie.from("Permission","").maxAge(0).path("/").build();
            headers.add(HttpHeaders.SET_COOKIE, sessionCookie.toString());
            headers.add(HttpHeaders.SET_COOKIE, permCookie.toString());
        }


        if (sessionIdCookie == null) {
            return new ResponseEntity<>(headers,HttpStatus.UNAUTHORIZED);
        }

       

        LoginResult status = authService.verifyUser(sessionIdCookie.getValue(), perm);
        
        if (status == LoginResult.NOTLOGGEDIN){
            return new ResponseEntity<>(headers,HttpStatus.UNAUTHORIZED);
        } else if (status == LoginResult.WRONGUSERTYPE){
            UserType type = authService.getCurrentType(sessionIdCookie.getValue());
            ResponseCookie permCookie =  ResponseCookie.from("Permission",type.toString()).build();
            headers.add(HttpHeaders.SET_COOKIE, permCookie.toString()); // Force update permission cookie incase client changes it.
            return new ResponseEntity<>(headers,HttpStatus.FORBIDDEN); 
        }

        

        return joinPoint.proceed();
    }
    
}

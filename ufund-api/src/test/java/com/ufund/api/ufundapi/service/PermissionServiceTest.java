package com.ufund.api.ufundapi.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ufund.api.ufundapi.model.request.LoginRequest;
import com.ufund.api.ufundapi.model.user.LoginResponse;
import com.ufund.api.ufundapi.model.user.LoginResult;
import com.ufund.api.ufundapi.model.user.permission.PermissionType;
import com.ufund.api.ufundapi.model.user.permission.RequiresPermission;
import com.ufund.api.ufundapi.persistence.UserDao;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {
    
    
    @Mock 
    private UserDao userDao;

    private AuthService authService;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private RequiresPermission requiresPermission;

    private PermissionService permissionService;

    private String sessionId;

    @BeforeEach
    void setUp() {
        
        authService = new AuthService(userDao);
        LoginResponse result = authService.login(new LoginRequest("Admin", "123"));
        sessionId = result.getSessionId();
        permissionService = new PermissionService(authService);
        MockitoAnnotations.openMocks(this);
        when(requiresPermission.permissionType()).thenReturn(PermissionType.HELPER);
    }



    @AfterEach
    void cleanup(){
        RequestContextHolder.resetRequestAttributes();
    }

    /*
    
     * Testing verifyUser() functionality.
     */

  
    @Test
    public void testVerify_fail() {

        Object result = assertDoesNotThrow(() -> permissionService.verifyUser(joinPoint, requiresPermission));
        assertEquals(HttpStatus.UNAUTHORIZED, ((ResponseEntity<?>) result).getStatusCode());
        
    }


    @Test
    public void testVerify_WrongCookies() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        when(requiresPermission.permissionType()).thenReturn(PermissionType.HELPER);
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("Usernam123123e","wrong user key");
        request.setCookies(cookies);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
     
      
        Object result = assertDoesNotThrow(() -> permissionService.verifyUser(joinPoint, requiresPermission));

        assertEquals(HttpStatus.UNAUTHORIZED, ((ResponseEntity<?>) result).getStatusCode());
        
        
    }

    @Test
    public void testVerify_EmptyCookies() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        when(requiresPermission.permissionType()).thenReturn(PermissionType.HELPER);
        Cookie[] cookies = new Cookie[0];
        request.setCookies(cookies);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
     
      
        Object result = assertDoesNotThrow(() -> permissionService.verifyUser(joinPoint, requiresPermission));

        assertEquals(HttpStatus.UNAUTHORIZED, ((ResponseEntity<?>) result).getStatusCode());
        
        
    }

    @Test
    public void testVerify_NoUsername() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);

        Object result =  assertDoesNotThrow(()->permissionService.verifyUser(joinPoint, requiresPermission));
        assertEquals(HttpStatus.UNAUTHORIZED, ((ResponseEntity<?>) result).getStatusCode());
        
    }


     
    @Test
    public void testVerify_NotLogged() {
      
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(requiresPermission.permissionType()).thenReturn(PermissionType.HELPER);
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("SessionId",sessionId);
        request.setCookies(cookies);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
     
      
        Object result = assertDoesNotThrow(() -> permissionService.verifyUser(joinPoint, requiresPermission));

        assertEquals(HttpStatus.UNAUTHORIZED, ((ResponseEntity<?>) result).getStatusCode());
        

    }

    @Test
    public void testVerify_NotLoggedAdmin() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        when(requiresPermission.permissionType()).thenReturn(PermissionType.HELPER);
 
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("SessionId","Admin");
        request.setCookies(cookies);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    

        Object result = assertDoesNotThrow(() -> permissionService.verifyUser(joinPoint, requiresPermission));

        assertEquals(HttpStatus.UNAUTHORIZED, ((ResponseEntity<?>) result).getStatusCode());
        

    }


    @Test
    public void testVerify_WrongUserType(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("SessionId", sessionId));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

     
        when(requiresPermission.permissionType()).thenReturn(PermissionType.HELPER);

        
        AuthService spyService = spy(authService);
        PermissionService service = new PermissionService(spyService);

       

        
        Object result = assertDoesNotThrow(() ->service.verifyUser(joinPoint, requiresPermission));


        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED,((ResponseEntity<?>) result).getStatusCode());
    }

    
    @Test
    public void testVerify_Success() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("SessionId", "admin"));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

       
        when(requiresPermission.permissionType()).thenReturn(PermissionType.HELPER);


        AuthService spyService = spy(authService);
        PermissionService service = new PermissionService(spyService);


        doReturn(true).when(spyService).isLoggedIn("admin");
        doReturn(LoginResult.SUCCESS)
            .when(spyService).verifyUser("admin", PermissionType.HELPER);

      
        when(assertDoesNotThrow(() -> joinPoint.proceed())).thenReturn("WORKED FINALLY");

       
        Object result = assertDoesNotThrow(() -> service.verifyUser(joinPoint, requiresPermission));

   
        assertEquals("WORKED FINALLY", result);

    
        RequestContextHolder.resetRequestAttributes();

    }


}

package com.ufund.api.ufundapi.model.user;

/**
 * An interface representing a login response
 */
public interface LoginResponse {
    /**
     * gives login result of on a operation
     * @return The login result of on a operation
     */
    LoginResult getLoginResult(); 

    /**
     * Returns a session id if created or needed
     * @return The session id from the login response
     */
    String getSessionId();
}

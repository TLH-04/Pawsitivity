package com.ufund.api.ufundapi.model.user.permission;

/**
 * Enum to store permission types
 */
public enum PermissionType {
    /**
     * SHARED permission type for operations shared between Admin and Helper
     */
    SHARED,
    /**
     * SHARED permission type for operations ONLY for Helper
     */
    HELPER,
    /**
     * SHARED permission type for operations ONLY for Admin
     */
    ADMIN,
    /**
     * SHARED permission type for operations ONLY for Super Admin
     */
    SUPER
}

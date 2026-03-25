package com.ufund.api.ufundapi.model.user.permission;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tags a method with a required permission type 
 * 
 * @author Jason Ugbaja
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    /**
     * gets the required permission from the annotation
     * @return the permission type for a method
     */
    PermissionType permissionType() default PermissionType.SHARED;
}

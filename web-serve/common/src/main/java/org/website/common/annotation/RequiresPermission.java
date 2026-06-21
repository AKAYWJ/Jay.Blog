package org.website.common.annotation;

import java.lang.annotation.*;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    String value(); // 权限码，例如 user:list
}
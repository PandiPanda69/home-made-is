/**
 * 
 */
package fr.thedestiny.auth.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;

/**
 * @author Sébastien
 */
@With(SecurityAction.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Security {

	boolean logged() default true;

	boolean restrictedAccess() default false;
}

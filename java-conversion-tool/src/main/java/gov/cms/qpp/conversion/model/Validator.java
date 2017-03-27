package gov.cms.qpp.conversion.model;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates a class for use by validation in the validation registry.
 * 
 * @author Scott Fradkin
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Validator {

	String templateId();

	boolean required() default false;
}

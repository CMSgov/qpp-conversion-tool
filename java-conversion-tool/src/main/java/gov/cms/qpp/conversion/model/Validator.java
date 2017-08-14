package gov.cms.qpp.conversion.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates a class for use by validation in the validation registry.
 * 
 * @author Scott Fradkin
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Validator {
	TemplateId value();

	/**
	 * The program with which this validator is associated. {@link Program#ALL} by default.
	 *
	 * @return Program
	 */
	Program program() default Program.ALL;
	boolean required() default false;
}

package gov.cms.qpp.conversion.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This is the annotation to mark class implementations that should be
 * registered for encoding nodes into some output like JSON.
 *
 * @author David Uselmann
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Encoder {

	/**
	 * The param(s) is/are the string pattern(s) that the defined handler will
	 * act.
	 *
	 * @return
	 */
	TemplateId value();

	/**
	 * The program with which this encoder is associated. {@link Program#ALL} by default.
	 *
	 * @return Program
	 */
	Program program() default Program.ALL;
}

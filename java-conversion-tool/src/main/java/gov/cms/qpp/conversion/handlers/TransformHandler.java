package gov.cms.qpp.conversion.handlers;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * This is the annotation to mark class implementations that should be
 * registered for transforming a section for the XML document.
 * 
 * @author daviduselmann
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface TransformHandler {
	/**
	 * The param(s) is/are the string pattern(s) that the defined handler will act.
	 * @return
	 */
	String elementName();
	String templateId();
	
}

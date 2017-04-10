package gov.cms.qpp.conversion.model;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * This is the annotation to mark class implementations that should be
 * registered for transforming a section for the XML document.
 * 
 * @author David Uselmann
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface XmlDecoder {

	TemplateId templateId();

	boolean required() default false;
	
}
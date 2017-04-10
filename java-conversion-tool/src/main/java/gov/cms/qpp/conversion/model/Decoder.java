package gov.cms.qpp.conversion.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This is the annotation to mark class implementations that should be
 * registered for transforming a section of the XML document.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Decoder {
	/**
	 * An instance of the {@link gov.cms.qpp.conversion.model.TemplateId} enumeration.
	 *
	 * @return The template ID that can be decoded.
	 */
	TemplateId value();
}

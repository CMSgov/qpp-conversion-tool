package gov.cms.qpp.conversion.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This is the annotation to mark class implementations that should be
 * registered for transforming a section for the XML document.
 *
 * @author David Uselmann
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface XmlDecoderNew {
	/**
	 * The param(s) is/are the string pattern(s) that the defined handler will act.
	 *
	 * @return
	 */
	TemplateId value();
}

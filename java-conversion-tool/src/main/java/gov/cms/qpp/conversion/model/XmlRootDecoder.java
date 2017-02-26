package gov.cms.qpp.conversion.model;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * This is the annotation to mark class implementations that this is a root element decoder.
 * 
 * @author daviduselmann
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface XmlRootDecoder {
	/**
	 * The name of the root element.
	 * @return
	 */
	String rootElement();
}

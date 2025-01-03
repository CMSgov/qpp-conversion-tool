package gov.cms.qpp.test.annotations;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark a method as a JUnit 5 acceptance test.
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE, ANNOTATION_TYPE})
@Test
@Tag("integration")
public @interface IntegrationTest {
}

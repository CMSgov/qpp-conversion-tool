package gov.cms.qpp.test.annotations;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark a method as a parameterized JUnit 5 acceptance test.
 */
@Retention(RUNTIME)
@Target(METHOD)
@ParameterizedTest
@Tag("acceptance")
public @interface ParameterizedAcceptanceTest {
}

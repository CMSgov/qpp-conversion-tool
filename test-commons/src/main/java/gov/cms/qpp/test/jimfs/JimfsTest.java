package gov.cms.qpp.test.jimfs;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@ParameterizedTest
@MethodSource("jimfs")
@Retention(RUNTIME)
@Target(METHOD)
public @interface JimfsTest {

}

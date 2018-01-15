package gov.cms.qpp.test.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
@BeforeEach
@AfterEach
public @interface AroundEach {

}

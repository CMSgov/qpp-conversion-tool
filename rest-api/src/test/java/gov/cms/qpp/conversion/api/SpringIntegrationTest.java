package gov.cms.qpp.conversion.api;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import gov.cms.qpp.test.annotations.IntegrationTest;

@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@SpringTest
@IntegrationTest
public @interface SpringIntegrationTest {

}

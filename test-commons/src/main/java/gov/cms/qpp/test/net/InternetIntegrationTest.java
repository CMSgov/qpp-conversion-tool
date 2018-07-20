package gov.cms.qpp.test.net;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import gov.cms.qpp.test.annotations.IntegrationTest;

@InternetTest
@IntegrationTest
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface InternetIntegrationTest {

}

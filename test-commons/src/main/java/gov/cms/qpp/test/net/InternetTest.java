package gov.cms.qpp.test.net;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;

@Test
@ArgumentsSource(InternetArgumentSource.class)
@Tag("internet")
@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE})
public @interface InternetTest {

}

package gov.cms.qpp.conversion.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest(classes = RestApiApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public @interface SpringTest {

}

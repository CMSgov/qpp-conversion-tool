package gov.cms.qpp.conversion.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApiApplication.class)
public class RestApiApplicationTest {

	@Test
	public void contextLoads() {
	}

	@Test
	public void testMain() {
		RestApiApplication.main(new String[] {
			"--spring.main.web-environment=false"
		});
	}

}

package gov.cms.qpp.conversion.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class RestApiApplicationTest {

    @Test
    public void mainMethodExists() throws Exception {
        assertNotNull(RestApiApplication.class.getMethod("main", String[].class));
    }
}

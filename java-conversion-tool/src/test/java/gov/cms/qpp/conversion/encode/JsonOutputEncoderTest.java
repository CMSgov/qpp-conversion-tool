package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class JsonOutputEncoderTest {

	JsonOutputEncoder joe;

	@Before
	public void before() {
		joe = new JsonOutputEncoder() {
			@Override
			protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
				EncodeException ee = new EncodeException("thrown", new RuntimeException("test"));
				ee.setTemplateId("id");
				throw ee;
			}
		};
		Validations.init();
	}

	@Test
	public void testAddValidationAndGetValidations() {
		assertFalse(joe.validations().iterator().hasNext());
		joe.addValidation("id", new EncodeException("error", new RuntimeException("test")));
		joe.addValidation("id", "another");
		Iterator<String> validations = joe.validations().iterator();
		assertTrue(validations.hasNext());
		assertEquals("id - error", validations.next());
		assertEquals("id - another", validations.next());
	}

	@Test
	public void testAddValidationAndGetValidationById() {
		List<String> validations = joe.getValidationsById("id");
		assertEquals(null, validations);

		joe.addValidation("id", new EncodeException("err", new RuntimeException("test")));

		validations = joe.getValidationsById("id");
		assertNotNull(validations);
		assertEquals(1, validations.size());
		assertEquals("err", validations.get(0));
	}

	@Test
	public void testAddValidationByEncodeException() {
		assertFalse(joe.validations().iterator().hasNext());

		joe.encode((JsonWrapper) null, (Node) null); // the values are not used in the test

		Iterator<String> validations = joe.validations().iterator();
		assertTrue(validations.hasNext());
		assertEquals("id - thrown", validations.next());
	}
}

package gov.cms.qpp.conversion.model.error;


import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DetailTest {
	@Test
	public void stringRepresentation() {
		Detail objectUnderTest = new Detail("text", "path");

		assertThat("toString representation", objectUnderTest.toString(), is("Detail{message='text', path='path', value='null', type='null'}"));
	}

	@Test
	public void stringRepresentationWithValueAndType() {
		Detail objectUnderTest = new Detail("text", "path", "value", "type");

		assertThat("toString representation", objectUnderTest.toString(), is("Detail{message='text', path='path', value='value', type='type'}"));
	}

	@Test
	public void testEquals () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Detail detail = new Detail();
		Detail nullDetail = new Detail(null, null, null, null);
		Detail emptyValues = new Detail("","","","");
		String[] mutators = {"setMessage", "setPath", "setValue", "setType"};

		for (String methodName : mutators) {
			Method setter = Detail.class.getMethod(methodName, String.class);
			setter.invoke(detail, "meep");

			Detail empty = new Detail();
			Detail copy = new Detail(detail);

			assertEquals("detail should equal detail", detail, detail);
			assertEquals("Copied detail should equal original", detail, copy);
			assertNotEquals("Empty detail should not equal initialized detail", empty, detail);
			assertNotEquals("Empty values detail should not equal initialized detail", emptyValues, detail);
			assertNotEquals("Null detail should not equal initialized detail", nullDetail, detail);

			setter.invoke(nullDetail, "meep");
			setter.invoke(emptyValues, "meep");
		}
	}

	@Test
	public void moreEqualsTesting() {
		Detail detail = new Detail("meep","meeo","meep","meep");
		Detail nullDetail = new Detail(null,null,null,null);

		assertEquals("Null detail should equal Null detail", nullDetail, new Detail(nullDetail));
		assertNotEquals("detail should not equal null", detail, null);
		assertNotEquals("detail should not equal a non-Detail class", detail, "meep");
	}

	@Test
	public void testHashCode() {
		Detail emptyValues = new Detail("meep","meeo","meep","meep");
		Detail nullDetail = new Detail(null,null,null,null);
		assertEquals(emptyValues.hashCode(), -29068033);
		assertEquals(nullDetail.hashCode(), 0);
	}
}
package gov.cms.qpp.conversion.model.error;


import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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
	public void equalsContract() {
		EqualsVerifier.forClass(Detail.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}

//	@Test
//	public void testHashCode() {
//		Detail emptyValues = new Detail("meep","meeo","meep","meep");
//		Detail nullDetail = new Detail(null,null,null,null);
//		assertEquals(emptyValues.hashCode(), -29068033);
//		assertEquals(nullDetail.hashCode(), 0);
//	}
}
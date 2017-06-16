package gov.cms.qpp.conversion.encode.helper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class to test the Quality Measures Lookup helper
 */
public class QualityMeasuresLookupTest {
	@Test
	public void getMeasureIdExists() throws Exception {
		assertThat("Measure id exists in lookup",
				QualityMeasuresLookup.getMeasureId("40280381-51f0-825b-0152-22a1e7e81737"), is("CMS130v5"));
	}

	@Test
	public void getMeasureIdNotExists() throws Exception {
		String invalidValue = "invalidValue";
		assertThat("Measure id does not exists in lookup",
				QualityMeasuresLookup.getMeasureId(invalidValue), is(invalidValue));
	}

	@Test
	public void privateConstructorTest() throws Exception {
		// reflection concept to get constructor of a Singleton class.
		Constructor<QualityMeasuresLookup> constructor = QualityMeasuresLookup.class.getDeclaredConstructor();
		// change the accessibility of constructor for outside a class object creation.
		constructor.setAccessible(true);
		// creates object of a class as constructor is accessible now.
		QualityMeasuresLookup qualityMeasuresLookup = constructor.newInstance();
		// close the accessibility of a constructor.
		constructor.setAccessible(false);
		Assert.assertThat("Expect to have an instance here ", qualityMeasuresLookup, instanceOf(QualityMeasuresLookup.class));
	}
}
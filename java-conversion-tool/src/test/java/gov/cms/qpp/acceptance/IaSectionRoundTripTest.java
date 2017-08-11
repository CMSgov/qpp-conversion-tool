package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class IaSectionRoundTripTest {
	Path file;

	@Before
	public void setUp() {
		file = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
	}

	@Test
	public void testIaSectionConvertsIaCategory() throws IOException {
		Converter converter = new Converter(new PathQrdaSource(file));
		JsonWrapper qpp = converter.transform();
		String iaCategory = JsonHelper.readJsonAtJsonPath(qpp.toString(),
				"$.measurementSets[2].category", String.class);

		assertThat("Must contain a category", iaCategory, is("ia"));
	}

	@Test
	public void testIaSectionConvertsIaMeasureId() throws IOException {
		Converter converter = new Converter(new PathQrdaSource(file));
		JsonWrapper qpp = converter.transform();
		String iaMeasureId = JsonHelper.readJsonAtJsonPath(qpp.toString(),
				"$.measurementSets[2].measurements[0].measureId", String.class);

		assertThat("Must contain measure id", iaMeasureId, is("IA_EPA_3"));
	}

	@Test
	public void testIaSectionConvertsMeasurePerformed() throws IOException {
		Converter converter = new Converter(new PathQrdaSource(file));
		JsonWrapper qpp = converter.transform();
		Boolean measurePerformed = JsonHelper.readJsonAtJsonPath(qpp.toString(),
				"$.measurementSets[2].measurements[0].value", Boolean.class);

		assertTrue("Must contain a measure performed", measurePerformed);
	}
}

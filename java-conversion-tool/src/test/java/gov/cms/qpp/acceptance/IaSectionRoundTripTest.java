package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.ConversionFileWriterWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class IaSectionRoundTripTest {
	Path file;

	@Before
	public void setUp() {
		file = Paths.get("../qrda-files/valid-QRDA-III.xml");
	}

	@After
	public void cleanUp() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III.qpp.json"));
	}

	@Test
	public void testIaSectionConvertsIaCategory() throws IOException {
		new ConversionFileWriterWrapper(file).transform();
		String iaCategory = JsonHelper.readJsonAtJsonPath(Paths.get("valid-QRDA-III.qpp.json"),
				"$.measurementSets[2].category", String.class);

		assertThat("Must contain a category", iaCategory, is("ia"));
	}

	@Test
	public void testIaSectionConvertsIaMeasureId() throws IOException {
		new ConversionFileWriterWrapper(file).transform();
		String iaMeasureId = JsonHelper.readJsonAtJsonPath(Paths.get("valid-QRDA-III.qpp.json"),
				"$.measurementSets[2].measurements[0].measureId", String.class);

		assertThat("Must contain measure id", iaMeasureId, is("IA_EPA_1"));
	}

	@Test
	public void testIaSectionConvertsMeasurePerformed() throws IOException {
		new ConversionFileWriterWrapper(file).transform();
		Boolean measurePerformed = JsonHelper.readJsonAtJsonPath(Paths.get("valid-QRDA-III.qpp.json"),
				"$.measurementSets[2].measurements[0].value", Boolean.class);

		assertTrue("Must contain a measure performed", measurePerformed);
	}
}

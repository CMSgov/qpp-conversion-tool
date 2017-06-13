package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.ConversionFileWriterWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ReportingParametersActRoundTripTest {
	private static final Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");

	private final String SUCCESS_JSON = "valid-QRDA-III-latest.qpp.json";

	@Before
	public void setUp() throws IOException {
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(path);
		converterWrapper.transform();
	}

	@After
	public void deleteFilesIfExist() throws IOException {
		Files.deleteIfExists(Paths.get(SUCCESS_JSON));
	}

	@Test
	public void testQualityMeasuresContainsPerformanceStart() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(Paths.get(SUCCESS_JSON),
		"$.measurementSets[0].measurements[4].performanceStart", String.class);

		assertThat("", performanceStart, is("2017-01-01"));
	}

	@Test
	public void testQualityMeasuresContainsPerformanceEnd() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(Paths.get(SUCCESS_JSON),
				"$.measurementSets[0].measurements[4].performanceEnd", String.class);

		assertThat("", performanceStart, is("2017-12-31"));
	}

	@Test
	public void testAciSectionContainsPerformanceStart() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(Paths.get(SUCCESS_JSON),
				"$.measurementSets[1].measurements[3].performanceStart", String.class);

		assertThat("", performanceStart, is("2017-02-01"));
	}

	@Test
	public void testAciSectionContainsPerformanceEnd() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(Paths.get(SUCCESS_JSON),
				"$.measurementSets[1].measurements[3].performanceEnd", String.class);

		assertThat("", performanceStart, is("2017-05-31"));
	}

	@Test
	public void testIaContainsPerformanceStart() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(Paths.get(SUCCESS_JSON),
				"$.measurementSets[2].measurements[2].performanceStart", String.class);

		assertThat("", performanceStart, is("2017-01-01"));
	}

	@Test
	public void testIaContainsPerformanceEnd() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(Paths.get(SUCCESS_JSON),
				"$.measurementSets[2].measurements[2].performanceEnd", String.class);

		assertThat("", performanceStart, is("2017-04-30"));
	}
}

package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;

class ReportingParametersActRoundTripTest {

	private static final Path VALID_QRDA_III = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");

	private static String json;

	@BeforeAll
	public static void setUp() {
		Converter converter = new Converter(new PathQrdaSource(VALID_QRDA_III));
		JsonWrapper qpp = converter.transform();
		json = qpp.toString();
	}

	@Test
	void testQualityMeasuresContainsPerformanceStart() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
		"$.measurementSets[0].performanceStart", String.class);

		assertThat(performanceStart).isEqualTo("2017-01-01");
	}

	@Test
	void testQualityMeasuresContainsPerformanceEnd() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[0].performanceEnd", String.class);

		assertThat(performanceStart).isEqualTo("2017-12-31");
	}

	@Test
	void testAciSectionContainsPerformanceStart() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[1].performanceStart", String.class);

		assertThat(performanceStart).isEqualTo("2017-02-01");
	}

	@Test
	void testAciSectionContainsPerformanceEnd() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[1].performanceEnd", String.class);

		assertThat(performanceStart).isEqualTo("2017-05-31");
	}

	@Test
	void testIaContainsPerformanceStart() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[2].performanceStart", String.class);

		assertThat(performanceStart).isEqualTo("2017-01-01");
	}

	@Test
	void testIaContainsPerformanceEnd() throws IOException {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[2].performanceEnd", String.class);

		assertThat(performanceStart).isEqualTo("2017-04-30");
	}
}

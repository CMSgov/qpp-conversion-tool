package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.TypeRef;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;

class ReportingParametersActRoundTripTest {

	private static final Path VALID_QRDA_III = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");

	private static String json;

	@BeforeAll
	public static void setUp() {
		Converter converter = new Converter(new PathSource(VALID_QRDA_III));
		JsonWrapper qpp = converter.transform();
		json = qpp.toString();
	}

	@Test
	void testQualityMeasuresContainsPerformanceStart() {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
		"$.measurementSets[0].measurements[0].performanceStart", new TypeRef<String>() { });

		assertThat(performanceStart).isEqualTo("2017-01-01");
	}

	@Test
	void testQualityMeasuresContainsPerformanceEnd() {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[0].measurements[0].performanceEnd", new TypeRef<String>() { });

		assertThat(performanceStart).isEqualTo("2017-12-31");
	}

	@Test
	void testAciSectionContainsPerformanceStart() {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[1].performanceStart", new TypeRef<String>() { });

		assertThat(performanceStart).isEqualTo("2017-02-01");
	}

	@Test
	void testAciSectionContainsPerformanceEnd() {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[1].performanceEnd", new TypeRef<String>() { });

		assertThat(performanceStart).isEqualTo("2017-05-31");
	}

	@Test
	void testIaContainsPerformanceStart() {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[2].measurements[0].performanceStart", new TypeRef<String>() { });

		assertThat(performanceStart).isEqualTo("2017-01-01");
	}

	@Test
	void testIaContainsPerformanceEnd() {
		String performanceStart = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[2].measurements[0].performanceEnd", new TypeRef<String>() { });

		assertThat(performanceStart).isEqualTo("2017-04-30");
	}
}

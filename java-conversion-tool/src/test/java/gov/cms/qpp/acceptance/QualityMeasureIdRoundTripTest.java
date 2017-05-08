package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.TransformationStatus;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class QualityMeasureIdRoundTripTest {
	public static final Path JUNK_QRDA3_FILE = Paths.get("src/test/resources/negative/junk_in_quality_measure.xml");

	@After
	public void deleteJsonFile() throws IOException {
		Files.deleteIfExists(Paths.get("junk_in_quality_measure.qpp.json"));
	}

	@Test
	public void testRoundTripForQualityMeasureId() throws IOException {
		Converter converter = new Converter(JUNK_QRDA3_FILE);
		TransformationStatus transformStatus = converter.transform();

		assertThat("Converting the junk QRDA3 file should have been successful.", transformStatus,
			is(TransformationStatus.SUCCESS));

		List<Map<String, ?>> qualityMeasures = JsonHelper.readJsonAtJsonPath(Paths.get("junk_in_quality_measure.qpp.json"),
			"$.measurementSets[?(@.category=='quality')].measurements[*]", List.class);

		assertThat("There should still be a quality measure even with the junk stuff in quality measure.",
			qualityMeasures, hasSize(1));
		assertThat("The measureId in the quality measure should still populate given the junk stuff in the measure.",
			qualityMeasures.get(0).get("measureId"), is("CMS165v5"));
	}
}

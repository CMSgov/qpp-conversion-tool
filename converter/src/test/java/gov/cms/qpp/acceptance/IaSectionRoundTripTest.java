package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;

class IaSectionRoundTripTest {

	Path file;

	@BeforeEach
	void setUp() {
		file = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
	}

	@Test
	void testIaSectionConvertsIaCategory() throws IOException {
		Converter converter = new Converter(new PathSource(file));
		JsonWrapper qpp = converter.transform();
		String iaCategory = JsonHelper.readJsonAtJsonPath(qpp.toString(),
				"$.measurementSets[2].category", String.class);

		assertWithMessage("Must contain a category")
				.that(iaCategory)
				.isEqualTo("ia");
	}

	@Test
	void testIaSectionConvertsIaMeasureId() throws IOException {
		Converter converter = new Converter(new PathSource(file));
		JsonWrapper qpp = converter.transform();
		String iaMeasureId = JsonHelper.readJsonAtJsonPath(qpp.toString(),
				"$.measurementSets[2].measurements[0].measureId", String.class);

		assertWithMessage("Must contain measure id")
				.that(iaMeasureId)
				.isEqualTo("IA_EPA_3");
	}

	@Test
	void testIaSectionConvertsMeasurePerformed() throws IOException {
		Converter converter = new Converter(new PathSource(file));
		JsonWrapper qpp = converter.transform();
		Boolean measurePerformed = JsonHelper.readJsonAtJsonPath(qpp.toString(),
				"$.measurementSets[2].measurements[0].value", Boolean.class);

		assertWithMessage("Must contain a measure performed")
			.that(measurePerformed)
			.isTrue();
	}
}

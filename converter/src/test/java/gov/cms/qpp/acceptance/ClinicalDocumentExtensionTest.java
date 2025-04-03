package gov.cms.qpp.acceptance;

import gov.cms.qpp.CacheBuilder;
import gov.cms.qpp.model.CacheType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.DocumentationReference;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.truth.Truth.assertThat;

class ClinicalDocumentExtensionTest {

	private static final Path DIR = Path.of("src/test/resources/");
	private static final Path VALID = DIR.resolve("Qrda_CatIII_Provider.xml");
	private static final Path INVALID = DIR.resolve("negative/Qrda_CatIII_Provider_invalid_CD_extension.xml");

	@BeforeAll
	static void initMockApmIds() throws IOException {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.TEST_MEASURE_DATA);
	}

	@AfterAll
	static void teardown() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test
	void valid() {
		Assertions.assertAll(() -> convert(VALID));
	}

	@Test
	void invalid() {
		Assertions.assertThrows(TransformException.class, () -> convert(INVALID));
	}

	@Test
	void invalidMessage() throws IOException {
		try {
			convert(INVALID);
		} catch (TransformException ex) {
			Detail detail = ex.getDetails().getErrors().get(0).getDetails().get(0);
			assertThat(detail.getMessage()).isEqualTo(ProblemCode.NOT_VALID_QRDA_DOCUMENT
				.format(Context.REPORTING_YEAR, DocumentationReference.CLINICAL_DOCUMENT).getMessage());
		}
	}

	private JsonWrapper convert(Path location) throws IOException {
		ApmEntityIds apmEntityIds = CacheBuilder.getEntityIds(CacheType.ApmEntityIds);
		Converter converter = new Converter(new PathSource(location), new Context(apmEntityIds));
		return converter.transform();
	}
}
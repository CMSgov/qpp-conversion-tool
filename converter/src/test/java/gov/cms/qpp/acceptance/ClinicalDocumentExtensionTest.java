package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

class ClinicalDocumentExtensionTest {

	private static final Path DIR = Paths.get("src/test/resources/");
	private static final Path VALID = DIR.resolve("Qrda_CatIII_Provider.xml");
	private static final Path INVALID = DIR.resolve("negative/Qrda_CatIII_Provider_invalid_CD_extension.xml");

	@Test
	void valid() {
		Assertions.assertAll(() -> convert(VALID));
	}

	@Test
	void invalid() {
		Assertions.assertThrows(TransformException.class, () -> convert(INVALID));
	}

	@Test
	void invalidMessage() {
		try {
			convert(INVALID);
		} catch (TransformException ex) {
			Detail detail = ex.getDetails().getErrors().get(0).getDetails().get(0);
			assertThat(detail.getMessage()).isEqualTo(ErrorCode.NOT_VALID_QRDA_DOCUMENT.getMessage());
		}
	}

	private JsonWrapper convert(Path location) {
		Converter converter = new Converter(new PathSource(location));
		return converter.transform();
	}
}
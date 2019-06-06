package gov.cms.qpp.acceptance;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.TransformException;

class CpcTest {

	private static final String CPC_FILE = "src/test/resources/cpc1.xml";

	@Test
	void historicalFull() {
		Assertions.assertThrows(TransformException.class, () -> {
			Converter converter = new Converter(new PathSource(Paths.get(CPC_FILE)));
			converter.transform();
		});
	}

}
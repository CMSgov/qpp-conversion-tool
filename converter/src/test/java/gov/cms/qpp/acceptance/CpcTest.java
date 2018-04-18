package gov.cms.qpp.acceptance;

import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.segmentation.QrdaScope;

class CpcTest {

	private static final String CPC_FILE = "src/test/resources/cpc1.xml";

	@Test
	void historicalAciSectionScope() {
		run("ACI_SECTION");
	}

	@Test
	void historicalIaSectionScope() {
		run("IA_SECTION");
	}

	@Test
	void historicalIAMeasurePerformedScope() {
		run("IA_MEASURE");
	}

	@Test
	void historicalClinicalDocumentScope() {
		Assertions.assertThrows(TransformException.class, () -> run("CLINICAL_DOCUMENT"));
	}

	@Test
	void historicalFull() {
		Assertions.assertThrows(TransformException.class, () -> {
			Converter converter = new Converter(new PathSource(Paths.get(CPC_FILE)));
			converter.transform();
		});
	}

	private void run(String type) {
		Converter converter = new Converter(new PathSource(Paths.get(CPC_FILE)));
		converter.getContext().setHistorical(true);
		converter.getContext().setScope(Collections.singleton(QrdaScope.getInstanceByName(type)));
		converter.transform();
	}
}
package gov.cms.qpp.acceptance;

import com.google.common.collect.Sets;
import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Paths;

public class CpcTest extends BaseTest {

	private static final String CPC_FILE = "src/test/resources/cpc1.xml";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void historicalAciSectionScope() {
		run("ACI_SECTION");
	}

	@Test
	public void historicalIaSectionScope() {
		run("IA_SECTION");
	}

	@Test
	public void historicalIAMeasurePerformedScope() {
		run("IA_MEASURE");
	}

	@Test
	public void historicalClinicalDocumentScope() {
		thrown.expect(TransformException.class);
		run("CLINICAL_DOCUMENT");
	}

	private void run(String type) {
		Converter converter = new Converter(new PathQrdaSource(Paths.get(CPC_FILE)));
		Converter.setHistorical(true);
		Converter.setScope(Sets.newHashSet(QrdaScope.getInstanceByName(type)));
		converter.transform();
	}
}
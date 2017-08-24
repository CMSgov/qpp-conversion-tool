package gov.cms.qpp.acceptance.cpc;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CpcApmTest {

	private static final String DIR = "src/test/resources/cpc_plus/";
	private static final Path FAIL_ABSENT_PARENT =
			Paths.get(DIR + "CPCPlus_PracID_Not_Provided_ SampleQRDA-III.xml");
	private static final Path FAIL_ABSENT =
			Paths.get(DIR + "CPCPlus_PracID_Null_SampleQRDA-III.xml");

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testPracticeIdFailureAbsentId() {
		thrown.expect(TransformException.class);
		Converter converter = new Converter(new PathQrdaSource(FAIL_ABSENT));
		converter.transform();
	}

	@Test
	public void testPracticeIDFailureAbsentIdParent() {
		thrown.expect(TransformException.class);
		Converter converter = new Converter(new PathQrdaSource(FAIL_ABSENT_PARENT));
		converter.transform();
	}
}


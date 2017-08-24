package gov.cms.qpp.acceptance.cpc;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Paths;

import static junit.framework.TestCase.fail;

public class CpcProgramNameTest {
	private static final String DIR = "src/test/resources/cpc_plus/";
	private static final String FAILURE = DIR + "CPCPlus_CMSPrgrm_DiffCode_SampleQRDA-III.xml";
	private static final String LOWER = DIR + "CPCPlus_CMSPrgrm_LowerCase_SampleQRDA-III.xml";
	private static final String UPPER = DIR + "CPCPlus_CMSPrgrm_UpperCase_SampleQRDA-III.xml";
	private static final String MIXED = DIR + "CPCPlus_CMSPrgrm_MixedCase_SampleQRDA-III.xml";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testProgramNameFailure() {
		thrown.expect(TransformException.class);
		Converter converter = new Converter(new PathQrdaSource(Paths.get(FAILURE)));
		converter.transform();
	}

	@Test
	public void testProgramNameSuccessUpper() {
		try {
			Converter converter = new Converter(new PathQrdaSource(Paths.get(UPPER)));
			converter.transform();
		} catch (TransformException ex) {
			fail("Upper case name program name should not trigger a TransformException");
		}
	}

	@Test
	public void testProgramNameSuccessLower() {
		try {
			Converter converter = new Converter(new PathQrdaSource(Paths.get(LOWER)));
			converter.transform();
		} catch (TransformException ex) {
			fail("Lower case name program name should not trigger a TransformException");
		}
	}

	@Test
	public void testProgramNameSuccessMixed() {
		try {
			Converter converter = new Converter(new PathQrdaSource(Paths.get(MIXED)));
			converter.transform();
		} catch (TransformException ex) {
			fail("Mixed case name program name should not trigger a TransformException");
		}
	}
}

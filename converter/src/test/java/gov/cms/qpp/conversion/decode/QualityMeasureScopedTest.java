package gov.cms.qpp.conversion.decode;


import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import gov.cms.qpp.conversion.xml.XmlException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QualityMeasureScopedTest {
	QualityMeasureIdDecoder objectUnderTest;
	private static String location = "src/test/resources/fixtures/qppct298/cms137v5.xml";

	@Before
	public void setup() {
		objectUnderTest = new QualityMeasureIdDecoder(new Context());
	}

	@Test
	public void internalDecodeValidMeasure137V5Ipop() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long ipops = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals("IPOP"))
				.count();

		assertEquals("Valid CMS137v5 measure should have 2 IPOP values", ipops, 2);
	}

	@Test
	public void internalDecodeValidMeasure137V5IpopStrata() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		Supplier<Stream<Node>> setup = () -> pluckDescendants(result,
					TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2,
					TemplateId.MEASURE_DATA_CMS_V2)
					.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals("IPOP"))
					.flatMap(ipop -> pluckDescendants(ipop, TemplateId.REPORTING_STRATUM_CMS));

		List<String> strata = Arrays.asList("EFB5B088-CE10-43DE-ACCD-9913B7AC12A2",
		"94B9555F-8700-45EF-B69F-433EBEDE8051",
		"ABC5631A-81C0-45C9-9306-716EAE39CDDB",
		"2654804B-E6DA-4401-AA8B-1FEEACC0C259");

		assertEquals("Valid CMS137v5 measure's IPOP values should have 4 strata", setup.get().count(), 4);
		assertTrue(setup.get().map(node -> node.getValue(StratifierDecoder.STRATIFIER_ID)).collect(Collectors.toSet()).containsAll(strata));
	}

	@Test
	public void internalDecodeValidMeasure137V5Denom() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long denom = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals("DENOM"))
				.count();

		assertEquals("Valid CMS137v5 measure should have 2 DENOM values", denom, 2);
	}

	@Test
	public void internalDecodeValidMeasure137V5Denex() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long denex = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals("DENEX"))
				.count();

		assertEquals("Valid CMS137v5 measure should have 2 DENEX values", denex, 2);
	}

	@Test
	public void internalDecodeValidMeasure137V5Numer() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long numer = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals("NUMER"))
				.count();

		assertEquals("Valid CMS137v5 measure should have 2 DENEX values", numer, 2);
	}

	@Test
	public void internalDecodeValidMeasure137V5() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long numer = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals("NUMER"))
				.count();

		assertEquals("Valid CMS137v5 measure should have 2 DENEX values", numer, 2);
	}

	private Stream<Node> pluckDescendants(Node parent, TemplateId... path) {
		Stream<Node> tier = Stream.of(parent);
		for (TemplateId nextTier : Arrays.asList(path)) {
			tier = tier.flatMap(node -> node.getChildNodes(nextTier));
		}
		return tier;
	}

	private Node scopedConversion(QrdaScope testSection, String path) {
		Converter converter = new Converter(new PathQrdaSource(Paths.get(path)));
		converter.getContext().setScope(Sets.newHashSet(testSection));
		converter.transform();
		return converter.getDecoded();
	}

}

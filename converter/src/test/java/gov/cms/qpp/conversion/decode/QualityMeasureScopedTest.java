package gov.cms.qpp.conversion.decode;


import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import gov.cms.qpp.conversion.xml.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

class QualityMeasureScopedTest {
	private static String location = "src/test/resources/fixtures/qppct298/cms137v5.xml";


	@Test
	void internalDecodeValidMeasure137V5Ipop() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long ipops = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals(SubPopulations.IPOP))
				.count();

		assertThat(ipops).isEqualTo(2);
	}

	@Test
	void internalDecodeValidMeasure137V5Denom() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long denom = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals(SubPopulations.DENOM))
				.count();

		assertThat(denom).isEqualTo(2);
	}

	@Test
	void internalDecodeValidMeasure137V5Denex() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long denex = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals(SubPopulations.DENEX))
				.count();

		assertThat(denex).isEqualTo(2);
	}

	@Test
	void internalDecodeValidMeasure137V5Numer() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, location);
		long numer = pluckDescendants(result, TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, TemplateId.MEASURE_DATA_CMS_V2)
				.filter(node -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals(SubPopulations.NUMER))
				.count();

		assertThat(numer).isEqualTo(2);
	}

	private Stream<Node> pluckDescendants(Node parent, TemplateId... path) {
		Stream<Node> tier = Stream.of(parent);
		for (TemplateId nextTier : Arrays.asList(path)) {
			tier = tier.flatMap(node -> node.getChildNodes(nextTier));
		}
		return tier;
	}

	private Node scopedConversion(QrdaScope testSection, String path) {
		Converter converter = new Converter(new PathSource(Paths.get(path)));
		converter.getContext().setScope(Sets.newHashSet(testSection));
		converter.transform();
		return converter.getReport().getDecoded();
	}

}

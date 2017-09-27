package gov.cms.qpp.conversion.validate;


import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import gov.cms.qpp.conversion.xml.XmlException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;


public class QualityMeasureScopedValidatonTest {
	private static Path baseDir = Paths.get("src/test/resources/fixtures/qppct298/");

	@Test
	public void validateCms137V5() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		Set<Detail> details = validateNode(result);

		assertThat("Valid CMS137v5 markup should not result in errors", details.size(), is(0));
	}

	@Test
	public void validateCms137V5FailMissingIpopStrata() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		removeMeasureStrata(result, "IPOP");
		Set<Detail> details = validateNode(result);

		assertThat("Valid CMS137v5 markup should result in errors", details.size(), greaterThan(0));
	}

	private void removeMeasureStrata(Node parent, String type) {
		List<Node> measures = parent.findNode(TemplateId.MEASURE_DATA_CMS_V2, prepFilter(type));
		measures.stream().findFirst().ifPresent(
				node -> node.getChildNodes(TemplateId.REPORTING_STRATUM_CMS)
						.forEach(child -> child.getParent().removeChildNode(child)));
	}

	private Predicate<List<Node>> prepFilter(final String type) {
		return (nodes) ->
			nodes.stream().allMatch(node ->
				(node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals(type)) &&
					node.getChildNodes().stream().anyMatch(
						child -> child.getType().equals(TemplateId.REPORTING_STRATUM_CMS))
			);
	}

	private Node scopedConversion(QrdaScope testSection, String path) {
		Converter converter = new Converter(new PathQrdaSource(baseDir.resolve(path)));
		converter.getContext().setScope(Sets.newHashSet(testSection));
		converter.transform();
		return converter.getDecoded().findFirstNode(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
	}

	private Set<Detail> validateNode(Node node) {
		QualityMeasureIdValidator validator = new QualityMeasureIdValidator();
		validator.internalValidateSingleNode(node);
		return validator.getDetails();
	}
}

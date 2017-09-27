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
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
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
	public void validateCms137V5FailMissingDenomStrata() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		removeMeasureStrata(result, "DENOM");
		Set<Detail> details = validateNode(result);

		assertThat("Missing CMS137v5 DENOM strata should result in errors", details.size(), greaterThan(0));
	}

	@Test
	public void validateCms137V5FailMissingDenexStrata() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		removeMeasureStrata(result, "DENEX");
		Set<Detail> details = validateNode(result);

		assertThat("Missing CMS137v5 DENEX strata should result in errors", details.size(), greaterThan(0));
	}

	@Test
	public void validateCms137V5FailMissingNumerStrata() throws IOException, XmlException {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		removeMeasureStrata(result, "NUMER");
		Set<Detail> details = validateNode(result);

		assertThat("Missing CMS137v5 NUMER strata should result in errors", details.size(), greaterThan(0));
	}

	private void removeMeasureStrata(Node parent, String type) {
		Node measure = parent.findNode(TemplateId.MEASURE_DATA_CMS_V2, prepFilter(type)).get(0);
		List<Node> strata = measure.getChildNodes(TemplateId.REPORTING_STRATUM_CMS)
				.collect(Collectors.toList());
		strata.forEach(measure::removeChildNode);

	}

	private Predicate<List<Node>> prepFilter(final String type) {
		return (nodes) ->
			!(nodes == null || nodes.size() == 0) &&
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

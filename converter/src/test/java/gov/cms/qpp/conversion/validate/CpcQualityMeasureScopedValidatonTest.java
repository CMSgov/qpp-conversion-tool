package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.segmentation.QrdaScope;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

class CpcQualityMeasureScopedValidatonTest {
	private static Path baseDir = Paths.get("src/test/resources/fixtures/qppct298/");

	@Test
	void validateCms137V5() {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		Set<Detail> details = validateNode(result);

		assertWithMessage("Valid CMS137v5 markup should not result in errors")
				.that(details).isEmpty();
	}

	@Test
	void validateCms137V5FailMissingDenomStrata() {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		removeMeasureStrata(result, SubPopulationLabel.DENOM.name());
		Set<Detail> details = validateNode(result);

		assertWithMessage("Missing CMS137v5 DENOM strata should result in errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(getMessages(SubPopulationLabel.DENOM.name(),
						"BC948E65-B908-493B-B48B-04AC342D3E6C",
						"EFB5B088-CE10-43DE-ACCD-9913B7AC12A2", "94B9555F-8700-45EF-B69F-433EBEDE8051"));
	}

	@Test
	void validateCms137V5FailMissingDenexStrata() {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		removeMeasureStrata(result, SubPopulationLabel.DENEX.name());
		Set<Detail> details = validateNode(result);

		assertWithMessage("Missing CMS137v5 DENEX strata should result in errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(getMessages(SubPopulationLabel.DENEX.name(),
						"56BC7FA2-C22A-4440-8652-2D3568852C60",
						"EFB5B088-CE10-43DE-ACCD-9913B7AC12A2", "94B9555F-8700-45EF-B69F-433EBEDE8051"));
	}

	@Test
	void validateCms137V5FailMissingNumerStrata() {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		removeMeasureStrata(result, SubPopulationLabel.NUMER.name());
		Set<Detail> details = validateNode(result);

		assertWithMessage("Missing CMS137v5 NUMER strata should result in errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(getMessages(SubPopulationLabel.NUMER.name(),
						"0BBF8596-4CFE-47F4-A0D7-9BEAB94BA4CD",
						"EFB5B088-CE10-43DE-ACCD-9913B7AC12A2", "94B9555F-8700-45EF-B69F-433EBEDE8051"));
	}

	@Test
	void validateCms137V5FailMissingIpopStrata() {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5.xml");
		removeMeasureStrata(result, "IPOP");
		Set<Detail> details = validateNode(result);

		assertWithMessage("Missing CMS137v5 IPOP strata should result in errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(getMessages("IPOP",
						"EC2C5F63-AF76-4D3C-85F0-5423F8C28541",
						"EFB5B088-CE10-43DE-ACCD-9913B7AC12A2", "94B9555F-8700-45EF-B69F-433EBEDE8051"));
	}

	@Test
	void validateCms137V5FailMissingMeasure() {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v5_MissingMeasure.xml");
		Set<Detail> details = validateNode(result);
		LocalizedError message = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format("CMS137v5", "IPP,IPOP", "EC2C5F63-AF76-4D3C-85F0-5423F8C28541");

		assertWithMessage("Missing CMS137v5 IPOP strata should result in errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(message);
	}

	private void removeMeasureStrata(Node parent, String type) {
		Node measure = parent.findNode(TemplateId.MEASURE_DATA_CMS_V2)
				.stream().filter(prepFilter(type)).findFirst().get();
		List<Node> strata = measure.getChildNodes(TemplateId.REPORTING_STRATUM_CMS)
				.collect(Collectors.toList());
		strata.forEach(measure::removeChildNode);
	}

	private Predicate<Node> prepFilter(final String type) {
		return (node) -> node.getValue(MeasureDataDecoder.MEASURE_TYPE).equals(type);
	}

	private Node scopedConversion(QrdaScope testSection, String path) {
		Converter converter = new Converter(new PathSource(baseDir.resolve(path)));
		converter.getContext().setScope(Sets.newHashSet(testSection));
		converter.transform();
		return converter.getReport().getDecoded().findFirstNode(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
	}

	private Set<Detail> validateNode(Node node) {
		CpcQualityMeasureIdValidator validator = new CpcQualityMeasureIdValidator();
		validator.internalValidateSingleNode(node);
		return validator.getDetails();
	}

	private LocalizedError[] getMessages(String type, String measure, String... subs) {
		Set<LocalizedError> messages = new HashSet<>();
		messages.add(ErrorCode.CPC_QUALITY_MEASURE_ID_STRATA_MISMATCH.format(0, subs.length, type, measure, Arrays.asList(subs)));
		Arrays.stream(subs).forEach(sub ->
				messages.add(ErrorCode.CPC_QUALITY_MEASURE_ID_MISSING_STRATA.format(sub, type, measure)));
		return messages.toArray(new LocalizedError[messages.size()]);
	}
}

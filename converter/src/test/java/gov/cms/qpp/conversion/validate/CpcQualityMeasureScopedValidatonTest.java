package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.segmentation.QrdaScope;

class CpcQualityMeasureScopedValidatonTest {
	private static Path baseDir = Paths.get("src/test/resources/fixtures/qppct298/");

	@Test
	void validateCms137V5() {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v6.xml");
		List<Detail> details = validateNode(result);

		assertWithMessage("Valid CMS137v5 markup should not result in errors")
				.that(details).isEmpty();
	}

	@Test
	void validateCms137V6FailMissingMeasure() {
		Node result = scopedConversion(QrdaScope.MEASURE_REFERENCE_RESULTS_CMS_V2, "cms137v6_MissingMeasure.xml");
		List<Detail> details = validateNode(result);
		LocalizedError message = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format("CMS137v6", "IPP,IPOP", "E6569B35-D2C5-464B-A608-BDB2F082FE57");

		assertWithMessage("Missing CMS137v6 IPOP strata should result in errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(message);
	}

	private Node scopedConversion(QrdaScope testSection, String path) {
		Converter converter = new Converter(new PathSource(baseDir.resolve(path)));
		converter.getContext().setScope(Sets.newHashSet(testSection));
		converter.transform();
		return converter.getReport().getDecoded().findFirstNode(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
	}

	private List<Detail> validateNode(Node node) {
		CpcQualityMeasureIdValidator validator = new CpcQualityMeasureIdValidator();
		return validator.validateSingleNode(node).getErrors();
	}

}

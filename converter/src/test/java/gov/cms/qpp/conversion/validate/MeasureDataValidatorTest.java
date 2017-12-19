package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.xml.XmlUtils;

/**
 * Test the MeasureData Validator
 */
class MeasureDataValidatorTest {

	@Test
	void internalValidateSingleNode() throws Exception {
		String happy = TestHelper.getFixture("measureDataHappy.xml");
		Node placeholder = new QppXmlDecoder(new Context()).decode(XmlUtils.stringToDom(happy));
		MeasureDataValidator validator = new MeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		Set<Detail> errors = validator.getDetails();
		assertWithMessage("Expect no errors on the happy path")
				.that(errors).isEmpty();
	}

	@Test
	void missingAggregateCount() throws Exception {
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertWithMessage("missing error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT);
	}

	@Test
	void invalidAggregateCount() throws Exception {
		Node aggregateCount = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		testNode.addChildNode(aggregateCount);
		aggregateCount.putValue("aggregateCount", "error");
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertWithMessage("Should result in a type error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER);
	}

	@Test
	void duplicateAggregateCountsFails() throws Exception {
		Node aggregateCount = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCount.putValue("aggregateCount", "100");
		aggregateCount.putValue("aggregateCount", "200", false);
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		testNode.addChildNodes(aggregateCount);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertWithMessage("missing error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR);
	}

	@Test
	void negativeAggregateCountsFails() throws Exception {
		Node aggregateCount = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCount.putValue("aggregateCount", "-1");
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		testNode.addChildNodes(aggregateCount);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertWithMessage("missing error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.MEASURE_DATA_VALUE_NOT_INTEGER);
	}

	@Test
	void multipleNegativeMeasureDataTest() throws Exception {
		//setup
		Path path = Paths.get("src/test/resources/negative/angerMeasureDataValidations.xml");

		//execute
		Converter converter = new Converter(new PathSource(path));
		AllErrors allErrors = new AllErrors();
		try {
			converter.transform();
		} catch(TransformException exception) {
			allErrors = exception.getDetails();
		}

		List<Detail> errors = getErrors(allErrors);

		assertWithMessage("Must contain the error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsAllOf(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER,
						ErrorCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR,
						ErrorCode.MEASURE_DATA_VALUE_NOT_INTEGER);
	}

	private List<Detail> getErrors(AllErrors content) {
		return content.getErrors().get(0).getDetails();
	}

}
package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.validate.MeasureDataValidator.EMPTY_POPULATION_ID;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
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
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(happy));
		MeasureDataValidator validator = new MeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V4);
		List<Detail> errors = validator.validateSingleNode(underTest).getErrors();
		assertWithMessage("Expect no errors on the happy path")
				.that(errors).isEmpty();
	}

	@Test
	void missingAggregateCount() {
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		MeasureDataValidator validator = new MeasureDataValidator();
		List<Detail> errors = validator.validateSingleNode(testNode).getErrors();
		assertWithMessage("missing error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT.format(EMPTY_POPULATION_ID));
	}

	@Test
	void invalidAggregateCount() throws Exception {
		Node aggregateCount = new Node(TemplateId.PI_AGGREGATE_COUNT);
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		testNode.addChildNode(aggregateCount);
		aggregateCount.putValue("aggregateCount", "error");
		MeasureDataValidator validator = new MeasureDataValidator();
		List<Detail> errors = validator.validateSingleNode(testNode).getErrors();
		assertWithMessage("Should result in a type error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER);
	}

	@Test
	void duplicateAggregateCountsFails() throws Exception {
		Node aggregateCount = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggregateCount.putValue("aggregateCount", "100");
		aggregateCount.putValue("aggregateCount", "200", false);
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		testNode.addChildNodes(aggregateCount);
		MeasureDataValidator validator = new MeasureDataValidator();
		List<Detail> errors = validator.validateSingleNode(testNode).getErrors();
		assertWithMessage("missing error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR.format(TemplateId.MEASURE_DATA_CMS_V4.name(), 2));
	}

	@Test
	void negativeAggregateCountsFails() throws Exception {
		Node aggregateCount = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggregateCount.putValue("aggregateCount", "-1");
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		testNode.addChildNodes(aggregateCount);
		MeasureDataValidator validator = new MeasureDataValidator();
		List<Detail> errors = validator.validateSingleNode(testNode).getErrors();
		assertWithMessage("missing error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.MEASURE_DATA_VALUE_NOT_INTEGER.format(EMPTY_POPULATION_ID));
	}

	@Test
	void multipleNegativeMeasureDataTest() {
		//setup
		Path path = Path.of("src/test/resources/negative/angerMeasureDataValidations.xml");

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
				.containsAtLeast(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER,
						ProblemCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR.format(TemplateId.MEASURE_DATA_CMS_V4.name(), 2),
						ProblemCode.MEASURE_DATA_VALUE_NOT_INTEGER.format("58347456-D1F3-4BBB-9B35-5D42825A0AB3"));
	}

	private List<Detail> getErrors(AllErrors content) {
		return content.getErrors().get(0).getDetails();
	}

}
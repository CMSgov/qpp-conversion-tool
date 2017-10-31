package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.validate.MeasureDataValidator.MISSING_AGGREGATE_COUNT;

/**
 * Test the MeasureData Validator
 */
public class MeasureDataValidatorTest {

	@Test
	public void internalValidateSingleNode() throws Exception {
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
	public void missingAggregateCount() throws Exception {
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertWithMessage("missing error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(MISSING_AGGREGATE_COUNT);
	}

	@Test
	public void invalidAggregateCount() throws Exception {
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
	public void duplicateAggregateCountsFails() throws Exception {
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
	public void negativeAggregateCountsFails() throws Exception {
		Node aggregateCount = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCount.putValue("aggregateCount", "-1");
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		testNode.addChildNodes(aggregateCount);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertWithMessage("missing error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(MeasureDataValidator.INVALID_VALUE);
	}

	@Test
	public void multipleNegativeMeasureDataTest() throws Exception {
		//setup
		Path path = Paths.get("src/test/resources/negative/angerMeasureDataValidations.xml");

		//execute
		Converter converter = new Converter(new PathQrdaSource(path));
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
						MeasureDataValidator.INVALID_VALUE);
	}

	private List<Detail> getErrors(AllErrors content) {
		return content.getErrors().get(0).getDetails();
	}

}
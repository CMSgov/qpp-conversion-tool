package gov.cms.qpp.conversion.model.error;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import static com.google.common.truth.Truth.assertThat;

class DetailTest {

	@Test
	void equalsContract() {
		EqualsVerifier.forClass(Detail.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}

	@Test
	void testSetters() {
		Detail detail = new Detail();
		detail.setMessage("message");
		detail.setType("type");
		detail.setValue("value");
		detail.setLocation(new Location());
		Detail otherDetail = new Detail(detail);

		assertThat(detail).isEqualTo(otherDetail);
	}

	@Test
	void testComputeLocation() {
		Node node = new Node(TemplateId.CLINICAL_DOCUMENT);

		Detail detail = Detail.forProblemAndNode(ProblemCode.UNEXPECTED_ERROR, node);

		assertThat(detail.getLocation().getLocation()).isEqualTo(node.getType().getHumanReadableTitle());
	}

	@Test
	void testComputeLocationMeasure() {
		Node node = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V4);
		String measureId = "Moof";
		node.putValue("measureId", measureId);

		Detail detail = Detail.forProblemAndNode(ProblemCode.UNEXPECTED_ERROR, node);

		assertThat(detail.getLocation().getLocation()).isEqualTo(node.getType().getHumanReadableTitle() + " " + measureId);
	}

	@Test
	void testComputeLocationEmpty() {
		Node node = new Node(TemplateId.PI_AGGREGATE_COUNT);

		Detail detail = Detail.forProblemAndNode(ProblemCode.UNEXPECTED_ERROR, node);

		assertThat(detail.getLocation().getLocation()).isEmpty();
	}
}

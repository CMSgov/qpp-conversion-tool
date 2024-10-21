package gov.cms.qpp.conversion.util;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.model.Constants.AGGREGATE_COUNT;

public class DuplicationCheckHelperTest {

	@Test
	void testDuplicateCheckerWithNoDuplicateAggregateCounts() {
		Node node = new Node(TemplateId.PI_AGGREGATE_COUNT);
		node.putValue(AGGREGATE_COUNT, "1234", false);

		int duplicationValue = DuplicationCheckHelper.calculateDuplications(node, AGGREGATE_COUNT);
		assertThat(duplicationValue).isEqualTo(0);
	}

	@Test
	void testDuplicateCheckerWithDuplicateAggregateCounts() {
		Node node = new Node(TemplateId.PI_AGGREGATE_COUNT);
		node.putValue(AGGREGATE_COUNT, "1234", false);
		node.putValue(AGGREGATE_COUNT, "1234", false);

		int duplicationValue = DuplicationCheckHelper.calculateDuplications(node, AGGREGATE_COUNT);
		assertThat(duplicationValue).isEqualTo(2);
	}
}

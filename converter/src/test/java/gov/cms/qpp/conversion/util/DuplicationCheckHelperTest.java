package gov.cms.qpp.conversion.util;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import static com.google.common.truth.Truth.assertThat;

public class DuplicationCheckHelperTest {

	@Test
	void testDuplicateCheckerWithNoDuplicateAggregateCounts() {
		Node node = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		node.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "1234", false);

		int duplicationValue = DuplicationCheckHelper.calculateDuplications(node, AggregateCountDecoder.AGGREGATE_COUNT);
		assertThat(duplicationValue).isEqualTo(0);
	}

	@Test
	void testDuplicateCheckerWithDuplicateAggregateCounts() {
		Node node = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		node.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "1234", false);
		node.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "1234", false);

		int duplicationValue = DuplicationCheckHelper.calculateDuplications(node, AggregateCountDecoder.AGGREGATE_COUNT);
		assertThat(duplicationValue).isEqualTo(2);
	}
}

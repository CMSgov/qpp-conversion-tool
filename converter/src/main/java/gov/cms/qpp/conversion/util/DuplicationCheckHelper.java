package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;

import java.util.List;

/**
 * Utility class to return the number of duplications found.
 */
public class DuplicationCheckHelper {
	public static final int ACCOUNT_FOR_ORIGINAL_AGGREGATE_COUNT = 1;
	public static final int ACCOUNT_FOR_MISSING_AGGREGATE_COUNT = 0;

	private DuplicationCheckHelper() {}

	/**
	 * Finds the number of duplications of aggregate counts
	 *
	 * @param node to check duplications from
	 * @return
	 */
	public static int calculateDuplications(Node node) {
		List<String> aggCountList = node.getDuplicateValues(AggregateCountDecoder.AGGREGATE_COUNT);
		return aggCountList != null
			? aggCountList.size() + ACCOUNT_FOR_ORIGINAL_AGGREGATE_COUNT : ACCOUNT_FOR_MISSING_AGGREGATE_COUNT;
	}
}

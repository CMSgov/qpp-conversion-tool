package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.model.Node;

import java.util.List;

/**
 * Utility class to return the number of duplications found.
 */
public class DuplicationCheckHelper {
	public static final int ACCOUNT_FOR_ORIGINAL_VALUE = 1;
	public static final int ACCOUNT_FOR_MISSING_VALUE = 0;

	private DuplicationCheckHelper() {}

	/**
	 * Finds the number of duplications of aggregate counts
	 *
	 * @param node to check duplications from
	 * @return
	 */
	public static int calculateDuplications(Node node, String type) {
		List<String> valueCountList = node.getDuplicateValues(type);
		return (valueCountList != null)
			? (valueCountList.size() + ACCOUNT_FOR_ORIGINAL_VALUE) : ACCOUNT_FOR_MISSING_VALUE;
	}
}

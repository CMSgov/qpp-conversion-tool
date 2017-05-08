package gov.cms.qpp.conversion.encode.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class helps lookup the eCQM Measure Id by GUUID
 */
public class QualityMeasuresLookup {

	/**
	 * Private Constructor for a static helper function class
	 */
	private QualityMeasuresLookup() {
	}

	// Private internal hashmap
	private static final Map<String, String> internalMap = new HashMap();

	static {
		internalMap.put("40280381-51f0-825b-0152-22b98cff181a", "CMS165v5");
		internalMap.put("40280381-503f-a1fc-0150-d33f5b0a1b8c", "CMS185v5");
		internalMap.put("40280381-51f0-825b-0152-229c4ea3170c", "CMS125v5");
		internalMap.put("40280381-51f0-825b-0152-22a1e7e81737", "CMS130v5");
		internalMap.put("40280381-52fc-3a32-0153-395ce63513af", "CMS147v6");
		internalMap.put("40280381-52fc-3a32-0153-1a646a2a0bfa", "CMS127v5");
		internalMap.put("40280381-51f0-825b-0152-229afff616ee", "CMS122v5");
		internalMap.put("40280381-52fc-3a32-0153-1a3981870b45", "CMS144v5");
		internalMap.put("40280381-51f0-825b-0152-22aae8a21778", "CMS139v5");
		internalMap.put("40280381-537c-f767-0153-c378bd7207a5", "CMS2v6");
		internalMap.put("40280381-52fc-3a32-0153-3d64af97147b", "CMS68v6");
	}

	/**
	 * Look for the guiid in the internal hash map and return its value
	 *
	 * @param guiid Measure GUUID
	 * @return String eMeasureId
	 */
	public static String getMeasureId(String guiid) {
		return internalMap.containsKey(guiid) ? internalMap.get(guiid) : guiid;
	}
}

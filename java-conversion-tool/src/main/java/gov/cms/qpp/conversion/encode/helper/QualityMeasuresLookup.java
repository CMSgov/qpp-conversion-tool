package gov.cms.qpp.conversion.encode.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class helps lookup the eCQM Measure Id by GUID
 */
public class QualityMeasuresLookup {

	// Private internal hashmap
	private static final Map<String, String> internalMap = new HashMap<>();

	static {
		internalMap.put("40280381-52fc-3a32-0153-1a4ba57f0b8a","CMS117v5");
		internalMap.put("40280381-51f0-825b-0152-229afff616ee","CMS122v5");
		internalMap.put("40280381-51f0-825b-0152-229b7b2f16f9","CMS123v5");
		internalMap.put("40280381-51f0-825b-0152-229bdcab1702","CMS124v5");
		internalMap.put("40280381-51f0-825b-0152-229c4ea3170c","CMS125v5");
		internalMap.put("40280381-52fc-3a32-0153-1a646a2a0bfa","CMS127v5");
		internalMap.put("40280381-51f0-825b-0152-22a112d2172a","CMS128v5");
		internalMap.put("40280381-503f-a1fc-0151-10de35992766","CMS129v6");
		internalMap.put("40280381-51f0-825b-0152-22a1e7e81737","CMS130v5");
		internalMap.put("40280381-51f0-825b-0152-22a24cdd1740","CMS131v5");
		internalMap.put("40280381-52fc-3a32-0153-1a2308830b04","CMS132v5");
		internalMap.put("40280381-52fc-3a32-0153-1f44d4980f0f","CMS133v5");
		internalMap.put("40280381-528a-60ff-0152-d7504e61249a","CMS134v5");
		internalMap.put("40280381-52fc-3a32-0153-62239c5d1ef6","CMS135v5");
		internalMap.put("40280381-51f0-825b-0152-22a639d81762","CMS136v6");
		internalMap.put("40280381-528a-60ff-0152-8e089ed20376","CMS137v5");
		internalMap.put("40280381-503f-a1fc-0150-d33f5b0a1b8c","CMS138v5");
		internalMap.put("40280381-51f0-825b-0152-22aae8a21778","CMS139v5");
		internalMap.put("40280381-52fc-3a32-0153-1a31050d0b24","CMS142v5");
		internalMap.put("40280381-52fc-3a32-0153-1a3471e70b34","CMS143v5");
		internalMap.put("40280381-52fc-3a32-0153-1a3981870b45","CMS144v5");
		internalMap.put("40280381-52fc-3a32-0153-2e772ecc10c0","CMS145v5");
		internalMap.put("40280381-52fc-3a32-0153-424a166015d1","CMS146v5");
		internalMap.put("40280381-52fc-3a32-0153-395ce63513af","CMS147v6");
		internalMap.put("40280381-52fc-3a32-0153-1a401cc10b57","CMS149v5");
		internalMap.put("40280381-51f0-825b-0152-22b52da917ba","CMS153v5");
		internalMap.put("40280381-52fc-3a32-0153-42810251160f","CMS154v5");
		internalMap.put("40280381-51f0-825b-0152-22b695b217dc","CMS155v5");
		internalMap.put("40280381-52fc-3a32-0153-56d2b4f01ae5","CMS156v5");
		internalMap.put("40280381-52fc-3a32-0153-1a4425a90b6c","CMS157v5");
		internalMap.put("40280381-5223-eb6b-0152-6a1558f51bdc","CMS158v5");
		internalMap.put("40280381-5118-2f4e-0151-3a9382cd09ba","CMS159v5");
		internalMap.put("40280381-503f-a1fc-0150-afe320c01761","CMS160v5");
		internalMap.put("40280381-503f-a1fc-0150-de8350a220c3","CMS161v5");
		internalMap.put("40280381-52fc-3a32-0153-5736a9401b9f","CMS164v5");
		internalMap.put("40280381-51f0-825b-0152-22b98cff181a","CMS165v5");
		internalMap.put("40280381-51f0-825b-0152-22ba7621182e","CMS166v6");
		internalMap.put("40280381-52fc-3a32-0153-1a4838b80b79","CMS167v5");
		internalMap.put("40280381-503f-a1fc-0150-67942bbc07d7","CMS169v5");
		internalMap.put("40280381-503f-a1fc-0151-11602d4d2909","CMS177v5");
		internalMap.put("40280381-528a-60ff-0152-94967c8a0860","CMS22v5");
		internalMap.put("40280381-537c-f767-0153-c378bd7207a5","CMS2v6");
		internalMap.put("40280381-5118-2f4e-0151-59fb81bf1055","CMS50v5");
		internalMap.put("40280381-51f0-825b-0152-2273af5a150b","CMS52v5");
		internalMap.put("40280381-51f0-825b-0152-227617db152e","CMS56v5");
		internalMap.put("40280381-51f0-825b-0152-227c2f851589","CMS65v6");
		internalMap.put("40280381-51f0-825b-0152-227ce2c81597","CMS66v5");
		internalMap.put("40280381-52fc-3a32-0153-3d64af97147b","CMS68v6");
		internalMap.put("40280381-5118-2f4e-0151-20a31a780368","CMS69v5");
		internalMap.put("40280381-52fc-3a32-0153-1f6962df0f9c","CMS74v6");
		internalMap.put("40280381-52fc-3a32-0153-1f39c24d0eef","CMS75v5");
		internalMap.put("40280381-51f0-825b-0152-2295c55c16aa","CMS82v4");
		internalMap.put("40280381-52fc-3a32-0153-1f3f70ca0eff","CMS90v6");

	}

	/**
	 * Private Constructor for a static helper function class
	 */
	private QualityMeasuresLookup() {
	}

	/**
	 * Look for the guid in the internal hash map and return its value
	 *
	 * @param guid Measure GUID
	 * @return String eMeasureId
	 */
	public static String getMeasureId(String guid) {
		return internalMap.getOrDefault(guid, guid);
	}
}

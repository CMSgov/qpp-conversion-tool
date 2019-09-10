package gov.cms.qpp.conversion;

public enum DocumentationReference {

	CPC_PLUS_SUBMISSIONS(15),
	IDENTIFIERS(16),
	PERFORMANCE_PERIOD(18),
	CLINICAL_DOCUMENT(20),
	PRACTICE_SITE_ADDRESS(26),
	REPORTING_PARAMETERS_ACT(82),
	MEASURE_IDS(94),
	CEHRT(15);

	private static final String BASE_PATH = "https://ecqi.healthit.gov/system/files/2018_CMS_QRDA_III_Eligible_Clinicians_and_EP_IG_v2_508.pdf#page=";
	private final String path;

	DocumentationReference(int page) {
		this.path = BASE_PATH + page;
	}

	@Override
	public String toString() {
		return path;
	}

}

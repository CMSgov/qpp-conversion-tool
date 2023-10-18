package gov.cms.qpp.conversion;

public enum DocumentationReference {

	PCF_SUBMISSIONS(14),
	IDENTIFIERS(15),
	PERFORMANCE_PERIOD(17),
	CLINICAL_DOCUMENT(19),
	PRACTICE_SITE_ADDRESS(25),
	REPORTING_PARAMETERS_ACT(17),
	MEASURE_IDS(43),
	CEHRT(15);

	private static final String BASE_PATH = "https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=";
	private final String path;

	DocumentationReference(int page) {
		this.path = BASE_PATH + page;
	}

	@Override
	public String toString() {
		return path;
	}

}

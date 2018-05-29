package gov.cms.qpp.conversion;

public enum DocumentationReference {

	CPC_PLUS_SUBMISSIONS(14),
	IDENTIFIERS(15),
	PERFORMANCE_PERIOD(17),
	CLINICAL_DOCUMENT(19),
	PRACTICE_SITE_ADDRESS(25),
	DOCUMENTATION_OF_TIN_NPI(28),
	REPORTING_PARAMETERS_ACT(80),
	MEASURE_IDS(88);

	private static final String BASE_PATH = "https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=";
	private final String path;

	DocumentationReference(int page) {
		this.path = BASE_PATH + page;
	}

	@Override
	public String toString() {
		return path;
	}

}

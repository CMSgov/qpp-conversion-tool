package gov.cms.qpp.conversion;

public enum DocumentationReference {

	PCF_SUBMISSIONS(12),
	IDENTIFIERS(15),
	PERFORMANCE_PERIOD(17),
	CLINICAL_DOCUMENT(19),
	PRACTICE_SITE_ADDRESS(22),
	REPORTING_PARAMETERS_ACT(17),
	MEASURE_IDS(43),
	CEHRT(22);

	private static final String BASE_PATH = "https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=";
	public static final String PERFORMANCE_START_DATE = "01/01/2024";
	public static final String PERFORMANCE_END_DATE = "12/31/2024";
	public static final String ROSTER_UPDATE_DATE = "December 1, 2024";
	private final String path;

	DocumentationReference(int page) {
		this.path = BASE_PATH + page;
	}

	@Override
	public String toString() {
		return path;
	}

}

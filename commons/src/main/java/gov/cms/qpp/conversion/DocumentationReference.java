package gov.cms.qpp.conversion;

public enum DocumentationReference {
	CLINICAL_DOCUMENT(19),
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

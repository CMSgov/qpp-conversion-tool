package gov.cms.qpp.conversion.api.model;

public enum Status {

	ACCEPTED,
	ACCEPTED_WITH_WARNINGS,
	REJECTED;

	public boolean isAccepted() {
		return this != REJECTED;
	}
}
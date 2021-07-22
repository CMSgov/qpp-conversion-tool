package gov.cms.qpp.conversion.api.model;

/**
 * Request body for updating a cpc file
 */
public class CpcFileStatusUpdateRequest {

	private Boolean processed;

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

}

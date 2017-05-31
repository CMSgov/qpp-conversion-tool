package gov.cms.qpp.conversion.api.model;

import gov.cms.qpp.conversion.TransformationStatus;

/**
 * Object to hold the Conversion Result
 */
public class ConversionResult {
	private String content;
	private TransformationStatus status;

	public ConversionResult(final String content, final TransformationStatus status) {
		this.content = content;
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public TransformationStatus getStatus() {
		return status;
	}
}

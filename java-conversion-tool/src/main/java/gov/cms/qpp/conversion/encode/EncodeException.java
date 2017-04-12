package gov.cms.qpp.conversion.encode;

/**
 * This exception indicates an issue encountered during the encoding (json serialization) process.
 * @author Scott Fradkin
 *
 */
public class EncodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String templateId = "";

	public EncodeException(String message, Exception cause) {
		super(message, cause);
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTemplateId() {
		return templateId;
	}
}

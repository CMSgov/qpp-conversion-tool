package gov.cms.qpp.conversion.encode;

/**
 * This exception indicates an issue encountered during the encoding (json serialization) process.
 */
public class EncodeException extends RuntimeException {

	private static final long serialVersionUID = 3L;
	private final String templateId;

	/**
	 * Constructor of specific Exception type
	 *
	 * @param message reason the exception is being created.
	 */
	public EncodeException(String message) {
		super(message);
		this.templateId = "";
	}

	/**
	 * Constructor of specific exception type
	 *
	 * @param message Reason the exception is being created
	 * @param cause   Root cause of exception
	 */
	public EncodeException(String message, Exception cause) {
		super(message, cause);
		this.templateId = "";
	}

	/**
	 * Constructor of specific Exception type with a template ID.
	 *
	 * @param message reason the exception is being created.
	 * @param templateId The template ID that had a problem encoding.
	 */
	public EncodeException(String message, String templateId) {
		super(message);
		this.templateId = templateId;
	}

	/**
	 * Constructor of specific exception type with a template ID.
	 *
	 * @param message Reason the exception is being created
	 * @param cause   Root cause of exception
	 * @param templateId The template ID that had a problem encoding.
	 */
	public EncodeException(String message, Exception cause, String templateId) {
		super(message, cause);
		this.templateId = templateId;
	}

	/**
	 * Accessor for the internal template id value
	 *
	 * @return String
	 */
	public String getTemplateId() {
		return templateId;
	}
}

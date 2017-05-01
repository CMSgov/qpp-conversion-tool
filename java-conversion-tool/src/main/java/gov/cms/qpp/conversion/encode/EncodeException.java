package gov.cms.qpp.conversion.encode;

/**
 * This exception indicates an issue encountered during the encoding (json serialization) process.
 */
public class EncodeException extends RuntimeException {

	private static final long serialVersionUID = 3L;
	private String templateId = "";

	/**
	 * Constructor of specific Exception type
	 *
	 * @param message reason the exception is being created.
	 */
	public EncodeException(String message) {
		super(message);
	}

	/**
	 * Constructor of specific exception type
	 *
	 * @param message Reson the exception is being created
	 * @param cause   Root cause of exception
	 */
	public EncodeException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 * Setter for the type of Encoder this exception pertains to.
	 *
	 * @param templateId TemplateId value
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * Accesor for the internal template id value
	 *
	 * @return String
	 */
	public String getTemplateId() {
		return templateId;
	}
}

package gov.cms.qpp.conversion.encode;

public class EncodeException extends Exception {

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

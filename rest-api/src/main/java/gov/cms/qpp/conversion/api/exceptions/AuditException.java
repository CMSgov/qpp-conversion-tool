package gov.cms.qpp.conversion.api.exceptions;


public class AuditException extends RuntimeException {
	public AuditException(Throwable ex) {
		super(ex);
	}
}

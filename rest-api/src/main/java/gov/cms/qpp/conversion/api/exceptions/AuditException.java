package gov.cms.qpp.conversion.api.exceptions;


/**
 * Used to rethrow any unexpected exceptions that might occur while persisting
 * Audit information
 */
public class AuditException extends RuntimeException {
	public AuditException(Throwable ex) {
		super(ex);
	}
}

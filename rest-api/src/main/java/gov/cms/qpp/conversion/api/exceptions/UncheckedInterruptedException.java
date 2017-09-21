package gov.cms.qpp.conversion.api.exceptions;

public class UncheckedInterruptedException extends RuntimeException {

	public UncheckedInterruptedException(InterruptedException exception) {
		super(exception);
	}
}

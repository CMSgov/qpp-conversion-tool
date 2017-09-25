package gov.cms.qpp.conversion.api.exceptions;

import java.util.Objects;

/**
 *  Wraps an {@link InterruptedException} with an unchecked exception.
 */
public class UncheckedInterruptedException extends RuntimeException {

	/**
	 * Constructs this exception.
	 *
	 * @param exception the {@code InterruptedException}.
	 * @throws NullPointerException if the cause is {@code null}.
	 */
	public UncheckedInterruptedException(InterruptedException exception) {
		super(Objects.requireNonNull(exception));
	}
}

package gov.cms.qpp.conversion.model.error;

public interface LocalizedProblem {

	/**
	 * Gets the error code associated with this error
	 */
	ProblemCode getProblemCode();

	/**
	 * Gets the message associated with this error
	 */
	String getMessage();

}

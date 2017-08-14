package gov.cms.qpp.conversion.util;


import gov.cms.qpp.conversion.model.Program;

/**
 * Maintains a value that discerns the program of the current thread's submission.
 */
public class ProgramContext {
	private static ThreadLocal<Program> context = ThreadLocal.withInitial(() -> Program.ALL);
	
	private ProgramContext(){}

	/**
	 * Sets the current thread's {@link Program} value
	 *
	 * @param value the submission's program
	 */
	public static void set(Program value) {
		context.set(value);
	}

	/**
	 * Gets the current thread's {@link Program} value
	 *
	 * @return the program
	 */
	public static Program get() {
		return context.get();
	}

	/**
	 * Removes the current thread's {@link Program} value
	 */
	public static void remove() {
		context.remove();
	}
}

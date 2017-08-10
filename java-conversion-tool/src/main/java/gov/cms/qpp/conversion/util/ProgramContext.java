package gov.cms.qpp.conversion.util;


import gov.cms.qpp.conversion.model.Program;

public class ProgramContext {
	private static ThreadLocal<Program> context = ThreadLocal.withInitial(() -> Program.ALL);
	
	private ProgramContext(){}

	public static void set(Program value) {
		context.set(value);
	}

	public static Program get() {
		return context.get();
	}

	public static void remove() {
		context.remove();
	}
}

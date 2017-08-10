package gov.cms.qpp.conversion.util;


import gov.cms.qpp.conversion.model.Program;

import java.util.Optional;

public class ProgramContext {
	private static ThreadLocal<Program> context = new ThreadLocal<>();

	public static void set(Program value) {
		remove();
		context.set(value);
	}

	public static Program get() {
		return Optional.ofNullable(context.get()).orElse(Program.ALL);
	}

	public static void remove() {
		context.remove();
	}
}

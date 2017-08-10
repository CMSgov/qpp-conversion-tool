package gov.cms.qpp.conversion.util;


public class ProgramContext {
	private static ThreadLocal<String> context = new ThreadLocal<>();

	public static void set(String value) {
		remove();
		context.set(value);
	}

	public static void remove() {
		context.remove();
	}
}

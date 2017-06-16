package gov.cms.qpp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import gov.cms.qpp.conversion.ConversionEntry;

public abstract class ConversionTestSuite {

	private static final PrintStream IGNORED = new PrintStream(new OutputStream() {
		@Override
		public void write(int b) throws IOException {				
		}
	});

	private static final Map<Field, Object> FLAGS = new HashMap<>();

	static {
		for (Field field : ConversionEntry.class.getDeclaredFields()) {
			if (isResettable(field)) {
				field.setAccessible(true);
				try {
					FLAGS.put(field, field.get(null));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static boolean isResettable(Field field) {
		if (!isBoolean(field)) {
			return false;
		}
		int mod = field.getModifiers();
		return Modifier.isStatic(mod) && !Modifier.isFinal(mod);
	}

	private static boolean isBoolean(Field field) {
		Class<?> type = field.getType();
		return type == boolean.class || type == Boolean.class;
	}

	protected static String getFixture(final String name) throws IOException {
		Path path = Paths.get("src/test/resources/fixtures/" + name);
		return new String(Files.readAllBytes(path));
	}

	private PrintStream stdout;
	private PrintStream stderr;

	@Before
	public final void setupSuite() throws Exception {
		saveOutput();
		ignoreOutput();
	}

	@After
	public final void teardownSuite() throws Exception {
		resetOutput();
		resetScope();
		resetFlags();
	}

	private void saveOutput() {
		stdout = System.out;
		stderr = System.err;
	}

	private void ignoreOutput() {
		System.setOut(IGNORED);
		System.setErr(IGNORED);
	}

	private void resetFlags() {
		FLAGS.forEach((field, value) -> {
			try {
				field.set(null, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void resetScope() throws NoSuchFieldException, IllegalAccessException {
		Field scope = ConversionEntry.class.getDeclaredField("SCOPE");
		scope.setAccessible(true);
		Set<?> set = (Set<?>) scope.get(null);
		set.clear();
	}

	private void resetOutput() {
		System.setOut(stdout);
		System.setErr(stderr);
	}

	protected final PrintStream console() {
		return stdout;
	}

}
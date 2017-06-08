package gov.cms.qpp.acceptance;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import gov.cms.qpp.conversion.ConversionEntry;

public abstract class ConversionEntrySuite {

	private final Map<Field, Object> fields = new HashMap<>();

	@Before
	public final void setupSuite() throws Exception {
		this.fields.clear();
		for (Field field : ConversionEntry.class.getDeclaredFields()) {
			if (isResettable(field)) {
				field.setAccessible(true);
				this.fields.put(field, field.get(null));
			}
		}
	}

	private boolean isResettable(Field field) {
		int mod = field.getModifiers();
		return Modifier.isStatic(mod) && !Modifier.isFinal(mod);
	}

	@After
	public final void teardownSuite() {
		this.fields.forEach((field, value) -> {
			try {
				field.set(null, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

}
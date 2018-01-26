package gov.cms.qpp.conversion.model.validation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MeasureIndexInit {
	private static final String LOCATION = "../tools/docker/docker-artifacts/measures_index";

	public static void reinitMeasureConfigs(boolean load) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		if (load) {
			System.setProperty(MeasureConfigs.MEASURES_INDEX_DIR, LOCATION);
		} else {
			System.clearProperty(MeasureConfigs.MEASURES_INDEX_DIR);
		}

		Method reinit = MeasureConfigs.class.getDeclaredMethod("initMeasureConfigs");
		reinit.setAccessible(true);
		reinit.invoke(null);
		reinit.setAccessible(false);
	}
}

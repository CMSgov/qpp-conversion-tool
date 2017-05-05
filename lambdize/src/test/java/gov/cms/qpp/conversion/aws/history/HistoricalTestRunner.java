package gov.cms.qpp.conversion.aws.history;

import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.Ignore;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class HistoricalTestRunner extends BlockJUnit4ClassRunner {
	private static final String ACCESS = "aws.accessKeyId";
	private static final String SECRET = "aws.secretKey";
	private static boolean runHistoricalTests = true;

	/**
	 * Creates a HistoricalTestRunner to run {@code klass}
	 *
	 * @throws InitializationError if the test class is malformed.
	 */
	public HistoricalTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
		Map<String, String> properties = getS3Properties();

		if (runHistoricalTests) {
			System.setProperty(ACCESS, properties.get(ACCESS));
			System.setProperty(SECRET, properties.get(SECRET));
		}
	}

	/**
	 * Evaluates whether {@link FrameworkMethod}s are ignored based on the
	 * {@link Ignore} annotation.
	 */
	@Override
	protected boolean isIgnored(FrameworkMethod child) {
		if(!runHistoricalTests) {
			System.err.println("Not running historical test");
		}
		return !runHistoricalTests || child.getAnnotation(Ignore.class) != null;
	}

	private Map<String, String> getS3Properties() {
		Map<String, String> properties = null;
		try {
			Path path = Paths.get("src")
					.resolve("test")
					.resolve("resources")
					.resolve("s3Properties.json");
			properties = JsonHelper.readJson(path, Map.class);
		} catch (Exception e) {
			runHistoricalTests = false;
			System.err.println("You must configure an s3Properties.json file.  Will not run historical tests.");
			e.printStackTrace(System.err);
		}

		if (properties == null || !(properties.containsKey(ACCESS) && properties.containsKey(SECRET))) {
			runHistoricalTests = false;
			String message = String.format("s3Properties.json must contain %s and %s configurations.  Will not run historical tests.", ACCESS, SECRET);
			System.err.println(message);
		}

		return properties;
	}

}

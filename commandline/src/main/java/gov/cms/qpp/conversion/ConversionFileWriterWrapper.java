package gov.cms.qpp.conversion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Calls the {@link Converter} and writes the results to a file.
 */
public class ConversionFileWriterWrapper {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(ConversionFileWriterWrapper.class);

	private final Source source;
	private final FileSystem fileSystem;
	private Context context;

	public ConversionFileWriterWrapper(Path inFile) {
		this.source = new PathSource(inFile);

		fileSystem = inFile.getFileSystem();
	}

	/**
	 * Context for the conversion
	 *
	 * @param context the conversion context
	 * @return this for chaining
	 */
	public ConversionFileWriterWrapper setContext(Context context) {
		this.context = context;
		return this;
	}

	/**
	 * Execute the conversion.
	 */
	public void transform() {
		Converter converter = context == null ? new Converter(source) : new Converter(source, context);

		executeConverter(converter);
	}

	/**
	 * Execute the converter and do initial handling of the result.
	 *
	 * @param converter The Converter to execute.
	 */
	private void executeConverter(Converter converter) {
		try {
			JsonWrapper jsonWrapper = converter.transform();
			Path outFile = getOutputFile(source.getName(), true);
			DEV_LOG.info("Successful conversion.  Writing out QPP to {}",
				outFile.toString());
			writeOutQpp(jsonWrapper, outFile);
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			Path outFile = getOutputFile(source.getName(), false);
			DEV_LOG.warn("There were errors during conversion.  Writing out errors to " + outFile.toString(),
					exception);
			writeOutErrors(allErrors, outFile);
		}
	}

	/**
	 * Write out the QPP to a file.
	 *
	 * @param jsonWrapper The QPP to write
	 * @param outFile The location to write.
	 */
	private void writeOutQpp(JsonWrapper jsonWrapper, Path outFile) {
		try (Writer writer = Files.newBufferedWriter(outFile)) {
			writer.write(jsonWrapper.toString());
			writer.flush();
		} catch (IOException exception) {
			DEV_LOG.error("Could not write out QPP JSON to file", exception);
		}
	}

	/**
	 * Write out the errors to a file.
	 *
	 * @param allErrors The errors to write.
	 * @param outFile The location to write.
	 */
	private void writeOutErrors(AllErrors allErrors, Path outFile) {
		try (Writer writer = Files.newBufferedWriter(outFile)) {
			ObjectWriter jsonObjectWriter = new ObjectMapper()
					.setSerializationInclusion(JsonInclude.Include.NON_NULL)
					.writer()
					.withDefaultPrettyPrinter();
			jsonObjectWriter.writeValue(writer, allErrors);
		} catch (IOException exception) {
			DEV_LOG.error("Could not write out error JSON to file", exception);
		}
	}

	/**
	 * Determine what the output file's name should be.
	 *
	 * @param name base string that helps relate the output file to it's corresponding source
	 * @param success Whether the conversion was successful or not.
	 * @return the output file name
	 */
	private Path getOutputFile(String name, final boolean success) {
		String outName = name.replaceFirst("(?i)(\\.xml)?$", getFileExtension(success));
		return fileSystem.getPath(outName);
	}

	/**
	 * Get an appropriate file extension for the transformation output filename.
	 *
	 * @return a file extension
	 */
	private String getFileExtension(boolean success) {
		return success ? ".qpp.json" : ".err.json";
	}
}

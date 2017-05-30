package gov.cms.qpp.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConversionFileWriterWrapper {
	private static final Logger CLIENT_LOG = LoggerFactory.getLogger("CLIENT-LOG");
	private static final Logger DEV_LOG = LoggerFactory.getLogger(ConversionFileWriterWrapper.class);

	private Path inFile;
	private boolean doDefaults;
	private boolean doValidation;

	public ConversionFileWriterWrapper(Path inFile) {
		this.inFile = inFile;
	}

	/**
	 * Switch for enabling or disabling inclusion of default nodes.
	 *
	 * @param doIt toggle value
	 * @return this for chaining
	 */
	public ConversionFileWriterWrapper doDefaults(boolean doIt) {
		this.doDefaults = doIt;
		return this;
	}

	/**
	 * Switch for enabling or disabling validation.
	 *
	 * @param doIt toggle value
	 * @return this for chaining
	 */
	public ConversionFileWriterWrapper doValidation(boolean doIt) {
		this.doValidation = doIt;
		return this;
	}

	public void transform() {
		Converter converter = new Converter(inFile)
			.doDefaults(doDefaults)
			.doValidation(doValidation);

		executeConverter(converter);
	}

	private void executeConverter(Converter converter) {
		try {
			JsonWrapper jsonWrapper = converter.transform();
			Path outFile = getOutputFile(inFile.getFileName().toString(), true);
			writeOutQpp(jsonWrapper, outFile);
		} catch (TransformException exception) {
			AllErrors allErrors = exception.getDetails();
			Path outFile = getOutputFile(inFile.getFileName().toString(), false);
			writeOutErrors(allErrors, outFile);
		}
	}

	private void writeOutQpp(JsonWrapper jsonWrapper, Path outFile) {
		try (Writer writer = Files.newBufferedWriter(outFile)) {
			writer.write(jsonWrapper.toString());
			writer.flush();
		} catch (IOException exception) {
			CLIENT_LOG.error("Could not write out QPP JSON to file");
			DEV_LOG.error("Could not write out QPP JSON to file", exception);
		}
	}

	private void writeOutErrors(AllErrors allErrors, Path outFile) {
		try (Writer writer = Files.newBufferedWriter(outFile)) {
			ObjectWriter jsonObjectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
			jsonObjectWriter.writeValue(writer, allErrors);
			writer.flush();
		} catch (IOException exception) {
			CLIENT_LOG.error("Could not write out error JSON to file");
			DEV_LOG.error("Could not write out error JSON to file", exception);
		}
	}

	/**
	 * Determine what the output file's name should be.
	 *
	 * @param name base string that helps relate the output file to it's corresponding source
	 * @param success
	 * @return the output file name
	 */
	private Path getOutputFile(String name, final boolean success) {
		String outName = name.replaceFirst("(?i)(\\.xml)?$", getFileExtension(success));
		return Paths.get(outName);
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

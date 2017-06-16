package gov.cms.qpp.conversion;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.util.ExceptionHelper;

/**
 * Calls the {@link Converter} and writes the results to a file.
 */
public class ConversionFileWriterWrapper {
	private static final Logger CLIENT_LOG = LoggerFactory.getLogger("CLIENT-LOG");
	private static final Logger DEV_LOG = LoggerFactory.getLogger(ConversionFileWriterWrapper.class);

	private Path inFile;
	private boolean doDefaults = true;
	private boolean doValidation = true;

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

	/**
	 * Execute the conversion.
	 * @return 
	 */
	public CompletableFutureCaller transform() {
		Converter converter = new Converter(inFile)
			.doDefaults(doDefaults)
			.doValidation(doValidation);

		return executeConverter(converter);
	}

	/**
	 * Execute the converter and do initial handling of the result.
	 *
	 * @param converter The Converter to execute.
	 */
	private CompletableFutureCaller executeConverter(Converter converter) {
		CompletableFuture<?> conversion = converter.transform().whenComplete((jsonWrapper, exception) -> {
			if (exception != null) {
				handleConverterError(exception);
				return;
			}

			handleConversion(jsonWrapper);
		});

		return CompletableFutureCaller.of(conversion);
	}

	private void handleConversion(JsonWrapper jsonWrapper) {
		Path outFile = getOutputFile(inFile.getFileName().toString(), true);
		CLIENT_LOG.info("Successful conversion.  Writing out QPP to {}",
			outFile.toString());
		DEV_LOG.info("Successful conversion.");
		writeOutQpp(jsonWrapper, outFile);
	}

	private void handleConverterError(Throwable caught) {
		TransformException error = ExceptionHelper.unwrap(caught, TransformException.class);
		if (error == null) {
			throw ExceptionHelper.propagated(caught);
		}

		AllErrors allErrors = error.getDetails();
		Path outFile = getOutputFile(inFile.getFileName().toString(), false);
		CLIENT_LOG.warn("There were errors during conversion.  Writing out errors to {}",
			outFile.toString());
		DEV_LOG.warn("There were errors during conversion.", error);
		writeOutErrors(allErrors, outFile);
		return;
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
			CLIENT_LOG.error("Could not write out QPP JSON to file");
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
			CLIENT_LOG.error("Could not write out error JSON to file");
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

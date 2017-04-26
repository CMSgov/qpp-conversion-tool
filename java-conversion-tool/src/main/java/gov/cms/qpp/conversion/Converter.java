package gov.cms.qpp.conversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.encode.ScopedQppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ErrorSource;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * Expects a list of file names as CLI parameters to be processed
 * Supports wild card characters in paths
 */
public class Converter {

	public static final Logger CLIENT_LOG = LoggerFactory.getLogger("CLIENT-LOG");
	private static final Logger DEV_LOG = LoggerFactory.getLogger(Converter.class);

	protected static final String NOT_VALID_XML_DOCUMENT = "The file is not a valid XML document";
	protected static final String UNEXPECTED_ERROR = "Unexpected exception occurred during conversion";

	private boolean doDefaults = true;
	private boolean doValidation = true;
	private List<ValidationError> validationErrors = new ArrayList<>();
	private InputStream xmlStream;
	private Path inFile;
	private Node decoded;

	/**
	 * Constructor for the CLI Converter application
	 *
	 * @param inFile File
	 */
	public Converter(Path inFile) {
		this.xmlStream = null;
		this.inFile = inFile;
	}

	/**
	 * Constructor for the CLI Converter application
	 *
	 * @param xmlStream input stream for xml content
	 */
	public Converter(InputStream xmlStream) {
		this.xmlStream = xmlStream;
		this.inFile = null;
	}

	/**
	 * Switch for enabling or disabling inclusion of default nodes.
	 *
	 * @param doIt toggle value
	 * @return this for chaining
	 */
	Converter doDefaults(boolean doIt) {
		this.doDefaults = doIt;
		return this;
	}

	/**
	 * Switch for enabling or disabling validation.
	 *
	 * @param doIt toggle value
	 * @return this for chaining
	 */
	Converter doValidation(boolean doIt) {
		this.doValidation = doIt;
		return this;
	}

	public TransformationStatus transform() {
		DEV_LOG.info("Transform invoked with file {}", inFile);
		try {
			if (!usingStream()) {
				transform(inFile);
			} else {
				transform(xmlStream);
			}
		} catch (XmlInputFileException | XmlException xe) {
			CLIENT_LOG.error(NOT_VALID_XML_DOCUMENT);
			DEV_LOG.error(NOT_VALID_XML_DOCUMENT, xe);
			validationErrors.add(new ValidationError(NOT_VALID_XML_DOCUMENT));
		} catch (Exception exception) {
			DEV_LOG.error(UNEXPECTED_ERROR, exception);
			validationErrors.add(new ValidationError(UNEXPECTED_ERROR));
		}

		if (!usingStream() && !validationErrors.isEmpty()) {
			writeValidationErrorsToFile();
		}

		return getStatus();
	}

	/**
	 * Transform a source a given file.
	 *
	 * @param inFile a source file
	 * @throws XmlException when transforming
	 * @throws IOException when writing to given file
	 */
	private void transform(Path inFile) throws XmlException, IOException {
		String inputFileName = inFile.getFileName().toString().trim();
		Node decoded = transform(XmlUtils.fileToStream(inFile));
		Path outFile = getOutputFile(inputFileName);

		if (decoded != null && validationErrors.isEmpty()) {
			writeConverted(decoded, outFile);
		}
	}

	/**
	 * Transform the content in a given input stream
	 *
	 * @param inStream source content
	 * @return a transformed representation of the source content
	 * @throws XmlException during transform
	 */
	private Node transform(InputStream inStream) throws XmlException {
		QrdaValidator validator = new QrdaValidator();
		decoded = XmlInputDecoder.decodeXml(XmlUtils.parseXmlStream(inStream));
		if (null != decoded) {
			CLIENT_LOG.info("Decoded template ID {} from file '{}'", decoded.getId(), inStream);

			if (!doDefaults) {
				DefaultDecoder.removeDefaultNode(decoded.getChildNodes());
			}
			if (doValidation) {
				validationErrors.addAll(validator.validate(decoded));
			}
		} else {
			validationErrors.add(new ValidationError("The file is not a QRDA-III XML document"));
		}

		return decoded;
	}

	/**
	 * Returns true if we are not using a file but using a stream.  False otherwise.
	 *
	 * @return Whether or not a stream is used in lieu of a file
	 */
	private boolean usingStream() {
		return inFile == null && xmlStream != null;
	}

	/**
	 * Determine the exit status of the transformation
	 *
	 * @return exit status
	 */
	private TransformationStatus getStatus() {
		if (null == decoded) {
			return TransformationStatus.NON_RECOVERABLE;
		}
		return validationErrors.isEmpty() ? TransformationStatus.SUCCESS : TransformationStatus.ERROR;
	}

	/**
	 * Assemble output based on the existence of transformations errors
	 *
	 * @return resulting transformation output content
	 */
	public InputStream getConversionResult() {
		return (!validationErrors.isEmpty())
				? writeValidationErrorsToStream()
				: writeConverted();
	}

	/**
	 * Assemble transformation validation errors
	 *
	 * @return error content
	 */
	private InputStream writeValidationErrorsToStream() {
		String identifier = xmlStream.toString();
		AllErrors allErrors = constructErrorHierarchy(identifier, validationErrors);
		byte[] errors = new byte[0];
		try {
			errors = constructErrorJson(allErrors);
		} catch (JsonProcessingException exception) {
			DEV_LOG.error("Error converting the validation errors into JSON", exception);
			String exceptionJson = "{ \"exception\": \"JsonProcessingException\" }";
			return new ByteArrayInputStream(exceptionJson.getBytes());
		}
		Validations.clear();
		return new ByteArrayInputStream(errors);
	}

	/**
	 * Assemble transformation error content and write to a file.
	 */
	private void writeValidationErrorsToFile() {
		String fileName = inFile.getFileName().toString().trim();
		Path outFile = getOutputFile(fileName);
		AllErrors allErrors = constructErrorHierarchy(fileName, validationErrors);

		try (Writer errWriter = Files.newBufferedWriter(outFile)) {
			final String writingErrorString = "Writing error file {}";
			DEV_LOG.error(writingErrorString, outFile);
			CLIENT_LOG.error(writingErrorString, outFile);
			writeErrorJson(allErrors, errWriter);
		} catch (IOException exception) { // coverage ignore candidate
			final String notWriteErrorFile = MessageFormat.format("Could not write to error file {0}", outFile);
			DEV_LOG.error(notWriteErrorFile, exception);
			CLIENT_LOG.error(notWriteErrorFile);
		} finally {
			Validations.clear();
		}
	}

	/**
	 * Constructs an {@link AllErrors} from all the validation errors.
	 *
	 * Currently consists of only a single {@link ErrorSource}.
	 *
	 * @param inputIdentifier An identifier for a source of QRDA3 XML.
	 * @param validationErrors A list of validation errors.
	 * @return All the errors.
	 */
	private AllErrors constructErrorHierarchy(final String inputIdentifier, final List<ValidationError> validationErrors) {
		return new AllErrors(Arrays.asList(constructErrorSource(inputIdentifier, validationErrors)));
	}

	/**
	 * Constructs an {@link ErrorSource} for the given {@code inputIdentifier} from the passed in validation errors.
	 *
	 * @param inputIdentifier An identifier for a source of QRDA3 XML.
	 * @param validationErrors A list of validation errors.
	 * @return A single source of validation errors.
	 */
	private ErrorSource constructErrorSource(final String inputIdentifier, final List<ValidationError> validationErrors) {
		return new ErrorSource(inputIdentifier, validationErrors);
	}

	/**
	 * Writes the {@link AllErrors} in JSON format to the {@code Writer}.
	 *
	 * @param allErrors All the errors to write out into JSON.
	 * @param writer The writer that will receive the JSON.
	 * @throws IOException Thrown when AllErrors can't be serialized or if it can't be written to the Writer.
	 */
	private void writeErrorJson(final AllErrors allErrors, final Writer writer) throws IOException {
		ObjectWriter jsonObjectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		jsonObjectWriter.writeValue(writer, allErrors);
	}

	/**
	 * Converts the {@link AllErrors} into JSON and converts that into an array of {@code byte}s.
	 *
	 * @param allErrors All the errors to convert into JSON.
	 * @return An array of bytes of JSON of AllErrors.
	 * @throws JsonProcessingException Thrown when AllErrors can't be serialized into JSON.
	 */
	private byte[] constructErrorJson(final AllErrors allErrors) throws JsonProcessingException {
		ObjectWriter jsonObjectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		return jsonObjectWriter.writeValueAsBytes(allErrors);
	}

	/**
	 * Place transformed content into an input stream
	 *
	 * @return content resulting from the transformation
	 */
	private InputStream writeConverted() {
		JsonOutputEncoder encoder = getEncoder();
		CLIENT_LOG.info("Decoded template ID {}", decoded.getId());

		try {
			encoder.setNodes(Collections.singletonList(decoded));
			InputStream inputStream = encoder.encode();
			validationErrors.addAll(encoder.getValidationErrors());
			return inputStream;
		} catch (EncodeException e) {
			throw new XmlInputFileException("Issues decoding/encoding.", e);
		} finally {
			Validations.clear();
		}
	}

	/**
	 * Write converted content to a specified file
	 * 
	 * @param decoded content to be written
	 * @param outFile destination file where output should be written
	 */
	private void writeConverted(Node decoded, Path outFile) {
		JsonOutputEncoder encoder = getEncoder();

		CLIENT_LOG.info("Decoded template ID {} to file '{}'", decoded.getId(), outFile);

		try (Writer writer = Files.newBufferedWriter(outFile)) {
			encoder.setNodes(Collections.singletonList(decoded));
			encoder.encode(writer);
			validationErrors.addAll(encoder.getValidationErrors());
		} catch (IOException | EncodeException e) { // coverage ignore candidate
			throw new XmlInputFileException("Issues decoding/encoding.", e);
		} finally {
			Validations.clear();
		}
	}

	/**
	 * Encoder used to create the output representation of transformed data.
	 *
	 * @see QppOutputEncoder
	 * @return an encoder
	 */
	protected JsonOutputEncoder getEncoder() {
		Collection<QrdaScope> scope = ConversionEntry.getScope();
		return (!scope.isEmpty()) ? new ScopedQppOutputEncoder() : new QppOutputEncoder();
	}

	/**
	 * Determine what the output file's name should be.
	 *
	 * @param name base string that helps relate the output file to it's corresponding source
	 * @return the output file name
	 */
	public Path getOutputFile(String name) {
		String outName = name.replaceFirst("(?i)(\\.xml)?$", getFileExtension());
		return Paths.get(outName);
	}

	/**
	 * Get an appropriate file extension for the transformation output filename.
	 *
	 * @return a file extension
	 */
	private String getFileExtension() {
		return (!validationErrors.isEmpty()) ? ".err.json" : ".qpp.json";
	}
}

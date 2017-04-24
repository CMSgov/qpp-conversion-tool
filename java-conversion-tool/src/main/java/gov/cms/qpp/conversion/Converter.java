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
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ErrorSource;
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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * Expects a list of filenames as CLI parameters to be processed
 * Supports wild card characters in paths
 */
public class Converter {

	public static final Logger CLIENT_LOG = LoggerFactory.getLogger("CLIENT-LOG");
	private static final Logger DEV_LOG = LoggerFactory.getLogger(Converter.class);

	private static final String NOT_VALID_XML_DOCUMENT = "The file is not a valid XML document";

	private boolean doDefaults = true;
	private boolean doValidation = true;
	private List<ValidationError> validationErrors = Collections.emptyList();
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

	public Converter doDefaults(boolean doIt) {
		this.doDefaults = doIt;
		return this;
	}

	public Converter doValidation(boolean doIt) {
		this.doValidation = doIt;
		return this;
	}

	public Integer transform() {
		DEV_LOG.info("Transform invoked with file {}", inFile);

		try {
			if (inFile != null) {
				transform(inFile);
			} else {
				transform(xmlStream);
			}
			return getStatus();
		} catch (XmlInputFileException | XmlException xe) {
			CLIENT_LOG.error(NOT_VALID_XML_DOCUMENT);
			DEV_LOG.error(NOT_VALID_XML_DOCUMENT, xe);
			return getStatus();
		} catch (Exception exception) {
			DEV_LOG.error("Unexpected exception occurred during conversion", exception);
			return getStatus();
		}
	}

	private void transform(Path inFile) throws XmlException, IOException {
		String inputFileName = inFile.getFileName().toString().trim();
		Node decoded = transform(XmlUtils.fileToStream(inFile));
		Path outFile = getOutputFile(inputFileName);

		if (decoded != null) {
			if (validationErrors.isEmpty()) {
				writeConverted(decoded, outFile);
			} else {
				writeValidationErrors(validationErrors, outFile);
			}
		}
	}

	private Node transform(InputStream inStream) throws XmlException {
		QrdaValidator validator = new QrdaValidator();
		validationErrors = Collections.emptyList();
		decoded = XmlInputDecoder.decodeXml(XmlUtils.parseXmlStream(inStream));
		if (null != decoded) {
			CLIENT_LOG.info("Decoded template ID {} from file '{}'", decoded.getId(), inStream);

			if (!doDefaults) {
				DefaultDecoder.removeDefaultNode(decoded.getChildNodes());
			}
			if (doValidation) {
				validationErrors = validator.validate(decoded);
			}
		}

		return decoded;
	}

	private Integer getStatus() {
		Integer status;
		if (null == decoded) {
			status = 2;
		} else {
			status = (validationErrors.isEmpty()) ? 0 : 1;
		}
		return status;
	}

	public InputStream getConversionResult() {
		return (!validationErrors.isEmpty())
				? writeValidationErrors()
				: writeConverted();
	}

	/**
	 * Compiles the validation errors into an {@code InputStream}.
	 *
	 * @return The errors.
	 */
	private InputStream writeValidationErrors() {
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
	 * Compiles the validation errors and writes them to the supplied path.
	 *
	 * @param validationErrors The validation errors to write.
	 * @param outFile The path to the file to write the errors to.
	 */
	private void writeValidationErrors(List<ValidationError> validationErrors, Path outFile) {

		String fileName = inFile.toString();
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

	private InputStream writeConverted() {
		JsonOutputEncoder encoder = getEncoder();
		CLIENT_LOG.info("Decoded template ID {}", decoded.getId());

		try {
			encoder.setNodes(Collections.singletonList(decoded));
			return encoder.encode();
		} catch (EncodeException e) {
			throw new XmlInputFileException("Issues decoding/encoding.", e);
		} finally {
			Validations.clear();
		}
	}

	private void writeConverted(Node decoded, Path outFile) {
		JsonOutputEncoder encoder = getEncoder();

		CLIENT_LOG.info("Decoded template ID {} to file '{}'", decoded.getId(), outFile);

		try (Writer writer = Files.newBufferedWriter(outFile)) {
			encoder.setNodes(Collections.singletonList(decoded));
			encoder.encode(writer);
			// do something with encode validations
		} catch (IOException | EncodeException e) { // coverage ignore candidate
			throw new XmlInputFileException("Issues decoding/encoding.", e);
		} finally {
			Validations.clear();
		}
	}

	protected JsonOutputEncoder getEncoder() {
		return new QppOutputEncoder();
	}

	public Path getOutputFile(String name) {
		String outName = name.replaceFirst("(?i)(\\.xml)?$", getFileExtension());
		return Paths.get(outName);
	}

	private String getFileExtension() {
		return (!validationErrors.isEmpty()) ? ".err.json" : ".qpp.json";
	}
}

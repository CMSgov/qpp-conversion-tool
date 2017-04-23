package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validations;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	private Collection<TemplateId> scope;
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
	public Converter doDefaults(boolean doIt) {
		this.doDefaults = doIt;
		return this;
	}

	/**
	 * Switch for enabling or disabling validation.
	 *
	 * @param doIt toggle value
	 * @return this for chaining
	 */
	public Converter doValidation(boolean doIt) {
		this.doValidation = doIt;
		return this;
	}

	public Converter scope (Collection<TemplateId> templateIds) {
		this.scope = templateIds;
		return this;
	}

	/**
	 * Transform the wrapped resource. This may be a {@link Path} or an {@link InputStream}.
	 *
	 * @return exit status code of the transformation. A non-zero exit represents a failure.
	 */
	public Integer transform() {
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

	/**
	 * Transform a source a given file.
	 *
	 * @param inFile a source file
	 * @throws XmlException
	 * @throws IOException
	 */
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

	/**
	 * Transform the content in a given input stream
	 *
	 * @param inStream source content
	 * @return a transformed representation of the source content
	 * @throws XmlException
	 */
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

	/**
	 * Determine the exit status of the transformation
	 *
	 * @return exit status
	 */
	private Integer getStatus() {
		Integer status;
		if (null == decoded) {
			status = 2;
		} else {
			status = (validationErrors.isEmpty()) ? 0 : 1;
		}
		return status;
	}

	/**
	 * Assemble output based on the existence of transformations errors
	 *
	 * @return resulting transformation output content
	 */
	public InputStream getConversionResult() {
		return (!validationErrors.isEmpty())
				? writeValidationErrors()
				: writeConverted() ;
	}

	/**
	 * Assemble transformation validation errors
	 *
	 * @return error content
	 */
	private InputStream writeValidationErrors() {
		String errors = validationErrors.stream()
				.map(error -> "Validation Error: " + error.getErrorText())
				.collect(Collectors.joining(System.lineSeparator()));
		Validations.clear();
		return new ByteArrayInputStream(errors.getBytes());
	}

	/**
	 * Assemble transformation error content and write to a file.
	 *
	 * @param validationErrors errors that occurred during transformation
	 * @param outFile destination file where error output should be written
	 */
	private void writeValidationErrors(List<ValidationError> validationErrors, Path outFile) {
		try (Writer errWriter = Files.newBufferedWriter(outFile)) {
			for (ValidationError error : validationErrors) {
				String errorXPath = error.getPath();
				errWriter.write("Validation Error: " + error.getErrorText() + System.lineSeparator()
								+ (errorXPath != null && !errorXPath.isEmpty() ? "\tat " + errorXPath : ""));
			}
		} catch (IOException e) { // coverage ignore candidate
			DEV_LOG.error("Could not write to file: {}", outFile.toString(), e);
		} finally {
			Validations.clear();
		}
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
			return encoder.encode();
		} catch (EncodeException e) {
			throw new XmlInputFileException("Issues decoding/encoding.", e);
		} finally {
			Validations.clear();
		}
	}

	/**
	 * Write converted content to a specified file
	 * @param decoded content to be written
	 * @param outFile destination file where output should be written
	 */
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

	/**
	 * Encoder used to create the output representation of transformed data.
	 *
	 * @see QppOutputEncoder
	 * @return an encoder
	 */
	protected JsonOutputEncoder getEncoder() {
		return new QppOutputEncoder();
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
		return (!validationErrors.isEmpty()) ? ".err.txt" : ".qpp.json";
	}
}
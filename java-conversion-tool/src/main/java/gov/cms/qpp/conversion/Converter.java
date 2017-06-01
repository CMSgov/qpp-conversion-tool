package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.encode.ScopedQppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ErrorSource;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import gov.cms.qpp.conversion.util.NamedInputStream;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
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

	public Node getDecoded() {
		return decoded;
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

	/**
	 * Perform conversion.
	 *
	 * @return status of conversion
	 */
	public JsonWrapper transform() throws TransformException {
		DEV_LOG.info("Transform invoked with file {}", inFile);
		JsonWrapper qpp = null;
		try {
			if (!usingStream()) {
				qpp = transform(inFile);
			} else {
				qpp = transform(xmlStream);
			}
		} catch (XmlInputFileException | XmlException xe) {
			CLIENT_LOG.error(NOT_VALID_XML_DOCUMENT);
			DEV_LOG.error(NOT_VALID_XML_DOCUMENT, xe);
			validationErrors.add(new ValidationError(NOT_VALID_XML_DOCUMENT));
		} catch (Exception exception) {
			DEV_LOG.error(UNEXPECTED_ERROR, exception);
			validationErrors.add(new ValidationError(UNEXPECTED_ERROR));
		}

		if (!validationErrors.isEmpty()) {
			throw new TransformException("Validation errors exist", null,
				constructErrorHierarchy(sourceIdentifier(), validationErrors));
		}

		return qpp;
	}

	/**
	 * Transform a source a given file.
	 *
	 * @param inFile a source file
	 * @throws XmlException when transforming
	 * @throws IOException when writing to given file
	 */
	private JsonWrapper transform(Path inFile) throws XmlException, IOException {
		return transform(new NamedInputStream(XmlUtils.fileToStream(inFile), inFile.toString()));
	}

	/**
	 * Transform the content in a given input stream
	 *
	 * @param inStream source content
	 * @return a transformed representation of the source content
	 * @throws XmlException during transform
	 */
	private JsonWrapper transform(InputStream inStream) throws XmlException {
		QrdaValidator validator = new QrdaValidator();
		decoded = XmlInputDecoder.decodeXml(XmlUtils.parseXmlStream(inStream));
		JsonWrapper qpp = null;
		if (null != decoded) {
			CLIENT_LOG.info("Decoded template ID {} from file '{}'", decoded.getType(), inStream);

			if (!doDefaults) {
				DefaultDecoder.removeDefaultNode(decoded.getChildNodes());
			}
			if (doValidation) {
				validationErrors.addAll(validator.validate(decoded));
			}

			if (validationErrors.isEmpty()) {
				qpp = encode();
			}
		} else {
			validationErrors.add(new ValidationError("The file is not a QRDA-III XML document"));
		}

		return qpp;
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
	 * Place transformed content into an input stream
	 *
	 * @return content resulting from the transformation
	 */
	private JsonWrapper encode() {
		JsonOutputEncoder encoder = getEncoder();
		CLIENT_LOG.info("Decoded template ID {}", decoded.getType());

		try {
			encoder.setNodes(Collections.singletonList(decoded));
			JsonWrapper qpp = encoder.encode();
			validationErrors.addAll(encoder.getValidationErrors());
			return qpp;
		} catch (EncodeException e) {
			throw new XmlInputFileException("Issues decoding/encoding.", e);
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

	private String sourceIdentifier() {
		if (usingStream()) {
			return xmlStream.toString();
		} else {
			return inFile.getFileName().toString();
		}
	}
}

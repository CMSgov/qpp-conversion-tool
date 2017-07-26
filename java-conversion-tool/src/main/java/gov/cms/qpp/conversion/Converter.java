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
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
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

	public static final Logger DEV_LOG = LoggerFactory.getLogger(Converter.class);

	static final String NOT_VALID_XML_DOCUMENT = "The file is not a valid XML document";
	static final String UNEXPECTED_ERROR = "Unexpected exception occurred during conversion";

	private boolean doDefaults = true;
	private boolean doValidation = true;
	private List<Detail> details = new ArrayList<>();
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
	Converter doValidation(boolean doIt) {
		this.doValidation = doIt;
		return this;
	}

	/**
	 * Perform conversion.
	 *
	 * @return status of conversion
	 */
	public JsonWrapper transform() {
		DEV_LOG.info("Transform invoked with file {}", getName());
		JsonWrapper qpp = null;
		try {
			if (!usingStream()) {
				qpp = transform(inFile);
			} else {
				qpp = transform(xmlStream);
			}
		} catch (XmlInputFileException | XmlException xe) {
			DEV_LOG.error(NOT_VALID_XML_DOCUMENT, xe);
			details.add(new Detail(NOT_VALID_XML_DOCUMENT));
		} catch (Exception exception) {
			DEV_LOG.error(UNEXPECTED_ERROR, exception);
			details.add(new Detail(UNEXPECTED_ERROR));
		}

		if (!details.isEmpty()) {
			throw new TransformException("Validation errors exist", null,
				constructErrorHierarchy(sourceIdentifier(), details));
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
			DEV_LOG.info("Decoded template ID {} from file '{}'", decoded.getType(), inStream);

			if (!doDefaults) {
				DefaultDecoder.removeDefaultNode(decoded.getChildNodes());
			}
			if (doValidation) {
				details.addAll(validator.validate(decoded));
			}

			if (details.isEmpty()) {
				qpp = encode();
			}
		} else {
			details.add(new Detail("The file is not a QRDA-III XML document"));
		}

		return qpp;
	}

	private String getName() {
		return (inFile == null ? xmlStream : inFile).toString();
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
	 * Currently consists of only a single {@link Error}.
	 *
	 * @param inputIdentifier An identifier for a source of QRDA3 XML.
	 * @param details A list of validation errors.
	 * @return All the errors.
	 */
	private AllErrors constructErrorHierarchy(final String inputIdentifier, final List<Detail> details) {
		return new AllErrors(Arrays.asList(constructErrorSource(inputIdentifier, details)));
	}

	/**
	 * Constructs an {@link Error} for the given {@code inputIdentifier} from the passed in validation errors.
	 *
	 * @param inputIdentifier An identifier for a source of QRDA3 XML.
	 * @param details A list of validation errors.
	 * @return A single source of validation errors.
	 */
	private Error constructErrorSource(final String inputIdentifier, final List<Detail> details) {
		return new Error(inputIdentifier, details);
	}

	/**
	 * Place transformed content into an input stream
	 *
	 * @return content resulting from the transformation
	 */
	private JsonWrapper encode() {
		JsonOutputEncoder encoder = getEncoder();
		DEV_LOG.info("Encoding template ID {}", decoded.getType());

		try {
			encoder.setNodes(Collections.singletonList(decoded));
			JsonWrapper qpp = encoder.encode();
			details.addAll(encoder.getDetails());
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

	/**
	 * Returns an identifier for either the file or stream depending on what is being used.
	 *
	 * @return An identifier.
	 */
	private String sourceIdentifier() {
		if (usingStream()) {
			return xmlStream.toString();
		} else {
			return inFile.getFileName().toString();
		}
	}
}

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
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import gov.cms.qpp.conversion.util.ProgramContext;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * Expects a list of file names as CLI parameters to be processed
 * Supports wild card characters in paths
 */
public class Converter {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(Converter.class);

	static final String NOT_VALID_XML_DOCUMENT = "The file is not a valid XML document";
	static final String UNEXPECTED_ERROR = "Unexpected exception occurred during conversion";

	private static boolean historical = false;
	private static Set<QrdaScope> scope = new HashSet<>();

	private boolean doDefaults = true;
	private boolean doValidation = true;
	private List<Detail> details = new ArrayList<>();
	private Node decoded;
	private final QrdaSource source;

	/**
	 * Constructor for the CLI Converter application
	 *
	 * @param source QrdaSource to use for the conversion
	 */
	public Converter(QrdaSource source) {
		Objects.requireNonNull(source, "source");
		ProgramContext.set(Program.ALL);

		this.source = source;
	}

	/**
	 * Is this a conversion of historical submissions.
	 *
	 * @return determination of whether or not the conversion is enacted on historical submissions.
	 */
	public static boolean setHistorical() {
		return historical;
	}

	/**
	 * Sets whether conversions are historical or not.
	 *
	 * @param isHistorical Flag indicating whether conversions are historical or not.
	 */
	public static void setHistorical(boolean isHistorical) {
		Converter.historical = isHistorical;
	}

	/**
	 * Get the scope that determines which data may be transformed.
	 *
	 * @return scope The scope.
	 */
	public static Collection<QrdaScope> getScope() {
		return Collections.unmodifiableSet(scope);
	}

	/**
	 * Sets the scope of the converter.
	 *
	 * @param newScope The new scope.
	 */
	public static void setScope(Set<QrdaScope> newScope) {
		scope = newScope;
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
		DEV_LOG.info("Transform invoked with file {}", source.getName());
		JsonWrapper qpp = null;
		try {
			qpp = transform(source.toInputStream());
		} catch (XmlInputFileException | XmlException xe) {
			DEV_LOG.error(NOT_VALID_XML_DOCUMENT, xe);
			details.add(new Detail(NOT_VALID_XML_DOCUMENT));
		} catch (Exception exception) {
			DEV_LOG.error(UNEXPECTED_ERROR, exception);
			details.add(new Detail(UNEXPECTED_ERROR));
		} finally {
			ProgramContext.remove();
		}

		if (!details.isEmpty()) {
			throw new TransformException("Validation errors exist", null,
				constructErrorHierarchy(source.getName(), details));
		}

		return qpp;
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
		Element doc = XmlUtils.parseXmlStream(inStream);
		decoded = XmlInputDecoder.decodeXml(doc);
		JsonWrapper qpp = null;
		if (null != decoded) {
			DEV_LOG.info("Decoded template ID {} from file '{}'", decoded.getType(), source.getName());

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
		AllErrors errors = new AllErrors();
		errors.addError(constructErrorSource(inputIdentifier, details));
		return errors;
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
		return (!getScope().isEmpty()) ? new ScopedQppOutputEncoder() : new QppOutputEncoder();
	}

}

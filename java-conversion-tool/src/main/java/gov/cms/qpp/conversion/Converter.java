package gov.cms.qpp.conversion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * Expects a list of file names as CLI parameters to be processed
 * Supports wild card characters in paths
 */
public class Converter {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(Converter.class);

	static final String NOT_VALID_XML_DOCUMENT = "The file is not a valid XML document";
	static final String UNEXPECTED_ERROR = "Unexpected exception occurred during conversion";

	private final QrdaSource source;
	private final Context context;
	private List<Detail> details = new ArrayList<>();
	private Node decoded;

	/**
	 * Constructor for the CLI Converter application
	 *
	 * @param source QrdaSource to use for the conversion
	 */
	public Converter(QrdaSource source) {
		this(source, new Context());
	}

	/**
	 * Constructor for the CLI Converter application
	 *
	 * @param source QrdaSource to use for the conversion
	 * @param context Context to use for the conversion
	 */
	public Converter(QrdaSource source, Context context) {
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(context, "context");

		this.source = source;
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public Node getDecoded() {
		return decoded;
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
		Element doc = XmlUtils.parseXmlStream(inStream);
		decoded = XmlInputDecoder.decodeXml(context, doc);
		JsonWrapper qpp = null;
		if (null != decoded) {
			DEV_LOG.info("Decoded template ID {} from file '{}'", decoded.getType(), source.getName());

			if (!context.isDoDefaults()) {
				DefaultDecoder.removeDefaultNode(decoded.getChildNodes());
			}

			if (context.isDoValidation()) {
				QrdaValidator validator = new QrdaValidator(context);
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
		return (!context.getScope().isEmpty()) ? new ScopedQppOutputEncoder(context) : new QppOutputEncoder(context);
	}

}

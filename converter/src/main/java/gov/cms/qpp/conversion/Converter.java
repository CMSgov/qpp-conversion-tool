package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.XmlDecoderEngine;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.encode.ScopedQppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.ValidationResult;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * Expects a list of file names as CLI parameters to be processed
 * Supports wild card characters in paths
 */
public class Converter {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(Converter.class);

	private final Source source;
	private final Context context;
	private List<Detail> errors = new ArrayList<>();
	private List<Detail> warnings = new ArrayList<>();
	private Node decoded;
	private JsonWrapper encoded;

	public Converter(Source source) {
		this(source, new Context());
	}

	/**
	 * Constructor for the CLI Converter application
	 *
	 * @param source Source to use for the conversion
	 * @param context Context to use for the conversion
	 */
	public Converter(Source source, Context context) {
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(context, "context");

		this.source = source;
		this.context = context;
	}

	/**
	 * Get the conversion context environment for the converter instance.
	 * @return context information
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Perform conversion.
	 *
	 * @return status of conversion
	 */
	public JsonWrapper transform() {
		DEV_LOG.info("Transform invoked");
		try {
			encoded = transform(source.toInputStream());
		} catch (XmlInputFileException | XmlException xe) {
			DEV_LOG.error(ErrorCode.NOT_VALID_XML_DOCUMENT.getMessage(), xe);
			Detail detail = Detail.forErrorCode(ErrorCode.NOT_VALID_XML_DOCUMENT);
			errors.add(detail);
		} catch (Exception exception) {
			DEV_LOG.error(ErrorCode.UNEXPECTED_ERROR.getMessage(), exception);
			Detail detail = Detail.forErrorCode(ErrorCode.UNEXPECTED_ERROR);
			errors.add(detail);
		}

		if (!errors.isEmpty()) {
			throw new TransformException("Validation errors exist", null, getReport());
		}

		return encoded;
	}

	/**
	 * Transform the content in a given input stream
	 *
	 * @param inStream source content
	 * @return a transformed representation of the source content
	 * @throws XmlException during transform
	 */
	private JsonWrapper transform(InputStream inStream) {
		Element doc = XmlUtils.parseXmlStream(inStream);
		decoded = XmlDecoderEngine.decodeXml(context, doc);
		JsonWrapper qpp = null;
		if (null != decoded) {
			DEV_LOG.info("Decoded template ID {}", decoded.getType());

			if (context.isDoValidation()) {
				QrdaValidator validator = new QrdaValidator(context);
				ValidationResult result = validator.validate(decoded);
				errors.addAll(result.getErrors());
				warnings.addAll(result.getWarnings());
			}

			if (errors.isEmpty()) {
				qpp = encode();
			}
		} else {
			Detail detail = Detail.forErrorCode(ErrorCode.NOT_VALID_QRDA_DOCUMENT.format(
				Context.REPORTING_YEAR, DocumentationReference.CLINICAL_DOCUMENT));
			errors.add(detail);
		}

		return qpp;
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
			JsonWrapper qpp = new JsonWrapper(encoder.encode(), true);
			errors.addAll(encoder.getErrors());
			warnings.addAll(encoder.getWarnings());
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

	/**
	 * Retrieve the Converter's {@link ConversionReport}
	 *
	 * @return the conversion report
	 */
	public ConversionReport getReport() {
		return new ConversionReport(source, errors, warnings, decoded, encoded);
	}

}

package gov.cms.qpp.conversion;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.util.CloneHelper;

/**
 * Report on the stat of a conversion.
 */
public class ConversionReport {
	private final ObjectMapper mapper = new ObjectMapper();
	private Source source;
	private Node decoded;
	private JsonWrapper encoded;
	private AllErrors reportDetails;
	private List<Detail> warnings;

	private String qppValidationDetails;

	/**
	 * Construct a con version report
	 */
	ConversionReport(Source source, List<Detail> errors, List<Detail> warnings, Node decoded, JsonWrapper encoded) {
		this.source = source;
		this.decoded = decoded;
		this.encoded = encoded;
		this.warnings = warnings;
		reportDetails = constructErrorHierarchy(source.getName(), errors);
	}

	/**
	 * Constructs an {@link AllErrors} from all the validation errors.
	 *
	 * Currently consists of only a single {@link Error}.
	 *
	 * @param inputIdentifier
	 *            An identifier for a source of QRDA3 XML.
	 * @param details
	 *            A list of validation errors.
	 * @return All the errors.
	 */
	private AllErrors constructErrorHierarchy(final String inputIdentifier, final List<Detail> details) {
		AllErrors errors = new AllErrors();
		errors.addError(constructErrorSource(inputIdentifier, details));
		return errors;
	}

	/**
	 * Constructs an {@link Error} for the given {@code inputIdentifier} from the
	 * passed in validation errors.
	 *
	 * @param inputIdentifier
	 *            An identifier for a source of QRDA3 XML.
	 * @param details
	 *            A list of validation errors.
	 * @return A single source of validation errors.
	 */
	private Error constructErrorSource(final String inputIdentifier, final List<Detail> details) {
		return new Error(inputIdentifier, details);
	}

	/**
	 * Defensive copy of decoded submission
	 *
	 * @return decoded {@link Node}
	 */
	public Node getDecoded() {
		return CloneHelper.deepClone(decoded);
	}

	/**
	 * Defensive copy of the result of the conversion
	 *
	 * @return encoded {@link JsonWrapper}
	 */
	public JsonWrapper getEncoded() {
		return CloneHelper.deepClone(encoded);
	}

	/**
	 * Retrieve information pertaining to errors generated during the conversion.
	 *
	 * @return all errors registered during conversion
	 */
	public AllErrors getReportDetails() {
		return reportDetails;
	}

	/**
	 * Mutator for reportDetails
	 *
	 * @param details
	 *            updated errors
	 */
	public void setReportDetails(AllErrors details) {
		reportDetails = details;
	}

	/**
	 * Mutator for QPP validation details
	 *
	 * @param details
	 *            QPP validation details
	 */
	public void setRawValidationDetails(String details) {
		qppValidationDetails = details;
	}

	/**
	 * Get the {@link Source} for the input to the converter.
	 *
	 * @return {@link Source} for the input.
	 */
	public Source getQrdaSource() {
		return source;
	}

	/**
	 * Get the {@link Source} for the output.
	 *
	 * @return {@link Source} for the output.
	 */
	public Source getQppSource() {
		return getEncoded().toSource();
	}

	/**
	 * Get the {@link Source} for the conversion validation errors.
	 *
	 * @return {@link Source} for the validation errors.
	 */
	public Source getValidationErrorsSource() {
		try {
			byte[] validationErrorBytes = mapper.writeValueAsBytes(reportDetails);
			return new InputStreamSupplierSource("ValidationErrors", new ByteArrayInputStream(validationErrorBytes));
		} catch (JsonProcessingException e) {
			throw new EncodeException("Issue serializing error report details", e);
		}
	}

	public List<Detail> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<Detail> warnings) {
		this.warnings = warnings;
	}

	/**
	 * Get the {@link Source} for the raw QPP validation errors (if any).
	 *
	 * @return {@link Source} for the raw QPP validation errors.
	 */
	public Source getRawValidationErrorsOrEmptySource() {
		String raw = (qppValidationDetails != null) ? qppValidationDetails : "";
		byte[] rawValidationErrorBytes = raw.getBytes(StandardCharsets.UTF_8);
		return new InputStreamSupplierSource("RawValidationErrors", new ByteArrayInputStream(rawValidationErrorBytes));
	}

	/**
	 * Gets the purpose of the conversion
	 *
	 * @return The purpose of the conversion, for example \"Test\". Treat null as a
	 *         standard production call
	 */
	public String getPurpose() {
		return source.getPurpose();
	}
}
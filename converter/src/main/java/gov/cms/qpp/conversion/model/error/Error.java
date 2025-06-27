package gov.cms.qpp.conversion.model.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Holds a list of validation errors associated with a single source.
 *
 * The source could be a file, a stream, or some other entity.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error implements Serializable {
	private static final long serialVersionUID = 1596644641404778774L;
	private String sourceIdentifier;
	private String type;
	private String message;
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonProperty("details")
	private List<Detail> details = new ArrayList<>();

	/**
	 * Constructs an empty {@code Error}.
	 */
	public Error() {
		// empty on purpose
	}

	/**
	 * Constructs an {@code Error} with the specified source identifier and list of
	 * {@link Detail}.
	 *
	 * @param sourceIdentifier The identifier of a source that contains the validation errors
	 * @param details The list of {@code Detail}s.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	public Error(String sourceIdentifier, List<Detail> details) {
		this.sourceIdentifier = sourceIdentifier;
		this.details = details;
	}

	/**
	 * Gets the source identifier.
	 *
	 * @return The source identifier.
	 */
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	/**
	 * Sets the source identifier.
	 *
	 * @param sourceIdentifier The source identifier.
	 */
	public void setSourceIdentifier(final String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}

	/**
	 * Gets the type.
	 *
	 * @return The type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type The type.
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets the message.
	 *
	 * @return The message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message The message.
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * getDetails returns a copy of the list of ValidationErrors
	 * so callers cannot modify the internal list.
	 *
	 * @return A new list of {@link Detail}, or null if there are none.
	 */
	@JsonProperty("details")
	public List<Detail> getDetails() {
		return details == null
				? null
				: new ArrayList<>(details);
	}

	/**
	 * setDetails sets the internal List of Detail
	 *
	 * @param details A list of ValidationErrors.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	@JsonProperty("details")
	public void setDetails(final List<Detail> details) {
		this.details = details;
	}

	/**
	 * addValidationError will add an error to the list of validation errors
	 *
	 * @param detail The Detail to add.
	 */
	public void addValidationError(final Detail detail) {
		if (details == null) {
			details = new ArrayList<>();
		}
		details.add(detail);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("sourceIdentifier", sourceIdentifier)
				.add("type", type)
				.add("message", message)
				.add("details", details)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Error)) {
			return false;
		}
		Error that = (Error) o;
		return Objects.equals(sourceIdentifier, that.sourceIdentifier)
				&& Objects.equals(type, that.type)
				&& Objects.equals(message, that.message)
				&& Objects.equals(details, that.details);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceIdentifier, type, message, details);
	}
}

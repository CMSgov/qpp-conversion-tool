package gov.cms.qpp.conversion.model.error;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import gov.cms.qpp.conversion.model.Node;

/**
 * Holds the error information from Validators.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Detail implements Serializable {
	private static final long serialVersionUID = 8818544157552590676L;

	@JsonProperty("errorCode")
	private Integer errorCode;
	@JsonProperty("message")
	private String message;
	@JsonProperty("path")
	private String path = "";
	@JsonProperty("value")
	private String value;
	@JsonProperty("type")
	private String type;

	/**
	 * Dummy constructor for ORM
	 */
	public Detail() {
		//Dummy constructor for jackson mapping
	}

	/**
	 * Copy constructor
	 */
	public Detail(Detail detail) {
		errorCode = detail.errorCode;
		message = detail.message;
		path = detail.path;
		value = detail.value;
		type = detail.type;
	}

	/**
	 * Creates a mutable Detail based on the given error and node
	 */
	public static Detail forErrorAndNode(LocalizedError error, Node node) {
		Objects.requireNonNull(node, "node");

		Detail detail = forErrorCode(error);
		detail.setPath(node.getPath());
		return detail;
	}

	/**
	 * Creates a mutable Detail based on the given error
	 * @param error error to be added
	 * @return
	 */
	public static Detail forErrorCode(LocalizedError error) {
		Objects.requireNonNull(error, "error");

		Detail detail = new Detail();
		detail.setErrorCode(error.getErrorCode().getCode());
		detail.setMessage(error.getMessage());
		return detail;
	}

	/**
	 * The code for the error
	 *
	 * @return An {@link ErrorCode}
	 */
	@JsonProperty("errorCode")
	public Integer getErrorCode() {
		return errorCode;
	}

	@JsonProperty("errorCode")
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * A description of what this error is about.
	 *
	 * @return An error description.
	 */
	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@JsonProperty("message")
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the path that this error references.
	 *
	 * @return The path that this error references.
	 */
	@JsonProperty("path")
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path that this error references.
	 *
	 * @param path The path that this error references.
	 */
	@JsonProperty("path")
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets the value that this error references.
	 *
	 * @return The value that this error references.
	 */
	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the type that this error references.
	 *
	 * @return The type that this error references.
	 */
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("errorCode", errorCode)
				.add("message", message)
				.add("path", path)
				.add("value", value)
				.add("type", type)
				.toString();
	}

	/**
	 * Evaluate equality of state.
	 *
	 * @param o Object to compare against
	 * @return evaluation
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Detail that = (Detail) o;
		boolean equals = true; // doing equals this way to avoid making jacoco/sonar unhappy
		equals &= Objects.equals(errorCode, that.errorCode);
		equals &= Objects.equals(message, that.message);
		equals &= Objects.equals(path, that.path);
		equals &= Objects.equals(value, that.value);
		equals &= Objects.equals(type, that.type);
		return equals;
	}

	/**
	 * get object hash code
	 *
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(errorCode, message, path, value, type);
	}
}

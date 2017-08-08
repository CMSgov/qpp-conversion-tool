package gov.cms.qpp.conversion.model.error;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds the error information from Validators.
 */
public class Detail {

	@JsonProperty("message")
	private String message;
	@JsonProperty("path")
	private String path = "";
	@JsonProperty("value")
	private String value;
	@JsonProperty("type")
	private String type;

	/**
	 * Dummy constructor for Jackson mapping
	 */
	public Detail() {
		//Dummy constructor for jackson mapping
	}

	/**
	 * Copy constructor
	 */
	public Detail(Detail detail) {
		this(detail.getMessage(), detail.getPath(), detail.getValue(), detail.getType());
	}

	/**
	 * Constructs a {@code Detail} with just a description.
	 *
	 * @param text A description of the error.
	 */
	public Detail(String text) {
		this.message = text;
	}

	/**
	 * Constructs a {@code Detail} with a description and an path to point where the error is in the original document.
	 *
	 * @param text A description of the error.
	 * @param path A path to where the error is.
	 */
	public Detail(String text, String path) {
		this.message = text;
		this.path = path;
	}

	/**
	 * Constructs a {@code Detail} with a description and an path to point where the error is in the original document
	 * as well as stating the offending value and a classification {@link Detail#type}.
	 *
	 * @param text A description of the error.
	 * @param path A path to where the error is.
	 * @param value The offending value.
	 * @param type A classification of the error.
	 */
	public Detail(String text, String path, String value, String type) {
		this(text, path);
		this.value = value;
		this.type = type;
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
	public void setMessage(String newMessage) {
		message = newMessage;
	}

	/**
	 * Gets the path that this error references.
	 *
	 * @return The path that this error references.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path that this error references.
	 *
	 * @param newPath The path that this error references.
	 */
	public void setPath(String newPath) {
		path = newPath;
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

	public void setValue(String newValue) {
		value = newValue;
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

	public void setType(String newType) {
		type = newType;
	}

	/**
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Detail{");
		sb.append("message='").append(message).append('\'');
		sb.append(", path='").append(path).append('\'');
		sb.append(", value='").append(value).append('\'');
		sb.append(", type='").append(type).append('\'');
		sb.append('}');
		return sb.toString();
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

		Detail detail = (Detail) o;

		return Objects.equals(message, detail.message) &&
				Objects.equals(path, detail.path) &&
				Objects.equals(value, detail.value) &&
				Objects.equals(type, detail.type);
	}

	/**
	 * get object hash code
	 *
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(message, path, value, type);
	}
}

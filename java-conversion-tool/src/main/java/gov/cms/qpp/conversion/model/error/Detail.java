package gov.cms.qpp.conversion.model.error;

import java.io.Serializable;

/**
 * Holds the error information from Validators.
 */
public class Detail implements Serializable {
	private static final long serialVersionUID = 8818544157552590676L;
	private String message;
	private String path = "";
	private String value;
	private String type;

	/**
	 * Dummy constructor for Jackson mapping
	 */
	public Detail() {
		//Dummy constructor for jackson mapping
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
	public String getMessage() {
		return message;
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
	 * Gets the value that this error references.
	 *
	 * @return The value that this error references.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets the type that this error references.
	 *
	 * @return The type that this error references.
	 */
	public String getType() {
		return type;
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
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Detail detail = (Detail) o;

		if (message != null ? !message.equals(detail.message) : detail.message != null) return false;
		if (path != null ? !path.equals(detail.path) : detail.path != null) return false;
		if (value != null ? !value.equals(detail.value) : detail.value != null) return false;
		return type != null ? type.equals(detail.type) : detail.type == null;
	}

	/**
	 * get object hash code
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		int result = message != null ? message.hashCode() : 0;
		result = 31 * result + (path != null ? path.hashCode() : 0);
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}

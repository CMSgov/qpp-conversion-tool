package gov.cms.qpp.conversion.model.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;

import gov.cms.qpp.conversion.model.Node;

import java.io.Serializable;
import java.util.Objects;

/**
 * Holds the error information from Validators.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Detail implements Serializable {
	private static final long serialVersionUID = 8818544157552590676L;

	private Integer errorCode;
	private String message;
	private String path = "";
	private String value;
	private String type;
	private String location;

	/**
	 * Dummy constructor for ORM
	 */
	public Detail() {
		//Dummy constructor for jackson mapping
	}

	/**
	 * Copy constructor
	 *
	 * @param detail object from which to copy
	 */
	public Detail(Detail detail) {
		errorCode = detail.errorCode;
		message = detail.message;
		path = detail.path;
		value = detail.value;
		type = detail.type;
		location = detail.location;
	}

	/**
	 * Creates a mutable Detail based on the given error and node
	 *
	 * @param error error to be added
	 * @param node node that gives the error context
	 * @return detail for given error
	 */
	public static Detail forErrorAndNode(LocalizedError error, Node node) {
		Objects.requireNonNull(node, "node");

		Detail detail = forErrorCode(error);
		detail.setPath(node.getPath());
		detail.setLocation(computeLocation(node));

		return detail;
	}

	/**
	 * Creates a mutable Detail based on the given error
	 *
	 * @param error error to be added
	 * @return detail for given error
	 */
	public static Detail forErrorCode(LocalizedError error) {
		Objects.requireNonNull(error, "error");

		Detail detail = new Detail();
		detail.setErrorCode(error.getErrorCode().getCode());
		detail.setMessage(error.getMessage());
		return detail;
	}

	private static String computeLocation(Node node) {

		String location = null;

		Node importantParentNode = node.findParentNodeWithHumanReadableTemplateId();

		if (importantParentNode != null) {
			String importantParentTitle = importantParentNode.getType().getHumanReadableTitle();
			String possibleMeasureId = importantParentNode.getValue("measureId");

			location = importantParentTitle;

			if (!StringUtils.isEmpty(possibleMeasureId)) {
				location += " " + possibleMeasureId;
			}
		}

		return location;
	}

	/**
	 * The code for the error
	 *
	 * @return An {@link ErrorCode}
	 */
	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * A description of what this error is about.
	 *
	 * @return An error description.
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
	 * @param path The path that this error references.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets the value that this error references.
	 *
	 * @return The value that this error references.
	 */
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the type that this error references.
	 *
	 * @return The type that this error references.
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
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
		equals &= Objects.equals(location, that.location);
		return equals;
	}

	/**
	 * get object hash code
	 *
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(errorCode, message, path, value, type, location);
	}
}

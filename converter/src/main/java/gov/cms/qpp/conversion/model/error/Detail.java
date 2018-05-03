package gov.cms.qpp.conversion.model.error;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

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
	private Integer line;
	private Integer column;
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
		line = detail.line;
		column = detail.column;
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
		Detail detail = forErrorCode(error);

		if (node != null) {
			detail.setPath(node.getPath());

			if (node.getLine() != Node.DEFAULT_LOCATION_NUMBER) {
				detail.setLine(node.getLine());
			}

			if (node.getColumn() != Node.DEFAULT_LOCATION_NUMBER) {
				detail.setColumn(node.getColumn());
			}
		}
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

		StringBuilder location = new StringBuilder();

		Node importantParentNode = node.findParentNodeWithHumanReadableTemplateId();

		if (importantParentNode != null) {
			String importantParentTitle = importantParentNode.getType().getHumanReadableTitle();
			String possibleMeasureId = importantParentNode.getValue("measureId");

			location.append(importantParentTitle);

			if (!StringUtils.isEmpty(possibleMeasureId)) {
				location.append(" ");
				location.append(possibleMeasureId);
			}
		}

		return location.toString();
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
	 * Gets the line of the submitted document that caused this error
	 *
	 * @return The line of the submitted document that caused this error
	 */
	public Integer getLine() {
		return line;
	}

	/**
	 * Sets the line of the submitted document that caused this error
	 *
	 * @param path The line of the submitted document that caused this error
	 */
	public void setLine(Integer line) {
		this.line = line;
	}

	/**
	 * Gets the line of the submitted document that caused this error
	 *
	 * @return The line of the submitted document that caused this error
	 */
	public Integer getColumn() {
		return column;
	}

	/**
	 * Sets the column of the submitted document that caused this error
	 *
	 * @param path The column of the submitted document that caused this error
	 */
	public void setColumn(Integer column) {
		this.column = column;
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

	/**
	 * The human readable location where this error occurred.
	 *
	 * @return The location.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the human readable location where this error occurred.
	 *
	 * @param location The location.
	 */
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
				.add("line", line)
				.add("column", column)
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
		return new EqualsBuilder()
				.append(errorCode, that.errorCode)
				.append(message, that.message)
				.append(path, that.path)
				.append(value, that.value)
				.append(type, that.type)
				.append(line, that.line)
				.append(column, that.column)
				.append(location, that.location)
				.isEquals();
	}

	/**
	 * get object hash code
	 *
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(errorCode, message, path, value, type, line, column, location);
	}
}

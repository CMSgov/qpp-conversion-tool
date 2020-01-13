package gov.cms.qpp.conversion.model.error;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.util.MeasureConfigHelper;

/**
 * Holds the error information from {@link Validator}s.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Detail implements Serializable {
	private static final long serialVersionUID = 8818544157552598922L;

	private Integer problemCode;
	private String message;
	private String value;
	private String type;
	private Location location = new Location();

	/**
	 * Default constructor to support ORM and the copy constructor
	 */
	public Detail() {
		// Dummy constructor for jackson mapping and to support copy constructor
	}

	/**
	 * Copy constructor
	 *
	 * @param copy object to copy
	 */
	public Detail(Detail copy) {
		problemCode = copy.problemCode;
		message = copy.message;
		value = copy.value;
		type = copy.type;
		location = new Location(copy.location);
	}

	/**
	 * Creates a mutable {@link Detail} based on the given {@link LocalizedProblem} and {@link Node}
	 *
	 * @param problem error to be added
	 * @param node node that gives the error context
	 * @return detail for given error
	 */
	public static Detail forProblemAndNode(LocalizedProblem problem, Node node) {
		Detail detail = forProblemCode(problem);

		if (node != null) {
			Location location = detail.getLocation();
			if (node.getLine() != Node.DEFAULT_LOCATION_NUMBER) {
				location.setLine(node.getLine());
			}

			if (node.getColumn() != Node.DEFAULT_LOCATION_NUMBER) {
				location.setColumn(node.getColumn());
			}

			location.setPath(node.getOrComputePath());
			location.setLocation(computeLocation(node));
		}

		return detail;
	}

	/**
	 * Creates a mutable {@link Detail} based on the given {@link LocalizedProblem}
	 *
	 * @param problem error to be added
	 * @return detail for given error
	 */
	public static Detail forProblemCode(LocalizedProblem problem) {
		Objects.requireNonNull(problem, "error");

		Detail detail = new Detail();
		detail.setProblemCode(problem.getProblemCode().getCode());
		detail.setMessage(problem.getMessage());
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
				String possibleElectronicMeasureId = MeasureConfigHelper.getMeasureConfigIdByUuidOrDefault(possibleMeasureId);
				if (!StringUtils.isEmpty(possibleElectronicMeasureId)) {
					location.append(" (");
					location.append(possibleElectronicMeasureId);
					location.append(")");
				}
			}
		}

		return location.toString();
	}

	/**
	 * The code for the error
	 *
	 * @return An {@link ProblemCode}
	 */
	public Integer getProblemCode() {
		return problemCode;
	}

	public void setProblemCode(Integer problemCode) {
		this.problemCode = problemCode;
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
	 * The {@link Location} object containing human-readable location data such as line and column numbers
	 *
	 * @return The location.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the {@link Location} object where this error occurred.
	 *
	 * @param location The location.
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("problemCode", problemCode)
				.add("message", message)
				.add("value", value)
				.add("type", type)
				.add("location", location)
				.toString();
	}

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
				.append(problemCode, that.problemCode)
				.append(message, that.message)
				.append(value, that.value)
				.append(type, that.type)
				.append(location, that.location)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return Objects.hash(problemCode, message, value, type, location);
	}
}

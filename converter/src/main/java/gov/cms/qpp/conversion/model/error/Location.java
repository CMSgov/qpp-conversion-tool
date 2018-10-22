package gov.cms.qpp.conversion.model.error;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.MoreObjects;

/**
 * Location data holder object
 */
public class Location implements Serializable {

	private static final long serialVersionUID = 1812341767532590176L;

	private String location;
	private String path = "";
	private Integer line;
	private Integer column;

	/**
	 * Default constructor to support ORM and the copy constructor
	 */
	public Location() {
	}

	/**
	 * Copy constructor
	 *
	 * @param copy object to copy
	 */
	public Location(Location copy) {
		if (copy != null) {
			location = copy.location;
			path = copy.path;
			line = copy.line;
			column = copy.column;
		}
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
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("location", location)
				.add("path", path)
				.add("line", line)
				.add("column", column)
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

		Location that = (Location) o;
		return new EqualsBuilder()
				.append(location, that.location)
				.append(path, that.path)
				.append(line, that.line)
				.append(column, that.column)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return Objects.hash(location, path, line, column);
	}

}

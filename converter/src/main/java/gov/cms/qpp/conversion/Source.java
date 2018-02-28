package gov.cms.qpp.conversion;

import java.io.InputStream;

/**
 * An object that defines a source of information.
 */
public interface Source {

	/**
	 * The name.
	 *
	 * @return The name of the source.
	 */
	String getName();

	/**
	 * An {@link InputStream} representation of the source.
	 *
	 * @return An InputStream representing the source.
	 */
	InputStream toInputStream();

	/**
	 * The size of the source.
	 *
	 * @return The source's size.
	 */
	long getSize();

	/**
	 * Whether or not the source is test data
	 *
	 * @return The test status of the source
	 */
	boolean isTest();
}

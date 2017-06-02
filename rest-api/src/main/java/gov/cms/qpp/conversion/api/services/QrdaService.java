package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.encode.JsonWrapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface of the QRDA-III to QPP Conversion Service
 */
public interface QrdaService {

	/**
	 * Converts a given a input stream with to conversion result content
	 *
	 * @param fileInputStream Object to be converted
	 * @return Results of the conversion
	 * @throws IOException If error occurs during file upload or conversion
	 */
	JsonWrapper convertQrda3ToQpp(InputStream fileInputStream);
}

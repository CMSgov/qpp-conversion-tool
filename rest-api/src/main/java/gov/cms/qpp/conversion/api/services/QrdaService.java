package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.api.model.ConversionResult;

import java.io.IOException;
import java.io.InputStream;

public interface QrdaService {
	public ConversionResult convertQrda3ToQpp(InputStream fileInputStream) throws IOException;
}

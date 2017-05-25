package gov.cms.qpp.conversion.api.services;

import java.io.IOException;
import java.io.InputStream;

public interface QrdaService {
	public String convertQrda3ToQpp(InputStream fileInputStream) throws IOException;
}

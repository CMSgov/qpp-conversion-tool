package gov.cms.qpp.conversion.api.services;


import java.io.InputStream;

public interface AuditService {
	void audit(InputStream fileContent, InputStream qppContent);
}

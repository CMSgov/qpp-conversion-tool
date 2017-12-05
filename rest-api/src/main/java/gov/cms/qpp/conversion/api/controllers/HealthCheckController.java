package gov.cms.qpp.conversion.api.controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.cms.qpp.conversion.api.model.HealthCheck;
import gov.cms.qpp.conversion.api.services.VersionService;

/**
 * Controller to simply respond with status 200 with a GET /health call.
 */
@RestController
@RequestMapping("/health")
public class HealthCheckController {

	@Autowired
	private VersionService version;

	/**
	 * Invoked with an HTTP GET call.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<HealthCheck> health() {
		HealthCheck healthCheck = new HealthCheck();
		healthCheck.setEnvironmentVariables(new ArrayList<>(System.getenv().keySet()));
		healthCheck.setSystemProperties(
				System.getProperties().keySet().stream().map(String::valueOf).collect(Collectors.toList()));
		healthCheck.setImplementationVersion(version.getImplementationVersion());

		return ResponseEntity.ok(healthCheck);
	}
}

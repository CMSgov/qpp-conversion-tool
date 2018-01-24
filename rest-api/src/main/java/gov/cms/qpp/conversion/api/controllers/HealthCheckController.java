package gov.cms.qpp.conversion.api.controllers;

import gov.cms.qpp.conversion.api.model.HealthCheck;
import gov.cms.qpp.conversion.api.services.VersionService;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to simply respond with status 200 with a GET /health call.
 */
@RestController
@RequestMapping("/health")
public class HealthCheckController {

	private VersionService version;

	/**
	 * Provide dependency
	 *
	 * @param version reference to the version service
	 */
	public HealthCheckController(final VersionService version) {
		this.version = version;
	}

	/**
	 * Invoked with an HTTP GET call.
	 *
	 * @return health check of version, environment variables, and system properties
	 */
	@GetMapping
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

package gov.cms.qpp.conversion.api.controllers;

import gov.cms.qpp.conversion.api.model.HealthCheck;
import gov.cms.qpp.conversion.api.services.VersionService;

import java.util.ArrayList;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to simply respond with status 200 with a GET /health call.
 */
@Tag(name = "Health Check")
@RestController
@RequestMapping("/health")
public class HealthCheckController {

	private final VersionService version;

	/**
	 * Provide dependency
	 *
	 * @param version reference to the version service
	 */
	public HealthCheckController(VersionService version) {
		this.version = version;
	}

	/**
	 * Invoked with an HTTP GET call.
	 *
	 * @return health check of version, environment variables, and system properties
	 */
	@Operation(summary = "Gets the current state of the API.", description = "Returns the environment variables, system properties and implementation version of the conversion tool.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = HealthCheck.class), mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
			@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody HealthCheck health() {
		HealthCheck healthCheck = new HealthCheck();
		healthCheck.setEnvironmentVariables(new ArrayList<>(System.getenv().keySet()));
		healthCheck.setSystemProperties(
				System.getProperties().keySet().stream().map(String::valueOf).collect(Collectors.toList()));
		healthCheck.setImplementationVersion(version.getImplementationVersion());

		return healthCheck;
	}
}

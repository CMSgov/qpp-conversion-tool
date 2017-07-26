package gov.cms.qpp.conversion.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to simply respond with status 200 with a GET /health call.
 */
@RestController
@RequestMapping("/health")
public class HealthCheckController {

	/**
	 * Invoked with an HTTP GET call.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public void health() {
		//for the health check
	}
}

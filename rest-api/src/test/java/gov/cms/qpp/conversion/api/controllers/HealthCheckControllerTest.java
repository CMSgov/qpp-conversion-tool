package gov.cms.qpp.conversion.api.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.api.services.VersionService;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HealthCheckControllerTest {

	@InjectMocks
	private HealthCheckController service;

	@Mock
	private VersionService version;

	@Test
	void testHealthCheckContainsAllSystemProperties() {
		List<String> systemProperties = System.getProperties().keySet().stream().map(String::valueOf)
				.collect(Collectors.toList());

		Truth.assertThat(service.health().getSystemProperties()).containsExactlyElementsIn(systemProperties);
	}

	@Test
	void testHealthCheckContainsAllEnvironmentVariables() {
		Set<String> environmentVariables = System.getenv().keySet();

		Truth.assertThat(service.health().getEnvironmentVariables())
				.containsExactlyElementsIn(environmentVariables);
	}

	@Test
	void testHealthCheckContainsImplementationVersion() {
		Mockito.when(version.getImplementationVersion()).thenReturn("Mock Version");

		Truth.assertThat(service.health().getImplementationVersion()).isEqualTo("Mock Version");
	}

}

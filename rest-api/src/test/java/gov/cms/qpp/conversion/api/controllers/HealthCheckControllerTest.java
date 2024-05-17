package gov.cms.qpp.conversion.api.controllers;

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
	void testHealthCheckContainsImplementationVersion() {
		Mockito.when(version.getImplementationVersion()).thenReturn("Mock Version");

		Truth.assertThat(service.health().getImplementationVersion()).isEqualTo("Mock Version");
	}

}

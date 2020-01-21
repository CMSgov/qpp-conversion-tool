package gov.cms.qpp.conversion.api.services.internal;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.api.services.internal.ManifestVersionService;

class ManifestVersionServiceTest {

	@Test
	void testReadsVersionFromManifest() {
		// in this context, getImplementationVersion will return the surefire test runner version (if ran with our maven files)
		Truth.assertThat(new ManifestVersionService().getImplementationVersion()).isNotEmpty();
	}

}

package gov.cms.qpp.conversion.api.services;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;

class VersionServiceImplTest {

	@Test
	void testReadsVersionFromManifest() {
		// in this context, getImplementationVersion will return the surefire test runner version (if ran with our maven files)
		Truth.assertThat(new VersionServiceImpl().getImplementationVersion()).isNotEmpty();
	}

}

package gov.cms.qpp.conversion.api.services.internal;

import org.springframework.stereotype.Service;

import com.jcabi.manifests.Manifests;

import gov.cms.qpp.conversion.api.services.VersionService;

/**
 * Implementation of VersionService using META-INF/MANIFEST.mf
 */
@Service
public class ManifestVersionService implements VersionService {

	@Override
	public String getImplementationVersion() {
		return Manifests.read("Implementation-Version");
	}

}

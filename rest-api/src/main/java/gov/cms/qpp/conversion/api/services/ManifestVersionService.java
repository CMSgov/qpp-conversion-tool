package gov.cms.qpp.conversion.api.services;

import org.springframework.stereotype.Service;

import com.jcabi.manifests.Manifests;

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

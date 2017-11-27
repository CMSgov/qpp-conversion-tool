package gov.cms.qpp.conversion.api.services;

import org.springframework.stereotype.Service;

import com.jcabi.manifests.Manifests;

@Service
public class VersionServiceImpl implements VersionService {

	@Override
	public String getImplementationVersion() {
		return Manifests.read("Implementation-Version");
	}

}

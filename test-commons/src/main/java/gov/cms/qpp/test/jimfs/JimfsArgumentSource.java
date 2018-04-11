package gov.cms.qpp.test.jimfs;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import com.google.common.jimfs.Configuration;

public class JimfsArgumentSource implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
		return Stream.of(Configuration.unix(), Configuration.osX(), Configuration.windows())
				.map(FileTestHelper::createMockFileSystem)
				.map(Arguments::of);
	}

}

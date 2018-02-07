package gov.cms.qpp.test.jimfs;

import java.nio.file.FileSystem;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;

public interface JimfsContract {

	static Stream<FileSystem> jimfs() {
		return Stream.of(Configuration.unix(), Configuration.osX(), Configuration.windows())
				.map(FileTestHelper::createMockFileSystem);
	}

}

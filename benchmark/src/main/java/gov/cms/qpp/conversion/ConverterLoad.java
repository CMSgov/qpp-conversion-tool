package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.reflections.util.ClasspathHelper;

import java.io.InputStream;


public class ConverterLoad extends AbstractJavaSamplerClient {

	@Override
	public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
		String fileName = javaSamplerContext.getParameter("fileName", "valid-QRDA-III-latest.xml");
		boolean success = true;
		SampleResult result = new SampleResult();
		result.sampleStart();
		runConversion(fileName);
		result.sampleEnd();
		result.setSuccessful(success);
		return result;
	}

	private void runConversion(String fileName) {
		InputStream inStream = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);
		Converter converter = new Converter(inStream);
		JsonWrapper qpp = converter.transform();
	}
}

package gov.cms.qpp.conversion;


import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPFileArg;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertThat;

public class ConverterLoad {
	private static StandardJMeterEngine jmeter;

	@BeforeClass
	public static void setupClass() throws IOException {
		jmeter = new StandardJMeterEngine();

		//JMeter initialization (properties, log levels, locale, etc)
		JMeterUtils.setJMeterHome("src/test/resources/");
		JMeterUtils.loadJMeterProperties("src/test/resources/jmeter.properties");
		JMeterUtils.initLocale();
	}

	private static HTTPSamplerProxy makeSampler() throws IOException {
		HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
		httpSampler.setContentEncoding("UTF-8");
		httpSampler.setProtocol("http");
		httpSampler.setDomain("184.73.24.93");
		httpSampler.setPort(2680);
		httpSampler.setPath("v1/qrda3");
		httpSampler.setMethod("POST");
		httpSampler.setPostBodyRaw(true);
		httpSampler.setDoMultipartPost(true);
		httpSampler.setHTTPFiles(new HTTPFileArg[] {getFileArg()});
		httpSampler.setConnectTimeout("1000");
		httpSampler.setResponseTimeout("2000");
		return httpSampler;
	}

	private static HTTPFileArg getFileArg() throws IOException {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		HTTPFileArg fileArg = new HTTPFileArg();
		fileArg.setPath(path.toFile().getCanonicalPath());
		fileArg.setMimeType("text/xml");
		fileArg.setParamName("file");
		return fileArg;
	}

	@Test
	public void converterLoad10Test() throws IOException {
		Map<String, String> results = executePlan(1, 10, 5);
		assertThat(Long.valueOf(results.get("Average")), Matchers.lessThan(3000l));
		assertThat(Long.valueOf(results.get("ErrorCount")), Matchers.equalTo(0l));
	}

	@Test
	public void converterFindBreakingPoint() throws IOException {
		int errorCount = 0;
		int numThreads = 0;
		while (errorCount < 1) {
			numThreads += 10;
			Map<String, String> results = executePlan(1, numThreads, 5);
			errorCount = Integer.valueOf(results.get("ErrorCount"));
		}

		assertThat(numThreads, Matchers.greaterThan(20));
	}

	private Map<String, String> executePlan(int numLoops, int numThreads, int rampUp) throws IOException {
		HashTree testPlanTree = new HashTree();
		HTTPSamplerProxy httpSampler = makeSampler();

		LoopController loopController = new LoopController();
		loopController.setLoops(numLoops);
		loopController.addTestElement(httpSampler);
		loopController.setFirst(true);
		loopController.initialize();

		ThreadGroup threadGroup = new ThreadGroup();
		threadGroup.setNumThreads(numThreads);
		threadGroup.setRampUp(rampUp);
		threadGroup.setSamplerController(loopController);

		TestPlan testPlan = new TestPlan("JMeter regression test");
		HashTree tpConfig = testPlanTree.add(testPlan);
		HashTree tgConfig = tpConfig.add(threadGroup);
		HashTree samplerConfig = tgConfig.add(httpSampler);

		//Summarizer
		Summariser summer = null;
		String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
		if (summariserName.length() > 0) {
			summer = new Summariser(summariserName);
		}

		//String logFile = "report.jtl";
		ResultCollector logger = new ResultCollector(summer);
		//logger.setFilename(logFile);

		tgConfig.add(logger);
		//SaveService.saveTree(testPlanTree, new FileOutputStream("jmeter_api_sample.jmx"));

		jmeter.configure(testPlanTree);
		jmeter.run();
		//return extractTotals(summer);
		return extractTotals(summer);
	}

	private Map<String, String> extractTotals(Summariser summer) {
		Map<String, String> values = new HashMap<>();
		try {
			Field gross = Summariser.class.getDeclaredField("myTotals");
			gross.setAccessible(true);
			Object obj = gross.get(summer);
			Field total = obj.getClass().getDeclaredField("total");
			total.setAccessible(true);
			Object totalObj = total.get(obj);
			Method[] methods = totalObj.getClass().getDeclaredMethods();
			for(Method method : methods) {
				if (method.getName().startsWith("get")){
					method.setAccessible(true);
					values.put(method.getName().replace("get", ""), "" + method.invoke(totalObj));
				}
			}
		} catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace(System.out);
		}
		return values;
	}
}

package gov.cms.qpp.conversion;


import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.Test;

public class ConverterLoad {
	@Test
	public void converterLoad100Test() {
		StandardJMeterEngine jmeter = new StandardJMeterEngine();

		//JMeter initialization (properties, log levels, locale, etc)
		JMeterUtils.setJMeterHome("src/test/resources/");
		JMeterUtils.loadJMeterProperties("src/test/resources/jmeter.properties");
		//JMeterUtils.loadJMeterProperties("src/test/resources/saveservice.properties");
		JMeterUtils.initLogging();
		JMeterUtils.initLocale();
		HashTree testPlanTree = new HashTree();

		Arguments args = new Arguments();
		args.addArgument("", "qrda", "=");

		HTTPSampler httpSampler = new HTTPSampler();
		httpSampler.setDomain("184.73.24.93");
		httpSampler.setPort(2680);
		httpSampler.setPath("v1/qrda3");
		httpSampler.setMethod("POST");
		httpSampler.setPostBodyRaw(true);
		httpSampler.setDoMultipartPost(true);
		httpSampler.setArguments(args);
//		<boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
//          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
//            <collectionProp name="Arguments.arguments">
//              <elementProp name="" elementType="HTTPArgument">
//                <boolProp name="HTTPArgument.always_encode">false</boolProp>
//                <stringProp name="Argument.value">meep</stringProp>
//                <stringProp name="Argument.metadata">=</stringProp>
//              </elementProp>
//            </collectionProp>
//          </elementProp>


				//http://184.73.24.93:2680/v1/qrda3

		LoopController loopController = new LoopController();
		loopController.setLoops(10);
		loopController.addTestElement(httpSampler);
		loopController.setFirst(true);
		loopController.initialize();

		ThreadGroup threadGroup = new ThreadGroup();
		threadGroup.setNumThreads(1);
		threadGroup.setRampUp(5);
		threadGroup.setSamplerController(loopController);

//		ResultCollector resultCollector = new ResultCollector();
//		resultCollector.setFilename("../target/jmeter/report.jtl");
//		SampleSaveConfiguration saveConfiguration = new SampleSaveConfiguration();
//		saveConfiguration.setAsXml(true);
//		saveConfiguration.setCode(true);
//		saveConfiguration.setLatency(true);
//		saveConfiguration.setTime(true);
//		saveConfiguration.setTimestamp(true);
//		resultCollector.setSaveConfig(saveConfiguration);

		TestPlan testPlan = new TestPlan("JMeter regression test");

		//testPlanTree.add("resultCollector", resultCollector);
		testPlanTree.add("testPlan", testPlan);
		testPlanTree.add("loopController", loopController);
		testPlanTree.add("threadGroup", threadGroup);
		testPlanTree.add("httpSampler", httpSampler);

		//Summarizer
		Summariser summer = null;
		String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");//$NON-NLS-1$
		if (summariserName.length() > 0) {
			summer = new Summariser(summariserName);
		}

		String logFile = "report.jtl";
		ResultCollector logger = new ResultCollector(summer);
		logger.setFilename(logFile);

		testPlanTree.add(testPlanTree.getArray()[0], logger);

		jmeter.configure(testPlanTree);
		jmeter.run();
		System.out.println("done");
	}
}

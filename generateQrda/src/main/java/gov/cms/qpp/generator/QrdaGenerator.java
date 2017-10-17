package gov.cms.qpp.generator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.Strata;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulations;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QrdaGenerator {
	private static List<MeasureConfig> measureConfigs = MeasureConfigs.getMeasureConfigs();

	private Mustache submission;
	private Mustache subpopulation;
	private Mustache performanceRate;

	public static void main(String... args) throws IOException {
		QrdaGenerator generator = new QrdaGenerator();
		generator.generate();
	}

	private QrdaGenerator() {
		MustacheFactory mf = new DefaultMustacheFactory();
		submission = mf.compile("submission-template.xml");
		subpopulation = mf.compile("subpopulation-template.xml");
		performanceRate = mf.compile("performance-rate-template.xml");
	}

	private void generate() throws IOException {
		List<MeasureConfig> configs = measureConfigs.stream()
				.filter(measureConfig -> measureConfig.getCategory().equals("quality") &&
						measureConfig.getElectronicMeasureId() != null &&
						!measureConfig.getElectronicMeasureId().isEmpty())
//				.filter(measureConfig -> measureConfig.getElectronicMeasureId().equals("CMS165v5"))
				.collect(Collectors.toList());
		writeQrda(configs);
	}

	private void writeQrda(List<MeasureConfig> configs) throws IOException {
		Instant instant = Instant.now();

		submission.execute(
				new FileWriter("generated-qrda-" + instant.getEpochSecond() + ".xml"),
				new Context(configs)).flush();
	}

	private class Context {
		List<MeasureConfig> configs;
		Function generateIpop = uuid -> generateSubpopulation(uuid, SubPopulations.IPOP);
		Function generateDenom = uuid -> generateSubpopulation(uuid, SubPopulations.DENOM);
		Function generateDenex = uuid -> generateSubpopulation(uuid, SubPopulations.DENEX);
		Function generateDenexcep = uuid -> generateSubpopulation(uuid, SubPopulations.DENEXCEP);
		Function generateNumer = uuid -> generateSubpopulation(uuid, SubPopulations.NUMER);
		Function generatePerformanceRate = uuid -> generatePerformanceRate(uuid);

		private Context(List<MeasureConfig> configs) {
			this.configs = configs;
		}

		private String generatePerformanceRate(Object uuid) {
			Map<String, Object> ctx = new HashMap<>();
			ctx.put("uuid", uuid);
			return performanceRate.execute(new StringWriter(), ctx).toString();
		}

		private String generateSubpopulation(Object uuid, String type) {
			if (uuid == null || ((String) uuid).isEmpty()) {
				return "";
			}

			Map<String, Object> ctx = new HashMap<>();
			ctx.put("uuid", uuid);
			ctx.put("label", type);
			ctx.put("value", 10);
			ctx.put("total", 120);

			return subpopulation.execute(new StringWriter(), ctx).toString();
		}
	}
}

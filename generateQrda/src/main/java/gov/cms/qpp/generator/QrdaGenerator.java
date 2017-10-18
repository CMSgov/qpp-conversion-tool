package gov.cms.qpp.generator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
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

	private List<MeasureConfig> quality;
	private List<MeasureConfig> aci;
	private List<MeasureConfig> ia;

	public static void main(String... args) throws IOException {
		QrdaGenerator generator = new QrdaGenerator();
		generator.generate();
	}

	private QrdaGenerator() throws IOException {
		MustacheFactory mf = new DefaultMustacheFactory();
		submission = mf.compile("submission-template.xml");
		subpopulation = mf.compile("subpopulation-template.xml");
		performanceRate = mf.compile("performance-rate-template.xml");

		quality = filterQualityMeasures();
		aci = filterAciMeasures();
		ia = filterIaMeasures();
	}

	private List<MeasureConfig> filterQualityMeasures() throws IOException {
		return measureConfigs.stream()
				.filter(measureConfig -> measureConfig.getCategory().equals("quality") &&
						measureConfig.getElectronicMeasureId() != null &&
						!measureConfig.getElectronicMeasureId().isEmpty())
				.collect(Collectors.toList());
	}

	private List<MeasureConfig> filterAciMeasures() {
		return measureConfigs.stream()
				.filter(measureConfig -> measureConfig.getCategory().equals("aci"))
				.collect(Collectors.toList());
	}

	private List<MeasureConfig> filterIaMeasures() {
		return measureConfigs.stream()
				.filter(measureConfig -> measureConfig.getCategory().equals("ia"))
				.collect(Collectors.toList());
	}

	private void generate() throws IOException {
		Instant instant = Instant.now();

		submission.execute(
				new FileWriter("./qrda-files/large/comprehensive-qrda.xml"),
				new Context(quality, aci, ia)).flush();
	}

	private class Context {
		List<MeasureConfig> quality;
		List<MeasureConfig> aci;
		List<MeasureConfig> ia;
		Function generateIpop = uuid -> generateSubpopulation(uuid, SubPopulations.IPOP);
		Function generateDenom = uuid -> generateSubpopulation(uuid, SubPopulations.DENOM);
		Function generateDenex = uuid -> generateSubpopulation(uuid, SubPopulations.DENEX);
		Function generateDenexcep = uuid -> generateSubpopulation(uuid, SubPopulations.DENEXCEP);
		Function generateNumer = uuid -> generateSubpopulation(uuid, SubPopulations.NUMER);
		Function generatePerformanceRate = uuid -> generatePerformanceRate(uuid);


		private Context(List<MeasureConfig> quality, List<MeasureConfig> aci, List<MeasureConfig> ia) {
			this.quality = quality;
			this.aci = aci;
			this.ia = ia;
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

package gov.cms.qpp.conversion.correlation;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ValueOriginMapper {
	private List<Association> associations = new ArrayList<>();

	ValueOriginMapper() {
	}

	List<Association> getAssociations() {
		return associations;
	}

	@SuppressWarnings("unchecked")
	void mapIt(String base, Object holder) {
		if (holder instanceof Map) {
			map(base, (Map) holder);
		} else {
			map(base, (List) holder);
		}
	}

	@SuppressWarnings("unchecked")
	private void map(String base, Map<String, Object> toAssociate) {
		for (Map.Entry<String, Object> entry : toAssociate.entrySet()) {
			if (entry.getKey().equals("metadata_holder")) {
				continue;
			}
			String newBase = base + "." + entry.getKey();

			if (entry.getValue() instanceof Map || entry.getValue() instanceof List) {
				mapIt(newBase, entry.getValue());
			} else {
				Set<Map<String, String>> metadataSet = ((Set<Map<String, String>>)toAssociate.get("metadata_holder"));
				String xPath = getXpath(metadataSet, entry.getKey());
				if (xPath != null) {
					associations.add(
							new Association(xPath, base + "." + entry.getKey(),
									entry.getValue().toString()));
				}
			}
		}
	}

	private String getXpath(Set<Map<String, String>> metadataSet, String key) {
		String xPath = null;
		Map<String, String> current = null;
		if (metadataSet == null) {
			return xPath;
		}
		for (Map<String, String> metaMap : metadataSet) {
			String label = metaMap.get("encodeLabel");
			if (label.equals(key)) {
				String relative = PathCorrelator.getXpath(metaMap.get("template"), label, metaMap.get("nsuri"));
				xPath = (relative == null) ? metaMap.get("path") : metaMap.get("path") + "/" + relative;
				break;
			}
			if (current == null || metaMap.get("path").length() < current.get("path").length()) {
				current = metaMap;
				xPath = metaMap.get("path");
			}
		}
		return xPath;
	}

	private void map(String base, List<Object> toAssociate) {
		int index = 0;
		for (Object obj : toAssociate) {
			String newBase = base + "[" + index++ + "]";
			mapIt(newBase, obj);
		}
	}

	void writeAssociations() {
		Path path = Paths.get("qrdaToQppAssociations.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(path))
		{
			for (Association assoc : associations) {
				writer.write(assoc.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class Association {
		private String qrda;
		private String qpp;
		private Object value;

		Association(String qrdaPath, String qppPath, Object outValue) {
			this.qrda = qrdaPath;
			this.qpp = qppPath;
			this.value = outValue;
		}

		void setQrda(String qrdaValue) {
			this.qrda = qrdaValue;
		}

		String getQrda() {
			return qrda;
		}

		void setQpp(String qppValue) {
			this.qpp = qppValue;
		}

		String getQpp() {
			return qpp;
		}

		void setValue(String outValue) {
			this.value = outValue;
		}

		Object getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "qpp='" + qpp + '\'' +
					", qrda='" + qrda + '\'' +
					", value=" + value + '\n';
		}
	}
}

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
import java.util.stream.Collectors;

import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.JsonWrapper.Kind;

class ValueOriginMapper {
	private List<Association> associations = new ArrayList<>();

	ValueOriginMapper() {
	}

	List<Association> getAssociations() {
		return associations;
	}

	void mapItJsW(String base, JsonWrapper holder) {
		if (holder.isObject()) {
			mapMapJsW(base, holder);
		} else {
			mapListJsW(base, holder);
		}
	}
	@SuppressWarnings("unchecked")
	void mapItOld(String base, Object holder) {
		if (holder instanceof Map) {
			mapOld(base, (Map<String, Object>) holder);
		} else {
			mapOld(base, (List<Object>) holder);
		}
	}

	void map(String base, JsonWrapper toAssociate) {
		if (toAssociate.isList()) {
			int[] index = { 0 }; // TODO not thread safe, but stream does not have an (index,item) signature
			toAssociate.stream().forEach( child -> {
				String newBase = base + "[" + index[0]++ + "]";
				map(newBase, child);
			});
		}
		if (toAssociate.isObject()) {
			toAssociate.stream().forEach(entry -> {
				String newBase = base + "." + entry.getKey();
				JsonWrapper metadata = entry.getMetadata();
				String xPath = getXpath(metadata, entry.getKey());
				if (xPath != null) {
					associations.add(new Association(xPath, newBase, entry.toObject().toString()));
				}
				if (entry.isKind(Kind.OBJECT)) {
					map(newBase, entry);
				}
			});
		}
	}
	@SuppressWarnings("unchecked")
	private void mapOld(String base, Map<String, Object> toAssociate) { // TODO remove when streams work
		for (Map.Entry<String, Object> entry : toAssociate.entrySet()) {
			if (entry.getKey().equals(JsonWrapper.METADATA_HOLDER)) {
				continue;
			}
			String newBase = base + "." + entry.getKey();

			if (entry.getValue() instanceof Map || entry.getValue() instanceof List) {
				mapItOld(newBase, entry.getValue());
			} else {
				Set<Map<String, String>> metadataSet = ((Set<Map<String, String>>)toAssociate.get(JsonWrapper.METADATA_HOLDER));
				String xPath = getXpathOld(metadataSet, entry.getKey());
				if (xPath != null) {
					associations.add(
							new Association(xPath, base + "." + entry.getKey(),
									entry.getValue().toString()));
				}
			}
		}
	}
	private void mapMapJsW(String base, JsonWrapper toAssociate) { // TODO remove when streams work
		List<JsonWrapper> associates = toAssociate.stream().collect(Collectors.toList());
		for (JsonWrapper entry : associates) {
			String key = entry.getKey();
			if (entry.getKey().equals(JsonWrapper.METADATA_HOLDER)) {
				continue;
			}
			String newBase = base + "." + key;

			if (entry.isKind(Kind.OBJECT)) {
				mapItJsW(newBase, entry);
			} else {
				JsonWrapper metadataSet = toAssociate.getMetadata();
				String xPath = getXpathJsW(metadataSet, key);
				if (xPath != null) {
					associations.add( new Association(xPath, base + "." + key, entry.toString()));
				}
			}
		}
	}

	private String getXpath(JsonWrapper metadata, String key) {
		if (metadata == null) {
			return null;
		}
		JsonWrapper xPath = metadata.stream()
			.reduce((JsonWrapper)null, 
			(current, meta) -> {
				String label = meta.getString(JsonWrapper.ENCODING_KEY);
				if (label.equals(key)) {
					String relative = PathCorrelator.getXpath(meta.getString("template"), label, meta.getString("nsuri"));
					String axPath = (relative == null) ? meta.getString("path") : meta.getString("path") + "/" + relative;
					return new JsonWrapper().put("xPath", axPath);
				}
				if (current == null || meta.getString("path").length() < current.getString("path").length()) {
					current = meta;
					String axPath = meta.getString("path");
					return current.put("xPath", axPath); // TODO ADSF TODO this causes an NPE 
				}
				return current;
			});
		return xPath == null ?null :xPath.getString("xPath").toString();
	}
	private String getXpathOld(Set<Map<String, String>> metadataSet, String key) { // TODO remove when working with streams
		String xPath = null;
		Map<String, String> current = null;
		if (metadataSet == null) {
			return xPath;
		}
		for (Map<String, String> metaMap : metadataSet) {
			String label = metaMap.get(JsonWrapper.ENCODING_KEY);
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
	private String getXpathJsW(JsonWrapper metadata, String key) { // TODO remove when working with streams
		String xPath = null;
		JsonWrapper current = null;
		if (metadata == null) {
			return xPath;
		}
		List<JsonWrapper> metadataSet = metadata.stream().collect(Collectors.toList());
		for (JsonWrapper metaMap : metadataSet) {
			String label = metaMap.getString(JsonWrapper.ENCODING_KEY);
			if (label.equals(key)) {
				String relative = PathCorrelator.getXpath(metaMap.getString("template"), label, metaMap.getString("nsuri"));
				xPath = (relative == null) ? metaMap.getString("path") : metaMap.getString("path") + "/" + relative;
				break;
			}
			if (current == null || metaMap.getString("path").length() < current.getString("path").length()) {
				current = metaMap;
				xPath = metaMap.getString("path");
			}
		}
		return xPath;
	}

	private void mapOld(String base, List<Object> toAssociate) { // TODO remove when streams works
		int index = 0;
		for (Object obj : toAssociate) {
			String newBase = base + "[" + index++ + "]";
			mapItOld(newBase, obj);
		}
	}
	private void mapListJsW(String base, JsonWrapper toAssociate) { // TODO remove when streams works
		List<JsonWrapper> associates = toAssociate.stream().collect(Collectors.toList());
		int index = 0;
		for (JsonWrapper jsw : associates) {
			String newBase = base + "[" + index++ + "]";
			mapItJsW(newBase, jsw);
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

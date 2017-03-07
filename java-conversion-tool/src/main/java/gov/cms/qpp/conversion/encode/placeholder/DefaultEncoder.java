package gov.cms.qpp.conversion.encode.placeholder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

/**
 * Encoder used as a placholder when a fully implemented encoder has yet to be developed.
 * @author David Uselmann
 *
 */
public class DefaultEncoder extends JsonOutputEncoder {
	final String description;
	
    final Logger LOG = LoggerFactory.getLogger(getClass());

	
	public DefaultEncoder(String description) {
		this.description = description;
	}
	

	@Override
	protected void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {
		LOG.debug("Default JSON encoder {} is handling templateId {} and is described as '{}' ",
				getClass(), node.getId(), description);


		// TODO like the decoder this might be better in the parent
		// and we are given the child
		JsonWrapper childWrapper = new JsonWrapper();
		
		for (String name : node.getKeys()) {
			String nameForEncode = name.replace("Decoder", "Encoder");
			childWrapper.putString(nameForEncode, node.getValue(name));
		}
		
		wrapper.putObject(node.getId(), childWrapper.getObject());
		
		for (Node child : node.getChildNodes()) {
			childWrapper.putObject(child.getId(), childWrapper);
			encode(childWrapper, child);;
		}
	}

	
	// this one looks like a node that is not necessary
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.30")
	public static class N_Encoder extends DefaultEncoder {
		public N_Encoder() {
			super("Performance Rate");
		}
	}
	// this seems to be handled by 2.16.840.1.113883.10.20.27.3.3
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.24")
	public static class R_Encoder extends DefaultEncoder {
		public R_Encoder() {
			super("Aggregate Count - CMS");
		}
	}
//	 this one looks like a node that is not necessary
	@Encoder(templateId="2.16.840.1.113883.10.20.24.2.2")
	public static class B_Encoder extends DefaultEncoder {
		public B_Encoder() {
			super("Measure Section");
		}
	}
	// this one looks like a node that is not necessary
	@Encoder(templateId="2.16.840.1.113883.10.20.27.2.3")
	public static class D_Encoder extends DefaultEncoder {
		public D_Encoder() {
			super("QRDA Category III Measure Section - CMS (V2)");
		}
	}
	// this one looks like a root node that is not necessary
//	@Encoder(templateId="2.16.840.1.113883.10.20.24.3.98")
//	public static class G_Encoder extends DefaultEncoder {
//		public G_Encoder() {
//			super("Measure Reference");
//		}
//	}
	
	
	
	
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.29")
	public static class I_Encoder extends DefaultEncoder {
		public I_Encoder() {
			super("Advancing Care Information Measure Performed Measure Reference and Results");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.17")
	public static class K_Encoder extends DefaultEncoder {
		public K_Encoder() {
			super("Measure Reference and Results - CMS (V2)");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.23")
	public static class L_Encoder extends DefaultEncoder {
		public L_Encoder() {
			super("Reporting Parameters Act - CMS (V2)*");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.26")
	public static class S_Encoder extends DefaultEncoder {
		public S_Encoder() {
			super("Continuous Variable Measure Value - CMS");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.22")
	public static class T_Encoder extends DefaultEncoder {
		public T_Encoder() {
			super("Ethnicity Supplemental Data Element – CMS (V2)");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.16")
	public static class U_Encoder extends DefaultEncoder {
		public U_Encoder() {
			super("Measure Data - CMS (V2)");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.20")
	public static class V_Encoder extends DefaultEncoder {
		public V_Encoder() {
			super("Reporting Stratum - CMS");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.21")
	public static class W_Encoder extends DefaultEncoder {
		public W_Encoder() {
			super("Sex Supplemental Data Element - CMS (V2)");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.19")
	public static class X_Encoder extends DefaultEncoder {
		public X_Encoder() {
			super("Race Supplemental Data Element - CMS (V2)");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.18")
	public static class Y_Encoder extends DefaultEncoder {
		public Y_Encoder() {
			super("Payer Supplemental Data Element - CMS (V2)");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.1")
	public static class Z_Encoder extends DefaultEncoder {
		public Z_Encoder() {
			super("Measure Reference and Results- CMS (V2)");
		}
	}
	@Encoder(templateId="2.16.840.1.113883.10.20.27.3.25")
	public static class ZZ_Encoder extends DefaultEncoder {
		public ZZ_Encoder() {
			super("Performance Rate for Proportion Measure - CMS (V2)");
		}
	}
	

}

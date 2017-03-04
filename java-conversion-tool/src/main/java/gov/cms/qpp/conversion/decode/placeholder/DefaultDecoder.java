package gov.cms.qpp.conversion.decode.placeholder;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

public class DefaultDecoder extends QppXmlDecoder {
	final String description;
	
    final Logger LOG = LoggerFactory.getLogger(getClass());

	
	public DefaultDecoder(String description) {
		this.description = description;
	}

	static final String[] childrenToScan 
		= new String[] {"entry","organizer","component","observation","entryRelationship"};
	
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		LOG.debug("Default decoder {} is handling templateId {} and is described as '{}' ",
					getClass(), thisnode.getId(), description);
		thisnode.putValue("DefaultDecoderFor", description);
		return DecodeResult.TreeContinue;
	}

// The names of the default decoder classes does not matter.
// TODO must comment out these defaults as real implementations are written.
	
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.1.2")
//	public static class A_Decoder extends DefaultDecoder {
//		public A_Decoder() {
//			super("Document-Level Template: QRDA Category III Report - CMS (V2)");
//		}
//	}
	// this one looks like a node that is not necessary
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.24.2.2")
//	public static class B_Decoder extends DefaultDecoder {
//		public B_Decoder() {
//			super("Measure Section");
//		}
//	}
	// this one looks like a node that is not necessary
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.6")
//	public static class C_Decoder extends DefaultDecoder {
//		public C_Decoder() {
//			super("QRDA Category III Reporting Parameters Section - CMS (V2)*");
//		}
//	}
	// this one looks like a node that is not necessary
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.3")
//	public static class D_Decoder extends DefaultDecoder {
//		public D_Decoder() {
//			super("QRDA Category III Measure Section - CMS (V2)");
//		}
//	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.4")
//	public static class E_Decoder extends DefaultDecoder {
//		public E_Decoder() {
//			super("Improvement Activity Section");
//		}
//	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.5")
//	public static class F_Decoder extends DefaultDecoder {
//		public F_Decoder() {
//			super("Advancing Care Information Section");
//		}
//	}
	// this one looks like a root node that is not necessary
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.24.3.98")
//	public static class G_Decoder extends DefaultDecoder {
//		public G_Decoder() {
//			super("Measure Reference");
//		}
//	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.28")
//	public static class H_Decoder extends DefaultDecoder {
//		public H_Decoder() {
//			super("Advancing Care Information Numerator Denominator Type Measure Reference and Results");
//		}
//	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.29")
	public static class I_Decoder extends DefaultDecoder {
		public I_Decoder() {
			super("Advancing Care Information Measure Performed Measure Reference and Results");
		}
	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.33")
//	public static class J_Decoder extends DefaultDecoder {
//		public J_Decoder() {
//			super("Improvement Activity Performed Measure Reference and Results");
//		}
//	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.17")
	public static class K_Decoder extends DefaultDecoder {
		public K_Decoder() {
			super("Measure Reference and Results - CMS (V2)");
		}
	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.23")
//	public static class L_Decoder extends DefaultDecoder {
//		public L_Decoder() {
//			super("Reporting Parameters Act - CMS (V2)*");
//		}
//	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.27")
//	public static class M_Decoder extends DefaultDecoder {
//		public M_Decoder() {
//			super("Measure Performed");
//		}
//	}
	// this one looks like a node that is not necessary
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.30")
//	public static class N_Decoder extends DefaultDecoder {
//		public N_Decoder() {
//			super("Performance Rate");
//		}
//	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.31")
//	public static class O_Decoder extends DefaultDecoder {
//		public O_Decoder() {
//			super("Advancing Care Information Numerator Denominator Type Measure Numerator Data");
//		}
//	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.32")
//	public static class P_Decoder extends DefaultDecoder {
//		public P_Decoder() {
//			super("Advancing Care Information Numerator Denominator Type Measure Denominator Data");
//		}
//	}
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.3")
//	public static class Q_Decoder extends DefaultDecoder {
//		public Q_Decoder() {
//			super("Aggregate Count");
//		}
//	}
//	// this seems to be handled by 2.16.840.1.113883.10.20.27.3.3
//	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.24")
//	public static class R_Decoder extends DefaultDecoder {
//		public R_Decoder() {
//			super("Aggregate Count - CMS");
//		}
//	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.26")
	public static class S_Decoder extends DefaultDecoder {
		public S_Decoder() {
			super("Continuous Variable Measure Value - CMS");
		}
	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.22")
	public static class T_Decoder extends DefaultDecoder {
		public T_Decoder() {
			super("Ethnicity Supplemental Data Element – CMS (V2)");
		}
	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.16")
	public static class U_Decoder extends DefaultDecoder {
		public U_Decoder() {
			super("Measure Data - CMS (V2)");
		}
	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.20")
	public static class V_Decoder extends DefaultDecoder {
		public V_Decoder() {
			super("Reporting Stratum - CMS");
		}
	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.21")
	public static class W_Decoder extends DefaultDecoder {
		public W_Decoder() {
			super("Sex Supplemental Data Element - CMS (V2)");
		}
	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.19")
	public static class X_Decoder extends DefaultDecoder {
		public X_Decoder() {
			super("Race Supplemental Data Element - CMS (V2)");
		}
	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.18")
	public static class Y_Decoder extends DefaultDecoder {
		public Y_Decoder() {
			super("Payer Supplemental Data Element - CMS (V2)");
		}
	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.1")
	public static class Z_Decoder extends DefaultDecoder {
		public Z_Decoder() {
			super("Measure Reference and Results- CMS (V2)");
		}
	}
	@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.25")
	public static class ZZ_Decoder extends DefaultDecoder {
		public ZZ_Decoder() {
			super("Performance Rate for Proportion Measure - CMS (V2)");
		}
	}

}



/**
A
Document-Level Template: QRDA Category III Report - CMS (V2)
2.16.840.1.113883.10.20.27.1.2:2016-11-01
B
Measure Section 
2.16.840.1.113883.10.20.24.2.2
C
QRDA Category III Reporting Parameters Section - CMS (V2)*
2.16.840.1.113883.10.20.27.2.6:2016-11-01*
D
QRDA Category III Measure Section - CMS (V2)
2.16.840.1.113883.10.20.27.2.3:2016-11-01
E
Improvement Activity Section
2.16.840.1.113883.10.20.27.2.4:2016-09-01
F
Advancing Care Information Section
2.16.840.1.113883.10.20.27.2.5:2016-09-01
G
Measure Reference
2.16.840.1.113883.10.20.24.3.98
H
Advancing Care Information Numerator Denominator Type Measure Reference and Results
2.16.840.1.113883.10.20.27.3.28:2016-09-01
I
Advancing Care Information Measure Performed Measure Reference and Results
2.16.840.1.113883.10.20.27.3.29:2016-09-01
J
Improvement Activity Performed Measure Reference and Results
2.16.840.1.113883.10.20.27.3.33:2016-09-01
K
Measure Reference and Results - CMS (V2)
2.16.840.1.113883.10.20.27.3.17:2016-11-01
L
Reporting Parameters Act - CMS (V2)*
2.16.840.1.113883.10.20.27.3.23:2016-11-01*
M
Measure Performed
2.16.840.1.113883.10.20.27.3.27:2016-09-01
N
Performance Rate
2.16.840.1.113883.10.20.27.3.30:2016-09-01
O
Advancing Care Information Numerator Denominator Type Measure Numerator Data
2.16.840.1.113883.10.20.27.3.31:2016-09-01
P
Advancing Care Information Numerator Denominator Type Measure Denominator Data
2.16.840.1.113883.10.20.27.3.32:2016-09-01
Q
Aggregate Count
2.16.840.1.113883.10.20.27.3.3
R
Aggregate Count - CMS
2.16.840.1.113883.10.20.27.3.24
S
Continuous Variable Measure Value - CMS
2.16.840.1.113883.10.20.27.3.26
T
Ethnicity Supplemental Data Element – CMS (V2)
2.16.840.1.113883.10.20.27.3.22:2016-11-01
U
Measure Data - CMS (V2)
2.16.840.1.113883.10.20.27.3.16:2016-11-01
V
Reporting Stratum - CMS
2.16.840.1.113883.10.20.27.3.20
W
Sex Supplemental Data Element - CMS (V2)
2.16.840.1.113883.10.20.27.3.21:2016-11-01
X
Race Supplemental Data Element - CMS (V2)
2.16.840.1.113883.10.20.27.3.19:2016-11-01
Y
Payer Supplemental Data Element - CMS (V2)
2.16.840.1.113883.10.20.27.3.18:2016-11-01
Z
Measure Reference and Results- CMS (V2)
2.16.840.1.113883.10.20.27.3.1:2016-09-01
ZZ
Performance Rate for Proportion Measure - CMS (V2)
2.16.840.1.113883.10.20.27.3.25:2016-11-01
*/
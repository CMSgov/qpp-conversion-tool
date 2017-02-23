package gov.cms.qpp.conversion.model;

import java.io.Serializable;

/**
 * Represents a way of identifying a Node.
 * In this case, a Node is identified using both an element name and
 * a template id.
 * 
 */
public class NodeId implements Serializable {

	private static final long serialVersionUID = -39984254082771401L;
	
	private String elementName;
	private String templateId;
	
	public NodeId(String eleName, String templId) {
		this.setElementName(eleName);
		this.setTemplateId(templId);
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof NodeId)) {
			return false;
		}
		
		NodeId other = (NodeId) obj;
		
		return this.elementName.equals(other.getElementName()) && this.templateId.equals(other.getTemplateId());
	}
	
	@Override
	public int hashCode() {
		
		return elementName.hashCode() + templateId.hashCode();
	}
	
	@Override
	public String toString() {
		
		return "NodeId: elementName: " + elementName + ", templateId: " + templateId;
	}
	
}

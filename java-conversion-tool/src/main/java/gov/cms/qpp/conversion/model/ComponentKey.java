package gov.cms.qpp.conversion.model;

import java.util.Objects;

public class ComponentKey {
	private final TemplateId template;
	private final Program program;
	private volatile int hashCode;

	public ComponentKey(TemplateId templateId, Program programName) {
		template = templateId;
		program = programName;
	}

	public TemplateId getTemplate() {
		return template;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o.getClass().equals(ComponentKey.class))) {
			return false;
		}

		ComponentKey that = (ComponentKey) o;
		return Objects.equals(template, that.template)
				&& Objects.equals(program, that.program);
	}

	@Override
	public int hashCode() {
		int hash = hashCode;
		if (hash == 0) {
			hashCode = Objects.hash(template, program);
		}
		return hashCode;
	}
}

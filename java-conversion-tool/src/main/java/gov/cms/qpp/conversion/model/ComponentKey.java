package gov.cms.qpp.conversion.model;

import java.util.Objects;

public class ComponentKey {
	private TemplateId template;
	private Program program;

	public ComponentKey(TemplateId templateId, Program programName) {
		template = templateId;
		program = programName;
	}

	public TemplateId getTemplate() {
		return template;
	}

	public Program getProgram() {
		return program;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ComponentKey)) return false;

		ComponentKey that = (ComponentKey) o;
		return Objects.equals(template, that.template)
				&& Objects.equals(program, that.program);
	}

	@Override
	public int hashCode() {
		return Objects.hash(template, program);
	}
}

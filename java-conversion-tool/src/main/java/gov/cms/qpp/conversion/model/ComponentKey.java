package gov.cms.qpp.conversion.model;

import java.util.Objects;

/**
 * A key used for identifying "components" i.e. {@link Encoder}'s, {@link Decoder}'s and {@link Validator}'s.
 */
public class ComponentKey {
	private final TemplateId template;
	private final Program program;
	private volatile int hashCode;

	/**
	 * Construct ComponentKey using constituent values.
	 *
	 * @param templateId a template id
	 * @param programName a program MIPS or CPC
	 */
	public ComponentKey(TemplateId templateId, Program programName) {
		template = templateId;
		program = programName;
	}

	/**
	 * Get the template id value of the key.
	 *
	 * @return the template id
	 */
	public TemplateId getTemplate() {
		return template;
	}

	/**
	 * equality check
	 *
	 * @param o object for comparison
	 * @return result
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || !(o.getClass().equals(ComponentKey.class))) {
			return false;
		}

		ComponentKey that = (ComponentKey) o;
		return Objects.equals(template, that.template)
				&& Objects.equals(program, that.program);
	}

	/**
	 * get object's hash code
	 *
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = Objects.hash(template, program);
		}
		return hashCode;
	}
}

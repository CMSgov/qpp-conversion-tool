package gov.cms.qpp.conversion;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.segmentation.QrdaScope;

/**
 * Stateful converter context. The values in this data structure will change
 * throughout the conversion.
 */
public class Context {
	public static String REPORTING_YEAR = "2017";
	private final Map<Class<? extends Annotation>, Registry<?>> registries = new IdentityHashMap<>();
	private Program program = Program.ALL;
	private Set<QrdaScope> scope = EnumSet.noneOf(QrdaScope.class);
	private boolean historical;
	private boolean doDefaults = true;
	private boolean doValidation = true;

	/**
	 * Gets the current contextual {@link Program}
	 *
	 * @return The current {@link Program}, which may have been changed automatically during conversion
	 */
	public Program getProgram() {
		return program;
	}

	/**
	 * Sets the current contextual {@link Program}
	 *
	 * @param program The new {@link Program}
	 */
	public void setProgram(Program program) {
		this.program = program;
	}

	/**
	 * Get the scope that determines which data may be transformed.
	 *
	 * @return scope The scope.
	 */
	public Set<QrdaScope> getScope() {
		return scope;
	}

	/**
	 * Sets the scope of the converter.
	 *
	 * @param scope The new scope.
	 */
	public void setScope(Set<QrdaScope> scope) {
		this.scope = scope;
	}

	/**
	 * Checks whether or not the context has a non-empty scope
	 *
	 * @return true if the scope is not null and contains at least one element
	 */
	public boolean hasScope() {
		return scope != null && !scope.isEmpty();
	}

	/**
	 * Is this a conversion of historical submissions.
	 *
	 * @return determination of whether or not the conversion is enacted on historical submissions.
	 */
	public boolean isHistorical() {
		return historical;
	}

	/**
	 * Sets whether conversions are historical or not.
	 *
	 * @param historical Flag indicating whether conversions are historical or not.
	 */
	public void setHistorical(boolean historical) {
		this.historical = historical;
	}

	/**
	 * Whether this context wants default conversion performed
	 *
	 * @return doDefaults
	 */
	public boolean isDoDefaults() {
		return doDefaults;
	}

	/**
	 * Switch for enabling or disabling inclusion of default nodes.
	 *
	 * @param doDefaults whether the converter should perform defaults
	 */
	public void setDoDefaults(boolean doDefaults) {
		this.doDefaults = doDefaults;
	}

	/**
	 * Whether this context wants validation performed
	 *
	 * @return doValidation
	 */
	public boolean isDoValidation() {
		return doValidation;
	}

	/**
	 * Switch for enabling or disabling validation.
	 *
	 * @param doValidation toggle value
	 */
	public void setDoValidation(boolean doValidation) {
		this.doValidation = doValidation;
	}

	/**
	 * Looks up or creates a new {@link Registry} for the given annotation type under this context
	 *
	 * @param annotation The annotation type to use for class path searching in the {@link Registry}
	 * @return The existing or new {@link Registry}
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation, R> Registry<R> getRegistry(Class<A> annotation) {
		return (Registry<R>) registries.computeIfAbsent(annotation, key -> new Registry<>(this, key));
	}

}
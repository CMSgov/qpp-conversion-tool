package gov.cms.qpp.conversion;

import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;
import java.util.Map;

import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.validate.pii.MissingPiiValidator;
import gov.cms.qpp.conversion.validate.pii.PiiValidator;

/**
 * Stateful converter context. The values in this data structure will change
 * throughout the conversion.
 */
public class Context {
	public static final String REPORTING_YEAR = "2021";
	private final Map<Class<? extends Annotation>, Registry<?>> registries = new IdentityHashMap<>();
	private Program program = Program.ALL;
	private boolean historical;
	private boolean doValidation = true;
	private PiiValidator piiValidator = MissingPiiValidator.INSTANCE;
	private ApmEntityIds apmEntityIds;

	/**
	 * Initialize a context with the default APM entity id file
	 */
	public Context() {
		apmEntityIds = new ApmEntityIds(ApmEntityIds.DEFAULT_CPC_PLUS_APM_ENTITY_FILE_NAME, ApmEntityIds.DEFAULT_PCF_APM_ENTITY_FILE_NAME);
	}

	/**
	 * Initialize a context with a pre-set APM entity id file
	 *
	 * @param apmEntityIds
	 */
	public Context(ApmEntityIds apmEntityIds) {
		this.apmEntityIds = apmEntityIds;
	}

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

	public PiiValidator getPiiValidator() {
		return piiValidator;
	}

	public void setPiiValidator(PiiValidator piiValidator) {
		this.piiValidator = piiValidator;
	}

	public ApmEntityIds getApmEntityIds() {
		return apmEntityIds;
	}

	public void setApmEntityIds(final ApmEntityIds apmEntityIds) {
		this.apmEntityIds = apmEntityIds;
	}

	/**
	 * Looks up or creates a new {@link Registry} for the given annotation type under this context
	 *
	 * @param <A> Marker annotation that helps identify registry types
	 * @param <R> Types that comprise the registry
	 * @param annotation The annotation type to use for class path searching in the {@link Registry}
	 * @return The existing or new {@link Registry}
	 */
	@SuppressWarnings("unchecked")
	public <A extends Annotation, R> Registry<R> getRegistry(Class<A> annotation) {
		return (Registry<R>) registries.computeIfAbsent(annotation, key -> new Registry<>(this, key));
	}

}
package gov.cms.qpp.conversion.model;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.cms.qpp.conversion.Context;

/**
 * This class manages the available transformation handlers.
 * R is the stored and return interface type.
 * V is the key type to access the registered values.
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
public class Registry<R> {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(Registry.class);
	private static final Map<Class<?>, Function<Context, Object>> CONSTRUCTORS = new IdentityHashMap<>();
	private static final Map<Class<? extends Annotation>, Map<ComponentKey, Class<?>>> SHARED_REGISTRY_MAP
			= new ConcurrentHashMap<>();

	private final Context context;
	private final Map<ComponentKey, Class<?>> registryMap;
	private final Class<? extends Annotation> annotationClass;

	/**
	 * Registry constructor
	 *
	 * @param context         The context to use for this registry. Must not be null.
	 * @param annotationClass The annotation to use for class path searching in this registry. Must not be null.
	 */
	public Registry(Context context, Class<? extends Annotation> annotationClass) {
		Objects.requireNonNull(context, "context");
		Objects.requireNonNull(annotationClass, "annotationClass");

		this.context = context;
		this.annotationClass = annotationClass;
		this.registryMap = new HashMap<>(
				SHARED_REGISTRY_MAP.computeIfAbsent(annotationClass, this::lookupAnnotatedClasses)
		);
	}

	/**
	 * Searches the class path for types with the given annotation
	 *
	 * @param annotationClass The annotation for which to search
	 * @return A map of classes with the given annotation
	 */
	private Map<ComponentKey, Class<?>> lookupAnnotatedClasses(Class<? extends Annotation> annotationClass) {
		Reflections reflections = new Reflections("gov.cms");
		Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotationClass);
		Map<ComponentKey, Class<?>> registry = new HashMap<>(annotatedClasses.size());

		for (Class<?> annotatedClass : annotatedClasses) {
			for (ComponentKey key : getComponentKeys(annotatedClass)) {
				registry.put(key, annotatedClass);
			}
		}

		return registry;
	}

	Set<ComponentKey> getComponentKeys(Class<?> annotatedClass) {
		Annotation annotation = annotatedClass.getAnnotation(annotationClass);
		Set<ComponentKey> values = new HashSet<>();

		if (annotation instanceof Decoder) {
			Decoder decoder = (Decoder) annotation;
			values.add(new ComponentKey(decoder.value(), decoder.program()));
		}
		if (annotation instanceof Encoder) {
			Encoder encoder = (Encoder) annotation;
			values.add(new ComponentKey(encoder.value(), encoder.program()));
		}
		if (annotation instanceof Validator) {
			Validator validator = (Validator) annotation;
			values.add(new ComponentKey(validator.value(), validator.program()));
		}
		return values;
	}

	/**
	 * This method returns a top‐level handler for the given XPATH
	 *
	 * @param registryKey template id key
	 * @return value corresponding to registry key
	 */
	public R get(TemplateId registryKey) {
		return instantiateHandler(findHandler(registryKey));
	}

	/**
	 * Instantiate a given handler class.
	 *
	 * @param handlerClass the class to instantiate
	 * @return an instance of the given class
	 */
	private R instantiateHandler(Class<? extends R> handlerClass) {
		if (handlerClass == null) {
			return null;
		}

		return handlerClass.cast(
				CONSTRUCTORS.computeIfAbsent(handlerClass, this::createHandler).apply(context)
		);
	}

	/**
	 * Creates a function that will return new instances of the handlerClass
	 *
	 * @param handlerClass The class of which to create new instances
	 * @return A function that returns instances of the handlerClass when supplied with a context
	 */
	private Function<Context, Object> createHandler(Class<?> handlerClass) {
		try {
			return createHandlerConstructor(handlerClass);
		} catch (ConstructorNotFoundException e) {
			DEV_LOG.warn("Unable to create constructor handle for class " + handlerClass.getName(), e);
			return ignore -> null;
		}
	}

	private static <T> Function<Context, Object> createHandlerConstructor(Class<T> handlerClass) {
		Constructor<T> constructor;
		try {
			try {
				constructor = handlerClass.getConstructor(Context.class);
				MethodHandle handle = MethodHandles.lookup()
						.unreflectConstructor(constructor)
						.asType(MethodType.methodType(Object.class, Context.class));

				return constructorContextArgument(handle);
			} catch (NoSuchMethodException thatsOk) {
				constructor = getNoArgsConstructor(handlerClass);
				MethodHandle handle = MethodHandles.lookup()
						.unreflectConstructor(constructor)
						.asType(MethodType.methodType(Object.class));

				return constructorNoArgs(handle);
			}
		} catch (IllegalAccessException e) {
			throw new ConstructorNotFoundException(
					"Constructor must be accessible via reflection for " + handlerClass.getName(), e
			);
		}
	}

	@SuppressWarnings("unchecked") // suppress cast from wildcard <?> to type <T>
	private static <T> Constructor<T> getNoArgsConstructor(Class<T> type) {
		Constructor<T> constructor = getNoArgsConstructor((Constructor<T>[]) type.getConstructors());
		if (constructor == null) {
			constructor = getNoArgsConstructor((Constructor<T>[]) type.getDeclaredConstructors());

			if (constructor == null) {
				throw new ConstructorNotFoundException(
						type + " does not have a no-args constructor (public OR private)"
				);
			}
		}
		constructor.setAccessible(true);
		return constructor;
	}

	private static <T> Constructor<T> getNoArgsConstructor(Constructor<T>[] constructors) {
		for (Constructor<T> constructor : constructors) {
			if (constructor.getParameterCount() == 0) {
				return constructor;
			}
		}
		return null;
	}

	private static Function<Context, Object> constructorContextArgument(MethodHandle handle) {
		return passedContext -> {
			try {
				return handle.invokeExact(passedContext);
			} catch (Exception codeProblem) { // NOSONAR the method throws throwable
				DEV_LOG.warn("Unable to invoke constructor handle", codeProblem);
				return null;
			} catch (Throwable severeRuntimeError) { // NOSONAR the method throws throwable
				throw new SevereRuntimeException(severeRuntimeError);
			}
		};
	}

	private static Function<Context, Object> constructorNoArgs(MethodHandle handle) {
		return ignore -> {
			try {
				return handle.invokeExact();
			} catch (Exception codeProblem) { // NOSONAR the method throws throwable
				DEV_LOG.warn("Unable to invoke no-args constructor handle", codeProblem);
				return null;
			} catch (Throwable severeRuntimeError) { // NOSONAR the method throws throwable
				throw new SevereRuntimeException(severeRuntimeError);
			}
		};
	}

	/**
	 * Retrieve handlers that apply generally and specifically to the given template.
	 *
	 * @param registryKey the template for which handlers will be searched
	 * @return all applicable handlers
	 */
	public Set<R> inclusiveGet(TemplateId registryKey) {
		return findHandlers(getKeys(registryKey, true)).stream()
				.map(this::instantiateHandler)
				.collect(Collectors.toCollection(LinkedHashSet<R>::new));
	}

	/**
	 * Get a template specific list that specifies the order in which handler
	 * classes will be searched.
	 *
	 * @param registryKey     a template id
	 * @param generalPriority specify the order of specificity (general first or program first)
	 * @return list of component keys
	 */
	private List<ComponentKey> getKeys(TemplateId registryKey, boolean generalPriority) {
		Program contextProgram = context.getProgram();
		if (contextProgram == Program.ALL) {
			return Collections.singletonList(new ComponentKey(registryKey, contextProgram));
		}

		List<ComponentKey> returnValue = Arrays.asList(
				new ComponentKey(registryKey, contextProgram),
				new ComponentKey(registryKey, Program.ALL)
		);
		if (generalPriority) {
			Collections.reverse(returnValue);
		}
		return returnValue;
	}

	/**
	 * Retrieve a handler for the given template id
	 *
	 * @param registryKey template id
	 * @return handler class for {@link Validator}, {@link Decoder}, or {@link Encoder}
	 */
	private Class<? extends R> findHandler(TemplateId registryKey) {
		return findHandlers(getKeys(registryKey, false))
				.stream()
				.findFirst()
				.orElse(null);
	}

	/**
	 * Find and return handler classes that correspond to the given component keys.
	 *
	 * @param keys a list of potential {@link Registry#registryMap} keys
	 * @return ordered set of handler classes
	 */
	private Set<Class<? extends R>> findHandlers(List<ComponentKey> keys) {
		Set<Class<? extends R>> handlers = new LinkedHashSet<>();
		keys.forEach(key -> {
			@SuppressWarnings("unchecked")
			Class<? extends R> handler = (Class<? extends R>) registryMap.get(key);
			if (handler != null) {
				handlers.add(handler);
			}
		});
		return handlers;
	}

	/**
	 * Means to register a new transformation handler
	 *
	 * @param registryKey key that identifies a component i.e. {@link Validator}, {@link Decoder}, or {@link Encoder}
	 * @param handler     the keyed {@link Validator}, {@link Decoder}, or {@link Encoder}
	 */
	public void register(ComponentKey registryKey, Class<? extends R> handler) {
		DEV_LOG.debug(
				"Registering " + handler.getName() + " to '" + registryKey + "' for " +
						annotationClass.getSimpleName() + "."
		);
		if (registryMap.containsKey(registryKey)) {
			DEV_LOG.error(
					"Duplicate registered handler for " +
							registryKey + " both " +
							registryMap.get(registryKey).getName() + " and " + handler.getName()
			);
		}

		registryMap.put(registryKey, handler);
	}

	public int size() {
		return registryMap.size();
	}
}
